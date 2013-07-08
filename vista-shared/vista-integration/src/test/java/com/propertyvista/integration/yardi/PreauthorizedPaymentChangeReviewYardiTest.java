/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 * the License.
 *
 * Created on 2013-06-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.math.BigDecimal;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.payment.AutoPayReviewDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;
import com.propertyvista.test.integration.PaymentAgreementTester;
import com.propertyvista.test.integration.PreauthorizedPaymentBuilder;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.yardi.mock.LeaseChargeUpdateEvent;
import com.propertyvista.yardi.mock.LeaseChargeUpdater;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;

/**
 * 
 * @see com.propertyvista.biz.financial.payment.PreauthorizedPaymentChangeReviewInternalTest
 * 
 */
public class PreauthorizedPaymentChangeReviewYardiTest extends PaymentYardiTestBase {

    private Lease lease;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createYardiLease("prop123", "t000111");
        setSysDate("2011-01-01");
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());
        lease = loadLeaseToModel("t000111");
        Tenant tenant = lease.leaseParticipants().iterator().next().cast();
        getDataModel(CustomerDataModel.class).addPaymentMethod(tenant.customer(), lease.unit().building(), PaymentType.Echeck);
    }

    public void testLeaseServiceChanges() throws Exception {
        getDataModel(LeaseDataModel.class).createPreauthorizedPayment(lease, new PreauthorizedPaymentBuilder(). //
                add(lease.currentTerm().version().leaseProducts().serviceItem(), "500.00"). //
                add(lease.currentTerm().version().leaseProducts().featureItems().get(0), "80.00"). //
                build());
        Persistence.service().commit();

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop123", "t000111", "rent").
            set(LeaseChargeUpdater.Name.Description, "Rent").
            set(LeaseChargeUpdater.Name.ServiceFromDate, DateUtils.detectDateformat("2010-01-01")).
            set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("2014-12-31")).
            set(LeaseChargeUpdater.Name.ChargeCode, "rrent").
            set(LeaseChargeUpdater.Name.Amount, "1200.00");        
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        // PAP is suspended
        new PaymentAgreementTester(lease.billingAccount()).count(1)//
                .activeCount(0);

        AutoPayReviewDTO reviewDTO = ServerSideFactory.create(PaymentMethodFacade.class).getSuspendedPreauthorizedPaymentReview(lease.billingAccount());

        // Verify created Data
        {
            assertEquals("PAP to review", 1, reviewDTO.pap().size());

            AutoPayReviewPreauthorizedPaymentDTO papReview = reviewDTO.pap().get(0);
            assertEquals("PAP Charges to review", 2, papReview.items().size());

            assertEquals("New Rent Price", new BigDecimal("1200.00"), papReview.items().get(0).suggested().totalPrice().getValue());
            assertEquals("Suggested Rent Payment", new BigDecimal("600.00"), papReview.items().get(0).suggested().payment().getValue());

            assertEquals("New Parking Price", new BigDecimal("80.00"), papReview.items().get(1).suggested().totalPrice().getValue());
            assertEquals("Suggested Parking Payment", new BigDecimal("80.00"), papReview.items().get(1).suggested().payment().getValue());
        }
    }
}

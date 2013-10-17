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

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.payment.AutoPayReviewLeaseDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;
import com.propertyvista.test.integration.PaymentAgreementTester;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.integration.PreauthorizedPaymentBuilder;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.yardi.mock.LeaseChargeUpdateEvent;
import com.propertyvista.yardi.mock.LeaseChargeUpdater;

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
        createYardiBuilding("prop1");
        createYardiLease("prop1", "t000111");
        setSysDate("2011-01-01");
        yardiImportAll(getYardiCredential("prop1"));
        loadBuildingToModel("prop1");
        lease = loadLeaseToModel("t000111");
        Tenant tenant = lease.leaseParticipants().iterator().next().cast();
        getDataModel(CustomerDataModel.class).addPaymentMethod(tenant.customer(), lease.unit().building(), PaymentType.Echeck);
        setPaymentBatchProcess();
    }

    public void testLeaseServiceChanges() throws Exception {
        getDataModel(LeaseDataModel.class).createPreauthorizedPayment(lease, new PreauthorizedPaymentBuilder(). //
                add(lease.currentTerm().version().leaseProducts().serviceItem(), "500.00"). //
                add(lease.currentTerm().version().leaseProducts().featureItems().get(0), "80.00"). //
                build());
        Persistence.service().commit();

        assertEquals("PAD next Generation date", "2011-02-01", ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(lease));

        new PaymentAgreementTester(lease.billingAccount())//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("500 + 80"));

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop1", "t000111", "rent").
            set(LeaseChargeUpdater.Name.Description, "Rent").
            set(LeaseChargeUpdater.Name.ServiceFromDate, DateUtils.detectDateformat("2010-01-01")).
            set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("2014-12-31")).
            set(LeaseChargeUpdater.Name.ChargeCode, "rrent").
            set(LeaseChargeUpdater.Name.Amount, "1200.00");        
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }
        yardiImportAll(getYardiCredential("prop1"));

        // PAP is updated
        new PaymentAgreementTester(lease.billingAccount())//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("600 + 80"));

        AutopayAgreement newPap1 = new PaymentAgreementTester(lease.billingAccount()).lastRecord();
        assertEquals("PAP Charges", 2, newPap1.coveredItems().size());

        AutoPayReviewLeaseDTO reviewDTO = ServerSideFactory.create(PaymentMethodFacade.class).getAutopayAgreementRequiresReview(lease.billingAccount());

        // Verify created Data
        {
            assertEquals("PAP to review", 1, reviewDTO.pap().size());

            AutoPayReviewPreauthorizedPaymentDTO papReview = reviewDTO.pap().get(0);
            assertEquals("PAP Charges to review", 2, papReview.items().size());

            assertEquals("New Rent Price", new BigDecimal("1200.00"), papReview.items().get(0).current().totalPrice().getValue());
            assertEquals("Suggested Rent Payment", new BigDecimal("600.00"), papReview.items().get(0).current().payment().getValue());

            assertEquals("New Parking Price", new BigDecimal("80.00"), papReview.items().get(1).current().totalPrice().getValue());
            assertEquals("Suggested Parking Payment", new BigDecimal("80.00"), papReview.items().get(1).current().payment().getValue());
        }

        // Post and process all payments
        advanceSysDate("2011-02-01");
        new PaymentRecordTester(lease.billingAccount())//
                .count(1) //
                .lastRecordStatus(PaymentStatus.Queued)//
                .lastRecordAmount("680.00");

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop1", "t000111", "lock").
            set(LeaseChargeUpdater.Name.Description, "Locker").
            set(LeaseChargeUpdater.Name.ServiceFromDate, DateUtils.detectDateformat("2010-01-01")).
            set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("2014-12-31")).
            set(LeaseChargeUpdater.Name.ChargeCode, "lock").
            set(LeaseChargeUpdater.Name.Amount, "20.00");        
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        yardiImportAll(getYardiCredential("prop1"));

        AutopayAgreement newPap2 = new PaymentAgreementTester(lease.billingAccount()).lastRecord();
        assertEquals("PAP Charges", 3, newPap2.coveredItems().size());
    }
}

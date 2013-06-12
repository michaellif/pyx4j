/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.payment.AutoPayReviewDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;
import com.propertyvista.test.integration.PaymentAgreementTester;
import com.propertyvista.test.integration.PreauthorizedPaymentBuilder;
import com.propertyvista.test.mock.MockConfig;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.AutoPayChangePolicyDataModel;

public class PreauthorizedPaymentChangeReviewInternalTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    @Override
    protected void preloadData() {
        MockConfig config = new MockConfig();
        config.billConfirmationMethod = LeaseBillingPolicy.BillConfirmationMethod.automatic;
        preloadData(config);
    }

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = super.getMockModelTypes();
        models.add(AutoPayChangePolicyDataModel.class);
        return models;
    }

    public void testLeaseServiceChanges() throws Exception {
        setSysDate("2011-01-01");
        createLease("2011-01-01", "2012-03-10", new BigDecimal("1000.00"), null);
        BillableItem parking = addOutdoorParking();
        approveApplication(true);

        PreauthorizedPayment pap1 = setPreauthorizedPayment(new PreauthorizedPaymentBuilder(). //
                add(getLease().currentTerm().version().leaseProducts().serviceItem(), "500.00"). //
                add(parking, "80.00"). //
                build());

        new PaymentAgreementTester(getLease().billingAccount()).count(1)//
                .activeCount(1)//
                .lastRecordAmount("580.00");

        //TODO use adjustments Yardi Like changes
        {
            Lease lease = ServerSideFactory.create(LeaseFacade.class).load(getLease(), true);
            lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(new BigDecimal("1200.00"));
            ServerSideFactory.create(LeaseFacade.class).finalize(lease);
            ServerSideFactory.create(PaymentMethodFacade.class).suspendPreauthorizedPayment(pap1);
            Persistence.service().commit();
        }

        new PaymentAgreementTester(getLease().billingAccount()).count(1)//
                .activeCount(0)//
                .lastRecordAmount("580.00");

        AutoPayReviewDTO reviewDTO = ServerSideFactory.create(PaymentMethodFacade.class).getSuspendedPreauthorizedPaymentReview(getLease().billingAccount());

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

    public void testLeaseFeatureChanges() throws Exception {
        setSysDate("2011-01-01");
        createLease("2011-01-01", "2012-03-10", new BigDecimal("1000.00"), null);
        BillableItem parking = addOutdoorParking();
        approveApplication(true);

        PreauthorizedPayment pap1 = setPreauthorizedPayment(new PreauthorizedPaymentBuilder(). //
                add(getLease().currentTerm().version().leaseProducts().serviceItem(), "1000.00"). //
                add(parking, "80.00"). //
                build());

        addLargeLocker();
        finalizeLeaseAdendum();

        {
            ServerSideFactory.create(PaymentMethodFacade.class).suspendPreauthorizedPayment(pap1);
            Persistence.service().commit();
        }

        AutoPayReviewDTO reviewDTO = ServerSideFactory.create(PaymentMethodFacade.class).getSuspendedPreauthorizedPaymentReview(getLease().billingAccount());

        // Verify created Data
        {
            assertEquals("PAP to review", 1, reviewDTO.pap().size());

            AutoPayReviewPreauthorizedPaymentDTO papReview = reviewDTO.pap().get(0);
            assertEquals("PAP Charges to review", 3, papReview.items().size());

            assertEquals("New Rent Price", new BigDecimal("1000.00"), papReview.items().get(0).suggested().totalPrice().getValue());
            assertEquals("Suggested Rent Payment", new BigDecimal("1000.00"), papReview.items().get(0).suggested().payment().getValue());

            assertEquals("New Parking Price", new BigDecimal("80.00"), papReview.items().get(1).suggested().totalPrice().getValue());
            assertEquals("Suggested Parking Payment", new BigDecimal("80.00"), papReview.items().get(1).suggested().payment().getValue());

            assertEquals("New Locker Price", new BigDecimal("60.00"), papReview.items().get(2).suggested().totalPrice().getValue());
            assertNull("Suggested Locker Payment", papReview.items().get(2).suggested().payment().getValue());
        }
    }

}

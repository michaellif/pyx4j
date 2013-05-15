/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.RegressionTests;
import com.propertyvista.biz.system.OperationsTriggerFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.payment.pad.EFTTransportFacade;
import com.propertyvista.payment.pad.mock.EFTTransportFacadeMock;
import com.propertyvista.payment.pad.mock.ScheduledResponseAcknowledgment;
import com.propertyvista.test.mock.MockConfig;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.test.mock.schedule.OperationsTriggerFacadeMock;

@Category(RegressionTests.class)
public class PadProcessingTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        ServerSideFactory.register(EFTTransportFacade.class, EFTTransportFacadeMock.class);
        ServerSideFactory.register(OperationsTriggerFacade.class, OperationsTriggerFacadeMock.class);
    }

    @Override
    protected void preloadData() {
        MockConfig config = new MockConfig();
        config.billConfirmationMethod = LeaseBillingPolicy.BillConfirmationMethod.automatic;
        preloadData(config);
    }

    public void testPadSuccessful() throws Exception {
        setSysDate("2011-04-01");
        setCaledonPAdPaymentBatchProcess();

        Customer customer = getDataModel(CustomerDataModel.class).addCustomer();
        getDataModel(CustomerDataModel.class).setCurrentItem(customer);

        Lease lease = getDataModel(LeaseDataModel.class).addLease("2011-04-01", "2012-03-10", new BigDecimal(100), null, customer);
        getDataModel(LeaseDataModel.class).setCurrentItem(lease);

        LeasePaymentMethod paymentMethod = getDataModel(CustomerDataModel.class).addPaymentMethod(PaymentType.Echeck);

        // Make a payment
        PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(paymentMethod, "100");

        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);
        Persistence.service().commit();

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        advanceSysDate("2011-04-02");

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Cleared);
    }

    public void testPadRejected() throws Exception {
        setSysDate("2011-04-01");
        setCaledonPAdPaymentBatchProcess();

        Customer customer = getDataModel(CustomerDataModel.class).addCustomer();
        getDataModel(CustomerDataModel.class).setCurrentItem(customer);

        Lease lease = getDataModel(LeaseDataModel.class).addLease("2011-04-01", "2012-03-10", new BigDecimal(100), null, customer);
        getDataModel(LeaseDataModel.class).setCurrentItem(lease);

        LeasePaymentMethod paymentMethod = getDataModel(CustomerDataModel.class).addPaymentMethod(PaymentType.Echeck);

        // Make a payment
        PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(paymentMethod, "100");

        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);
        Persistence.service().commit();

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        MockEventBus.fireEvent(new ScheduledResponseAcknowledgment(PadTransactionUtils.toCaldeonTransactionId(paymentRecord.id()), "2001"));

        advanceSysDate("2011-04-02");

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Rejected);

    }
}

/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.Assert;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.system.OperationsTriggerFacade;
import com.propertyvista.biz.system.eft.EFTTransportFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.eft.mock.efttransport.EFTTransportFacadeMock;
import com.propertyvista.eft.mock.efttransport.ScheduleTransportConnectionError;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.test.mock.schedule.OperationsTriggerFacadeMock;
import com.propertyvista.test.mock.schedule.SchedulerMock;

@Category(RegressionTests.class)
public class FundsTransferProcessErrorRecoveryTest extends LeaseFinancialTestBase {

    private CustomerDataModel customerDataModel;

    private Customer customer;

    private LeasePaymentMethod paymentMethod;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        registerFacadeMock(EFTTransportFacade.class, EFTTransportFacadeMock.class);
        registerFacadeMock(OperationsTriggerFacade.class, OperationsTriggerFacadeMock.class);
        EFTTransportFacadeMock.init();

        customerDataModel = getDataModel(CustomerDataModel.class);
        customer = customerDataModel.addCustomer();
        createLease("01-Feb-2011", "01-Sep-2012", new BigDecimal(100), null, customer);
        paymentMethod = customerDataModel.addPaymentMethod(customer, getBuilding(), PaymentType.Echeck);
    }

    public void testSendFileConnectionErrors() throws Exception {
        // Make a payment
        PaymentRecord paymentRecord1 = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethod, "100");
        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord1, null);
        Persistence.service().commit();

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        MockEventBus.fireEvent(new ScheduleTransportConnectionError(true));

        try {
            SchedulerMock.runProcess(PmcProcessType.paymentsPadSend, (Date) null);
            Assert.fail("Process should fail");
        } catch (RuntimeException ok) {
            Assert.assertTrue("Mock exception expected", ok.getMessage().contains("Mock"));
        }

        MockEventBus.fireEvent(new ScheduleTransportConnectionError(false));

        // Should be able to send the payment record again
        SchedulerMock.runProcess(PmcProcessType.paymentsPadSend, (Date) null);
        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveAcknowledgment, (Date) null);
        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveReconciliation, (Date) null);

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Cleared);

    }

    public void testSendSedonFile() throws Exception {
        // Make a payment
        PaymentRecord paymentRecord1 = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethod, "100");
        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord1, null);
        Persistence.service().commit();
        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        SchedulerMock.runProcess(PmcProcessType.paymentsPadSend, (Date) null);
        // Do not Acknowledgment the file.

        //Prepare second file
        PaymentRecord paymentRecord2 = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethod, "200");
        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord2, null);
        Persistence.service().commit();
        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        try {
            SchedulerMock.runProcess(PmcProcessType.paymentsPadSend, (Date) null);
            Assert.fail("Process should fail");
        } catch (Error ok) {
            Assert.assertTrue("Can't send expected", ok.getMessage().contains("Acknowledged or Canceled"));
        }

        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveAcknowledgment, (Date) null);

        // Should be able to send the payment record again
        SchedulerMock.runProcess(PmcProcessType.paymentsPadSend, (Date) null);
        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Received);

        // Receive first Reconciliation
        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveReconciliation, (Date) null);

        // The second record was still not processes
        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Received);

        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveAcknowledgment, (Date) null);
        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveReconciliation, (Date) null);

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Cleared);

    }
}

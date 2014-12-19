/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2014
 * @author vlads
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.payment.CreditCardFacade.ReferenceNumberPrefix;
import com.propertyvista.biz.system.OperationsAlertFacade;
import com.propertyvista.biz.system.eft.CreditCardPaymentProcessorFacade;
import com.propertyvista.biz.system.encryption.EncryptedStorageFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.eft.mock.cards.CreditCardPaymentProcessorFacadeMock;
import com.propertyvista.operations.domain.eft.cards.CardTransactionRecord;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.test.mock.schedule.SchedulerMock;
import com.propertyvista.test.mock.security.EncryptedStorageFacadeMock;
import com.propertyvista.test.mock.security.OperationsAlertFacadeMock;

@Category({ FunctionalTests.class })
public class PaymentHealthMonitorTest extends LeaseFinancialTestBase {

    private CustomerDataModel customerDataModel;

    private Customer customer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        registerFacadeMock(CreditCardPaymentProcessorFacade.class, CreditCardPaymentProcessorFacadeMock.class);
        CreditCardPaymentProcessorFacadeMock.init();
        registerFacadeMock(EncryptedStorageFacade.class, EncryptedStorageFacadeMock.class);
        registerFacadeMock(OperationsAlertFacade.class, OperationsAlertFacadeMock.class);

        customerDataModel = getDataModel(CustomerDataModel.class);
        customer = customerDataModel.addCustomer();
        createLease("01-Jan-2011", "01-Sep-2012", new BigDecimal(100), BigDecimal.ZERO, customer);
        activateLease();

    }

    public void testVerifyCardTransactions() throws Exception {
        setSysDate("2011-01-01");

        // Make a payments
        List<PaymentRecord> paymentRecords = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LeasePaymentMethod paymentMethod = customerDataModel.addPaymentMethod(customer, getBuilding(), PaymentType.CreditCard);
            Persistence.service().commit();

            PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethod, "100");
            Persistence.service().commit();

            ServerSideFactory.create(PaymentFacade.class).processPaymentUnitOfWork(paymentRecord, true);

            new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Received);
            paymentRecords.add(paymentRecord);
        }

        SchedulerMock.runProcess(PmcProcessType.vistaHeathMonitor, (Date) null, 0, 0);

        // 3 days later, Has missing CardsAggregatedTransfer
        SchedulerMock.runProcess(PmcProcessType.vistaHeathMonitor, "2011-01-04", 0, 1);

        int expectedFailed = 0;
        // Break records in different ways
        {
            CardTransactionRecord cardTransactionRecord = EntityFactory.create(CardTransactionRecord.class);
            cardTransactionRecord.voided().setValue(true);
            breakCardTransactionRecord(cardTransactionRecord, paymentRecords.get(0));
            expectedFailed++;
        }

        {
            CardTransactionRecord cardTransactionRecord = EntityFactory.create(CardTransactionRecord.class);
            cardTransactionRecord.saleResponseCode().setValue("1366");
            breakCardTransactionRecord(cardTransactionRecord, paymentRecords.get(1));
            expectedFailed++;
        }

        {
            paymentRecords.get(2).paymentStatus().setValue(PaymentStatus.Rejected);
            Persistence.service().persist(paymentRecords.get(2));
            expectedFailed++;
        }

        Persistence.service().commit();
        SchedulerMock.runProcess(PmcProcessType.vistaHeathMonitor, (Date) null, 0, expectedFailed);
    }

    private void breakCardTransactionRecord(final CardTransactionRecord cardTransactionRecordTemplate, PaymentRecord paymentRecord) {
        final EntityQueryCriteria<CardTransactionRecord> criteria = EntityQueryCriteria.create(CardTransactionRecord.class);
        criteria.eq(criteria.proto().paymentTransactionId(),
                ServerSideFactory.create(CreditCardFacade.class).getTransactionreferenceNumber(ReferenceNumberPrefix.RentPayments, paymentRecord.id()));

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                int count = Persistence.service().update(criteria, cardTransactionRecordTemplate);
                Assert.assertEquals("Updated Records", 1, count);
                return null;
            }
        });

    }

    public void testVerifyCardTransactionScheduled() throws Exception {
        setSysDate("2011-01-01");

        //Make Scheduled
        {
            LeasePaymentMethod paymentMethod = customerDataModel.addPaymentMethod(customer, getBuilding(), PaymentType.CreditCard);
            Persistence.service().commit();

            PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethod, "100");
            paymentRecord.targetDate().setValue(DateUtils.monthAdd(SystemDateManager.getLogicalDate(), 3));
            ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
            Persistence.service().commit();

            ServerSideFactory.create(PaymentFacade.class).schedulePayment(paymentRecord);
            Persistence.service().commit();
        }

        SchedulerMock.runProcess(PmcProcessType.vistaHeathMonitor, (Date) null, 0, 0);

        // 3 days later, Has missing CardsAggregatedTransfer
        SchedulerMock.runProcess(PmcProcessType.vistaHeathMonitor, "2011-01-04", 0, 0);
    }

    public void testVerifyCardTransactionCanceled() throws Exception {
        setSysDate("2011-01-01");

        //Make Scheduled
        {
            LeasePaymentMethod paymentMethod = customerDataModel.addPaymentMethod(customer, getBuilding(), PaymentType.CreditCard);
            Persistence.service().commit();

            PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), paymentMethod, "100");
            paymentRecord.targetDate().setValue(DateUtils.monthAdd(SystemDateManager.getLogicalDate(), 3));
            ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
            Persistence.service().commit();

            ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecord);
            Persistence.service().commit();
        }

        SchedulerMock.runProcess(PmcProcessType.vistaHeathMonitor, (Date) null, 0, 0);

        // 3 days later, Has missing CardsAggregatedTransfer
        SchedulerMock.runProcess(PmcProcessType.vistaHeathMonitor, "2011-01-04", 0, 0);
    }

}

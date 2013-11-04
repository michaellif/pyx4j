/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 5, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.payment.PaymentBatchContext;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;
import com.propertyvista.test.integration.InvoiceLineItemTester;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.test.mock.schedule.SchedulerMock;
import com.propertyvista.yardi.mock.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.PropertyUpdater;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;

@Category(RegressionTests.class)
public class PaymentBatchSingleBuildingEcheckYardiTest extends PaymentYardiTestBase {

    private Lease lease11;

    private Lease lease12;

    private Lease lease13;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createYardiBuilding("prop123");
        createYardiLease("prop123", "t000111");
        createYardiLease("prop123", "t000112");
        createYardiLease("prop123", "t000113");

        setSysDate("2011-01-01");
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        loadBuildingToModel("prop123");

        lease11 = loadLeaseAndCreateEcheckPaymentMethod("t000111");
        lease12 = loadLeaseAndCreateEcheckPaymentMethod("t000112");
        lease13 = loadLeaseAndCreateEcheckPaymentMethod("t000113");
    }

    public void testSuccessfulBatchPostingDirectCall() throws Exception {
        // Make a payment
        final List<PaymentRecord> paymentRecords = new ArrayList<PaymentRecord>();
        paymentRecords.add(getDataModel(LeaseDataModel.class).createPaymentRecord(lease11, PaymentType.Echeck, "101.00"));
        paymentRecords.add(getDataModel(LeaseDataModel.class).createPaymentRecord(lease12, PaymentType.Echeck, "102.00"));
        paymentRecords.add(getDataModel(LeaseDataModel.class).createPaymentRecord(lease13, PaymentType.Echeck, "103.00"));
        Persistence.service().commit();

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, Exception>() {
            @Override
            public Void execute() throws Exception {
                PaymentBatchContext paymentBatchContext = ServerSideFactory.create(ARFacade.class).createPaymentBatchContext(lease11.unit().building());
                for (PaymentRecord paymentRecord : paymentRecords) {
                    ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, paymentBatchContext);
                }
                paymentBatchContext.postBatch();
                return null;
            }
        });

        new PaymentRecordTester(lease11.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
        new PaymentRecordTester(lease12.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
        new PaymentRecordTester(lease13.billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());
        Persistence.service().commit();

        new InvoiceLineItemTester(lease11).count(YardiPayment.class, 1).lastRecordAmount(YardiPayment.class, "-101.00");
        new InvoiceLineItemTester(lease12).count(YardiPayment.class, 1).lastRecordAmount(YardiPayment.class, "-102.00");
        new InvoiceLineItemTester(lease13).count(YardiPayment.class, 1).lastRecordAmount(YardiPayment.class, "-103.00");

        // Cancel one record, Can't use UnitOfWork in tests
        //N.B. The transaction will lock the InvoiceLineItem table in HSQLDB, There is a Hack in YardiMockResidentTransactionsStubImpl
        ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecords.get(0));
        Persistence.service().commit();

        new InvoiceLineItemTester(lease11).count(YardiPayment.class, 0);
    }

    public void testSuccessfulBatchPostingProcess() throws Exception {
        // Make a payment
        final List<PaymentRecord> paymentRecords = new ArrayList<PaymentRecord>();
        paymentRecords.add(getDataModel(LeaseDataModel.class).schedulePaymentRecord(lease11, PaymentType.Echeck, "101.00", "2011-01-02"));
        paymentRecords.add(getDataModel(LeaseDataModel.class).schedulePaymentRecord(lease12, PaymentType.Echeck, "102.00", "2011-01-02"));
        paymentRecords.add(getDataModel(LeaseDataModel.class).schedulePaymentRecord(lease13, PaymentType.Echeck, "103.00", "2011-01-02"));
        Persistence.service().commit();

        setSysDate("2011-01-02");

        //Run the batch process
        SchedulerMock.runProcess(PmcProcessType.paymentsScheduledEcheck, "2011-01-02");

        new PaymentRecordTester(lease11.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
        new PaymentRecordTester(lease12.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
        new PaymentRecordTester(lease13.billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());
        Persistence.service().commit();

        new InvoiceLineItemTester(lease11).count(YardiPayment.class, 1).lastRecordAmount(YardiPayment.class, "-101.00");
        new InvoiceLineItemTester(lease12).count(YardiPayment.class, 1).lastRecordAmount(YardiPayment.class, "-102.00");
        new InvoiceLineItemTester(lease13).count(YardiPayment.class, 1).lastRecordAmount(YardiPayment.class, "-103.00");

        // Cancel one record, Can't use UnitOfWork in tests
        //N.B. The transaction will lock the InvoiceLineItem table in HSQLDB, There is a Hack in YardiMockResidentTransactionsStubImpl
        ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecords.get(0));
        Persistence.service().commit();

        new InvoiceLineItemTester(lease11).count(YardiPayment.class, 0);
    }

    public void testFailedPaymentsBatchPosting() throws Exception {
        // Make a payment
        final List<PaymentRecord> paymentRecords = new ArrayList<PaymentRecord>();
        paymentRecords.add(getDataModel(LeaseDataModel.class).schedulePaymentRecord(lease11, PaymentType.Echeck, "101.00", "2011-01-02"));
        paymentRecords.add(getDataModel(LeaseDataModel.class).schedulePaymentRecord(lease12, PaymentType.Echeck, "102.00", "2011-01-02"));
        paymentRecords.add(getDataModel(LeaseDataModel.class).schedulePaymentRecord(lease13, PaymentType.Echeck, "103.00", "2011-01-02"));
        Persistence.service().commit();

        {
            PropertyUpdater updater = new PropertyUpdater("prop123")//
                    .set(PropertyUpdater.MockFeatures.BlockTransactionPostLeases, lease12.leaseId().getValue());
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }

        setSysDate("2011-01-02");

        //Run the batch process
        SchedulerMock.runProcess(PmcProcessType.paymentsScheduledEcheck, "2011-01-02");

        new PaymentRecordTester(lease11.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
        new PaymentRecordTester(lease12.billingAccount()).lastRecordStatus(PaymentStatus.Scheduled);
        new PaymentRecordTester(lease13.billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());
        Persistence.service().commit();

        new InvoiceLineItemTester(lease11).count(YardiPayment.class, 1).lastRecordAmount(YardiPayment.class, "-101.00");
        new InvoiceLineItemTester(lease12).count(YardiPayment.class, 0);
        new InvoiceLineItemTester(lease13).count(YardiPayment.class, 1).lastRecordAmount(YardiPayment.class, "-103.00");

        // Cancel one record, Can't use UnitOfWork in tests
        //N.B. The transaction will lock the InvoiceLineItem table in HSQLDB, There is a Hack in YardiMockResidentTransactionsStubImpl
        ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecords.get(0));
        Persistence.service().commit();

        new InvoiceLineItemTester(lease11).count(YardiPayment.class, 0);
    }
}

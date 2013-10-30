/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 4, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.test.mock.schedule.SchedulerMock;
import com.propertyvista.yardi.mock.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.PropertyUpdater;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;

public class PaymentBatchThreeBuildingYardiTest extends PaymentYardiTestBase {

    private static final Logger log = LoggerFactory.getLogger(PaymentBatchThreeBuildingYardiTest.class);

    private Lease lease11;

    private Lease lease12;

    private Lease lease21;

    private Lease lease22;

    private Lease lease31;

    private Lease lease32;

    final List<PaymentRecord> paymentRecords = new ArrayList<PaymentRecord>();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createYardiBuilding("prop123");
        createYardiBuilding("prop2");
        createYardiBuilding("prop3");

        createYardiLease("prop123", "t000111");
        createYardiLease("prop123", "t000112");

        createYardiLease("prop2", "t000211");
        createYardiLease("prop2", "t000212");

        createYardiLease("prop3", "t000311");
        createYardiLease("prop3", "t000312");

        setSysDate("2011-01-01");
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123,prop2,prop3"), new ExecutionMonitor());

        loadBuildingToModel("prop123");
        loadBuildingToModel("prop2");
        loadBuildingToModel("prop3");

        lease11 = loadLeaseAndCreateEcheckPaymentMethod("t000111");
        lease12 = loadLeaseAndCreateEcheckPaymentMethod("t000112");

        lease21 = loadLeaseAndCreateEcheckPaymentMethod("t000211");
        lease22 = loadLeaseAndCreateEcheckPaymentMethod("t000212");

        lease31 = loadLeaseAndCreateEcheckPaymentMethod("t000311");
        lease32 = loadLeaseAndCreateEcheckPaymentMethod("t000312");

        // Make a payments
        paymentRecords.add(getDataModel(LeaseDataModel.class).schedulePaymentRecord(lease11, PaymentType.Echeck, "101.00", "2011-01-02"));
        paymentRecords.add(getDataModel(LeaseDataModel.class).schedulePaymentRecord(lease12, PaymentType.Echeck, "102.00", "2011-01-02"));

        paymentRecords.add(getDataModel(LeaseDataModel.class).schedulePaymentRecord(lease21, PaymentType.Echeck, "201.00", "2011-01-02"));
        paymentRecords.add(getDataModel(LeaseDataModel.class).schedulePaymentRecord(lease22, PaymentType.Echeck, "202.00", "2011-01-02"));

        paymentRecords.add(getDataModel(LeaseDataModel.class).schedulePaymentRecord(lease31, PaymentType.Echeck, "301.00", "2011-01-02"));
        paymentRecords.add(getDataModel(LeaseDataModel.class).schedulePaymentRecord(lease32, PaymentType.Echeck, "302.00", "2011-01-02"));

        Persistence.service().commit();
    }

    public void testSuccessfulBatchPosting() throws Exception {

        setSysDate("2011-01-02");

        //Run the batch process
        SchedulerMock.runProcess(PmcProcessType.paymentsScheduledEcheck, "2011-01-02");

        new PaymentRecordTester(lease11.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
        new PaymentRecordTester(lease12.billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        new PaymentRecordTester(lease21.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
        new PaymentRecordTester(lease22.billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        new PaymentRecordTester(lease31.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
        new PaymentRecordTester(lease32.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
    }

    public void testOneBatchFailedPosting() throws Exception {
        {
            PropertyUpdater updater = new PropertyUpdater("prop2")//
                    .set(PropertyUpdater.MockFeatures.BlockBatchOpening, true);
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }

        setSysDate("2011-01-02");

        //Run the batch process
        SchedulerMock.runProcess(PmcProcessType.paymentsScheduledEcheck, "2011-01-02");

        new PaymentRecordTester(lease11.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
        new PaymentRecordTester(lease12.billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        new PaymentRecordTester(lease21.billingAccount()).lastRecordStatus(PaymentStatus.Scheduled);
        new PaymentRecordTester(lease22.billingAccount()).lastRecordStatus(PaymentStatus.Scheduled);

        new PaymentRecordTester(lease31.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
        new PaymentRecordTester(lease32.billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        // Test how process will recover
        {
            PropertyUpdater updater = new PropertyUpdater("prop2")//
                    .set(PropertyUpdater.MockFeatures.BlockBatchOpening, false);
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }

        SchedulerMock.runProcess(PmcProcessType.paymentsScheduledEcheck, "2011-01-02");

        new PaymentRecordTester(lease21.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
        new PaymentRecordTester(lease22.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
    }

    public void testZerroAmountCancelataion() throws Exception {
        // cancel first record in batch
        {
            PaymentRecord recordToCancell = Persistence.service().retrieve(PaymentRecord.class, paymentRecords.get(2).getPrimaryKey());
            recordToCancell.amount().setValue(BigDecimal.ZERO);
            Persistence.service().persist(recordToCancell);
            log.info("Payment {} to be canceled", recordToCancell.id().getValue());
        }
        // cancel last record in batch
        {
            PaymentRecord recordToCancell = Persistence.service().retrieve(PaymentRecord.class, paymentRecords.get(5).getPrimaryKey());
            recordToCancell.amount().setValue(BigDecimal.ZERO);
            Persistence.service().persist(recordToCancell);
            log.info("Payment {} to be canceled", recordToCancell.id().getValue());
        }
        Persistence.service().commit();

        setSysDate("2011-01-02");

        //Run the batch process
        SchedulerMock.runProcess(PmcProcessType.paymentsScheduledEcheck, "2011-01-02");

        new PaymentRecordTester(lease11.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
        new PaymentRecordTester(lease12.billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        new PaymentRecordTester(lease21.billingAccount()).lastRecordStatus(PaymentStatus.Canceled);
        new PaymentRecordTester(lease22.billingAccount()).lastRecordStatus(PaymentStatus.Queued);

        new PaymentRecordTester(lease31.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
        new PaymentRecordTester(lease32.billingAccount()).lastRecordStatus(PaymentStatus.Canceled);
    }

}

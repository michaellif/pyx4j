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

import java.util.ArrayList;
import java.util.List;

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
        {
            // @formatter:off
            PropertyUpdater updater = new PropertyUpdater("prop2").
            set(PropertyUpdater.ADDRESS.Address1, "22 prop2 str").
            set(PropertyUpdater.ADDRESS.Country, "Canada");        
            // @formatter:on
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }
        {
            // @formatter:off
            PropertyUpdater updater = new PropertyUpdater("prop3").
            set(PropertyUpdater.ADDRESS.Address1, "22 prop2 str").
            set(PropertyUpdater.ADDRESS.Country, "Canada");        
            // @formatter:on
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }

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

        lease11 = loadLeaseAndCreatePaymentMethod("t000111");
        lease12 = loadLeaseAndCreatePaymentMethod("t000112");

        lease21 = loadLeaseAndCreatePaymentMethod("t000211");
        lease22 = loadLeaseAndCreatePaymentMethod("t000212");

        lease31 = loadLeaseAndCreatePaymentMethod("t000311");
        lease32 = loadLeaseAndCreatePaymentMethod("t000312");

        // Make a payments
        final List<PaymentRecord> paymentRecords = new ArrayList<PaymentRecord>();
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

}

/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 2, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.financial.payment.PadTransactionUtils;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.eft.mock.efttransport.ScheduledResponseReconciliation;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.test.mock.schedule.SchedulerMock;
import com.propertyvista.yardi.mock.updater.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.updater.PropertyUpdater;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;

@Category(FunctionalTests.class)
public class PaymentReturnEcheckYardiTest extends PaymentYardiTestBase {

    private Lease lease11;

    private Lease lease12;

    private List<PaymentRecord> paymentRecords;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createYardiBuilding("prop123");
        createYardiLease("prop123", "t000111");
        createYardiLease("prop123", "t000112");

        setSysDate("2011-01-01");
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        loadBuildingToModel("prop123");

        lease11 = loadLeaseAndCreateEcheckPaymentMethod("t000111");
        lease12 = loadLeaseAndCreateEcheckPaymentMethod("t000112");

        // Make a payment
        paymentRecords = new ArrayList<PaymentRecord>();
        paymentRecords.add(getDataModel(LeaseDataModel.class).createPaymentRecord(lease11, PaymentType.Echeck, "101.00"));
        paymentRecords.add(getDataModel(LeaseDataModel.class).createPaymentRecord(lease12, PaymentType.Echeck, "102.00"));

        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecords.get(0), null);
        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecords.get(1), null);
        Persistence.service().commit();

        setCaledonPadPaymentBatchProcess();
    }

    public void testSuccessfulReturn() throws Exception {
        advanceSysDate("2011-01-02");
        new PaymentRecordTester(lease11.billingAccount()).lastRecordStatus(PaymentStatus.Cleared);
        new PaymentRecordTester(lease12.billingAccount()).lastRecordStatus(PaymentStatus.Cleared);

        MockEventBus.fireEvent(new ScheduledResponseReconciliation(PadTransactionUtils.toCaldeonTransactionId(paymentRecords.get(0).id()), "905", "Test"));

        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveReconciliation, (Date) null);
        new PaymentRecordTester(lease11.billingAccount()).lastRecordStatus(PaymentStatus.Returned);
    }

    public void testNoAccessSuspendedReturn() throws Exception {
        advanceSysDate("2011-01-02");
        new PaymentRecordTester(lease11.billingAccount()).lastRecordStatus(PaymentStatus.Cleared);
        new PaymentRecordTester(lease12.billingAccount()).lastRecordStatus(PaymentStatus.Cleared);

        {
            PropertyUpdater updater = new PropertyUpdater("prop123")//
                    .set(PropertyUpdater.MockFeatures.BlockAccess, true);
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }

        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        Building building = loadBuildingToModel("prop123");
        Assert.assertTrue("Now Suspended", building.suspended().getValue(false));

        MockEventBus.fireEvent(new ScheduledResponseReconciliation(PadTransactionUtils.toCaldeonTransactionId(paymentRecords.get(0).id()), "905", "Test"));

        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveReconciliation, (Date) null);
        new PaymentRecordTester(lease11.billingAccount()).lastRecordStatus(PaymentStatus.Returned);
    }

    public void testNoAccessNotSuspendedReturn() throws Exception {
        advanceSysDate("2011-01-02");
        new PaymentRecordTester(lease11.billingAccount()).lastRecordStatus(PaymentStatus.Cleared);
        new PaymentRecordTester(lease12.billingAccount()).lastRecordStatus(PaymentStatus.Cleared);

        {
            PropertyUpdater updater = new PropertyUpdater("prop123")//
                    .set(PropertyUpdater.MockFeatures.BlockAccess, true);
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }

        MockEventBus.fireEvent(new ScheduledResponseReconciliation(PadTransactionUtils.toCaldeonTransactionId(paymentRecords.get(0).id()), "905", "Test"));

        SchedulerMock.runProcess(PmcProcessType.paymentsReceiveReconciliation, (Date) null);

        // Record not processed, Since building is not suspend, but we will try again
        new PaymentRecordTester(lease11.billingAccount()).lastRecordStatus(PaymentStatus.Cleared);

        Building building = loadBuildingToModel("prop123");
        ServerSideFactory.create(BuildingFacade.class).suspend(building);
        Persistence.service().commit();

        SchedulerMock.runProcess(PmcProcessType.paymentsPadProcessReconciliation, (Date) null);

        new PaymentRecordTester(lease11.billingAccount()).lastRecordStatus(PaymentStatus.Returned);
    }
}

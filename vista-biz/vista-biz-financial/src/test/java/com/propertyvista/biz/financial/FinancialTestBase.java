/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.admin.domain.scheduler.RunStats;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.billing.print.BillPrint;
import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.financial.preload.ARPolicyDataModel;
import com.propertyvista.biz.financial.preload.BuildingDataModel;
import com.propertyvista.biz.financial.preload.DepositPolicyDataModel;
import com.propertyvista.biz.financial.preload.IdAssignmentPolicyDataModel;
import com.propertyvista.biz.financial.preload.LeaseAdjustmentPolicyDataModel;
import com.propertyvista.biz.financial.preload.LeaseAdjustmentReasonDataModel;
import com.propertyvista.biz.financial.preload.LeaseBillingPolicyDataModel;
import com.propertyvista.biz.financial.preload.LeaseDataModel;
import com.propertyvista.biz.financial.preload.LocationsDataModel;
import com.propertyvista.biz.financial.preload.PreloadConfig;
import com.propertyvista.biz.financial.preload.ProductItemTypesDataModel;
import com.propertyvista.biz.financial.preload.ProductTaxPolicyDataModel;
import com.propertyvista.biz.financial.preload.TaxesDataModel;
import com.propertyvista.biz.financial.preload.TenantDataModel;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Feature.Type;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.DepositLifecycle;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.Status;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.server.jobs.DepositInterestAdjustmentProcess;
import com.propertyvista.server.jobs.DepositRefundProcess;
import com.propertyvista.server.jobs.LeaseActivationProcess;
import com.propertyvista.server.jobs.LeaseCompletionProcess;
import com.propertyvista.server.jobs.PmcProcess;
import com.propertyvista.server.jobs.PmcProcessContext;

public abstract class FinancialTestBase extends VistaDBTestBase {

    public interface FunctionalTests {
    }

    public interface RegressionTests extends FunctionalTests {
    }

    private long startTime;

    protected LeaseDataModel leaseDataModel;

    protected LeaseAdjustmentReasonDataModel leaseAdjustmentReasonDataModel;

    protected ARPolicyDataModel arPolicyDataModel;

    public interface Task {
        void execute();
    }

    public class Schedule {
        private final int[] fields = new int[Calendar.FIELD_COUNT];

        public Schedule set(int field, int value) {
            fields[field] = value;
            return this;
        }

        public int[] getFields() {
            return fields;
        }

        public boolean match(Calendar cal) {
            int match = 0;
            for (int field = 0; field < fields.length; field++) {
                if (fields[field] == 0 || fields[field] == cal.get(field)) {
                    match += 1;
                }
            }
            return (match == fields.length);
        }
    }

    private final HashMap<Schedule, Task> taskSchedule = new HashMap<Schedule, Task>();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Persistence.service().startBackgroundProcessTransaction();
        SysDateManager.setSysDate("01-Jan-2000");
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void tearDown() throws Exception {
        Persistence.service().commit();
        Persistence.service().endTransaction();
        System.out.println("Execution Time - " + (System.currentTimeMillis() - startTime) + "ms");
        super.tearDown();
    }

    protected void preloadData() {
        boolean ok = false;
        PreloadConfig config = new PreloadConfig();
        try {
            preloadData(config);
            ok = true;
        } finally {
            if (!ok) {
                Persistence.service().commit();
            }
        }

    }

    protected void preloadData(PreloadConfig config) {
        setDate("01-Jan-2010");

        LocationsDataModel locationsDataModel = new LocationsDataModel(config);
        locationsDataModel.generate();

        TaxesDataModel taxesDataModel = new TaxesDataModel(config, locationsDataModel);
        taxesDataModel.generate();

        ProductItemTypesDataModel productItemTypesDataModel = new ProductItemTypesDataModel(config);
        productItemTypesDataModel.generate();

        leaseAdjustmentReasonDataModel = new LeaseAdjustmentReasonDataModel(config);
        leaseAdjustmentReasonDataModel.generate();

        BuildingDataModel buildingDataModel = new BuildingDataModel(config, productItemTypesDataModel);
        buildingDataModel.generate();

        IdAssignmentPolicyDataModel idAssignmentPolicyDataModel = new IdAssignmentPolicyDataModel(config);
        idAssignmentPolicyDataModel.generate();

        ProductTaxPolicyDataModel productTaxPolicyDataModel = new ProductTaxPolicyDataModel(config, productItemTypesDataModel, taxesDataModel,
                buildingDataModel);
        productTaxPolicyDataModel.generate();

        DepositPolicyDataModel depositPolicyDataModel = new DepositPolicyDataModel(config, productItemTypesDataModel, buildingDataModel);
        depositPolicyDataModel.generate();

        LeaseAdjustmentPolicyDataModel leaseAdjustmentPolicyDataModel = new LeaseAdjustmentPolicyDataModel(config, leaseAdjustmentReasonDataModel,
                taxesDataModel, buildingDataModel);
        leaseAdjustmentPolicyDataModel.generate();

        TenantDataModel tenantDataModel = new TenantDataModel(config);
        tenantDataModel.generate();

        leaseDataModel = new LeaseDataModel(config, buildingDataModel, tenantDataModel);
        leaseDataModel.generate();

        //TODO if commented - check exception
        LeaseBillingPolicyDataModel leaseBillingPolicyDataModel = new LeaseBillingPolicyDataModel(config, buildingDataModel);
        leaseBillingPolicyDataModel.generate();

        arPolicyDataModel = new ARPolicyDataModel(config, buildingDataModel);
        arPolicyDataModel.generate();

    }

    protected Bill runBilling(boolean confirm) {
        return runBilling(confirm, false);
    }

    protected Bill runBilling(boolean confirm, boolean printBill) {
        Lease lease = retrieveLease();

        DataDump.dump("leaseT", lease);

        Bill bill = ServerSideFactory.create(BillingFacade.class).runBilling(lease);

        Persistence.service().commit();

        return confirmBill(bill, confirm, printBill);
    }

    protected Bill runBillingPreview() {
        Lease lease = retrieveLease();

        if (lease.version().isNull()) {
            lease = retrieveLeaseDraft();
        }

        DataDump.dump("leaseT", lease);

        Bill bill = ServerSideFactory.create(BillingFacade.class).runBillingPreview(lease);

        Persistence.service().commit();

        return bill;
    }

    protected Bill confirmBill(Bill bill, boolean confirm, boolean printBill) {
        if (confirm) {
            bill = ServerSideFactory.create(BillingFacade.class).confirmBill(bill);
        } else {
            bill = ServerSideFactory.create(BillingFacade.class).rejectBill(bill, "Just test");
        }

        Persistence.service().commit();

        if (printBill) {
            printBill(bill);
        }
        return bill;
    }

    protected void printBill(Bill bill) {
        try {
            BillPrint.printBill(BillingUtils.createBillDto(bill), new FileOutputStream(billFileName(bill, getClass().getSimpleName())));
            DataDump.dump("bill", bill);
            DataDump.dump("lease", bill.billingAccount().lease());
        } catch (FileNotFoundException e) {
            throw new Error(e);
        }

    }

    protected void printTransactionHistory(TransactionHistoryDTO transactionHistory) {
        TransactionHistoryPrinter.printTransactionHistory(transactionHistory, transactionHistoryFileName(transactionHistory, getClass().getSimpleName()));
    }

    protected void setLeaseTerms(String leaseDateFrom, String leaseDateTo) {
        setLeaseTerms(leaseDateFrom, leaseDateTo, null, null);
    }

    protected void setLeaseTerms(String leaseDateFrom, String leaseDateTo, BigDecimal agreedPrice, BigDecimal carryforwardBalance) {
        Lease lease = retrieveLeaseForEdit();

        // TODO - this must be done via facade
        lease.leaseFrom().setValue(FinancialTestsUtils.getDate(leaseDateFrom));
        lease.leaseTo().setValue(FinancialTestsUtils.getDate(leaseDateTo));
        lease.version().leaseProducts().serviceItem().expirationDate().set(lease.leaseTo());

        lease.billingAccount().carryforwardBalance().setValue(carryforwardBalance);

        if (carryforwardBalance != null) {
            lease.creationDate().setValue(new LogicalDate(SysDateManager.getSysDate()));
        }

        if (agreedPrice != null) {
            lease.version().leaseProducts().serviceItem().agreedPrice().setValue(agreedPrice);
        }

        Persistence.service().persist(lease);

        BillingType billingType = ServerSideFactory.create(BillingFacade.class).ensureBillingType(lease);

        lease.billingAccount().billingType().set(billingType);

        Persistence.service().persist(lease.billingAccount());

        Persistence.service().commit();

    }

    protected Bill approveApplication(boolean printBill) {
        ServerSideFactory.create(LeaseFacade.class).approveApplication(retrieveLease(), null, null);
        Persistence.service().commit();
        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(retrieveLease());
        if (printBill) {
            printBill(bill);
        }
        return bill;
    }

    protected Bill approveExistingLease(boolean printBill) {
        ServerSideFactory.create(LeaseFacade.class).approveExistingLease(retrieveLease());
        Persistence.service().commit();
        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(retrieveLease());
        if (printBill) {
            printBill(bill);
        }
        return bill;
    }

    protected void activateLease() {
        ServerSideFactory.create(LeaseFacade.class).activate(retrieveLease().getPrimaryKey());
    }

    protected void completeLease() {
        ServerSideFactory.create(LeaseFacade.class).complete(retrieveLease().getPrimaryKey());
    }

    protected void closeLease() {
        ServerSideFactory.create(LeaseFacade.class).close(retrieveLease().getPrimaryKey());
    }

    protected Lease retrieveLease() {
        return Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey().asCurrentKey());
    }

    protected Lease retrieveLeaseDraft() {
        return Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey().asDraftKey());
    }

    protected Lease retrieveLeaseForEdit() {
        return Persistence.retrieveDraftForEdit(Lease.class, leaseDataModel.getLeaseKey());
    }

    protected BillableItem addParking(String effectiveDate, String expirationDate) {
        return addBillableItem(Type.parking, effectiveDate, expirationDate);
    }

    protected BillableItem addParking() {
        return addBillableItem(Type.parking);
    }

    protected BillableItem addLocker(String effectiveDate, String expirationDate) {
        return addBillableItem(Type.locker, effectiveDate, expirationDate);
    }

    protected BillableItem addLocker() {
        return addBillableItem(Type.locker);
    }

    protected BillableItem addPet(String effectiveDate, String expirationDate) {
        return addBillableItem(Type.pet, effectiveDate, expirationDate);
    }

    protected BillableItem addPet() {
        BillableItem billableItem = addBillableItem(Type.pet);
        return billableItem;
    }

    protected BillableItem addBooking(String date) {
        return addBillableItem(Type.booking, date, date);
    }

    protected void changeBillableItem(String billableItemId, String effectiveDate, String expirationDate) {
        Lease lease = retrieveLeaseForEdit();

        BillableItem billableItem = findBillableItem(billableItemId, lease);

        billableItem.effectiveDate().setValue(FinancialTestsUtils.getDate(effectiveDate));
        billableItem.expirationDate().setValue(FinancialTestsUtils.getDate(expirationDate));

        lease.saveAction().setValue(SaveAction.saveAsDraft);
        Persistence.service().persist(lease);
        Persistence.service().commit();
    }

    private BillableItem addBillableItem(Feature.Type featureType) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());
        return addBillableItem(featureType, lease.leaseFrom().getValue(), lease.leaseTo().getValue());
    }

    private BillableItem addBillableItem(Feature.Type featureType, String effectiveDate, String expirationDate) {
        return addBillableItem(featureType, FinancialTestsUtils.getDate(effectiveDate), FinancialTestsUtils.getDate(expirationDate));
    }

    private BillableItem addBillableItem(Feature.Type featureType, LogicalDate effectiveDate, LogicalDate expirationDate) {
        Lease draftLease = retrieveLeaseForEdit();

        ProductItem serviceItem = leaseDataModel.getServiceItem();
        Service.ServiceV service = serviceItem.product().cast();
        Persistence.service().retrieve(service.features());
        for (Feature feature : service.features()) {
            if (featureType.equals(feature.version().type().getValue()) && feature.version().items().size() != 0) {
                LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
                BillableItem billableItem = leaseFacade.createBillableItem(feature.version().items().get(0), draftLease.unit().building());

                billableItem.effectiveDate().setValue(effectiveDate);
                billableItem.expirationDate().setValue(expirationDate);
                draftLease.version().leaseProducts().featureItems().add(billableItem);

                draftLease.saveAction().setValue(SaveAction.saveAsDraft);
                leaseFacade.persist(draftLease);

                return billableItem;
            }
        }
        return null;
    }

    protected Lease finalizeLeaseAdendum() {
        return ServerSideFactory.create(LeaseFacade.class).finalize(retrieveLeaseDraft());
    }

    protected void setDeposit(String billableItemId, DepositType depositType) {
        Lease lease = retrieveLeaseForEdit();
        BillableItem billableItem = findBillableItem(billableItemId, lease);

        Deposit deposit = ServerSideFactory.create(DepositFacade.class).createDeposit(depositType, billableItem, lease.unit().building());
        DepositLifecycle depositLifecycle = ServerSideFactory.create(DepositFacade.class).createDepositLifecycle(deposit, lease.billingAccount());

        Persistence.service().persist(deposit);
        Persistence.service().persist(depositLifecycle);
    }

    protected BillableItemAdjustment addServiceAdjustment(String value, BillableItemAdjustment.Type adjustmentType, String effectiveDate, String expirationDate) {
        Lease lease = retrieveLeaseForEdit();
        return addBillableItemAdjustment(lease.version().leaseProducts().serviceItem().uid().getValue(), value, adjustmentType,
                FinancialTestsUtils.getDate(effectiveDate), FinancialTestsUtils.getDate(expirationDate));
    }

    protected BillableItemAdjustment addServiceAdjustment(String value, BillableItemAdjustment.Type adjustmentType) {
        Lease lease = retrieveLeaseForEdit();
        return addBillableItemAdjustment(lease.version().leaseProducts().serviceItem().uid().getValue(), value, adjustmentType, lease.leaseFrom().getValue(),
                lease.leaseTo().getValue());
    }

    protected BillableItemAdjustment addFeatureAdjustment(String billableItemId, String value, BillableItemAdjustment.Type adjustmentType) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());
        return addBillableItemAdjustment(billableItemId, value, adjustmentType, lease.leaseFrom().getValue(), lease.leaseTo().getValue());

    }

    protected BillableItemAdjustment addFeatureAdjustment(String billableItemId, String value, BillableItemAdjustment.Type adjustmentType,
            String effectiveDate, String expirationDate) {
        return addBillableItemAdjustment(billableItemId, value, adjustmentType, FinancialTestsUtils.getDate(effectiveDate),
                FinancialTestsUtils.getDate(expirationDate));
    }

    private BillableItemAdjustment addBillableItemAdjustment(String billableItemId, String value, BillableItemAdjustment.Type adjustmentType,
            LogicalDate effectiveDate, LogicalDate expirationDate) {

        Lease lease = retrieveLeaseForEdit();
        BillableItem actualBillableItem = findBillableItem(billableItemId, lease);

        BillableItemAdjustment adjustment = EntityFactory.create(BillableItemAdjustment.class);
        adjustment.effectiveDate().setValue(new LogicalDate(lease.leaseFrom().getValue()));
        if (value == null) {
            adjustment.value().setValue(null);
        } else {
            adjustment.value().setValue(new BigDecimal(value));
        }
        adjustment.type().setValue(adjustmentType);
        adjustment.effectiveDate().setValue(effectiveDate);
        adjustment.expirationDate().setValue(expirationDate);
        adjustment.billableItem().set(actualBillableItem);

        Persistence.service().persist(adjustment);
        Persistence.service().commit();

        return adjustment;
    }

    protected void changeBillableItemAdjustment(String billableItemAdjustmentId, String effectiveDate, String expirationDate, SaveAction saveAction) {
        Lease lease = retrieveLeaseForEdit();

        BillableItemAdjustment billableItemAdjustment = findBillableItemAdjustment(billableItemAdjustmentId, lease);

        billableItemAdjustment.effectiveDate().setValue(FinancialTestsUtils.getDate(effectiveDate));
        billableItemAdjustment.expirationDate().setValue(FinancialTestsUtils.getDate(expirationDate));

        lease.saveAction().setValue(saveAction);
        Persistence.service().persist(lease);
        Persistence.service().commit();
    }

    protected LeaseAdjustment addGoodWillCredit(String amount) {
        return addGoodWillCredit(amount, true);
    }

    protected LeaseAdjustment addGoodWillCredit(String amount, boolean immediate) {
        return addLeaseAdjustment(amount, leaseAdjustmentReasonDataModel.getReason(LeaseAdjustmentReasonDataModel.Reason.goodWill), immediate);
    }

    protected LeaseAdjustment addAccountCharge(String amount) {
        return addAccountCharge(amount, true);
    }

    protected LeaseAdjustment addAccountCharge(String amount, boolean immediate) {
        return addLeaseAdjustment(amount, leaseAdjustmentReasonDataModel.getReason(LeaseAdjustmentReasonDataModel.Reason.accountCharge), immediate);
    }

    private LeaseAdjustment addLeaseAdjustment(String amount, LeaseAdjustmentReason reason, boolean immediate) {

        Lease lease = retrieveLease();

        LeaseAdjustment adjustment = EntityFactory.create(LeaseAdjustment.class);
        adjustment.status().setValue(Status.submited);
        adjustment.amount().setValue(new BigDecimal(amount));
        adjustment.executionType().setValue(immediate ? LeaseAdjustment.ExecutionType.immediate : LeaseAdjustment.ExecutionType.pending);
        adjustment.targetDate().setValue(new LogicalDate(SysDateManager.getSysDate()));
        adjustment.description().setValue(reason.name().getValue());
        adjustment.reason().setValue(reason.getValue());
        adjustment.billingAccount().set(lease.billingAccount());

        Persistence.service().persist(adjustment);
        Persistence.service().commit();

        if (immediate) {
            ServerSideFactory.create(ARFacade.class).postImmediateAdjustment(adjustment);
        }
        return adjustment;
    }

    protected PaymentRecord receivePayment(String receivedDate, LeaseParticipant leaseParticipant, String amount, PaymentType type) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());

        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        paymentRecord.createdDate().setValue(FinancialTestsUtils.getDate(receivedDate));
        paymentRecord.receivedDate().setValue(FinancialTestsUtils.getDate(receivedDate));
        paymentRecord.amount().setValue(new BigDecimal(amount));
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Submitted);
        paymentRecord.billingAccount().set(lease.billingAccount());
        paymentRecord.leaseParticipant().set(leaseParticipant);

        // add payment method type
        PaymentMethod pm = EntityFactory.create(PaymentMethod.class);
        pm.type().setValue(type);
        paymentRecord.paymentMethod().set(pm);
        Persistence.service().persist(pm);

        Persistence.service().persist(paymentRecord);
        Persistence.service().commit();

        return paymentRecord;
    }

    protected void postPayment(PaymentRecord paymentRecord) {
        ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord);
        Persistence.service().commit();
    }

    protected PaymentRecord receiveAndPostPayment(String receivedDate, String amount) {
        return receiveAndPostPayment(receivedDate, amount, PaymentType.Cash);
    }

    protected PaymentRecord receiveAndPostPayment(String receivedDate, String amount, PaymentType type) {
        PaymentRecord paymentRecord = receivePayment(receivedDate, null, amount, type); // TODO : find leaseParticipant, if nedded here!.. 
        postPayment(paymentRecord);
        return paymentRecord;
    }

    protected DebitCreditLink createHardDebitCreditLink(PaymentRecord paymentRecord, InvoiceDebit debit, String targetAmount) {
        return ServerSideFactory.create(ARFacade.class).createHardLink(paymentRecord, debit, new BigDecimal(targetAmount));
    }

    protected void removeHardLink(DebitCreditLink link) {
        ServerSideFactory.create(ARFacade.class).removeHardLink(link);
    }

    protected void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) {
        ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, applyNSF);
        Persistence.service().commit();
    }

    private BillableItem findBillableItem(String billableItemId, Lease lease) {
        if (lease.version().leaseProducts().serviceItem().uid().getValue().equals(billableItemId)) {
            return lease.version().leaseProducts().serviceItem();
        } else {
            for (BillableItem item : lease.version().leaseProducts().featureItems()) {
                if (item.uid().getValue().equals(billableItemId)) {
                    return item;
                }
            }
        }
        return null;
    }

    private BillableItemAdjustment findBillableItemAdjustment(String billableItemAdjustmentId, Lease lease) {
        for (BillableItemAdjustment itemAdjustment : lease.version().leaseProducts().serviceItem().adjustments()) {
            if (itemAdjustment.uid().getValue().equals(billableItemAdjustmentId)) {
                return itemAdjustment;
            }
        }
        for (BillableItem item : lease.version().leaseProducts().featureItems()) {
            for (BillableItemAdjustment itemAdjustment : item.adjustments()) {
                if (itemAdjustment.uid().getValue().equals(billableItemAdjustmentId)) {
                    return itemAdjustment;
                }
            }
        }
        return null;
    }

    protected static String billFileName(Bill bill, String prefix) {
        String ext = ".pdf";
        File dir = new File("target", "bills-dump");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new Error("Can't create directory " + dir.getAbsolutePath());
            }
        }
        File file = new File(dir, prefix + "-" + bill.billSequenceNumber().getValue() + ext);
        if (file.exists()) {
            if (!file.delete()) {
                throw new Error("Can't delete file " + file.getAbsolutePath());
            }
        }
        return file.getAbsolutePath();
    }

    protected static String transactionHistoryFileName(TransactionHistoryDTO transactionHistory, String prefix) {
        String ext = ".txt";
        File dir = new File("target", "transaction-history-dump");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new Error("Can't create directory " + dir.getAbsolutePath());
            }
        }
        File file = new File(dir, prefix + "-" + transactionHistory.issueDate().getValue() + ext);
        return file.getAbsolutePath();
    }

    protected void clearSchedle() {
        taskSchedule.clear();
    }

    protected void scheduleTask(Task task, String... dates) {
        for (String dateStr : dates) {
            Schedule entry = new Schedule();
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(DateUtils.detectDateformat(dateStr));
            entry.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
            entry.set(Calendar.MONTH, cal.get(Calendar.MONTH));
            entry.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            taskSchedule.put(entry, task);
        }
    }

    protected void scheduleTask(Task task, Schedule entry) {
        taskSchedule.put(entry, task);
    }

    protected void schedulePmcProcess(final PmcProcess pmcProcess, Schedule entry) {
        taskSchedule.put(entry, new Task() {
            @Override
            public void execute() {
                RunStats runStats = EntityFactory.create(RunStats.class);
                Date runDate = SysDateManager.getSysDate();
                PmcProcessContext sharedContext = new PmcProcessContext(runStats, runDate);
                if (pmcProcess.start(sharedContext)) {
                    PmcProcessContext pmcContext = new PmcProcessContext(runStats, runDate);
                    pmcProcess.executePmcJob(pmcContext);
                    Persistence.service().commit();
                    pmcProcess.complete(sharedContext);
                }
            }
        });
    }

    protected void setDate(String dateStr) {
        SysDateManager.setSysDate(dateStr);
    }

    protected void advanceDate(String dateStr) {
        Date curDate = SysDateManager.getSysDate();
        Date setDate = DateUtils.detectDateformat(dateStr);
        if (setDate.before(curDate)) {
            throw new Error("Can't go back in time from " + curDate.toString() + " to " + setDate.toString());
        }
        // run tasks scheduled before the set date
        Calendar calTo = GregorianCalendar.getInstance();
        calTo.setTime(setDate);
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(curDate);
        while (cal.before(calTo)) {
            cal.add(Calendar.DATE, 1);
            for (Schedule entry : taskSchedule.keySet()) {
                if (!entry.match(cal)) {
                    continue;
                }
                Task task = taskSchedule.get(entry);
                if (task != null) {
                    SysDateManager.setSysDate(cal.getTime());
                    task.execute();
                }
            }
        }
        SysDateManager.setSysDate(setDate);
    }

    protected void setDepositBatchProcess() {
        // schedule deposit interest adjustment batch process to run on 1st of each month
        schedulePmcProcess(new DepositInterestAdjustmentProcess(), new Schedule().set(Calendar.DAY_OF_MONTH, 1));
        // schedule deposit refund batch process to run every day
        schedulePmcProcess(new DepositRefundProcess(), new Schedule());
    }

    protected void setLeaseBatchProcess() {
        // schedule lease activation and completion process to run daily
        schedulePmcProcess(new LeaseActivationProcess(), new Schedule());
        schedulePmcProcess(new LeaseCompletionProcess(), new Schedule());
    }
}

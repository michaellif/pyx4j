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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.billing.print.BillPrint;
import com.propertyvista.biz.financial.preload.ARPolicyDataModel;
import com.propertyvista.biz.financial.preload.BuildingDataModel;
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
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Feature.Type;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.AdjustmentType;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.ExecutionType;
import com.propertyvista.domain.tenant.lease.Deposit.RepaymentMode;
import com.propertyvista.domain.tenant.lease.Deposit.ValueType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.Status;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.TransactionHistoryDTO;

public abstract class FinancialTestBase extends VistaDBTestBase {

    private long startTime;

    protected LeaseDataModel leaseDataModel;

    protected LeaseAdjustmentReasonDataModel leaseAdjustmentReasonDataModel;

    protected ARPolicyDataModel arPolicyDataModel;

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
        LocationsDataModel locationsDataModel = new LocationsDataModel(config);
        locationsDataModel.generate(true);

        TaxesDataModel taxesDataModel = new TaxesDataModel(config, locationsDataModel);
        taxesDataModel.generate(true);

        ProductItemTypesDataModel productItemTypesDataModel = new ProductItemTypesDataModel(config);
        productItemTypesDataModel.generate(true);

        leaseAdjustmentReasonDataModel = new LeaseAdjustmentReasonDataModel(config);
        leaseAdjustmentReasonDataModel.generate(true);

        BuildingDataModel buildingDataModel = new BuildingDataModel(config, productItemTypesDataModel);
        buildingDataModel.generate(true);

        IdAssignmentPolicyDataModel idAssignmentPolicyDataModel = new IdAssignmentPolicyDataModel(config);
        idAssignmentPolicyDataModel.generate(true);

        ProductTaxPolicyDataModel productTaxPolicyDataModel = new ProductTaxPolicyDataModel(config, productItemTypesDataModel, taxesDataModel,
                buildingDataModel);
        productTaxPolicyDataModel.generate(true);

        LeaseAdjustmentPolicyDataModel leaseAdjustmentPolicyDataModel = new LeaseAdjustmentPolicyDataModel(config, leaseAdjustmentReasonDataModel,
                taxesDataModel, buildingDataModel);
        leaseAdjustmentPolicyDataModel.generate(true);

        TenantDataModel tenantDataModel = new TenantDataModel(config);
        tenantDataModel.generate(true);

        leaseDataModel = new LeaseDataModel(config, buildingDataModel, tenantDataModel);
        leaseDataModel.generate(true);

        //TODO if commented - check exception
        LeaseBillingPolicyDataModel leaseBillingPolicyDataModel = new LeaseBillingPolicyDataModel(config, buildingDataModel);
        leaseBillingPolicyDataModel.generate(true);

        arPolicyDataModel = new ARPolicyDataModel(config, buildingDataModel);
        arPolicyDataModel.generate(true);

    }

    protected Bill runBilling(boolean confirm) {
        return runBilling(confirm, false);
    }

    protected Bill runBilling(boolean confirm, boolean printBill) {
        Lease lease = retrieveLease();

        DataDump.dump("leaseT", lease);

        ServerSideFactory.create(BillingFacade.class).runBilling(lease);

        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(lease);

        return confirmBill(bill, confirm, printBill);
    }

    protected Bill confirmBill(Bill bill, boolean confirm, boolean printBill) {
        if (confirm) {
            bill = ServerSideFactory.create(BillingFacade.class).confirmBill(bill);
        } else {
            bill = ServerSideFactory.create(BillingFacade.class).rejectBill(bill);
        }

        Persistence.service().commit();

        if (printBill) {
            try {
                BillPrint.printBill(BillingUtils.createBillDto(bill), new FileOutputStream(billFileName(bill, getClass().getSimpleName())));
                DataDump.dump("bill", bill);
                DataDump.dump("lease", bill.billingAccount().lease());
            } catch (FileNotFoundException e) {
                throw new Error(e);
            }
        }
        return bill;
    }

    protected void printTransactionHistory(TransactionHistoryDTO transactionHistory) {
        TransactionHistoryPrinter.printTransactionHistory(transactionHistory, transactionHistoryFileName(transactionHistory, getClass().getSimpleName()));
    }

    protected void initLease(String leaseDateFrom, String leaseDateTo) {
        initLease(leaseDateFrom, leaseDateTo, null);
    }

    protected void initLease(String leaseDateFrom, String leaseDateTo, BigDecimal carryforwardBalance) {
        Lease lease = retrieveLeaseForEdit();

        lease.leaseFrom().setValue(FinancialTestsUtils.getDate(leaseDateFrom));
        lease.leaseTo().setValue(FinancialTestsUtils.getDate(leaseDateTo));

        lease.billingAccount().carryforwardBalance().setValue(carryforwardBalance);

        ServerSideFactory.create(LeaseFacade.class).initLease(lease);

        BillingType billingType = ServerSideFactory.create(BillingFacade.class).ensureBillingType(lease);

        lease.billingAccount().billingType().set(billingType);

        Persistence.service().persist(lease.billingAccount());

        Persistence.service().commit();

    }

    protected Bill approveApplication() {
        ServerSideFactory.create(LeaseFacade.class).approveApplication(retrieveLease(), null, null);
        return ServerSideFactory.create(BillingFacade.class).getLatestBill(retrieveLease());
    }

    protected Bill verifyExistingLease() {
        ServerSideFactory.create(LeaseFacade.class).verifyExistingLease(retrieveLease());
        return ServerSideFactory.create(BillingFacade.class).getLatestBill(retrieveLease());
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
        return Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());
    }

    protected Lease retrieveLeaseForEdit() {
        return Persistence.retrieveDraft(Lease.class, leaseDataModel.getLeaseKey());
    }

    protected BillableItem addParking(String effectiveDate, String expirationDate, SaveAction saveAction) {
        return addBillableItem(Type.parking, effectiveDate, expirationDate, saveAction);
    }

    protected BillableItem addParking(SaveAction saveAction) {
        return addBillableItem(Type.parking, saveAction);
    }

    protected BillableItem addLocker(String effectiveDate, String expirationDate, SaveAction saveAction) {
        return addBillableItem(Type.locker, effectiveDate, expirationDate, saveAction);
    }

    protected BillableItem addLocker(SaveAction saveAction) {
        return addBillableItem(Type.locker, saveAction);
    }

    protected BillableItem addPet(String effectiveDate, String expirationDate, SaveAction saveAction) {
        return addBillableItem(Type.pet, effectiveDate, expirationDate, saveAction);
    }

    protected BillableItem addPet(SaveAction saveAction) {
        BillableItem billableItem = addBillableItem(Type.pet, SaveAction.saveAsDraft);
        setDeposit(billableItem.uid().getValue(), "200", ValueType.amount, RepaymentMode.returnAtLeaseEnd, saveAction);
        return billableItem;
    }

    protected BillableItem addBooking(String date, SaveAction saveAction) {
        return addBillableItem(Type.booking, date, date, saveAction);
    }

    protected void changeBillableItem(String billableItemId, String effectiveDate, String expirationDate, SaveAction saveAction) {
        Lease lease = retrieveLeaseForEdit();

        BillableItem billableItem = findBillableItem(billableItemId, lease);

        billableItem.effectiveDate().setValue(FinancialTestsUtils.getDate(effectiveDate));
        billableItem.expirationDate().setValue(FinancialTestsUtils.getDate(expirationDate));

        lease.saveAction().setValue(saveAction);
        Persistence.service().persist(lease);
        Persistence.service().commit();
    }

    private BillableItem addBillableItem(Feature.Type featureType, SaveAction saveAction) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());
        return addBillableItem(featureType, lease.leaseFrom().getValue(), lease.leaseTo().getValue(), saveAction);
    }

    private BillableItem addBillableItem(Feature.Type featureType, String effectiveDate, String expirationDate, SaveAction saveAction) {
        return addBillableItem(featureType, FinancialTestsUtils.getDate(effectiveDate), FinancialTestsUtils.getDate(expirationDate), saveAction);
    }

    private BillableItem addBillableItem(Feature.Type featureType, LogicalDate effectiveDate, LogicalDate expirationDate, SaveAction saveAction) {
        Lease draftLease = retrieveLeaseForEdit();

        ProductItem serviceItem = leaseDataModel.getServiceItem();
        Service.ServiceV service = serviceItem.product().cast();
        Persistence.service().retrieve(service.features());
        for (Feature feature : service.features()) {
            if (featureType.equals(feature.version().type().getValue()) && feature.version().items().size() != 0) {
                BillableItem billableItem = EntityFactory.create(BillableItem.class);
                Persistence.service().retrieve(feature.version().items().get(0));
                billableItem.item().set(feature.version().items().get(0));
                billableItem.agreedPrice().setValue(billableItem.item().price().getValue());

                billableItem.effectiveDate().setValue(effectiveDate);
                billableItem.expirationDate().setValue(expirationDate);
                draftLease.version().leaseProducts().featureItems().add(billableItem);

                draftLease.saveAction().setValue(saveAction);
                Persistence.service().persist(draftLease);
                Persistence.service().commit();

                return billableItem;
            }
        }
        return null;
    }

    protected void setDeposit(String billableItemId, String value, ValueType valueType, RepaymentMode repaymentMode, SaveAction saveAction) {
        Lease lease = retrieveLeaseForEdit();
        BillableItem billableItem = findBillableItem(billableItemId, lease);
        billableItem.deposit().depositAmount().setValue(new BigDecimal(value));
        billableItem.deposit().valueType().setValue(valueType);
        billableItem.deposit().repaymentMode().setValue(repaymentMode);
        lease.saveAction().setValue(saveAction);
        Persistence.service().persist(lease);
        Persistence.service().commit();
    }

    protected BillableItemAdjustment addServiceAdjustment(String value, AdjustmentType adjustmentType, ExecutionType termType) {
        Lease lease = retrieveLeaseForEdit();
        return addBillableItemAdjustment(lease.version().leaseProducts().serviceItem().uid().getValue(), value, adjustmentType, termType, lease.leaseFrom()
                .getValue(), lease.leaseTo().getValue());
    }

    protected BillableItemAdjustment addFeatureAdjustment(String billableItemId, String value, AdjustmentType adjustmentType, ExecutionType termType) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());
        return addBillableItemAdjustment(billableItemId, value, adjustmentType, termType, lease.leaseFrom().getValue(), lease.leaseTo().getValue());

    }

    protected BillableItemAdjustment addFeatureAdjustment(String billableItemId, String value, AdjustmentType adjustmentType, ExecutionType termType,
            String effectiveDate, String expirationDate) {
        return addBillableItemAdjustment(billableItemId, value, adjustmentType, termType, FinancialTestsUtils.getDate(effectiveDate),
                FinancialTestsUtils.getDate(expirationDate));
    }

    private BillableItemAdjustment addBillableItemAdjustment(String billableItemId, String value, AdjustmentType adjustmentType, ExecutionType executionType,
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
        adjustment.adjustmentType().setValue(adjustmentType);
        adjustment.executionType().setValue(executionType);
        adjustment.effectiveDate().setValue(effectiveDate);
        adjustment.expirationDate().setValue(expirationDate);
        adjustment.description().setValue(executionType.name());
        adjustment.billableItem().set(actualBillableItem);

        Persistence.service().persist(adjustment);
        Persistence.service().commit();

        return adjustment;
    }

    protected LeaseAdjustment addGoodWillCredit(String amount) {
        return addGoodWillCredit(amount, null);
    }

    protected LeaseAdjustment addGoodWillCredit(String amount, String effectiveDate) {
        return addLeaseAdjustment(amount, leaseAdjustmentReasonDataModel.getReason(LeaseAdjustmentReasonDataModel.Reason.goodWill),
                FinancialTestsUtils.getDate(effectiveDate));
    }

    protected LeaseAdjustment addAccountCharge(String amount) {
        return addAccountCharge(amount, null);
    }

    protected LeaseAdjustment addAccountCharge(String amount, String effectiveDate) {
        return addLeaseAdjustment(amount, leaseAdjustmentReasonDataModel.getReason(LeaseAdjustmentReasonDataModel.Reason.accountCharge),
                FinancialTestsUtils.getDate(effectiveDate));
    }

    private LeaseAdjustment addLeaseAdjustment(String amount, LeaseAdjustmentReason reason, LogicalDate targetDate) {

        Lease lease = retrieveLease();

        LeaseAdjustment adjustment = EntityFactory.create(LeaseAdjustment.class);
        adjustment.status().setValue(Status.submited);
        adjustment.amount().setValue(new BigDecimal(amount));
        adjustment.executionType().setValue(targetDate == null ? LeaseAdjustment.ExecutionType.immediate : LeaseAdjustment.ExecutionType.pending);
        adjustment.targetDate().setValue(new LogicalDate(SysDateManager.getSysDate()));
        adjustment.description().setValue(reason.name().getValue());
        adjustment.reason().setValue(reason.getValue());
        adjustment.billingAccount().set(lease.billingAccount());

        Persistence.service().persist(adjustment);
        Persistence.service().commit();

        if (targetDate == null) {
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
    }

    protected PaymentRecord receiveAndPostPayment(String receivedDate, String amount) {
        return receiveAndPostPayment(receivedDate, amount, PaymentType.Cash);
    }

    protected PaymentRecord receiveAndPostPayment(String receivedDate, String amount, PaymentType type) {
        PaymentRecord paymentRecord = receivePayment(receivedDate, null, amount, type); // TODO : find leaseParticipant, if nedded here!.. 
        postPayment(paymentRecord);
        return paymentRecord;
    }

    protected void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) {
        ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, applyNSF);
    }

    private BillableItem findBillableItem(String billableItemId, Lease lease) {
        BillableItem billableItem = null;
        if (lease.version().leaseProducts().serviceItem().uid().getValue().equals(billableItemId)) {
            billableItem = lease.version().leaseProducts().serviceItem();
        } else {
            for (BillableItem item : lease.version().leaseProducts().featureItems()) {
                if (item.uid().getValue().equals(billableItemId)) {
                    billableItem = item;
                    break;
                }
            }
        }
        return billableItem;
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
}

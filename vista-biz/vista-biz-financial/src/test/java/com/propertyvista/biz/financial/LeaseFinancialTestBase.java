/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 16, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.billing.print.BillPrint;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.ARCode.Type;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.DepositLifecycle;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.Status;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.test.integration.IntegrationTestBase;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.ARPolicyDataModel;
import com.propertyvista.test.mock.models.AutoPayPolicyDataModel;
import com.propertyvista.test.mock.models.BuildingDataModel;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.DepositPolicyDataModel;
import com.propertyvista.test.mock.models.GLCodeDataModel;
import com.propertyvista.test.mock.models.IdAssignmentPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseAdjustmentPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseBillingPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.MerchantAccountDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;
import com.propertyvista.test.mock.models.ProductTaxPolicyDataModel;
import com.propertyvista.test.mock.models.TaxesDataModel;

public abstract class LeaseFinancialTestBase extends IntegrationTestBase {

    private Building building;

    private Lease lease;

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = new ArrayList<Class<? extends MockDataModel<?>>>();
        models.add(PmcDataModel.class);
        models.add(CustomerDataModel.class);
        models.add(LocationsDataModel.class);
        models.add(TaxesDataModel.class);
        models.add(GLCodeDataModel.class);
        models.add(ARCodeDataModel.class);
        models.add(BuildingDataModel.class);
        models.add(MerchantAccountDataModel.class);
        models.add(IdAssignmentPolicyDataModel.class);
        models.add(ProductTaxPolicyDataModel.class);
        models.add(DepositPolicyDataModel.class);
        models.add(LeaseAdjustmentPolicyDataModel.class);
        models.add(ARPolicyDataModel.class);
        models.add(AutoPayPolicyDataModel.class);
        models.add(LeaseBillingPolicyDataModel.class);
        models.add(LeaseDataModel.class);
        return models;
    }

    protected Lease getLease() {
        return lease;
    }

    protected Building getBuilding() {
        if (building == null) {
            building = getDataModel(BuildingDataModel.class).addBuilding();
            getDataModel(MerchantAccountDataModel.class).addMerchantAccount(building);
            Persistence.service().commit();
        }
        return building;
    }

    protected void createLease(String leaseDateFrom, String leaseDateTo) {
        createLease(leaseDateFrom, leaseDateTo, null, null);
    }

    protected void createLease(String leaseDateFrom, String leaseDateTo, BigDecimal agreedPrice, BigDecimal carryforwardBalance) {
        createLease(leaseDateFrom, leaseDateTo, agreedPrice, carryforwardBalance, getDataModel(CustomerDataModel.class).addCustomer());
    }

    protected void createLease(String leaseDateFrom, String leaseDateTo, BigDecimal agreedPrice, BigDecimal carryforwardBalance, Customer customer) {
        lease = getDataModel(LeaseDataModel.class).addLease(getBuilding(), leaseDateFrom, leaseDateTo, agreedPrice, carryforwardBalance,
                Arrays.asList(new Customer[] { customer }));
    }

    protected void renewLease(String leaseDateTo, BigDecimal agreedPrice, LeaseTerm.Type leaseTermType) {
        LeaseTerm term = ServerSideFactory.create(LeaseFacade.class).createOffer(getLease(), leaseTermType);
        term.termTo().setValue(getDate(leaseDateTo));
        ServerSideFactory.create(LeaseFacade.class).acceptOffer(getLease(), term);
    }

    protected Lease retrieveLease() {
        return ServerSideFactory.create(LeaseFacade.class).load(getLease(), false);
    }

    protected Lease retrieveLeaseDraft() {
        return ServerSideFactory.create(LeaseFacade.class).load(getLease(), true);
    }

    protected Bill runBilling() {
        Lease lease = retrieveLease();

        DataDump.dump("leaseT", lease);

        Bill bill = ServerSideFactory.create(BillingFacade.class).runBilling(lease);

        Persistence.service().commit();

        return bill;
    }

    protected Bill runBilling(boolean confirm) {
        runBilling();
        return confirmBill(confirm);
    }

    protected Bill runBillingPreview() {
        Lease lease = retrieveLease();

        DataDump.dump("leaseT", lease);

        Bill bill = ServerSideFactory.create(BillingFacade.class).runBillingPreview(lease);

        Persistence.service().commit();

        return bill;
    }

    protected Bill confirmBill(boolean confirm) {
        Bill bill = getLatestBill();
        if (confirm) {
            bill = ServerSideFactory.create(BillingFacade.class).confirmBill(bill);
        } else {
            bill = ServerSideFactory.create(BillingFacade.class).rejectBill(bill, "Just test");
        }

        Persistence.service().commit();
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
        printTransactionHistory(transactionHistory, false);
    }

    protected void printTransactionHistory(TransactionHistoryDTO transactionHistory, boolean copyToSystemOut) {
        TransactionHistoryPrinter.printTransactionHistory(transactionHistory, transactionHistoryFileName(transactionHistory, getClass().getSimpleName()));
        if (copyToSystemOut) {
            TransactionHistoryPrinter.printTransactionHistory(transactionHistory);
        }
    }

    protected Bill getBill(int billSequenceNumber) {
        Bill bill = ServerSideFactory.create(BillingFacade.class).getBill(getLease(), billSequenceNumber);
        return bill;
    }

    protected Bill getLatestBill() {
        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(getLease());
        return bill;
    }

    protected Bill getLatestConfirmedBill() {
        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestConfirmedBill(getLease());
        return bill;
    }

    protected Bill approveApplication(boolean printBill) {
        ServerSideFactory.create(LeaseFacade.class).approve(retrieveLease(), null, null);
        Persistence.service().commit();
        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(retrieveLease());
        if (printBill) {
            printBill(bill);
        }
        return bill;
    }

    protected void approveExistingLease(boolean printBill) {
        ServerSideFactory.create(LeaseFacade.class).approve(retrieveLease(), null, null);
        Persistence.service().commit();
    }

    protected void activateLease() {
        ServerSideFactory.create(LeaseFacade.class).activate(retrieveLease());
    }

    protected void completeLease() {
        ServerSideFactory.create(LeaseFacade.class).complete(retrieveLease());
    }

    protected void closeLease() {
        ServerSideFactory.create(LeaseFacade.class).close(retrieveLease());
    }

    protected void terminateLease(CompletionType reason) {
        terminateLease(reason, null);
    }

    protected void terminateLease(CompletionType reason, String asOfDate) {
        LogicalDate asOf;
        if (asOfDate == null) {
            asOf = new LogicalDate(getSysDate());
        } else {
            asOf = new LogicalDate(DateUtils.detectDateformat(asOfDate));
        }
        Lease lease = retrieveLeaseDraft();
        lease.leaseTo().setValue(asOf);
        lease.completion().setValue(reason);
        Persistence.service().persist(lease);

        if (asOfDate == null) {
            // anding now - do complete
            ServerSideFactory.create(LeaseFacade.class).complete(lease);
        }

        Persistence.service().commit();
    }

    protected BillableItem addOutdoorParking(String effectiveDate, String expirationDate) {
        return addBillableItem(ARCodeDataModel.Code.outdoorParking, effectiveDate, expirationDate);
    }

    protected BillableItem addOutdoorParking() {
        return addBillableItem(ARCodeDataModel.Code.outdoorParking);
    }

    protected BillableItem addLargeLocker(String effectiveDate, String expirationDate) {
        return addBillableItem(ARCodeDataModel.Code.largeLocker, effectiveDate, expirationDate);
    }

    protected BillableItem addLargeLocker() {
        return addBillableItem(ARCodeDataModel.Code.largeLocker);
    }

    protected BillableItem addCat(String effectiveDate, String expirationDate) {
        return addBillableItem(ARCodeDataModel.Code.catRent, effectiveDate, expirationDate);
    }

    protected BillableItem addCat() {
        return addBillableItem(ARCodeDataModel.Code.catRent);
    }

    protected BillableItem addBooking(String date) {
        return addBillableItem(ARCodeDataModel.Code.booking, date, date);
    }

    protected void cancelBillableItem(String billableItemId, String expirationDate) {
        Lease lease = retrieveLease();

        BillableItem billableItem = findBillableItem(billableItemId, lease);
        assert (billableItem != null);

        billableItem.expirationDate().setValue(getDate(expirationDate));

        Persistence.service().merge(billableItem);
        Persistence.service().commit();
    }

    protected void changeBillableItem(String billableItemId, String effectiveDate, String expirationDate) {
        Lease lease = retrieveLeaseDraft();

        BillableItem billableItem = findBillableItem(billableItemId, lease);
        assert (billableItem != null);

        billableItem.effectiveDate().setValue(getDate(effectiveDate));
        billableItem.expirationDate().setValue(getDate(expirationDate));

        ServerSideFactory.create(LeaseFacade.class).persist(lease.currentTerm());
        Persistence.service().commit();
    }

    private BillableItem addBillableItem(ARCodeDataModel.Code code) {
        Lease lease = retrieveLease();
        return addBillableItem(code, lease.currentTerm().termFrom().getValue(), lease.currentTerm().termTo().getValue());
    }

    private BillableItem addBillableItem(ARCodeDataModel.Code code, String effectiveDate, String expirationDate) {
        return addBillableItem(code, getDate(effectiveDate), getDate(expirationDate));
    }

    private BillableItem addBillableItem(ARCodeDataModel.Code code, LogicalDate effectiveDate, LogicalDate expirationDate) {
        Lease lease = retrieveLeaseDraft();

        ProductItem serviceItem = lease.currentTerm().version().leaseProducts().serviceItem().item();
        Persistence.service().retrieve(serviceItem.product());
        Service.ServiceV service = serviceItem.product().cast();
        Persistence.service().retrieveMember(service.features());

        ARCode arCode = getDataModel(ARCodeDataModel.class).getARCode(code);

        for (Feature feature : service.features()) {

            if (arCode.equals(feature.code())) {

                Persistence.service().retrieveMember(feature.version().items());
                for (ProductItem item : feature.version().items()) {

                    LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
                    BillableItem billableItem = leaseFacade.createBillableItem(lease, item, lease.unit().building());

                    billableItem.effectiveDate().setValue(effectiveDate);
                    billableItem.expirationDate().setValue(expirationDate);

                    lease.currentTerm().version().leaseProducts().featureItems().add(billableItem);

                    // correct agreed price for existing leases:
                    BigDecimal agreedPrice = null;
                    if (lease.status().getValue() == Lease.Status.ExistingLease) {
                        switch (code) {
                        case outdoorParking:
                            agreedPrice = new BigDecimal("80.00");
                            break;
                        case largeLocker:
                            agreedPrice = new BigDecimal("60.00");
                            break;
                        case catRent:
                            agreedPrice = new BigDecimal("20.00");
                            break;
                        case booking:
                            agreedPrice = new BigDecimal("30.00");
                            break;
                        default:
                            break;
                        }
                    }
                    if (agreedPrice != null) {
                        billableItem.agreedPrice().setValue(agreedPrice);
                    } else {
                        billableItem.agreedPrice().setValue(item.price().getValue());
                    }

                    leaseFacade.persist(lease.currentTerm());
                    Persistence.service().commit();
                    return billableItem;

                }
            }

        }

        return null;
    }

    protected LeaseTerm finalizeLeaseAdendum() {
        LeaseTerm leaseTerm = ServerSideFactory.create(LeaseFacade.class).finalize(retrieveLeaseDraft().currentTerm());
        Persistence.service().commit();
        return leaseTerm;
    }

    protected void setDeposit(String billableItemId, DepositType depositType) {
        Lease lease = retrieveLeaseDraft();
        BillableItem billableItem = findBillableItem(billableItemId, lease);
        assert (billableItem != null);

        Deposit deposit = ServerSideFactory.create(DepositFacade.class).createDeposit(depositType, billableItem, lease.unit().building());
        DepositLifecycle depositLifecycle = ServerSideFactory.create(DepositFacade.class).createDepositLifecycle(deposit, lease.billingAccount());

        billableItem.deposits().add(deposit);
        ServerSideFactory.create(LeaseFacade.class).persist(lease.currentTerm());

        Persistence.service().persist(depositLifecycle);
        Persistence.service().commit();
    }

    protected BillableItemAdjustment addServiceAdjustment(String value, BillableItemAdjustment.Type adjustmentType, String effectiveDate, String expirationDate) {
        Lease lease = retrieveLease();
        return addBillableItemAdjustment(lease.currentTerm().version().leaseProducts().serviceItem().uid().getValue(), value, adjustmentType,
                getDate(effectiveDate), getDate(expirationDate));
    }

    protected BillableItemAdjustment addServiceAdjustment(String value, BillableItemAdjustment.Type adjustmentType) {
        Lease lease = retrieveLease();
        return addBillableItemAdjustment(lease.currentTerm().version().leaseProducts().serviceItem().uid().getValue(), value, adjustmentType, lease
                .currentTerm().termFrom().getValue(), lease.currentTerm().termTo().getValue());
    }

    protected BillableItemAdjustment addFeatureAdjustment(String billableItemId, String value, BillableItemAdjustment.Type adjustmentType) {
        Lease lease = retrieveLease();
        return addBillableItemAdjustment(billableItemId, value, adjustmentType, lease.currentTerm().termFrom().getValue(), lease.currentTerm().termTo()
                .getValue());

    }

    protected BillableItemAdjustment addFeatureAdjustment(String billableItemId, String value, BillableItemAdjustment.Type adjustmentType,
            String effectiveDate, String expirationDate) {
        return addBillableItemAdjustment(billableItemId, value, adjustmentType, getDate(effectiveDate), getDate(expirationDate));
    }

    protected BillableItemAdjustment addBillableItemAdjustment(String billableItemId, String value, BillableItemAdjustment.Type adjustmentType,
            LogicalDate effectiveDate, LogicalDate expirationDate) {

        Lease lease = retrieveLeaseDraft();
        BillableItem actualBillableItem = findBillableItem(billableItemId, lease);
        assert (actualBillableItem != null);

        BillableItemAdjustment adjustment = EntityFactory.create(BillableItemAdjustment.class);
        if (value == null) {
            adjustment.value().setValue(null);
        } else {
            adjustment.value().setValue(new BigDecimal(value));
        }
        adjustment.type().setValue(adjustmentType);
        adjustment.effectiveDate().setValue(effectiveDate);
        adjustment.expirationDate().setValue(expirationDate);

        actualBillableItem.adjustments().add(adjustment);

        ServerSideFactory.create(LeaseFacade.class).persist(lease.currentTerm());
        Persistence.service().commit();

        return adjustment;
    }

    protected void changeBillableItemAdjustment(String billableItemAdjustmentId, String effectiveDate, String expirationDate) {
        Lease lease = retrieveLeaseDraft();

        BillableItemAdjustment billableItemAdjustment = findBillableItemAdjustment(billableItemAdjustmentId, lease);

        billableItemAdjustment.effectiveDate().setValue(getDate(effectiveDate));
        billableItemAdjustment.expirationDate().setValue(getDate(expirationDate));

        ServerSideFactory.create(LeaseFacade.class).finalize(lease.currentTerm());
        Persistence.service().commit();
    }

    protected LeaseAdjustment addGoodWillCredit(String amount) {
        return addGoodWillCredit(amount, true);
    }

    protected LeaseAdjustment addGoodWillCredit(String amount, boolean immediate) {
        return addLeaseAdjustment(amount, ServerSideFactory.create(ARFacade.class).getReservedARCode(Type.AccountCredit), immediate);
    }

    protected LeaseAdjustment addAccountCharge(String amount) {
        return addAccountCharge(amount, true);
    }

    protected LeaseAdjustment addAccountCharge(String amount, boolean immediate) {
        return addLeaseAdjustment(amount, ServerSideFactory.create(ARFacade.class).getReservedARCode(Type.AccountCharge), immediate);
    }

    private LeaseAdjustment addLeaseAdjustment(String amount, ARCode arCode, boolean immediate) {
        Lease lease = retrieveLease();

        LeaseAdjustment adjustment = EntityFactory.create(LeaseAdjustment.class);
        adjustment.status().setValue(Status.submited);
        adjustment.amount().setValue(new BigDecimal(amount));
        adjustment.executionType().setValue(immediate ? LeaseAdjustment.ExecutionType.immediate : LeaseAdjustment.ExecutionType.pending);
        adjustment.targetDate().setValue(new LogicalDate(getSysDate()));
        adjustment.description().setValue(arCode.name().getValue());
        adjustment.code().setValue(arCode.getValue());
        adjustment.billingAccount().set(lease.billingAccount());

        Persistence.service().persist(adjustment);
        Persistence.service().commit();

        if (immediate) {
            ServerSideFactory.create(ARFacade.class).postImmediateAdjustment(adjustment);
            Persistence.service().commit();
        }
        return adjustment;
    }

    @Deprecated
    protected AutopayAgreement setPreauthorizedPayment(String value) {
        throw new Error("TODO remove this");
    }

    protected AutopayAgreement setPreauthorizedPayment(List<AutopayAgreement.AutopayAgreementCoveredItem> items) {
        AutopayAgreement preauthorizedPayment = getDataModel(LeaseDataModel.class).createPreauthorizedPayment(lease, items);
        Assert.assertNotNull("CreatePreauthorizedPayment failed to create PAP", preauthorizedPayment);
        Persistence.service().commit();
        return preauthorizedPayment;
    }

    protected void deletePreauthorizedPayment(AutopayAgreement preauthorizedPayment) {
        ServerSideFactory.create(PaymentMethodFacade.class).deleteAutopayAgreement(preauthorizedPayment);
        Persistence.service().commit();
    }

    protected PaymentRecord receivePayment(String receivedDate, String amount, PaymentType type) {
        Lease lease = retrieveLease();

        // Just use the first tenant
        LeaseTermParticipant<?> leaseParticipant = lease.currentTerm().version().tenants().iterator().next();
        Persistence.service().retrieve(leaseParticipant);

        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        paymentRecord.createdDate().setValue(getDate(receivedDate));
        paymentRecord.receivedDate().setValue(getDate(receivedDate));
        paymentRecord.amount().setValue(new BigDecimal(amount));
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Submitted);
        paymentRecord.billingAccount().set(lease.billingAccount());
        paymentRecord.leaseTermParticipant().set(leaseParticipant);

        // add payment method type
        LeasePaymentMethod pm = EntityFactory.create(LeasePaymentMethod.class);
        pm.customer().set(leaseParticipant.leaseParticipant().customer());
        pm.type().setValue(type);
        paymentRecord.paymentMethod().set(pm);
        Persistence.service().persist(pm);

        Persistence.service().persist(paymentRecord);
        Persistence.service().commit();

        return paymentRecord;
    }

    protected void postPayment(PaymentRecord paymentRecord) throws ARException {
        ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord, null);
        Persistence.service().commit();
    }

    protected PaymentRecord receiveAndPostPayment(String receivedDate, String amount) throws ARException {
        return receiveAndPostPayment(receivedDate, amount, PaymentType.Cash);
    }

    protected PaymentRecord receiveAndPostPayment(String receivedDate, String amount, PaymentType type) throws ARException {
        PaymentRecord paymentRecord = receivePayment(receivedDate, amount, type);
        postPayment(paymentRecord);
        return paymentRecord;
    }

    protected DebitCreditLink createHardDebitCreditLink(PaymentRecord paymentRecord, InvoiceDebit debit, String targetAmount) {
        DebitCreditLink link = ServerSideFactory.create(ARFacade.class).createHardLink(paymentRecord, debit, new BigDecimal(targetAmount));
        Persistence.service().commit();
        return link;

    }

    protected void removeHardLink(DebitCreditLink link) {
        ServerSideFactory.create(ARFacade.class).removeHardLink(link);
        Persistence.service().commit();
    }

    protected void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) throws ARException {
        ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, applyNSF);
        Persistence.service().commit();
    }

    protected LogicalDate getAutopayExecutionDate() {
        return ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(getLease());
    }

    protected LogicalDate getActualAutopayExecutionDate() {
        BillingCycle curCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(getLease(), new LogicalDate(getSysDate()));
        return curCycle.actualAutopayExecutionDate().getValue();
    }

    private BillableItem findBillableItem(String billableItemId, Lease lease) {
        if (lease.currentTerm().version().leaseProducts().serviceItem().uid().getValue().equals(billableItemId)) {
            return lease.currentTerm().version().leaseProducts().serviceItem();
        } else {
            for (BillableItem item : lease.currentTerm().version().leaseProducts().featureItems()) {
                if (item.uid().getValue().equals(billableItemId)) {
                    return item;
                }
            }
        }
        return null;
    }

    private BillableItemAdjustment findBillableItemAdjustment(String billableItemAdjustmentId, Lease lease) {
        for (BillableItemAdjustment itemAdjustment : lease.currentTerm().version().leaseProducts().serviceItem().adjustments()) {
            if (itemAdjustment.uid().getValue().equals(billableItemAdjustmentId)) {
                return itemAdjustment;
            }
        }
        for (BillableItem item : lease.currentTerm().version().leaseProducts().featureItems()) {
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
}

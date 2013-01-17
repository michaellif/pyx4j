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
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.admin.domain.scheduler.RunStats;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.billing.print.BillPrint;
import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Feature.Type;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.DepositLifecycle;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.Status;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.server.jobs.BillingProcess;
import com.propertyvista.server.jobs.DepositInterestAdjustmentProcess;
import com.propertyvista.server.jobs.DepositRefundProcess;
import com.propertyvista.server.jobs.FutureBillingCycleInitializationProcess;
import com.propertyvista.server.jobs.LeaseActivationProcess;
import com.propertyvista.server.jobs.LeaseCompletionProcess;
import com.propertyvista.server.jobs.LeaseRenewalProcess;
import com.propertyvista.server.jobs.PmcProcess;
import com.propertyvista.server.jobs.PmcProcessContext;
import com.propertyvista.test.preloader.ARPolicyDataModel;
import com.propertyvista.test.preloader.BuildingDataModel;
import com.propertyvista.test.preloader.DepositPolicyDataModel;
import com.propertyvista.test.preloader.IdAssignmentPolicyDataModel;
import com.propertyvista.test.preloader.LeaseAdjustmentPolicyDataModel;
import com.propertyvista.test.preloader.LeaseAdjustmentReasonDataModel;
import com.propertyvista.test.preloader.LeaseBillingPolicyDataModel;
import com.propertyvista.test.preloader.LocationsDataModel;
import com.propertyvista.test.preloader.PmcDataModel;
import com.propertyvista.test.preloader.PreloadConfig;
import com.propertyvista.test.preloader.ProductItemTypesDataModel;
import com.propertyvista.test.preloader.ProductTaxPolicyDataModel;
import com.propertyvista.test.preloader.TaxesDataModel;
import com.propertyvista.test.preloader.TenantDataModel;

public abstract class FinancialTestBase extends VistaDBTestBase {

    public interface FunctionalTests {
    }

    public interface RegressionTests extends FunctionalTests {
    }

    private long startTime;

    protected PmcDataModel pmcDataModel;

    protected BuildingDataModel buildingDataModel;

    protected LeaseAdjustmentReasonDataModel leaseAdjustmentReasonDataModel;

    protected ARPolicyDataModel arPolicyDataModel;

    protected TenantDataModel tenantDataModel;

    protected PreloadConfig config;

    protected Lease lease;

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

        NamespaceManager.setNamespace("t" + System.currentTimeMillis());

        TestLifecycle.testSession(null, VistaBasicBehavior.CRM);
        TestLifecycle.testNamespace(NamespaceManager.getNamespace());
        TestLifecycle.beginRequest();
    }

    @Override
    protected void tearDown() throws Exception {
        Persistence.service().commit();
        Persistence.service().endTransaction();
        System.out.println("Execution Time - " + (System.currentTimeMillis() - startTime) + "ms");

        TestLifecycle.tearDown();
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
        this.config = config;

        setDate("01-Jan-2010");

        pmcDataModel = new PmcDataModel(config);
        pmcDataModel.generate();

        LocationsDataModel locationsDataModel = new LocationsDataModel(config);
        locationsDataModel.generate();

        TaxesDataModel taxesDataModel = new TaxesDataModel(config, locationsDataModel);
        taxesDataModel.generate();

        ProductItemTypesDataModel productItemTypesDataModel = new ProductItemTypesDataModel(config);
        productItemTypesDataModel.generate();

        leaseAdjustmentReasonDataModel = new LeaseAdjustmentReasonDataModel(config);
        leaseAdjustmentReasonDataModel.generate();

        buildingDataModel = new BuildingDataModel(config, locationsDataModel, productItemTypesDataModel);
        buildingDataModel.generate();

        IdAssignmentPolicyDataModel idAssignmentPolicyDataModel = new IdAssignmentPolicyDataModel(config, pmcDataModel);
        idAssignmentPolicyDataModel.generate();

        ProductTaxPolicyDataModel productTaxPolicyDataModel = new ProductTaxPolicyDataModel(config, productItemTypesDataModel, taxesDataModel,
                buildingDataModel);
        productTaxPolicyDataModel.generate();

        DepositPolicyDataModel depositPolicyDataModel = new DepositPolicyDataModel(config, productItemTypesDataModel, buildingDataModel);
        depositPolicyDataModel.generate();

        LeaseAdjustmentPolicyDataModel leaseAdjustmentPolicyDataModel = new LeaseAdjustmentPolicyDataModel(config, leaseAdjustmentReasonDataModel,
                taxesDataModel, buildingDataModel);
        leaseAdjustmentPolicyDataModel.generate();

        tenantDataModel = new TenantDataModel(config);
        tenantDataModel.generate();

        LeaseBillingPolicyDataModel leaseBillingPolicyDataModel = new LeaseBillingPolicyDataModel(config, pmcDataModel);
        leaseBillingPolicyDataModel.generate();

        arPolicyDataModel = new ARPolicyDataModel(config, buildingDataModel);
        arPolicyDataModel.generate();

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
        TransactionHistoryPrinter.printTransactionHistory(transactionHistory, transactionHistoryFileName(transactionHistory, getClass().getSimpleName()));
    }

    protected void createLease(String leaseDateFrom, String leaseDateTo) {
        createLease(leaseDateFrom, leaseDateTo, null, null);
    }

    protected void createLease(String leaseDateFrom, String leaseDateTo, BigDecimal agreedPrice, BigDecimal carryforwardBalance) {
        ProductItem serviceItem = buildingDataModel.generateResidentialUnitServiceItem();

        if (carryforwardBalance != null) {
            lease = ServerSideFactory.create(LeaseFacade.class).create(Lease.Status.ExistingLease);
        } else {
            lease = ServerSideFactory.create(LeaseFacade.class).create(Lease.Status.Application);
            ServerSideFactory.create(OccupancyFacade.class).scopeAvailable(serviceItem.element().cast().getPrimaryKey());
        }

        lease.currentTerm().termFrom().setValue(FinancialTestsUtils.getDate(leaseDateFrom));
        lease.currentTerm().termTo().setValue(FinancialTestsUtils.getDate(leaseDateTo));

        lease = ServerSideFactory.create(LeaseFacade.class).setUnit(lease, (AptUnit) serviceItem.element().cast());

        if (agreedPrice != null) {
            lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(agreedPrice);
        } else if (serviceItem.price().getValue().compareTo(BigDecimal.ZERO) != 0) {
            lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(serviceItem.price().getValue());
        } else {
            lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(new BigDecimal(500 + RandomUtil.randomInt(500)));
        }

        LeaseTermTenant tenantInLease = EntityFactory.create(LeaseTermTenant.class);
        tenantInLease.leaseParticipant().customer().set(tenantDataModel.getTenantCustomer());
        tenantInLease.role().setValue(LeaseTermParticipant.Role.Applicant);
        lease.currentTerm().version().tenants().add(tenantInLease);

        lease.billingAccount().carryforwardBalance().setValue(carryforwardBalance);

        lease.creationDate().setValue(new LogicalDate(SysDateManager.getSysDate()));

        ServerSideFactory.create(LeaseFacade.class).persist(lease);
        Persistence.service().commit();
    }

    protected void renewLease(String leaseDateTo, BigDecimal agreedPrice, LeaseTerm.Type leaseTermType) {
        LeaseTerm term = ServerSideFactory.create(LeaseFacade.class).createOffer(lease, leaseTermType);
        term.termTo().setValue(FinancialTestsUtils.getDate(leaseDateTo));
        ServerSideFactory.create(LeaseFacade.class).acceptOffer(lease, term);
    }

    protected Bill getBill(int billSequenceNumber) {
        Bill bill = ServerSideFactory.create(BillingFacade.class).getBill(retrieveLease(), billSequenceNumber);
        return bill;
    }

    protected Bill getLatestBill() {
        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(retrieveLease());
        return bill;
    }

    protected Bill getLatestConfirmedBill() {
        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestConfirmedBill(retrieveLease());
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
            asOf = new LogicalDate(SysDateManager.getSysDate());
        } else {
            asOf = new LogicalDate(DateUtils.detectDateformat(asOfDate));
        }
        Lease lease = retrieveLeaseForEdit();
        lease.leaseTo().setValue(asOf);
        lease.completion().setValue(reason);
        Persistence.service().persist(lease);

        if (asOfDate == null) {
            // anding now - do complete
            ServerSideFactory.create(LeaseFacade.class).complete(lease);
        }

        Persistence.service().commit();
    }

    protected Lease retrieveLease() {
        return ServerSideFactory.create(LeaseFacade.class).load(lease, false);
    }

    protected Lease retrieveLeaseDraft() {
        return ServerSideFactory.create(LeaseFacade.class).load(lease, true);
    }

    protected Lease retrieveLeaseForEdit() {
        return ServerSideFactory.create(LeaseFacade.class).load(lease, true);
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

    protected void cancelBillableItem(String billableItemId, String expirationDate) {
        Lease lease = retrieveLease();

        BillableItem billableItem = findBillableItem(billableItemId, lease);
        assert (billableItem != null);

        billableItem.expirationDate().setValue(FinancialTestsUtils.getDate(expirationDate));

        Persistence.service().merge(billableItem);
        Persistence.service().commit();
    }

    protected void changeBillableItem(String billableItemId, String effectiveDate, String expirationDate) {
        Lease lease = retrieveLeaseForEdit();

        BillableItem billableItem = findBillableItem(billableItemId, lease);
        assert (billableItem != null);

        billableItem.effectiveDate().setValue(FinancialTestsUtils.getDate(effectiveDate));
        billableItem.expirationDate().setValue(FinancialTestsUtils.getDate(expirationDate));

        ServerSideFactory.create(LeaseFacade.class).persist(lease.currentTerm());
        Persistence.service().commit();
    }

    private BillableItem addBillableItem(Feature.Type featureType) {
        Lease lease = retrieveLease();
        return addBillableItem(featureType, lease.currentTerm().termFrom().getValue(), lease.currentTerm().termTo().getValue());
    }

    private BillableItem addBillableItem(Feature.Type featureType, String effectiveDate, String expirationDate) {
        return addBillableItem(featureType, FinancialTestsUtils.getDate(effectiveDate), FinancialTestsUtils.getDate(expirationDate));
    }

    private BillableItem addBillableItem(Feature.Type featureType, LogicalDate effectiveDate, LogicalDate expirationDate) {
        Lease lease = retrieveLeaseForEdit();

        // correct agreed price for existing leases:
        BigDecimal agreedPrice = null;
        if (lease.status().getValue() == Lease.Status.ExistingLease) {
            switch (featureType) {
            case parking:
                agreedPrice = new BigDecimal("80.00");
                break;
            case locker:
                agreedPrice = new BigDecimal("60.00");
                break;
            case addOn:
                agreedPrice = new BigDecimal("40.00");
                break;
            case pet:
                agreedPrice = new BigDecimal("20.00");
                break;
            case booking:
                agreedPrice = new BigDecimal("100.00");
                break;
            default:
                break;
            }
        }

        ProductItem serviceItem = lease.currentTerm().version().leaseProducts().serviceItem().item();
        Persistence.service().retrieve(serviceItem.product());
        Service.ServiceV service = serviceItem.product().cast();
        Persistence.service().retrieve(service.features());
        for (Feature feature : service.features()) {
            if (featureType.equals(feature.featureType().getValue()) && feature.version().items().size() != 0) {
                LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
                BillableItem billableItem = leaseFacade.createBillableItem(lease, feature.version().items().get(0), lease.unit().building());

                billableItem.effectiveDate().setValue(effectiveDate);
                billableItem.expirationDate().setValue(expirationDate);

                lease.currentTerm().version().leaseProducts().featureItems().add(billableItem);

                if (agreedPrice != null) {
                    billableItem.agreedPrice().setValue(agreedPrice);
                }

                leaseFacade.persist(lease.currentTerm());
                return billableItem;
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
        Lease lease = retrieveLeaseForEdit();
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
                FinancialTestsUtils.getDate(effectiveDate), FinancialTestsUtils.getDate(expirationDate));
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
        return addBillableItemAdjustment(billableItemId, value, adjustmentType, FinancialTestsUtils.getDate(effectiveDate),
                FinancialTestsUtils.getDate(expirationDate));
    }

    protected BillableItemAdjustment addBillableItemAdjustment(String billableItemId, String value, BillableItemAdjustment.Type adjustmentType,
            LogicalDate effectiveDate, LogicalDate expirationDate) {

        Lease lease = retrieveLeaseForEdit();
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
        Lease lease = retrieveLeaseForEdit();

        BillableItemAdjustment billableItemAdjustment = findBillableItemAdjustment(billableItemAdjustmentId, lease);

        billableItemAdjustment.effectiveDate().setValue(FinancialTestsUtils.getDate(effectiveDate));
        billableItemAdjustment.expirationDate().setValue(FinancialTestsUtils.getDate(expirationDate));

        ServerSideFactory.create(LeaseFacade.class).finalize(lease.currentTerm());
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

    protected PaymentRecord receivePayment(String receivedDate, String amount, PaymentType type) {
        Lease lease = retrieveLease();

        // Just use the first tenant
        LeaseTermParticipant<?> leaseParticipant = lease.currentTerm().version().tenants().iterator().next();
        Persistence.service().retrieve(leaseParticipant);

        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        paymentRecord.createdDate().setValue(FinancialTestsUtils.getDate(receivedDate));
        paymentRecord.receivedDate().setValue(FinancialTestsUtils.getDate(receivedDate));
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

    protected void postPayment(PaymentRecord paymentRecord) {
        ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord);
        Persistence.service().commit();
    }

    protected PaymentRecord receiveAndPostPayment(String receivedDate, String amount) {
        return receiveAndPostPayment(receivedDate, amount, PaymentType.Cash);
    }

    protected PaymentRecord receiveAndPostPayment(String receivedDate, String amount, PaymentType type) {
        PaymentRecord paymentRecord = receivePayment(receivedDate, amount, type);
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

    protected void setBillingBatchProcess() {
        schedulePmcProcess(new BillingProcess(), new Schedule());
        schedulePmcProcess(new FutureBillingCycleInitializationProcess(), new Schedule());
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
        schedulePmcProcess(new LeaseRenewalProcess(), new Schedule());
    }
}

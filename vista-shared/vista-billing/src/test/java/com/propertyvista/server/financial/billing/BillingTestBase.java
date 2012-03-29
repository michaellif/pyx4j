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
package com.propertyvista.server.financial.billing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.essentials.server.xml.XMLEntitySchemaWriter;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Feature.Type;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.AdjustmentType;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.ExecutionType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;
import com.propertyvista.domain.tenant.lease.PaymentRecord;
import com.propertyvista.server.financial.ar.ARFacade;
import com.propertyvista.server.financial.billing.print.BillPrint;
import com.propertyvista.server.financial.preload.BuildingDataModel;
import com.propertyvista.server.financial.preload.LeaseAdjustmentReasonDataModel;
import com.propertyvista.server.financial.preload.LeaseDataModel;
import com.propertyvista.server.financial.preload.LocationsDataModel;
import com.propertyvista.server.financial.preload.ProductItemTypesDataModel;
import com.propertyvista.server.financial.preload.ProductTaxPolicyDataModel;
import com.propertyvista.server.financial.preload.TaxesDataModel;
import com.propertyvista.server.financial.preload.TenantDataModel;

abstract class BillingTestBase extends VistaDBTestBase {

    protected LeaseDataModel leaseDataModel;

    protected LeaseAdjustmentReasonDataModel leaseAdjustmentReasonDataModel;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Persistence.service().startBackgroundProcessTransaction();
        setSysDate("01-Jan-2000");
        if (false) {
            XMLEntitySchemaWriter.printSchema(new FileOutputStream(new File("target", "bill-model.xsd")), true, Bill.class);
            XMLEntitySchemaWriter.printSchema(new FileOutputStream(new File("target", "leas-model.xsd")), true, Lease.class);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        Persistence.service().commit();
        Persistence.service().endTransaction();
        super.tearDown();
    }

    protected void preloadData() {
        LocationsDataModel locationsDataModel = new LocationsDataModel();
        locationsDataModel.generate(true);

        TaxesDataModel taxesDataModel = new TaxesDataModel(locationsDataModel);
        taxesDataModel.generate(true);

        ProductItemTypesDataModel productItemTypesDataModel = new ProductItemTypesDataModel();
        productItemTypesDataModel.generate(true);

        leaseAdjustmentReasonDataModel = new LeaseAdjustmentReasonDataModel();
        leaseAdjustmentReasonDataModel.generate(true);

        BuildingDataModel buildingDataModel = new BuildingDataModel(productItemTypesDataModel);
        buildingDataModel.generate(true);

        ProductTaxPolicyDataModel productTaxPolicyDataModel = new ProductTaxPolicyDataModel(productItemTypesDataModel, taxesDataModel, buildingDataModel);
        productTaxPolicyDataModel.generate(true);

        TenantDataModel tenantDataModel = new TenantDataModel();
        tenantDataModel.generate(true);

        leaseDataModel = new LeaseDataModel(buildingDataModel, tenantDataModel);
        leaseDataModel.generate(true);

    }

    protected Bill runBilling(boolean confirm) {
        return runBilling(confirm, false);
    }

    protected Bill runBilling(boolean confirm, boolean printBill) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());
        ServerSideFactory.create(BillingFacade.class).runBilling(lease);

        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(lease);

        if (confirm) {
            ServerSideFactory.create(BillingFacade.class).confirmBill(bill);
        } else {
            ServerSideFactory.create(BillingFacade.class).rejectBill(bill);
        }

        Persistence.service().commit();

        if (printBill) {
            try {
                BillPrint.printBill(bill, new FileOutputStream(billFileName(bill)));
                DataDump.dump("bill", bill);
                DataDump.dump("lease", lease);
            } catch (FileNotFoundException e) {
                throw new Error(e);
            }
        }

        return bill;
    }

    protected static void setSysDate(String dateStr) {
        BillingLifecycle.setSysDate(BillingTestUtils.getDate(dateStr));

        if (dateStr == null) {
            Persistence.service().setTransactionSystemTime(new Date());
        } else {
            Persistence.service().setTransactionSystemTime(DateUtils.detectDateformat(dateStr));
        }
    }

    protected void setLeaseConditions(String leaseDateFrom, String leaseDateTo, Integer billingPeriodStartDate) {
        Lease lease = Persistence.retrieveDraft(Lease.class, leaseDataModel.getLeaseKey());

        lease.leaseFrom().setValue(BillingTestUtils.getDate(leaseDateFrom));
        lease.leaseTo().setValue(BillingTestUtils.getDate(leaseDateTo));

        lease.billingAccount().billingPeriodStartDate().setValue(billingPeriodStartDate);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(lease);
        Persistence.service().commit();

    }

    protected void setLeaseStatus(Lease.Status status) {
        Lease lease = Persistence.retrieveDraft(Lease.class, leaseDataModel.getLeaseKey());

        lease.version().status().setValue(status);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(lease);
        Persistence.service().commit();

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
        return addBillableItem(Type.pet);
    }

    protected BillableItem addBooking(String date) {
        return addBillableItem(Type.booking, date, date);
    }

    protected void changeBillableItem(String billableItemId, String effectiveDate, String expirationDate) {
        Lease lease = Persistence.retrieveDraft(Lease.class, leaseDataModel.getLeaseKey());

        BillableItem billableItem = findBillableItem(billableItemId, lease);

        billableItem.effectiveDate().setValue(BillingTestUtils.getDate(effectiveDate));
        billableItem.expirationDate().setValue(BillingTestUtils.getDate(expirationDate));

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(lease);
        Persistence.service().commit();
    }

    private BillableItem addBillableItem(Feature.Type featureType) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());
        return addBillableItem(featureType, lease.leaseFrom().getValue(), lease.leaseTo().getValue());
    }

    private BillableItem addBillableItem(Feature.Type featureType, String effectiveDate, String expirationDate) {
        return addBillableItem(featureType, BillingTestUtils.getDate(effectiveDate), BillingTestUtils.getDate(expirationDate));
    }

    private BillableItem addBillableItem(Feature.Type featureType, LogicalDate effectiveDate, LogicalDate expirationDate) {
        Lease lease = Persistence.retrieveDraft(Lease.class, leaseDataModel.getLeaseKey());

        ProductItem serviceItem = leaseDataModel.getServiceItem();
        Service.ServiceV service = serviceItem.product().cast();
        for (Feature feature : service.features()) {
            if (featureType.equals(feature.version().type().getValue()) && feature.version().items().size() != 0) {
                BillableItem billableItem = EntityFactory.create(BillableItem.class);
                billableItem.item().set(feature.version().items().get(0));
                billableItem.effectiveDate().setValue(effectiveDate);
                billableItem.expirationDate().setValue(expirationDate);
                lease.version().leaseProducts().featureItems().add(billableItem);

                lease.saveAction().setValue(SaveAction.saveAsFinal);
                Persistence.service().persist(lease);
                Persistence.service().commit();

                return billableItem;
            }
        }
        return null;
    }

    protected BillableItemAdjustment addServiceAdjustment(String value, AdjustmentType adjustmentType, ExecutionType termType) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());
        return addBillableItemAdjustment(lease.version().leaseProducts().serviceItem().uid().getValue(), value, adjustmentType, termType, lease.leaseFrom()
                .getValue(), lease.leaseTo().getValue());
    }

    protected BillableItemAdjustment addFeatureAdjustment(String billableItemId, String value, AdjustmentType adjustmentType, ExecutionType termType) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());
        return addBillableItemAdjustment(billableItemId, value, adjustmentType, termType, lease.leaseFrom().getValue(), lease.leaseTo().getValue());

    }

    protected BillableItemAdjustment addFeatureAdjustment(String billableItemId, String value, AdjustmentType adjustmentType, ExecutionType termType,
            String effectiveDate, String expirationDate) {
        return addBillableItemAdjustment(billableItemId, value, adjustmentType, termType, BillingTestUtils.getDate(effectiveDate),
                BillingTestUtils.getDate(expirationDate));
    }

    private BillableItemAdjustment addBillableItemAdjustment(String billableItemId, String value, AdjustmentType adjustmentType, ExecutionType executionType,
            LogicalDate effectiveDate, LogicalDate expirationDate) {

        Lease lease = Persistence.retrieveDraft(Lease.class, leaseDataModel.getLeaseKey());
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

        actualBillableItem.adjustments().add(adjustment);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(lease);
        Persistence.service().commit();

        return adjustment;
    }

    protected LeaseAdjustment addGoodWillAdjustment(String amount, String effectiveDate, boolean immediate) {
        return addLeaseAdjustment(amount, leaseAdjustmentReasonDataModel.getReason(LeaseAdjustmentReasonDataModel.Reason.goodWill),
                BillingTestUtils.getDate(effectiveDate), immediate);
    }

    private LeaseAdjustment addLeaseAdjustment(String amount, LeaseAdjustmentReason reason, LogicalDate effectiveDate, boolean immediate) {

        Lease lease = Persistence.retrieveDraft(Lease.class, leaseDataModel.getLeaseKey());

        LeaseAdjustment adjustment = EntityFactory.create(LeaseAdjustment.class);
        adjustment.effectiveDate().setValue(new LogicalDate(lease.leaseFrom().getValue()));
        adjustment.amount().setValue(new BigDecimal(amount));
        adjustment.executionType().setValue(immediate ? LeaseAdjustment.ExecutionType.immediate : LeaseAdjustment.ExecutionType.pending);
        if (adjustment.amount().getValue().compareTo(new BigDecimal("0")) > 0) {
            adjustment.actionType().setValue(LeaseAdjustment.ActionType.credit);
        } else if (adjustment.amount().getValue().compareTo(new BigDecimal("0")) < 0) {
            adjustment.actionType().setValue(LeaseAdjustment.ActionType.charge);
        }
        adjustment.effectiveDate().setValue(effectiveDate);
        adjustment.description().setValue(reason.name().getValue());
        adjustment.reason().setValue(reason.getValue());

        lease.billingAccount().adjustments().add(adjustment);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(lease);
        Persistence.service().commit();

        if (immediate) {
            ServerSideFactory.create(ARFacade.class).postImmediateAdjustment(adjustment);
        }
        return adjustment;
    }

    protected PaymentRecord receivePayment(String receivedDate, String amount) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());

        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        paymentRecord.receivedDate().setValue(BillingTestUtils.getDate(receivedDate));
        paymentRecord.amount().setValue(new BigDecimal(amount));
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);

        Persistence.service().retrieveMember(lease.billingAccount().payments());
        lease.billingAccount().payments().add(paymentRecord);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.service().persist(lease);
        Persistence.service().commit();

        return paymentRecord;
    }

    protected void postPayment(PaymentRecord paymentRecord) {
        ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord);
    }

    protected PaymentRecord receiveAndPostPayment(String receivedDate, String amount) {
        PaymentRecord paymentRecord = receivePayment(receivedDate, amount);
        postPayment(paymentRecord);
        return paymentRecord;
    }

    protected void rejectPayment(PaymentRecord paymentRecord) {
        ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord);
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

    protected static String billFileName(Bill bill) {
        String ext = ".pdf";
        File dir = new File("target", "reports-dump");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new Error("Can't create directory " + dir.getAbsolutePath());
            }
        }
        File file = new File(dir, "Bill-" + bill.getPrimaryKey().toString() + ext);
        if (file.exists()) {
            if (!file.delete()) {
                throw new Error("Can't delete file " + file.getAbsolutePath());
            }
        }
        return file.getAbsolutePath();
    }
}

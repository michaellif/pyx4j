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
package com.propertyvista.server.billing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportProcessor;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Payment;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Feature.Type;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.AdjustmentType;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.TermType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.billing.preload.BuildingDataModel;
import com.propertyvista.server.billing.preload.LeaseDataModel;
import com.propertyvista.server.billing.preload.LocationsDataModel;
import com.propertyvista.server.billing.preload.ProductItemTypesDataModel;
import com.propertyvista.server.billing.preload.ProductTaxPolicyDataModel;
import com.propertyvista.server.billing.preload.TaxesDataModel;
import com.propertyvista.server.billing.preload.TenantDataModel;
import com.propertyvista.server.billing.print.BillPrint;

abstract class BillingTestBase extends VistaDBTestBase {

    protected LeaseDataModel leaseDataModel;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Persistence.service().startBackgroundProcessTransaction();
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
        BillingFacade.runBilling(lease);

        Bill bill = BillingFacade.getLatestBill(lease.leaseFinancial().billingAccount());

        if (confirm) {
            BillingFacade.confirmBill(bill);
        } else {
            BillingFacade.rejectBill(bill);
        }

        Persistence.service().commit();

        Persistence.service().retrieve(bill.charges());
        Persistence.service().retrieve(bill.chargeAdjustments());
        Persistence.service().retrieve(bill.leaseAdjustments());

        DataDump.dump("bill", bill);
        DataDump.dump("lease", lease);

        if (printBill) {
            FileOutputStream pdf = null;
            try {
                pdf = new FileOutputStream(billFileName(bill));
                JasperReportProcessor.createReport(BillPrint.createModel(bill), JasperFileFormat.PDF, pdf);
                pdf.flush();
            } catch (FileNotFoundException e) {
                throw new Error(e);
            } catch (IOException e) {
                throw new Error(e);
            } finally {
                IOUtils.closeQuietly(pdf);
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
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());

        lease.leaseFrom().setValue(BillingTestUtils.getDate(leaseDateFrom));
        lease.leaseTo().setValue(BillingTestUtils.getDate(leaseDateTo));

        lease.leaseFinancial().billingPeriodStartDate().setValue(billingPeriodStartDate);

        Persistence.service().persist(lease);
        Persistence.service().commit();

    }

    protected void setLeaseStatus(Lease.Status status) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());
        lease.status().setValue(status);

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

    protected void changeBillableItem(BillableItem billableItem, String effectiveDate, String expirationDate) {
        billableItem.effectiveDate().setValue(BillingTestUtils.getDate(effectiveDate));
        billableItem.expirationDate().setValue(BillingTestUtils.getDate(expirationDate));
        Persistence.service().persist(billableItem);
    }

    private BillableItem addBillableItem(Feature.Type featureType) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());
        return addBillableItem(featureType, lease.leaseFrom().getValue(), lease.leaseTo().getValue());
    }

    private BillableItem addBillableItem(Feature.Type featureType, String effectiveDate, String expirationDate) {
        return addBillableItem(featureType, BillingTestUtils.getDate(effectiveDate), BillingTestUtils.getDate(expirationDate));
    }

    private BillableItem addBillableItem(Feature.Type featureType, LogicalDate effectiveDate, LogicalDate expirationDate) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());

        ProductItem serviceItem = leaseDataModel.getServiceItem();
        Service service = serviceItem.product().cast();
        for (Feature feature : service.features()) {
            if (featureType.equals(feature.type().getValue()) && feature.items().size() != 0) {
                BillableItem billableItem = EntityFactory.create(BillableItem.class);
                billableItem.item().set(feature.items().get(0));
                billableItem.effectiveDate().setValue(effectiveDate);
                billableItem.expirationDate().setValue(expirationDate);
                lease.leaseProducts().featureItems().add(billableItem);

                Persistence.service().persist(lease);
                Persistence.service().commit();

                return billableItem;
            }
        }
        return null;
    }

    protected BillableItemAdjustment addBillableItemAdjustment(BillableItem billableItem, String value, AdjustmentType adjustmentType, TermType termType) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());
        return addBillableItemAdjustment(billableItem, value, adjustmentType, termType, lease.leaseFrom().getValue(), lease.leaseTo().getValue());

    }

    protected BillableItemAdjustment addBillableItemAdjustment(BillableItem billableItem, String value, AdjustmentType adjustmentType, TermType termType,
            String effectiveDate, String expirationDate) {
        return addBillableItemAdjustment(billableItem, value, adjustmentType, termType, BillingTestUtils.getDate(effectiveDate),
                BillingTestUtils.getDate(expirationDate));
    }

    private BillableItemAdjustment addBillableItemAdjustment(BillableItem billableItem, String value, AdjustmentType adjustmentType, TermType termType,
            LogicalDate effectiveDate, LogicalDate expirationDate) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());

        BillableItemAdjustment adjustment = EntityFactory.create(BillableItemAdjustment.class);
        adjustment.effectiveDate().setValue(new LogicalDate(lease.leaseFrom().getValue()));
        if (value == null) {
            adjustment.value().setValue(null);
        } else {
            adjustment.value().setValue(new BigDecimal(value));
        }
        adjustment.adjustmentType().setValue(adjustmentType);
        adjustment.termType().setValue(termType);
        adjustment.effectiveDate().setValue(effectiveDate);
        adjustment.expirationDate().setValue(expirationDate);
        billableItem.adjustments().add(adjustment);

        Persistence.service().persist(billableItem);
        Persistence.service().commit();

        return adjustment;
    }

    protected Payment receivePayment(String receivedDate, String amount) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLeaseKey());

        Payment payment = EntityFactory.create(Payment.class);
        payment.receivedDate().setValue(BillingTestUtils.getDate(receivedDate));
        payment.amount().setValue(new BigDecimal(amount));
        payment.paymentStatus().setValue(Payment.PaymentStatus.Posted);
        payment.billingStatus().setValue(Payment.BillingStatus.New);

        lease.leaseFinancial().billingAccount().payments().add(payment);

        Persistence.service().persist(lease.leaseFinancial().billingAccount());
        Persistence.service().commit();

        return payment;
    }

    protected void rejectPayment(Payment payment) {
        payment.paymentStatus().setValue(Payment.PaymentStatus.Rejected);
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

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

import java.math.BigDecimal;

import org.junit.Ignore;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.billing.Bill;
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

@Ignore
public class BillingTestBase extends VistaDBTestBase {

    protected LeaseDataModel leaseDataModel;

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
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLease().getPrimaryKey());
        BillingFacade.runBilling(lease);

        Bill bill = BillingFacade.getLatestBill(lease.leaseFinancial().billingAccount());

        if (confirm) {
            BillingFacade.confirmBill(bill);
        } else {
            BillingFacade.rejectBill(bill);
        }

        Persistence.service().retrieve(bill.charges());
        Persistence.service().retrieve(bill.chargeAdjustments());
        Persistence.service().retrieve(bill.leaseAdjustments());

        DataDump.dump("bill", bill);
        DataDump.dump("lease", lease);

        return bill;
    }

    protected static void setSysDate(String date) {
        BillingLifecycle.setSysDate(BillingTestUtils.getDate(date));
    }

    protected void setLeaseConditions(String leaseDateFrom, String leaseDateTo, Integer billingPeriodStartDate) {
        Lease lease = leaseDataModel.getLease();

        lease.leaseFrom().setValue(BillingTestUtils.getDate(leaseDateFrom));
        lease.leaseTo().setValue(BillingTestUtils.getDate(leaseDateTo));

        lease.leaseFinancial().billingPeriodStartDate().setValue(billingPeriodStartDate);

        Persistence.service().persist(lease);

    }

    protected void setLeaseStatus(Lease.Status status) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLease().getPrimaryKey());
        lease.status().setValue(status);
        Persistence.service().persist(lease);
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

    private BillableItem addBillableItem(Feature.Type featureType) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLease().getPrimaryKey());
        return addBillableItem(featureType, lease.leaseFrom().getValue(), lease.leaseTo().getValue());
    }

    private BillableItem addBillableItem(Feature.Type featureType, String effectiveDate, String expirationDate) {
        return addBillableItem(featureType, BillingTestUtils.getDate(effectiveDate), BillingTestUtils.getDate(expirationDate));
    }

    private BillableItem addBillableItem(Feature.Type featureType, LogicalDate effectiveDate, LogicalDate expirationDate) {
        Lease lease = leaseDataModel.getLease();

        ProductItem serviceItem = leaseDataModel.getServiceItem();
        Service service = serviceItem.product().cast();
        for (Feature feature : service.features()) {
            if (featureType.equals(feature.type().getValue()) && feature.items().size() != 0) {
                BillableItem billableItem = EntityFactory.create(BillableItem.class);
                billableItem.item().set(feature.items().get(0));
                billableItem.effectiveDate().setValue(effectiveDate);
                billableItem.expirationDate().setValue(expirationDate);
                lease.serviceAgreement().featureItems().add(billableItem);
                Persistence.service().persist(lease);
                return billableItem;
            }
        }
        return null;
    }

    protected BillableItemAdjustment addBillableItemAdjustment(BillableItem billableItem, String value, AdjustmentType adjustmentType, TermType termType) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLease().getPrimaryKey());
        return addBillableItemAdjustment(billableItem, value, adjustmentType, termType, lease.leaseFrom().getValue(), lease.leaseTo().getValue());

    }

    protected BillableItemAdjustment addBillableItemAdjustment(BillableItem billableItem, String value, AdjustmentType adjustmentType, TermType termType,
            String effectiveDate, String expirationDate) {
        return addBillableItemAdjustment(billableItem, value, adjustmentType, termType, BillingTestUtils.getDate(effectiveDate),
                BillingTestUtils.getDate(expirationDate));
    }

    private BillableItemAdjustment addBillableItemAdjustment(BillableItem billableItem, String value, AdjustmentType adjustmentType, TermType termType,
            LogicalDate effectiveDate, LogicalDate expirationDate) {
        Lease lease = leaseDataModel.getLease();

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

        return adjustment;
    }

}

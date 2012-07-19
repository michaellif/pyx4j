/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease.common;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.crm.rpc.services.lease.common.LeaseEditorCrudServiceBase;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

public abstract class LeaseEditorCrudServiceBaseImpl<DTO extends LeaseDTO> extends LeaseCrudServiceBaseImpl<DTO> implements LeaseEditorCrudServiceBase<DTO> {

    protected LeaseEditorCrudServiceBaseImpl(Class<DTO> dtoClass) {
        super(dtoClass);
    }

    @Override
    protected void enhanceRetrieved(Lease in, DTO dto) {
        super.enhanceRetrieved(in, dto);

        if (!dto.unit().isNull()) {
            // fill runtime editor data:
            fillServiceEligibilityData(dto);
            fillserviceItems(dto);
        }
    }

    @Override
    protected void create(Lease dbo, DTO dto) {
        updateAdjustments(dbo);
        ServerSideFactory.create(LeaseFacade.class).init(dbo);
        ServerSideFactory.create(LeaseFacade.class).persist(dbo);
    }

    @Override
    protected void save(Lease dbo, DTO in) {
        updateAdjustments(dbo);
        ServerSideFactory.create(LeaseFacade.class).persist(dbo);
    }

    private void updateAdjustments(Lease lease) {
        // ServiceItem Adjustments:
        updateAdjustments(lease.version().leaseProducts().serviceItem());

        // BillableItem Adjustments:
        for (BillableItem ci : lease.version().leaseProducts().featureItems()) {
            updateAdjustments(ci);
        }
    }

    private void updateAdjustments(BillableItem item) {
        for (BillableItemAdjustment adj : item.adjustments()) {
            // set creator:
            if (adj.createdWhen().isNull()) {
                adj.createdBy().set(CrmAppContext.getCurrentUserEmployee());
            }
        }
    }

    @Override
    public void setSelectedUnit(AsyncCallback<DTO> callback, AptUnit unitId, DTO currentValue) {
        ServerSideFactory.create(LeaseFacade.class).setUnit(currentValue, unitId);
        loadDetachedProducts(currentValue);
        fillServiceEligibilityData(currentValue);
        fillserviceItems(currentValue);
        callback.onSuccess(currentValue);
    }

    @Override
    public void setSelectedService(AsyncCallback<DTO> callback, ProductItem serviceId, DTO currentValue) {
        ServerSideFactory.create(LeaseFacade.class).setService(currentValue, serviceId);
        loadDetachedProducts(currentValue);
        fillServiceEligibilityData(currentValue);
        callback.onSuccess(currentValue);
    }

    @Override
    public void createBillableItem(AsyncCallback<BillableItem> callback, ProductItem productItemId, DTO currentValue) {
        callback.onSuccess(ServerSideFactory.create(LeaseFacade.class).createBillableItem(productItemId));
    }

    @Override
    public void createDeposit(AsyncCallback<Deposit> callback, DepositType depositType, BillableItem item, DTO currentValue) {
        assert !currentValue.unit().isNull();
        callback.onSuccess(ServerSideFactory.create(DepositFacade.class).createDeposit(depositType, item, currentValue.unit().building()));
    }

    // Internals:

    private boolean fillServiceEligibilityData(DTO currentValue) {
        currentValue.selectedFeatureItems().clear();
        currentValue.selectedConcessions().clear();

        assert !currentValue.unit().isNull();
        Building building = currentValue.unit().building();
        if (building == null || building.isNull()) {
            return false;
        }

        ProductCatalog catalog = building.productCatalog();
        ProductItem serviceItem = currentValue.version().leaseProducts().serviceItem().item();
        if (catalog == null || serviceItem == null) {
            return false;
        }

        // find the service by Service item:
        Service.ServiceV selectedService = null;
        Persistence.service().retrieve(serviceItem.product());
        if (serviceItem.product().getInstanceValueClass().equals(Service.ServiceV.class)) {
            selectedService = serviceItem.product().cast();
        }

        // fill related:
        if (selectedService != null) {
            // features:
            Persistence.service().retrieve(selectedService.features());
            for (Feature feature : selectedService.features()) {
                Persistence.service().retrieve(feature.version().items());
                for (ProductItem item : feature.version().items()) {
                    if (!item.isDefault().isBooleanTrue()) {
                        Persistence.service().retrieve(item.product());
                        currentValue.selectedFeatureItems().add(item);
                    }
                }
            }

            // concessions:
            Persistence.service().retrieve(selectedService.concessions());
            currentValue.selectedConcessions().addAll(selectedService.concessions());
        }

        return (selectedService != null);
    }

    private void fillserviceItems(DTO currentValue) {
        currentValue.selectedServiceItems().clear();

        Persistence.service().retrieve(currentValue.unit().building());
        EntityQueryCriteria<Service> criteria = new EntityQueryCriteria<Service>(Service.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().catalog(), currentValue.unit().building().productCatalog()));
        criteria.add(PropertyCriterion.eq(criteria.proto().version().type(), currentValue.type()));
        servicesLoop: for (Service service : Persistence.service().query(criteria)) {
            EntityQueryCriteria<ProductItem> serviceCriteria = EntityQueryCriteria.create(ProductItem.class);
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().type(), ServiceItemType.class));
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().product(), service.version()));
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().element(), currentValue.unit()));
            serviceCriteria
                    .add(PropertyCriterion.ne(serviceCriteria.proto().id(), currentValue.version().leaseProducts().serviceItem().item().getPrimaryKey()));
            currentValue.selectedServiceItems().addAll(Persistence.service().query(serviceCriteria));
            if (!currentValue.selectedServiceItems().isEmpty()) {
                break servicesLoop;
            }
        }
    }
}
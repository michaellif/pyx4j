/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-01
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease.common;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractVersionedCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.tenant.LeaseFacade2;
import com.propertyvista.crm.rpc.services.lease.common.LeaseTermCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Guarantor2;
import com.propertyvista.domain.tenant.Tenant2;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseTermDTO;

public class LeaseTermCrudServiceImpl extends AbstractVersionedCrudServiceDtoImpl<LeaseTerm, LeaseTermDTO> implements LeaseTermCrudService {

    public LeaseTermCrudServiceImpl() {
        super(LeaseTerm.class, LeaseTermDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void create(LeaseTerm dbo, LeaseTermDTO dto) {
        save(dbo, dto);
    }

    @Override
    protected void save(LeaseTerm dbo, LeaseTermDTO in) {
        updateAdjustments(dbo);
        ServerSideFactory.create(LeaseFacade2.class).persist(dbo);
//        // TODO: call this persist if lease has been changed only!
//        ServerSideFactory.create(LeaseFacade2.class).persist(dbo.lease());
    }

    @Override
    protected void persist(LeaseTerm dbo, LeaseTermDTO in) {
        throw new Error("Facade should be used");
    }

    @Override
    protected void saveAsFinal(LeaseTerm entity) {
        ServerSideFactory.create(LeaseFacade2.class).finalize(entity);
    }

    @Override
    protected void enhanceRetrieved(LeaseTerm in, LeaseTermDTO dto) {
        super.enhanceRetrieved(in, dto);

        Persistence.service().retrieve(dto.version().leaseProducts().serviceItem().item().product());

        for (BillableItem item : dto.version().leaseProducts().featureItems()) {
            Persistence.service().retrieve(item.item().product());
        }

        Persistence.service().retrieve(dto.version().tenants());
        for (Tenant2 item : dto.version().tenants()) {
            Persistence.service().retrieve(item.screening());
        }

        Persistence.service().retrieve(dto.version().guarantors());
        for (Guarantor2 item : dto.version().guarantors()) {
            Persistence.service().retrieve(item.screening());
        }

        Persistence.service().retrieve(dto.lease());
        if (!dto.lease().unit().isNull()) {
            // fill runtime editor data:
            fillServiceEligibilityData(dto);
            fillserviceItems(dto);
        }
    }

    @Override
    public void setSelectedUnit(AsyncCallback<LeaseTermDTO> callback, AptUnit unitId, LeaseTermDTO currentValue) {
        ServerSideFactory.create(LeaseFacade2.class).setUnit(currentValue, unitId);
        loadDetachedProducts(currentValue);
        fillServiceEligibilityData(currentValue);
        fillserviceItems(currentValue);
        callback.onSuccess(currentValue);
    }

    @Override
    public void setSelectedService(AsyncCallback<LeaseTermDTO> callback, ProductItem serviceId, LeaseTermDTO currentValue) {
        ServerSideFactory.create(LeaseFacade2.class).setService(currentValue, serviceId);
        loadDetachedProducts(currentValue);
        fillServiceEligibilityData(currentValue);
        callback.onSuccess(currentValue);
    }

    @Override
    public void createBillableItem(AsyncCallback<BillableItem> callback, ProductItem productItemId, LeaseTermDTO currentValue) {
        callback.onSuccess(ServerSideFactory.create(LeaseFacade2.class).createBillableItem(productItemId, currentValue.lease().unit().building()));
    }

    @Override
    public void createDeposit(AsyncCallback<Deposit> callback, DepositType depositType, BillableItem item, LeaseTermDTO currentValue) {
        assert !currentValue.lease().unit().isNull();
        callback.onSuccess(ServerSideFactory.create(DepositFacade.class).createDeposit(depositType, item, currentValue.lease().unit().building()));
    }

    // Internals:
    private void loadDetachedProducts(LeaseTermDTO dto) {
        Persistence.service().retrieve(dto.version().leaseProducts().serviceItem().item().product());

        for (BillableItem item : dto.version().leaseProducts().featureItems()) {
            Persistence.service().retrieve(item.item().product());
        }
    }

    private boolean fillServiceEligibilityData(LeaseTermDTO currentValue) {
        currentValue.selectedFeatureItems().clear();
        currentValue.selectedConcessions().clear();

        assert !currentValue.lease().unit().isNull();
        Building building = currentValue.lease().unit().building();
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

    private void fillserviceItems(LeaseTermDTO currentValue) {
        currentValue.selectedServiceItems().clear();

        Persistence.service().retrieve(currentValue.lease().unit().building());
        EntityQueryCriteria<Service> criteria = new EntityQueryCriteria<Service>(Service.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().catalog(), currentValue.lease().unit().building().productCatalog()));
        criteria.add(PropertyCriterion.eq(criteria.proto().version().type(), currentValue.type()));
        servicesLoop: for (Service service : Persistence.service().query(criteria)) {
            EntityQueryCriteria<ProductItem> serviceCriteria = EntityQueryCriteria.create(ProductItem.class);
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().type(), ServiceItemType.class));
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().product(), service.version()));
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().element(), currentValue.lease().unit()));
            serviceCriteria
                    .add(PropertyCriterion.ne(serviceCriteria.proto().id(), currentValue.version().leaseProducts().serviceItem().item().getPrimaryKey()));
            currentValue.selectedServiceItems().addAll(Persistence.service().query(serviceCriteria));
            if (!currentValue.selectedServiceItems().isEmpty()) {
                break servicesLoop;
            }
        }
    }

    private void updateAdjustments(LeaseTerm leaseTerm) {
        // ServiceItem Adjustments:
        updateAdjustments(leaseTerm.version().leaseProducts().serviceItem());

        // BillableItem Adjustments:
        for (BillableItem ci : leaseTerm.version().leaseProducts().featureItems()) {
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
}

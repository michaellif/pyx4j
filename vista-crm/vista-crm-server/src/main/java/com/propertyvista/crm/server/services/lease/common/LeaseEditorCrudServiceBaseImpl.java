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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractVersionedCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.crm.rpc.services.lease.common.LeaseEditorCrudServiceBase;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.ExecutionType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;

public abstract class LeaseEditorCrudServiceBaseImpl<DTO extends LeaseDTO> extends AbstractVersionedCrudServiceDtoImpl<Lease, DTO> implements
        LeaseEditorCrudServiceBase<DTO> {

    private final boolean isApplication;

    protected LeaseEditorCrudServiceBaseImpl(Class<DTO> dtoClass) {
        super(Lease.class, dtoClass);
        isApplication = dtoClass.equals(LeaseApplicationDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Lease in, DTO dto) {
        enhanceRetrievedCommon(in, dto);

        // load detached entities:
        Persistence.service().retrieve(dto.billingAccount().adjustments());
//      Persistence.service().retrieve(dto.documents());

        if (!dto.unit().isNull()) {
            // fill selected building by unit:
            dto.selectedBuilding().set(dto.unit().belongsTo());
            fillServiceEligibilityData(dto);
            fillserviceItems(dto);
        }

        // calculate price adjustments:
        PriceCalculationHelpers.calculateChargeItemAdjustments(dto.version().leaseProducts().serviceItem());
        for (BillableItem item : dto.version().leaseProducts().featureItems()) {
            PriceCalculationHelpers.calculateChargeItemAdjustments(item);

            // Need this for navigation
            Persistence.service().retrieve(item.item().product());
        }

        for (Tenant item : dto.version().tenants()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers);
        }

        for (Guarantor item : dto.version().guarantors()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers);
        }

        // Need this for navigation
        Persistence.service().retrieve(dto.version().leaseProducts().serviceItem().item().product());

        // create bill preview:
        dto.billingPreview().set(BillingUtils.createBillPreviewDto(ServerSideFactory.create(BillingFacade.class).runBillingPreview(in)));
    }

    @Override
    protected void enhanceListRetrieved(Lease in, DTO dto) {
        enhanceRetrievedCommon(in, dto);
    }

    private void enhanceRetrievedCommon(Lease in, DTO dto) {
        // load detached entities:
        Persistence.service().retrieve(dto.unit());
        Persistence.service().retrieve(dto.unit().belongsTo());

        Persistence.service().retrieve(dto.version().tenants());
        Persistence.service().retrieve(dto.version().guarantors());

        Persistence.service().retrieve(dto.billingAccount());
    }

    @Override
    protected void persist(Lease dbo, DTO in) {
        throw new Error("Facade should be used");
    }

    @Override
    protected void create(Lease entity, DTO dto) {
        updateAdjustments(entity);
        ServerSideFactory.create(LeaseFacade.class).initLease(entity);
    }

    @Override
    protected void save(Lease dbo, DTO in) {
        updateAdjustments(dbo);
        ServerSideFactory.create(LeaseFacade.class).persistLease(dbo);
    }

    @Override
    protected void saveAsFinal(Lease entity) {
        ServerSideFactory.create(LeaseFacade.class).saveAsFinal(entity);
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
            // correct adjustment expiration date:
            if (ExecutionType.oneTime == adj.executionType().getValue()) {
                adj.expirationDate().setValue(null);
            }
        }
    }

    @Override
    public void setSelectedUnit(AsyncCallback<DTO> callback, AptUnit unitId, DTO dto) {
        ServerSideFactory.create(LeaseFacade.class).setUnit(dto, unitId);
        dto.selectedBuilding().set(dto.unit().belongsTo());
        callback.onSuccess(dto);
    }

    @Override
    public void setSelectedService(AsyncCallback<DTO> callback, ProductItem serviceId, DTO dto) {
        ServerSideFactory.create(LeaseFacade.class).setService(dto, serviceId);
        fillServiceEligibilityData(dto);
        fillserviceItems(dto);
        callback.onSuccess(dto);
    }

    @Override
    public void calculateChargeItemAdjustments(AsyncCallback<BigDecimal> callback, BillableItem item) {
        callback.onSuccess(PriceCalculationHelpers.calculateChargeItemAdjustments(item));
    }

    // Internals:

    private boolean fillServiceEligibilityData(DTO currentValue) {

        currentValue.selectedFeatureItems().clear();
        currentValue.selectedUtilityItems().clear();
        currentValue.selectedConcessions().clear();

        Building building = currentValue.selectedBuilding();
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

        // fill related features/utilities and concession:
        if (selectedService != null) {
            List<FeatureItemType> utilitiesToExclude = new ArrayList<FeatureItemType>(catalog.includedUtilities().size() + catalog.externalUtilities().size());
            utilitiesToExclude.addAll(catalog.includedUtilities());
            utilitiesToExclude.addAll(catalog.externalUtilities());

            // fill features:
            Persistence.service().retrieve(selectedService.features());
            for (Feature feature : selectedService.features()) {
                Persistence.service().retrieve(feature.version().items());
                for (ProductItem item : feature.version().items()) {
                    switch (feature.version().type().getValue()) {
                    case addOn:
                    case utility:
                        // filter out utilities included in price for selected building:
                        if (!utilitiesToExclude.contains(item.type())) {
                            currentValue.selectedUtilityItems().add(item);
                        }
                        break;
                    default:
                        currentValue.selectedFeatureItems().add(item);
                    }
                }
            }

            // fill concessions:
            Persistence.service().retrieve(selectedService.concessions());
            currentValue.selectedConcessions().addAll(selectedService.concessions());
        }

        return (selectedService != null);
    }

    private void fillserviceItems(DTO currentValue) {
        currentValue.selectedServiceItems().clear();

        Persistence.service().retrieve(currentValue.unit().belongsTo());
        EntityQueryCriteria<Service> criteria = new EntityQueryCriteria<Service>(Service.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().catalog(), currentValue.unit().belongsTo().productCatalog()));
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
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

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractVersionedCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.crm.rpc.services.lease.common.LeaseTermCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.financial.offering.Feature;
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
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.server.common.util.LeaseParticipantUtils;

public class LeaseTermCrudServiceImpl extends AbstractVersionedCrudServiceDtoImpl<LeaseTerm, LeaseTermDTO> implements LeaseTermCrudService {

    private RetrieveTraget retrieveTraget;

    public LeaseTermCrudServiceImpl() {
        super(LeaseTerm.class, LeaseTermDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    public void retrieve(AsyncCallback<LeaseTermDTO> callback, Key entityId, RetrieveTraget retrieveTraget) {
        this.retrieveTraget = retrieveTraget;
        super.retrieve(callback, entityId, retrieveTraget);
    }

    @Override
    protected void create(LeaseTerm dbo, LeaseTermDTO dto) {
        updateAdjustments(dbo);

        // check for newly created parent (lease/application):
        if (!dto.newParentLease().isNull()) {
            dbo.lease().set(dto.newParentLease());
            dbo.lease().currentTerm().set(dbo);

            ServerSideFactory.create(LeaseFacade.class).init(dto.newParentLease());
            ServerSideFactory.create(LeaseFacade.class).persist(dbo.lease());
        } else {
            ServerSideFactory.create(LeaseFacade.class).persist(dbo);
        }
    }

    @Override
    protected void save(LeaseTerm dbo, LeaseTermDTO in) {
        updateAdjustments(dbo);

        if (dbo.lease().equals(dbo.lease().currentTerm())) {
            ServerSideFactory.create(LeaseFacade.class).persist(dbo.lease());
        } else {
            ServerSideFactory.create(LeaseFacade.class).persist(dbo);
        }
    }

    @Override
    protected void persist(LeaseTerm dbo, LeaseTermDTO in) {
        throw new Error("Facade should be used");
    }

    @Override
    protected void saveAsFinal(LeaseTerm entity) {
        ServerSideFactory.create(LeaseFacade.class).finalize(entity);
    }

    @Override
    protected void enhanceRetrieved(LeaseTerm in, LeaseTermDTO dto) {
        super.enhanceRetrieved(in, dto);

        if (in.getPrimaryKey() != null) {
            Persistence.service().retrieve(dto.version().tenants());
        }
        for (Tenant item : dto.version().tenants()) {
            LeaseParticipantUtils.retrieveLeaseTermEffectiveScreening(item, AttachLevel.ToStringMembers);
        }

        if (in.getPrimaryKey() != null) {
            Persistence.service().retrieve(dto.version().guarantors());
        }
        for (Guarantor item : dto.version().guarantors()) {
            LeaseParticipantUtils.retrieveLeaseTermEffectiveScreening(item, AttachLevel.ToStringMembers);
        }

        loadDetachedProducts(dto);

        Persistence.service().retrieve(dto.lease());
        if (!dto.lease().unit().isNull()) {
            if (dto.lease().unit().building().isValueDetached()) {
                Persistence.service().retrieve(dto.lease().unit().building());
            }

            if (retrieveTraget == RetrieveTraget.Edit) {
                // fill runtime editor data:
                fillServiceEligibilityData(dto);
                fillserviceItems(dto);
            }
        }
    }

    @Override
    public void setSelectedUnit(AsyncCallback<LeaseTermDTO> callback, AptUnit unitId, LeaseTermDTO currentValue) {
        LeaseTerm term = currentValue; // works for newly created lease/application, but:
        if (currentValue.lease().currentTerm().getInstanceValueClass().equals(LeaseTerm.class)) {
            term = createDBO(currentValue);
        }
        term = ServerSideFactory.create(LeaseFacade.class).setUnit(term, unitId);
        currentValue = createDTO(term);
        loadDetachedProducts(currentValue);
        fillServiceEligibilityData(currentValue);
        fillserviceItems(currentValue);
        callback.onSuccess(currentValue);
    }

    @Override
    public void setSelectedService(AsyncCallback<LeaseTermDTO> callback, ProductItem serviceId, LeaseTermDTO currentValue) {
        LeaseTerm term = currentValue; // works for newly created lease/application, but:
        if (currentValue.lease().currentTerm().getInstanceValueClass().equals(LeaseTerm.class)) {
            term = createDBO(currentValue);
        }
        term = ServerSideFactory.create(LeaseFacade.class).setService(term, serviceId);
        currentValue = createDTO(term);
        loadDetachedProducts(currentValue);
        fillServiceEligibilityData(currentValue);
        callback.onSuccess(currentValue);
    }

    @Override
    public void createBillableItem(AsyncCallback<BillableItem> callback, ProductItem productItemId, LeaseTermDTO currentValue) {
        callback.onSuccess(ServerSideFactory.create(LeaseFacade.class).createBillableItem(productItemId, currentValue.lease().unit().building()));
    }

    @Override
    public void createDeposit(AsyncCallback<Deposit> callback, DepositType depositType, BillableItem item, LeaseTermDTO currentValue) {
        assert !currentValue.lease().unit().isNull();
        callback.onSuccess(ServerSideFactory.create(DepositFacade.class).createDeposit(depositType, item, currentValue.lease().unit().building()));
    }

    @Override
    public void acceptOffer(AsyncCallback<VoidSerializable> callback, Key entityId) {
        LeaseTerm offer = Persistence.secureRetrieve(LeaseTerm.class, entityId);
        ServerSideFactory.create(LeaseFacade.class).acceptOffer(offer.lease(), offer);
        Persistence.service().commit();
        callback.onSuccess(null);
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

        if (currentValue.lease().unit().building().isValueDetached()) {
            Persistence.service().retrieve(currentValue.lease().unit().building());
        }

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
                if (ServerSideFactory.create(LeaseFacade.class).isProductAvailable(currentValue.lease(), feature)) {
                    Persistence.service().retrieve(feature.version().items());
                    for (ProductItem item : feature.version().items()) {
                        if (!item.isDefault().isBooleanTrue()) {
                            Persistence.service().retrieve(item.product());
                            currentValue.selectedFeatureItems().add(item);
                        }
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

        if (currentValue.lease().unit().building().isValueDetached()) {
            Persistence.service().retrieve(currentValue.lease().unit().building());
        }

        EntityQueryCriteria<Service> criteria = new EntityQueryCriteria<Service>(Service.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().catalog(), currentValue.lease().unit().building().productCatalog()));
        criteria.add(PropertyCriterion.eq(criteria.proto().version().serviceType(), currentValue.lease().type()));
        for (Service service : Persistence.service().query(criteria)) {
            if (ServerSideFactory.create(LeaseFacade.class).isProductAvailable(currentValue.lease(), service)) {
                EntityQueryCriteria<ProductItem> serviceCriteria = EntityQueryCriteria.create(ProductItem.class);
                serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().type(), ServiceItemType.class));
                serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().product(), service.version()));
                serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().element(), currentValue.lease().unit()));
                serviceCriteria.add(PropertyCriterion.ne(serviceCriteria.proto().id(), currentValue.version().leaseProducts().serviceItem().item()
                        .getPrimaryKey()));
                currentValue.selectedServiceItems().addAll(Persistence.service().query(serviceCriteria));
                if (!currentValue.selectedServiceItems().isEmpty()) {
                    break;
                }
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

    public void update(LeaseTerm dbo, LeaseTermDTO dto) {
        enhanceRetrieved(dbo, dto);
    }
}

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
package com.propertyvista.crm.server.services.lease;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractVersionedCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.crm.rpc.services.lease.LeaseCrudServiceBase;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.ExecutionType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;

public abstract class LeaseCrudServiceBaseImpl<DTO extends LeaseDTO> extends AbstractVersionedCrudServiceDtoImpl<Lease, DTO> implements
        LeaseCrudServiceBase<DTO> {

    protected LeaseCrudServiceBaseImpl(Class<DTO> dtoClass) {
        super(Lease.class, dtoClass);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Lease in, DTO dto) {
        Persistence.service().retrieve(dto.unit());
        Persistence.service().retrieve(dto.unit().belongsTo());

        // load detached entities:
        Persistence.service().retrieve(dto.version().tenants());
        Persistence.service().retrieve(dto.version().guarantors());
        Persistence.service().retrieve(dto.leaseApplication().onlineApplication());
//        Persistence.service().retrieve(dto.documents());
        if (!dto.unit().isNull()) {
            // fill selected building by unit:
            dto.selectedBuilding().set(dto.unit().belongsTo());
            syncBuildingProductCatalog(dto.selectedBuilding());
        }

        // calculate price adjustments:
        PriceCalculationHelpers.calculateChargeItemAdjustments(dto.version().leaseProducts().serviceItem());
        for (BillableItem item : dto.version().leaseProducts().featureItems()) {
            PriceCalculationHelpers.calculateChargeItemAdjustments(item);
        }
    }

    @Override
    protected void enhanceListRetrieved(Lease in, DTO dto) {
        Persistence.service().retrieve(dto.unit());
        Persistence.service().retrieve(dto.unit().belongsTo());

        // TODO this should be part of EntityQueryCriteria.finalizedOrDraft
        if (in.version().isNull()) {
            Lease draft = Persistence.service().retrieve(entityClass, in.getPrimaryKey().asDraftKey());
            dto.version().set(draft.version());
        }

        // place here versioned detached item retrieve: 
        Persistence.service().retrieve(dto.version().tenants());
        Persistence.service().retrieve(dto.version().guarantors());
    }

    @Override
    protected void persist(Lease dbo, DTO in) {
        throw new Error("Facade should be used");
    }

    @Override
    protected void create(Lease entity, DTO dto) {
        updateAdjustments(entity);
        ServerSideFactory.create(LeaseFacade.class).createLease(entity);
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

        // Lease Financial Adjustments:
        updateAdjustments(lease.billingAccount());
    }

    private void updateAdjustments(BillableItem item) {
        for (BillableItemAdjustment adj : item.adjustments()) {
            // set creator:
            if (adj.createdWhen().isNull()) {
                adj.createdBy().set(CrmAppContext.getCurrentUserEmployee());
            }
            // set adjustment expiration date:
            if (ExecutionType.oneTime == adj.executionType().getValue()) {
                adj.expirationDate().setValue(item.effectiveDate().getValue());
            }
        }
    }

    private void updateAdjustments(BillingAccount billingAccount) {
        for (LeaseAdjustment adj : billingAccount.adjustments()) {
            // set creator:
            if (adj.createdWhen().isNull()) {
                adj.createdBy().set(CrmAppContext.getCurrentUserEmployee());
            }
            // set adjustment expiration date:
            // (to the same date as effective - one time adjustment)
            adj.expirationDate().setValue(adj.effectiveDate().getValue());
        }
    }

    @Override
    public void setSelectededUnit(AsyncCallback<AptUnit> callback, Key unitId) {
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitId);
        Persistence.service().retrieve(unit.belongsTo());
        syncBuildingProductCatalog(unit.belongsTo());
        callback.onSuccess(unit);
    }

    private Building syncBuildingProductCatalog(Building building) {
        if (building == null || building.isNull()) {
            return null;
        }

        // load detached entities:
        Persistence.service().retrieve(building.productCatalog());
        Persistence.service().retrieve(building.productCatalog().services());

        // load detached service eligibility matrix data:
        for (Service item : building.productCatalog().services()) {
            Persistence.service().retrieve(item.version().items());
            Persistence.service().retrieve(item.version().features());
            for (Feature fi : item.version().features()) {
                Persistence.service().retrieve(fi.version().items());
            }
            Persistence.service().retrieve(item.version().concessions());
        }
//      
//  Currently not used here:        
//
//        EntityQueryCriteria<Feature> featureCriteria = EntityQueryCriteria.create(Feature.class);
//        featureCriteria.add(PropertyCriterion.eq(featureCriteria.proto().catalog(), building.serviceCatalog()));
//        List<Feature> features = Persistence.service().query(featureCriteria);
//        building.serviceCatalog().features().clear();
//        building.serviceCatalog().features().addAll(features);
//        for (Feature item : features) {
//            Persistence.service().retrieve(item.items());
//        }
//
//        EntityQueryCriteria<Concession> concessionCriteria = EntityQueryCriteria.create(Concession.class);
//        concessionCriteria.add(PropertyCriterion.eq(concessionCriteria.proto().catalog(), building.serviceCatalog()));
//        List<Concession> concessions = Persistence.service().query(concessionCriteria);
//        building.serviceCatalog().concessions().clear();
//        building.serviceCatalog().concessions().addAll(concessions);

        return building;
    }

    @Override
    public void calculateChargeItemAdjustments(AsyncCallback<BigDecimal> callback, BillableItem item) {
        callback.onSuccess(PriceCalculationHelpers.calculateChargeItemAdjustments(item));
    }
}
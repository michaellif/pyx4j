/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 */
package com.propertyvista.crm.server.services.unit;

import java.util.Date;

import com.pyx4j.commons.Pair;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.financial.productcatalog.ProductCatalogFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.crm.server.services.AbstractCrmCrudServiceImpl;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.AptUnitServicePriceDTO;
import com.propertyvista.server.common.util.AddressRetriever;

public class UnitCrudServiceImpl extends AbstractCrmCrudServiceImpl<AptUnit, AptUnitDTO> implements UnitCrudService {

    private static class Binder extends CrudEntityBinder<AptUnit, AptUnitDTO> {

        protected Binder() {
            super(AptUnit.class, AptUnitDTO.class);
        }

        @Override
        protected void bind() {
            bindCompleteObject();
            bind(toProto.buildingCode(), boProto.building().propertyCode());
        }

    }

    public UnitCrudServiceImpl() {
        super(new Binder());
    }

    @Override
    protected AptUnitDTO init(InitializationData initializationData) {
        UnitInitializationdata initData = (UnitInitializationdata) initializationData;
        AptUnitDTO newUnit = EntityFactory.create(AptUnitDTO.class);
        newUnit.building().set(Persistence.service().retrieve(Building.class, initData.parent().getPrimaryKey()));
        newUnit.buildingLegalAddress().set(AddressRetriever.getUnitLegalAddress(newUnit));
        return newUnit;
    }

    @Override
    protected void enhanceRetrieved(AptUnit bo, AptUnitDTO to, RetrieveTarget retrieveTarget) {
        // find corresponding lease:
        {
            to.lease().set(ServerSideFactory.create(OccupancyFacade.class).retriveCurrentLease(bo));
        }

        Persistence.service().retrieve(to.floorplan());
        Persistence.service().retrieve(to.building());
        to.buildingCode().setValue(to.building().propertyCode().getValue());

        to.buildingLegalAddress().set(AddressRetriever.getUnitLegalAddress(to));

        // retrieve market rent prices
        retrieveServicePrices(bo, to);

        // check unit catalog/lease readiness:
        if (retrieveTarget == RetrieveTarget.View) {
            EntityQueryCriteria<ProductItem> criteria = EntityQueryCriteria.create(ProductItem.class);

            criteria.add(PropertyCriterion.in(criteria.proto().product().holder().code().type(), ARCode.Type.unitRelatedServices()));
            criteria.add(PropertyCriterion.eq(criteria.proto().element(), bo));

            to.isPresentInCatalog().setValue(Persistence.service().exists(criteria));
            to.isAvailableForExistingLease().setValue(ServerSideFactory.create(OccupancyFacade.class).isAvailableForExistingLease(bo.getPrimaryKey()));

            Pair<Date, Lease> result = ServerSideFactory.create(OccupancyFacade.class).isReserved(bo.getPrimaryKey());
            if (result.getB() != null) {
                to.reservedUntil().setValue(result.getA());
            }
        }
    }

    @Override
    protected void enhanceListRetrieved(AptUnit in, AptUnitDTO dto) {
        //TODO: calculate value here:
        dto.buildingCode().set(Persistence.service().retrieve(Building.class, dto.building().getPrimaryKey()).propertyCode());

        // load detached entities (temporary):
        Persistence.service().retrieve(dto.floorplan());
        // TODO actually just this is necessary, but it' doesn't implemented still:
        //Persistence.service().retrieve(dto.floorplan().name());
        //Persistence.service().retrieve(dto.floorplan().marketingName());

        dto.info().economicStatusDescription().setValue(null);
    }

    @Override
    protected boolean persist(AptUnit bo, AptUnitDTO to) {
        ServerSideFactory.create(BuildingFacade.class).persist(bo);
        return true;
    }

    private void retrieveServicePrices(AptUnit bo, AptUnitDTO dto) {
        EntityQueryCriteria<ProductItem> criteria = EntityQueryCriteria.create(ProductItem.class);

        criteria.add(PropertyCriterion.eq(criteria.proto().product().holder().catalog().building(), dto.building()));
        criteria.add(PropertyCriterion.in(criteria.proto().product().holder().code().type(), ARCode.Type.unitRelatedServices()));
        criteria.add(PropertyCriterion.eq(criteria.proto().product().holder().defaultCatalogItem(), false));
        criteria.add(PropertyCriterion.eq(criteria.proto().element(), bo));
        criteria.isCurrent(criteria.proto().product());

        for (ProductItem item : Persistence.secureQuery(criteria)) {
            AptUnitServicePriceDTO serviceDTO = EntityFactory.create(AptUnitServicePriceDTO.class);

            serviceDTO.id().setValue(item.product().holder().id().getValue());
            serviceDTO.code().set(item.product().holder().code());
            serviceDTO.name().setValue(item.product().holder().version().name().getValue());
            serviceDTO.price().setValue(ServerSideFactory.create(ProductCatalogFacade.class).calculateItemPrice(item));

            dto.marketPrices().add(serviceDTO);
        }
    }
}

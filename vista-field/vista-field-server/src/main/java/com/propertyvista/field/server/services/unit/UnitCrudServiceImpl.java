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
 * @version $Id$
 */
package com.propertyvista.field.server.services.unit;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.AptUnitServicePriceDTO;
import com.propertyvista.field.rpc.services.unit.UnitCrudService;

public class UnitCrudServiceImpl extends AbstractCrudServiceDtoImpl<AptUnit, AptUnitDTO> implements UnitCrudService {

    public UnitCrudServiceImpl() {
        super(AptUnit.class, AptUnitDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
        bind(dtoProto.buildingCode(), dboProto.building().propertyCode());
    }

    @Override
    protected void enhanceRetrieved(AptUnit in, AptUnitDTO dto, RetrieveTarget retrieveTarget) {
        //TODO: calculate value here:
        dto.buildingCode().set(Persistence.service().retrieve(Building.class, dto.building().getPrimaryKey()).propertyCode());

        // load detached entities:
        if (!dto.marketing().isValueDetached()) { // This is not called for now cince file is detached in annotation. see comments on this filed
            Persistence.service().retrieve(dto.marketing().adBlurbs());
        }

        // find corresponding lease:
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().unit(), in));
            criteria.add(PropertyCriterion.in(criteria.proto().status(), Lease.Status.current()));
            // set sorting by 'from date' to get last active lease first:
            criteria.asc(criteria.proto().leaseFrom());
            dto.lease().set(Persistence.service().retrieve(criteria));
        }

        Persistence.service().retrieve(dto.floorplan());
        Persistence.service().retrieve(dto.building());

        // retrieve market rent prices
        retrieveServicePrices(dto);

        // check unit catalog/lease readiness:
        if (retrieveTarget == RetrieveTarget.View) {
            EntityQueryCriteria<ProductItem> criteria = EntityQueryCriteria.create(ProductItem.class);
            criteria.add(PropertyCriterion.in(criteria.proto().code().type(), ARCode.Type.services()));
            criteria.add(PropertyCriterion.eq(criteria.proto().element(), in));

            dto.isPresentInCatalog().setValue(Persistence.service().exists(criteria));
            dto.isAvailableForExistingLease().setValue(ServerSideFactory.create(OccupancyFacade.class).isAvailableForExistingLease(in.getPrimaryKey()));
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

        // just clear unnecessary data before serialization:
        if (!dto.marketing().isValueDetached()) {
            dto.marketing().description().setValue(null);
        }
        dto.info().economicStatusDescription().setValue(null);
    }

    @Override
    protected void persist(AptUnit entity, AptUnitDTO dto) {
        ServerSideFactory.create(BuildingFacade.class).persist(entity);
    }

    private void retrieveServicePrices(AptUnitDTO dto) {
        EntityQueryCriteria<Service> criteria = EntityQueryCriteria.create(Service.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().catalog().building(), dto.building()));
        criteria.add(PropertyCriterion.in(criteria.proto().type(), ARCode.Type.unitRelatedServices()));

        for (Service service : Persistence.secureQuery(criteria)) {
            if (!service.isDefaultCatalogItem().isBooleanTrue()) {
                Persistence.service().retrieve(service.version().items());
                for (ProductItem item : service.version().items()) {
                    if (item.element().getInstanceValueClass().equals(AptUnit.class) & item.element().getPrimaryKey().equals(dto.getPrimaryKey())) {
                        AptUnitServicePriceDTO serviceDTO = EntityFactory.create(AptUnitServicePriceDTO.class);
                        serviceDTO.id().setValue(service.id().getValue());
                        serviceDTO.type().setValue(service.type().getValue());
                        serviceDTO.name().setValue(service.version().name().getValue());
                        serviceDTO.price().setValue(item.price().getValue());
                        dto.marketPrices().add(serviceDTO);
                    }
                }
            }
        }
    }
}

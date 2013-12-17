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
package com.propertyvista.crm.server.services.unit;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.AptUnitServicePriceDTO;

public class UnitCrudServiceImpl extends AbstractCrudServiceDtoImpl<AptUnit, AptUnitDTO> implements UnitCrudService {

    public UnitCrudServiceImpl() {
        super(AptUnit.class, AptUnitDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
        bind(toProto.buildingCode(), boProto.building().propertyCode());
    }

    @Override
    protected AptUnitDTO init(InitializationData initializationData) {
        UnitInitializationdata initData = (UnitInitializationdata) initializationData;
        AptUnitDTO newUnit = EntityFactory.create(AptUnitDTO.class);
        newUnit.building().set(Persistence.service().retrieve(Building.class, initData.parent().getPrimaryKey()));
        return newUnit;
    }

    @Override
    protected void enhanceRetrieved(AptUnit in, AptUnitDTO to, RetrieveTarget retrieveTarget) {
        // load detached entities:
        if (!to.marketing().isValueDetached()) { // This is not called for now cince file is detached in annotation. see comments on this filed
            Persistence.service().retrieve(to.marketing().adBlurbs());
        }

        // find corresponding lease:
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().unit(), in));
            criteria.add(PropertyCriterion.in(criteria.proto().status(), Lease.Status.current()));
            // set sorting by 'from date' to get last active lease first:
            criteria.desc(criteria.proto().leaseFrom());
            to.lease().set(Persistence.service().retrieve(criteria));
        }

        Persistence.service().retrieve(to.floorplan());
        Persistence.service().retrieve(to.building());
        to.buildingCode().setValue(to.building().propertyCode().getValue());

        to.buildingLegalAddress().set(to.building().info().address());
        to.buildingLegalAddress().suiteNumber().set(to.info().number());

        // retrieve market rent prices
        retrieveServicePrices(to);

        // check unit catalog/lease readiness:
        if (retrieveTarget == RetrieveTarget.View) {
            EntityQueryCriteria<ProductItem> criteria = EntityQueryCriteria.create(ProductItem.class);
            criteria.add(PropertyCriterion.in(criteria.proto().product().holder().code().type(), ARCode.Type.services()));
            criteria.add(PropertyCriterion.eq(criteria.proto().element(), in));

            to.isPresentInCatalog().setValue(Persistence.service().exists(criteria));
            to.isAvailableForExistingLease().setValue(ServerSideFactory.create(OccupancyFacade.class).isAvailableForExistingLease(in.getPrimaryKey()));
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
    protected void persist(AptUnit bo, AptUnitDTO to) {
        ServerSideFactory.create(BuildingFacade.class).persist(bo);
    }

    private void retrieveServicePrices(AptUnitDTO dto) {
        EntityQueryCriteria<Service> criteria = EntityQueryCriteria.create(Service.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().catalog().building(), dto.building()));
        criteria.add(PropertyCriterion.in(criteria.proto().code().type(), ARCode.Type.unitRelatedServices()));

        for (Service service : Persistence.secureQuery(criteria)) {
            if (!service.isDefaultCatalogItem().isBooleanTrue()) {
                Persistence.ensureRetrieve(service.version().items(), AttachLevel.Attached);
                for (ProductItem item : service.version().items()) {
                    if (item.element().getInstanceValueClass().equals(AptUnit.class) & item.element().getPrimaryKey().equals(dto.getPrimaryKey())) {
                        AptUnitServicePriceDTO serviceDTO = EntityFactory.create(AptUnitServicePriceDTO.class);
                        serviceDTO.id().setValue(service.id().getValue());
                        serviceDTO.code().set(service.code());
                        serviceDTO.name().setValue(service.version().name().getValue());
                        serviceDTO.price().setValue(item.price().getValue());
                        dto.marketPrices().add(serviceDTO);
                    }
                }
            }
        }
    }
}

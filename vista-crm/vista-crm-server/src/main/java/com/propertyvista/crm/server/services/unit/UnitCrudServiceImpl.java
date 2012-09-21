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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.AptUnitServicePriceDTO;

public class UnitCrudServiceImpl extends AbstractCrudServiceDtoImpl<AptUnit, AptUnitDTO> implements UnitCrudService {

    private static final Vector<Service.ServiceType> SERVICES_PROVIDED_BY_UNIT = new Vector<Service.ServiceType>(Arrays.asList(
            Service.ServiceType.residentialUnit, Service.ServiceType.commercialUnit));

    public UnitCrudServiceImpl() {
        super(AptUnit.class, AptUnitDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
        bind(dtoProto.buildingCode(), dboProto.building().propertyCode());
    }

    @Override
    protected void enhanceRetrieved(AptUnit in, AptUnitDTO dto) {
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
            dto.lease().set(Persistence.service().retrieve(criteria));
        }

        Persistence.service().retrieve(dto.floorplan());
        Persistence.service().retrieve(dto.building());

        // retrieve market rent prices
        retrieveServicePrices(dto);

        // check unit catalog/lease readiness:
        {
            EntityQueryCriteria<ProductItem> criteria = EntityQueryCriteria.create(ProductItem.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().type(), ServiceItemType.class));
            criteria.add(PropertyCriterion.eq(criteria.proto().element(), in));
            dto.isPresentInCatalog().setValue(Persistence.service().exists(criteria));
        }

        dto.isAvailableForExistingLease().setValue(ServerSideFactory.create(OccupancyFacade.class).isAvailableForExistingLease(in.getPrimaryKey()));
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
    protected void persist(AptUnit dbo, AptUnitDTO in) {
        boolean isNewUnit = dbo.id().isNull();
        super.persist(dbo, in);
        if (isNewUnit) {
            ServerSideFactory.create(OccupancyFacade.class).setupNewUnit((AptUnit) dbo.createIdentityStub());
        }
    }

    private void retrieveServicePrices(AptUnitDTO dto) {
        EntityQueryCriteria<Service> criteria = EntityQueryCriteria.create(Service.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().catalog().building(), dto.building()));
        criteria.add(PropertyCriterion.in(criteria.proto().version().serviceType(), SERVICES_PROVIDED_BY_UNIT));

        List<Service> services = Persistence.secureQuery(criteria);
        for (Service service : services) {
            Persistence.service().retrieve(service.version().items());
            for (ProductItem item : service.version().items()) {
                if (item.element().getInstanceValueClass().equals(AptUnit.class) & item.element().getPrimaryKey().equals(dto.getPrimaryKey())) {
                    AptUnitServicePriceDTO serviceDTO = EntityFactory.create(AptUnitServicePriceDTO.class);
                    serviceDTO.id().setValue(service.id().getValue());
                    serviceDTO.type().setValue(service.version().serviceType().getValue());
                    serviceDTO.price().setValue(item.price().getValue());
                    dto.marketPrices().add(serviceDTO);
                }
            }
        }
    }
}

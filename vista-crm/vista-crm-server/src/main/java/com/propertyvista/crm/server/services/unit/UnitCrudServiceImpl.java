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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.AptUnitServicePriceDTO;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerImpl;

public class UnitCrudServiceImpl extends GenericCrudServiceDtoImpl<AptUnit, AptUnitDTO> implements UnitCrudService {

    private static final Vector<Service.Type> SERVICES_PROVIDED_BY_UNIT = new Vector<Service.Type>(Arrays.asList(Service.Type.residentialUnit,
            Service.Type.residentialShortTermUnit, Service.Type.commercialUnit));

    public UnitCrudServiceImpl() {
        super(AptUnit.class, AptUnitDTO.class);
    }

    @Override
    protected void enhanceDTO(AptUnit in, AptUnitDTO dto, boolean fromList) {
        //TODO: calculate value here:
        dto.buildingCode().set(Persistence.service().retrieve(Building.class, dto.belongsTo().getPrimaryKey()).propertyCode());

        if (!fromList) {
            // load detached entities:
            if (!dto.marketing().isValueDetached()) { // This is not called for now cince file is detached in annotation. see comments on this filed
                Persistence.service().retrieve(dto.marketing().adBlurbs());
            }

            Persistence.service().retrieve(dto.floorplan());
            Persistence.service().retrieve(dto.belongsTo());

            // retrieve market rent prices
            retrieveServicePrices(dto);

        } else {
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

        // Fill Unit financial data (transient):  
        dto.financial()._unitRent().setValue(null);
        dto.financial()._marketRent().setValue(null);

        EntityQueryCriteria<ProductItem> serviceItemCriteria = new EntityQueryCriteria<ProductItem>(ProductItem.class);
        serviceItemCriteria.add(PropertyCriterion.eq(serviceItemCriteria.proto().element(), in));
        ProductItem item = Persistence.service().retrieve(serviceItemCriteria);
        if (item != null) {
            dto.financial()._marketRent().setValue(item.price().getValue());
        }

        EntityQueryCriteria<Lease> leaseCriteria = new EntityQueryCriteria<Lease>(Lease.class);
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().unit(), in));
        Lease lease = Persistence.service().retrieve(leaseCriteria);
        if (lease != null && !lease.version().leaseProducts().isNull() && !lease.version().leaseProducts().serviceItem().isNull()) {
            PriceCalculationHelpers.calculateChargeItemAdjustments(lease.version().leaseProducts().serviceItem());
            dto.financial()._unitRent().setValue(lease.version().leaseProducts().serviceItem()._currentPrice().getValue());
        }
    }

    @Override
    protected void persistDBO(AptUnit dbo, AptUnitDTO in) {
        boolean isNewUnit = dbo.id().isNull();

        if (isNewUnit) {
            // if the unit is new, create a new occupancy for it and
            AptUnitOccupancySegment vacant = EntityFactory.create(AptUnitOccupancySegment.class);
            vacant.status().setValue(Status.vacant);
            vacant.dateFrom().setValue(new LogicalDate());
            vacant.dateTo().setValue(AptUnitOccupancyManagerHelper.MAX_DATE);
            dbo._AptUnitOccupancySegment().add(vacant);

        }
        super.persistDBO(dbo, in);
        if (isNewUnit) {
            new AptUnitOccupancyManagerImpl(dbo).scopeAvailable();
        }
    }

    @Override
    protected String convertPropertyDTOPathToDBO(String path, AptUnit dboProto, AptUnitDTO dtoProto) {
        if (path.equals(dtoProto.buildingCode().getPath().toString())) {
            return dboProto.belongsTo().propertyCode().getPath().toString();
        } else {
            return dboProto.getObjectClass().getSimpleName() + path.substring(path.indexOf(Path.PATH_SEPARATOR));
        }
    }

    private void retrieveServicePrices(AptUnitDTO dto) {
        EntityQueryCriteria<Service> criteria = EntityQueryCriteria.create(Service.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().catalog().building(), dto.belongsTo()));
        criteria.add(PropertyCriterion.in(criteria.proto().version().type(), SERVICES_PROVIDED_BY_UNIT));

        List<Service> services = Persistence.secureQuery(criteria);
        for (Service service : services) {
            Persistence.service().retrieve(service.version().items());
            for (ProductItem item : service.version().items()) {
                if (item.element().getInstanceValueClass().equals(AptUnit.class) & item.element().getPrimaryKey().equals(dto.getPrimaryKey())) {
                    AptUnitServicePriceDTO serviceDTO = EntityFactory.create(AptUnitServicePriceDTO.class);
                    serviceDTO.id().setValue(service.id().getValue());
                    serviceDTO.type().setValue(service.version().type().getValue());
                    serviceDTO.price().setValue(item.price().getValue());
                    dto.servicePrices().add(serviceDTO);
                }
            }

        }

    }
}

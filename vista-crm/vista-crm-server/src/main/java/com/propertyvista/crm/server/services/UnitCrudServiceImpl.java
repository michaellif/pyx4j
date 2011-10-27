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
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;

public class UnitCrudServiceImpl extends GenericCrudServiceDtoImpl<AptUnit, AptUnitDTO> implements UnitCrudService {

    public UnitCrudServiceImpl() {
        super(AptUnit.class, AptUnitDTO.class);
    }

    @Override
    protected void enhanceDTO(AptUnit in, AptUnitDTO dto, boolean fromList) {
        //TODO: calculate value here:
        dto.numberOfOccupants().setValue(0.0);
        dto.buildingCode().set(Persistence.service().retrieve(Building.class, dto.belongsTo().getPrimaryKey()).propertyCode());

        if (!fromList) {
            // load detached entities:
            Persistence.service().retrieve(in.marketing().adBlurbs());
            Persistence.service().retrieve(in.floorplan());
        } else {
            // load detached entities (temporary):
            Persistence.service().retrieve(in.floorplan());
            // TODO actually just this is necessary, but it' doesn't implemented still:
            //Persistence.service().retrieve(in.floorplan().name());
            //Persistence.service().retrieve(in.floorplan().marketingName());

            // just clear unnecessary data before serialization: 
            in.marketing().description().setValue(null);
            in.info().economicStatusDescription().setValue(null);
        }

        // Fill Unit financial data (transient):  
        in.financial().unitRent().setValue(0.0);
        in.financial().marketRent().setValue(0.0);

        EntityQueryCriteria<ServiceItem> serviceItemCriteria = new EntityQueryCriteria<ServiceItem>(ServiceItem.class);
        serviceItemCriteria.add(PropertyCriterion.eq(serviceItemCriteria.proto().element(), in));
        ServiceItem item = Persistence.service().retrieve(serviceItemCriteria);
        if (item != null) {
            in.financial().marketRent().setValue(item.price().getValue());
        }

        EntityQueryCriteria<Lease> leaseCriteria = new EntityQueryCriteria<Lease>(Lease.class);
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().unit(), in));
        Lease lease = Persistence.service().retrieve(leaseCriteria);
        if (lease != null && !lease.serviceAgreement().isNull() && !lease.serviceAgreement().serviceItem().isNull()) {
            PriceCalculationHelpers.calculateChargeItemAdjustments(lease.serviceAgreement().serviceItem());
            in.financial().unitRent().setValue(lease.serviceAgreement().serviceItem().adjustedPrice().getValue());
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
}

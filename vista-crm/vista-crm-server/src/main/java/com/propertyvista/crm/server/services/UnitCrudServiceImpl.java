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

import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.AptUnitDTO;

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
        } else {
            // load detached entities (temporary):
            Persistence.service().retrieve(in.floorplan());
            // TODO actually just this is necessary, but it' doesn't implemented still:
            //Persistence.service().retrieve(in.floorplan().name());

            // just clear unnecessary data before serialisation: 
            in.marketing().description().setValue(null);
            in.info().economicStatusDescription().setValue(null);
        }
    }
}

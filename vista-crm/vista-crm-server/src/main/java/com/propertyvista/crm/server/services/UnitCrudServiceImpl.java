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

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.dto.AptUnitDTO;

public class UnitCrudServiceImpl extends GenericCrudServiceDtoImpl<AptUnit, AptUnitDTO> implements UnitCrudService {

    public UnitCrudServiceImpl() {
        super(AptUnit.class, AptUnitDTO.class);
    }

    @Override
    protected void enhanceRetrieveDTO(AptUnit in, AptUnitDTO dto) {

        EntityQueryCriteria<AptUnitItem> unitItemCriteria = EntityQueryCriteria.create(AptUnitItem.class);
        unitItemCriteria
                .add(new PropertyCriterion(EntityFactory.getEntityPrototype(AptUnitItem.class).belongsTo().getPath().toString(), Restriction.EQUAL, in));
        for (AptUnitItem unitItem : PersistenceServicesFactory.getPersistenceService().query(unitItemCriteria)) {
            dto.details().add(unitItem);
        }

        EntityQueryCriteria<AptUnitOccupancy> occupancyCriteria = EntityQueryCriteria.create(AptUnitOccupancy.class);
        occupancyCriteria
                .add(new PropertyCriterion(EntityFactory.getEntityPrototype(AptUnitOccupancy.class).unit().getPath().toString(), Restriction.EQUAL, in));
        for (AptUnitOccupancy occupancy : PersistenceServicesFactory.getPersistenceService().query(occupancyCriteria)) {
            dto.occupancies().add(occupancy);
        }

        //TODO: calculate value here:
        dto.numberOfOccupants().setValue(0.0);
    }
}

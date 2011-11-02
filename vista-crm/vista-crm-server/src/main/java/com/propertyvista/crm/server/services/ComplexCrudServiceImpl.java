/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.ComplexCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.ComplexDTO;

public class ComplexCrudServiceImpl extends GenericCrudServiceDtoImpl<Complex, ComplexDTO> implements ComplexCrudService {

    public ComplexCrudServiceImpl() {
        super(Complex.class, ComplexDTO.class);
    }

    @Override
    protected void enhanceDTO(Complex in, ComplexDTO dto, boolean fromList) {
        super.enhanceDTO(in, dto, fromList);
        Persistence.service().retrieve(dto.dashboard());

        if (!fromList) {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().complex(), in));
            List<Building> buildings = Persistence.service().query(criteria);
            for (Building building : buildings) {
                if (building.complexPrimary().isBooleanTrue()) {
                    Persistence.service().retrieve(building.contacts().contacts());
                    Persistence.service().retrieve(building.contacts().phones());
                    dto.primaryBuilding().set(building);
                    break;
                }
            }

            dto.buildings().addAll(buildings);
        }
    }

    @Override
    protected void persistDBO(Complex dbo, ComplexDTO in) {
        super.persistDBO(dbo, in);
        Building primary = in.primaryBuilding();

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().complex(), dbo));
        List<Building> buildings = Persistence.service().query(criteria);
        for (Building building : buildings) {
            building.complexPrimary().setValue(building.getPrimaryKey().equals(primary.getPrimaryKey()));
        }

        Persistence.service().merge(buildings);
    }
}

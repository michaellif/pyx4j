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
    protected void enhanceDTO(Complex in, ComplexDTO out, boolean fromList) {
        super.enhanceDTO(in, out, fromList);

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().complex(), in));

        List<Building> buildings = Persistence.service().query(criteria);

        if (!buildings.isEmpty()) {
            Building primary = null;
            for (Building building : buildings) {
                if (building.complexPrimary().isBooleanTrue()) {
                    primary = building;
                    Persistence.service().retrieve(primary.contacts().contacts());
                    Persistence.service().retrieve(primary.contacts().phones());
                    out.primaryBuilding().set(primary);
                    break;
                }
            }

            if (!fromList) {
                out.buildings().addAll(buildings);

                Persistence.service().retrieve(in.dashboard());
                out.dashboard().set(in.dashboard());
            }
        }
    }

    @Override
    protected void persistDBO(Complex out, ComplexDTO in) {
        super.persistDBO(out, in);
        Building primary = in.primaryBuilding();

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().complex(), out));
        List<Building> buildings = Persistence.service().query(criteria);

        for (Building building : buildings) {
            if (building.equals(primary)) {
                building.complexPrimary().setValue(false);
            } else {
                building.complexPrimary().setValue(true);
            }
        }

        Persistence.service().merge(buildings);
    }
}

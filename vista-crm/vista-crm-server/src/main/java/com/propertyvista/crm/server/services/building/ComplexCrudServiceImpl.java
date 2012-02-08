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
package com.propertyvista.crm.server.services.building;

import java.util.List;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.crm.rpc.services.building.ComplexCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.ComplexDTO;

public class ComplexCrudServiceImpl extends AbstractCrudServiceDtoImpl<Complex, ComplexDTO> implements ComplexCrudService {

    private class BuildingBinder extends EntityDtoBinder<Building, Building> {

        protected BuildingBinder() {
            super(Building.class, Building.class);
        }

        @Override
        protected void bind() {
            bind(dtoProto.propertyCode(), dboProto.propertyCode());
            bind(dtoProto.complex().id(), dboProto.complex().id());
            bind(dtoProto.complexPrimary(), dboProto.complexPrimary());
            bind(dtoProto.orderInComplex(), dboProto.orderInComplex());
            bind(dtoProto.info().name(), dboProto.info().name());
            bind(dtoProto.info().type(), dboProto.info().type());
        }

    }

    public ComplexCrudServiceImpl() {
        super(Complex.class, ComplexDTO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.name(), dboProto.name());
        bind(dtoProto.website(), dboProto.website());
    }

    @Override
    protected void enhanceRetrieved(Complex entity, ComplexDTO dto) {
        if (!entity.dashboard().isNull()) {
            Persistence.service().retrieve(entity.dashboard());
            dto.dashboard().set(entity.dashboard());
        } else {
            // load first building  dashboard by default:
            EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().type(), DashboardMetadata.DashboardType.building));
            List<DashboardMetadata> dashboards = Persistence.service().query(criteria);
            if (!dashboards.isEmpty()) {
                dto.dashboard().set(dashboards.get(0));
            }
        }

        {
            // fill transient data:
            // TODO use Persistence.service().retrieve(in.buildings(), new BuildingBinder());
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().complex(), entity));
            criteria.asc(criteria.proto().orderInComplex());
            for (Building building : Persistence.service().query(criteria)) {
                dto.buildings().add(new BuildingBinder().createDTO(building));
            }
        }
    }

    @Override
    protected void persist(Complex dbo, ComplexDTO in) {
        super.persist(dbo, in);

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().complex(), in));
        for (Building building : Persistence.service().query(criteria)) {
            if (!in.buildings().contains(building)) {
                building.complex().set(null);
                Persistence.service().persist(building);
            }
        }

        int count = 0;
        for (Building dto : in.buildings()) {
            dto.complex().set(dbo);
            dto.orderInComplex().setValue(count++);
            Building building = Persistence.service().retrieve(Building.class, dto.getPrimaryKey());
            // update, possible primary building change, addition to complex, order
            if (new BuildingBinder().updateDBO(dto, building)) {
                Persistence.service().persist(building);
            }
        }
    }
}

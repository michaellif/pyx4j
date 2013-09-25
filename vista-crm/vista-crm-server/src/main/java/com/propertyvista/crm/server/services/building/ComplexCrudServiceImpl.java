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

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.crm.rpc.services.building.ComplexCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.ComplexDTO;

public class ComplexCrudServiceImpl extends AbstractCrudServiceDtoImpl<Complex, ComplexDTO> implements ComplexCrudService {

    private class BuildingBinder extends EntityBinder<Building, Building> {

        protected BuildingBinder() {
            super(Building.class, Building.class);
        }

        @Override
        protected void bind() {
            bind(toProto.propertyCode(), boProto.propertyCode());
            bind(toProto.complex().id(), boProto.complex().id());
            bind(toProto.complexPrimary(), boProto.complexPrimary());
            bind(toProto.orderInComplex(), boProto.orderInComplex());
            bind(toProto.info().name(), boProto.info().name());
            bind(toProto.info().type(), boProto.info().type());
        }

    }

    public ComplexCrudServiceImpl() {
        super(Complex.class, ComplexDTO.class);
    }

    @Override
    protected void bind() {
        bind(toProto.name(), boProto.name());
        bind(toProto.website(), boProto.website());
    }

    @Override
    protected void enhanceRetrieved(Complex bo, ComplexDTO to, RetrieveTarget retrieveTarget) {
        // add dashboards
        if (retrieveTarget == RetrieveTarget.View) {
            EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().type(), DashboardMetadata.DashboardType.building));
            to.dashboards().addAll(Persistence.secureQuery(criteria, AttachLevel.ToStringMembers));
        }

        // fill transient data:
        // TODO use Persistence.service().retrieve(in.buildings(), new BuildingBinder());
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().complex(), bo));
        criteria.asc(criteria.proto().orderInComplex());
        for (Building building : Persistence.service().query(criteria)) {
            to.buildings().add(new BuildingBinder().createTO(building));
        }
    }

    @Override
    protected void enhanceListRetrieved(Complex entity, ComplexDTO dto) {
        super.enhanceListRetrieved(entity, dto);
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().complex(), entity));
        criteria.add(PropertyCriterion.eq(criteria.proto().complexPrimary(), true));
        dto.primaryBuilding().set(Persistence.secureRetrieve(criteria));
        if (!dto.primaryBuilding().isNull()) {
            dto.address().set(dto.primaryBuilding().info().address());
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

        // set it to primary if just one building in complex:
        if (in.buildings().size() == 1) {
            in.buildings().get(0).complexPrimary().setValue(Boolean.TRUE);
        }

        int count = 0;
        for (Building dto : in.buildings()) {
            dto.complex().set(dbo);
            dto.orderInComplex().setValue(count++);
            Building building = Persistence.service().retrieve(Building.class, dto.getPrimaryKey());
            // update, possible primary building change, addition to complex, order
            if (new BuildingBinder().updateBO(dto, building)) {
                Persistence.service().persist(building);
            }
        }
    }
}

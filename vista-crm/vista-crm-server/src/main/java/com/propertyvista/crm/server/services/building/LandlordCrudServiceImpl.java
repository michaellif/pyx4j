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

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.crm.rpc.services.building.LandlordCrudService;
import com.propertyvista.domain.property.Landlord;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.LandlordDTO;

public class LandlordCrudServiceImpl extends AbstractCrudServiceDtoImpl<Landlord, LandlordDTO> implements LandlordCrudService {

    private class BuildingBinder extends EntityBinder<Building, Building> {

        protected BuildingBinder() {
            super(Building.class, Building.class);
        }

        @Override
        protected void bind() {
            bind(toProto.propertyCode(), boProto.propertyCode());
            bind(toProto.info().name(), boProto.info().name());
            bind(toProto.info().type(), boProto.info().type());
        }

    }

    public LandlordCrudServiceImpl() {
        super(Landlord.class, LandlordDTO.class);
    }

    @Override
    protected void bind() {
        bind(toProto.name(), boProto.name());
        bind(toProto.address(), boProto.address());
        bind(toProto.website(), boProto.website());
        bind(toProto.logo(), boProto.logo());
        bind(toProto.signature(), boProto.signature());
        bind(toProto.buildings(), boProto.buildings(), new BuildingBinder());
    }

    @Override
    protected void retrievedSingle(Landlord bo, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        Persistence.ensureRetrieve(bo.buildings(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.logo(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.signature(), AttachLevel.Attached);
    }

    @Override
    protected void persist(Landlord bo, LandlordDTO to) {
        super.persist(bo, to);

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().landlord(), bo));
        for (Building building : Persistence.service().query(criteria)) {
            if (!to.buildings().contains(building)) {
                building.landlord().set(null);
                Persistence.service().persist(building);
            }
        }

        for (Building dto : to.buildings()) {
            if (!dto.landlord().equals(bo)) {
                Building building = Persistence.service().retrieve(Building.class, dto.getPrimaryKey());
                building.landlord().set(bo);
                Persistence.service().persist(building);
            }
        }
    }
}

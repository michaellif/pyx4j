/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.selections;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractListServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;
import com.propertyvista.crm.rpc.services.selections.SelectBuildingListService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.occupancy.UnitAvailabilityCriteria;

public class SelectBuildingListServiceImpl extends AbstractListServiceImpl<Building> implements SelectBuildingListService {

    public SelectBuildingListServiceImpl() {
        super(Building.class);
    }

    @Override
    protected void bind() {
        bind(toProto.id(), boProto.id());
        bindCompleteObject();
    }

    @Override
    public Criterion convertCriterion(EntityListCriteria<Building> criteria, Criterion cr) {
        if (cr instanceof UnitAvailabilityCriteria) {
            UnitAvailabilityCriteria availability = (UnitAvailabilityCriteria) cr;
            return ServerSideFactory.create(OccupancyFacade.class).buildAvalableCriteria(criteria.proto().units().$(), availability.getStatus(),
                    availability.getFrom(), null);
        } else {
            return super.convertCriterion(criteria, cr);
        }
    }

    @Override
    public void getBuildingsForSelection(AsyncCallback<Vector<BuildingForSelectionDTO>> callback, EntityQueryCriteria<Building> criteria) {
        Vector<Building> buildings = Persistence.secureQuery(criteria);
        Vector<BuildingForSelectionDTO> dtos = new Vector<BuildingForSelectionDTO>(buildings.size());
        for (Building buidling : buildings) {
            dtos.add(convertTo4SelectionDto(buidling));
        }
        callback.onSuccess(dtos);
    }

    private BuildingForSelectionDTO convertTo4SelectionDto(Building building) {
        BuildingForSelectionDTO dto = EntityFactory.create(BuildingForSelectionDTO.class);
        dto.id().setValue(building.id().getValue());
        dto.propertyCode().setValue(building.propertyCode().getValue());
        dto.name().setValue(building.info().name().getValue());
        dto.address().setValue(building.info().address().getStringView());
        return dto;
    }
}

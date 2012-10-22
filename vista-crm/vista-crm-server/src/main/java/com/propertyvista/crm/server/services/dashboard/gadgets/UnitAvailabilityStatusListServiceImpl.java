/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.dto.gadgets.UnitAvailabilityStatusDTO;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.property.asset.building.Building;

public class UnitAvailabilityStatusListServiceImpl extends AbstractListServiceDtoImpl<UnitAvailabilityStatus, UnitAvailabilityStatusDTO> {

    public UnitAvailabilityStatusListServiceImpl() {
        super(UnitAvailabilityStatus.class, UnitAvailabilityStatusDTO.class);
    }

    @Override
    protected void bind() {
        // REFERENCES        
        bind(dtoProto.propertyCode(), dboProto.building().propertyCode());
        bind(dtoProto.externalId(), dboProto.building().externalId());
        bind(dtoProto.buildingName(), dboProto.building().info().name());
        bind(dtoProto.address(), dboProto.building().info().address());
        bind(dtoProto.propertyManager(), dboProto.building().propertyManager().name());
        bind(dtoProto.complex(), dboProto.building().complex().name());
        bind(dtoProto.unit(), dboProto.unit().info().number());
        bind(dtoProto.floorplanName(), dboProto.floorplan().name());
        bind(dtoProto.floorplanMarketingName(), dboProto.floorplan().marketingName());

        // STATUS DATA    
        bind(dtoProto.statusFrom(), dboProto.statusFrom());
        bind(dtoProto.statusUntil(), dboProto.statusUntil());
        bind(dtoProto.vacancyStatus(), dboProto.vacancyStatus());
        bind(dtoProto.rentedStatus(), dboProto.rentedStatus());
        bind(dtoProto.scoping(), dboProto.scoping());
        bind(dtoProto.rentReadinessStatus(), dboProto.rentReadinessStatus());
        bind(dtoProto.unitRent(), dboProto.unitRent());
        bind(dtoProto.marketRent(), dboProto.marketRent());
        bind(dtoProto.rentDeltaAbsolute(), dboProto.rentDeltaAbsolute());
        bind(dtoProto.rentDeltaRelative(), dboProto.rentDeltaRelative());
        bind(dtoProto.rentEndDay(), dboProto.rentEndDay());
        bind(dtoProto.vacantSince(), dboProto.vacantSince());
        bind(dtoProto.rentedFromDay(), dboProto.rentedFromDay());
        bind(dtoProto.moveInDay(), dboProto.moveInDay());

        // BUISNESS DATA
        bind(dtoProto.unitId(), dboProto.unit().id());
        bind(dtoProto.buildingsFilterAnchor(), dboProto.building());
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<UnitAvailabilityStatusDTO>> callback, EntityListCriteria<UnitAvailabilityStatusDTO> dtoCriteria) {
        dtoCriteria.add(PropertyCriterion.isNotNull(dtoCriteria.proto().vacancyStatus()));
        dtoCriteria.add(PropertyCriterion.in(dtoCriteria.proto().buildingsFilterAnchor(), Util.enforcePortfolio(new Vector<Building>())));
        super.list(callback, dtoCriteria);
    }

}

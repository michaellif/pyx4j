/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.services.resident.SelfRegistrationBuildingsSourceService;

public class SelfRegistrationBuildingsSourceServiceImpl implements SelfRegistrationBuildingsSourceService {

    @Override
    public void obtainBuildings(AsyncCallback<EntitySearchResult<SelfRegistrationBuildingDTO>> callback) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        List<Building> buildingsDbo = Persistence.service().query(criteria);

        Vector<SelfRegistrationBuildingDTO> buildingsDto = new Vector<SelfRegistrationBuildingDTO>();
        for (Building dbo : buildingsDbo) {
            buildingsDto.add(toDto(dbo));
        }

        EntitySearchResult<SelfRegistrationBuildingDTO> result = new EntitySearchResult<SelfRegistrationBuildingDTO>();
        result.setData(buildingsDto);
        result.setTotalRows(buildingsDto.size());
        result.hasMoreData(false);

        callback.onSuccess(result);
    }

    private SelfRegistrationBuildingDTO toDto(Building dbo) {

        SelfRegistrationBuildingDTO dto = EntityFactory.create(SelfRegistrationBuildingDTO.class);
        dto.address().setValue(dbo.info().address().getStringView());
        dto.buildingKey().set(dbo.createIdentityStub());

        return dto;
    }
}

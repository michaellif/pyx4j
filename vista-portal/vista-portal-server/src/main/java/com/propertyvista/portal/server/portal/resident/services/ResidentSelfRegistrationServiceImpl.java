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
package com.propertyvista.portal.server.portal.resident.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.ResidentSelfRegistration;
import com.propertyvista.portal.rpc.portal.resident.dto.ResidentSelfRegistrationDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.resident.services.ResidentSelfRegistrationService;

public class ResidentSelfRegistrationServiceImpl implements ResidentSelfRegistrationService {

    @Override
    public void obtainBuildings(AsyncCallback<EntitySearchResult<SelfRegistrationBuildingDTO>> callback) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        List<Building> buildingsDbo = new Vector<Building>(Persistence.service().query(criteria));
        Collections.sort(buildingsDbo, new Comparator<Building>() {
            @Override
            public int compare(Building o1, Building o2) {
                int c = o1.info().address().country().name().compareTo(o2.info().address().country().name());
                if (c == 0) {
                    c = o1.info().address().province().name().compareTo(o2.info().address().province().name());
                    if (c == 0) {
                        c = o1.info().address().city().compareTo(o2.info().address().city());
                        if (c == 0) {
                            c = o1.info().address().streetName().compareTo(o2.info().address().streetName());
                            if (c == 0) {
                                c = o1.info().address().streetNumber().compareTo(o2.info().address().streetNumber());
                            }
                        }
                    }
                }
                return c;
            }
        });

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

    @Override
    public void selfRegistration(AsyncCallback<VoidSerializable> callback, ResidentSelfRegistrationDTO dto) {
        ResidentSelfRegistration request = EntityFactory.create(ResidentSelfRegistration.class);

        request.buildingId().set(dto.building().buildingKey());
        request.firstName().setValue(dto.firstName().getValue());
        request.middleName().setValue(dto.middleName().getValue());
        request.lastName().setValue(dto.lastName().getValue());
        request.securityCode().setValue(dto.securityCode().getValue());
        request.email().setValue(dto.email().getValue());
        request.password().setValue(dto.password().getValue());

        ServerSideFactory.create(CustomerFacade.class).residentSelfRegistration(request);
        Persistence.service().commit();
        callback.onSuccess(null);
    }
}

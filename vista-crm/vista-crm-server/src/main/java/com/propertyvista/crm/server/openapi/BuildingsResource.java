/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.server.openapi;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.crm.server.openapi.model.BuildingRS;
import com.propertyvista.domain.property.asset.building.Building;

@Path("/buildings")
public class BuildingsResource {

    private final IEntityPersistenceService service;

    public BuildingsResource() {
        service = PersistenceServicesFactory.getPersistenceService();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<BuildingRS> listBuildings() {

        List<BuildingRS> buildingsRS = new ArrayList<BuildingRS>();

        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);

        List<Building> buildings = service.query(buildingCriteria);

        for (Building building : buildings) {
            buildingsRS.add(new BuildingRS(building));
        }

        return buildingsRS;
    }
}

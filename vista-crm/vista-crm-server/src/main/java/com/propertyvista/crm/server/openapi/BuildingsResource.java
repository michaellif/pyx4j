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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.server.openapi.model.BuildingRS;
import com.propertyvista.crm.server.openapi.model.BuildingsRS;
import com.propertyvista.crm.server.openapi.model.util.Converter;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;

@Path("/buildings")
public class BuildingsResource {

    private final static Logger log = LoggerFactory.getLogger(BuildingsResource.class);

    private final IEntityPersistenceService service;

    public BuildingsResource() {
        service = PersistenceServicesFactory.getPersistenceService();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public BuildingsRS listBuildings() {

        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);

        BuildingsRS buildingsRS = new BuildingsRS();

        List<Building> buildings = service.query(buildingCriteria);

        for (Building building : buildings) {
            BuildingRS buildingRS = Converter.convertBuilding(building);
            buildingsRS.buildings.add(buildingRS);

            EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
            floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
            List<Floorplan> floorplans = service.query(floorplanCriteria);
//            for (Floorplan floorplan : floorplans) {
//                for (Media media : floorplan.media()) {
//                    log.info("Floor Media {}", media);
//                    EntityQueryCriteria<Media> mediaCriteria = EntityQueryCriteria.create(Media.class);
//                    mediaCriteria.add(PropertyCriterion.eq(mediaCriteria.proto().id(), media.id()));
//                }
//            }

            buildingRS.floorplans = Converter.convertFloorplans(floorplans);

//            for (Media media : building.media()) {
//                log.info("Media {}", media);
//                EntityQueryCriteria<Media> mediaCriteria = EntityQueryCriteria.create(Media.class);
//            }
        }

        return buildingsRS;
    }
}

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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.crm.server.openapi.model.BuildingRS;
import com.propertyvista.crm.server.openapi.model.BuildingsRS;
import com.propertyvista.crm.server.openapi.model.FloorplanRS;
import com.propertyvista.crm.server.openapi.model.util.Converter;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.common.reference.SharedData;

@Path("/buildings")
public class BuildingsResource {

    private final static Logger log = LoggerFactory.getLogger(BuildingsResource.class);

    private final IEntityPersistenceService service;

    public BuildingsResource() {
        service = PersistenceServicesFactory.getPersistenceService();

        // TODO this is only temporary
        SharedData.init();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_XML })
    public Response createBuildings(InputStream is) {
        try {
            // retrieve xml
            String xml = IOUtils.toString(is);

            // parse xml
            BuildingRS buildingRS = MarshallUtil.unmarshal(BuildingRS.class, xml);

            // convert building to vista
            Building building = Converter.convertBuilding(buildingRS);
            log.info("Parsed building {}", building);

            // save building related data
            log.info("For now not saving this to the database");
//            service.persist(building.info().address());
//            service.persist(building.info());
//            service.persist(building.marketing());
//            service.persist(building);
        } catch (JAXBException je) {
            log.error("Failed to parse XML", je);
        } catch (IOException ie) {
            log.error("IO error", ie);
        }

        // for now we just show all the buildings, later we will have to show just the one that they've passed
        return Response.created(URI.create("/buildings")).build();
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
            for (Floorplan floorplan : floorplans) {
                FloorplanRS floorplanRS = Converter.convertFloorplan(floorplan);

                for (Media media : floorplan.media()) {
                    EntityQueryCriteria<Media> mediaCriteria = EntityQueryCriteria.create(Media.class);
                    mediaCriteria.add(PropertyCriterion.eq(mediaCriteria.proto().id(), media.id().getValue()));
                    List<Media> fetchedMedias = service.query(mediaCriteria);
                    for (Media fetchedMedia : fetchedMedias) {
                        floorplanRS.medias.media.add(Converter.convertMedia(fetchedMedia));
                    }
                }
                buildingRS.floorplans.floorplans.add(floorplanRS);
            }
        }

        return buildingsRS;
    }
}

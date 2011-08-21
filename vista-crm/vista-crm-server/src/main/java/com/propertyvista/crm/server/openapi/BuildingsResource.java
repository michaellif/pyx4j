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

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.crm.server.openapi.model.BuildingRS;
import com.propertyvista.crm.server.openapi.model.BuildingsRS;
import com.propertyvista.crm.server.openapi.model.FloorplanRS;
import com.propertyvista.crm.server.openapi.model.util.Converter;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
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

        long start = System.currentTimeMillis();

        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);

        BuildingsRS buildingsRS = new BuildingsRS();

        List<Building> buildings = service.query(buildingCriteria);

        for (Building building : buildings) {
            BuildingRS buildingRS = Converter.convertBuilding(building);
            buildingsRS.buildings.add(buildingRS);

            //Get Amenity
            {
                EntityQueryCriteria<BuildingAmenity> criteria = EntityQueryCriteria.create(BuildingAmenity.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), building));
                for (BuildingAmenity amenity : PersistenceServicesFactory.getPersistenceService().query(criteria)) {
                    buildingRS.amenities.add(Converter.convertBuildingAmenity(amenity));
                }
            }
            if (!building.media().isEmpty()) {
                PersistenceServicesFactory.getPersistenceService().retrieve(building.media());
                for (Media media : building.media()) {
                    buildingRS.medias.add(Converter.convertMedia(media));
                }
            }

            {
                for (ServiceItemType utility : building.serviceCatalog().includedUtilities()) {
                    buildingRS.includedUtilities.add(Converter.convertBuildingIncludedUtility(utility));
                }
            }

            buildingRS.unitCount = 0;

            EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
            floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
            List<Floorplan> floorplans = service.query(floorplanCriteria);
            for (Floorplan floorplan : floorplans) {
                FloorplanRS floorplanRS = Converter.convertFloorplan(floorplan);

                //Get Amenity
                {
                    EntityQueryCriteria<FloorplanAmenity> criteria = EntityQueryCriteria.create(FloorplanAmenity.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), floorplan));
                    for (FloorplanAmenity amenity : PersistenceServicesFactory.getPersistenceService().query(criteria)) {
                        floorplanRS.amenities.add(Converter.convertFloorplanAmenity(amenity));
                    }
                }

                // Count Units and get stats
                {
                    EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
                    for (AptUnit u : PersistenceServicesFactory.getPersistenceService().query(criteria)) {
                        buildingRS.unitCount++;

                        floorplanRS.rentFrom = min(floorplanRS.rentFrom, u.financial().unitRent().getValue());
                        floorplanRS.rentTo = max(floorplanRS.rentTo, u.financial().unitRent().getValue());
                        floorplanRS.sqftFrom = min(floorplanRS.sqftFrom, u.info().area().getValue());
                        floorplanRS.sqftTo = max(floorplanRS.sqftTo, u.info().area().getValue());

                        if (!u.availableForRent().isNull()) {
                            if ((floorplanRS.availableFrom == null) || (floorplanRS.availableFrom.after(u.availableForRent().getValue()))) {
                                floorplanRS.availableFrom = u.availableForRent().getValue();
                            }
                        }
                    }
                }
                buildingRS.rentFrom = min(buildingRS.rentFrom, floorplanRS.rentFrom);
                buildingRS.rentTo = max(buildingRS.rentTo, floorplanRS.rentTo);
                buildingRS.sqftFrom = min(buildingRS.sqftFrom, floorplanRS.sqftFrom);
                buildingRS.sqftTo = max(buildingRS.sqftTo, floorplanRS.sqftTo);

                if (!floorplan.media().isEmpty()) {
                    PersistenceServicesFactory.getPersistenceService().retrieve(floorplan.media());
                    for (Media media : floorplan.media()) {
                        floorplanRS.medias.add(Converter.convertMedia(media));
                    }
                }

                buildingRS.floorplans.add(floorplanRS);
            }
        }

        log.info("BuildingRS Retrive time {} msec", TimeUtils.since(start));

        return buildingsRS;
    }

    Double min(Double a, Double b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return Math.min(a, b);
        }
    }

    Double max(Double a, Double b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return Math.max(a, b);
        }
    }
}

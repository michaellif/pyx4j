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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.server.openapi.model.BuildingRS;
import com.propertyvista.crm.server.openapi.model.BuildingsRS;
import com.propertyvista.crm.server.openapi.model.FloorplanRS;
import com.propertyvista.crm.server.openapi.model.util.Converter;
import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.PropertyManager;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.util.DomainUtil;

@Path("/buildings")
public class BuildingsResource {

    private final static Logger log = LoggerFactory.getLogger(BuildingsResource.class);

    private final IEntityPersistenceService service;

    public BuildingsResource() {
        service = Persistence.service();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public BuildingsRS listBuildings(@QueryParam("pm") String propertyManagerName) {

        long start = System.currentTimeMillis();
        BuildingsRS buildingsRS = new BuildingsRS();

        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        if (CommonsStringUtils.isStringSet(propertyManagerName)) {
            EntityQueryCriteria<PropertyManager> criteria = EntityQueryCriteria.create(PropertyManager.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().name(), propertyManagerName));
            PropertyManager propertyManager = service.retrieve(criteria);
            if (propertyManager == null) {
                throw new Error("PropertyManager '" + propertyManagerName + "' not found");
            }
            buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().propertyManager(), propertyManager));
        }
        List<Building> buildings = service.query(buildingCriteria);

        Map<Complex, BuildingRS> complexes = new Hashtable<Complex, BuildingRS>();

        for (Building building : buildings) {
            if (!PublicVisibilityType.global.equals(building.marketing().visibility().getValue())) {
                continue;
            }
            try {
                BuildingRS buildingRS;

                // Group buildings by Complex, Exporting as one Building in Complex.
                boolean exportBuildingInfo = false;
                if (building.complex().isNull()) {
                    if (building.marketing().adBlurbs().getMeta().isDetached()) {
                        Persistence.service().retrieve(building.marketing().adBlurbs());
                    }
                    if (building.contacts().propertyContacts().getMeta().isDetached()) {
                        Persistence.service().retrieve(building.contacts().propertyContacts());
                    }
                    buildingRS = Converter.convertBuilding(building);
                    buildingRS.unitCount = 0;
                    buildingsRS.buildings.add(buildingRS);
                    exportBuildingInfo = true;
                } else {
                    buildingRS = complexes.get(building.complex());
                    if (buildingRS == null) {
                        // Just in case if somebody forgets to set Primary building 
                        if (building.marketing().adBlurbs().getMeta().isDetached()) {
                            Persistence.service().retrieve(building.marketing().adBlurbs());
                        }
                        if (building.contacts().propertyContacts().getMeta().isDetached()) {
                            Persistence.service().retrieve(building.contacts().propertyContacts());
                        }
                        buildingRS = Converter.convertBuilding(building);

                        buildingsRS.buildings.add(buildingRS);
                        complexes.put(building.complex(), buildingRS);

                        buildingRS.unitCount = 0;
                    }
                    if (building.complexPrimary().isBooleanTrue()) {
                        exportBuildingInfo = true;
                        if (building.marketing().adBlurbs().getMeta().isDetached()) {
                            Persistence.service().retrieve(building.marketing().adBlurbs());
                        }
                        if (building.contacts().propertyContacts().getMeta().isDetached()) {
                            Persistence.service().retrieve(building.contacts().propertyContacts());
                        }
                        Converter.copyDBOtoRS(building, buildingRS);
                    }
                }

                if (exportBuildingInfo) {
                    //Get Amenity
                    {
                        EntityQueryCriteria<BuildingAmenity> criteria = EntityQueryCriteria.create(BuildingAmenity.class);
                        criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
                        for (BuildingAmenity amenity : Persistence.service().query(criteria)) {
                            buildingRS.amenities.add(Converter.convertBuildingAmenity(amenity));
                        }
                    }
                    //Parking
                    {
                        EntityQueryCriteria<Parking> criteria = EntityQueryCriteria.create(Parking.class);
                        criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
                        for (Parking i : Persistence.service().query(criteria)) {
                            buildingRS.parkings.add(Converter.convertParking(i));
                        }
                    }
                    if (!building.media().isEmpty()) {
                        Persistence.service().retrieve(building.media());
                        for (Media media : building.media()) {
                            if (PublicVisibilityType.global.equals(media.visibility().getValue())) {
                                buildingRS.medias.add(Converter.convertMedia(media));
                            }
                        }
                    }

                    {
                        // TODO: get those utilities from building add-ons list?
//                        Persistence.service().retrieve(building.productCatalog());
//                        for (ProductItemType utility : building.productCatalog().includedUtilities()) {
//                            buildingRS.includedUtilities.add(Converter.convertBuildingIncludedUtility(utility));
//                        }
                    }
                }

                EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
                floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
                List<Floorplan> floorplans = service.query(floorplanCriteria);
                nextFloorplan: for (Floorplan floorplan : floorplans) {
                    FloorplanRS floorplanRS = Converter.convertFloorplan(floorplan);
                    floorplanRS.unitCount = 0;

                    // Count Units and find earliest availableFrom
                    {
                        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                        criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
                        for (AptUnit u : Persistence.service().query(criteria)) {
                            floorplanRS.unitCount++;

                            floorplanRS.rentFrom = DomainUtil.min(floorplanRS.rentFrom, u.financial()._marketRent().getValue());
                            floorplanRS.rentTo = DomainUtil.max(floorplanRS.rentTo, u.financial()._marketRent().getValue());
                            floorplanRS.sqftFrom = DomainUtil.min(floorplanRS.sqftFrom, DomainUtil.getAreaInSqFeet(u.info().area(), u.info().areaUnits()));
                            floorplanRS.sqftTo = DomainUtil.max(floorplanRS.sqftTo, DomainUtil.getAreaInSqFeet(u.info().area(), u.info().areaUnits()));

                            if (!u._availableForRent().isNull()) {
                                if ((floorplanRS.availableFrom == null) || (floorplanRS.availableFrom.after(u._availableForRent().getValue()))) {
                                    floorplanRS.availableFrom = u._availableForRent().getValue();
                                }
                            }
                        }
                    }

                    //From all floorplans matched by beds/baths, We select 1 with closest availability and we show it on the site
                    FloorplanRS floorplanSameRS = findSameFloorplan(buildingRS.floorplans, Converter.convertFloorplan(floorplan));
                    if (floorplanSameRS != null) {
                        if (afterOrNull(floorplanRS.availableFrom, floorplanSameRS.availableFrom)) {
                            // Ignore this floorplanRS
                            floorplanSameRS.unitCount += floorplanRS.unitCount;
                            floorplanSameRS.rentFrom = DomainUtil.min(floorplanSameRS.rentFrom, floorplanRS.rentFrom);
                            floorplanSameRS.rentTo = DomainUtil.max(floorplanSameRS.rentTo, floorplanRS.rentTo);
                            floorplanSameRS.sqftFrom = DomainUtil.min(floorplanSameRS.sqftFrom, floorplanRS.sqftFrom);
                            floorplanSameRS.sqftTo = DomainUtil.max(floorplanSameRS.sqftTo, floorplanRS.sqftTo);
                            continue nextFloorplan;
                        }
                        floorplanRS.unitCount += floorplanSameRS.unitCount;
                        floorplanRS.rentFrom = DomainUtil.min(floorplanSameRS.rentFrom, floorplanRS.rentFrom);
                        floorplanRS.rentTo = DomainUtil.max(floorplanSameRS.rentTo, floorplanRS.rentTo);
                        floorplanRS.sqftFrom = DomainUtil.min(floorplanSameRS.sqftFrom, floorplanRS.sqftFrom);
                        floorplanRS.sqftTo = DomainUtil.max(floorplanSameRS.sqftTo, floorplanRS.sqftTo);

                        buildingRS.floorplans.remove(floorplanSameRS);
                    }
                    buildingRS.floorplans.add(floorplanRS);

                    //Get Amenity
                    {
                        EntityQueryCriteria<FloorplanAmenity> criteria = EntityQueryCriteria.create(FloorplanAmenity.class);
                        criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
                        for (FloorplanAmenity amenity : Persistence.service().query(criteria)) {
                            floorplanRS.amenities.add(Converter.convertFloorplanAmenity(amenity));
                        }
                    }
                    if (!floorplan.media().isEmpty()) {
                        Persistence.service().retrieve(floorplan.media());
                        for (Media media : floorplan.media()) {
                            if (PublicVisibilityType.global.equals(media.visibility().getValue())) {
                                floorplanRS.medias.add(Converter.convertMedia(media));
                            }
                        }
                    }

                }
            } catch (Throwable t) {
                log.error("Error converting building {}", building, t);
                if (ServerSideConfiguration.instance().isDevelopmentBehavior()) {
                    throw new Error("Internal error", t);
                } else {
                    throw new Error("Internal error");
                }
            }
        }

        for (BuildingRS buildingRS : buildingsRS.buildings) {
            for (FloorplanRS floorplanRS : buildingRS.floorplans) {
                buildingRS.unitCount += floorplanRS.unitCount;
                buildingRS.rentFrom = DomainUtil.min(buildingRS.rentFrom, floorplanRS.rentFrom);
                buildingRS.rentTo = DomainUtil.max(buildingRS.rentTo, floorplanRS.rentTo);
                buildingRS.sqftFrom = DomainUtil.min(buildingRS.sqftFrom, floorplanRS.sqftFrom);
                buildingRS.sqftTo = DomainUtil.max(buildingRS.sqftTo, floorplanRS.sqftTo);
            }
        }

        log.info("BuildingRS Retrieve time {} msec", TimeUtils.since(start));

        return buildingsRS;
    }

    private FloorplanRS findSameFloorplan(List<FloorplanRS> floorplans, FloorplanRS convertedFloorplan) {
        for (FloorplanRS floorplanRS : floorplans) {
            if (equalsInteger(floorplanRS.bedrooms, convertedFloorplan.bedrooms) && equalsInteger(floorplanRS.dens, convertedFloorplan.dens)
                    && equalsInteger(floorplanRS.bathrooms, convertedFloorplan.bathrooms)) {
                return floorplanRS;
            }
        }
        // Not found
        return null;
    }

    private boolean afterOrNull(LogicalDate availableFromNew, LogicalDate availableFromPrev) {
        if (availableFromPrev == null) {
            return (availableFromNew == null);
        } else if (availableFromNew == null) {
            return true;
        } else {
            return availableFromNew.after(availableFromPrev);
        }
    }

    boolean equalsInteger(Integer value1, Integer value2) {
        if (value1 == null) {
            value1 = Integer.valueOf(0);
        }
        if (value2 == null) {
            value2 = Integer.valueOf(0);
        }
        return value1.equals(value2);
    }

}

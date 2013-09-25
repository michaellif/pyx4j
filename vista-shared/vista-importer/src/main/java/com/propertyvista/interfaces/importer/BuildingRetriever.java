/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.interfaces.importer.converter.AptUnitConverter;
import com.propertyvista.interfaces.importer.converter.AptUnitOccupancyConverter;
import com.propertyvista.interfaces.importer.converter.BuildingAmenityConverter;
import com.propertyvista.interfaces.importer.converter.BuildingConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanAmenityConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanConverter;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.converter.MediaConverter;
import com.propertyvista.interfaces.importer.converter.ParkingConverter;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.AptUnitOccupancyIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public class BuildingRetriever {

    private static MediaConfig buildingConfig = new MediaConfig();

    public BuildingIO getModel(Building building, MediaConfig mediaConfig) {

        buildingConfig = mediaConfig;
        buildingConfig.directory = "";

        MediaConfig floorplanConfig = new MediaConfig();

        if (building.contacts().organizationContacts().getMeta().isDetached()) {
            Persistence.service().retrieve(building.contacts().organizationContacts());
        }
        if (building.marketing().adBlurbs().getMeta().isDetached()) {
            Persistence.service().retrieve(building.marketing().adBlurbs());
        }
        if (building.contacts().propertyContacts().getMeta().isDetached()) {
            Persistence.service().retrieve(building.contacts().propertyContacts());
        }

        BuildingIO buildingIO = new BuildingConverter().createTO(building);
        if (!buildingIO.propertyCode().isNull()) {
            buildingConfig.directory = buildingIO.propertyCode().getStringView() + "/";
        }

        //Get Amenity
        {
            Persistence.service().retrieveMember(building.amenities());
            for (BuildingAmenity amenity : building.amenities()) {
                buildingIO.amenities().add(new BuildingAmenityConverter().createTO(amenity));
            }
        }

        Persistence.service().retrieve(building.media());
        for (Media media : building.media()) {
            buildingIO.medias().add(new MediaConverter(buildingConfig, ImageTarget.Building).createTO(media));
        }

        //TODO
        //IList<ContactIO> contacts();

        EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
        floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
        floorplanCriteria.asc(floorplanCriteria.proto().id());
        List<Floorplan> floorplans = Persistence.service().query(floorplanCriteria);
        for (Floorplan floorplan : floorplans) {
            FloorplanIO floorplanIO = new FloorplanConverter().createTO(floorplan);
            buildingIO.floorplans().add(floorplanIO);

            //Get Amenity
            {
                Persistence.service().retrieveMember(floorplan.amenities());
                for (FloorplanAmenity amenity : floorplan.amenities()) {
                    floorplanIO.amenities().add(new FloorplanAmenityConverter().createTO(amenity));
                }
            }

            // Count Units
            {
                EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
                criteria.asc(criteria.proto().info().number());
                for (AptUnit unit : Persistence.service().query(criteria)) {
                    AptUnitIO aptUnitIO = new AptUnitConverter().createTO(unit);
                    floorplanIO.units().add(aptUnitIO);

                    //Get Occupancy

                    EntityQueryCriteria<AptUnitOccupancySegment> occupancyCriteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
                    occupancyCriteria.add(PropertyCriterion.eq(occupancyCriteria.proto().unit(), unit));
                    for (AptUnitOccupancySegment occupancy : Persistence.service().query(occupancyCriteria)) {
                        AptUnitOccupancyIO occupancyIO = new AptUnitOccupancyConverter().createTO(occupancy);
                        aptUnitIO.AptUnitOccupancySegment().add(occupancyIO);
                    }
                }
            }

            floorplanConfig.baseFolder = buildingConfig.baseFolder;
            floorplanConfig.directory = buildingConfig.directory + "floorplans/" + floorplan.name().getStringView() + "/";

            Persistence.service().retrieve(floorplan.media());
            for (Media media : floorplan.media()) {
                floorplanIO.medias().add(new MediaConverter(floorplanConfig, ImageTarget.Floorplan).createTO(media));
            }

        }

        //Parking
        {
            EntityQueryCriteria<Parking> criteria = EntityQueryCriteria.create(Parking.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
            for (Parking i : Persistence.service().query(criteria)) {
                buildingIO.parkings().add(new ParkingConverter().createTO(i));
            }
        }

        // Other Data
        {
            EntityQueryCriteria<Elevator> criteria = EntityQueryCriteria.create(Elevator.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
            for (Elevator i : Persistence.service().query(criteria)) {
                buildingIO.elevators().add(strip(i));
            }
        }

        {
            EntityQueryCriteria<Boiler> criteria = EntityQueryCriteria.create(Boiler.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
            for (Boiler i : Persistence.service().query(criteria)) {
                buildingIO.boilers().add(strip(i));
            }
        }

        {
            EntityQueryCriteria<Roof> criteria = EntityQueryCriteria.create(Roof.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
            for (Roof i : Persistence.service().query(criteria)) {
                buildingIO.roofs().add(strip(i));
            }
        }

        {
            EntityQueryCriteria<LockerArea> criteria = EntityQueryCriteria.create(LockerArea.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
            for (LockerArea i : Persistence.service().query(criteria)) {
                buildingIO.lockerAreas().add(strip(i));
            }
        }

        return buildingIO;
    }

    private <T extends IEntity> T strip(T entity) {
        entity.removeMemberValue("id");
        entity.removeMemberValue("belongsTo");
        return entity;
    }
}

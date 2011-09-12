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
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.interfaces.importer.converter.AptUnitConverter;
import com.propertyvista.interfaces.importer.converter.BuildingAmenityConverter;
import com.propertyvista.interfaces.importer.converter.BuildingConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanAmenityConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanConverter;
import com.propertyvista.interfaces.importer.converter.MediaConverter;
import com.propertyvista.interfaces.importer.converter.ParkingConverter;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;

public class BuildingRetriever {

    public BuildingIO getModel(Building building, String imagesBaseFolder) {

        if (building.contacts().contacts().getMeta().isDetached()) {
            Persistence.service().retrieve(building.contacts().contacts());
        }
        if (building.marketing().adBlurbs().getMeta().isDetached()) {
            Persistence.service().retrieve(building.marketing().adBlurbs());
        }

        BuildingIO buildingIO = new BuildingConverter().createDTO(building);

        //Get Amenity
        {
            EntityQueryCriteria<BuildingAmenity> criteria = EntityQueryCriteria.create(BuildingAmenity.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), building));
            for (BuildingAmenity amenity : Persistence.service().query(criteria)) {
                buildingIO.amenities().add(new BuildingAmenityConverter().createDTO(amenity));
            }
        }
        //Parking
        {
            EntityQueryCriteria<Parking> criteria = EntityQueryCriteria.create(Parking.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), building));
            for (Parking i : Persistence.service().query(criteria)) {
                buildingIO.parkings().add(new ParkingConverter().createDTO(i));
            }
        }

        Persistence.service().retrieve(building.media());
        for (Media media : building.media()) {
            buildingIO.medias().add(new MediaConverter(imagesBaseFolder).createDTO(media));
        }

        //TODO
        //IList<ContactIO> contacts();

        EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
        floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
        List<Floorplan> floorplans = Persistence.service().query(floorplanCriteria);
        for (Floorplan floorplan : floorplans) {
            FloorplanIO floorplanIO = new FloorplanConverter().createDTO(floorplan);
            buildingIO.floorplans().add(floorplanIO);

            //Get Amenity
            {
                EntityQueryCriteria<FloorplanAmenity> criteria = EntityQueryCriteria.create(FloorplanAmenity.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), floorplan));
                for (FloorplanAmenity amenity : Persistence.service().query(criteria)) {
                    floorplanIO.amenities().add(new FloorplanAmenityConverter().createDTO(amenity));
                }
            }

            // Count Units
            {
                EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
                for (AptUnit u : Persistence.service().query(criteria)) {
                    floorplanIO.units().add(new AptUnitConverter().createDTO(u));
                }
            }

            Persistence.service().retrieve(floorplan.media());
            for (Media media : floorplan.media()) {
                floorplanIO.medias().add(new MediaConverter(imagesBaseFolder).createDTO(media));
            }

        }

        return buildingIO;
    }
}

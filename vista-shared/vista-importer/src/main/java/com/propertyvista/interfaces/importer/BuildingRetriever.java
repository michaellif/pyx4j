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

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.interfaces.importer.converter.AddressConverter;
import com.propertyvista.interfaces.importer.converter.AptUnitConverter;
import com.propertyvista.interfaces.importer.converter.BuildingAmenityConverter;
import com.propertyvista.interfaces.importer.converter.BuildingConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanAmenityConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanConverter;
import com.propertyvista.interfaces.importer.converter.MediaConverter;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;

public class BuildingRetriever {

    public BuildingIO getModel(Building building) {

        BuildingIO buildingIO = new BuildingConverter().createDTO(building);
        new AddressConverter().copyDBOtoDTO(building.info().address(), buildingIO.address());

        //Get Amenity
        {
            EntityQueryCriteria<BuildingAmenity> criteria = EntityQueryCriteria.create(BuildingAmenity.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), building));
            for (BuildingAmenity amenity : PersistenceServicesFactory.getPersistenceService().query(criteria)) {
                buildingIO.amenities().add(new BuildingAmenityConverter().createDTO(amenity));
            }
        }

        PersistenceServicesFactory.getPersistenceService().retrieve(building.media());
        for (Media media : building.media()) {
            buildingIO.medias().add(new MediaConverter().createDTO(media));
        }

        //TODO
        //IList<ContactIO> contacts();
        //IList<ParkingIO> parkings();

        EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
        floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
        List<Floorplan> floorplans = PersistenceServicesFactory.getPersistenceService().query(floorplanCriteria);
        for (Floorplan floorplan : floorplans) {
            FloorplanIO floorplanIO = new FloorplanConverter().createDTO(floorplan);

            //Get Amenity
            {
                EntityQueryCriteria<FloorplanAmenity> criteria = EntityQueryCriteria.create(FloorplanAmenity.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), floorplan));
                for (FloorplanAmenity amenity : PersistenceServicesFactory.getPersistenceService().query(criteria)) {
                    floorplanIO.amenities().add(new FloorplanAmenityConverter().createDTO(amenity));
                }
            }

            // Count Units and get stats
            {
                EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
                for (AptUnit u : PersistenceServicesFactory.getPersistenceService().query(criteria)) {
                    floorplanIO.units().add(new AptUnitConverter().createDTO(u));
                }
            }

            PersistenceServicesFactory.getPersistenceService().retrieve(floorplan.media());
            for (Media media : floorplan.media()) {
                floorplanIO.medias().add(new MediaConverter().createDTO(media));
            }

        }

        return buildingIO;
    }
}

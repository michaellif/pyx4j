/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.interfaces.importer.converter.AptUnitConverter;
import com.propertyvista.interfaces.importer.converter.FloorplanAmenityConverter;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.converter.MediaConverter;
import com.propertyvista.interfaces.importer.model.AmenityIO;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;
import com.propertyvista.interfaces.importer.model.MediaIO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public class BuildingImporter extends ImportPersister {

    private static I18n i18n = I18n.get(BuildingImporter.class);

    private final static Logger log = LoggerFactory.getLogger(BuildingImporter.class);

    public List<String> verify(BuildingIO buildingIO, MediaConfig mediaConfig) {
        List<String> messages = new Vector<String>();
        // Set defaults
        if (buildingIO.type().isNull()) {
            buildingIO.type().setValue(BuildingInfo.Type.residential);
        }

        if (buildingIO.propertyCode().isNull()) {
            messages.add("propertyCode can't be empty");
        }

        // Media
        {
            for (MediaIO iIO : buildingIO.medias()) {
                String m = new MediaConverter(mediaConfig, ImageTarget.Building).verify(iIO);
                if (m != null) {
                    messages.add("Building '" + buildingIO.propertyCode().getValue() + "' " + m);
                }
            }
        }

        //    Floorplan
        {
            for (FloorplanIO floorplanIO : buildingIO.floorplans()) {
                if (floorplanIO.name().isNull()) {
                    messages.add("Floorplan name in building '" + buildingIO.propertyCode().getValue() + "' can't be empty");
                }

                // Media
                {
                    for (MediaIO iIO : floorplanIO.medias()) {
                        String m = new MediaConverter(mediaConfig, ImageTarget.Floorplan).verify(iIO);
                        if (m != null) {
                            messages.add("Floorplan '" + floorplanIO.name().getValue() + "' in building '" + buildingIO.propertyCode().getValue() + "' " + m);
                        }
                    }
                }
                //Units
                {
                    for (AptUnitIO iIO : floorplanIO.units()) {
                        if (iIO.number().isNull()) {
                            messages.add("AptUnit number in '" + floorplanIO.name().getValue() + "' in building '" + buildingIO.propertyCode().getValue()
                                    + "' can't be empty");
                        }
                    }
                }

            }
        }
        return messages;
    }

    public ImportCounters persist(BuildingIO buildingIO, MediaConfig mediaConfig) {

        ImportCounters counters = new ImportCounters();
        counters.buildings++;

        if (buildingIO.propertyCode().isNull()) {
            throw new UserRuntimeException("propertyCode can't be empty");
        }
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
            List<Building> buildings = Persistence.service().query(criteria);
            if (buildings.size() != 0) {
                throw new UserRuntimeException("Building '" + buildingIO.propertyCode().getValue() + "' already exists");
            }
        }

        // Save building
        Building building = createBuilding(buildingIO, mediaConfig);

        //Floorplan
        {
            for (FloorplanIO floorplanIO : buildingIO.floorplans()) {
                Floorplan floorplan = createFloorplan(floorplanIO, building, mediaConfig);

                counters.floorplans += 1;

                //FloorplanAmenity
                {
                    List<FloorplanAmenity> items = new Vector<FloorplanAmenity>();
                    for (AmenityIO iIO : buildingIO.amenities()) {
                        FloorplanAmenity i = new FloorplanAmenityConverter().createDBO(iIO);
                        i.belongsTo().set(floorplan);
                        items.add(i);
                    }
                    Persistence.service().persist(items);
                }

                //Units
                {
                    List<AptUnit> items = new Vector<AptUnit>();
                    for (AptUnitIO aptUnitIO : floorplanIO.units()) {
                        if (aptUnitIO.number().isNull()) {
                            throw new UserRuntimeException("AptUnit number in '" + floorplanIO.name().getValue() + "' in building '"
                                    + buildingIO.propertyCode().getValue() + "' can't be empty");
                        }

                        {
                            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                            criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
                            criteria.add(PropertyCriterion.eq(criteria.proto().info().number(), aptUnitIO.number().getValue()));
                            List<AptUnit> units = Persistence.service().query(criteria);
                            if (units.size() != 0) {
                                throw new UserRuntimeException("AptUnit '" + aptUnitIO.number().getValue() + "' in '" + floorplanIO.name().getValue()
                                        + "' in '" + buildingIO.propertyCode().getValue() + "' already exists");
                            }
                        }

                        AptUnit i = new AptUnitConverter().createDBO(aptUnitIO);
                        i.belongsTo().set(building);
                        i.floorplan().set(floorplan);
                        items.add(i);
                    }
                    Persistence.service().merge(items);
                    counters.units += items.size();
                }

            }
        }
        return counters;
    }
}

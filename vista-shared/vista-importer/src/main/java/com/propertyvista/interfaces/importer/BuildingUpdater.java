/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;

public class BuildingUpdater {

    public ImportCounters update(BuildingIO buildingIO, String imagesBaseFolder) {
        ImportCounters counters = new ImportCounters();
        Building building;
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().propertyCode(), buildingIO.propertyCode().getValue()));
            List<Building> buildings = Persistence.service().query(criteria);
            if (buildings.size() == 0) {
                throw new UserRuntimeException("Building '" + buildingIO.propertyCode().getValue() + "' not found");
            } else if (buildings.size() > 1) {
                throw new UserRuntimeException("More then one building '" + buildingIO.propertyCode().getValue() + "' found");
            } else {
                building = buildings.get(0);
            }
        }

        for (FloorplanIO floorplanIO : buildingIO.floorplans()) {
            Floorplan floorplan;
            {
                EntityQueryCriteria<Floorplan> criteria = EntityQueryCriteria.create(Floorplan.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
                criteria.add(PropertyCriterion.eq(criteria.proto().name(), floorplanIO.name().getValue()));
                List<Floorplan> floorplans = Persistence.service().query(criteria);
                if (floorplans.size() == 0) {
                    throw new UserRuntimeException("Floorplan '" + floorplanIO.name().getValue() + "' in  building '" + buildingIO.propertyCode().getValue()
                            + "' not found");
                } else if (floorplans.size() > 1) {
                    throw new UserRuntimeException("More then one Floorplan '" + floorplanIO.name().getValue() + "' in  building '"
                            + buildingIO.propertyCode().getValue() + "' found");
                } else {
                    floorplan = floorplans.get(0);
                }
            }
            counters.floorplans += 1;

            for (AptUnitIO aptUnitIO : floorplanIO.units()) {
                EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
                criteria.add(PropertyCriterion.eq(criteria.proto().info().number(), aptUnitIO.number().getValue()));
                List<AptUnit> units = Persistence.service().query(criteria);

                AptUnit unit;
                if (units.size() == 0) {
                    throw new UserRuntimeException("AptUnit '" + aptUnitIO.number().getValue() + "' in '" + floorplanIO.name().getValue() + "' in '"
                            + buildingIO.propertyCode().getValue() + "' not found");
                } else if (units.size() > 1) {
                    throw new UserRuntimeException("More then one AptUnit '" + aptUnitIO.number().getValue() + "' in '" + floorplanIO.name().getValue()
                            + "' in '" + buildingIO.propertyCode().getValue() + "' found");
                } else {
                    unit = units.get(0);
                }

                // Update
                if (!unit.availableForRent().equals(aptUnitIO.availableForRent())) {
                    unit.availableForRent().setValue(aptUnitIO.availableForRent().getValue());
                    Persistence.service().persist(unit);
                    counters.units += 1;
                    counters.buildings = 1;
                }

            }
        }
        return counters;
    }
}

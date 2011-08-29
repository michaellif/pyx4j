/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.merger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo;
import com.propertyvista.server.common.generator.Model;

public class UnitsMerger {
    private final static Logger log = LoggerFactory.getLogger(UnitsMerger.class);

    // loaded from the database, use this for look up by info.propertyCode.getValue
    private final Map<String, Building> buildingsByCode = new HashMap<String, Building>();

    // loaded from the database, look up by building.id
    private final Map<Key, Building> buildingsById = new HashMap<Key, Building>();

    /**
     * Merge two lists, one is what comes to us form an external system - imported
     * The other - existing, is what we have in our database
     * 
     * We will go through the list of imported items, trying to find a corresponding existing entry.
     * If this entry does not exist, we will include imported item into the merge list as is
     * If existing entry exists, we will apply imported data onto existing item and return modified existing item in the merge list
     * 
     * @param importedList
     *            Imported from an external system
     * @param existingList
     *            Existing in our database
     * @return Merged list
     */
    public Model merge(Model imported, Model existing) {
        Model merged = new Model();

        // cache buildings for lookup
        for (Building building : existing.getBuildings()) {
            buildingsByCode.put(building.propertyCode().getValue(), building);
            buildingsById.put(building.id().getValue(), building);
        }

        List<AptUnit> mergedUnits = merge(imported.getAptUnits(), existing.getAptUnits());
        merged.getAptUnits().addAll(mergedUnits);

        return merged;
    }

    private List<AptUnit> merge(List<AptUnit> importedList, List<AptUnit> existingList) {
        List<AptUnit> merged = new ArrayList<AptUnit>();
        for (AptUnit imported : importedList) {

            // try finding the same unit in existing list
            AptUnit existing = null;
            String importedName = imported.info().number().getValue();
            String importedPropertyCode = imported.belongsTo().propertyCode().getValue();
            Building building = buildingsByCode.get(importedPropertyCode);
            if (building == null) {
                log.warn("Downloaded unit references building {} that does not exist locally", importedPropertyCode);
                continue;
            }

            for (AptUnit unit : existingList) {
                Building existingBuilding = buildingsById.get(unit.belongsTo().id().getValue());
                String existingPropertyCode = existingBuilding.propertyCode().getValue();

                // first check that the buildings are the same
                log.debug("Comparing building codes {} and {}", importedPropertyCode, existingPropertyCode);
                if (importedPropertyCode.equals(existingPropertyCode)) {
                    log.debug("Found proper building {}", importedPropertyCode);

                    // for now this code is not yet implemented
                    String existingName = unit.info().number().getValue();
                    if (importedName.equals(existingName)) {
                        existing = unit;
                        break;
                    }
                }
            }

            imported.belongsTo().set(building);
            if (existing == null) {
                log.info("Downloaded new unit {} will be added to local database", importedName);
                merged.add(imported);
            } else {
                log.info("Downloaded unit {} already exists in local database, will be merged", importedName);
                merge(imported, existing);
                merged.add(existing);
            }
        }
        return merged;
    }

    private void merge(AptUnit imported, AptUnit existing) {

        merge(imported.info(), existing.info());

        // marketing
        existing.marketing().name().setValue(imported.marketing().name().getValue());

        // financial
        existing.financial().unitRent().setValue(imported.financial().unitRent().getValue());
        existing.financial().marketRent().setValue(imported.financial().marketRent().getValue());
    }

    private void merge(AptUnitInfo imported, AptUnitInfo existing) {
        existing.number().setValue(imported.number().getValue());
        existing._bedrooms().setValue(imported._bedrooms().getValue());
        existing._bathrooms().setValue(imported._bathrooms().getValue());
        existing.area().setValue(imported.area().getValue());
        existing.areaUnits().setValue(imported.areaUnits().getValue());
        existing.economicStatus().setValue(imported.economicStatus().getValue());
    }
}

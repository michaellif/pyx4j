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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo;

public class UnitsMerger {
    private final static Logger log = LoggerFactory.getLogger(UnitsMerger.class);

    // loaded from the database, use this for look up by info.propertyCode.getValue
    private final Map<String, Building> buildingsByCode = new HashMap<String, Building>();

    // loaded from the database, look up by building.id
    private final Map<Key, Building> buildingsById = new HashMap<Key, Building>();

    public List<AptUnit> merge(Building building, List<AptUnit> importedList, List<AptUnit> existingList) {
        Set<AptUnit> merged = new HashSet<AptUnit>();
        merged.addAll(existingList);

        Map<String, AptUnit> existingUnitsByNumber = unitsByNumber(existingList);

        for (AptUnit imported : importedList) {
            try {
                AptUnit existing = existingUnitsByNumber.get(imported.info().number().getValue());
                if (existing == null) {
                    imported.building().set(building);
                }

                Floorplan floorplan = getExistingFloorplan(building, imported.floorplan());
                if (floorplan == null) {
                    imported.floorplan().building().set(building);
                    Persistence.service().persist(imported.floorplan());

                    building.floorplans().add(imported.floorplan());
                    Persistence.service().persist(building);
                }

                //set existing floorplan for new unit
                if (existing == null && floorplan != null) {
                    imported.floorplan().set(floorplan);
                }

                merged.add(existing != null ? merge(imported, existing) : imported);
            } catch (Exception e) {
                log.error(String.format("Error during imported unit %s merging", imported.info().number().getValue()), e);
            }
        }

        return new ArrayList<AptUnit>(merged);
    }

    private Floorplan getExistingFloorplan(Building building, Floorplan imported) {
        Iterator<Floorplan> floorplanIterator = building.floorplans().iterator();
        while (floorplanIterator.hasNext()) {
            Floorplan existing = floorplanIterator.next();
            if (StringUtils.equals(existing.name().getValue(), imported.name().getValue())) {
                return existing;
            }
        }
        return null;
    }

    private Map<String, AptUnit> unitsByNumber(List<AptUnit> existingList) {
        Map<String, AptUnit> unitsByNumber = new HashMap<String, AptUnit>();
        for (AptUnit unit : existingList) {
            unitsByNumber.put(unit.info().number().getValue(), unit);
        }
        return unitsByNumber;
    }

    private AptUnit merge(AptUnit imported, AptUnit existing) {

        //info
        merge(imported.info(), existing.info());

        //floorplan
        //merge(imported.floorplan(), existing.floorplan());

        // marketing
        Persistence.service().retrieve(existing.marketing());
        existing.marketing().name().setValue(imported.marketing().name().getValue());

        // financial
        existing.financial()._unitRent().setValue(imported.financial()._unitRent().getValue());
        existing.financial()._marketRent().setValue(imported.financial()._marketRent().getValue());

        return existing;
    }

    private void merge(Floorplan imported, Floorplan existing) {
        existing.name().setValue(imported.name().getValue());
        existing.bedrooms().setValue(imported.bedrooms().getValue());
        existing.bathrooms().setValue(imported.bathrooms().getValue());
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

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo;

public class UnitsMerger {

    private final static Logger log = LoggerFactory.getLogger(UnitsMerger.class);

    public List<AptUnit> merge(Building building, List<AptUnit> importedList, List<AptUnit> existingList) {
        List<AptUnit> mergedList = new ArrayList<AptUnit>();

        Map<String, AptUnit> existingUnitsByNumber = unitsByNumber(existingList);

        for (AptUnit imported : importedList) {
            boolean sucsess = false;
            try {
                AptUnit merged;
                // merge unit data
                AptUnit existing = existingUnitsByNumber.get(imported.info().number().getValue());
                merged = merge(building, imported, existing);

                mergedList.add(merged);
                sucsess = true;
            } finally {
                if (!sucsess) {
                    log.error("Error during imported unit {} merging", imported.info().number().getValue());
                }
            }
        }

        return mergedList;
    }

    private Floorplan getExistingFloorplan(Building building, Floorplan imported) {
        Iterator<Floorplan> floorplanIterator = building.floorplans().iterator();
        while (floorplanIterator.hasNext()) {
            Floorplan existing = floorplanIterator.next();
            Persistence.service().retrieve(existing);
            Persistence.service().retrieve(imported);
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

    public AptUnit merge(Building building, AptUnit imported, AptUnit existing) {
        AptUnit merged = EntityFactory.create(AptUnit.class);
        if (existing == null) {
            merged = imported;
        } else {

            //info
            merge(imported.info(), existing.info());

            // marketing
            Persistence.service().retrieve(existing.marketing());
            existing.marketing().name().setValue(imported.marketing().name().getValue());

            // financial
            existing.financial()._unitRent().setValue(imported.financial()._unitRent().getValue());
            existing.financial()._marketRent().setValue(imported.financial()._marketRent().getValue());
            merged = existing;
        }
        merged.building().set(building);

        // merge floorplan
        Floorplan floorplanExisting = getExistingFloorplan(building, merged.floorplan());
        if (floorplanExisting != null) {
            merged.floorplan().set(floorplanExisting);
        } else {
            //set floorplan for new unit
            imported.floorplan().building().set(building);
            Persistence.service().persist(imported.floorplan());

            building.floorplans().add(imported.floorplan());

            merged.floorplan().set(imported.floorplan());
        }

        return merged;
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

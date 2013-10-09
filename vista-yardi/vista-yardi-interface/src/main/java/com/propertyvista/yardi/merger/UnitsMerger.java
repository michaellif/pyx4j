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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;

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

    private Map<String, AptUnit> unitsByNumber(List<AptUnit> existingList) {
        Map<String, AptUnit> unitsByNumber = new HashMap<String, AptUnit>();
        for (AptUnit unit : existingList) {
            unitsByNumber.put(unit.info().number().getValue(), unit);
        }
        return unitsByNumber;
    }

    public AptUnit merge(Building building, AptUnit imported, AptUnit existing) {
        AptUnit merged = null;
        if (existing == null) {
            merged = imported;
            merged.building().set(building);
        } else {
            // merge new data into existing
            merged = existing;

            //info
            merge(imported.info(), merged.info());

            // marketing
            Persistence.service().retrieve(merged.marketing());
            merged.marketing().name().setValue(imported.marketing().name().getValue());

            // financial
            merged.financial()._unitRent().setValue(imported.financial()._unitRent().getValue());
            merged.financial()._marketRent().setValue(imported.financial()._marketRent().getValue());
        }

        // merge floorplan
        mergeFloorplan(building, merged);

        return merged;
    }

    private void mergeFloorplan(Building building, AptUnit unit) {
        Floorplan fp = unit.floorplan();
        if (fp.getPrimaryKey() != null) {
            // already there - don't touch it as PV is the origin for marketing data
            log.info("    exising unit, existing floorplan - noop");
            return;
        }

        // new unit - see if the floorplan is in the list
        Persistence.service().retrieveMember(building.floorplans());
        for (Floorplan existing : building.floorplans()) {
            if (StringUtils.equals(existing.name().getValue(), fp.name().getValue())) {
                unit.floorplan().set(existing);
                log.info("    new unit, existing floorplan: {}", fp.name().getValue());
                return;
            }
        }

        // new floorplan - persist
        log.info("    new unit - creating floorplan: {}", fp.name().getValue());
        fp.building().set(building);
        Persistence.service().persist(fp);

        // add to the list
        building.floorplans().add(fp);
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

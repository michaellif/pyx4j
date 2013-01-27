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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;

public class BuildingsMerger {
    private final static Logger log = LoggerFactory.getLogger(BuildingsMerger.class);

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
    public List<Building> merge(List<Building> importedList, List<Building> existingList) {
        Set<Building> merged = new HashSet<Building>();
        merged.addAll(existingList);

        Map<String, Building> existingBuildingsByCode = buildingsByCode(existingList);

        for (Building imported : importedList) {
            try {
                Building existing = existingBuildingsByCode.get(imported.propertyCode().getValue());
                merged.add(existing != null ? merge(imported, existing) : imported);
            } catch (Exception e) {
                log.error(String.format("Error during imported building %s merging", imported.propertyCode().getValue()), e);
            }
        }

        return new ArrayList<Building>(merged);
    }

    private Map<String, Building> buildingsByCode(List<Building> existingList) {
        Map<String, Building> buildingsByCode = new HashMap<String, Building>();
        for (Building building : existingList) {
            buildingsByCode.put(building.propertyCode().getValue(), building);
        }
        return buildingsByCode;
    }

    /**
     * We could make this method generic, by iterating over meta data
     */
    private Building merge(Building imported, Building existing) {

        merge(imported.info(), existing.info());
        merge(imported.marketing(), existing.marketing());

        return existing;
    }

    private void merge(BuildingInfo imported, BuildingInfo existing) {
        merge(imported.address(), existing.address());
    }

    private void merge(AddressStructured imported, AddressStructured existing) {
        existing.streetName().setValue(imported.streetName().getValue());
        existing.streetDirection().setValue(imported.streetDirection().getValue());
        existing.streetName().setValue(imported.streetName().getValue());
        existing.streetType().setValue(imported.streetType().getValue());
        existing.city().setValue(imported.city().getValue());
        existing.province().set(imported.province());
        existing.country().set(imported.country());
        existing.postalCode().setValue(imported.postalCode().getValue());
    }

    private void merge(Marketing imported, Marketing existing) {
        existing.name().setValue(imported.name().getValue());
    }
}

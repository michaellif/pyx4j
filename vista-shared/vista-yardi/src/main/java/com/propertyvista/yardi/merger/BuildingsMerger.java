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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.contact.Address;
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
        List<Building> merged = new ArrayList<Building>();
        for (Building imported : importedList) {
            // try finding the same building in existing list
            Building existing = null;
            for (Building building : existingList) {
                if (building.info().propertyCode().getValue().equals(imported.info().propertyCode().getValue())) {
                    existing = building;
                    break;
                }
            }

            if (existing == null) {
                log.info("Did not find a bulding for property code {}", imported.info().propertyCode().getValue());
                merged.add(imported);
            } else {
                merge(imported, existing);
                merged.add(existing);
            }
        }
        return merged;
    }

    /**
     * We could make this method generic, by iterating over meta data
     */
    public void merge(Building imported, Building existing) {
        merge(imported.info(), existing.info());
        merge(imported.marketing(), existing.marketing());
    }

    public void merge(BuildingInfo imported, BuildingInfo existing) {
        merge(imported.address(), existing.address());
    }

    public void merge(Address imported, Address existing) {
        existing.streetName().setValue(imported.streetName().getValue());
        existing.streetDirection().setValue(imported.streetDirection().getValue());
        existing.streetName().setValue(imported.streetName().getValue());
        existing.city().setValue(imported.city().getValue());
        existing.province().set(imported.province());
        existing.country().set(imported.country());
        existing.postalCode().setValue(imported.postalCode().getValue());
        existing.addressType().setValue(imported.addressType().getValue());
    }

    public void merge(Marketing imported, Marketing existing) {
        existing.name().setValue(imported.name().getValue());
    }
}

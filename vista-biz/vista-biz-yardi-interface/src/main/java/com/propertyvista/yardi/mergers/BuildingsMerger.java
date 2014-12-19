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
 */
package com.propertyvista.yardi.mergers;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;

public class BuildingsMerger {

    public Building merge(Building imported, Building existing) {
        return existing == null ? imported : mergeBuilding(imported, existing);
    }

    private Building mergeBuilding(Building imported, Building existing) {

        merge(imported.info(), existing.info());
        merge(imported.marketing(), existing.marketing());

        return existing;
    }

    private void merge(BuildingInfo imported, BuildingInfo existing) {
        existing.name().setValue(imported.name().getValue());
        merge(imported.address(), existing.address());
    }

    private void merge(InternationalAddress imported, InternationalAddress existing) {
        existing.streetNumber().setValue(imported.streetNumber().getValue());
        existing.streetName().setValue(imported.streetName().getValue());
        existing.suiteNumber().setValue(imported.suiteNumber().getValue());
        existing.city().setValue(imported.city().getValue());
        existing.province().set(imported.province());
        existing.country().set(imported.country());
        existing.postalCode().setValue(imported.postalCode().getValue());
    }

    private void merge(Marketing imported, Marketing existing) {
        existing.name().setValue(imported.name().getValue());
    }
}

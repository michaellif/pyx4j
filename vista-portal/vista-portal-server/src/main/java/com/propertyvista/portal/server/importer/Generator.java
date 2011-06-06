/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 5, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.importer;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.portal.server.generator.BuildingsGenerator;

public class Generator {
    private Model model;

    private BuildingsGenerator generator = new BuildingsGenerator(DemoData.BUILDINGS_GENERATION_SEED);

    public Generator(Model model) {
        this.model = model;
    }

    public void generateMissingData() {
        for (Building building : model.getBuildings()) {
            for (int i = 0; i < 3; i++) {
                BuildingAmenity amenity = generator.createBuildingAmenity(building);
                model.getBuildingAmenities().add(amenity);
            }
        }
    }
}

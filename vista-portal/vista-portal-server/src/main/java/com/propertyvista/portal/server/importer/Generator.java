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

import com.propertvista.generator.BuildingsGenerator;
import com.propertvista.generator.util.CommonsGenerator;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.server.common.generator.Model;

public class Generator {
    private final Model model;

    private final BuildingsGenerator generator = new BuildingsGenerator(DemoData.BUILDINGS_GENERATION_SEED);

    public Generator(Model model) {
        this.model = model;
    }

    public void generateMissingData() {
        for (Building building : model.getBuildings()) {
            for (int i = 0; i < 3; i++) {
                BuildingAmenity amenity = generator.createBuildingAmenity(building);
                model.getBuildingAmenities().add(amenity);
            }
            if (building.marketing().description().isNull()) {
                building.marketing().description().setValue(CommonsGenerator.lipsum());
            }
        }

        for (FloorplanDTO floorplanDTO : model.getFloorplans()) {
            if (floorplanDTO.name().isNull()) {
                floorplanDTO.name().setValue("missing:" + RandomUtil.randomLetters(3));
            }
            if (floorplanDTO.description().isNull()) {
                floorplanDTO.description().setValue(CommonsGenerator.lipsum());
            }
            for (int i = 0; i < 2 + DataGenerator.randomInt(6); i++) {
                FloorplanAmenity amenity = BuildingsGenerator.createFloorplanAmenity();
                amenity.belongsTo().set(floorplanDTO);
                floorplanDTO.amenities().add(amenity);
            }
        }
    }
}

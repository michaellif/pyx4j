/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 15, 2014
 * @author stanp
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.domain.YardiBuilding;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager;

public class YardiBuildingManagerImpl implements YardiBuildingManager {

    @Override
    public BuildingBuilder addDefaultBuilding() {
        YardiBuilding building = EntityFactory.create(YardiBuilding.class);
        building.buildingId().setValue(DEFAULT_PROPERTY_CODE);
        // save
        addBuilding(building);

        return new BuildingBuilderImpl(building) //
                .setAddress(null) //
                .addFloorplan(DEFAULT_FP_NAME, DEFAULT_FP_BATHS, DEFAULT_FP_BEDS) //
                .addUnit(DEFAULT_UNIT_NO, DEFAULT_FP_NAME, DEFAULT_UNIT_RENT, null);
    }

    @Override
    public BuildingBuilder addBuilding(String propertyId) {
        assert propertyId != null : "property id cannot be null";

        YardiBuilding building = EntityFactory.create(YardiBuilding.class);
        building.buildingId().setValue(propertyId);

        addBuilding(building);

        return new BuildingBuilderImpl(building);
    }

    @Override
    public BuildingBuilder getBuilding(String propertyId) {
        assert propertyId != null : "property id cannot be null";

        YardiBuilding building = YardiMockModelUtils.findBuilding(propertyId);
        if (building == null) {
            throw new Error("Building not found: " + propertyId);
        }
        return new BuildingBuilderImpl(building);
    }

    private void addBuilding(YardiBuilding building) {
        assert building != null : "building cannot be null";

        YardiMock.server().getModel().getBuildings().add(building);
    }
}

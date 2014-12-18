/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-16
 * @author vlads
 */
package com.propertyvista.interfaces.importer.oapi.base;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.interfaces.importer.model.BuildingModel;

public class BuildingProcessor {

    private final static Logger log = LoggerFactory.getLogger(BuildingProcessor.class);

    public List<Building> process(List<BuildingModel> model) {

        return getBuildings(model);
    }

    private List<Building> getBuildings(List<BuildingModel> model) {
        List<Building> buildings = new ArrayList<Building>(model.size());
        for (BuildingModel entityModel : model) {
            buildings.add(getEntity(entityModel));
        }

        return buildings;
    }

    private Building getEntity(BuildingModel entityModel) {

        Building building = EntityFactory.create(Building.class);
        building.propertyCode().set(entityModel.property());
        building.info().name().set(entityModel.name());
        building.info().address().streetNumber().set(entityModel.streetNumber());
        building.info().address().streetName().set(entityModel.streetName());
        building.info().address().city().set(entityModel.city());
        building.info().address().province().set(entityModel.province());
        building.info().address().country().setValue(ISOCountry.valueOf(entityModel.country().getValue()));
        building.info().address().postalCode().setValue(entityModel.postalCode().getValue());

        return building;
    }

}

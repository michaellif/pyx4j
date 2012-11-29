/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 29, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.oapi.model.AddressIO;
import com.propertyvista.oapi.model.BuildingIO;

public class BuildingPersistenceTest {

    @Before
    public void initDB() throws Exception {
        VistaTestDBSetup.init();
    }

    @Test
    public void testNewBuildingPersistance() throws Exception {
        BuildingIO buildingIO = createBuilding();

        Building building = BuildingMarshaller.getInstance().marshal(buildingIO);

        System.out.println("++++++++++" + building);

        Persistence.service().persist(building);

        building = Persistence.service().retrieve(Building.class, building.getPrimaryKey());

        System.out.println("++++++++++" + building);

        BuildingIO buildingIO2 = BuildingMarshaller.getInstance().unmarshal(building);

//        assertEquals(buildingIO, buildingIO2);

    }

// work in progress

    public BuildingIO createBuilding() {
        AddressIO addressIO = new AddressIO();
        addressIO.city.value = "Toronto";
        addressIO.country.value = "Canada";
        addressIO.postalCode.value = "M9A 4X9";
        addressIO.province.value = "Ontario";
        BuildingIO b = new BuildingIO("building1");

        return b;
    }

}

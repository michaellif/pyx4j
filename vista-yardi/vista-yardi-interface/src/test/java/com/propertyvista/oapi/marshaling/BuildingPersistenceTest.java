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

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.oapi.model.AddressIO;
import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.BuildingInfoIO;
import com.propertyvista.oapi.model.types.BuildingTypeIO;
import com.propertyvista.oapi.model.types.StreetTypeIO;
import com.propertyvista.oapi.ws.WSOapiTestBase;
import com.propertyvista.oapi.xml.StringIO;

public class BuildingPersistenceTest extends WSOapiTestBase {

    @Override
    @Before
    public void preloadData() {
        super.preloadData();
    }

    @Test
    public void testBewBuildingPersistance() throws Exception {
        BuildingIO buildingIO = createBuilding();

        Building building = BuildingMarshaller.getInstance().marshal(buildingIO);

        System.out.println("++++++++++" + building);

        Persistence.service().persist(building);

        building = Persistence.service().retrieve(Building.class, building.getPrimaryKey());

        System.out.println("++++++++++" + building);

        BuildingIO buildingIO2 = BuildingMarshaller.getInstance().unmarshal(building);

        assertEquals(buildingIO.propertyCode, buildingIO2.propertyCode);
        assertEquals(buildingIO.info.address.city.value, buildingIO2.info.address.city.value);
        assertEquals(buildingIO.info.address.country.value, buildingIO2.info.address.country.value);
        assertEquals(buildingIO.info.address.postalCode.value, buildingIO2.info.address.postalCode.value);
        assertEquals(buildingIO.info.address.province.value, buildingIO2.info.address.province.value);
        assertEquals(buildingIO.info.address.streetName.value, buildingIO2.info.address.streetName.value);
        assertEquals(buildingIO.info.address.streetNumber.value, buildingIO2.info.address.streetNumber.value);
        assertEquals(buildingIO.info.address.streetType.value, buildingIO2.info.address.streetType.value);

    }

// work in progress

    public BuildingIO createBuilding() {
        BuildingIO b = new BuildingIO("building1");
        BuildingInfoIO info = new BuildingInfoIO();
        AddressIO addressIO = new AddressIO();

        addressIO.city = new StringIO("Toronto");
        addressIO.country = new StringIO("Canada");
        addressIO.postalCode = new StringIO("M9A 4X9");
        addressIO.province = new StringIO("Ontario");
        addressIO.streetName = new StringIO("Bathurst");
        addressIO.streetNumber = new StringIO("255");
        addressIO.streetType = new StreetTypeIO(AddressStructured.StreetType.street);

        info.address = addressIO;
        info.buildingType = new BuildingTypeIO(BuildingInfo.Type.residential);
        b.info = info;
        return b;
    }

}

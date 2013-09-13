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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.pmc.IntegrationSystem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.oapi.binder.BuildingPersister;
import com.propertyvista.oapi.binder.UnitPersister;
import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.UnitIO;
import com.propertyvista.oapi.ws.WSOapiTestBase;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.StringIO;

public class UnitPersistenceTest extends WSOapiTestBase {

    private final static Logger log = LoggerFactory.getLogger(UnitPersistenceTest.class);

    @Before
    public void init() throws Exception {
        preloadData();
    }

    @Test
    public void testUpdateUnitPersistance() {
        String propertyCode = Long.toString(System.currentTimeMillis());
        propertyCode = propertyCode.substring(propertyCode.length() - 6, propertyCode.length());
        BuildingIO buildingIO = createBuilding(propertyCode);
        Building building = BuildingMarshaller.getInstance().unmarshal(buildingIO);
        building.integrationSystemId().setValue(IntegrationSystem.internal);
        new BuildingPersister().persist(building);

        UnitIO unitIO = createUnit("1", propertyCode);
        AptUnit unit = UnitMarshaller.getInstance().unmarshal(unitIO);

        log.debug("++++++++++ {}", unit);

        new UnitPersister().persist(unit);

        unit = new UnitPersister().retrieve(unit);

        log.debug("++++++++++ {}", unit);

        UnitIO unitIO2 = UnitMarshaller.getInstance().marshal(unit);

        assertEquals(unitIO.number, unitIO2.number);
        assertEquals(unitIO.propertyCode, unitIO2.propertyCode);
        assertEquals(unitIO.baths.getValue(), unitIO2.baths.getValue());
        assertEquals(unitIO.beds.getValue(), unitIO2.beds.getValue());
        assertEquals(unitIO.floorplanName.getValue(), unitIO2.floorplanName.getValue());
    }

    private UnitIO createUnit(String number, String propertyCode) {
        UnitIO unit = new UnitIO();
        unit.number = number;
        unit.propertyCode = propertyCode;
        unit.floorplanName = new StringIO("2bdrm");
        unit.baths = new IntegerIO(1);
        unit.beds = new IntegerIO(2);
        return unit;
    }

    private BuildingIO createBuilding(String propertyCode) {
        BuildingIO building = new BuildingIO(propertyCode);
        return building;
    }

}

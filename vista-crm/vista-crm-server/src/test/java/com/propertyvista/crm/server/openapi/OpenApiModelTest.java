/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 27, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.crm.server.openapi;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.BuildingsGenerator;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.crm.server.openapi.model.BuildingRS;
import com.propertyvista.crm.server.openapi.model.BuildingsRS;
import com.propertyvista.crm.server.openapi.model.FloorplanRS;
import com.propertyvista.crm.server.openapi.model.util.Converter;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.common.reference.SharedData;

public class OpenApiModelTest {

    private final static Logger log = LoggerFactory.getLogger(OpenApiModelTest.class);

    @Test
    public void testXsdSchema() throws Exception {
        MarshallUtil.printSchema(BuildingsRS.class, System.out, true);
    }

    @Test
    public void testXmlBuildings() throws Exception {

        BuildingsGenerator generator = new BuildingsGenerator();
        List<Building> buildings = generator.createBuildings(1, null);

        BuildingsRS buildingsRS = new BuildingsRS();
        for (Building building : buildings) {
            BuildingRS buildingRS = Converter.convertBuilding(building);
            buildingsRS.buildings.add(buildingRS);

            Floorplan floorplan = generator.createFloorplan("MyFloorplan");
            FloorplanRS floorplanRS = Converter.convertFloorplan(floorplan);
            buildingRS.floorplans.floorplans.add(floorplanRS);
// TODO Dmitry - theris no createMedia() in current MediaGenerator!? 
//            MediaRS mediaRS = Converter.convertMedia(MediaGenerator.createMedia());
//            floorplanRS.medias.media.add(mediaRS);
        }
        String xml = MarshallUtil.marshall(buildingsRS);

        log.info("\n{}\n", xml);
    }

    @BeforeClass
    public static void init() {
        SharedData.init();
    }
}

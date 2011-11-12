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

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.BuildingsGenerator;
import com.propertvista.generator.MediaGenerator;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.essentials.j2se.util.MarshallUtil;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.tests.VistaTestsNamespaceResolver;
import com.propertyvista.crm.server.openapi.model.BuildingRS;
import com.propertyvista.crm.server.openapi.model.BuildingsRS;
import com.propertyvista.crm.server.openapi.model.FloorplanRS;
import com.propertyvista.crm.server.openapi.model.MediaRS;
import com.propertyvista.crm.server.openapi.model.util.Converter;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.common.reference.SharedData;

public class OpenApiModelTest {

    private final static Logger log = LoggerFactory.getLogger(OpenApiModelTest.class);

    @Test
    public void testXsdSchema() throws Exception {
        MarshallUtil.printSchema(BuildingsRS.class, System.out, false);
    }

    public static void assertEqual(String name, IEntity expected, IEntity actual) {
        Path changePath = EntityGraph.getChangedDataPath(expected, actual);
        if (changePath != null) {
            DataDump.dump("client", expected);
            DataDump.dump("server", actual);

            // we need to log this on the info level, since this information is quite important
            log.info("expected {}", expected);
            log.info("actual {}", actual);
//            Object expectedValue = expected.getMember(changePath).getValue();
//            Object actualValue = actual.getMember(changePath).getValue();
//            log.info("Expected {}, Actual {}", expectedValue, actualValue);
            Assert.fail(name + " are not the same: " + changePath);// + " expected " + expectedValue + ", actual " + actualValue);
        }
    }

    @Test
    public void testConvert() {
        BuildingsGenerator generator = new BuildingsGenerator();
        List<Building> buildings = generator.createBuildings(10);

        for (Building building : buildings) {
            BuildingRS buildingRS = Converter.convertBuilding(building);

            Assert.assertNotNull("Converted building", buildingRS);

            Building building2 = Converter.convertBuilding(buildingRS);
            Assert.assertEquals("propertyCode", building.propertyCode().getStringView(), building2.propertyCode().getStringView());

            // TODO we need to
            assertEqual("building", building, building2);
        }
    }

    @Test
    public void testXmlBuildingsUnmarshall() throws IOException, JAXBException {
        String xml = IOUtils.getTextResource("buildings.xml", BuildingsResource.class);

        BuildingsRS buildings = MarshallUtil.unmarshal(BuildingsRS.class, xml);
        Assert.assertNotNull("Converted buildings", buildings);
        Assert.assertTrue("Has buildings", !buildings.buildings.isEmpty());
    }

    @Test
    public void testXmlBuildingsMarshall() throws Exception {

        BuildingsGenerator generator = new BuildingsGenerator();
        List<Building> buildings = generator.createBuildings(1);

        BuildingsRS buildingsRS = new BuildingsRS();
        for (Building building : buildings) {
            BuildingRS buildingRS = Converter.convertBuilding(building);
            log.info("building date {}", building.info().structureBuildYear().getValue());
            log.info("building date {}", buildingRS.info.structureBuildYear);
            buildingsRS.buildings.add(buildingRS);

            Floorplan floorplan = generator.createFloorplan();
            FloorplanRS floorplanRS = Converter.convertFloorplan(floorplan);
            buildingRS.floorplans.add(floorplanRS);
            MediaRS mediaRS = Converter.convertMedia(MediaGenerator.createMedia());
            floorplanRS.medias.add(mediaRS);
        }
        String xml = MarshallUtil.marshall(buildingsRS);

        log.info("\n{}\n", xml);
    }

    @BeforeClass
    public static void init() {
        NamespaceManager.setNamespace(VistaTestsNamespaceResolver.demoNamespace);
        SharedData.init();
    }
}

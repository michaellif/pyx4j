/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.xml.XMLEntityWriter;
import com.pyx4j.essentials.server.xml.XMLStringWriter;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.tests.VistaTestsNamespaceResolver;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.interfaces.importer.model.CreateModelXML;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.xml.ImportXMLEntityName;

public class BuildingExample {

    private final static Logger log = LoggerFactory.getLogger(CreateModelXML.class);

    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(true));
        NamespaceManager.setNamespace(VistaTestsNamespaceResolver.demoNamespace);
        String imagesBaseFolder = "data/export/images/";

        ImportIO importIO = EntityFactory.create(ImportIO.class);

        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);

        List<Building> buildings = PersistenceServicesFactory.getPersistenceService().query(buildingCriteria);

        for (Building building : buildings) {
            importIO.buildings().add(new BuildingRetriever().getModel(building, imagesBaseFolder));
        }

        File f = new File("all-buildings-example.xml");
        FileWriter w = null;
        try {
            w = new FileWriter(f);
            XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"));
            XMLEntityWriter xmlWriter = new XMLEntityWriter(xml, new ImportXMLEntityName());
            xmlWriter.setEmitId(false);
            xmlWriter.write(importIO);
            w.write(xml.toString());
            w.flush();
        } catch (IOException e) {
            log.error("debug write", e);
        } finally {
            IOUtils.closeQuietly(w);
        }
        log.info("buildings {} ", buildings.size());
        log.info("Total time {} msec", TimeUtils.since(start));
    }
}

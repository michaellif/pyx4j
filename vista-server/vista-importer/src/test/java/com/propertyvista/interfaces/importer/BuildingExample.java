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
 */
package com.propertyvista.interfaces.importer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.xml.XMLEntityWriter;
import com.pyx4j.entity.xml.XMLStringWriter;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.tests.VistaTestsNamespaceResolver;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.xml.ImportXMLEntityNamingConvention;

public class BuildingExample {

    private final static Logger log = LoggerFactory.getLogger(BuildingExample.class);

    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(DatabaseType.MySQL));
        NamespaceManager.setNamespace(VistaTestsNamespaceResolver.demoNamespace);

        MediaConfig mediaConfig = new MediaConfig();
        mediaConfig.baseFolder = "data/export/images/";

        ImportIO importIO = EntityFactory.create(ImportIO.class);

        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);

        List<Building> buildings = Persistence.service().query(buildingCriteria);

        for (Building building : buildings) {
            importIO.buildings().add(new BuildingRetriever().getModel(building, mediaConfig));
        }

        File f = new File("all-buildings-example.xml");
        FileWriter w = null;
        try {
            w = new FileWriter(f);
            XMLStringWriter xml = new XMLStringWriter(StandardCharsets.UTF_8);
            XMLEntityWriter xmlWriter = new XMLEntityWriter(xml, new ImportXMLEntityNamingConvention());
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

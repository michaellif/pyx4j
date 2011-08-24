/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import java.io.File;
import java.io.FileReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.server.xml.XMLEntityParser;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.xml.ImportXMLEntityFactory;

public class BuildingImport {

    private final static Logger log = LoggerFactory.getLogger(BuildingImport.class);

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(true));
        NamespaceManager.setNamespace("star");

        String fileName;

        //fileName = "all-buildings-example.xml";
        fileName = "buildings.xml";
        String imagesBaseFolder = "data/export/images/";

        XMLEntityParser parser = new XMLEntityParser(new ImportXMLEntityFactory());

        ImportIO importIO = parser.parse(ImportIO.class, getDom(new File(fileName)).getDocumentElement());

        for (BuildingIO building : importIO.buildings()) {
            new BuildingImporter().persist(building, imagesBaseFolder);
        }

        log.info("Total time {} msec", TimeUtils.since(start));
    }

    private static Document getDom(File xmlFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(null);
        return builder.parse(new InputSource(new FileReader(xmlFile)));
    }
}

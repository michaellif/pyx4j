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
package com.propertyvista.interfaces.importer.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.report.XMLStringWriter;
import com.pyx4j.essentials.server.xml.XMLEntityModelWriter;
import com.pyx4j.essentials.server.xml.XMLEntitySchemaWriter;

import com.propertyvista.interfaces.importer.xml.ImportXMLEntityName;

public class CreateModelXML {

    private final static Logger log = LoggerFactory.getLogger(CreateModelXML.class);

    public static void main(String[] args) {

        ImportIO ent = EntityFactory.create(ImportIO.class);

        XMLEntitySchemaWriter.printSchema(BuildingIO.class, System.out, false);

        File f = new File("import-mode.xml");
        FileWriter w = null;
        try {
            w = new FileWriter(f);
            XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"));
            XMLEntityModelWriter xmlWriter = new XMLEntityModelWriter(xml, new ImportXMLEntityName());
            xmlWriter.setEmitId(false);
            //xmlWriter.write(ent);
            w.write(xml.toString());
            w.flush();
        } catch (IOException e) {
            log.error("debug write", e);
        } finally {
            com.pyx4j.gwt.server.IOUtils.closeQuietly(w);
        }
    }
}

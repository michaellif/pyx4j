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
package com.propertyvista.crm.server.openapi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.j2se.util.MarshallUtil;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.tests.VistaTestsNamespaceResolver;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.crm.server.openapi.model.BuildingsRS;

public class OpenApiModelExample {

    private final static Logger log = LoggerFactory.getLogger(OpenApiModelExample.class);

    public static void main(String[] args) throws JAXBException {
        long start = System.currentTimeMillis();

        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(true));
        NamespaceManager.setNamespace(VistaTestsNamespaceResolver.demoNamespace);

        BuildingsRS buildingsRS = new BuildingsResource().listBuildings();

        log.info("buildings {} ", buildingsRS.buildings.size());
        log.info("Retrive time {} msec", TimeUtils.since(start));

        String xml = MarshallUtil.marshall(buildingsRS);

        log.info("Total time {} msec", TimeUtils.since(start));

        File f = new File("all-buildings-example.xml");
        FileWriter w = null;
        try {
            w = new FileWriter(f);
            w.write(xml);
            w.flush();
        } catch (IOException e) {
            log.error("debug write", e);
        } finally {
            IOUtils.closeQuietly(w);
        }
    }

}

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 4, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.io.IOException;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.interfaces.importer.BuildingUpdater;
import com.propertyvista.interfaces.importer.ImportCounters;
import com.propertyvista.interfaces.importer.ImportUtils;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;

public class PmcDataPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PmcDataPreloader.class);

    private String data = null;

    public PmcDataPreloader() {
        try {
            data = IOUtils.getUTF8TextResource("pmc/" + NamespaceManager.getNamespace() + ".xml", PmcDataPreloader.class);
        } catch (IOException e) {
            log.error("resource load error", e);
        }
    }

    public boolean hasData() {
        return (data != null);
    }

    @Override
    public String create() {
        if (data != null) {
            String imagesBaseFolder = "data/pmc/images/";
            ImportIO importIO = ImportUtils.parse(ImportIO.class, new InputSource(new StringReader(data)));
            ImportCounters counters = new ImportCounters();
            for (BuildingIO building : importIO.buildings()) {
                //counters.add(new BuildingImporter().persist(building, imagesBaseFolder, true));
                counters.add(new BuildingUpdater().updateData(building, imagesBaseFolder, true));
            }
            SharedGeoLocator.save();
            return SimpleMessageFormat.format("Imported {0} building(s), {1} floorplan(s), {2} unit(s)", counters.buildings, counters.floorplans,
                    counters.units);
        } else {
            return null;
        }
    }

    @Override
    public String delete() {
        return null;
    }

}

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 23, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.server.deferred.DeferredProcessorThread;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;
import com.pyx4j.essentials.server.upload.UploadServiceImpl;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.rpc.PmcImportDTO;
import com.propertyvista.admin.rpc.services.ImportUploadService;
import com.propertyvista.interfaces.importer.BuildingImporter;
import com.propertyvista.interfaces.importer.BuildingUpdater;
import com.propertyvista.interfaces.importer.ImportCounters;
import com.propertyvista.interfaces.importer.ImportUtils;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;
import com.propertyvista.server.domain.admin.Pmc;

public class ImportUploadServiceImpl extends UploadServiceImpl<PmcImportDTO> implements ImportUploadService {

    private final static Logger log = LoggerFactory.getLogger(ImportUploadServiceImpl.class);

    @Override
    public long getMaxSize() {
        return 10 * 1024 * 1024;
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public ProcessingStatus onUploadRecived(final UploadData data, final UploadDeferredProcess process, final UploadResponse response) {

        Thread t = new DeferredProcessorThread("Import", process, new Runnable() {
            @Override
            public void run() {
                runImport(data, process, response);
            }
        });
        t.setDaemon(true);
        t.start();

        return ProcessingStatus.processWillContinue;
    }

    private static void runImport(UploadData data, UploadDeferredProcess process, UploadResponse response) {
        try {
            PmcImportDTO importDTO = (PmcImportDTO) process.getData();
            if (importDTO.id().isNull()) {
                throw new Error();
            }
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            Pmc pmc = Persistence.service().retrieve(Pmc.class, importDTO.id().getValue());
            if (pmc == null) {
                throw new Error("PMC Not found");
            }
            NamespaceManager.setNamespace(pmc.dnsName().getValue());

            String imagesBaseFolder = "data/export/images/";
            //imagesBaseFolder = "M:\\stuff\\vista\\prod";

            ImportIO importIO = ImportUtils.parse(ImportIO.class, new InputSource(new ByteArrayInputStream(data.data)));
            process.status().setProgress(0);
            process.status().setProgressMaximum(importIO.buildings().size());

            int count = 0;
            if (!importDTO.updateOnly().isBooleanTrue()) {
                List<String> messages = new Vector<String>();
                for (BuildingIO building : importIO.buildings()) {
                    messages.addAll(new BuildingImporter().verify(building, imagesBaseFolder));
                    count++;
                    process.status().setProgress(count);
                }
                if (messages.size() > 0) {
                    log.error("validation failed {}; {}", messages.size(), ConverterUtils.convertStringCollection(messages, "\n"));
                    if (!importDTO.ignoreMissingMedia().isBooleanTrue()) {
                        throw new Error("Validation error count:" + messages.size() + "; messages:" + ConverterUtils.convertStringCollection(messages, "\n"));
                    }
                }
                process.status().setProgress(0);
            }

            count = 0;
            ImportCounters counters = new ImportCounters();
            for (BuildingIO building : importIO.buildings()) {
                log.debug("processing building {} {}", count + "/" + importIO.buildings().size(), building.propertyCode().getValue());
                if (importDTO.updateOnly().isBooleanTrue()) {
                    counters.add(new BuildingUpdater().update(building, imagesBaseFolder));
                } else {
                    counters.add(new BuildingImporter().persist(building, imagesBaseFolder, importDTO.ignoreMissingMedia().isBooleanTrue()));
                }
                count++;
                process.status().setProgress(count);
            }
            if (importDTO.updateOnly().isBooleanTrue()) {
                response.message = SimpleMessageFormat.format("Updated {0} units in {1} building(s)", counters.units, counters.buildings);
            } else {
                response.message = SimpleMessageFormat.format("Imported {0} building(s), {1} floorplan(s), {2} unit(s)", count, counters.floorplans,
                        counters.units);
            }
            log.info("import upload completed {}", response.message);
        } finally {
            SharedGeoLocator.save();
            NamespaceManager.remove();
        }
    }
}

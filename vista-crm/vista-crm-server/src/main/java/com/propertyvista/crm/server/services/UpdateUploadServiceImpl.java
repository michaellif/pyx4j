/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.io.ByteArrayInputStream;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.server.deferred.DeferredProcessorThread;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;
import com.pyx4j.essentials.server.upload.UploadServiceImpl;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.crm.rpc.dto.UpdateUploadDTO;
import com.propertyvista.crm.rpc.services.UpdateUploadService;
import com.propertyvista.interfaces.importer.BuildingUpdater;
import com.propertyvista.interfaces.importer.ImportCounters;
import com.propertyvista.interfaces.importer.ImportUtils;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;

public class UpdateUploadServiceImpl extends UploadServiceImpl<UpdateUploadDTO> implements UpdateUploadService {

    private final static Logger log = LoggerFactory.getLogger(UpdateUploadServiceImpl.class);

    @Override
    public long getMaxSize() {
        return 5 * 1024 * 1024;
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public ProcessingStatus onUploadRecived(final UploadData data, final UploadDeferredProcess process, final UploadResponse response) {
        final String namespace = NamespaceManager.getNamespace();
        Thread t = new DeferredProcessorThread("Update", process, new Runnable() {
            @Override
            public void run() {
                NamespaceManager.setNamespace(namespace);
                runImport(data, process, response);
            }
        });
        t.setDaemon(true);
        t.start();

        return ProcessingStatus.processWillContinue;
    }

    private static void runImport(UploadData data, UploadDeferredProcess process, UploadResponse response) {
        String imagesBaseFolder = "data/export/images/";

        ImportIO importIO = ImportUtils.parse(ImportIO.class, new InputSource(new ByteArrayInputStream(data.data)));
        process.status().setProgress(0);
        process.status().setProgressMaximum(importIO.buildings().size());

        int count = 0;
        ImportCounters counters = new ImportCounters();
        for (BuildingIO building : importIO.buildings()) {
            log.debug("processing building {} {}", count + "/" + importIO.buildings().size(), building.propertyCode().getValue());
            counters.add(new BuildingUpdater().updateUnitAvailability(building, imagesBaseFolder));
            count++;
            process.status().setProgress(count);
        }
        response.message = SimpleMessageFormat.format("Updated {0} units in {1} building(s)", counters.units, counters.buildings);
        log.info(" Update upload completed {}", response.message);
    }
}

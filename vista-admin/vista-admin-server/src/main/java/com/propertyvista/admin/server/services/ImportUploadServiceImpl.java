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

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;
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
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.server.common.reference.geo.GeoLocator.Mode;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;
import com.propertyvista.server.domain.admin.Pmc;

public class ImportUploadServiceImpl extends UploadServiceImpl<PmcImportDTO, IEntity> implements ImportUploadService {

    private final static Logger log = LoggerFactory.getLogger(ImportUploadServiceImpl.class);

    @Override
    public long getMaxSize() {
        return 10 * 1024 * 1024;
    }

    @Override
    public String getUploadFileTypeName() {
        return "Import";
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public ProcessingStatus onUploadRecived(final UploadData data, final UploadDeferredProcess<PmcImportDTO, IEntity> process,
            final UploadResponse<IEntity> response) {

        //TODO This is not the very best example how to for execution on server. VladS - Change!
        Thread t = new DeferredProcessorThread("Import", process, new Runnable() {
            @Override
            public void run() {
                Persistence.service().startBackgroundProcessTransaction();
                boolean ok = false;
                try {
                    runImport(data, process, response);
                    Persistence.service().commit();
                    ok = true;
                } finally {
                    if (!ok) {
                        Persistence.service().rollback();
                    }
                    Persistence.service().endTransaction();
                }
            }
        });
        t.setDaemon(true);
        t.start();

        return ProcessingStatus.processWillContinue;
    }

    private static void runImport(UploadData data, UploadDeferredProcess<PmcImportDTO, IEntity> process, UploadResponse<IEntity> response) {
        try {
            PmcImportDTO importDTO = process.getData();
            if (importDTO.id().isNull()) {
                throw new Error();
            }
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            Pmc pmc = Persistence.service().retrieve(Pmc.class, importDTO.id().getValue());
            if (pmc == null) {
                throw new Error("PMC Not found");
            }
            NamespaceManager.setNamespace(pmc.dnsName().getValue());

            MediaConfig mediaConfig = new MediaConfig();
            mediaConfig.baseFolder = "data/" + NamespaceManager.getNamespace();

            ImportIO importIO = ImportUtils.parse(importDTO.adapterType().getValue(), data.data,
                    DownloadFormat.valueByExtension(FilenameUtils.getExtension(response.fileName)));
            process.status().setProgress(0);
            process.status().setProgressMaximum(importIO.buildings().size());

            int count = 0;
            if (!importDTO.type().getValue().equals(PmcImportDTO.ImportType.updateUnitAvailability)) {
                List<String> messages = new Vector<String>();
                for (BuildingIO building : importIO.buildings()) {
                    messages.addAll(new BuildingImporter().verify(building, mediaConfig));
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

            mediaConfig.ignoreMissingMedia = importDTO.ignoreMissingMedia().isBooleanTrue();

            count = 0;
            SharedGeoLocator.setMode(Mode.updateCache);
            ImportCounters counters = new ImportCounters();
            for (BuildingIO building : importIO.buildings()) {
                log.debug("processing building {} {}", count + "/" + importIO.buildings().size(), building.propertyCode().getValue());
                switch (importDTO.type().getValue()) {
                case newData:
                    counters.add(new BuildingImporter().persist(building, mediaConfig));
                    break;
                case updateUnitAvailability:
                    throw new Error("This functionality is currently disabled, use 'Type: Update Data' instead"); //TODO fix later, unit availability update should not create new buildings, update data should
//                    counters.add(new BuildingUpdater().updateUnitAvailability(building, mediaConfig));
//                    break;
                case updateData:
                    counters.add(new BuildingUpdater().updateData(building, mediaConfig));
                    break;
                }
                Persistence.service().commit();
                count++;
                process.status().setProgress(count);
            }
            switch (importDTO.type().getValue()) {
            case newData:
                response.message = SimpleMessageFormat.format("Imported {0} building(s), {1} floorplan(s), {2} unit(s)", count, counters.floorplans,
                        counters.units);
                break;
            case updateData:
                response.message = SimpleMessageFormat.format("Updated {0} building(s), {1} floorplan(s), {2} unit(s)", counters.buildings,
                        counters.floorplans, counters.units);
                break;
            case updateUnitAvailability:
                response.message = SimpleMessageFormat.format("Updated {0} units in {1} building(s)", counters.units, counters.buildings);
                break;
            }
            log.info("import upload completed {}", response.message);
        } finally {
            SharedGeoLocator.save();
            NamespaceManager.remove();
        }
    }
}

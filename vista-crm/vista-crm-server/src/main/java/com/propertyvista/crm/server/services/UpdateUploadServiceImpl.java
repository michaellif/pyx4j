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

import java.util.Collection;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.DeferredUploadProcess;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.gwt.server.deferred.DeferredProcessorThread;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.crm.rpc.services.UpdateUploadService;
import com.propertyvista.dto.DownloadableUploadResponseDTO;
import com.propertyvista.interfaces.importer.BuildingUpdater;
import com.propertyvista.interfaces.importer.ImportCounters;
import com.propertyvista.interfaces.importer.ImportUploadDeferredProcess;
import com.propertyvista.interfaces.importer.ImportUtils;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;

public class UpdateUploadServiceImpl extends AbstractUploadServiceImpl<ImportUploadDTO, DownloadableUploadResponseDTO> implements UpdateUploadService {

    private static final I18n i18n = I18n.get(UpdateUploadServiceImpl.class);

    private final static Logger log = LoggerFactory.getLogger(UpdateUploadServiceImpl.class);

    @Override
    public long getMaxSize() {
        return 5 * 1024 * 1024;
    }

    @Override
    @I18nComment("Used in message like this 'Unsupported Data update File Type .docx'")
    public String getUploadFileTypeName() {
        return i18n.tr("Data Update");
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    protected DeferredUploadProcess<ImportUploadDTO, DownloadableUploadResponseDTO> createUploadDeferredProcess(ImportUploadDTO data) {
        return new ImportUploadDeferredProcess(data);
    }

    @Override
    public ProcessingStatus onUploadReceived(final UploadData data, final DeferredUploadProcess<ImportUploadDTO, DownloadableUploadResponseDTO> process,
            final UploadResponse<DownloadableUploadResponseDTO> response) {
        process.getData().type().setValue(ImportUploadDTO.ImportType.updateUnitAvailability);
        process.onUploadReceived(data, response);
        DeferredProcessRegistry.start(data.deferredCorrelationId, process, ThreadPoolNames.IMPORTS);
        return ProcessingStatus.processWillContinue;

    }

    @Deprecated
    public ProcessingStatus OLD_onUploadRecived(final UploadData data, final DeferredUploadProcess<ImportUploadDTO, DownloadableUploadResponseDTO> process,
            final UploadResponse<DownloadableUploadResponseDTO> response) {
        final String namespace = NamespaceManager.getNamespace();

        //TODO This is not the very best example how to for execution on server. VladS - Change!
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

    private static void runImport(UploadData data, DeferredUploadProcess<ImportUploadDTO, DownloadableUploadResponseDTO> process,
            UploadResponse<DownloadableUploadResponseDTO> response) {

        ImportIO importIO = ImportUtils.parse(process.getData().dataFormat().getValue(), data.data,
                DownloadFormat.valueByExtension(FilenameUtils.getExtension(response.fileName)));

        process.status().setProgress(0);
        process.status().setProgressMaximum(importIO.buildings().size());

        MediaConfig mediaConfig = new MediaConfig();
        mediaConfig.baseFolder = "data/export/images/";

        int count = 0;
        ImportCounters counters = new ImportCounters();
        for (BuildingIO building : importIO.buildings()) {
            log.debug("processing building {} {}", count + "/" + importIO.buildings().size(), building.propertyCode().getValue());
            counters.add(new BuildingUpdater().updateData(building, mediaConfig));
            count++;
            process.status().setProgress(count);
        }
        response.message = SimpleMessageFormat.format("Updated {0} units in {1} building(s)", counters.units, counters.buildings);
        log.info(" Update upload completed {}", response.message);
    }
}

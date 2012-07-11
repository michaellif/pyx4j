/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;

import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.crm.rpc.dto.ImportUploadResponseDTO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.processor.ImportProcessor;

public class ImportUploadDeferredProcess extends UploadDeferredProcess<ImportUploadDTO, ImportUploadResponseDTO> {

    private static final long serialVersionUID = 1L;

    private byte[] binaryData;

    private UploadResponse<ImportUploadResponseDTO> response;

    public ImportUploadDeferredProcess(ImportUploadDTO data) {
        super(data);
    }

    @Override
    public void onUploadRecived(final UploadData data, final UploadResponse<ImportUploadResponseDTO> response) {
        binaryData = data.data;
        this.response = response;
    }

    @Override
    public void execute() {
        boolean success = false;
        try {
            Persistence.service().startBackgroundProcessTransaction();
            executeImport();
            if (status().isCanceled()) {
                Persistence.service().rollback();
            } else {
                Persistence.service().commit();
            }
            success = true;
        } finally {
            if (!success) {
                Persistence.service().rollback();
            }
            Persistence.service().endTransaction();
        }
    }

    private void executeImport() {
        ImportIO importIO = ImportUtils.parse(getData().dataFormat().getValue(), binaryData,
                DownloadFormat.valueByExtension(FilenameUtils.getExtension(response.fileName)));
        ImportProcessor importProcessor = ImportUtils.createImportProcessor(getData(), importIO);
        if (importProcessor.validate(importIO, status(), getData(), response)) {
            importProcessor.persist(importIO, status(), getData(), response);
        }
        status().setCompleted();
    }
}

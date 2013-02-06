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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.server.contexts.Lifecycle;

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
        this.response.data = EntityFactory.create(ImportUploadResponseDTO.class);
        this.response.data.success().setValue(Boolean.FALSE);
    }

    @Override
    public void execute() {
        boolean success = false;
        try {
            Lifecycle.startElevatedUserContext();
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
            Lifecycle.endContext();
        }
    }

    private void executeImport() {
        ImportIO importIO = ImportUtils.parse(getData().dataFormat().getValue(), binaryData,
                DownloadFormat.valueByExtension(FilenameUtils.getExtension(response.fileName)));
        if (ImportUtils.createValidationErrorResponse(importIO, status(), response)) {
            status().setCompleted();
            return;
        }
        ImportProcessor importProcessor = ImportUtils.createImportProcessor(getData(), importIO);
        boolean valid = importProcessor.validate(importIO, status(), getData(), response);
        if (ImportUtils.createValidationErrorResponse(importIO, status(), response)) {
            status().setCompleted();
            return;
        }
        if (valid) {
            importProcessor.persist(importIO, status(), getData(), response);
            ImportUtils.createProcessingResponse(importIO, status(), response);
            this.response.data.success().setValue(Boolean.TRUE);
        }
        status().setCompleted();
    }
}

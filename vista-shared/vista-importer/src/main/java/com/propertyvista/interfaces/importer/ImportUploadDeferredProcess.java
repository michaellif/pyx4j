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

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.server.contexts.Lifecycle;

import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.processor.ImportProcessor;
import com.propertyvista.server.common.upload.AbstractUploadWithDownloadableResponceDeferredProcess;

public class ImportUploadDeferredProcess extends AbstractUploadWithDownloadableResponceDeferredProcess<ImportUploadDTO> {

    private static final long serialVersionUID = 1L;

    private ProcessingResponseReport errorReport;

    private ProcessingResponseReport processingReport;

    public ImportUploadDeferredProcess(ImportUploadDTO data) {
        super(data);
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
            Lifecycle.endElevatedUserContext();
        }

        // Store reports in user session
        if (errorReport != null) {
            if (getResponse().message().isNull()) {
                getResponse().message()
                        .setValue(SimpleMessageFormat.format("There are validation {0} errors in uploaded file", errorReport.getMessagesCount()));
            }
            getResponse().success().setValue(Boolean.FALSE);
            String fileName = "validationError.xlsx";
            getResponse().resultUrl().setValue(fileName);
            errorReport.createDownloadable(fileName);
        } else if (processingReport != null) {
            getResponse().success().setValue(Boolean.TRUE);
            String fileName = "processingResults.xlsx";
            getResponse().resultUrl().setValue(fileName);
            processingReport.createDownloadable(fileName);
        }
        status().setCompleted();
    }

    private void executeImport() {
        ImportIO importIO = ImportUtils.parse(getUploadInitiationData().dataFormat().getValue(), getBinaryData(),
                DownloadFormat.valueByExtension(FilenameUtils.getExtension(getResponse().fileName().getValue())));
        errorReport = ImportUtils.createValidationErrorReport(importIO);
        if (errorReport != null) {
            return;
        }
        ImportProcessor importProcessor = ImportUtils.createImportProcessor(getUploadInitiationData(), importIO);
        boolean valid = importProcessor.validate(importIO, status(), getUploadInitiationData(), getResponse());
        errorReport = ImportUtils.createValidationErrorReport(importIO);
        if (errorReport != null) {
            return;
        }
        if (valid) {
            importProcessor.persist(importIO, status(), getUploadInitiationData(), getResponse());
            processingReport = ImportUtils.createProcessingResponse(importIO);
        }
    }
}

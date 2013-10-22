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
package com.propertyvista.operations.server.services;

import java.util.Collection;

import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.interfaces.importer.ImportUploadDeferredProcess;
import com.propertyvista.operations.rpc.services.ImportUploadService;
import com.propertyvista.server.common.upload.AbstractUploadWithDownloadableResponceDeferredProcess;
import com.propertyvista.server.common.upload.AbstractUploadWithDownloadableResponceServiceImpl;

public class ImportUploadServiceImpl extends AbstractUploadWithDownloadableResponceServiceImpl<ImportUploadDTO> implements ImportUploadService {

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
    protected AbstractUploadWithDownloadableResponceDeferredProcess<ImportUploadDTO> createUploadDeferredProcess(ImportUploadDTO data) {
        return new ImportUploadDeferredProcess(data);
    }

}

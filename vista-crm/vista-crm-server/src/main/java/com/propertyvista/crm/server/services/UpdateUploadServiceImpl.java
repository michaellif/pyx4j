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
import java.util.EnumSet;

import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.dto.ImportUploadDTO;
import com.propertyvista.crm.rpc.services.UpdateUploadService;
import com.propertyvista.interfaces.importer.ImportUploadDeferredProcess;
import com.propertyvista.server.common.upload.AbstractUploadWithDownloadableResponceDeferredProcess;
import com.propertyvista.server.common.upload.AbstractUploadWithDownloadableResponceServiceImpl;

public class UpdateUploadServiceImpl extends AbstractUploadWithDownloadableResponceServiceImpl<ImportUploadDTO> implements UpdateUploadService {

    private static final I18n i18n = I18n.get(UpdateUploadServiceImpl.class);

    private static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.XML, DownloadFormat.CSV, DownloadFormat.XLS,
            DownloadFormat.XLSX);

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
    protected AbstractUploadWithDownloadableResponceDeferredProcess<ImportUploadDTO> createUploadDeferredProcess(ImportUploadDTO data) {
        return new ImportUploadDeferredProcess(data);
    }

}

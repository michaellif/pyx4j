/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services;

import java.util.Collection;

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;
import com.pyx4j.essentials.server.upload.UploadServiceImpl;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.portal.rpc.ptapp.dto.ApplicationDocumentUploadDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationDocumentUploadService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.server.domain.ApplicationDocumentData;

public class ApplicationDocumentUploadServiceImpl extends UploadServiceImpl<ApplicationDocumentUploadDTO> implements ApplicationDocumentUploadService {

    private static I18n i18n = I18n.get(ApplicationDocumentUploadServiceImpl.class);

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(ApplicationDocumentData.class).data().getMeta().getLength();
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Application Document");
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public ProcessingStatus onUploadRecived(UploadData data, UploadDeferredProcess process, UploadResponse response) {
        response.fileContentType = MimeMap.getContentType(FilenameUtils.getExtension(response.fileName));

        ApplicationDocumentUploadDTO dto = (ApplicationDocumentUploadDTO) process.getData();

        TenantInLease tenant = Persistence.service().retrieve(TenantInLease.class, dto.tenantId().getValue());
        if (tenant == null) {
            throw new Error("Unknown tenantId: " + dto.tenantId().getValue());
        }
        if (!tenant.application().id().getValue().equals(PtAppContext.getCurrentUserApplication().id().getValue())) {
            throw new Error("Wrong TenantId: " + dto.tenantId().getValue());
        }

        ApplicationDocumentData applicationDocumentData = createApplicationDocumentData(data.data, response.fileContentType, tenant);

        response.uploadKey = applicationDocumentData.id().getValue();

        return ProcessingStatus.completed;
    }

    private ApplicationDocumentData createApplicationDocumentData(byte[] data, String contentType, TenantInLease tenant) {
        ApplicationDocumentData applicationDocumentData = EntityFactory.create(ApplicationDocumentData.class);
        applicationDocumentData.data().setValue(data);
        applicationDocumentData.tenant().set(tenant.tenant());
        applicationDocumentData.contentType().setValue(contentType);
        applicationDocumentData.application().set(tenant.application());
        ApplicationEntityServiceImpl.saveApplicationEntity(applicationDocumentData);
        return applicationDocumentData;
    }

}

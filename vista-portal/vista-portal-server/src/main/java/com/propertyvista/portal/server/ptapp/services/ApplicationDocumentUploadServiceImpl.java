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
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.portal.rpc.ptapp.dto.ApplicationDocumentUploadDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationDocumentUploadService;
import com.propertyvista.server.domain.ApplicationDocumentBlob;

public class ApplicationDocumentUploadServiceImpl extends UploadServiceImpl<ApplicationDocumentUploadDTO, ApplicationDocument> implements
        ApplicationDocumentUploadService {

    private static I18n i18n = I18n.get(ApplicationDocumentUploadServiceImpl.class);

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(ApplicationDocumentBlob.class).data().getMeta().getLength();
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
    public ProcessingStatus onUploadRecived(UploadData data, UploadDeferredProcess<ApplicationDocumentUploadDTO, ApplicationDocument> process,
            UploadResponse<ApplicationDocument> response) {
        response.fileContentType = MimeMap.getContentType(FilenameUtils.getExtension(response.fileName));

        ApplicationDocumentUploadDTO dto = process.getData();

        TenantInLease tenant = Persistence.service().retrieve(TenantInLease.class, dto.tenantInLeaseId().getValue());
        if (tenant == null) {
            throw new Error("Unknown tenantId: " + dto.tenantInLeaseId().getValue());
        }
        if (SecurityController.checkBehavior(VistaTenantBehavior.Prospective)) {
            //TODO
//            if (!EqualsHelper.equals(tenant.application().getPrimaryKey(), PtAppContext.getCurrentUserApplicationPrimaryKey())) {
//                throw new Error("Wrong ApplicationId: " + tenant.application().getPrimaryKey());
//            }
        } else {
            SecurityController.assertBehavior(VistaCrmBehavior.Tenants);
        }

        ApplicationDocumentBlob applicationDocumentData = EntityFactory.create(ApplicationDocumentBlob.class);
        applicationDocumentData.data().setValue(data.data);
        applicationDocumentData.contentType().setValue(response.fileContentType);

        Persistence.secureSave(applicationDocumentData);

        ApplicationDocument newDocument = EntityFactory.create(ApplicationDocument.class);
        newDocument.blobKey().setValue(applicationDocumentData.id().getValue());
        newDocument.fileName().setValue(response.fileName);
        newDocument.fileSize().setValue(response.fileSize);
        newDocument.timestamp().setValue(response.timestamp);
        newDocument.contentMimeType().setValue(response.fileContentType);
        Persistence.service().persist(newDocument);

        response.data = newDocument;

        return ProcessingStatus.completed;
    }

}

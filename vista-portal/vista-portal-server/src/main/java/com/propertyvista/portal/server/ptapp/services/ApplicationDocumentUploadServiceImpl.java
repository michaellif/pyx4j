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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.propertyvista.portal.rpc.ptapp.dto.ApplicationDocumentUploadDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationDocumentUploadService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.server.adapters.ApplicationDocumentUploadedBlobSecurityAdapterImpl;
import com.propertyvista.server.domain.ApplicationDocumentBlob;

public class ApplicationDocumentUploadServiceImpl extends UploadServiceImpl<ApplicationDocumentUploadDTO, ApplicationDocument> implements
        ApplicationDocumentUploadService {

    private static final I18n i18n = I18n.get(ApplicationDocumentUploadServiceImpl.class);

    private static final Logger log = LoggerFactory.getLogger(ApplicationDocumentUploadServiceImpl.class);

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

        ApplicationDocument newDocument = EntityFactory.create(ApplicationDocument.class);

        if (SecurityController.checkBehavior(VistaTenantBehavior.Prospective)) {
            newDocument.user().set(PtAppContext.getCurrentUser());
        } else {
            SecurityController.assertBehavior(VistaCrmBehavior.Tenants);
        }

        ApplicationDocumentBlob applicationDocumentData = EntityFactory.create(ApplicationDocumentBlob.class);
        applicationDocumentData.data().setValue(data.data);
        applicationDocumentData.contentType().setValue(response.fileContentType);

        Persistence.service().persist(applicationDocumentData);

        ApplicationDocumentUploadedBlobSecurityAdapterImpl.blobUploaded(applicationDocumentData.getPrimaryKey());

        newDocument.blobKey().setValue(applicationDocumentData.id().getValue());
        newDocument.fileName().setValue(response.fileName);
        newDocument.fileSize().setValue(response.fileSize);
        newDocument.timestamp().setValue(response.timestamp);
        newDocument.contentMimeType().setValue(response.fileContentType);

        newDocument.identificationDocument().set(dto.identificationDocument());
        newDocument.details().setValue(dto.details().getValue());

        Persistence.service().persist(newDocument);
        Persistence.service().commit();

        response.data = newDocument;

        return ProcessingStatus.completed;
    }

}

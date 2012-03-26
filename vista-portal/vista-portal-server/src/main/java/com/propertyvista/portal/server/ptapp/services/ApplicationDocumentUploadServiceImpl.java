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

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
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
import com.propertyvista.portal.server.ptapp.PtAppContext;
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

        if (SecurityController.checkBehavior(VistaTenantBehavior.Prospective)) {
            // allow a user to mess only with its own application
            // (actually it doesn't matter because once uploaded a document is not bound to application but to user)            
            EntityQueryCriteria<TenantInLease> criteria = EntityQueryCriteria.create(TenantInLease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().tenant().user(), PtAppContext.getCurrentUser()));
            TenantInLease tenantInLease = Persistence.service().retrieve(criteria);
            if (tenantInLease == null) {
                throw new Error("TenantInLease corresponding to current user was not found");
            }
            if (!EqualsHelper.equals(tenantInLease.application().getPrimaryKey(), PtAppContext.getCurrentUserApplicationPrimaryKey())) {
                log.warn(SimpleMessageFormat.format("Pt App user {0} have tried to access application number {1} that does not belong to him", PtAppContext
                        .getCurrentUser().getPrimaryKey(), PtAppContext.getCurrentUserApplicationPrimaryKey()));
                throw new Error("Wrong application: " + tenantInLease.application().getPrimaryKey());
            }
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

        newDocument.identificationDocument().set(dto.identificationDocument());
        newDocument.details().setValue(dto.details().getValue());

        Persistence.service().persist(newDocument);
        Persistence.service().commit();

        response.data = newDocument;

        return ProcessingStatus.completed;
    }

}

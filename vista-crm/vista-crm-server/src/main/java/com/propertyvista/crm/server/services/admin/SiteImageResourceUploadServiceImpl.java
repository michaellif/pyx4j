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
package com.propertyvista.crm.server.services.admin;

import java.util.Collection;

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;
import com.pyx4j.essentials.server.upload.UploadServiceImpl;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.server.common.blob.BlobService;

/**
 * @see com.propertyvista.portal.rpc.DeploymentConsts#mediaImagesServletMapping
 * 
 */
public class SiteImageResourceUploadServiceImpl extends UploadServiceImpl<IEntity, SiteImageResource> implements SiteImageResourceUploadService {

    private static final I18n i18n = I18n.get(SiteImageResourceUploadServiceImpl.class);

    @Override
    public long getMaxSize() {
        return 5 * 1024 * 1024;
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Site Resources Media");
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public ProcessingStatus onUploadRecived(final UploadData data, final UploadDeferredProcess<IEntity, SiteImageResource> process,
            final UploadResponse<SiteImageResource> response) {
        response.fileContentType = MimeMap.getContentType(FilenameUtils.getExtension(response.fileName));
        Key blobKey = BlobService.persist(data.data, response.fileName, response.fileContentType);
        response.uploadKey = blobKey;

        SiteImageResource newDocument = EntityFactory.create(SiteImageResource.class);
        newDocument.fileInfo().blobKey().setValue(blobKey);
        newDocument.fileInfo().fileName().setValue(response.fileName);
        newDocument.fileInfo().fileSize().setValue(response.fileSize);
        newDocument.fileInfo().timestamp().setValue(response.timestamp);
        newDocument.fileInfo().contentMimeType().setValue(response.fileContentType);
        Persistence.service().persist(newDocument);

        response.data = newDocument;

        return ProcessingStatus.completed;
    }
}

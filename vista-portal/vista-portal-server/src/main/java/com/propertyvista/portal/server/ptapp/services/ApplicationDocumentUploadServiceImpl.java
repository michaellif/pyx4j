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
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.DeferredUploadProcess;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.ptapp.services.ApplicationDocumentUploadService;
import com.propertyvista.server.adapters.ApplicationDocumentUploadedBlobSecurityAdapterImpl;
import com.propertyvista.server.domain.ApplicationDocumentBlob;

public class ApplicationDocumentUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, IFile> implements ApplicationDocumentUploadService {

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
    public ProcessingStatus onUploadReceived(UploadData data, DeferredUploadProcess<IEntity, IFile> process, UploadResponse<IFile> response) {
        response.fileContentType = MimeMap.getContentType(FilenameUtils.getExtension(response.fileName));

        ApplicationDocumentBlob applicationDocumentData = EntityFactory.create(ApplicationDocumentBlob.class);
        applicationDocumentData.data().setValue(data.data);
        applicationDocumentData.contentType().setValue(response.fileContentType);

        Persistence.service().persist(applicationDocumentData);
        response.uploadKey = applicationDocumentData.getPrimaryKey();

        ApplicationDocumentUploadedBlobSecurityAdapterImpl.blobUploaded(applicationDocumentData.getPrimaryKey());

        Persistence.service().commit();
        return ProcessingStatus.completed;
    }

}

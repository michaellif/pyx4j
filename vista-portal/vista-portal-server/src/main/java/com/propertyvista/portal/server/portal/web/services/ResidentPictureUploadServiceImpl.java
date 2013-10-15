/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.web.services;

import java.util.Collection;

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.DeferredUploadProcess;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.portal.rpc.portal.web.services.ResidentPictureUploadService;
import com.propertyvista.server.domain.CustomerPictureBlob;
import com.propertyvista.server.domain.FileBlob;

public class ResidentPictureUploadServiceImpl extends AbstractUploadServiceImpl<CustomerPicture, CustomerPicture> implements ResidentPictureUploadService {

    private static final I18n i18n = I18n.get(ResidentPictureUploadServiceImpl.class);

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(FileBlob.class).content().getMeta().getLength();
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Customer Picture");
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public ProcessingStatus onUploadReceived(final UploadData data, final DeferredUploadProcess<CustomerPicture, CustomerPicture> process,
            final UploadResponse<CustomerPicture> response) {

        CustomerPictureBlob blob = EntityFactory.create(CustomerPictureBlob.class);
        blob.contentType().setValue(MimeMap.getContentType(FilenameUtils.getExtension(response.fileName)));
        blob.data().setValue(data.data);

        Persistence.service().persist(blob);
        Persistence.service().commit();

        response.uploadKey = blob.getPrimaryKey();
        response.fileContentType = blob.contentType().getValue();

        CustomerPicture newDocument = EntityFactory.create(CustomerPicture.class);
        newDocument.blobKey().setValue(response.uploadKey);
        newDocument.fileName().setValue(response.fileName);
        newDocument.fileSize().setValue(response.fileSize);
        newDocument.timestamp().setValue(response.timestamp);
        newDocument.contentMimeType().setValue(response.fileContentType);

        response.data = newDocument;

        return ProcessingStatus.completed;
    }
}

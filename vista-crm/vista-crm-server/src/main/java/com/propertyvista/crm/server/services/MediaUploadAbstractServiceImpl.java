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

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.dto.MediaUploadDTO;
import com.propertyvista.domain.File;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;
import com.propertyvista.server.domain.FileBlob;

public abstract class MediaUploadAbstractServiceImpl extends AbstractUploadServiceImpl<MediaUploadDTO, File> {

    private static final I18n i18n = I18n.get(MediaUploadAbstractServiceImpl.class);

    public static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.JPEG, DownloadFormat.GIF, DownloadFormat.PNG,
            DownloadFormat.BMP);

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(FileBlob.class).content().getMeta().getLength();
    }

    protected abstract ImageTarget imageResizeTarget();

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Media");
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public ProcessingStatus onUploadReceived(final UploadData data, final UploadDeferredProcess<MediaUploadDTO, File> process,
            final UploadResponse<File> response) {
        response.fileContentType = MimeMap.getContentType(FilenameUtils.getExtension(response.fileName));
        Key blobKey = BlobService.persist(data.data, response.fileName, response.fileContentType);

        ThumbnailService.persist(blobKey, response.fileName, data.data, imageResizeTarget());
        Persistence.service().commit();
        response.uploadKey = blobKey;
        return ProcessingStatus.completed;
    }
}

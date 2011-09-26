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

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;
import com.pyx4j.essentials.server.upload.UploadServiceImpl;

import com.propertyvista.crm.rpc.services.SiteResourcesUploadService;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.server.common.blob.BlobService;
import com.propertyvista.server.common.blob.ThumbnailService;

public class SiteResourcesUploadServiceImpl extends UploadServiceImpl<IEntity> implements SiteResourcesUploadService {

    @Override
    public long getMaxSize() {
        return 5 * 1024 * 1024;
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public ProcessingStatus onUploadRecived(final UploadData data, final UploadDeferredProcess process, final UploadResponse response) {
        response.fileContentType = MimeMap.getContentType(FilenameUtils.getExtension(response.fileName));
        Key blobKey = BlobService.persist(data.data, response.fileName, response.fileContentType);
        ThumbnailService.persist(blobKey, response.fileName, data.data, ImageConsts.BUILDING_SMALL, ImageConsts.BUILDING_MEDIUM, ImageConsts.BUILDING_LARGE);
        response.uploadKey = blobKey;
        return ProcessingStatus.completed;
    }
}

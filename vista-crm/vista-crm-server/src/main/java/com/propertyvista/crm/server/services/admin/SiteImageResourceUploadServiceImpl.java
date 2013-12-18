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
import java.util.EnumSet;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadedData;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.domain.blob.MediaFileBlob;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.server.common.blob.BlobService;

/**
 * @see com.propertyvista.portal.rpc.DeploymentConsts#mediaImagesServletMapping
 */
public class SiteImageResourceUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, MediaFileBlob> implements SiteImageResourceUploadService {

    private static final I18n i18n = I18n.get(SiteImageResourceUploadServiceImpl.class);

    private static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.JPEG, DownloadFormat.GIF, DownloadFormat.PNG,
            DownloadFormat.BMP);

    public SiteImageResourceUploadServiceImpl() {
    }

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(MediaFileBlob.class).data().getMeta().getLength();
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
    protected void processUploadedData(IEntity uploadInitiationData, UploadedData uploadedData, IFile<MediaFileBlob> response) {
        Key blobKey = BlobService.persist(uploadedData.binaryContent, uploadedData.fileName, uploadedData.contentMimeType);

        response.blobKey().setValue(blobKey);

        SiteImageResource resource = EntityFactory.create(SiteImageResource.class);
        resource.file().set(response);
        Persistence.service().persist(resource);
        response.accessKey().setValue(resource.id().getStringView());
        Persistence.service().commit();

    }
}

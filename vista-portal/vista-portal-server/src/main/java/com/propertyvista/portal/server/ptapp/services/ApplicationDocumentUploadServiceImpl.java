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
import java.util.EnumSet;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadedData;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationDocumentUploadService;
import com.propertyvista.server.adapters.ApplicationDocumentUploadedBlobSecurityAdapterImpl;
import com.propertyvista.server.domain.ApplicationDocumentBlob;

public class ApplicationDocumentUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, ApplicationDocumentFile> implements
        ApplicationDocumentUploadService {

    private static final I18n i18n = I18n.get(ApplicationDocumentUploadServiceImpl.class);

    private static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.JPEG, DownloadFormat.GIF, DownloadFormat.PNG,
            DownloadFormat.TIF, DownloadFormat.BMP, DownloadFormat.PDF);

    public ApplicationDocumentUploadServiceImpl() {
        super(ApplicationDocumentFile.class);
    }

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
    protected void processUploadedData(IEntity uploadInitiationData, UploadedData uploadedData, ApplicationDocumentFile response) {

        ApplicationDocumentBlob blob = EntityFactory.create(ApplicationDocumentBlob.class);
        blob.data().setValue(uploadedData.binaryContent);
        blob.contentType().setValue(uploadedData.contentMimeType);

        Persistence.service().persist(blob);
        response.blobKey().setValue(blob.getPrimaryKey());

        ApplicationDocumentUploadedBlobSecurityAdapterImpl.blobUploaded(blob.getPrimaryKey());

        Persistence.service().commit();
    }

}

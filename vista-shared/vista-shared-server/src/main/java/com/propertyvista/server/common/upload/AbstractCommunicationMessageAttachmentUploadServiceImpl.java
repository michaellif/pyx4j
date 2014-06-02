/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 2, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.server.common.upload;

import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadedData;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.blob.MessageAttachmentBlob;
import com.propertyvista.domain.blob.MaintenanceRequestPictureBlob;

public class AbstractCommunicationMessageAttachmentUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, MessageAttachmentBlob> {
    public AbstractCommunicationMessageAttachmentUploadServiceImpl() {
    }

    private static final I18n i18n = I18n.get(AbstractCommunicationMessageAttachmentUploadServiceImpl.class);

    public static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.JPEG, DownloadFormat.GIF, DownloadFormat.PNG,
            DownloadFormat.BMP, DownloadFormat.PDF, DownloadFormat.ARCHIVE, DownloadFormat.TXT, DownloadFormat.DOCX, DownloadFormat.RTF);

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(MaintenanceRequestPictureBlob.class).data().getMeta().getLength();
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Maintenance Request Picture");
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    protected void processUploadedData(IEntity uploadInitiationData, UploadedData uploadedData, IFile<MessageAttachmentBlob> response) {
        MessageAttachmentBlob blob = EntityFactory.create(MessageAttachmentBlob.class);
        blob.contentType().setValue(uploadedData.contentMimeType);
        blob.data().setValue(uploadedData.binaryContent);
        Persistence.service().persist(blob);

        response.blobKey().setValue(blob.getPrimaryKey());

        Persistence.service().commit();

    }
}
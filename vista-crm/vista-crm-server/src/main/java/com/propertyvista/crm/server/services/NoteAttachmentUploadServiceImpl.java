/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-20
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadedData;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.NoteAttachmentUploadService;
import com.propertyvista.domain.note.NoteAttachment;
import com.propertyvista.server.domain.FileBlob;
import com.propertyvista.server.domain.NoteAttachmentBlob;

public class NoteAttachmentUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, NoteAttachment> implements NoteAttachmentUploadService {

    private static final I18n i18n = I18n.get(NoteAttachmentUploadServiceImpl.class);

    public NoteAttachmentUploadServiceImpl() {
        super(NoteAttachment.class);
    }

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(FileBlob.class).content().getMeta().getLength();
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Attachment");
    }

    @Override
    protected void processUploadedData(IEntity uploadInitiationData, UploadedData uploadedData, NoteAttachment response) {
        NoteAttachmentBlob blob = EntityFactory.create(NoteAttachmentBlob.class);
        blob.contentType().setValue(uploadedData.contentMimeType);
        blob.data().setValue(uploadedData.binaryContent);
        Persistence.service().persist(blob);

        response.blobKey().setValue(blob.getPrimaryKey());

        Persistence.service().commit();
    }

}

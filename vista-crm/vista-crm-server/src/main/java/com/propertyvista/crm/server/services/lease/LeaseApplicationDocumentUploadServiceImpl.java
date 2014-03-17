/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-02-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease;

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

import com.propertyvista.crm.rpc.services.lease.LeaseApplicationDocumentUploadService;
import com.propertyvista.domain.blob.LeaseApplicationDocumentBlob;
import com.propertyvista.domain.blob.LeaseTermAgreementDocumentBlob;

public class LeaseApplicationDocumentUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, LeaseApplicationDocumentBlob> implements
        LeaseApplicationDocumentUploadService {

    private static final I18n i18n = I18n.get(LeaseApplicationDocumentUploadServiceImpl.class);

    public static final Collection<DownloadFormat> supportedFormats = EnumSet.of(//@formatter:off
            DownloadFormat.JPEG,
            DownloadFormat.GIF, 
            DownloadFormat.PNG,
            DownloadFormat.BMP,
            DownloadFormat.PDF
    );//@formatter:on

    public LeaseApplicationDocumentUploadServiceImpl() {

    }

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(LeaseTermAgreementDocumentBlob.class).data().getMeta().getLength();
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Signed Lease Application Document");
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    protected void processUploadedData(IEntity entity, UploadedData uploadedData, IFile<LeaseApplicationDocumentBlob> response) {
        LeaseApplicationDocumentBlob blob = EntityFactory.create(LeaseApplicationDocumentBlob.class);
        blob.contentType().setValue(uploadedData.contentMimeType);
        blob.data().setValue(uploadedData.binaryContent);
        Persistence.service().persist(blob);
        Persistence.service().commit();
        response.blobKey().setValue(blob.getPrimaryKey());
    }
}

/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 6, 2013
 * @author vlads
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

import com.propertyvista.domain.blob.ProofOfIncomeDocumentBlob;

public abstract class AbstractProofOfIncomeDocumentUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, ProofOfIncomeDocumentBlob> {

    private static final I18n i18n = I18n.get(AbstractProofOfIncomeDocumentUploadServiceImpl.class);

    private static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.JPEG, DownloadFormat.GIF, DownloadFormat.PNG,
            DownloadFormat.TIF, DownloadFormat.BMP, DownloadFormat.PDF);

    public AbstractProofOfIncomeDocumentUploadServiceImpl() {
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(ProofOfIncomeDocumentBlob.class).data().getMeta().getLength();
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Proof Of Income Application");
    }

    @Override
    protected void processUploadedData(IEntity uploadInitiationData, UploadedData uploadedData, IFile<ProofOfIncomeDocumentBlob> response) {
        ProofOfIncomeDocumentBlob blob = EntityFactory.create(ProofOfIncomeDocumentBlob.class);
        blob.data().setValue(uploadedData.binaryContent);
        blob.contentType().setValue(uploadedData.contentMimeType);

        Persistence.service().persist(blob);
        response.blobKey().setValue(blob.getPrimaryKey());

        Persistence.service().commit();
    }

}

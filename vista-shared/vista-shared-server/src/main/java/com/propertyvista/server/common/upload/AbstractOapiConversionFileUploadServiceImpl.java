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

import com.propertyvista.operations.domain.imports.blob.OapiConversionBlob;

public class AbstractOapiConversionFileUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, OapiConversionBlob> {

    public static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.XLS, DownloadFormat.XLSX);

    private static final I18n i18n = I18n.get(AbstractOapiConversionFileUploadServiceImpl.class);

    public AbstractOapiConversionFileUploadServiceImpl() {
    }

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(OapiConversionBlob.class).data().getMeta().getLength();
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("OAPI Conversion file");
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    protected void processUploadedData(IEntity uploadInitiationData, UploadedData uploadedData, IFile<OapiConversionBlob> response) {
        OapiConversionBlob blob = EntityFactory.create(OapiConversionBlob.class);
        blob.contentType().setValue(uploadedData.contentMimeType);
        blob.data().setValue(uploadedData.binaryContent);
        Persistence.service().persist(blob);

        response.blobKey().setValue(blob.getPrimaryKey());

        Persistence.service().commit();
    }

}

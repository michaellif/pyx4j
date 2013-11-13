/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.services;

import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadedData;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.portal.rpc.portal.resident.services.services.InsuranceCertificateScanUploadService;
import com.propertyvista.portal.server.portal.resident.services.ResidentPictureUploadServiceImpl;
import com.propertyvista.server.domain.GeneralInsurancePolicyBlob;

public class InsuranceCertificateScanUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, InsuranceCertificateScan> implements
        InsuranceCertificateScanUploadService {

    public static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.JPEG, DownloadFormat.GIF, DownloadFormat.PNG,
            DownloadFormat.PDF, DownloadFormat.ARCHIVE);

    private static final I18n i18n = I18n.get(ResidentPictureUploadServiceImpl.class);

    public InsuranceCertificateScanUploadServiceImpl() {
        super(InsuranceCertificateScan.class);
    }

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(GeneralInsurancePolicyBlob.class).data().getMeta().getLength();
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Insurance Certificate Scan");
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    protected void processUploadedData(IEntity uploadInitiationData, UploadedData uploadedData, InsuranceCertificateScan response) {
        GeneralInsurancePolicyBlob blob = EntityFactory.create(GeneralInsurancePolicyBlob.class);
        blob.contentType().setValue(uploadedData.contentMimeType);
        blob.data().setValue(uploadedData.binaryContent);
        Persistence.service().persist(blob);

        response.blobKey().setValue(blob.getPrimaryKey());

        Persistence.service().commit();
    }
}
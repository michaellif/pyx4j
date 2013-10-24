/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-24
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.organization;

import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadedData;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.organization.EmployeeSignatureUploadService;
import com.propertyvista.domain.company.EmployeeSignature;
import com.propertyvista.server.domain.EmployeeSignatureBlob;
import com.propertyvista.server.domain.FileBlob;

public class EmployeeSignatureUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, EmployeeSignature> implements EmployeeSignatureUploadService {

    //    private static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.JPEG, DownloadFormat.GIF, DownloadFormat.PNG,
//            DownloadFormat.BMP);
    private static final I18n i18n = I18n.get(EmployeeSignatureUploadServiceImpl.class);

    private static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.PNG, DownloadFormat.JPEG);

    public EmployeeSignatureUploadServiceImpl() {
        super(EmployeeSignature.class);

    }

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(FileBlob.class).content().getMeta().getLength();

    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Employee's Signature");
    }

    @Override
    protected void processUploadedData(IEntity uploadInitiationData, UploadedData uploadedData, EmployeeSignature response) {
        EmployeeSignatureBlob blob = EntityFactory.create(EmployeeSignatureBlob.class);
        blob.contentType().setValue(uploadedData.contentMimeType);
        blob.data().setValue(uploadedData.binaryContent);
        Persistence.service().persist(blob);

        response.blobKey().setValue(blob.getPrimaryKey());
        Persistence.service().commit();
    }

}

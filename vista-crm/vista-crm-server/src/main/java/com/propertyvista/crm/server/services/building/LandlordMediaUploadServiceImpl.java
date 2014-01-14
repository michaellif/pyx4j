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
package com.propertyvista.crm.server.services.building;

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

import com.propertyvista.crm.rpc.services.building.LandlordMediaUploadService;
import com.propertyvista.domain.blob.LandlordMediaBlob;

public class LandlordMediaUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, LandlordMediaBlob> implements LandlordMediaUploadService {

    private static final I18n i18n = I18n.get(LandlordMediaUploadServiceImpl.class);

    private static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.PNG, DownloadFormat.JPEG);

    public LandlordMediaUploadServiceImpl() {
    }

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(LandlordMediaBlob.class).data().getMeta().getLength();

    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Landlord Media");
    }

    @Override
    protected void processUploadedData(IEntity uploadInitiationData, UploadedData uploadedData, IFile<LandlordMediaBlob> response) {
        LandlordMediaBlob blob = EntityFactory.create(LandlordMediaBlob.class);
        blob.contentType().setValue(uploadedData.contentMimeType);
        blob.data().setValue(uploadedData.binaryContent);
        Persistence.service().persist(blob);

        response.blobKey().setValue(blob.getPrimaryKey());
        Persistence.service().commit();
    }

}

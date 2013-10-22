/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.Callable;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadedData;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.PmcDocumentFileUploadService;
import com.propertyvista.domain.pmc.info.PmcDocumentFile;
import com.propertyvista.server.domain.PmcDocumentBlob;
import com.propertyvista.server.jobs.TaskRunner;

public class PmcDocumentFileUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, PmcDocumentFile> implements PmcDocumentFileUploadService {

    private static final I18n i18n = I18n.get(I18n.class);

    public static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.JPEG, DownloadFormat.GIF, DownloadFormat.PNG,
            DownloadFormat.BMP);

    public PmcDocumentFileUploadServiceImpl() {
        super(PmcDocumentFile.class);
    }

    @Override
    public long getMaxSize() {
        return EntityFactory.getEntityPrototype(PmcDocumentBlob.class).data().getMeta().getLength();
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Business Identification");
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    protected void processUploadedData(IEntity uploadInitiationData, final UploadedData uploadedData, final PmcDocumentFile response) {
        TaskRunner.runInOperationsNamespace(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                PmcDocumentBlob blob = EntityFactory.create(PmcDocumentBlob.class);
                blob.contentType().setValue(uploadedData.contentMimeType);
                blob.data().setValue(uploadedData.binaryContent);
                Persistence.service().persist(blob);

                response.blobKey().setValue(blob.getPrimaryKey());

                Persistence.service().commit();
                return null;
            }
        });

    }

}

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
import java.util.concurrent.Callable;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.DeferredUploadProcess;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.PmcDocumentFileUploadService;
import com.propertyvista.server.domain.PmcDocumentBlob;
import com.propertyvista.server.jobs.TaskRunner;

public class PmcDocumentFileUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, IFile> implements PmcDocumentFileUploadService {

    private static final I18n i18n = I18n.get(I18n.class);

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
        return DownloadFormat.getExtensions(SUPPORTED_FORMATS);
    }

    @Override
    public com.pyx4j.essentials.server.upload.UploadReciver.ProcessingStatus onUploadReceived(final UploadData data,
            DeferredUploadProcess<IEntity, IFile> process, final UploadResponse<IFile> response) {
        return TaskRunner.runInOperationsNamespace(new Callable<ProcessingStatus>() {

            @Override
            public com.pyx4j.essentials.server.upload.UploadReciver.ProcessingStatus call() throws Exception {
                PmcDocumentBlob blob = EntityFactory.create(PmcDocumentBlob.class);
                blob.data().setValue(data.data);
                blob.contentType().setValue(response.fileContentType);
                Persistence.service().persist(blob);
                Persistence.service().commit();
                response.uploadKey = blob.getPrimaryKey();
                return ProcessingStatus.completed;
            }
        });

    }

}

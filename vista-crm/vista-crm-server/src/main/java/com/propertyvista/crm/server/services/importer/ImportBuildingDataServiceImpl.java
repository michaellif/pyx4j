/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 15, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.importer;

import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.shared.AbstractIFileBlob;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadedData;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.gwt.server.deferred.IDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.dto.DeferredProcessingStarted;
import com.propertyvista.crm.rpc.services.importer.ImportBuildingDataService;

public class ImportBuildingDataServiceImpl extends AbstractUploadServiceImpl<IEntity, DeferredProcessingStarted> implements ImportBuildingDataService {

    private static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.XML);

    public ImportBuildingDataServiceImpl() {

    }

    @Override
    public long getMaxSize() {
        return 10 * 1024 * 1024;
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public String getUploadFileTypeName() {
        return "Vista CRM Building Data";
    }

    @Override
    protected void processUploadedData(IEntity uploadInitiationData, UploadedData uploadedData, IFile<DeferredProcessingStarted> response) {

        boolean mock = true;

        IDeferredProcess process;
        if (mock) {
            process = new ImportBuildingDataDeferredProcessMock();
        } else {
            process = new ImportBuildingDataDeferredProcess(uploadInitiationData, uploadedData);
        }

        String deferredCorrelationId = DeferredProcessRegistry.fork(process, ThreadPoolNames.IMPORTS);

        DeferredProcessingStarted blob = EntityFactory.create(DeferredProcessingStarted.class);
        blob.deferredCorrelationId().setValue(deferredCorrelationId);
        // Magic of java
        ((AbstractIFileBlob) response.blob()).set(blob);

    }
}

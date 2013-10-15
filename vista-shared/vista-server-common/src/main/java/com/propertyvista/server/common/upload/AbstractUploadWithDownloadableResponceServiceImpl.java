/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.upload;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.DeferredUploadProcess;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.dto.DownloadableUploadResponseDTO;

public abstract class AbstractUploadWithDownloadableResponceServiceImpl<U extends IEntity> extends AbstractUploadServiceImpl<U, DownloadableUploadResponseDTO> {

    @Override
    protected abstract AbstractUploadWithDownloadableResponceDeferredProcess<U> createUploadDeferredProcess(U data);

    @Override
    public final com.pyx4j.essentials.server.upload.UploadReciver.ProcessingStatus onUploadReceived(UploadData data,
            DeferredUploadProcess<U, DownloadableUploadResponseDTO> process, UploadResponse<DownloadableUploadResponseDTO> response) {
        process.onUploadReceived(data, response);
        DeferredProcessRegistry.start(data.deferredCorrelationId, process, ThreadPoolNames.IMPORTS);
        return ProcessingStatus.processWillContinue;

    }

}

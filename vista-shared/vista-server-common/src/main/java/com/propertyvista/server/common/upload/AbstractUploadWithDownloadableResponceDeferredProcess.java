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

import com.pyx4j.entity.shared.AbstractIFileBlob;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.essentials.server.upload.DeferredUploadProcess;
import com.pyx4j.essentials.server.upload.UploadedData;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.dto.DownloadableUploadResponseDTO;

public abstract class AbstractUploadWithDownloadableResponceDeferredProcess<U extends IEntity> extends DeferredUploadProcess<U, AbstractIFileBlob> {

    private static final long serialVersionUID = 1L;

    private DownloadableUploadResponseDTO response;

    private byte[] binaryData;

    public AbstractUploadWithDownloadableResponceDeferredProcess(U data) {
        super(data);
    }

    @Override
    public abstract void execute();

    @Override
    protected void onUploadProcessed(final UploadedData data, final IFile<AbstractIFileBlob> response) {
        // DO NOT Call super. the process will be Completed by execute() implementation 
        binaryData = data.binaryContent;
        this.response = EntityFactory.create(DownloadableUploadResponseDTO.class);
        // Continue execution of process
        DeferredProcessRegistry.start(data.deferredCorrelationId, this, ThreadPoolNames.IMPORTS);
    }

    protected final byte[] getBinaryData() {
        return binaryData;
    }

    @Override
    public DownloadableUploadResponseDTO getResponse() {
        return response;
    }

}

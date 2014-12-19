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
 */
package com.propertyvista.server.common.upload;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.shared.AbstractIFileBlob;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadedData;

public abstract class AbstractUploadWithDownloadableResponceServiceImpl<U extends IEntity> extends AbstractUploadServiceImpl<U, AbstractIFileBlob> {

    protected AbstractUploadWithDownloadableResponceServiceImpl() {
    }

    @Override
    protected abstract AbstractUploadWithDownloadableResponceDeferredProcess<U> createUploadDeferredProcess(U data);

    @Override
    protected void processUploadedData(U uploadInitiationData, UploadedData uploadedData, IFile<AbstractIFileBlob> response) {
    }

}

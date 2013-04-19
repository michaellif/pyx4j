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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;
import com.pyx4j.gwt.rpc.upload.UploadResponse;

import com.propertyvista.dto.DownloadableUploadResponseDTO;

public abstract class AbstractUploadWithDownloadableResponceDeferredProcess<U extends IEntity> extends UploadDeferredProcess<U, DownloadableUploadResponseDTO> {

    private static final long serialVersionUID = 1L;

    private byte[] binaryData;

    public AbstractUploadWithDownloadableResponceDeferredProcess(U data) {
        super(data);
    }

    @Override
    public final void onUploadReceived(UploadData data, UploadResponse<DownloadableUploadResponseDTO> response) {
        binaryData = data.data;
        response.data = EntityFactory.create(DownloadableUploadResponseDTO.class);
        response.data.success().setValue(Boolean.FALSE);
    }

    protected final byte[] getBinaryData() {
        return binaryData;
    }

}

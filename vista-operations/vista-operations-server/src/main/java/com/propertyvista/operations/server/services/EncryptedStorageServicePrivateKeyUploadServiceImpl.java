/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.sun.jersey.core.util.Base64;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.DeferredUploadProcess;
import com.pyx4j.gwt.rpc.upload.UploadResponse;

import com.propertyvista.biz.system.encryption.EncryptedStorageFacade;
import com.propertyvista.operations.rpc.dto.PrivateKeyDTO;
import com.propertyvista.operations.rpc.services.EncryptedStorageServicePrivateKeyUploadService;

public class EncryptedStorageServicePrivateKeyUploadServiceImpl extends AbstractUploadServiceImpl<PrivateKeyDTO, PrivateKeyDTO> implements
        EncryptedStorageServicePrivateKeyUploadService {

    @Override
    public long getMaxSize() {
        return 10 * 1024;
    }

    @Override
    public String getUploadFileTypeName() {
        return "Primary Key";
    }

    @Override
    public com.pyx4j.essentials.server.upload.UploadReciver.ProcessingStatus onUploadReceived(UploadData data,
            DeferredUploadProcess<PrivateKeyDTO, PrivateKeyDTO> process, UploadResponse<PrivateKeyDTO> response) {

        byte[] keyData = Base64.decode(data.data);

        ServerSideFactory.create(EncryptedStorageFacade.class).uploadPrivateKey(process.getData().publicKeyKey().getValue(), keyData,
                process.getData().password().getValue().getValue());

        return ProcessingStatus.completed;
    }

}

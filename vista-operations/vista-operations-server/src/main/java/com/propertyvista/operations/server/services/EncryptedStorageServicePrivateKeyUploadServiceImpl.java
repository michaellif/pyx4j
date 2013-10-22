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
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadedData;

import com.propertyvista.biz.system.encryption.EncryptedStorageFacade;
import com.propertyvista.operations.rpc.dto.PrivateKeyDTO;
import com.propertyvista.operations.rpc.services.EncryptedStorageServicePrivateKeyUploadService;

public class EncryptedStorageServicePrivateKeyUploadServiceImpl extends AbstractUploadServiceImpl<PrivateKeyDTO, IFile> implements
        EncryptedStorageServicePrivateKeyUploadService {

    public EncryptedStorageServicePrivateKeyUploadServiceImpl() {
        super(IFile.class);
    }

    @Override
    public long getMaxSize() {
        return 10 * 1024;
    }

    @Override
    public String getUploadFileTypeName() {
        return "Primary Key";
    }

    @Override
    protected void processUploadedData(PrivateKeyDTO uploadInitiationData, UploadedData uploadedData, IFile response) {
        byte[] keyData = Base64.decode(uploadedData.binaryContent);

        ServerSideFactory.create(EncryptedStorageFacade.class).uploadPrivateKey(uploadInitiationData.publicKeyKey().getValue(), keyData,
                uploadInitiationData.password().getValue().getValue());
    }

}

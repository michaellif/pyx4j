/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.PasswordSerializable;

import com.propertyvista.biz.system.encryption.EncryptedStorageFacade;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;
import com.propertyvista.operations.rpc.services.EncryptedStorageService;

public class EncryptedStorageServiceImpl implements EncryptedStorageService {

    private final static Logger log = LoggerFactory.getLogger(EncryptedStorageServiceImpl.class);

    @Override
    public void getSystemState(AsyncCallback<EncryptedStorageDTO> callback) {
        callback.onSuccess(ServerSideFactory.create(EncryptedStorageFacade.class).getSystemState());
    }

    @Override
    public void activateCurrentKeyDecryption(AsyncCallback<VoidSerializable> callback, PasswordSerializable password) {
        try {
            ServerSideFactory.create(EncryptedStorageFacade.class).activateDecryption(
                    ServerSideFactory.create(EncryptedStorageFacade.class).getCurrentPublicKey(), password.getValue());
            callback.onSuccess(null);
        } finally {
            password.destroy();
        }
    }

    @Override
    public void deactivateDecryption(AsyncCallback<VoidSerializable> callback) {
        ServerSideFactory.create(EncryptedStorageFacade.class).deactivateDecryption();
        callback.onSuccess(null);
    }

    @Override
    public void createNewKeyPair(AsyncCallback<String> callback, PasswordSerializable password) {
        try {
            log.info("Creating new KeyPair");
            byte[] keyData = ServerSideFactory.create(EncryptedStorageFacade.class).createNewKeyPair(password.getValue());
            byte[] binaryDataAsText = Base64.encodeBase64(keyData);
            Downloadable d = new Downloadable(binaryDataAsText, MimeMap.getContentType(DownloadFormat.TXT));
            String fileName = "key-" + new SimpleDateFormat("YYYY-MM-dd").format(new Date()) + ".key";
            d.save(fileName);
            callback.onSuccess(fileName);
        } finally {
            password.destroy();
        }
    }

    @Override
    public void makeCurrent(AsyncCallback<VoidSerializable> callback, Key publicKeyKey) {
        ServerSideFactory.create(EncryptedStorageFacade.class).makeCurrent(publicKeyKey);
        callback.onSuccess(null);
    }

    @Override
    public void activateDecryption(AsyncCallback<VoidSerializable> callback, Key publicKeyKey, PasswordSerializable password) {
        try {
            ServerSideFactory.create(EncryptedStorageFacade.class).activateDecryption(publicKeyKey, password.getValue());
            callback.onSuccess(null);
        } finally {
            password.destroy();
        }
    }

    @Override
    public void startKeyRotation(AsyncCallback<String> callback, Key publicKeyKey) {
        callback.onSuccess(ServerSideFactory.create(EncryptedStorageFacade.class).startKeyRotation(publicKeyKey));
    }

    @Override
    public void deactivateDecryption(AsyncCallback<VoidSerializable> callback, Key publicKeyKey) {
        ServerSideFactory.create(EncryptedStorageFacade.class).deactivateDecryption(publicKeyKey);
        callback.onSuccess(null);
    }

}

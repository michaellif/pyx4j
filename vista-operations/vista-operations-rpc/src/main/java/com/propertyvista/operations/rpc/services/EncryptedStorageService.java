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
 */
package com.propertyvista.operations.rpc.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.PasswordSerializable;

import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;

public interface EncryptedStorageService extends IService {

    void getSystemState(AsyncCallback<EncryptedStorageDTO> callback);

    void activateCurrentKeyDecryption(AsyncCallback<VoidSerializable> callback, PasswordSerializable password);

    void deactivateDecryption(AsyncCallback<VoidSerializable> callback);

    void createNewKeyPair(AsyncCallback<String> callback, PasswordSerializable password);

    void makeCurrent(AsyncCallback<VoidSerializable> callback, Key publicKeyKey);

    void activateDecryption(AsyncCallback<VoidSerializable> callback, Key publicKeyKey, PasswordSerializable password);

    void deactivateDecryption(AsyncCallback<VoidSerializable> callback, Key publicKeyKey);

    void startKeyRotation(AsyncCallback<String> callback, Key publicKeyKey);

}

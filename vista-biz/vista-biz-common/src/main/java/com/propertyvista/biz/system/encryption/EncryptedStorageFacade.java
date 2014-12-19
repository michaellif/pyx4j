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
 * @author vlads
 */
package com.propertyvista.biz.system.encryption;

import java.util.concurrent.atomic.AtomicInteger;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;

import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;

public interface EncryptedStorageFacade {

    /* User API used in EquifaxEncryptedStorage */

    public boolean isStorageAvalable();

    public Key getCurrentPublicKey();

    public byte[] encrypt(Key publicKeyKey, byte[] data);

    public byte[] decrypt(Key publicKeyKey, byte[] data) throws UserRuntimeException;

    /* Administration API used in Vista Operations */

    void preloaderTestKey();

    EncryptedStorageDTO getSystemState();

    byte[] createNewKeyPair(char[] password);

    void makeCurrent(Key publicKeyKey);

    String startKeyRotation(Key publicKeyKey);

    void keyRotationProcess(final AtomicInteger progress, Key fromPublicKeyKey, Key toPublicKeyKey);

    void uploadPrivateKey(Key publicKeyKey, byte[] encryptedPrivateKeyData, char[] password);

    void removePrivateKey(Key publicKeyKey);

    /**
     * Read Private Key from FS and decrypt it to memory.
     * After this call key can be used for decrypt operation.
     * 
     * @param publicKeyKey
     * @param password
     */
    void activateDecryption(Key publicKeyKey, char[] password);

    void deactivateDecryption(Key publicKeyKey);

    void deactivateDecryption();

}

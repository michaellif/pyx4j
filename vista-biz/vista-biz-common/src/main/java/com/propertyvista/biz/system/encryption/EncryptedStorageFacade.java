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
 * @version $Id$
 */
package com.propertyvista.biz.system.encryption;

import com.pyx4j.commons.Key;

import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;

public interface EncryptedStorageFacade {

    /* User API used in EquifaxEncryptedStorage */

    public Key getCurrentPublicKey();

    public byte[] encrypt(Key publicKeyKey, byte[] data);

    public byte[] decrypt(Key publicKeyKey, byte[] data);

    /* Administration API used in Vista Operations */

    EncryptedStorageDTO getSystemState();

    byte[] createNewKeyPair(char[] password);

    void makeCurrent(Key publicKeyKey);

    void startKeyRotation(Key publicKeyKey);

    void uploadPrivateKey(Key publicKeyKey, byte[] encryptedPrivateKeyData);

    /**
     * Read Private Key from FS and decrypt it to memory.
     * After this call key can be used for decrypt operation.
     * 
     * @param publicKeyKey
     * @param passwrord
     */
    void activateDecryption(Key publicKeyKey, char[] passwrord);

    void deactivateDecryption(Key publicKeyKey);

    void deactivateDecryption();

}

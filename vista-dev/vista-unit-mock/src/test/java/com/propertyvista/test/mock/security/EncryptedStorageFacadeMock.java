/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2014
 * @author vlads
 */
package com.propertyvista.test.mock.security;

import java.util.concurrent.atomic.AtomicInteger;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.system.encryption.EncryptedStorageFacade;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;

public class EncryptedStorageFacadeMock implements EncryptedStorageFacade {

    @Override
    public boolean isStorageAvalable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Key getCurrentPublicKey() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] encrypt(Key publicKeyKey, byte[] data) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] decrypt(Key publicKeyKey, byte[] data) throws UserRuntimeException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void preloaderTestKey() {
        // TODO Auto-generated method stub

    }

    @Override
    public EncryptedStorageDTO getSystemState() {
        EncryptedStorageDTO infoDto = EntityFactory.create(EncryptedStorageDTO.class);
        return infoDto;
    }

    @Override
    public byte[] createNewKeyPair(char[] password) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void makeCurrent(Key publicKeyKey) {
        // TODO Auto-generated method stub

    }

    @Override
    public String startKeyRotation(Key publicKeyKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void keyRotationProcess(AtomicInteger progress, Key fromPublicKeyKey, Key toPublicKeyKey) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uploadPrivateKey(Key publicKeyKey, byte[] encryptedPrivateKeyData, char[] password) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removePrivateKey(Key publicKeyKey) {
        // TODO Auto-generated method stub

    }

    @Override
    public void activateDecryption(Key publicKeyKey, char[] password) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deactivateDecryption(Key publicKeyKey) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deactivateDecryption() {
        // TODO Auto-generated method stub

    }

}

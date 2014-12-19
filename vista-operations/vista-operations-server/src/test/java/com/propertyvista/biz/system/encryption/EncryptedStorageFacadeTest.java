/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-13
 * @author vlads
 */
package com.propertyvista.biz.system.encryption;

import java.security.SecureRandom;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;

public class EncryptedStorageFacadeTest {

    @Before
    public void setUp() {
        VistaTestDBSetup.init();
    }

    private byte[] generateTestData() {
        SecureRandom random = new SecureRandom();
        int len = 2 * 1024 + random.nextInt(5 * 1024);
        byte data[] = new byte[len];
        random.nextBytes(data);
        return data;
    }

    private void verifyEncryptDecryptData(Key publicKeyKey) {
        byte data[] = generateTestData();

        byte[] encryptedData = ServerSideFactory.create(EncryptedStorageFacade.class).encrypt(publicKeyKey, data);

        Assert.assertFalse(EqualsHelper.equals(data, encryptedData));

        byte[] decryptedData = ServerSideFactory.create(EncryptedStorageFacade.class).decrypt(publicKeyKey, encryptedData);

        Assert.assertArrayEquals(data, decryptedData);
    }

    @Test
    public void testKeyCreation() {
        NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);

        char[] password = "Test1".toCharArray();
        EncryptedStorageDTO state;
        EncryptedStorageKeyDTO keyState;

        byte[] encryptedPrivateKeyData = ServerSideFactory.create(EncryptedStorageFacade.class).createNewKeyPair(password);

        state = ServerSideFactory.create(EncryptedStorageFacade.class).getSystemState();

        Assert.assertEquals("One Key", 1, state.keys().size());
        keyState = state.keys().get(0);

        Assert.assertTrue(keyState.decryptionEnabled().getValue());

        Key publicKeyKey = keyState.getPrimaryKey();
        verifyEncryptDecryptData(publicKeyKey);

        ServerSideFactory.create(EncryptedStorageFacade.class).deactivateDecryption(publicKeyKey);
        state = ServerSideFactory.create(EncryptedStorageFacade.class).getSystemState();
        keyState = state.keys().get(0);
        Assert.assertFalse(keyState.decryptionEnabled().getValue());

        // See that system can encrypt when there are no master password
        {
            byte data[] = generateTestData();
            byte[] encryptedData = ServerSideFactory.create(EncryptedStorageFacade.class).encrypt(publicKeyKey, data);

            try {
                ServerSideFactory.create(EncryptedStorageFacade.class).decrypt(publicKeyKey, encryptedData);
                Assert.fail("Should be impossible to decrypt");
            } catch (UserRuntimeException ok) {
            }

            ServerSideFactory.create(EncryptedStorageFacade.class).activateDecryption(publicKeyKey, password);

            byte[] decryptedData = ServerSideFactory.create(EncryptedStorageFacade.class).decrypt(publicKeyKey, encryptedData);
            Assert.assertArrayEquals(data, decryptedData);
        }

        // Remove and upload key again
        {
            ServerSideFactory.create(EncryptedStorageFacade.class).removePrivateKey(publicKeyKey);
            try {
                ServerSideFactory.create(EncryptedStorageFacade.class).activateDecryption(publicKeyKey, password);
                Assert.fail("Should be impossible to activate");
            } catch (UserRuntimeException ok) {
            }
            ServerSideFactory.create(EncryptedStorageFacade.class).uploadPrivateKey(publicKeyKey, encryptedPrivateKeyData, password);

            // Now back to normal
            verifyEncryptDecryptData(publicKeyKey);
        }

        ServerSideFactory.create(EncryptedStorageFacade.class).removePrivateKey(publicKeyKey);
    }
}

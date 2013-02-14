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

import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.crypto.Cipher;

import org.jasypt.util.binary.BasicBinaryEncryptor;
import org.jasypt.util.binary.BinaryEncryptor;
import org.jasypt.util.binary.StrongBinaryEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.EncryptedStorageConfiguration;
import com.propertyvista.operations.domain.encryption.EncryptedStorageCurrentKey;
import com.propertyvista.operations.domain.encryption.EncryptedStoragePublicKey;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;

public class EncryptedStorageFacadeImpl implements EncryptedStorageFacade {

    private static final Logger log = LoggerFactory.getLogger(EncryptedStorageFacadeImpl.class);

    private static final I18n i18n = I18n.get(EncryptedStorageFacadeImpl.class);

    private static Map<Key, PrivateKey> activeKeys = new HashMap<Key, PrivateKey>();

    @Override
    public Key getCurrentPublicKey() {
        EncryptedStorageCurrentKey current = Persistence.service().retrieve(EntityQueryCriteria.create(EncryptedStorageCurrentKey.class));
        if (current != null) {
            return current.current().getPrimaryKey();
        } else {
            return null;
        }
    }

    @Override
    public byte[] encrypt(Key publicKeyKey, byte[] data) {
        EncryptedStoragePublicKey publicKey = Persistence.service().retrieve(EncryptedStoragePublicKey.class, publicKeyKey);
        return encrypt(publicKey, data);
    }

    @Override
    public byte[] decrypt(Key publicKeyKey, byte[] data) throws UserRuntimeException {
        PrivateKey privateKey = activeKeys.get(publicKeyKey);
        if (privateKey == null) {
            throw new UserRuntimeException(i18n.tr("Data Decryption not possible, Contact support to activate decryption"));
        }
        return decrypt(privateKey, data);
    }

    private byte[] encrypt(EncryptedStoragePublicKey publicKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey.keyData().getValue());
            cipher.init(Cipher.ENCRYPT_MODE, keyFactory.generatePublic(publicKeySpec));
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new Error(e);
        }
    }

    private byte[] decrypt(PrivateKey privateKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new Error(e);
        }
    }

    private PrivateKey createPrivateKey(byte[] encryptedPrivateKeyBytes, char[] password) {
        byte[] privateKeyBinary = getBinaryEncryptor(password).decrypt(encryptedPrivateKeyBytes);
        PrivateKey privateKey;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBinary);
            privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (GeneralSecurityException e) {
            throw new Error(e);
        } finally {
            destroy(privateKeyBinary);
        }
        return privateKey;
    }

    private void destroy(byte[] binary) {
        if (binary != null) {
            for (int i = 0; i < binary.length; i++) {
                binary[i] = 0;
            }
            binary = null;
        }
    }

    @Override
    public EncryptedStorageDTO getSystemState() {
        EncryptedStorageDTO infoDto = EntityFactory.create(EncryptedStorageDTO.class);

        EncryptedStorageCurrentKey current = Persistence.service().retrieve(EntityQueryCriteria.create(EncryptedStorageCurrentKey.class));

        EntityQueryCriteria<EncryptedStoragePublicKey> criteria = EntityQueryCriteria.create(EncryptedStoragePublicKey.class);
        for (EncryptedStoragePublicKey publicKey : Persistence.service().query(criteria)) {
            EncryptedStorageKeyDTO keyDto = EntityFactory.create(EncryptedStorageKeyDTO.class);
            keyDto.setPrimaryKey(publicKey.getPrimaryKey());

            if ((current != null) && publicKey.getPrimaryKey().equals(current.current().getPrimaryKey())) {
                keyDto.isCurrent().setValue(Boolean.TRUE);
            }
            keyDto.decryptionEnabled().setValue(activeKeys.get(publicKey.getPrimaryKey()) != null);
            infoDto.keys().add(keyDto);
        }

        return infoDto;
    }

    @Override
    public byte[] createNewKeyPair(char[] password) {
        ByteArrayOutputStream encryptedPrivateKeyBuffer = new ByteArrayOutputStream();
        generateKey(password, encryptedPrivateKeyBuffer);
        return encryptedPrivateKeyBuffer.toByteArray();
    }

    @Override
    public void makeCurrent(Key publicKeyKey) {
        EncryptedStorageCurrentKey current = Persistence.service().retrieve(EntityQueryCriteria.create(EncryptedStorageCurrentKey.class));
        if (current == null) {
            current = EntityFactory.create(EncryptedStorageCurrentKey.class);
        }
        current.current().setPrimaryKey(publicKeyKey);
        Persistence.service().persist(current);
        Persistence.service().commit();
    }

    @Override
    public void startKeyRotation(Key publicKeyKey) {
        if (publicKeyKey.equals(getCurrentPublicKey())) {
            throw new UserRuntimeException("Can't deactivate current key");
        }
        if (activeKeys.get(publicKeyKey) == null) {
            throw new UserRuntimeException("Can't deactivate current with not activated decryption");
        }
    }

    @Override
    public void uploadPrivateKey(Key publicKeyKey, byte[] encryptedPrivateKeyData, char[] password) {
        EncryptedStoragePublicKey publicKey = Persistence.service().retrieve(EncryptedStoragePublicKey.class, publicKeyKey);
        if (publicKey == null) {
            throw new UserRuntimeException("PublicKey not found");
        }
        testKeyDecryption(publicKey, encryptedPrivateKeyData, password);
        getPrivateKeyStorage().savePrivateKey(publicKey.name().getValue(), encryptedPrivateKeyData);
        activateDecryption(publicKey.getPrimaryKey(), password);
    }

    @Override
    public void removePrivateKey(Key publicKeyKey) {
        EncryptedStoragePublicKey publicKey = Persistence.service().retrieve(EncryptedStoragePublicKey.class, publicKeyKey);
        if (publicKey == null) {
            throw new UserRuntimeException("PublicKey not found");
        }
        deactivateDecryption(publicKeyKey);
        getPrivateKeyStorage().removePrivateKey(publicKey.name().getValue());
    }

    @Override
    public void activateDecryption(Key publicKeyKey, char[] password) {
        EncryptedStoragePublicKey publicKey = Persistence.service().retrieve(EncryptedStoragePublicKey.class, publicKeyKey);
        if (publicKey == null) {
            throw new UserRuntimeException("PublicKey not found");
        }
        PrivateKey privateKey = loadPrivateKey(publicKey, password);
        testKeyDecryption(publicKey, privateKey);
        activeKeys.put(publicKeyKey, privateKey);
    }

    private PrivateKey loadPrivateKey(EncryptedStoragePublicKey publicKey, char[] password) {
        byte[] encryptedPrivateKeyBytes = getPrivateKeyStorage().loadPrivateKey(publicKey.name().getValue());
        if (encryptedPrivateKeyBytes == null) {
            throw new UserRuntimeException("PrivateKey not found");
        }
        return createPrivateKey(encryptedPrivateKeyBytes, password);
    }

    private void automaticActivateDecryption() {
        if (!ApplicationMode.isDevelopment()) {
            return;
        }
        EncryptedStorageConfiguration config = getEncryptedStorageConfiguration();
    }

    @Override
    public void deactivateDecryption(Key publicKeyKey) {
        activeKeys.remove(publicKeyKey);
    }

    @Override
    public void deactivateDecryption() {
        activeKeys.clear();
    }

    private byte[] generateTestData(EncryptedStoragePublicKey publicKey) {
        SecureRandom random = new SecureRandom();
        int len = 128;//2 * 1024 + random.nextInt(5 * 1024);
        byte bytes[] = new byte[len];
        random.nextBytes(bytes);
        return bytes;
    }

    private void testKeyDecryption(EncryptedStoragePublicKey publicKey, byte[] encryptedPrivateKeyBytes, char[] password) {
        PrivateKey privateKey = createPrivateKey(encryptedPrivateKeyBytes, password);
        testKeyDecryption(publicKey, privateKey);
    }

    private void testKeyDecryption(EncryptedStoragePublicKey publicKey, PrivateKey privateKey) {
        byte[] decodedSrc = decrypt(privateKey, publicKey.encryptTestData().getValue());
        if (!EqualsHelper.equals(decodedSrc, publicKey.keyTestData().getValue())) {
            throw new Error("Decryption test failed");
        }
    }

    private void generateKey(char[] password, ByteArrayOutputStream encryptedPrivateKeyBuffer) {
        final EncryptedStoragePublicKey publicKey = EntityFactory.create(EncryptedStoragePublicKey.class);
        publicKey.name().setValue(new SimpleDateFormat("yyyy-MM-dd_HHmm").format(new Date()));
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);

            KeyPair keyPair = generator.genKeyPair();
            publicKey.keyData().setValue(keyPair.getPublic().getEncoded());
            publicKey.keyTestData().setValue(generateTestData(publicKey));
            publicKey.encryptTestData().setValue(encrypt(publicKey, publicKey.keyTestData().getValue()));
            {
                PrivateKey privateKey = keyPair.getPrivate();
                byte[] encryptedPrivateKeyBytes = getBinaryEncryptor(password).encrypt(privateKey.getEncoded());
                testKeyDecryption(publicKey, encryptedPrivateKeyBytes, password);
                getPrivateKeyStorage().savePrivateKey(publicKey.name().getValue(), encryptedPrivateKeyBytes);

                encryptedPrivateKeyBuffer.write(encryptedPrivateKeyBytes, 0, encryptedPrivateKeyBytes.length);
            }
        } catch (GeneralSecurityException e) {
            log.error("Error", e);
            throw new Error(e.getMessage());
        }

        try {
            UnitOfWork.execute(new Callable<Void>() {

                @Override
                public Void call() {
                    Persistence.service().persist(publicKey);
                    return null;
                }

            });
        } catch (Exception e) {
            throw new Error(e);
        }

        activateDecryption(publicKey.getPrimaryKey(), password);
    }

    static EncryptedStorageConfiguration getEncryptedStorageConfiguration() {
        return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getEncryptedStorageConfiguration();
    }

    private PrivateKeyStorage getPrivateKeyStorage() {
        EncryptedStorageConfiguration config = getEncryptedStorageConfiguration();
        switch (config.privateKeyStorageType()) {
        case file:
            return new PrivateKeyStorageFile();
        case noStorage:
            return new PrivateKeyStorageNop();
        case sftp:
            return new PrivateKeyStorageNop();
        default:
            throw new Error("Unsupported " + config.privateKeyStorageType());
        }
    }

    private BinaryEncryptor getBinaryEncryptor(char[] password) {
        if (ApplicationMode.isDevelopment()) {
            BasicBinaryEncryptor binaryEncryptor = new BasicBinaryEncryptor();
            binaryEncryptor.setPasswordCharArray(password);
            return binaryEncryptor;
        } else {
            StrongBinaryEncryptor binaryEncryptor = new StrongBinaryEncryptor();
            binaryEncryptor.setPasswordCharArray(password);
            return binaryEncryptor;

        }
    }

}

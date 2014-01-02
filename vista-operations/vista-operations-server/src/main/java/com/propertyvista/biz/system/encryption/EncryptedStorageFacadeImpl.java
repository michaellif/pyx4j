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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.binary.BasicBinaryEncryptor;
import org.jasypt.util.binary.BinaryEncryptor;
import org.jasypt.util.binary.StrongBinaryEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.EncryptedStorageConfiguration;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.operations.domain.encryption.EncryptedStorageCurrentKey;
import com.propertyvista.operations.domain.encryption.EncryptedStoragePublicKey;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;
import com.propertyvista.server.TaskRunner;

public class EncryptedStorageFacadeImpl implements EncryptedStorageFacade {

    private static final Logger log = LoggerFactory.getLogger(EncryptedStorageFacadeImpl.class);

    private static final I18n i18n = I18n.get(EncryptedStorageFacadeImpl.class);

    private static Map<Key, PrivateKey> activeKeys = new HashMap<Key, PrivateKey>();

    private static List<EncryptedStorageConsumer> consumers = registerConsumers();

    private static final int SYMMETRIC_KEY_PASSWORD_LENGTH = 28;

    private static List<EncryptedStorageConsumer> registerConsumers() {
        List<EncryptedStorageConsumer> consumers = new ArrayList<EncryptedStorageConsumer>();
        consumers.add(new EncryptedStorageConsumerEquifax());
        return consumers;
    }

    @Override
    public boolean isStorageAvalable() {
        return (getCurrentPublicKey() != null);
    }

    @Override
    public Key getCurrentPublicKey() {
        EncryptedStorageCurrentKey current = TaskRunner.runInOperationsNamespace(new Callable<EncryptedStorageCurrentKey>() {
            @Override
            public EncryptedStorageCurrentKey call() {
                return Persistence.service().retrieve(EntityQueryCriteria.create(EncryptedStorageCurrentKey.class));
            }
        });
        if (current != null) {
            return current.current().getPrimaryKey();
        } else {
            return null;
        }
    }

    @Override
    public byte[] encrypt(final Key publicKeyKey, byte[] data) {
        EncryptedStoragePublicKey publicKey = TaskRunner.runInOperationsNamespace(new Callable<EncryptedStoragePublicKey>() {
            @Override
            public EncryptedStoragePublicKey call() {
                return Persistence.service().retrieve(EncryptedStoragePublicKey.class, publicKeyKey);
            }
        });
        if (publicKey == null) {
            throw new UserRuntimeException(i18n.tr("Data Encryption not possible, Contact support to activate encryption"));
        }
        return encrypt(publicKey, data);
    }

    @Override
    public byte[] decrypt(Key publicKeyKey, byte[] data) throws UserRuntimeException {
        automaticActivateDecryption();
        PrivateKey privateKey = activeKeys.get(publicKeyKey);
        if (privateKey == null) {
            throw new UserRuntimeException(i18n.tr("Data Decryption not possible, Contact support to activate decryption"));
        }
        return decrypt(privateKey, data);
    }

    private byte[] encrypt(EncryptedStoragePublicKey publicKey, byte[] data) {
        try {
            Cipher cipherRSA = createRSACipher();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey.keyData().getValue());
            cipherRSA.init(Cipher.ENCRYPT_MODE, keyFactory.generatePublic(publicKeySpec));

            // Create createAESCipher and encrypt data using this AES
            SecureRandom random = new SecureRandom();
            StringBuilder password = new StringBuilder();
            while (password.length() < SYMMETRIC_KEY_PASSWORD_LENGTH) {
                password.append(Integer.toHexString(random.nextInt()));
            }

            SecretKey symmetricKey = createAESSecretKey(password.toString());
            Cipher symmetricCipher = createAESCipher();
            symmetricCipher.init(Cipher.ENCRYPT_MODE, symmetricKey);

            // Attach encrypted with RSA AESKey to data
            byte[] encodedSymmetricKey = cipherRSA.doFinal(symmetricKey.getEncoded());
            byte[] encodedSymmetricKeyLength = ByteBuffer.allocate(4).putInt(encodedSymmetricKey.length).array();
            byte[] cipherText = symmetricCipher.doFinal(data);

            byte[] encryptedData = new byte[4 + encodedSymmetricKey.length + cipherText.length];
            System.arraycopy(encodedSymmetricKeyLength, 0, encryptedData, 0, encodedSymmetricKeyLength.length);
            System.arraycopy(encodedSymmetricKey, 0, encryptedData, 4, encodedSymmetricKey.length);
            System.arraycopy(cipherText, 0, encryptedData, encodedSymmetricKey.length + 4, cipherText.length);
            return encryptedData;

        } catch (GeneralSecurityException e) {
            throw new Error(e);
        }
    }

    private byte[] decrypt(PrivateKey privateKey, byte[] data) {
        try {
            byte[] encodedSymmetricKeyLength = new byte[4];

            System.arraycopy(data, 0, encodedSymmetricKeyLength, 0, 4);

            int keyLength = ByteBuffer.wrap(encodedSymmetricKeyLength).getInt();

            byte[] encodedSymmetricKey = new byte[keyLength];
            System.arraycopy(data, 4, encodedSymmetricKey, 0, keyLength);

            byte[] cipherText = new byte[data.length - keyLength - 4];
            System.arraycopy(data, keyLength + 4, cipherText, 0, data.length - keyLength - 4);

            Cipher cipherRSA = createRSACipher();
            cipherRSA.init(Cipher.DECRYPT_MODE, privateKey);
            encodedSymmetricKey = cipherRSA.doFinal(encodedSymmetricKey);

            SecretKey decodedSmmetricKey = new SecretKeySpec(encodedSymmetricKey, "AES");

            Cipher symmetricCipher = createAESCipher();
            symmetricCipher.init(Cipher.DECRYPT_MODE, decodedSmmetricKey);

            return symmetricCipher.doFinal(cipherText);

        } catch (GeneralSecurityException e) {
            throw new Error(e);
        }
    }

    private static Cipher createAESCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
        return Cipher.getInstance("AES/ECB/PKCS5Padding");
    }

    Cipher createRSACipher() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
        return Cipher.getInstance("RSA/ECB/PKCS1Padding");
    }

    private static SecretKey createAESSecretKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKey secret;
        if (getEncryptedStorageConfiguration().rsaKeysize() > 2048) {
            // A java.security.InvalidKeyException with the message "Illegal key size or default parameters" means that the cryptography strength is limited;
            // the unlimited strength jurisdiction policy files are not in the correct location. In a JDK, they should be placed under ${jdk}/jre/lib/security
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[8];
            random.nextBytes(salt);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        } else {
            byte[] keyBytes;
            try {
                keyBytes = password.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new Error(e);
            }
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            keyBytes = sha.digest(keyBytes);
            keyBytes = Arrays.copyOf(keyBytes, 16); // use only first 128 bit
            secret = new SecretKeySpec(keyBytes, "AES");
        }
        return secret;
    }

    private PrivateKey createPrivateKey(byte[] encryptedPrivateKeyBytes, char[] password) {
        byte[] privateKeyBinary;
        try {
            privateKeyBinary = getBinaryEncryptor(password).decrypt(encryptedPrivateKeyBytes);
        } catch (EncryptionOperationNotPossibleException e) {
            throw new UserRuntimeException("EncryptionOperationNotPossible or Wrong password", e);
        }
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
            keyDto.name().set(publicKey.name());
            keyDto.created().set(publicKey.created());
            keyDto.expired().set(publicKey.expired());

            if ((current != null) && publicKey.getPrimaryKey().equals(current.current().getPrimaryKey())) {
                keyDto.isCurrent().setValue(Boolean.TRUE);
            }
            keyDto.decryptionEnabled().setValue(activeKeys.get(publicKey.getPrimaryKey()) != null);

            keyDto.recordsCount().setValue(countRecords(publicKey.getPrimaryKey()));

            infoDto.keys().add(keyDto);
        }

        return infoDto;
    }

    private int countRecords(Key publicKeyKey) {
        int count = 0;
        for (EncryptedStorageConsumer consumer : consumers) {
            count += consumer.countRecords(publicKeyKey);
        }
        return count;
    }

    @Override
    public byte[] createNewKeyPair(char[] password) {
        ByteArrayOutputStream encryptedPrivateKeyBuffer = new ByteArrayOutputStream();
        generateKey(password, encryptedPrivateKeyBuffer);
        return encryptedPrivateKeyBuffer.toByteArray();
    }

    @Override
    public void preloaderTestKey() {
        String password = getEncryptedStorageConfiguration().automaticActivateDecryptionKeyPassword();
        if (password != null) {
            Key publicKeyKey = generateKey(password.toCharArray(), null);
            makeCurrent(publicKeyKey);
        }
    }

    private void automaticActivateDecryption() {
        if (!ApplicationMode.isDevelopment()) {
            return;
        }
        if (activeKeys.size() == 0) {
            final String password = getEncryptedStorageConfiguration().automaticActivateDecryptionKeyPassword();
            if (password != null) {
                TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                    @Override
                    public Void call() {
                        activateDecryption(getCurrentPublicKey(), password.toCharArray());
                        return null;
                    }
                });
            }
        }
    }

    @Override
    public void makeCurrent(final Key publicKeyKey) {
        EncryptedStoragePublicKey publicKey = Persistence.service().retrieve(EncryptedStoragePublicKey.class, publicKeyKey);
        if (publicKey == null) {
            throw new UserRuntimeException("PublicKey not found");
        }

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                EncryptedStorageCurrentKey current = Persistence.service().retrieve(EntityQueryCriteria.create(EncryptedStorageCurrentKey.class));
                if (current == null) {
                    current = EntityFactory.create(EncryptedStorageCurrentKey.class);
                }
                current.current().setPrimaryKey(publicKeyKey);
                Persistence.service().persist(current);
                return null;
            }

        });

        log.info("Key {} id#{} made current", publicKey.name().getValue(), publicKey.getPrimaryKey());

        ServerSideFactory.create(AuditFacade.class).record(AuditRecordEventType.System, publicKey, "Key {0} id#{1} made current", publicKey.name(),
                publicKey.getPrimaryKey());
    }

    @Override
    public String startKeyRotation(Key publicKeyKey) {
        if (publicKeyKey.equals(getCurrentPublicKey())) {
            throw new UserRuntimeException("Can't deactivate current key");
        }
        if (activeKeys.get(publicKeyKey) == null) {
            throw new UserRuntimeException("Can't deactivate key with not activated decryption");
        }
        Key fromPublicKeyKey = publicKeyKey;
        Key toPublicKeyKey = getCurrentPublicKey();
        if (activeKeys.get(toPublicKeyKey) == null) {
            throw new UserRuntimeException("Can't deactivate key while current key has no activated decryption");
        }
        EncryptedStoragePublicKey fromPublicKey = Persistence.service().retrieve(EncryptedStoragePublicKey.class, fromPublicKeyKey);
        if (fromPublicKey == null) {
            throw new UserRuntimeException("PublicKey 'From' not found");
        }
        EncryptedStoragePublicKey toPublicKey = Persistence.service().retrieve(EncryptedStoragePublicKey.class, toPublicKeyKey);
        if (toPublicKey == null) {
            throw new UserRuntimeException("PublicKey 'To' not found");
        }
        log.warn("Starting Key Rotation from {} id#{} to {} id#{}", fromPublicKey.name().getValue(), fromPublicKey.getPrimaryKey(), toPublicKey.name()
                .getValue(), toPublicKey.getPrimaryKey());

        ServerSideFactory.create(AuditFacade.class).record(AuditRecordEventType.System, fromPublicKey, "Starting Key Rotation from {0} id#{1} to {2} id#{3}",
                fromPublicKey.name(), fromPublicKey.getPrimaryKey(), toPublicKey.name(), toPublicKey.getPrimaryKey());

        int total = countRecords(fromPublicKeyKey);
        return DeferredProcessRegistry.fork(new KeyRotationDeferredProcess(total, fromPublicKeyKey, toPublicKeyKey), ThreadPoolNames.IMPORTS);
    }

    @Override
    public void keyRotationProcess(AtomicInteger progress, Key fromPublicKeyKey, Key toPublicKeyKey) {
        for (EncryptedStorageConsumer consumer : consumers) {
            consumer.processKeyRotation(progress, fromPublicKeyKey, toPublicKeyKey);
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

        log.warn("PrivateKey {} id#{} removed", publicKey.name().getValue(), publicKey.getPrimaryKey());
        ServerSideFactory.create(AuditFacade.class).record(AuditRecordEventType.System, publicKey, "PrivateKey {0} id#{1} removed", publicKey.name(),
                publicKey.getPrimaryKey());
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

        log.info("PublicKey {} id#{} Decryption activated", publicKey.name().getValue(), publicKey.getPrimaryKey());
        ServerSideFactory.create(AuditFacade.class).record(AuditRecordEventType.System, publicKey, "PublicKey {0} id#{1} Decryption activated",
                publicKey.name(), publicKey.getPrimaryKey());
    }

    private PrivateKey loadPrivateKey(EncryptedStoragePublicKey publicKey, char[] password) {
        byte[] encryptedPrivateKeyBytes = getPrivateKeyStorage().loadPrivateKey(publicKey.name().getValue());
        if (encryptedPrivateKeyBytes == null) {
            throw new UserRuntimeException("PrivateKey not found");
        }
        return createPrivateKey(encryptedPrivateKeyBytes, password);
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
        int len = 2 * 1024 + random.nextInt(5 * 1024);
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

    private Key generateKey(char[] password, ByteArrayOutputStream encryptedPrivateKeyBuffer) {
        final EncryptedStoragePublicKey publicKey = EntityFactory.create(EncryptedStoragePublicKey.class);
        publicKey.name().setValue(new SimpleDateFormat("yyyy-MM-dd_HHmm").format(new Date()));
        publicKey.algorithmsVersion().setValue(1);
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(getEncryptedStorageConfiguration().rsaKeysize());

            KeyPair keyPair = generator.genKeyPair();
            publicKey.keyData().setValue(keyPair.getPublic().getEncoded());
            publicKey.keyTestData().setValue(generateTestData(publicKey));
            publicKey.encryptTestData().setValue(encrypt(publicKey, publicKey.keyTestData().getValue()));
            {
                PrivateKey privateKey = keyPair.getPrivate();
                byte[] encryptedPrivateKeyBytes = getBinaryEncryptor(password).encrypt(privateKey.getEncoded());
                testKeyDecryption(publicKey, encryptedPrivateKeyBytes, password);
                getPrivateKeyStorage().savePrivateKey(publicKey.name().getValue(), encryptedPrivateKeyBytes);

                if (encryptedPrivateKeyBuffer != null) {
                    encryptedPrivateKeyBuffer.write(encryptedPrivateKeyBytes, 0, encryptedPrivateKeyBytes.length);
                }
            }
        } catch (GeneralSecurityException e) {
            log.error("Error", e);
            throw new Error(e.getMessage());
        }

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                Persistence.service().persist(publicKey);
                return null;
            }
        });

        log.info("New KeyPair {} id#{} created", publicKey.name().getValue(), publicKey.getPrimaryKey());
        ServerSideFactory.create(AuditFacade.class).record(AuditRecordEventType.System, publicKey, "New KeyPair {0} id#{1} created", publicKey.name(),
                publicKey.getPrimaryKey());

        activateDecryption(publicKey.getPrimaryKey(), password);

        return publicKey.getPrimaryKey();
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

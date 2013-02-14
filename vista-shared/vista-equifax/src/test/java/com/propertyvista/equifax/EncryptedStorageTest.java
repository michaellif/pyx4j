/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-31
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.equifax;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.jasypt.util.binary.BasicBinaryEncryptor;
import org.jasypt.util.binary.BinaryEncryptor;
import org.jasypt.util.binary.StrongBinaryEncryptor;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.pyx4j.gwt.server.IOUtils;

public class EncryptedStorageTest {

    private static boolean strong = false;

    @Test
    public void testSymmetricEncryption() throws Exception {
        byte[] input = "Test 123 Test 123".getBytes();
        System.out.println("input text : " + new String(input));

        KeyPair keyPair = createRSAKeyPair();

        byte[] plainText;

        if (true) {
            byte[] cipher = encrypt(keyPair.getPublic(), input);
            plainText = decrypt(keyPair.getPrivate(), cipher);
        } else {
            byte[] cipher = encrypt(null, input);
            plainText = decrypt(null, cipher);
        }

        System.out.println("plain text : " + new String(plainText));
    }

    @Test
    public void testRSAEncryption() throws Exception {
        byte[] input = "Key 123 Key 123".getBytes();
        System.out.println("input key : " + new String(input));

        KeyPair keyPair = createRSAKeyPair();

        Key privateKey = keyPair.getPrivate();
        Key publicKey = keyPair.getPublic();

        Cipher cipherRSA = Cipher.getInstance("RSA");
        cipherRSA.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipher = cipherRSA.doFinal(input);

        cipherRSA.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] plainText = cipherRSA.doFinal(cipher);
        System.out.println("plain key : " + new String(plainText));
    }

    byte[] encrypt(PublicKey publicKey, byte[] message) throws Exception {

        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 28) {
            sb.append(Integer.toHexString(random.nextInt()));
        }

        SecretKey symmetricKey = createAESSecretKey(sb.toString(), false);
        Cipher symmetricCipher = createAESCipher();
        symmetricCipher.init(Cipher.ENCRYPT_MODE, symmetricKey);

        byte[] encodedSymmetricKey = symmetricKey.getEncoded();

        if (publicKey != null) {
            Cipher cipherRSA = createRSACipher();
            cipherRSA.init(Cipher.ENCRYPT_MODE, publicKey);
            encodedSymmetricKey = cipherRSA.doFinal(encodedSymmetricKey);
        }

        byte[] encodedSymmetricKeyLength = ByteBuffer.allocate(4).putInt(encodedSymmetricKey.length).array();

        byte[] cipherText = symmetricCipher.doFinal(message);

        byte[] cipher = new byte[4 + encodedSymmetricKey.length + cipherText.length];
        System.arraycopy(encodedSymmetricKeyLength, 0, cipher, 0, encodedSymmetricKeyLength.length);
        System.arraycopy(encodedSymmetricKey, 0, cipher, 4, encodedSymmetricKey.length);
        System.arraycopy(cipherText, 0, cipher, encodedSymmetricKey.length + 4, cipherText.length);

        return cipher;
    }

    byte[] decrypt(Key privateKey, byte[] cipher) throws Exception {

        byte[] encodedSymmetricKeyLength = new byte[4];

        System.arraycopy(cipher, 0, encodedSymmetricKeyLength, 0, 4);

        int keyLength = ByteBuffer.wrap(encodedSymmetricKeyLength).getInt();

        byte[] encodedSymmetricKey = new byte[keyLength];
        System.arraycopy(cipher, 4, encodedSymmetricKey, 0, keyLength);

        byte[] cipherText = new byte[cipher.length - keyLength - 4];
        System.arraycopy(cipher, keyLength + 4, cipherText, 0, cipher.length - keyLength - 4);

        if (privateKey != null) {
            Cipher cipherRSA = createRSACipher();
            cipherRSA.init(Cipher.DECRYPT_MODE, privateKey);
            encodedSymmetricKey = cipherRSA.doFinal(encodedSymmetricKey);
        }

        SecretKey decodedSmmetricKey = new SecretKeySpec(encodedSymmetricKey, "AES");

        Cipher symmetricCipher = createAESCipher();
        symmetricCipher.init(Cipher.DECRYPT_MODE, decodedSmmetricKey);
        byte[] plainText = symmetricCipher.doFinal(cipherText);

        return plainText;
    }

    SecretKey createAESSecretKey(String password, boolean use256) throws NoSuchAlgorithmException, InvalidKeySpecException, DecoderException,
            UnsupportedEncodingException {
        SecretKey secret;
        if (use256) {
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

            byte[] keyBytes = password.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            keyBytes = sha.digest(keyBytes);
            keyBytes = Arrays.copyOf(keyBytes, 16); // use only first 128 bit
            secret = new SecretKeySpec(keyBytes, "AES");

        }
        return secret;
    }

    //Use ECB, it doesn't require IV
    Cipher createAESCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
        return Cipher.getInstance("AES/ECB/PKCS5Padding");
    }

    Cipher createRSACipher() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
        return Cipher.getInstance("RSA/ECB/PKCS1Padding");
    }

    KeyPair createRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        return kpg.genKeyPair();
    }

    @Ignore
    @Test
    public void testKeyPair() throws Exception {
        String keyStoreFileName = "./target/test.ks";

        char[] keyPassword = "Test1".toCharArray();

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);

        KeyPair keyPair = kpg.genKeyPair();

        byte[] publicKeyBinary;

        {
            PublicKey publicKey = keyPair.getPublic();
            publicKeyBinary = publicKey.getEncoded();
            //System.out.println("publicKey format:" + publicKey.getFormat());
        }

        {
            Key privateKey = keyPair.getPrivate();
            //System.out.println("privateKey format:" + privateKey.getFormat());

            byte[] encryptedBytes = getBinaryEncryptor(keyPassword).encrypt(privateKey.getEncoded());

            // Write the keyStore to disk.
            FileOutputStream fos = new FileOutputStream(keyStoreFileName);
            fos.write(encryptedBytes);
            fos.close();

        }

        byte[] data;
        byte[] encryptedData;

        // Create Data
        SecureRandom random = new SecureRandom();
        int len = 180;
        data = new byte[len];
        random.nextBytes(data);

        // encrypt Data
        {
            Cipher cipherRSA = Cipher.getInstance("RSA");
            {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBinary);
                PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

                cipherRSA.init(Cipher.ENCRYPT_MODE, publicKey);
            }

            encryptedData = cipherRSA.doFinal(data);

            //TODO
            // Create AES Key  128/256
            // make encryptedDataPart1
            // encryptedDataPart1 = cipher.doFinal(AESKey);

            // Use AES  to create encryptedDataPart2

            //encryptedData = len Part1(byte[2]) + encryptedDataPart1 + encryptedDataPart2

        }
        //System.out.println(Arrays.toString(cipherData));

        {
            // Read privateKey
            FileInputStream fis = new FileInputStream(keyStoreFileName);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            IOUtils.copyStream(fis, os, 1024);
            IOUtils.closeQuietly(fis);

            byte[] privateKeyBinary = getBinaryEncryptor(keyPassword).decrypt(os.toByteArray());

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBinary);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            //decrypt
            {
                Cipher ciphercipherRSA = Cipher.getInstance("RSA");
                ciphercipherRSA.init(Cipher.DECRYPT_MODE, privateKey);

                //TODO read AES from  encryptedData decript it using  RSA
                // decrypt AES
                // USe AES to decrypt the rest of data
                byte[] decodedSrc = ciphercipherRSA.doFinal(encryptedData);

                Assert.assertArrayEquals(data, decodedSrc);
            }
        }
    }

    private BinaryEncryptor getBinaryEncryptor(char[] password) {
        if (strong) {
            StrongBinaryEncryptor binaryEncryptor = new StrongBinaryEncryptor();
            binaryEncryptor.setPasswordCharArray(password);
            return binaryEncryptor;
        } else {
            BasicBinaryEncryptor binaryEncryptor = new BasicBinaryEncryptor();
            binaryEncryptor.setPasswordCharArray(password);
            return binaryEncryptor;
        }
    }

}

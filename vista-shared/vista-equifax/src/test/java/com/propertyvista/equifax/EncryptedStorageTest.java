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
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.jasypt.util.binary.BasicBinaryEncryptor;
import org.jasypt.util.binary.BinaryEncryptor;
import org.jasypt.util.binary.StrongBinaryEncryptor;
import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.gwt.server.IOUtils;

public class EncryptedStorageTest {

    private static boolean strong = false;

    @Test
    public void testKeyPair() throws Exception {
        String keyStoreFileName = "./target/test.ks";

        char[] keyPassword = "Test1".toCharArray();

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);

        KeyPair kp = kpg.genKeyPair();

        byte[] publicKeyBinary;

        {
            PublicKey publicKey = kp.getPublic();
            publicKeyBinary = publicKey.getEncoded();
            //System.out.println("publicKey format:" + publicKey.getFormat());
        }

        {
            Key privateKey = kp.getPrivate();
            //System.out.println("privateKey format:" + privateKey.getFormat());

            byte[] encryptedBytes = getBinaryEncryptor(keyPassword).encrypt(privateKey.getEncoded());

            // Write the keyStore to disk.      
            FileOutputStream fos = new FileOutputStream(keyStoreFileName);
            fos.write(encryptedBytes);
            fos.close();

        }

        Cipher cipher = Cipher.getInstance("RSA");
        {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBinary);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        }

        byte[] src = "Test".getBytes();

        byte[] cipherData = cipher.doFinal(src);
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

            {
                Cipher cipher2 = Cipher.getInstance("RSA");
                cipher2.init(Cipher.DECRYPT_MODE, privateKey);

                byte[] decodedSrc = cipher2.doFinal(cipherData);

                Assert.assertArrayEquals(src, decodedSrc);
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

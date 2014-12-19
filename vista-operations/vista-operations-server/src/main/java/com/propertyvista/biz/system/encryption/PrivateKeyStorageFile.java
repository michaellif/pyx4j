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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.j2se.util.FileUtils;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.EncryptedStorageConfiguration;

class PrivateKeyStorageFile implements PrivateKeyStorage {

    public File getFile(String name) {
        EncryptedStorageConfiguration config = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getEncryptedStorageConfiguration();
        return new File(config.privateKeyDirectory(), name);
    }

    @Override
    public void savePrivateKey(String name, byte[] encryptedPrivateKeyBytes) {
        File file = getFile(name);
        if (!file.getParentFile().isDirectory()) {
            if (!file.getParentFile().mkdirs()) {
                throw new Error("Unable to create directory '" + file.getParentFile().getAbsolutePath() + "'");
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(encryptedPrivateKeyBytes);
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    @Override
    public byte[] loadPrivateKey(String name) {
        File file = getFile(name);
        if (!file.exists()) {
            return null;
        }
        FileInputStream fis = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            fis = new FileInputStream(file);
            IOUtils.copyStream(fis, os, 1024);
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return os.toByteArray();
    }

    @Override
    public void removePrivateKey(String name) {
        File file = getFile(name);
        if (file.exists()) {
            try {
                FileUtils.secureDelete(file);
            } catch (IOException e) {
                throw new Error(e);
            }
        }
    }

}

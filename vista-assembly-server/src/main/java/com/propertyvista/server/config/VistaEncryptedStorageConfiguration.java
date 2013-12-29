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
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.io.File;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.Credentials;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.j2se.CredentialsFileStorage;

import com.propertyvista.config.EncryptedStorageConfiguration;

public class VistaEncryptedStorageConfiguration implements EncryptedStorageConfiguration {

    private final VistaServerSideConfiguration config;

    public VistaEncryptedStorageConfiguration(VistaServerSideConfiguration serverSideConfiguration) {
        this.config = serverSideConfiguration;
    }

    @Override
    public int rsaKeysize() {
        return config.getConfigProperties().getIntegerValue("EncryptedStorage.rsaKeysize", 2048);
    }

    @Override
    public File privateKeyDirectory() {
        String dirName = config.getConfigProperties().getValue("EncryptedStorage.keystore.dir");
        if (CommonsStringUtils.isStringSet(dirName)) {
            return new File(dirName);
        } else {
            return new File(config.vistaWorkDir(), "keystore");
        }
    }

    @Override
    public String automaticActivateDecryptionKeyPassword() {
        if (ApplicationMode.isDevelopment() || com.propertyvista.shared.config.VistaDemo.isDemo()) {
            if (config.isVistaQa()) {
                return null;
            } else {
                return "test";
            }
        } else {
            return null;
        }
    }

    @Override
    public PrivateKeyStorageType privateKeyStorageType() {
        return config.getConfigProperties().getEnumValue("EncryptedStorage.", PrivateKeyStorageType.class, PrivateKeyStorageType.file);
    }

    @Override
    public String sftpHost() {
        return null;
    }

    @Override
    public Credentials sftpCredentials() {
        return CredentialsFileStorage.getCredentials(new File(config.getConfigDirectory(), "keystore-sftp-credentials.properties"));
    }

}

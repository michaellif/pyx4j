/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 28, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system.encryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.Random;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.Credentials;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.j2se.CredentialsFileStorage;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaInterfaceCredentials;
import com.propertyvista.domain.security.PasswordIdentity;

public class PasswordEncryptorFacadeImpl implements PasswordEncryptorFacade {

    private final static Logger log = LoggerFactory.getLogger(PasswordEncryptorFacadeImpl.class);

    private static BasicTextEncryptor textEncryptor;

    @Override
    public void activateDecryption() {
        AbstractVistaServerSideConfiguration serverSideConfiguration = (AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance();
        File credentialsFile = new File(serverSideConfiguration.getConfigDirectory(), VistaInterfaceCredentials.passwordEncryptor);
        if (!credentialsFile.canRead() || credentialsFile.length() == 0) {
            saveCredentialsFile(credentialsFile, generateMasterPassword());
        }
        Credentials credentials = CredentialsFileStorage.getCredentials(credentialsFile);
        textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(credentials.password);
    }

    private String generateMasterPassword() {
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 16) {
            sb.append(Integer.toHexString(random.nextInt()));
        }
        return sb.toString();
    }

    private static org.jasypt.util.password.PasswordEncryptor getPasswordEncryptor() {
        if (ApplicationMode.isDevelopment()) {
            return new BasicPasswordEncryptor();
        } else {
            return new StrongPasswordEncryptor();
        }
    }

    @Override
    public String encryptUserPassword(String userPassword) {
        return getPasswordEncryptor().encryptPassword(userPassword);
    }

    @Override
    public boolean checkUserPassword(String inputPassword, String encryptedPassword) {
        return getPasswordEncryptor().checkPassword(inputPassword, encryptedPassword);
    }

    private String encrypt(String data) {
        assert (textEncryptor != null) : "PasswordEncryptorFacade.activateDecryption() was not initialized";
        return textEncryptor.encrypt(data);
    }

    private String decrypt(String data) {
        assert (textEncryptor != null) : "PasswordEncryptorFacade.activateDecryption() was not initialized";
        return textEncryptor.decrypt(data);
    }

    @Override
    public String decryptPassword(PasswordIdentity passwordDescr) {
        if (!passwordDescr.number().isNull()) {
            return passwordDescr.number().getValue();
        } else {
            return decrypt(passwordDescr.encrypted().getValue());
        }
    }

    @Override
    public void encryptPassword(PasswordIdentity passwordDescr, String password) {
        passwordDescr.encrypted().setValue(encrypt(password));
    }

    private void saveCredentialsFile(File credentialsFile, String masterPassword) {
        Properties props = new Properties();
        FileOutputStream out = null;
        try {
            props.setProperty("password", masterPassword);
            props.store(out = new FileOutputStream(credentialsFile), null);
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

}

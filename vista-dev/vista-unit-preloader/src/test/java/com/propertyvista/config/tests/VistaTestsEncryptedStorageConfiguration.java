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
package com.propertyvista.config.tests;

import java.io.File;

import com.pyx4j.config.server.Credentials;

import com.propertyvista.config.EncryptedStorageConfiguration;

public class VistaTestsEncryptedStorageConfiguration implements EncryptedStorageConfiguration {

    @Override
    public int rsaKeysize() {
        return 2048;
    }

    @Override
    public File privateKeyDirectory() {
        return new File("target");
    }

    @Override
    public String automaticActivateDecryptionKeyPassword() {
        return "test1234";
    }

    @Override
    public PrivateKeyStorageType privateKeyStorageType() {
        return PrivateKeyStorageType.file;
    }

    @Override
    public String sftpHost() {
        return null;
    }

    @Override
    public Credentials sftpCredentials() {
        return null;
    }

}

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
package com.propertyvista.biz.system.encryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.EncryptedStorageConfiguration;

class PrivateKeyStorageFile implements PrivateKeyStorage {

    @Override
    public void savePrivateKey(String name, byte[] encryptedPrivateKeyBytes) {
        EncryptedStorageConfiguration config = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getEncryptedStorageConfiguration();
        if (!config.privateKeyDirectory().isDirectory()) {
            if (!config.privateKeyDirectory().mkdirs()) {
                throw new Error("Unable to create directory '" + config.privateKeyDirectory().getAbsolutePath() + "'");
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(config.privateKeyDirectory(), name));
            fos.write(encryptedPrivateKeyBytes);
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

}

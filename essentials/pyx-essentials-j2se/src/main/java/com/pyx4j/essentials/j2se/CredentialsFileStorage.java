/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2012-12-03
 * @author vlads
 */
package com.pyx4j.essentials.j2se;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.Credentials;
import com.pyx4j.gwt.server.IOUtils;

public class CredentialsFileStorage {

    private static final Logger log = LoggerFactory.getLogger(Credentials.class);

    public static final String PROPERTY_USER = "user";

    public static final String PROPERTY_PASSWORD = "password";

    public static final String PROPERTY_ENCRYPT = "encrypt";

    public static Credentials getCredentials(File fileName) {
        Properties p = new Properties();
        FileReader reader = null;
        try {
            p.load(reader = new FileReader(fileName));
        } catch (IOException e) {
            log.error("read error", e);
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        Credentials c = new Credentials();
        c.userName = p.getProperty(PROPERTY_USER);
        if (CommonsStringUtils.isEmpty(c.userName)) {
            c.userName = p.getProperty("email");
        }
        c.password = p.getProperty(PROPERTY_PASSWORD);
        String encrypt = p.getProperty(PROPERTY_ENCRYPT);
        if ("false".equalsIgnoreCase(encrypt)) {
            return c;
        }
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(HostConfig.getHardwareAddress());
        if (PropertyValueEncryptionUtils.isEncryptedValue(c.password)) {
            c.password = PropertyValueEncryptionUtils.decrypt(c.password, encryptor);
        } else {
            p.put("password", PropertyValueEncryptionUtils.encrypt(c.password, encryptor));
            p.put("encrypt", "true");
            Writer writer = null;
            try {
                p.store(writer = new FileWriter(fileName), null);
                log.warn("Password file '{}' Encrypted", fileName);
            } catch (IOException e) {
                log.error("property write error", e);
                throw new Error(e);
            } finally {
                IOUtils.closeQuietly(writer);
            }
        }
        return c;
    }

}

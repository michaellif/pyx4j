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
 * Created on 2012-12-01
 * @author vlads
 */
package com.pyx4j.server.mail;

import java.io.File;
import java.util.Map;

import com.pyx4j.config.server.Credentials;
import com.pyx4j.config.server.PropertiesConfiguration;

public abstract class SMTPGmailMailServiceConfig extends SMTPMailServiceConfig {

    public SMTPGmailMailServiceConfig(Credentials credentials) {
        this(credentials, null);
    }

    public SMTPGmailMailServiceConfig(Credentials credentials, File propertiesFile) {
        host = "smtp.gmail.com";
        port = 465;
        smtpEncryption = SMTPEncryption.SSL;

        user = credentials.userName;
        password = credentials.password;

        if (propertiesFile != null && propertiesFile.canRead()) {
            Map<String, String> configProperties = PropertiesConfiguration.loadProperties(propertiesFile);
            readProperties("mail", configProperties);
        }
    }
}

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.io.File;
import java.util.Map;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.Credentials;
import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.j2se.CredentialsFileStorage;
import com.pyx4j.server.mail.SMTPMailServiceConfig;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.config.VistaInterfaceCredentials;
import com.propertyvista.operations.domain.dev.DevelopmentUser;
import com.propertyvista.server.common.security.DevelopmentSecurity;
import com.propertyvista.shared.config.VistaDemo;

class VistaSMTPMailServiceConfig extends SMTPMailServiceConfig {

    static VistaSMTPMailServiceConfig getGmailConfig(VistaServerSideConfiguration sideConfiguration) {

        VistaSMTPMailServiceConfig config = new VistaSMTPMailServiceConfig();

        config.host = "smtp.gmail.com";
        config.port = 465;
        config.starttls = true;

        File credentialsFile = new File(sideConfiguration.getConfigDirectory(), VistaInterfaceCredentials.mailSMTPvista);

        Map<String, String> configProperties = PropertiesConfiguration.loadProperties(credentialsFile);
        config.readProperties("mail", configProperties);

        Credentials credentials = CredentialsFileStorage.getCredentials(credentialsFile);
        config.user = credentials.userName;
        config.password = credentials.password;

        if (VistaDemo.isDemo() || ApplicationMode.isDevelopment()) {
            DevelopmentUser developmentUser = DevelopmentSecurity.findDevelopmentUser();
            if (developmentUser != null) {
                if (developmentUser.forwardAll().isBooleanTrue()) {
                    config.forwardAllTo = developmentUser.email().getValue();
                }
            }
        }
        if (CommonsStringUtils.isEmpty(config.forwardAllTo) && VistaDeployment.isVistaStaging()) {
            config.forwardAllTo = "support@propertyvista.com";
        }

        return config;
    }

    static IMailServiceConfigConfiguration getCustomConfig(String prefix, VistaServerSideConfiguration serverSideConfiguration) {
        VistaSMTPMailServiceConfig config = getGmailConfig(serverSideConfiguration);
        config.readProperties(prefix, serverSideConfiguration.getConfigProperties().getProperties());
        File mailCredentialsFile = new File(serverSideConfiguration.getConfigDirectory(), prefix.replace('.', '-') + "-credentials.properties");
        if (mailCredentialsFile.canRead()) {
            Credentials credentials = CredentialsFileStorage.getCredentials(mailCredentialsFile);
            config.user = credentials.userName;
            config.password = credentials.password;
        }
        return config;
    }

}

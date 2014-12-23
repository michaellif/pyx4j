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
 */
package com.propertyvista.server.config;

import java.io.File;
import java.util.Map;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.Credentials;
import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.j2se.CredentialsFileStorage;
import com.pyx4j.server.mail.SMTPMailServiceConfig;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.config.VistaInterfaceCredentials;
import com.propertyvista.operations.domain.dev.DevelopmentUser;
import com.propertyvista.server.common.security.DevelopmentSecurity;

class VistaSMTPMailServiceConfig extends SMTPMailServiceConfig {

    private String configurationId;

    private File credentialsFile;

    static VistaSMTPMailServiceConfig getDefaultConfig(VistaServerSideConfiguration serverSideConfiguration) {
        String prefix = "mail";

        VistaSMTPMailServiceConfig config = new VistaSMTPMailServiceConfig();
        config.configurationId = "Default";

        config.host = "smtp.gmail.com";
        config.port = 465;
        config.starttls = true;

        config.readProperties(prefix, serverSideConfiguration.getConfigProperties().getProperties());

        File credentialsFile = new File(serverSideConfiguration.getConfigDirectory(), VistaInterfaceCredentials.mailSMTPvista);

        Map<String, String> configProperties = PropertiesConfiguration.loadProperties(credentialsFile);
        config.readProperties(prefix, configProperties);

        Credentials credentials = CredentialsFileStorage.getCredentials(credentialsFile);
        config.user = credentials.userName;
        config.password = credentials.password;

        return config;
    }

    static IMailServiceConfigConfiguration getCustomConfig(String prefix, VistaServerSideConfiguration serverSideConfiguration) {
        VistaSMTPMailServiceConfig config = getDefaultConfig(serverSideConfiguration);
        config.configurationId = prefix;

        config.readProperties(prefix, serverSideConfiguration.getConfigProperties().getProperties());
        File mailCredentialsFile = new File(serverSideConfiguration.getConfigDirectory(), prefix.replace('.', '-') + "-credentials.properties");
        if (mailCredentialsFile.canRead()) {
            Credentials credentials = CredentialsFileStorage.getCredentials(mailCredentialsFile);
            config.user = credentials.userName;
            config.password = credentials.password;
            config.credentialsFile = mailCredentialsFile;
        }
        return config;
    }

    @Override
    public String getForwardAllTo() {
        if (VistaDeployment.isVistaProduction()) {
            return super.getForwardAllTo();
        } else if (VistaDeployment.isVistaStaging()) {
            if (CommonsStringUtils.isEmpty(super.getForwardAllTo())) {
                return "support@propertyvista.com";
            } else {
                return super.getForwardAllTo();
            }
        } else if (ApplicationMode.isDemo() || ApplicationMode.isDevelopment()
                || ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).isVistaQa()) {
            DevelopmentUser developmentUser = DevelopmentSecurity.findDevelopmentUser();
            if (developmentUser != null) {
                if (developmentUser.forwardAll().getValue(false)) {
                    return developmentUser.email().getValue();
                }
            }
        }
        return super.getForwardAllTo();
    }

    @Override
    public String configurationId() {
        return configurationId;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(super.toString());
        if (credentialsFile != null) {
            b.append("credentialsFile                                   : ").append(this.credentialsFile.getAbsolutePath()).append("\n");
        }
        return b.toString();
    }

}

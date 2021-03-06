/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-02-02
 * @author vlads
 */
package com.pyx4j.maven.password;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;

/**
 * Write credentials file base on settings.xml.
 * 
 */
@Mojo(name = "write", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST, threadSafe = true)
public class WriteCredentialsMojo extends CredentialsFileAbstractMojo implements Contextualizable {

    /**
     * The server id in maven settings.xml to use for username and password.
     * 
     */
    @Parameter(required = true)
    protected String serverId;

    /**
     * Encrypt values in created file
     */
    @Parameter(defaultValue = "true")
    protected boolean encrypt;

    @Override
    public void execute() throws MojoExecutionException {
        Server srv = settings.getServer(serverId);
        if (srv == null) {
            throw new MojoExecutionException("ServerId " + serverId + " not found");
        }

        File file = new File(locationDir, credentialsName);

        Properties credentials = new Properties();

        if (file.exists()) {
            FileReader reader = null;
            try {
                credentials.load(reader = new FileReader(file));
            } catch (IOException e) {
                getLog().error("read error", e);
                throw new MojoExecutionException("Can't read existing file " + file.getAbsolutePath());
            } finally {
                closeQuietly(reader);
            }
        }

        String password = decryptPassword(srv.getPassword());
        String encryptProperty = credentials.getProperty("encrypt");
        if (encrypt && !"false".equalsIgnoreCase(encryptProperty)) {
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setPassword(getHardwareAddress());
            password = PropertyValueEncryptionUtils.encrypt(password, encryptor);
        }

        credentials.put(usernameName, srv.getUsername());
        credentials.put(passwordName, password);

        Writer writer = null;
        try {
            credentials.store(writer = new FileWriter(file), null);
            getLog().info("Password file " + file.getAbsolutePath() + " created");
        } catch (IOException e) {
            getLog().error("write error", e);
            throw new MojoExecutionException("Can't write to file " + file.getAbsolutePath());
        } finally {
            closeQuietly(writer);
        }

    }

    @Override
    public void contextualize(Context context) throws ContextException {
        this.container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
    }

    public static String getHardwareAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            StringBuilder macAddress = new StringBuilder();
            while (en.hasMoreElements()) {
                NetworkInterface itf = en.nextElement();
                if (itf.isLoopback() || itf.isVirtual() || !itf.isUp() || itf.getName() == null) {
                    continue;
                }
                if (!itf.getName().startsWith("eth")) {
                    continue;
                }
                byte[] mac = itf.getHardwareAddress();
                for (byte b : mac) {
                    macAddress.append(String.valueOf(b));
                }
            }
            if (macAddress.length() == 0) {
                throw new Error("NetworkInterface not found");
            }
            return macAddress.toString();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Throwable e) {
        }
    }

}

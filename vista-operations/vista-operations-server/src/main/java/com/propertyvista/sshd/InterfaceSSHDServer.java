/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.sshd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.Credentials;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.j2se.CredentialsFileStorage;
import com.pyx4j.essentials.j2se.util.FileUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaInterfaceCredentials;
import com.propertyvista.payment.dbp.simulator.DirectDebitSimManager;
import com.propertyvista.payment.pad.simulator.PadSimSftpHelper;
import com.propertyvista.sshd.fs.UsersFileSystemFactory;

public class InterfaceSSHDServer {

    private static final Logger log = LoggerFactory.getLogger(InterfaceSSHDServer.class);

    private static SshServer sshd;

    public static synchronized void init() {
        AbstractVistaServerSideConfiguration config = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance());
        int port = config.interfaceSSHDPort();
        if (port == 0) {
            return;
        }

        try {
            SshServer sshd = SshServer.setUpDefaultServer();
            sshd.setPort(port);
            sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File(config.getConfigDirectory(), "sshd-key.ser").getAbsolutePath()));

            Map<String, File> usersRootDirectories = new HashMap<String, File>();
            Collection<Credentials> users = new ArrayList<Credentials>();

            // Tenant Sure
            {
                Credentials credentials = CredentialsFileStorage.getCredentials(new File(config.getConfigDirectory(),
                        VistaInterfaceCredentials.tenantSureSftpInterface));
                users.add(credentials);
                usersRootDirectories.put(credentials.userName, buildTenantSureRootDir(config));
            }

            if (ApplicationMode.isDevelopment()) {
                // Caledon simulator
                {
                    File file = new File(config.getConfigDirectory(), VistaInterfaceCredentials.caledonFundsTransferSimulator);
                    if (file.canRead()) {
                        Credentials credentials = CredentialsFileStorage.getCredentials(file);
                        users.add(credentials);
                        usersRootDirectories.put(credentials.userName, PadSimSftpHelper.buildSftpRootDir());
                    }
                }
                // BMO Simulator
                {
                    File file = new File(config.getConfigDirectory(), VistaInterfaceCredentials.bmoMailBoxPoolSimulator);
                    if (file.canRead()) {
                        Credentials credentials = CredentialsFileStorage.getCredentials(file);
                        users.add(credentials);
                        usersRootDirectories.put(credentials.userName, DirectDebitSimManager.getSftpRootDir());
                    }
                }
            }

            sshd.setPasswordAuthenticator(new SSHDPasswordAuthenticator(users));

            sshd.setFileSystemFactory(new UsersFileSystemFactory(usersRootDirectories));

            sshd.setCommandFactory(new ScpCommandFactory());

            List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
            namedFactoryList.add(new SftpSubsystem.Factory());
            sshd.setSubsystemFactories(namedFactoryList);

            sshd.start();

            InterfaceSSHDServer.sshd = sshd;

            log.info("SSHD listening on port {}", port);

        } catch (Throwable e) {
            log.error("SFTP Start error", e);
            throw new Error(e);
        }
    }

    public static synchronized void shutdown() {
        if (sshd != null) {
            try {
                sshd.stop();
            } catch (InterruptedException e) {
                log.error("SSHD stop error", e);
            } finally {
                sshd = null;
            }
        }
    }

    private static File buildTenantSureRootDir(AbstractVistaServerSideConfiguration config) {
        try {
            File dir = config.getTenantSureInterfaceSftpDirectory();
            FileUtils.forceMkdir(dir);
            FileUtils.forceMkdir(new File(dir, "hq-update"));
            FileUtils.forceMkdir(new File(dir, "reports"));
            return dir;
        } catch (IOException e) {
            throw new Error(e);
        }

    }
}

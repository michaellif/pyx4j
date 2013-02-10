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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.sshd.fs.SimpleFileSystemFactory;

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

            sshd.setPasswordAuthenticator(new SSHDPasswordAuthenticator());

            sshd.setFileSystemFactory(new SimpleFileSystemFactory(getRootDir(config)));

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

    private static File getRootDir(AbstractVistaServerSideConfiguration config) {
        //TODO make directory configuration per user
        File dir = config.getTenantSureInterfaceSftpDirectory();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.error("Unable to create directory {}", dir.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", dir.getAbsolutePath()));
            }
        }

        File dirHqUpdate = new File(dir, "hq-update");
        if (!dirHqUpdate.exists()) {
            if (!dirHqUpdate.mkdirs()) {
                log.error("Unable to create directory {}", dirHqUpdate.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", dirHqUpdate.getAbsolutePath()));
            }
        }
        File dirReports = new File(dir, "reports");
        if (!dirReports.exists()) {
            if (!dirReports.mkdirs()) {
                log.error("Unable to create directory {}", dirReports.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", dirReports.getAbsolutePath()));
            }
        }

        return dir;
    }
}

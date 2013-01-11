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
package com.propertyvista.payment.sshd;

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
import com.pyx4j.log4j.LoggerConfig;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.payment.sshd.fs.SimpleFileSystemFactory;

public class PaymentSSHDServer {

    private static final Logger log = LoggerFactory.getLogger(PaymentSSHDServer.class);

    private static SshServer sshd;

    public static synchronized void init() {
        AbstractVistaServerSideConfiguration config = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance());
        int port = config.paymentSSHDPort();
        if (port == 0) {
            return;
        }

        try {
            SshServer sshd = SshServer.setUpDefaultServer();
            sshd.setPort(port);
            sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File(config.getConfigDirectory(), "sshd-key.ser").getAbsolutePath()));

            sshd.setPasswordAuthenticator(new SSHDPasswordAuthenticator());

            sshd.setFileSystemFactory(new SimpleFileSystemFactory(getRootDir()));

            sshd.setCommandFactory(new ScpCommandFactory());

            List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
            namedFactoryList.add(new SftpSubsystem.Factory());
            sshd.setSubsystemFactories(namedFactoryList);

            sshd.start();

            PaymentSSHDServer.sshd = sshd;

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

    private static File getRootDir() {
        File dir = new File(new File(new File("vista-work"), LoggerConfig.getContextName()), "sshd");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.error("Unable to create directory {}", dir.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", dir.getAbsolutePath()));
            }
        }
        return dir;
    }
}

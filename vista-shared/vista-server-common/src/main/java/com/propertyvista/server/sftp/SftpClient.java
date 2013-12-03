/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.sftp;

import java.io.Closeable;
import java.io.File;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import com.pyx4j.config.server.Credentials;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.SftpConnectionConfiguration;

public class SftpClient implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(SftpClient.class);

    private final SftpConnectionConfiguration configuration;

    private final Credentials credentials;

    private final JSch jsch = new JSch();

    private Session session = null;

    private ChannelSftp channel = null;

    public SftpClient(SftpConnectionConfiguration configuration) {
        this.configuration = configuration;
        credentials = configuration.sftpCredentials();
    }

    void connect() throws JSchException {
        File knownHosts = new File(System.getProperty("user.home") + "/.ssh", "known_hosts");
        if (knownHosts.canRead()) {
            jsch.setKnownHosts(knownHosts.getAbsolutePath());
        }
        if (ServerSideConfiguration.isStartedUnderEclipse()) {
            knownHosts = new File("src/scripts/sftp", "known_hosts");
            if (knownHosts.canRead()) {
                jsch.setKnownHosts(knownHosts.getAbsolutePath());
            }
        }

        session = jsch.getSession(credentials.userName, configuration.sftpHost(), configuration.sftpPort());
        session.setPassword(credentials.password);
        session.connect();

        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.disconnect();
            channel = null;
        }
        if (session != null) {
            try {
                // Avoid server side error: java.io.IOException: An established connection was aborted by the software in your host machine
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
            session.disconnect();
            session = null;
        }
    }

    public static void sftpPut(SftpConnectionConfiguration configuration, File file, String dst) throws SftpTransportConnectionException {
        SftpClient client = new SftpClient(configuration);
        try {
            client.connect();
        } catch (JSchException e) {
            log.error("SFTP connecton error", e);
            IOUtils.closeQuietly(client);
            throw new SftpTransportConnectionException(e.getMessage(), e);
        }

        try {
            client.channel.cd(dst);
        } catch (SftpException e) {
            IOUtils.closeQuietly(client);
            log.error("SFTP error", e);
            throw new SftpTransportConnectionException(e.getMessage(), e);
        }

        try {
            client.channel.put(file.getAbsolutePath(), file.getName());

            log.info("SFTP file {} transfer completed to {}, directory {}", file.getAbsolutePath(), configuration.sftpHost(), dst);
        } catch (SftpException e) {
            log.error("SFTP error", e);
            throw new Error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

    public static <E extends SftpFile> E receiveFile(SftpConnectionConfiguration configuration, SftpRetrieveFilter<E> filter, String... directories)
            throws SftpTransportConnectionException {
        SftpClient client = new SftpClient(configuration);
        try {
            client.connect();
        } catch (JSchException e) {
            log.error("SFTP connecton error", e);
            IOUtils.closeQuietly(client);
            throw new SftpTransportConnectionException(e.getMessage(), e);
        }

        try {
            E receivedFile = null;
            String baseDirectory = client.channel.pwd();

            scanDirectories: for (String dir : directories) {
                client.channel.cd(dir);

                @SuppressWarnings("unchecked")
                Vector<LsEntry> rFiles = client.channel.ls(".");

                for (LsEntry rFile : rFiles) {
                    E dst = filter.accept(dir, rFile.getFilename());
                    if (dst != null) {
                        client.channel.get(rFile.getFilename(), dst.localFile.getAbsolutePath());
                        log.info("SFTP file {} received from {}, directory {}", dst.localFile.getAbsolutePath(), configuration.sftpHost(), dir);
                        receivedFile = dst;
                        receivedFile.remoteName = rFile.getFilename();
                        receivedFile.remotePath = dir;
                        break scanDirectories;
                    }
                }
                client.channel.cd(baseDirectory);
            }

            return receivedFile;
        } catch (SftpException e) {
            log.error("SFTP error", e);
            throw new Error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

    public static void removeFile(SftpConnectionConfiguration configuration, String remotePath, String remoteName) throws SftpTransportConnectionException {
        SftpClient client = new SftpClient(configuration);
        try {
            client.connect();
        } catch (JSchException e) {
            log.error("SFTP error", e);
            IOUtils.closeQuietly(client);
            throw new SftpTransportConnectionException(e.getMessage(), e);
        }
        try {
            client.channel.cd(remotePath);
            log.info("SFTP removing file {} / {} on {} ", remotePath, remoteName, configuration.sftpHost());
            client.channel.rm(remoteName);
        } catch (SftpException e) {
            log.error("SFTP error", e);
            throw new Error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

}

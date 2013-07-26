/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 29, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.pad;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.Credentials;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.CaledonFundsTransferConfiguration;
import com.propertyvista.config.VistaSystemsSimulationConfig;
import com.propertyvista.operations.domain.payment.pad.FundsTransferType;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.payment.pad.data.PadAckFile;

public class CaledonPadSftpClient {

    private static final Logger log = LoggerFactory.getLogger(CaledonPadSftpClient.class);

    private final String postDst = "in";

    private final String getSrc = "out";

    public static enum PadFileType {

        PadFile,

        Acknowledgement,

        Reconciliation;
    }

    private final CaledonFundsTransferConfiguration configuration;

    public CaledonPadSftpClient() {
        configuration = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getCaledonFundsTransferConfiguration();
    }

    private static boolean usePadSimulator() {
        return VistaSystemsSimulationConfig.getConfiguration().usePadSimulator().getValue(Boolean.TRUE);
    }

    private class SftpClient implements Closeable {

        JSch jsch = new JSch();

        Session session = null;

        ChannelSftp channel = null;

        Credentials credentials = configuration.sftpCredentials();

        void connect() throws JSchException {
            File knownHosts = new File(System.getProperty("user.home") + "/.ssh", "known_hosts");
            if (knownHosts.canRead()) {
                jsch.setKnownHosts(knownHosts.getAbsolutePath());
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
                channel.exit();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public String sftpPut(FundsTransferType fundsTransferType, File file) {
        return sftpPut(file, fundsTransferType.getDirectoryName(postDst));
    }

    public String sftpPutSim(FundsTransferType fundsTransferType, File file) {
        if (!usePadSimulator()) {
            throw new UserRuntimeException("PadSimulator is disabled");
        }
        return sftpPut(file, fundsTransferType.getDirectoryName(getSrc));
    }

    private String sftpPut(File file, String dst) {
        SftpClient client = new SftpClient();
        try {
            client.connect();

            client.channel.cd(dst);
            client.channel.put(file.getAbsolutePath(), file.getName());

            log.info("SFTP file {} transfer completed to {}", file.getAbsolutePath(), configuration.sftpHost());
            return null;
        } catch (SftpException e) {
            log.error("SFTP error", e);
            return e.getMessage();
        } catch (JSchException e) {
            log.error("SFTP error", e);
            return e.getMessage();
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

    public List<File> receiveFiles(String companyId, PadFileType padFileType, File targetDirectory) throws EFTTransportConnectionException {
        return receiveFiles(getSrc, companyId, padFileType, targetDirectory);
    }

    public List<File> receiveFilesSim(File targetDirectory) throws EFTTransportConnectionException {
        if (!usePadSimulator()) {
            throw new UserRuntimeException("PadSimulator is disabled");
        }
        return receiveFiles(postDst, null, PadFileType.PadFile, targetDirectory);
    }

    private List<File> receiveFiles(String src, String companyId, PadFileType padFileType, File targetDirectory) throws EFTTransportConnectionException {
        SftpClient client = new SftpClient();
        try {
            client.connect();
        } catch (JSchException e) {
            log.error("SFTP error", e);
            IOUtils.closeQuietly(client);
            throw new EFTTransportConnectionException(e.getMessage(), e);
        }

        try {
            client.channel.cd(src);

            List<File> lFiles = new ArrayList<File>();

            @SuppressWarnings("unchecked")
            Vector<LsEntry> rFiles = client.channel.ls(".");
            for (LsEntry rFile : rFiles) {

                boolean fileMatch = false;
                if (companyId == null) {
                    fileMatch = true;
                } else {
                    switch (padFileType) {
                    case PadFile:
                        // Used for simulator only
                        fileMatch = rFile.getFilename().endsWith("." + companyId);
                        break;
                    case Acknowledgement:
                        fileMatch = rFile.getFilename().endsWith("." + companyId + PadAckFile.FileNameSufix);
                        break;
                    case Reconciliation:
                        fileMatch = rFile.getFilename().endsWith(PadReconciliationFile.FileNameSufix + companyId);
                        break;
                    }
                }

                if (fileMatch) {
                    File dst = new File(targetDirectory, rFile.getFilename());
                    File dst2 = new File(new File(targetDirectory, "processed"), rFile.getFilename());
                    if ((!dst.exists()) && (!dst2.exists())) {
                        client.channel.get(rFile.getFilename(), dst.getAbsolutePath());
                        lFiles.add(dst);
                        log.info("SFTP file {} received from {}", dst.getAbsolutePath(), configuration.sftpHost());
                        // Only one file  
                        break;
                    } else {
                        log.debug("SFTP file {} already received", dst.getAbsolutePath());
                    }
                } else {
                    log.debug("SFTP file {} does not match companyId {} pattern", rFile.getFilename(), companyId);
                }
            }
            return lFiles;
        } catch (SftpException e) {
            log.error("SFTP error", e);
            throw new Error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

    public void removeFile(String fileName) {
        removeFile(getSrc, fileName);
    }

    public void removeFilesSim(String fileName) {
        if (!CaledonPadSftpClient.usePadSimulator()) {
            throw new UserRuntimeException("PadSimulator is disabled");
        }
        removeFile(postDst, fileName);
    }

    private void removeFile(String src, String fileName) {
        SftpClient client = new SftpClient();
        try {
            client.connect();
            client.channel.cd(src);
            String filePath = src + "/" + fileName;
            log.debug("removing file {}", filePath);
            client.channel.rm(fileName);
        } catch (SftpException e) {
            log.error("SFTP error", e);
            throw new Error(e.getMessage());
        } catch (JSchException e) {
            log.error("SFTP error", e);
            throw new Error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(client);
        }
    }
}

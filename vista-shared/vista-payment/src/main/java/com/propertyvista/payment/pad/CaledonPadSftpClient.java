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

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
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
import com.pyx4j.essentials.j2se.CredentialsFileStorage;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.payment.pad.data.PadAkFile;
import com.propertyvista.shared.VistaSystemIdentification;

public class CaledonPadSftpClient {

    private static final Logger log = LoggerFactory.getLogger(CaledonPadSftpClient.class);

    private static final String hostProd = "apato.caledoncard.com";

    private static final String hostTests = "dev.birchwoodsoftwaregroup.com";

    private final String postDst = "in";

    private final String getSrc = "out";

    private static boolean usePadSimulator = defaultUsePadSimulator();

    public static enum PadFileType {

        PadFile,

        Acknowledgement,

        Reconciliation;
    }

    private static boolean defaultUsePadSimulator() {
        return !EnumSet.of(VistaSystemIdentification.production, VistaSystemIdentification.staging).contains(VistaDeployment.getSystemIdentification());
    }

    public static boolean usePadSimulator() {
        return usePadSimulator;
    }

    public static void setUsePadSimulator(boolean usePadSymulator) {
        CaledonPadSftpClient.usePadSimulator = usePadSymulator;
    }

    private static Credentials getCredentials() {
        AbstractVistaServerSideConfiguration config = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance());
        return CredentialsFileStorage.getCredentials(new File(config.getConfigDirectory(), (usePadSimulator() ? "caledon-simulator-credentials.properties"
                : "caledon-credentials.properties")));
    }

    private static String sftpHost() {
        if (usePadSimulator()) {
            return hostTests;
        } else {
            return hostProd;
        }
    }

    private static int sftpPort() {
        if (usePadSimulator()) {
            return 3322;
        } else {
            return 22;
        }
    }

    private static class SftpClient {

        JSch jsch = new JSch();

        Session session = null;

        ChannelSftp channel = null;

        Credentials credentials = getCredentials();

        void close() {
            if (channel != null) {
                channel.exit();
            }
            if (session != null) {
                session.disconnect();
            }
        }

        void connect() throws JSchException {
            File knownHosts = new File(System.getProperty("user.home") + "/.ssh", "known_hosts");
            if (knownHosts.canRead()) {
                jsch.setKnownHosts(knownHosts.getAbsolutePath());
            }

            session = jsch.getSession(credentials.userName, sftpHost(), sftpPort());
            session.setPassword(credentials.password);
            session.connect();

            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
        }

    }

    public String sftpPut(File file) {
        return sftpPut(file, postDst);
    }

    public String sftpPutSim(File file) {
        if (!CaledonPadSftpClient.usePadSimulator()) {
            throw new UserRuntimeException("PadSimulator is disabled");
        }
        return sftpPut(file, getSrc);
    }

    private String sftpPut(File file, String dst) {
        SftpClient client = new SftpClient();
        try {
            client.connect();

            client.channel.cd(dst);
            client.channel.put(file.getAbsolutePath(), file.getName());

            log.info("SFTP file {} transfer completed to {}", file.getAbsolutePath(), sftpHost());
            return null;
        } catch (SftpException e) {
            log.error("SFTP error", e);
            return e.getMessage();
        } catch (JSchException e) {
            log.error("SFTP error", e);
            return e.getMessage();
        } finally {
            client.close();
        }
    }

    public List<File> receiveFiles(String companyId, PadFileType padFileType, File targetDirectory) {
        return receiveFiles(getSrc, companyId, padFileType, targetDirectory);
    }

    public List<File> receiveFilesSim(File targetDirectory) {
        if (!CaledonPadSftpClient.usePadSimulator()) {
            throw new UserRuntimeException("PadSimulator is disabled");
        }
        return receiveFiles(postDst, null, PadFileType.PadFile, targetDirectory);
    }

    private List<File> receiveFiles(String src, String companyId, PadFileType padFileType, File targetDirectory) {
        SftpClient client = new SftpClient();
        try {
            client.connect();

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
                        fileMatch = rFile.getFilename().endsWith("." + companyId + PadAkFile.FileNameSufix);
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
                        log.info("SFTP file {} received from {}", dst.getAbsolutePath(), sftpHost());
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
        } catch (JSchException e) {
            log.error("SFTP error", e);
            throw new Error(e.getMessage());
        } finally {
            client.close();
        }
    }

    public void removeFiles(List<File> files) {
        removeFiles(getSrc, files);
    }

    public void removeFilesSim(List<File> files) {
        if (!CaledonPadSftpClient.usePadSimulator()) {
            throw new UserRuntimeException("PadSimulator is disabled");
        }
        removeFiles(postDst, files);
    }

    private void removeFiles(String src, List<File> files) {
        SftpClient client = new SftpClient();
        try {
            client.connect();
            client.channel.cd(src);
            for (File file : files) {
                String filePath = src + "/" + file.getName();
                log.debug("removing file {}", filePath);
                client.channel.rm(file.getName());
            }
        } catch (SftpException e) {
            log.error("SFTP error", e);
            throw new Error(e.getMessage());
        } catch (JSchException e) {
            log.error("SFTP error", e);
            throw new Error(e.getMessage());
        } finally {
            client.close();
        }
    }
}

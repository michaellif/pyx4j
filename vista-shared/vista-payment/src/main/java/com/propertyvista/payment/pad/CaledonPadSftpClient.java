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

import com.pyx4j.essentials.j2se.J2SEServiceConnector;
import com.pyx4j.essentials.j2se.J2SEServiceConnector.Credentials;

public class CaledonPadSftpClient {

    private static final Logger log = LoggerFactory.getLogger(CaledonPadSftpClient.class);

    private final String hostProd = "apato.caledoncard.com";

    private final String postDst = "in";

    private final String getSrc = "out";

    private static boolean usePadSimulator = defaultUsePadSimulator();

    private static boolean defaultUsePadSimulator() {
        // TODO Auto-generated method stub
        return false;
    }

    public static boolean usePadSimulator() {
        return usePadSimulator;
    }

    public static void setUsePadSymulator(boolean usePadSymulator) {
        CaledonPadSftpClient.usePadSimulator = usePadSymulator;
    }

    private static Credentials getCredentials() {
        File credentialsFile = new File(System.getProperty("user.dir", "."), "caledon-credentials.properties");
        return J2SEServiceConnector.getCredentials(credentialsFile.getAbsolutePath());
    }

    public String sftpPut(File file) {
        Credentials credentials = getCredentials();
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channel = null;
        try {

            File knownHosts = new File(System.getProperty("user.home") + "/.ssh", "known_hosts");
            if (knownHosts.canRead()) {
                jsch.setKnownHosts(knownHosts.getAbsolutePath());
            }

            session = jsch.getSession(credentials.email, hostProd, 22);
            session.setPassword(credentials.password);
            session.connect();

            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            channel.cd(postDst);
            channel.put(file.getAbsolutePath(), file.getName());

            log.info("SFTP file {} transfer completed", file.getAbsolutePath());
            return null;
        } catch (SftpException e) {
            log.error("SFTP error", e);
            return e.getMessage();
        } catch (JSchException e) {
            log.error("SFTP error", e);
            return e.getMessage();
        } finally {
            if (channel != null) {
                channel.exit();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public List<File> reciveFiles(String companyId, Boolean acknowledgement, File targetDirectory) {
        Credentials credentials = getCredentials();
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channel = null;
        try {
            File knownHosts = new File(System.getProperty("user.home") + "/.ssh", "known_hosts");
            if (knownHosts.canRead()) {
                jsch.setKnownHosts(knownHosts.getAbsolutePath());
            }
            session = jsch.getSession(credentials.email, hostProd, 22);
            session.setPassword(credentials.password);
            session.connect();

            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            channel.cd(getSrc);

            List<File> lFiles = new ArrayList<File>();

            @SuppressWarnings("unchecked")
            Vector<LsEntry> rFiles = channel.ls(".");
            for (LsEntry rFile : rFiles) {

                boolean fileMatch = false;
                if (acknowledgement) {
                    fileMatch = rFile.getFilename().endsWith("." + companyId + "_acknowledgement.csv");
                } else {
                    fileMatch = rFile.getFilename().endsWith("." + companyId);
                }

                if (fileMatch) {
                    File dst = new File(targetDirectory, rFile.getFilename());
                    if (!dst.exists()) {
                        channel.get(rFile.getFilename(), dst.getAbsolutePath());
                        lFiles.add(dst);
                        log.info("SFTP file {} received", dst.getAbsolutePath());
                    }
                }
                // Only one file for Ack. 
                if (acknowledgement) {
                    break;
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
            if (channel != null) {
                channel.exit();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}

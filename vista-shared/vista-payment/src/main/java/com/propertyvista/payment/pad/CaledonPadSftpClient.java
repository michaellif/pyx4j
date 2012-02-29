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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;
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

    private static Credentials getCredentials() {
        File credentialsFile = new File(System.getProperty("user.dir", "."), "caledon-credentials.properties");
        return J2SEServiceConnector.getCredentials(credentialsFile.getAbsolutePath());
    }

    private void sftpPut(File file) {
        Credentials credentials = getCredentials();
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channel = null;
        try {
            session = jsch.getSession(credentials.email, hostProd, 22);
            session.setPassword(credentials.password);
            session.connect();

            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            channel.cd(postDst);
            channel.put(file.getAbsolutePath(), file.getName());

            log.info("SFTP file transfer completed");
        } catch (SftpException e) {
            log.error("SFTP error", e);
        } catch (JSchException e) {
            log.error("SFTP error", e);
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

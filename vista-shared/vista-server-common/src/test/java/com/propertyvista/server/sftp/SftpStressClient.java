/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.sftp;

import java.io.File;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SftpStressClient {

    static int max = 10;

    static String host = "interfaces.dev.birchwoodsoftwaregroup.com";

    static int port = 8822;

    static String user = "caledon";

    static String password = "Vista1102";

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-c") || args[i].equals("--count")) {
                i++;
                max = Integer.valueOf(args[i]);
            } else if (args[i].equals("-h") || args[i].equals("--host")) {
                i++;
                host = args[i];
            } else if (args[i].equals("-P") || args[i].equals("--port")) {
                i++;
                port = Integer.valueOf(args[i]);
            } else if (args[i].equals("-u") || args[i].equals("--user")) {
                i++;
                user = args[i];
            } else if (args[i].equals("-p") || args[i].equals("--password")) {
                i++;
                password = args[i];
            } else if (args[i].equals("--help")) {
                System.err.println("usage: -c 10 -h host -P port -u user -p pasword");
                return;
            } else {
                System.err.println("Invalid argument " + args[i]);
                System.err.println("usage: -c 10 -h host -P port -u user -p pasword");
                return;
            }
        }

        int errorCount = 0;
        for (int i = 0; i < max; i++) {
            System.out.print("" + i + "/" + max + " e:" + errorCount + "| ");
            System.out.println(" connecting " + i + " to " + user + "@" + host + ":" + port);
            try {
                connectionTest();
            } catch (Exception e) {
                System.out.println("\t error: " + e.getMessage());
                errorCount++;
            }
        }

        if (errorCount == 0) {
            System.out.println("All OK " + max);
        } else {
            System.out.println("Errors " + errorCount + "; OK " + (max - errorCount));
        }

    }

    private static void connectionTest() throws Exception {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channel = null;
        try {
            File knownHosts = new File(System.getProperty("user.home") + "/.ssh", "known_hosts");
            if (knownHosts.canRead()) {
                jsch.setKnownHosts(knownHosts.getAbsolutePath());
            }
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.connect();

            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            String baseDirectory = channel.pwd();

            @SuppressWarnings("unchecked")
            Vector<LsEntry> rFiles = channel.ls(".");
            for (LsEntry rFile : rFiles) {
                if (rFile.getAttrs().isDir()) {
                    channel.cd(rFile.getFilename());
                    channel.cd(baseDirectory);
                }
            }

            channel.cd(baseDirectory);
        } finally {
            if (channel != null) {
                channel.disconnect();
                channel = null;
            }
            if (session != null) {
                session.disconnect();
                session = null;
            }
        }

    }

}

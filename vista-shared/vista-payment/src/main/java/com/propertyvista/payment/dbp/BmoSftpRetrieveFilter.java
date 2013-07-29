/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 29, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.dbp;

import java.io.File;

import com.propertyvista.server.sftp.SftpFile;
import com.propertyvista.server.sftp.SftpRetrieveFilter;

public class BmoSftpRetrieveFilter implements SftpRetrieveFilter<SftpFile> {

    private final File targetDirectory;

    private final String bmoMailboxNumber;

    public BmoSftpRetrieveFilter(File targetDirectory, String bmoMailboxNumber) {
        this.targetDirectory = targetDirectory;
        this.bmoMailboxNumber = bmoMailboxNumber;
    }

    //%BMOCOM-SEND%ADW30451-BMOREMI-FILE-A%SFTP%POLLABLE%39fea1007thm70nj000lvgl3
    //%BMOCOM-SEND%ADW30451-BMOREMI-FILE-A%SFTP%ACCEPTED%39fea1007thm70nj000lvgl3
    @Override
    public SftpFile accept(String directoryName, String fileName) {
        if (!fileName.contains("%BMOCOM-SEND%" + bmoMailboxNumber + "-BMOREMI-FILE")) {
            return null;
        }
        if (existsLoadedOrProcessed(fileName) || existsLoadedOrProcessed(fileName.replace("%POLLABLE%", "%ACCEPTED%"))) {
            return null;
        }

        SftpFile sftpFile = new SftpFile();
        sftpFile.localFile = new File(targetDirectory, fileName);
        return sftpFile;
    }

    boolean existsLoadedOrProcessed(String fileName) {
        File dst = new File(targetDirectory, fileName);
        if (dst.exists()) {
            return true;
        } else {
            File dst2 = new File(new File(targetDirectory, "processed"), fileName);
            if (dst2.exists()) {
                return true;
            } else {
                return false;
            }
        }
    }

}

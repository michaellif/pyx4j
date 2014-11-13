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
package com.propertyvista.eft.dbp;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.eft.AbstractSftpRetrieveFilter;
import com.propertyvista.server.sftp.SftpFile;

public class BmoSftpRetrieveFilter extends AbstractSftpRetrieveFilter<SftpFile> {

    private static final Logger log = LoggerFactory.getLogger(BmoSftpRetrieveFilter.class);

    private final String bmoMailboxNumber;

    public BmoSftpRetrieveFilter(File targetDirectory, String bmoMailboxNumber) {
        super(targetDirectory);
        this.bmoMailboxNumber = bmoMailboxNumber;
    }

    //%BMOCOM-SEND%ADW30451-BMOREMI-FILE-A%SFTP%POLLABLE%39fea1007thm70nj000lvgl3
    //%BMOCOM-SEND%ADW30451-BMOREMI-FILE-A%SFTP%ACCEPTED%39fea1007thm70nj000lvgl3
    @Override
    public SftpFile accept(String directoryName, String fileName) {
        if (!fileName.contains("%BMOCOM-SEND%" + bmoMailboxNumber + "-BMOREMI-FILE")) {
            log.debug("{} remote file {} does not match expected pattern {}", this.getClass().getSimpleName(), fileName, bmoMailboxNumber);
            return null;
        }
        if (existsLoadedOrProcessed(fileName)) {
            return null;
        }
        if (fileName.contains("%POLLABLE%") && existsLoadedOrProcessed(fileName.replace("%POLLABLE%", "%ACCEPTED%"))) {
            return null;
        }
        if (fileName.contains("%ACCEPTED%") && existsLoadedOrProcessed(fileName.replace("%ACCEPTED%", "%POLLABLE%"))) {
            return null;
        }

        SftpFile sftpFile = new SftpFile();
        sftpFile.localFile = new File(targetDirectory, fileName);
        return sftpFile;
    }

}

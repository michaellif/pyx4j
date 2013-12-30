/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.dbp.simulator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.j2se.util.FileUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimFile.DirectDebitSimFileStatus;

public class DirectDebitSimManager {

    private static final Logger log = LoggerFactory.getLogger(DirectDebitSimManager.class);

    public static File getSftpRootDir() {
        File dir = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getBankingSimulatorConfiguration()
                .getBmoSimulatorSftpDirectory();
        try {
            FileUtils.forceMkdir(dir);
        } catch (IOException e) {
            throw new Error(e);
        }
        return dir;
    }

    public void send(DirectDebitSimFile directDebitSimFileId) {
        DirectDebitSimFile directDebitSimFile = Persistence.service().retrieve(DirectDebitSimFile.class, directDebitSimFileId.getPrimaryKey());
        Validate.isTrue(directDebitSimFile.status().getValue() == DirectDebitSimFileStatus.New);
        directDebitSimFile.status().setValue(DirectDebitSimFileStatus.Sent);
        directDebitSimFile.sentDate().setValue(new Date());
        Persistence.service().persist(directDebitSimFile);
        Persistence.service().retrieveMember(directDebitSimFile.records());

        File file = new File(getSftpRootDir(), "%BMOCOM-SEND%"
                + ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getBmoInterfaceConfiguration().bmoMailboxNumber()
                + "-BMOREMI-FILE-A%SFTP%POLLABLE%" + createBmoDateHash());

        log.info("Creating BMO file {}", file.getAbsolutePath());
        try {
            RemconFileWriter.write(directDebitSimFile, file);
        } catch (IOException e) {
            throw new UserRuntimeException(e.getMessage(), e);
        }

        Persistence.service().retrieveMember(directDebitSimFile.records());
    }

    private String createBmoDateHash() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }
}

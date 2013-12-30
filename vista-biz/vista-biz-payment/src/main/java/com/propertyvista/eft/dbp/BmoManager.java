/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 30, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.dbp;

import java.io.File;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.OperationsAlertFacade;
import com.propertyvista.biz.system.SftpTransportConnectionException;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.eft.EftFileUtils;
import com.propertyvista.eft.dbp.remcon.RemconFile;
import com.propertyvista.eft.dbp.remcon.RemconParser;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitFile;
import com.propertyvista.server.sftp.SftpFile;

/**
 * BMO SFTP interface implementation
 */
public class BmoManager {

    private static final Logger log = LoggerFactory.getLogger(BmoManager.class);

    public DirectDebitFile receiveBmoFile() throws SftpTransportConnectionException {
        File workdir = getBmoBaseDir();
        SftpFile sftpFile = new BmoSftpClient().receiveFile(workdir);
        if (sftpFile == null) {
            return null;
        }
        boolean parsOk = false;
        try {
            RemconFile remconFile = RemconParser.pars(sftpFile.localFile);
            DirectDebitFile directDebitFile = RemconFileInterpreter.interpreter(sftpFile, remconFile);
            parsOk = true;
            return directDebitFile;
        } finally {
            if (!parsOk) {
                EftFileUtils.move(sftpFile.localFile, workdir, "error");
            }
        }
    }

    public void confirmReceivedBmoFile(String fileName, boolean protocolErrorFlag) {
        File workdir = getBmoBaseDir();
        if (protocolErrorFlag) {
            EftFileUtils.move(new File(workdir, fileName), workdir, "error");
        } else {
            EftFileUtils.move(new File(workdir, fileName), workdir, "processed");
            try {
                try {
                    new BmoSftpClient().removeFile(fileName);
                } catch (SftpTransportConnectionException noConnection) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    new BmoSftpClient().removeFile(fileName);
                }
            } catch (Throwable e) {
                log.warn("unable to remove remote file {}", fileName, e);
                ServerSideFactory.create(OperationsAlertFacade.class).record(null, "Unable to remove remote file {} on BMO SFTP, Remove it manually", fileName);
            }
        }
    }

    private File getBmoBaseDir() {
        File padWorkdir = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getBmoInterfaceWorkDirectory();
        if (!padWorkdir.exists()) {
            if (!padWorkdir.mkdirs()) {
                log.error("Unable to create directory {}", padWorkdir.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", padWorkdir.getAbsolutePath()));
            }
        }
        return padWorkdir;
    }
}

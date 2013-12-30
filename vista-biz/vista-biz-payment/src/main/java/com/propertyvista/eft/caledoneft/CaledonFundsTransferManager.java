/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.caledoneft;

import java.io.File;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.OperationsAlertFacade;
import com.propertyvista.biz.system.SftpTransportConnectionException;
import com.propertyvista.biz.system.eft.FileCreationException;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.eft.EftFileUtils;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;
import com.propertyvista.operations.domain.eft.caledoneft.to.FundsTransferAckFile;

/**
 * FundsTransfer Caledon interface implementation
 */
public class CaledonFundsTransferManager {

    private static final Logger log = LoggerFactory.getLogger(CaledonFundsTransferManager.class);

    public void sendFundsTransferFile(FundsTransferFile padFile) throws FileCreationException, SftpTransportConnectionException {
        File padWorkdir = getPadBaseDir();
        File file = null;
        try {
            File fileSent;
            do {
                String filename = new SimpleDateFormat("yyyyMMddHHmmss").format(padFile.sent().getValue());
                //  YYYYMMDDxxxxxx_batchType.COMPANYID
                filename += "_" + padFile.fundsTransferType().getValue().getFileNamePart();
                filename += "." + padFile.companyId().getValue();
                file = new File(padWorkdir, filename);
                padFile.fileName().setValue(file.getName());
                fileSent = new File(new File(padWorkdir, "processed"), file.getName());
                if (file.exists() || fileSent.exists()) {
                    padFile.sent().setValue(new Date());
                }
            } while (file.exists() || fileSent.exists());

            log.debug("creating pad file {}", file.getAbsolutePath());

            CaledonPadFileWriter writer = new CaledonPadFileWriter(padFile, file);
            try {
                writer.write();
            } finally {
                writer.close();
            }
        } catch (Throwable e) {
            log.error("pad write error", e);
            if (file != null) {
                EftFileUtils.move(file, padWorkdir, "error");
            }
            throw new FileCreationException(e.getMessage(), e);
        }

        try {

            log.info("sending pad file {}", file.getAbsolutePath());

            new CaledonPadSftpClient().sftpPut(padFile.fundsTransferType().getValue(), file);

            log.info("pad file sent {}", file.getAbsolutePath());
        } catch (Throwable e) {
            log.error("pad send error", e);
            if (file != null) {
                EftFileUtils.move(file, padWorkdir, "error");
            }
            if (e instanceof SftpTransportConnectionException) {
                throw (SftpTransportConnectionException) e;
            } else {
                throw new Error(e.getMessage());
            }
        }
        EftFileUtils.move(file, padWorkdir, "processed");
    }

    public FundsTransferAckFile receiveFundsTransferAcknowledgementFile(String companyId) throws SftpTransportConnectionException {
        File padWorkdir = getPadBaseDir();
        CaledonFundsTransferSftpFile sftpFile = new CaledonPadSftpClient().receiveFiles(companyId, CaledonFundsTransferFileType.Acknowledgement, padWorkdir);
        if (sftpFile == null) {
            return null;
        }
        boolean parsOk = false;
        try {
            if (sftpFile.fileType != CaledonFundsTransferFileType.Acknowledgement) {
                throw new Error("Invalid acknowledgment file name" + sftpFile.localFile.getName());
            }
            FundsTransferAckFile padAkFile = new CaledonPadAcknowledgmentParser().parsReport(sftpFile.localFile);
            padAkFile.fundsTransferType().setValue(sftpFile.fundsTransferType);
            try {
                padAkFile.fileNameDate().setValue(new SimpleDateFormat("yyyyMMddHHmmss").parse(sftpFile.remoteName.substring(0, 15)));
            } catch (ParseException e) {
                throw new Error("Invalid acknowledgment file name format" + sftpFile.localFile.getName(), e);
            }
            padAkFile.remoteFileDate().setValue(new Date(sftpFile.lastModified));
            parsOk = true;
            return padAkFile;
        } finally {
            if (!parsOk) {
                EftFileUtils.move(sftpFile.localFile, padWorkdir, "error");
            }
        }
    }

    public FundsReconciliationFile receiveFundsTransferReconciliation(String companyId) throws SftpTransportConnectionException {
        File padWorkdir = getPadBaseDir();
        CaledonFundsTransferSftpFile sftpFile = new CaledonPadSftpClient().receiveFiles(companyId, CaledonFundsTransferFileType.Reconciliation, padWorkdir);
        if (sftpFile == null) {
            return null;
        }
        boolean parsOk = false;
        try {
            if (sftpFile.fileType != CaledonFundsTransferFileType.Reconciliation) {
                throw new Error("Invalid Reconciliation file name" + sftpFile.localFile.getName());
            }
            FundsReconciliationFile reconciliationFile = new CaledonPadReconciliationParser().parsReport(sftpFile.localFile);
            reconciliationFile.fundsTransferType().setValue(sftpFile.fundsTransferType);
            try {
                reconciliationFile.fileNameDate().setValue(new SimpleDateFormat("yyyyMMddHHmmss").parse(sftpFile.remoteName.substring(0, 15)));
            } catch (ParseException e) {
                throw new Error("Invalid acknowledgment file name format" + sftpFile.localFile.getName(), e);
            }
            reconciliationFile.remoteFileDate().setValue(new Date(sftpFile.lastModified));
            parsOk = true;
            return reconciliationFile;
        } finally {
            if (!parsOk) {
                EftFileUtils.move(sftpFile.localFile, padWorkdir, "error");
            }
        }
    }

    public void confirmReceivedFile(FundsTransferType fundsTransferType, String fileName, boolean protocolErrorFlag) {
        File padWorkdir = getPadBaseDir();
        if (protocolErrorFlag) {
            EftFileUtils.move(new File(padWorkdir, fileName), padWorkdir, "error");
        } else {
            EftFileUtils.move(new File(padWorkdir, fileName), padWorkdir, "processed");

            try {
                try {
                    new CaledonPadSftpClient().removeFile(fundsTransferType, fileName);
                } catch (SftpTransportConnectionException noConnection) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    new CaledonPadSftpClient().removeFile(fundsTransferType, fileName);
                }
            } catch (Throwable e) {
                log.warn("unable to remove remote file {}", fileName, e);
                ServerSideFactory.create(OperationsAlertFacade.class).record(null, "Unable to remove remote file {} {} on caledon SFTP, Remove it manually",
                        fundsTransferType, fileName);
            }
        }
    }

    private File getPadBaseDir() {
        File padWorkdir = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getCaledonInterfaceWorkDirectory();
        if (!padWorkdir.exists()) {
            if (!padWorkdir.mkdirs()) {
                log.error("Unable to create directory {}", padWorkdir.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", padWorkdir.getAbsolutePath()));
            }
        }
        return padWorkdir;
    }

}

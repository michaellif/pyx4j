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
package com.propertyvista.payment.pad;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.operations.domain.payment.dbp.DirectDebitFile;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.payment.dbp.BmoSftpClient;
import com.propertyvista.payment.dbp.RemconFileInterpreter;
import com.propertyvista.payment.dbp.remcon.RemconFile;
import com.propertyvista.payment.dbp.remcon.RemconParser;
import com.propertyvista.payment.pad.data.PadAckFile;
import com.propertyvista.server.sftp.SftpFile;
import com.propertyvista.server.sftp.SftpTransportConnectionException;

/**
 * FundsTransfer Caledon and BMO SFTP interface implementation
 */
public class EFTTransportFacadeImpl implements EFTTransportFacade {

    private static final Logger log = LoggerFactory.getLogger(EFTTransportFacadeImpl.class);

    @Override
    public void sendPadFile(PadFile padFile) {
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

            log.info("sending pad file {}", file.getAbsolutePath());

            CaledonPadFileWriter writer = new CaledonPadFileWriter(padFile, file);
            try {
                writer.write();
            } finally {
                writer.close();
            }

            String errorMessage = new CaledonPadSftpClient().sftpPut(padFile.fundsTransferType().getValue(), file);
            if (errorMessage != null) {
                throw new Error(errorMessage);
            }
            log.info("pad file sent {}", file.getAbsolutePath());
        } catch (Throwable e) {
            log.error("pad write error", e);
            if (file != null) {
                move(file, padWorkdir, "error");
            }
            throw new Error(e.getMessage());
        }
        move(file, padWorkdir, "processed");
    }

    @Override
    public PadAckFile receivePadAcknowledgementFile(String companyId) throws SftpTransportConnectionException {
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
            PadAckFile padAkFile = new CaledonPadAcknowledgmentParser().parsReport(sftpFile.localFile);
            padAkFile.fundsTransferType().setValue(sftpFile.fundsTransferType);
            parsOk = true;
            return padAkFile;
        } finally {
            if (!parsOk) {
                move(sftpFile.localFile, padWorkdir, "error");
            }
        }
    }

    @Override
    public PadReconciliationFile receivePadReconciliation(String companyId) throws SftpTransportConnectionException {
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
            PadReconciliationFile reconciliationFile = new CaledonPadReconciliationParser().parsReport(sftpFile.localFile);
            reconciliationFile.fundsTransferType().setValue(sftpFile.fundsTransferType);
            parsOk = true;
            return reconciliationFile;
        } finally {
            if (!parsOk) {
                move(sftpFile.localFile, padWorkdir, "error");
            }
        }
    }

    @Override
    public void confirmReceivedFile(FundsTransferType fundsTransferType, String fileName, boolean protocolErrorFlag) {
        File padWorkdir = getPadBaseDir();
        if (protocolErrorFlag) {
            move(new File(padWorkdir, fileName), padWorkdir, "error");
        } else {
            move(new File(padWorkdir, fileName), padWorkdir, "processed");
            new CaledonPadSftpClient().removeFile(fundsTransferType, fileName);
        }
    }

    @Override
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
                move(sftpFile.localFile, workdir, "error");
            }
        }
    }

    @Override
    public void confirmReceivedBmoFile(String fileName, boolean protocolErrorFlag) {
        File workdir = getBmoBaseDir();
        if (protocolErrorFlag) {
            move(new File(workdir, fileName), workdir, "error");
        } else {
            move(new File(workdir, fileName), workdir, "processed");
            new BmoSftpClient().removeFile(fileName);
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

    private String uniqueNameTimeStamp() {
        return new SimpleDateFormat("-yyMMdd-HHmmss.S").format(new Date());
    }

    private File move(File file, File baseDir, String subdir) {
        File dir = new File(baseDir, subdir);
        if (!dir.isDirectory() && !dir.mkdirs()) {
            log.error("Unable to create directory {}", dir.getAbsolutePath());
            return null;
        } else {
            File dst = new File(dir, file.getName());
            int attemptCount = 0;
            while (dst.exists()) {
                attemptCount++;
                if (attemptCount > 10) {
                    log.error("File {} already exists", dst.getAbsolutePath());
                    return null;
                }
                dst = new File(dir, file.getName() + uniqueNameTimeStamp());
            }
            if (!file.renameTo(dst)) {
                log.error("Rename {} to {} failed", file.getAbsolutePath(), dst.getAbsolutePath());
                return null;
            } else {
                return dst;
            }
        }
    }

}

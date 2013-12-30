/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.pad.simulator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.essentials.j2se.util.FileUtils;
import com.pyx4j.log4j.LoggerConfig;

import com.propertyvista.operations.domain.payment.pad.MerchantReconciliationStatus;
import com.propertyvista.operations.domain.payment.pad.FundsTransferFile.FileAcknowledgmentStatus;
import com.propertyvista.operations.domain.payment.pad.FundsReconciliationFile;
import com.propertyvista.operations.domain.payment.pad.TransactionReconciliationStatus;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimBatch;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimDebitRecord;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimFile;
import com.propertyvista.payment.pad.CaledonFundsTransferSftpFile;
import com.propertyvista.payment.pad.CaledonPadSftpClient;
import com.propertyvista.payment.pad.CaledonPadUtils;
import com.propertyvista.payment.pad.data.PadAckFile;
import com.propertyvista.server.sftp.SftpTransportConnectionException;

public class PadSimulationManager {

    private static final Logger log = LoggerFactory.getLogger(PadSimulationManager.class);

    private File getPadBaseDir() {
        File padWorkdir = new File(new File("vista-sim"), LoggerConfig.getContextName());
        try {
            FileUtils.forceMkdir(padWorkdir);
        } catch (IOException e) {
            throw new Error(e);
        }
        return padWorkdir;
    }

    public PadSimFile loadPadFile() {
        File padWorkdir = getPadBaseDir();
        CaledonFundsTransferSftpFile sftpFile;
        try {
            sftpFile = new CaledonPadSftpClient().receiveFilesSim(padWorkdir);
        } catch (SftpTransportConnectionException e) {
            throw new UserRuntimeException(e.getMessage(), e);
        }
        if (sftpFile == null) {
            return null;
        }

        PadSimFile padFile = new PadSimFileParser().parsReport(sftpFile.localFile);
        padFile.fileName().setValue(sftpFile.remoteName);

        Validate.isTrue(padFile.fundsTransferType().getValue() == sftpFile.fundsTransferType, "Unexpected fundsTransferType "
                + padFile.fundsTransferType().getValue());

        padFile.batchRecordsCount().setValue(padFile.batches().size());
        Persistence.service().persist(padFile);
        for (PadSimBatch padBatch : padFile.batches()) {
            Persistence.service().persist(padBatch);
        }
        Persistence.service().commit();
        // remove the loaded file from server
        try {
            new CaledonPadSftpClient().removeFilesSim(sftpFile.fundsTransferType, sftpFile.remoteName);
        } catch (SftpTransportConnectionException e) {
            throw new Error(e);
        }

        return padFile;
    }

    private void updateAcknowledgments(PadSimFile padFile) {
        if (padFile.acknowledgmentStatusCode().isNull()) {
            boolean batchLevelReject = false;
            boolean transactionReject = false;
            for (PadSimBatch padBatch : padFile.batches()) {
                if (!padBatch.acknowledgmentStatusCode().isNull()) {
                    batchLevelReject = true;
                } else {
                    for (PadSimDebitRecord record : padBatch.records()) {
                        if (!record.acknowledgmentStatusCode().isNull()) {
                            transactionReject = true;
                        }
                    }
                }
            }
            if (batchLevelReject && transactionReject) {
                padFile.acknowledgmentStatusCode().setValue(FileAcknowledgmentStatus.BatchAndTransactionReject.getStatusCode());
            } else if (batchLevelReject) {
                padFile.acknowledgmentStatusCode().setValue(FileAcknowledgmentStatus.BatchLevelReject.getStatusCode());
            } else if (transactionReject) {
                padFile.acknowledgmentStatusCode().setValue(FileAcknowledgmentStatus.TransactionReject.getStatusCode());
            } else {
                padFile.acknowledgmentStatusCode().setValue(FileAcknowledgmentStatus.Accepted.getStatusCode());
            }
        }
    }

    public void replyAcknowledgment(PadSimFile triggerStub) {
        PadSimFile padFile = Persistence.service().retrieve(PadSimFile.class, triggerStub.getPrimaryKey());
        padFile.state().add(PadSimFile.PadSimFileStatus.Acknowledged);
        padFile.acknowledged().setValue(SystemDateManager.getDate());

        Persistence.service().retrieveMember(padFile.batches());
        updateAcknowledgments(padFile);

        //YYYYMMDDhhmmss_pad.COMPANYID_acknowledgement.csv
        String fileName = padFile.fileName().getValue().substring(0, padFile.fileName().getValue().indexOf("_"));
        fileName += "_" + padFile.fundsTransferType().getValue().getFileNamePart();
        fileName += "." + padFile.companyId().getValue();
        fileName += PadAckFile.FileNameSufix;
        File file = new File(getPadBaseDir(), fileName);
        try {
            PadSimAcknowledgementFileWriter writer = new PadSimAcknowledgementFileWriter(padFile, file);
            try {
                writer.write();
            } finally {
                writer.close();
            }
        } catch (Throwable e) {
            log.error("pad write error", e);
            throw new Error(e.getMessage());
        }
        try {
            new CaledonPadSftpClient().sftpPutSim(padFile.fundsTransferType().getValue(), file);
        } catch (SftpTransportConnectionException e) {
            throw new UserRuntimeException(e.getMessage(), e);
        }

        log.info("pad file sent {}", file.getAbsolutePath());

        Persistence.service().persist(padFile);
        Persistence.service().commit();
    }

    private class SummaryTotal {

        int recordsCount = 0;

        BigDecimal totalAmount = new BigDecimal("0");

        void add(String amountValue) {
            recordsCount++;
            totalAmount = totalAmount.add(CaledonPadUtils.parsAmount(amountValue));
        }
    }

    private void updateReconciliation(PadSimFile padFile) {
        for (PadSimBatch padBatch : padFile.batches()) {
            if (padBatch.reconciliationStatus().isNull()) {
                padBatch.reconciliationStatus().setValue(MerchantReconciliationStatus.PAID);
            }
            SummaryTotal gross = new SummaryTotal();
            SummaryTotal rejects = new SummaryTotal();
            SummaryTotal returns = new SummaryTotal();

            for (PadSimDebitRecord record : padBatch.records()) {
                if (record.acknowledgmentStatusCode().isNull()) {
                    if (record.paymentDate().isNull()) {
                        record.paymentDate().setValue(CaledonPadUtils.formatDate(SystemDateManager.getDate()));
                    }
                    if (record.reconciliationStatus().isNull()) {
                        record.reconciliationStatus().setValue(TransactionReconciliationStatus.PROCESSED);
                    }
                    switch (record.reconciliationStatus().getValue()) {
                    case PROCESSED:
                        gross.add(record.amount().getValue());
                        break;
                    case REJECTED:
                        rejects.add(record.amount().getValue());
                        break;
                    case RETURNED:
                        returns.add(record.amount().getValue());
                        break;
                    }
                }
            }

            if (padBatch.grossPaymentCount().isNull()) {
                padBatch.grossPaymentCount().setValue(String.valueOf(gross.recordsCount));
            }
            if (padBatch.grossPaymentAmount().isNull()) {
                padBatch.grossPaymentAmount().setValue(CaledonPadUtils.formatAmount(gross.totalAmount));
            }

            if (padBatch.rejectItemsCount().isNull()) {
                padBatch.rejectItemsCount().setValue(String.valueOf(rejects.recordsCount));
            }
            if (padBatch.rejectItemsAmount().isNull()) {
                padBatch.rejectItemsAmount().setValue(CaledonPadUtils.formatAmount(rejects.totalAmount));
            }

            if (padBatch.returnItemsCount().isNull()) {
                padBatch.returnItemsCount().setValue(String.valueOf(returns.recordsCount));
            }
            if (padBatch.returnItemsAmount().isNull()) {
                padBatch.returnItemsAmount().setValue(CaledonPadUtils.formatAmount(returns.totalAmount));
            }

            Persistence.service().persist(padBatch);

        }
    }

    public void replyReconciliation(PadSimFile padStub) {
        PadSimFile padFile = Persistence.service().retrieve(PadSimFile.class, padStub.getPrimaryKey());
        Persistence.service().retrieveMember(padFile.batches());

        padFile.state().add(PadSimFile.PadSimFileStatus.ReconciliationSent);
        padFile.reconciliationSent().setValue(SystemDateManager.getDate());
        updateReconciliation(padFile);

        File file = new File(getPadBaseDir(), reconciliationReportFileName(padFile));
        try {
            PadSimReconciliationFileWriter writer = new PadSimReconciliationFileWriter(padFile, file);
            try {
                writer.write();
            } finally {
                writer.close();
            }
        } catch (Throwable e) {
            log.error("pad write error", e);
            throw new Error(e.getMessage());
        }
        try {
            new CaledonPadSftpClient().sftpPutSim(padFile.fundsTransferType().getValue(), file);
        } catch (SftpTransportConnectionException e) {
            throw new UserRuntimeException(e.getMessage(), e);
        }
        log.info("pad file sent {}", file.getAbsolutePath());

        Persistence.service().persist(padFile);
        Persistence.service().commit();
    }

    public PadSimFile createReturnReconciliation(PadSimFile padStub) {
        PadSimFile padFile = Persistence.service().retrieve(PadSimFile.class, padStub.getPrimaryKey());
        Persistence.service().retrieveMember(padFile.batches());

        PadSimFile padFileNew = EntityGraph.businessDuplicate(padFile);
        padFileNew.originalFile().set(padStub);
        // change the pad name
        // YYYMMDDhhmmss_pad.COMPANYID
        String filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        filename += "_" + FilenameUtils.getExtension(padFile.fileName().getValue());
        filename += "." + padFileNew.companyId().getValue();
        padFileNew.fileName().setValue(filename);

        padFileNew.state().clear();
        padFileNew.returns().setValue(Boolean.TRUE);

        Persistence.service().persist(padFileNew);

        for (PadSimBatch padBatch : padFileNew.batches()) {
            padBatch.grossPaymentCount().setValue(null);
            padBatch.grossPaymentAmount().setValue(null);
            padBatch.rejectItemsCount().setValue(null);
            padBatch.rejectItemsAmount().setValue(null);
            padBatch.returnItemsCount().setValue(null);
            padBatch.returnItemsAmount().setValue(null);

            Persistence.service().persist(padBatch);
        }

        Persistence.service().commit();

        return padFileNew;

    }

    private void updateReturns(PadSimFile padFile) {
        for (PadSimBatch padBatch : padFile.batches()) {
            if (padBatch.reconciliationStatus().isNull()) {
                padBatch.reconciliationStatus().setValue(MerchantReconciliationStatus.PAID);
            }
            SummaryTotal gross = new SummaryTotal();
            SummaryTotal rejects = new SummaryTotal();
            SummaryTotal returns = new SummaryTotal();

            Iterator<PadSimDebitRecord> it = padBatch.records().iterator();
            while (it.hasNext()) {
                PadSimDebitRecord record = it.next();
                if (record.acknowledgmentStatusCode().isNull()) {
                    if (record.paymentDate().isNull()) {
                        record.paymentDate().setValue(CaledonPadUtils.formatDate(SystemDateManager.getDate()));
                    }
                    if (record.reconciliationStatus().isNull()) {
                        it.remove();
                        continue;
                    }
                    switch (record.reconciliationStatus().getValue()) {
                    case REJECTED:
                        rejects.add(record.amount().getValue());
                        break;
                    case RETURNED:
                        returns.add(record.amount().getValue());
                        break;
                    default:
                        it.remove();
                        continue;
                    }
                }
            }

            if (padBatch.grossPaymentCount().isNull()) {
                padBatch.grossPaymentCount().setValue(String.valueOf(gross.recordsCount));
            }
            if (padBatch.grossPaymentAmount().isNull()) {
                padBatch.grossPaymentAmount().setValue(CaledonPadUtils.formatAmount(gross.totalAmount));
            }

            if (padBatch.rejectItemsCount().isNull()) {
                padBatch.rejectItemsCount().setValue(String.valueOf(rejects.recordsCount));
            }
            if (padBatch.rejectItemsAmount().isNull()) {
                padBatch.rejectItemsAmount().setValue(CaledonPadUtils.formatAmount(rejects.totalAmount));
            }

            if (padBatch.returnItemsCount().isNull()) {
                padBatch.returnItemsCount().setValue(String.valueOf(returns.recordsCount));
            }
            if (padBatch.returnItemsAmount().isNull()) {
                padBatch.returnItemsAmount().setValue(CaledonPadUtils.formatAmount(returns.totalAmount));
            }
            Persistence.service().persist(padBatch);
        }
    }

    String reconciliationReportFileName(PadSimFile padFile) {
        // YYYYMMDDhhmmss_reconciliation_rpt_pad.COMPANYID
        String fileName = padFile.fileName().getValue().substring(0, padFile.fileName().getValue().indexOf("_"));
        fileName += FundsReconciliationFile.FileNameSufix;
        fileName += "_" + padFile.fundsTransferType().getValue().getFileNamePart();
        fileName += "." + padFile.companyId().getValue();
        return fileName;
    }

    public void replyReturns(PadSimFile padStub) {
        PadSimFile padFile = Persistence.service().retrieve(PadSimFile.class, padStub.getPrimaryKey());
        Persistence.service().retrieveMember(padFile.batches());

        padFile.state().add(PadSimFile.PadSimFileStatus.ReturnSent);
        padFile.returnSent().setValue(SystemDateManager.getDate());
        updateReturns(padFile);

        File file = new File(getPadBaseDir(), reconciliationReportFileName(padFile));
        try {
            PadSimReconciliationFileWriter writer = new PadSimReconciliationFileWriter(padFile, file);
            try {
                writer.write();
            } finally {
                writer.close();
            }
        } catch (Throwable e) {
            log.error("pad write error", e);
            throw new Error(e.getMessage());
        }
        try {
            new CaledonPadSftpClient().sftpPutSim(padFile.fundsTransferType().getValue(), file);
        } catch (SftpTransportConnectionException e) {
            throw new UserRuntimeException(e.getMessage(), e);
        }
        log.info("pad file sent {}", file.getAbsolutePath());

        Persistence.service().persist(padFile);
        Persistence.service().commit();
    }
}

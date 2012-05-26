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
package com.propertyvista.admin.server.services.sim;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.log4j.LoggerConfig;

import com.propertyvista.admin.domain.payment.pad.PadFile.FileAcknowledgmentStatus;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimBatch;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimDebitRecord;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimFile;
import com.propertyvista.payment.pad.CaledonPadSftpClient;
import com.propertyvista.payment.pad.simulator.PadSimAcknowledgementFileWriter;
import com.propertyvista.payment.pad.simulator.PadSimFileParser;

public class PadSim {

    private static final Logger log = LoggerFactory.getLogger(PadSim.class);

    private File getPadBaseDir() {
        File padWorkdir = new File(new File("vista-sim"), LoggerConfig.getContextName());
        if (!padWorkdir.exists()) {
            if (!padWorkdir.mkdirs()) {
                log.error("Unable to create directory {}", padWorkdir.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", padWorkdir.getAbsolutePath()));
            }
        }
        return padWorkdir;
    }

    public PadSimFile loadPadFile() {
        File padWorkdir = getPadBaseDir();
        List<File> files = new CaledonPadSftpClient().reciveFilesSim(padWorkdir);
        if (files.size() == 0) {
            return null;
        }

        File file = files.get(0);
        files.remove(0);

        PadSimFile padFile = new PadSimFileParser().parsReport(file);
        padFile.fileName().setValue(file.getName());
        padFile.status().setValue(PadSimFile.PadSimFileStatus.Loaded);
        padFile.batchRecordsCount().setValue(padFile.batches().size());
        Persistence.service().persist(padFile);
        for (PadSimBatch padBatch : padFile.batches()) {
            Persistence.service().persist(padBatch);
        }
        Persistence.service().commit();
        // remove the loaded file from server
        {
            List<File> filesToRemove = new ArrayList<File>();
            filesToRemove.add(file);
            new CaledonPadSftpClient().removeFilesSim(filesToRemove);
        }

        // Ignore other files received if any
        for (File otherFiles : files) {
            otherFiles.delete();
        }
        return padFile;
    }

    public void replyAcknowledgment(PadSimFile triggerStub) {
        PadSimFile padFile = Persistence.service().retrieve(PadSimFile.class, triggerStub.getPrimaryKey());
        padFile.status().setValue(PadSimFile.PadSimFileStatus.Acknowledged);
        padFile.acknowledged().setValue(Persistence.service().getTransactionSystemTime());

        Persistence.service().retrieveMember(padFile.batches());
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

        File file = new File(getPadBaseDir(), padFile.fileName().getValue() + "_acknowledgement.csv");
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
        String errorMessage = new CaledonPadSftpClient().sftpPutSim(file);
        if (errorMessage != null) {
            throw new Error(errorMessage);
        }
        log.info("pad file sent {}", file.getAbsolutePath());

        Persistence.service().persist(padFile);
        Persistence.service().commit();
    }

    public void replyReconciliation(PadSimFile triggerStub) {
        PadSimFile padFile = Persistence.service().retrieve(PadSimFile.class, triggerStub.getPrimaryKey());
        Persistence.service().retrieveMember(padFile.batches());

        padFile.status().setValue(PadSimFile.PadSimFileStatus.ReconciliationSent);
        padFile.reconciliationSent().setValue(Persistence.service().getTransactionSystemTime());

        Persistence.service().persist(padFile);
        Persistence.service().commit();
    }
}

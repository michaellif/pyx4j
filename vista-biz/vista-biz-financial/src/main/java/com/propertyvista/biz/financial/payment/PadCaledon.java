/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.io.File;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.log4j.LoggerConfig;

import com.propertyvista.admin.domain.payment.pad.PadBatch;
import com.propertyvista.admin.domain.payment.pad.PadDebitRecord;
import com.propertyvista.admin.domain.payment.pad.PadFile;
import com.propertyvista.admin.domain.payment.pad.PadFileCreationNumber;
import com.propertyvista.admin.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.payment.pad.CaledonPadFileWriter;
import com.propertyvista.payment.pad.CaledonPadSftpClient;
import com.propertyvista.payment.pad.CaledonPadSftpClient.PadFileType;
import com.propertyvista.payment.pad.data.PadAkFile;

public class PadCaledon {

    private static final Logger log = LoggerFactory.getLogger(PadCaledon.class);

    private static Object lock = new Object();

    private final String companyId = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getCaledonCompanyId();

    private File getPadBaseDir() {
        File padWorkdir = new File(new File("vista-work"), LoggerConfig.getContextName());
        if (!padWorkdir.exists()) {
            if (!padWorkdir.mkdirs()) {
                log.error("Unable to create directory {}", padWorkdir.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", padWorkdir.getAbsolutePath()));
            }
        }
        return padWorkdir;
    }

    public PadFile sendPadFile() {
        File padWorkdir = getPadBaseDir();

        PadFile padFile;
        synchronized (lock) {
            EntityQueryCriteria<PadFile> criteria = EntityQueryCriteria.create(PadFile.class);
            criteria.add(PropertyCriterion.in(criteria.proto().status(), PadFile.PadFileStatus.Creating, PadFile.PadFileStatus.SendindError));
            padFile = Persistence.service().retrieve(criteria);
            if (padFile == null) {
                return null;
            }

            if (padFile.fileCreationNumber().isNull()) {
                padFile.fileCreationNumber().setValue(getNextFileCreationNumber());
            }

            padFile.status().setValue(PadFile.PadFileStatus.Sending);
            padFile.sent().setValue(new Date());
            Persistence.service().merge(padFile);
            Persistence.service().commit();
        }

        File file = null;
        try {
            File fileSent;
            do {
                String filename = new SimpleDateFormat("yyyyMMddHHmmss").format(padFile.sent().getValue());
                file = new File(padWorkdir, filename + "." + companyId);
                fileSent = new File(new File(padWorkdir, "processed"), file.getName());
                if (file.exists() || fileSent.exists()) {
                    padFile.sent().setValue(new Date());
                    padFile.fileName().setValue(file.getName());
                    Persistence.service().merge(padFile);
                    Persistence.service().commit();
                }
            } while (file.exists() || fileSent.exists());

            // Calculate Totals
            int fileRecordsCount = 0;
            BigDecimal fileAmount = new BigDecimal("0");
            int batchNumberCount = 0;
            Persistence.service().retrieveMember(padFile.batches());
            for (PadBatch padBatch : padFile.batches()) {
                Persistence.service().retrieveMember(padBatch.records());

                padBatch.batchNumber().setValue(++batchNumberCount);
                BigDecimal batchAmount = new BigDecimal("0");
                for (PadDebitRecord record : padBatch.records()) {
                    batchAmount = batchAmount.add(record.amount().getValue());
                }
                padBatch.batchAmount().setValue(batchAmount);

                fileRecordsCount += padBatch.records().size();
                fileAmount = fileAmount.add(batchAmount);
                // Save Calculated totals
                Persistence.service().persist(padBatch);
            }
            padFile.recordsCount().setValue(fileRecordsCount);
            padFile.fileAmount().setValue(fileAmount);
            Persistence.service().persist(padFile);

            log.info("sending pad file {}", file.getAbsolutePath());

            CaledonPadFileWriter writer = new CaledonPadFileWriter(padFile, file);
            try {
                writer.write(companyId);
            } finally {
                writer.close();
            }

            String errorMessage = new CaledonPadSftpClient().sftpPut(file);
            if (errorMessage != null) {
                throw new Error(errorMessage);
            }
            log.info("pad file sent {}", file.getAbsolutePath());
        } catch (Throwable e) {
            log.error("pad write error", e);
            //Error recovery
            padFile.status().setValue(PadFile.PadFileStatus.SendindError);
            Persistence.service().merge(padFile);
            Persistence.service().commit();

            if (file != null) {
                move(file, padWorkdir, "error");
            }

            throw new Error(e.getMessage());
        }

        move(file, padWorkdir, "processed");

        padFile.status().setValue(PadFile.PadFileStatus.Sent);
        Persistence.service().merge(padFile);
        Persistence.service().commit();

        padFile.batches().setAttachLevel(AttachLevel.Detached);
        return padFile;
    }

    /**
     * Length 4 Must be incremented by one for each file submitted per Company ID
     */
    private String getNextFileCreationNumber() {
        EntityQueryCriteria<PadFileCreationNumber> criteria = EntityQueryCriteria.create(PadFileCreationNumber.class);
        PadFileCreationNumber sequence = Persistence.service().retrieve(criteria);
        if (sequence == null) {
            sequence = EntityFactory.create(PadFileCreationNumber.class);
            sequence.number().setValue(0);
        }

        // Find and verify that previous file has acknowledgment
        {
            EntityQueryCriteria<PadFile> previousFileCriteria = EntityQueryCriteria.create(PadFile.class);
            previousFileCriteria.add(PropertyCriterion.eq(previousFileCriteria.proto().fileCreationNumber(), String.valueOf(sequence.number().getValue())));
            PadFile padFile = Persistence.service().retrieve(previousFileCriteria);
            if (padFile != null) {
                if (!EnumSet.of(PadFile.PadFileStatus.Acknowledged, PadFile.PadFileStatus.Procesed, PadFile.PadFileStatus.Canceled).contains(
                        padFile.status().getValue())) {
                    throw new Error("Can't send PAD file until previous file is processed");
                }

                //If a file has rejected the corrected file must be submitted using the same file creation number.
                if (PadFile.PadFileStatus.Canceled == padFile.status().getValue()) {
                    return String.valueOf(sequence.number().getValue());
                }
            }
        }

        int id = sequence.number().getValue() + 1;
        if (id == 999999) {
            id = 1;
        }
        sequence.number().setValue(id);
        Persistence.service().persist(sequence);
        return String.valueOf(id);
    }

    public PadFile recivePadAcknowledgementFile() {
        File padWorkdir = getPadBaseDir();
        List<File> files = new CaledonPadSftpClient().reciveFiles(companyId, PadFileType.Acknowledgement, padWorkdir);
        if (files.size() == 0) {
            return null;
        }
        File file = files.get(0);
        files.remove(0);
        if (!file.getName().endsWith(PadAkFile.FileNameSufix)) {
            throw new Error("Invalid acknowledgement file name" + file.getName());
        }
        PadFile padFile = new PadCaledonAcknowledgement().processFile(file);
        move(file, padWorkdir, "processed");

        // Cleanup SFTP directory
        {
            List<File> filesToRemove = new ArrayList<File>();
            filesToRemove.add(file);
            new CaledonPadSftpClient().removeFiles(filesToRemove);
        }

        // Ignore other files received if any
        for (File otherFiles : files) {
            otherFiles.delete();
        }
        return padFile;
    }

    public PadReconciliationFile recivePadReconciliation() {
        File padWorkdir = getPadBaseDir();
        List<File> files = new CaledonPadSftpClient().reciveFiles(companyId, PadFileType.Reconciliation, padWorkdir);
        if (files.size() == 0) {
            return null;
        }
        File file = files.get(0);
        files.remove(0);
        if (!file.getName().endsWith(PadReconciliationFile.FileNameSufix + companyId)) {
            throw new Error("Invalid Reconciliation file name" + file.getName());
        }
        PadReconciliationFile padFile = new PadCaledonReconciliation().processFile(file);
        move(file, padWorkdir, "processed");

        // Cleanup SFTP directory
        {
            List<File> filesToRemove = new ArrayList<File>();
            filesToRemove.add(file);
            new CaledonPadSftpClient().removeFiles(filesToRemove);
        }

        // Ignore other files received if any
        for (File otherFiles : files) {
            otherFiles.delete();
        }
        return padFile;
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

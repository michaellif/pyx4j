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

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaSystemsSimulationConfig;
import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.domain.payment.pad.PadFileCreationNumber;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.payment.pad.EFTTransportFacade;
import com.propertyvista.payment.pad.data.PadAkFile;
import com.propertyvista.server.jobs.TaskRunner;

public class PadCaledon {

    private static final Logger log = LoggerFactory.getLogger(PadCaledon.class);

    private final String companyId = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getCaledonCompanyId();

    public PadFile preparePadFile() {
        return TaskRunner.runAutonomousTransation(new Callable<PadFile>() {
            @Override
            public PadFile call() {
                EntityQueryCriteria<PadFile> criteria = EntityQueryCriteria.create(PadFile.class);
                criteria.eq(criteria.proto().companyId(), companyId);
                criteria.in(criteria.proto().status(), PadFile.PadFileStatus.Creating, PadFile.PadFileStatus.SendError);
                PadFile padFile = Persistence.service().retrieve(criteria);
                if (padFile == null) {
                    padFile = EntityFactory.create(PadFile.class);
                    padFile.status().setValue(PadFile.PadFileStatus.Creating);
                    padFile.fileCreationNumber().setValue(getNextFileCreationNumber());
                    padFile.companyId().setValue(companyId);
                    Persistence.service().persist(padFile);
                    Persistence.service().commit();
                }
                return padFile;
            }
        });
    }

    public boolean sendPadFile(final PadFile padFile) {
        EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().padFile(), padFile));
        int records = Persistence.service().count(criteria);
        if (records == 0) {
            return false;
        }

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                padFile.status().setValue(PadFile.PadFileStatus.Sending);
                padFile.sent().setValue(new Date());
                Persistence.service().merge(padFile);
                return null;
            }

        });

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {

                // Calculate Totals
                int fileRecordsCount = 0;
                BigDecimal fileAmount = BigDecimal.ZERO;
                int batchNumberCount = 0;
                Persistence.service().retrieveMember(padFile.batches());
                for (PadBatch padBatch : padFile.batches()) {
                    Persistence.service().retrieveMember(padBatch.records());

                    padBatch.batchNumber().setValue(++batchNumberCount);
                    BigDecimal batchAmount = BigDecimal.ZERO;
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

                return null;
            }

        });

        Throwable sendError = null;
        try {
            ServerSideFactory.create(EFTTransportFacade.class).sendPadFile(padFile);
            padFile.status().setValue(PadFile.PadFileStatus.Sent);
            padFile.sent().setValue(new Date());
        } catch (Throwable e) {
            sendError = e;
            padFile.status().setValue(PadFile.PadFileStatus.SendError);
        }

        padFile.batches().setAttachLevel(AttachLevel.Detached);

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                Persistence.service().merge(padFile);
                return null;
            }

        });

        if (sendError != null) {
            throw new Error(sendError.getMessage(), sendError);
        }

        return true;

    }

    /**
     * Length 4 Must be incremented by one for each file submitted per Company ID
     */
    private String getNextFileCreationNumber() {
        boolean useSimulator = VistaSystemsSimulationConfig.getConfiguration().usePadSimulator().getValue(Boolean.FALSE);

        EntityQueryCriteria<PadFileCreationNumber> criteria = EntityQueryCriteria.create(PadFileCreationNumber.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().simulator(), useSimulator));
        criteria.add(PropertyCriterion.eq(criteria.proto().companyId(), companyId));

        PadFileCreationNumber sequence = Persistence.service().retrieve(criteria);
        if (sequence == null) {
            sequence = EntityFactory.create(PadFileCreationNumber.class);
            sequence.number().setValue(0);
            sequence.simulator().setValue(useSimulator);
            sequence.companyId().setValue(companyId);
            if ((!useSimulator) && ApplicationMode.isDevelopment()) {
                sequence.number().setValue(PadCaledonDev.restoreFileCreationNumber(companyId));
            }
        }

        // Find and verify that previous file has acknowledgment
        {
            EntityQueryCriteria<PadFile> previousFileCriteria = EntityQueryCriteria.create(PadFile.class);
            previousFileCriteria.eq(previousFileCriteria.proto().companyId(), companyId);
            previousFileCriteria.eq(previousFileCriteria.proto().fileCreationNumber(), fileCreationNumberFormat(useSimulator, sequence.number().getValue()));
            PadFile padFile = Persistence.service().retrieve(previousFileCriteria);
            if (padFile != null) {
                if (!EnumSet.of(PadFile.PadFileStatus.Acknowledged, PadFile.PadFileStatus.Canceled).contains(padFile.status().getValue())) {
                    throw new Error("Can't send PAD file until previous file is processed");
                }

                //If a file has rejected the corrected file must be submitted using the same file creation number.
                if (PadFile.PadFileStatus.Canceled == padFile.status().getValue()) {
                    return fileCreationNumberFormat(useSimulator, sequence.number().getValue());
                }
            }
        }

        int id = sequence.number().getValue() + 1;
        if (id == 999999) {
            id = 1;
        }
        sequence.number().setValue(id);
        Persistence.service().persist(sequence);
        if ((!useSimulator) && ApplicationMode.isDevelopment()) {
            PadCaledonDev.saveFileCreationNumber(companyId, id);
        }
        return fileCreationNumberFormat(useSimulator, id);
    }

    private String fileCreationNumberFormat(boolean useSimulator, int value) {
        if (useSimulator) {
            return "s" + String.valueOf(value);
        } else {
            return String.valueOf(value);
        }
    }

    public boolean receivePadAcknowledgementFile(final ExecutionMonitor executionMonitor) {
        final PadAkFile padAkFile = ServerSideFactory.create(EFTTransportFacade.class).receivePadAcknowledgementFile(companyId);
        if (padAkFile == null) {
            executionMonitor.addProcessedEvent("Pooled, No file found on server");
            return false;
        } else {
            executionMonitor.addProcessedEvent("received file", padAkFile.fileName().getValue());
            executionMonitor.addProcessedEvent("fileCreationNumber", padAkFile.fileCreationNumber().getValue());
        }

        boolean processedOk = false;
        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    new PadCaledonAcknowledgement(executionMonitor).validateAndPersistFile(padAkFile);
                    return null;
                }
            });

            processedOk = true;
        } finally {
            ServerSideFactory.create(EFTTransportFacade.class).confirmReceivedFile(padAkFile.fileName().getValue(), !processedOk);
        }

        return true;
    }

    public boolean receivePadReconciliation(final ExecutionMonitor executionMonitor) {
        final PadReconciliationFile reconciliationFile = ServerSideFactory.create(EFTTransportFacade.class).receivePadReconciliation(companyId);
        if (reconciliationFile == null) {
            executionMonitor.addProcessedEvent("Pooled, No file found on server");
            return false;
        } else {
            executionMonitor.addProcessedEvent("received file", reconciliationFile.fileName().getValue());
        }

        boolean processedOk = false;
        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    new PadCaledonReconciliation().validateAndPersistFile(reconciliationFile);
                    return null;
                }
            });

            processedOk = true;
        } finally {
            ServerSideFactory.create(EFTTransportFacade.class).confirmReceivedFile(reconciliationFile.fileName().getValue(), !processedOk);
        }

        return true;
    }

}

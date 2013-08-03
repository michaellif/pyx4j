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
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.config.VistaSystemsSimulationConfig;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.domain.payment.pad.PadFileCreationNumber;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.payment.pad.EFTTransportFacade;
import com.propertyvista.payment.pad.data.PadAckFile;
import com.propertyvista.server.jobs.TaskRunner;
import com.propertyvista.server.sftp.SftpTransportConnectionException;

public class FundsTransferCaledon {

    private static final Logger log = LoggerFactory.getLogger(FundsTransferCaledon.class);

    private final String companyId = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getCaledonFundsTransferConfiguration()
            .getIntefaceCompanyId();

    public PadFile prepareFundsTransferFile(final FundsTransferType fundsTransferType) {
        return TaskRunner.runAutonomousTransation(new Callable<PadFile>() {
            @Override
            public PadFile call() {
                EntityQueryCriteria<PadFile> criteria = EntityQueryCriteria.create(PadFile.class);
                criteria.eq(criteria.proto().companyId(), companyId);
                criteria.eq(criteria.proto().fundsTransferType(), fundsTransferType);
                criteria.in(criteria.proto().status(), PadFile.PadFileStatus.Creating, PadFile.PadFileStatus.SendError);
                PadFile padFile = Persistence.service().retrieve(criteria);
                if (padFile == null) {
                    padFile = EntityFactory.create(PadFile.class);
                    padFile.status().setValue(PadFile.PadFileStatus.Creating);
                    padFile.fileCreationNumber().setValue(getNextFileCreationNumber(fundsTransferType));
                    padFile.companyId().setValue(companyId);
                    padFile.fundsTransferType().setValue(fundsTransferType);

                    Persistence.service().persist(padFile);
                    Persistence.service().commit();
                    log.info("created PadFile {} for {}", padFile.fileCreationNumber().getValue(), companyId);
                }
                return padFile;
            }
        });
    }

    static PadBatch getPadBatch(PadFile padFile, Pmc pmc, MerchantAccount merchantAccount) {
        EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
        criteria.eq(criteria.proto().padFile(), padFile);
        criteria.eq(criteria.proto().pmc(), pmc);
        criteria.eq(criteria.proto().merchantAccountKey(), merchantAccount.id());
        PadBatch padBatch = Persistence.service().retrieve(criteria);
        if (padBatch == null) {
            padBatch = EntityFactory.create(PadBatch.class);
            padBatch.padFile().set(padFile);
            padBatch.pmc().set(pmc);

            padBatch.merchantTerminalId().setValue(merchantAccount.merchantTerminalId().getValue());
            padBatch.bankId().setValue(merchantAccount.bankId().getValue());
            padBatch.branchTransitNumber().setValue(merchantAccount.branchTransitNumber().getValue());
            padBatch.accountNumber().setValue(merchantAccount.accountNumber().getValue());
            padBatch.chargeDescription().setValue(merchantAccount.chargeDescription().getValue());

            padBatch.merchantAccountKey().setValue(merchantAccount.id().getValue());
            Persistence.service().persist(padBatch);
        }
        return padBatch;
    }

    public boolean sendFundsTransferFile(final PadFile padFile) {
        EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
        criteria.eq(criteria.proto().padBatch().padFile(), padFile);
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
    private String getNextFileCreationNumber(FundsTransferType fundsTransferType) {
        boolean useSimulator = VistaSystemsSimulationConfig.getConfiguration().usePadSimulator().getValue(Boolean.FALSE);

        EntityQueryCriteria<PadFileCreationNumber> criteria = EntityQueryCriteria.create(PadFileCreationNumber.class);
        criteria.eq(criteria.proto().simulator(), useSimulator);
        criteria.eq(criteria.proto().companyId(), companyId);
        criteria.eq(criteria.proto().fundsTransferType(), fundsTransferType);

        boolean useFileBaseSequence = !VistaDeployment.isVistaProduction();
        if (useSimulator) {
            useFileBaseSequence = false;
        }

        PadFileCreationNumber sequence = Persistence.service().retrieve(criteria);
        if (sequence == null) {
            sequence = EntityFactory.create(PadFileCreationNumber.class);
            sequence.number().setValue(0);
            sequence.simulator().setValue(useSimulator);
            sequence.companyId().setValue(companyId);
            sequence.fundsTransferType().setValue(fundsTransferType);
        }
        if (useFileBaseSequence) {
            sequence.number().setValue(PadCaledonDev.restoreFileCreationNumber(companyId, fundsTransferType));
        }

        // Find and verify that previous file has acknowledgment
        {
            EntityQueryCriteria<PadFile> previousFileCriteria = EntityQueryCriteria.create(PadFile.class);
            previousFileCriteria.eq(previousFileCriteria.proto().companyId(), companyId);
            previousFileCriteria.eq(previousFileCriteria.proto().fundsTransferType(), fundsTransferType);
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
        if (useFileBaseSequence) {
            PadCaledonDev.saveFileCreationNumber(companyId, fundsTransferType, id);
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

    public FundsTransferType receiveFundsTransferAcknowledgementFile(final ExecutionMonitor executionMonitor) {
        final PadAckFile padAkFile;
        try {
            padAkFile = ServerSideFactory.create(EFTTransportFacade.class).receivePadAcknowledgementFile(companyId);
        } catch (SftpTransportConnectionException e) {
            executionMonitor.addInfoEvent("Pooled, Can't connect to server", e.getMessage());
            return null;
        }
        if (padAkFile == null) {
            executionMonitor.addInfoEvent("Pooled, No file found on server", null);
            return null;
        } else {
            executionMonitor.addInfoEvent("received file", padAkFile.fileName().getValue());
            executionMonitor.addInfoEvent("fundsTransferType" + padAkFile.fundsTransferType().getStringView(), null);
            executionMonitor.addInfoEvent("fileCreationNumber", padAkFile.fileCreationNumber().getValue());
        }

        boolean processedOk = false;
        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    new FundsTransferCaledonAcknowledgement(executionMonitor).validateAndPersistFile(padAkFile);
                    return null;
                }
            });

            processedOk = true;
        } finally {
            ServerSideFactory.create(EFTTransportFacade.class).confirmReceivedFile(padAkFile.fundsTransferType().getValue(), padAkFile.fileName().getValue(),
                    !processedOk);
        }

        return padAkFile.fundsTransferType().getValue();
    }

    public FundsTransferType receiveFundsTransferReconciliation(final ExecutionMonitor executionMonitor) {
        final PadReconciliationFile reconciliationFile;
        try {
            reconciliationFile = ServerSideFactory.create(EFTTransportFacade.class).receivePadReconciliation(companyId);
        } catch (SftpTransportConnectionException e) {
            executionMonitor.addInfoEvent("Pooled, Can't connect to server", e.getMessage());
            return null;
        }
        if (reconciliationFile == null) {
            executionMonitor.addInfoEvent("Pooled, No file found on server", null);
            return null;
        } else {
            executionMonitor.addInfoEvent("received file", reconciliationFile.fileName().getValue());
            executionMonitor.addInfoEvent("fundsTransferType", reconciliationFile.fundsTransferType().getStringView());
        }

        boolean processedOk = false;
        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    new PadCaledonReconciliation(executionMonitor).validateAndPersistFile(reconciliationFile);
                    return null;
                }
            });

            processedOk = true;
        } finally {
            ServerSideFactory.create(EFTTransportFacade.class).confirmReceivedFile(reconciliationFile.fundsTransferType().getValue(),
                    reconciliationFile.fileName().getValue(), !processedOk);
        }

        return reconciliationFile.fundsTransferType().getValue();
    }

}

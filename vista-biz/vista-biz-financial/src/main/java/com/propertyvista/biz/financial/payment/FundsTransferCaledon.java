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
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.SftpTransportConnectionException;
import com.propertyvista.biz.system.eft.EFTTransportFacade;
import com.propertyvista.biz.system.eft.FileCreationException;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.config.VistaSystemsSimulationConfig;
import com.propertyvista.domain.financial.CaledonFundsTransferType;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferBatch;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFileCreationNumber;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecord;
import com.propertyvista.operations.domain.eft.caledoneft.to.FundsTransferAckFile;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationTO;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportTO;

public class FundsTransferCaledon {

    private final ExecutionMonitor executionMonitor;

    private static final Logger log = LoggerFactory.getLogger(FundsTransferCaledon.class);

    private final String companyId = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getCaledonFundsTransferConfiguration()
            .getIntefaceCompanyId();

    FundsTransferCaledon(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    public FundsTransferFile prepareFundsTransferFile(final CaledonFundsTransferType fundsTransferType) {
        return new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<FundsTransferFile, RuntimeException>() {

            @Override
            public FundsTransferFile execute() {
                EntityQueryCriteria<FundsTransferFile> criteria = EntityQueryCriteria.create(FundsTransferFile.class);
                criteria.eq(criteria.proto().companyId(), companyId);
                criteria.eq(criteria.proto().fundsTransferType(), fundsTransferType);
                criteria.in(criteria.proto().status(), FundsTransferFile.PadFileStatus.Creating, FundsTransferFile.PadFileStatus.SendError);
                FundsTransferFile padFile = Persistence.service().retrieve(criteria);
                if (padFile == null) {
                    padFile = EntityFactory.create(FundsTransferFile.class);
                    padFile.status().setValue(FundsTransferFile.PadFileStatus.Creating);
                    padFile.fileCreationNumber().setValue(getNextFileCreationNumber(fundsTransferType));
                    padFile.companyId().setValue(companyId);
                    padFile.fundsTransferType().setValue(fundsTransferType);

                    Persistence.service().persist(padFile);
                    log.info("created FundsTransferFile # {} for {} {}", padFile.fileCreationNumber().getValue(), companyId, fundsTransferType);
                } else {
                    log.info("continue with FundsTransferFile # {} for {} {}", padFile.fileCreationNumber().getValue(), companyId, fundsTransferType);
                    executionMonitor.addInfoEvent("continue with file", padFile.fileCreationNumber().getValue());
                }
                return padFile;
            }
        });
    }

    static FundsTransferBatch getPadBatch(FundsTransferFile padFile, Pmc pmc, MerchantAccount merchantAccount) {
        EntityQueryCriteria<FundsTransferBatch> criteria = EntityQueryCriteria.create(FundsTransferBatch.class);
        criteria.eq(criteria.proto().padFile(), padFile);
        criteria.eq(criteria.proto().pmc(), pmc);
        criteria.eq(criteria.proto().merchantAccountKey(), merchantAccount.id());
        FundsTransferBatch padBatch = Persistence.service().retrieve(criteria);
        if (padBatch == null) {
            padBatch = EntityFactory.create(FundsTransferBatch.class);
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

    public boolean sendFundsTransferFile(final FundsTransferFile padFile) {
        EntityQueryCriteria<FundsTransferRecord> criteria = EntityQueryCriteria.create(FundsTransferRecord.class);
        criteria.eq(criteria.proto().padBatch().padFile(), padFile);
        int records = Persistence.service().count(criteria);
        if (records == 0) {
            executionMonitor.setMessage("Nothing to send");
            return false;
        }

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                padFile.status().setValue(FundsTransferFile.PadFileStatus.Sending);
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
                for (FundsTransferBatch padBatch : padFile.batches()) {
                    Persistence.service().retrieveMember(padBatch.records());

                    padBatch.batchNumber().setValue(++batchNumberCount);
                    BigDecimal batchAmount = BigDecimal.ZERO;
                    for (FundsTransferRecord record : padBatch.records()) {
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

        boolean revolving = false;
        boolean fileSent = false;
        try {
            ServerSideFactory.create(EFTTransportFacade.class).sendFundsTransferFile(padFile);
            padFile.status().setValue(FundsTransferFile.PadFileStatus.Sent);
            padFile.sent().setValue(new Date());

            executionMonitor.setMessage("PAD file# " + padFile.fileCreationNumber().getStringView());
            executionMonitor.addInfoEvent("sent file", padFile.fileName().getValue());
            executionMonitor.addInfoEvent("fileCreationNumber", padFile.fileCreationNumber().getValue());
            revolving = true;
            fileSent = true;
        } catch (SftpTransportConnectionException e) {
            // Allow to recover the process automatically
            padFile.status().setValue(FundsTransferFile.PadFileStatus.Creating);
            log.info("file was not sent due to connection error, set status to {} for next resend", padFile.status());
            revolving = true;
            executionMonitor.addErredEvent("connection error", e);
        } catch (FileCreationException e) {
            // Allow to recover the process automatically
            padFile.status().setValue(FundsTransferFile.PadFileStatus.Creating);
            log.info("file was not sent due file system error, set status to {} for next resend", padFile.status());
            revolving = true;
            executionMonitor.addErredEvent("create File", e);
        } finally {

            padFile.batches().setAttachLevel(AttachLevel.Detached);
            if (!revolving) {
                padFile.status().setValue(FundsTransferFile.PadFileStatus.SendError);
            }

            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    Persistence.service().merge(padFile);
                    return null;
                }

            });
        }

        return fileSent;

    }

    /**
     * Length 6
     * Must be incremented by one for each file submitted to Caledon, Unique per Company ID and FundsTransferType
     */
    private String getNextFileCreationNumber(CaledonFundsTransferType fundsTransferType) {
        boolean useSimulator = VistaSystemsSimulationConfig.getConfiguration().useFundsTransferSimulator().getValue(Boolean.FALSE);
        boolean useFileBaseSequence = !VistaDeployment.isVistaProduction();
        if (useSimulator) {
            useFileBaseSequence = false;
        }

        FundsTransferFileCreationNumber sequence;
        {
            EntityQueryCriteria<FundsTransferFileCreationNumber> criteria = EntityQueryCriteria.create(FundsTransferFileCreationNumber.class);
            criteria.eq(criteria.proto().simulator(), useSimulator);
            criteria.eq(criteria.proto().companyId(), companyId);
            criteria.eq(criteria.proto().fundsTransferType(), fundsTransferType);
            sequence = Persistence.service().retrieve(criteria);
        }
        if (sequence == null) {
            sequence = EntityFactory.create(FundsTransferFileCreationNumber.class);
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
            String previousValue = fileCreationNumberFormat(useSimulator, sequence.number().getValue());
            EntityQueryCriteria<FundsTransferFile> criteria = EntityQueryCriteria.create(FundsTransferFile.class);
            criteria.eq(criteria.proto().companyId(), companyId);
            criteria.eq(criteria.proto().fundsTransferType(), fundsTransferType);
            criteria.eq(criteria.proto().fileCreationNumber(), previousValue);
            FundsTransferFile padFile = Persistence.service().retrieve(criteria);
            if (padFile != null) {
                if (!EnumSet.of(FundsTransferFile.PadFileStatus.Acknowledged, FundsTransferFile.PadFileStatus.Canceled).contains(padFile.status().getValue())) {
                    throw new Error(SimpleMessageFormat.format("Can''t send FundsTransfer {0} File until previous file {1} is Acknowledged or Canceled",
                            fundsTransferType, previousValue));
                }

                //If a file has rejected the corrected file must be submitted using the same file creation number.
                if (FundsTransferFile.PadFileStatus.Canceled == padFile.status().getValue()) {
                    return previousValue;
                }
            }
        }

        int value = sequence.number().getValue() + 1;
        if (value == 999999) {
            value = 1;
        }

        // Assert file number duplication when creating the file, in other case index on the table will do assertions
        {
            EntityQueryCriteria<FundsTransferFile> criteria = EntityQueryCriteria.create(FundsTransferFile.class);
            criteria.eq(criteria.proto().companyId(), companyId);
            criteria.eq(criteria.proto().fundsTransferType(), fundsTransferType);
            criteria.eq(criteria.proto().fileCreationNumber(), fileCreationNumberFormat(useSimulator, value));
            if (Persistence.service().count(criteria) > 0) {
                throw new Error(SimpleMessageFormat.format("FundsTransfer {0} FileCreationNumber sequence is duplicated, the number ''{1}'' already exists",
                        fundsTransferType, value));
            }
        }

        sequence.number().setValue(value);
        Persistence.service().persist(sequence);
        if (useFileBaseSequence) {
            PadCaledonDev.saveFileCreationNumber(companyId, fundsTransferType, value);
        }

        return fileCreationNumberFormat(useSimulator, value);
    }

    private String fileCreationNumberFormat(boolean useSimulator, int value) {
        if (useSimulator) {
            return "s" + String.valueOf(value);
        } else {
            return String.valueOf(value);
        }
    }

    public CaledonFundsTransferType receiveFundsTransferAcknowledgementFile() {
        final FundsTransferAckFile padAkFile;
        try {
            padAkFile = ServerSideFactory.create(EFTTransportFacade.class).receiveFundsTransferAcknowledgementFile(companyId);
        } catch (SftpTransportConnectionException e) {
            executionMonitor.addInfoEvent("Pooled, Can't connect to server", e.getMessage());
            return null;
        }
        if (padAkFile == null) {
            executionMonitor.addInfoEvent("Pooled, No file found on server", null);
            return null;
        } else {
            executionMonitor.addInfoEvent("received file", padAkFile.fileName().getValue());
            executionMonitor.addInfoEvent("fundsTransferType", padAkFile.fundsTransferType().getStringView());
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

    public CaledonFundsTransferType receiveFundsTransferReconciliation() {
        final FundsReconciliationFile reconciliationFile;
        try {
            reconciliationFile = ServerSideFactory.create(EFTTransportFacade.class).receiveFundsTransferReconciliation(companyId);
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

    public Integer receiveCardsReconciliation() {
        Integer r;
        r = poolAndReceiveCardsDailyRepor();
        if (r != null) {
            return r;
        }
        r = poolAndReceiveCardsReconciliation();
        if (r != null) {
            return r;
        }

        return null;
    }

    Integer poolAndReceiveCardsDailyRepor() {
        // Process DailyReport to make records Cleared
        final DailyReportTO dailyReportFile;
        try {
            dailyReportFile = ServerSideFactory.create(EFTTransportFacade.class).receiveCardsDailyReportFile(
                    ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getCaledonFundsTransferConfiguration()
                            .getCardsReconciliationId());
        } catch (SftpTransportConnectionException e) {
            executionMonitor.addInfoEvent("Pooled, Can't connect to server", e.getMessage());
            return null;
        }
        if (dailyReportFile == null) {
            executionMonitor.addInfoEvent("Pooled, No dailyReport file found on server", null);
        } else {
            executionMonitor.addInfoEvent("received DailyReport file", dailyReportFile.fileName().getValue());

            boolean processedOk = false;
            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        new CardsDailyReportAcceptor(executionMonitor).validateAndPersistFile(dailyReportFile);
                        return null;
                    }
                });

                processedOk = true;
            } finally {
                Collection<String> fileNames = new ArrayList<>();
                fileNames.add(dailyReportFile.fileName().getValue());
                ServerSideFactory.create(EFTTransportFacade.class).confirmReceivedCardsReconciliationFiles(fileNames, !processedOk);
            }
            return dailyReportFile.records().size();
        }

        return null;
    }

    // Then process Reconciliation
    Integer poolAndReceiveCardsReconciliation() {
        final CardsReconciliationTO reconciliationFile;
        try {
            reconciliationFile = ServerSideFactory.create(EFTTransportFacade.class).receiveCardsReconciliationFiles(
                    ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getCaledonFundsTransferConfiguration()
                            .getCardsReconciliationId());
        } catch (SftpTransportConnectionException e) {
            executionMonitor.addInfoEvent("Pooled, Can't connect to server", e.getMessage());
            return null;
        }
        if (reconciliationFile == null) {
            executionMonitor.addInfoEvent("Pooled, No reconciliation file found on server", null);
        } else {
            executionMonitor.addInfoEvent("received CardTotal file", reconciliationFile.fileNameCardTotal().getValue());
            executionMonitor.addInfoEvent("received MerchantTotal file", reconciliationFile.fileNameMerchantTotal().getValue());

            boolean processedOk = false;
            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        new CardsReconciliationAcceptor(executionMonitor).validateAndPersistFile(reconciliationFile);
                        return null;
                    }
                });

                processedOk = true;
            } finally {
                Collection<String> fileNames = new ArrayList<>();
                fileNames.add(reconciliationFile.fileNameCardTotal().getValue());
                fileNames.add(reconciliationFile.fileNameMerchantTotal().getValue());
                ServerSideFactory.create(EFTTransportFacade.class).confirmReceivedCardsReconciliationFiles(fileNames, !processedOk);
            }
            return reconciliationFile.merchantTotals().size();
        }
        return null;
    }
}

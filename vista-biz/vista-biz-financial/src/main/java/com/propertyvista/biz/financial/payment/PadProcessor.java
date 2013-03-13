/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.EnumSet;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecordProcessing;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationSummary;
import com.propertyvista.server.jobs.TaskRunner;

public class PadProcessor {

    private static final Logger log = LoggerFactory.getLogger(PadProcessor.class);

    boolean processPayment(final PaymentRecord paymentRecord, final PadFile padFile) {
        MerchantAccount merchantAccount = PaymentUtils.retrieveMerchantAccount(paymentRecord);
        if ((merchantAccount == null) || (!PaymentUtils.isElectronicPaymentsAllowed(merchantAccount))) {
            return false;
        }
        paymentRecord.merchantAccount().set(merchantAccount);
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        Persistence.service().merge(paymentRecord);

        Persistence.service().retrieve(paymentRecord.billingAccount());

        final String namespace = NamespaceManager.getNamespace();
        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                PadBatch padBatch = getPadBatch(padFile, namespace, paymentRecord.merchantAccount());
                createPadDebitRecord(padBatch, paymentRecord);
                return null;
            }
        });

        return true;
    }

    private PadBatch getPadBatch(PadFile padFile, String namespace, MerchantAccount merchantAccount) {
        EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().padFile(), padFile));
        criteria.add(PropertyCriterion.eq(criteria.proto().pmcNamespace(), namespace));
        criteria.add(PropertyCriterion.eq(criteria.proto().merchantAccountKey(), merchantAccount.id()));
        PadBatch padBatch = Persistence.service().retrieve(criteria);
        if (padBatch == null) {
            padBatch = EntityFactory.create(PadBatch.class);
            padBatch.padFile().set(padFile);
            padBatch.pmcNamespace().setValue(namespace);

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

    private void createPadDebitRecord(PadBatch padBatch, PaymentRecord paymentRecord) {
        PadDebitRecord padRecord = EntityFactory.create(PadDebitRecord.class);
        padRecord.padBatch().set(padBatch);
        padRecord.processed().setValue(Boolean.FALSE);
        padRecord.clientId().setValue(paymentRecord.billingAccount().accountNumber().getValue());
        padRecord.amount().setValue(paymentRecord.amount().getValue());
        EcheckInfo echeckInfo = paymentRecord.paymentMethod().details().cast();

        padRecord.bankId().setValue(echeckInfo.bankId().getValue());
        padRecord.branchTransitNumber().setValue(echeckInfo.branchTransitNumber().getValue());
        padRecord.accountNumber().setValue(echeckInfo.accountNo().number().getValue());

        padRecord.transactionId().setValue(PadTransactionUtils.toCaldeonTransactionId(paymentRecord.id()));

        Persistence.service().persist(padRecord);

    }

    public void acknowledgmentReject(final PadDebitRecord debitRecord) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class,
                PadTransactionUtils.toVistaPaymentRecordId(debitRecord.transactionId()));
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Processed payment can't be rejected");
        }
        if (PaymentType.Echeck != paymentRecord.paymentMethod().type().getValue()) {
            throw new IllegalArgumentException("Invalid PaymentMethod:" + paymentRecord.paymentMethod().type().getStringView());
        }
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));

        // Caledon status codes
        if ("2001".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            paymentRecord.transactionErrorMessage().setValue("Invalid Amount");
        } else if ("2002".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            paymentRecord.transactionErrorMessage().setValue("Invalid Bank ID ");
        } else if ("2003".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            paymentRecord.transactionErrorMessage().setValue("Invalid Bank Transit Number ");
        } else if ("2004".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            paymentRecord.transactionErrorMessage().setValue("Invalid Bank Account Number ");
        } else if ("2005".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            paymentRecord.transactionErrorMessage().setValue("Invalid Reference Number");
        } else {
            paymentRecord.transactionErrorMessage().setValue(debitRecord.acknowledgmentStatusCode().getValue());
        }

        Persistence.service().merge(paymentRecord);

        try {
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, false);
        } catch (ARException e) {
            throw new Error("Processed payment can't be rejected", e);
        }

        log.info("Payment {} {} Rejected", paymentRecord.id().getValue(), paymentRecord.amount().getValue());

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                debitRecord.processed().setValue(Boolean.TRUE);
                Persistence.service().persist(debitRecord);
                return null;
            }
        });
    }

    public void aggregatedTransferRejected(PadBatch padBatch) {

        AggregatedTransfer at = EntityFactory.create(AggregatedTransfer.class);
        at.status().setValue(AggregatedTransferStatus.Rejected);
        at.paymentDate().setValue(new LogicalDate(padBatch.padFile().created().getValue()));
        at.grossPaymentAmount().setValue(padBatch.batchAmount().getValue());
        at.grossPaymentCount().setValue(padBatch.records().size());
        at.merchantAccount().setPrimaryKey(padBatch.merchantAccountKey().getValue());

        // Find MerchantAccount
        {
            // TODO handle the case when merchant account was changed.
            EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().id(), padBatch.merchantAccountKey()));
            criteria.add(PropertyCriterion.eq(criteria.proto().merchantTerminalId(), padBatch.merchantTerminalId()));
            MerchantAccount merchantAccount = Persistence.service().retrieve(criteria);
            if (merchantAccount == null) {
                throw new Error("Merchant Account '" + padBatch.merchantTerminalId().getValue() + "' not found");
            }
            merchantAccount.invalid().setValue(Boolean.TRUE);
            Persistence.service().persist(merchantAccount);
        }

        // Caledon status codes
        if ("1003".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            at.transactionErrorMessage().setValue("Invalid Terminal ID");
        } else if ("1004".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            at.transactionErrorMessage().setValue("Invalid Bank ID ");
        } else if ("1005".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            at.transactionErrorMessage().setValue("Invalid Bank Transit Number ");
        } else if ("1006".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            at.transactionErrorMessage().setValue("Invalid Bank Account Number ");
        } else if ("1007".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            at.transactionErrorMessage().setValue("Bank Information Mismatch");
        } else {
            at.transactionErrorMessage().setValue(padBatch.acknowledgmentStatusCode().getValue());
        }

        Persistence.service().persist(at);

        for (PadDebitRecord debitRecord : padBatch.records()) {
            PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class,
                    PadTransactionUtils.toVistaPaymentRecordId(debitRecord.transactionId()));
            if (paymentRecord == null) {
                throw new Error("Payment transaction '" + debitRecord.transactionId().getValue() + "' not found");
            }
            if (!EnumSet.of(PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
                throw new Error("Unexpected payment record status " + paymentRecord.getPrimaryKey() + " " + paymentRecord.paymentStatus().getValue());
            }
            // Update record status. Allow to ReSend automatically or Cancel Manually
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Queued);
            Persistence.service().persist(paymentRecord);

            PaymentRecordProcessing processing = EntityFactory.create(PaymentRecordProcessing.class);
            processing.paymentRecord().set(paymentRecord);
            processing.aggregatedTransfer().set(at);
            Persistence.service().persist(processing);
        }
    }

    void cancelAggregatedTransfer(AggregatedTransfer aggregatedTransfer) {
        Persistence.service().retrieveMember(aggregatedTransfer.rejectedBatchPayments(), AttachLevel.Attached);
        for (PaymentRecord paymentRecord : aggregatedTransfer.rejectedBatchPayments()) {
            if (paymentRecord.paymentStatus().getValue() == PaymentRecord.PaymentStatus.Queued) {
                ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecord);
            }
        }
        aggregatedTransfer.status().setValue(AggregatedTransferStatus.Canceled);
        Persistence.service().persist(aggregatedTransfer);
    }

    public void aggregatedTransferReconciliation(ExecutionMonitor executionMonitor, PadReconciliationSummary summary) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();

        final AggregatedTransfer at = EntityFactory.create(AggregatedTransfer.class);
        at.padReconciliationSummaryKey().setValue(summary.getPrimaryKey());
        switch (summary.reconciliationStatus().getValue()) {
        case HOLD:
            at.status().setValue(AggregatedTransferStatus.Hold);
            break;
        case PAID:
            at.status().setValue(AggregatedTransferStatus.Paid);
            break;
        }
        at.paymentDate().setValue(summary.paymentDate().getValue());
        at.grossPaymentAmount().setValue(summary.grossPaymentAmount().getValue());
        at.grossPaymentFee().setValue(summary.grossPaymentFee().getValue());
        at.grossPaymentCount().setValue(summary.grossPaymentCount().getValue());
        at.rejectItemsAmount().setValue(summary.rejectItemsAmount().getValue());
        at.rejectItemsFee().setValue(summary.rejectItemsFee().getValue());
        at.rejectItemsCount().setValue(summary.rejectItemsCount().getValue());
        at.returnItemsAmount().setValue(summary.returnItemsAmount().getValue());
        at.returnItemsFee().setValue(summary.returnItemsFee().getValue());
        at.returnItemsCount().setValue(summary.returnItemsCount().getValue());
        at.netAmount().setValue(summary.netAmount().getValue());
        at.adjustments().setValue(summary.adjustments().getValue());
        at.previousBalance().setValue(summary.previousBalance().getValue());
        at.merchantBalance().setValue(summary.merchantBalance().getValue());
        at.fundsReleased().setValue(summary.fundsReleased().getValue());

        // Find MerchantAccount
        {
            EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().merchantTerminalId(), summary.merchantTerminalId()));
            at.merchantAccount().set(Persistence.service().retrieve(criteria));
            if (at.merchantAccount().isNull()) {
                throw new Error("Merchant Account '" + summary.merchantTerminalId().getValue() + "' not found");
            }
        }

        Persistence.service().persist(at);

        for (final PadReconciliationDebitRecord debitRecord : summary.records()) {

            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        processDebitRecord(pmc, at, debitRecord);
                        return null;
                    }

                });

                switch (debitRecord.reconciliationStatus().getValue()) {
                case PROCESSED:
                    executionMonitor.addProcessedEvent("Processed", debitRecord.amount().getValue());
                    break;
                case REJECTED:
                    executionMonitor.addFailedEvent("Rejected", debitRecord.amount().getValue());
                    break;
                case RETURNED:
                    executionMonitor.addFailedEvent("Returned", debitRecord.amount().getValue());
                    break;
                case DUPLICATE:
                    executionMonitor.addErredEvent("Duplicate", debitRecord.amount().getValue(), "TransactionId " + debitRecord.transactionId().getValue());
                    break;
                }

            } catch (Throwable e) {
                log.error("payment transaction '" + debitRecord.transactionId().getValue() + "' processing error", e);
                executionMonitor.addErredEvent("Duplicate", debitRecord.amount().getValue());
            }
        }
    }

    private void processDebitRecord(final Pmc pmc, AggregatedTransfer at, final PadReconciliationDebitRecord debitRecord) {

        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class,
                PadTransactionUtils.toVistaPaymentRecordId(debitRecord.transactionId()));
        if (paymentRecord == null) {
            throw new Error("Payment transaction '" + debitRecord.transactionId().getValue() + "' not found in " + pmc.namespace().getStringView());
        }
        if (PaymentType.Echeck != paymentRecord.paymentMethod().type().getValue()) {
            throw new IllegalArgumentException("Invalid PaymentMethod:" + paymentRecord.paymentMethod().type().getStringView());
        }
        if (debitRecord.amount().getValue().compareTo(paymentRecord.amount().getValue()) != 0) {
            throw new Error("Unexpected transaction amount '" + paymentRecord.amount().getValue() + "', terminalId '"
                    + debitRecord.merchantTerminalId().getValue() + "', transactionId " + debitRecord.transactionId().getValue());
        }

        // Verify PAD record
        final PadDebitRecord padDebitRecord = TaskRunner.runInOperationsNamespace(new Callable<PadDebitRecord>() {
            @Override
            public PadDebitRecord call() throws Exception {
                EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().transactionId(), debitRecord.transactionId()));
                criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().pmcNamespace(), pmc.namespace()));
                return Persistence.service().retrieve(criteria);
            }
        });
        if (padDebitRecord == null) {
            throw new Error("Payment PAD transaction '" + debitRecord.transactionId().getValue() + "' not found");
        }

        switch (debitRecord.reconciliationStatus().getValue()) {
        case PROCESSED:
            if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                throw new Error("Payment PAD transaction '" + debitRecord.transactionId().getValue() + "' already received");
            }
            reconciliationClearedPayment(at, debitRecord, paymentRecord);
            break;
        case REJECTED:
            if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                throw new Error("Payment PAD transaction '" + debitRecord.transactionId().getValue() + "' already received");
            }
            reconciliationRejectPayment(at, debitRecord, paymentRecord);
            break;
        case RETURNED:
            reconciliationReturnedPayment(at, debitRecord, paymentRecord);
            break;
        case DUPLICATE:
            // TODO What todo ?
        default:
            throw new IllegalArgumentException("reconciliationStatus:" + debitRecord.reconciliationStatus().getValue());
        }

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                padDebitRecord.processed().setValue(Boolean.TRUE);
                Persistence.service().persist(padDebitRecord);
                return null;
            }
        });
    }

    private void reconciliationRejectPayment(AggregatedTransfer at, PadReconciliationDebitRecord debitRecord, PaymentRecord paymentRecord) {
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Processed payment '" + debitRecord.transactionId().getValue() + "' can't be rejected");
        }
        paymentRecord.aggregatedTransfer().set(at);
        paymentRecord.padReconciliationDebitRecordKey().setValue(debitRecord.getPrimaryKey());

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));

        paymentRecord.transactionErrorMessage().setValue(debitRecord.reasonCode().getValue() + " " + debitRecord.reasonText().getValue());

        Persistence.service().merge(paymentRecord);
        log.info("Payment {} {} Rejected", paymentRecord.id().getValue(), paymentRecord.amount().getValue());

        try {
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, true);
        } catch (ARException e) {
            throw new Error("Processed payment can't be rejected", e);
        }

    }

    private void reconciliationClearedPayment(AggregatedTransfer at, PadReconciliationDebitRecord debitRecord, PaymentRecord paymentRecord) {
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Processed payment '" + debitRecord.transactionId().getValue() + "' can't be cleared");
        }
        paymentRecord.aggregatedTransfer().set(at);
        paymentRecord.padReconciliationDebitRecordKey().setValue(debitRecord.getPrimaryKey());

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Cleared);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        Persistence.service().merge(paymentRecord);
        log.info("Payment {} {} Cleared", paymentRecord.id().getValue(), paymentRecord.amount().getValue());
    }

    private void reconciliationReturnedPayment(AggregatedTransfer at, PadReconciliationDebitRecord debitRecord, PaymentRecord paymentRecord) {
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Cleared).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Unprocessed payment '" + debitRecord.transactionId().getValue() + "' can't be returned");
        }
        paymentRecord.aggregatedTransferReturn().set(at);
        paymentRecord.padReconciliationReturnRecordKey().setValue(debitRecord.getPrimaryKey());

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Returned);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        Persistence.service().merge(paymentRecord);

        try {
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, false);
        } catch (ARException e) {
            throw new Error("Processed payment can't be returned", e);
        }

        log.info("Payment {} {} Returned", paymentRecord.id().getValue(), paymentRecord.amount().getValue());
    }
}

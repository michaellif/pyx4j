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

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.payment.pad.PadBatch;
import com.propertyvista.admin.domain.payment.pad.PadDebitRecord;
import com.propertyvista.admin.domain.payment.pad.PadFile;
import com.propertyvista.admin.domain.payment.pad.PadReconciliationDebitRecord;
import com.propertyvista.admin.domain.payment.pad.PadReconciliationSummary;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.server.jobs.TaskRunner;

public class PadProcessor {

    void queuePayment(PaymentRecord paymentRecord) {
        MerchantAccount merchantAccount = PaymentUtils.retrieveMerchantAccount(paymentRecord);
        Persistence.service().retrieve(paymentRecord.billingAccount());
        String namespace = NamespaceManager.getNamespace();
        try {
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
            PadFile padFile = getPadFile();
            PadBatch padBatch = getPadBatch(padFile, namespace, merchantAccount);
            createPadDebitRecord(padBatch, paymentRecord);
        } finally {
            NamespaceManager.setNamespace(namespace);
        }
    }

    PadFile getPadFile() {
        return TaskRunner.runAutonomousTransation(new Callable<PadFile>() {
            @Override
            public PadFile call() {
                EntityQueryCriteria<PadFile> criteria = EntityQueryCriteria.create(PadFile.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().status(), PadFile.PadFileStatus.Creating));
                PadFile padFile = Persistence.service().retrieve(criteria);
                if (padFile == null) {
                    padFile = EntityFactory.create(PadFile.class);
                    padFile.status().setValue(PadFile.PadFileStatus.Creating);
                    Persistence.service().persist(padFile);
                    Persistence.service().commit();
                }
                return padFile;
            }
        });
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
        padRecord.clientId().setValue(paymentRecord.billingAccount().accountNumber().getValue());
        padRecord.amount().setValue(paymentRecord.amount().getValue());
        EcheckInfo echeckInfo = paymentRecord.paymentMethod().details().cast();

        padRecord.bankId().setValue(echeckInfo.bankId().getValue());
        padRecord.branchTransitNumber().setValue(echeckInfo.branchTransitNumber().getValue());
        padRecord.accountNumber().setValue(echeckInfo.accountNo().getValue());

        padRecord.transactionId().setValue(paymentRecord.id().getStringView());

        Persistence.service().persist(padRecord);

    }

    public void acknowledgmentReject(PadDebitRecord debitRecord) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, new Key(debitRecord.transactionId().getValue()));
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Processed payment can't be rejected");
        }
        if (PaymentType.Echeck != paymentRecord.paymentMethod().type().getValue()) {
            throw new IllegalArgumentException("Invalid PaymentMethod:" + paymentRecord.paymentMethod().type().getStringView());
        }
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

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

        ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, false);
    }

    public void aggregatedTransferRejected(PadBatch padBatch) {
        Persistence.service().retrieveMember(padBatch.records());

        AggregatedTransfer at = EntityFactory.create(AggregatedTransfer.class);
        at.status().setValue(AggregatedTransferStatus.Rejected);
        at.paymentDate().setValue(new LogicalDate(padBatch.padFile().created().getValue()));
        at.grossPaymentAmount().setValue(padBatch.batchAmount().getValue());
        at.grossPaymentCount().setValue(padBatch.records().size());
        at.merchantAccount().setPrimaryKey(padBatch.merchantAccountKey().getValue());

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
            PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, new Key(debitRecord.transactionId().getValue()));
            if (paymentRecord == null) {
                throw new Error("Payment transaction '" + debitRecord.transactionId().getValue() + "' not found");
            }
            if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
                throw new Error("Processed payment can't be rejected");
            }
            // Do not update record status. Allow to ReSend or Cancel Manually
            paymentRecord.aggregatedTransfer().set(at);
            Persistence.service().persist(paymentRecord);
        }
    }

    public void aggregatedTransferReconciliation(PadReconciliationSummary summary) {
        AggregatedTransfer at = EntityFactory.create(AggregatedTransfer.class);
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
        at.merchantBalance().setValue(summary.merchantBalance().getValue());
        at.fundsReleased().setValue(summary.fundsReleased().getValue());

        Persistence.service().persist(at);

        for (PadReconciliationDebitRecord debitRecord : summary.records()) {
            PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, new Key(debitRecord.transactionId().getValue()));
            if (paymentRecord == null) {
                throw new Error("Payment transaction '" + debitRecord.transactionId().getValue() + "' not found");
            }
            if (PaymentType.Echeck != paymentRecord.paymentMethod().type().getValue()) {
                throw new IllegalArgumentException("Invalid PaymentMethod:" + paymentRecord.paymentMethod().type().getStringView());
            }
            if (debitRecord.amount().getValue().compareTo(paymentRecord.amount().getValue()) != 0) {
                throw new Error("Unexpected transaction amount '" + paymentRecord.amount().getValue() + "', terminalId '"
                        + debitRecord.merchantTerminalId().getValue() + "', transactionId " + debitRecord.transactionId().getValue());
            }
            switch (debitRecord.reconciliationStatus().getValue()) {
            case PROCESSED:
                reconciliationClearedPayment(at, debitRecord, paymentRecord);
                break;
            case REJECTED:
                reconciliationRejectPayment(at, debitRecord, paymentRecord);
                break;
            case RETURNED:
                reconciliationReturnedPayment(at, debitRecord, paymentRecord);
                break;
            case DUPLICATE:
                // TODO What todo ?
                reconciliationRejectPayment(at, debitRecord, paymentRecord);
                break;
            }

        }
    }

    private void reconciliationRejectPayment(AggregatedTransfer at, PadReconciliationDebitRecord debitRecord, PaymentRecord paymentRecord) {
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Processed payment can't be rejected");
        }
        paymentRecord.aggregatedTransfer().set(at);

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        paymentRecord.transactionErrorMessage().setValue(debitRecord.reasonCode().getValue() + " " + debitRecord.reasonText().getValue());

        Persistence.service().persist(paymentRecord);

        ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, true);
    }

    private void reconciliationClearedPayment(AggregatedTransfer at, PadReconciliationDebitRecord debitRecord, PaymentRecord paymentRecord) {
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Processed payment can't be cleared");
        }
        paymentRecord.aggregatedTransfer().set(at);

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Cleared);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        Persistence.service().persist(paymentRecord);
    }

    private void reconciliationReturnedPayment(AggregatedTransfer at, PadReconciliationDebitRecord debitRecord, PaymentRecord paymentRecord) {
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Cleared).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Unprocessed payment can't be returned");
        }
        paymentRecord.aggregatedTransferReturn().set(at);
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Returned);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        Persistence.service().persist(paymentRecord);
        ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, false);
    }
}

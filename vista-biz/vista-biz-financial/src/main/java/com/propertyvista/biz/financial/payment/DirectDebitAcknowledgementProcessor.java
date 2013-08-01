/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 1, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecordProcessing;
import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecordTransaction;

public class DirectDebitAcknowledgementProcessor extends AbstractAcknowledgementProcessor {

    private static final Logger log = LoggerFactory.getLogger(DirectDebitAcknowledgementProcessor.class);

    DirectDebitAcknowledgementProcessor(ExecutionMonitor executionMonitor) {
        super(FundsTransferType.DirectBankingPayment, executionMonitor);
    }

    @Override
    void retrieveOperationsPadBatchDetails(PadBatch padBatch) {
        for (PadDebitRecord debitRecord : padBatch.records()) {
            Persistence.service().retrieveMember(debitRecord.transactionRecords());
        }
    }

    // TODO this two functions are nearly the same make them more unified

    @Override
    void createRejectedAggregatedTransfer(PadBatch padBatch) {
        AggregatedTransfer at = EntityFactory.create(AggregatedTransfer.class);
        at.status().setValue(AggregatedTransferStatus.Rejected);
        at.fundsTransferType().setValue(FundsTransferType.DirectBankingPayment);
        at.paymentDate().setValue(new LogicalDate(padBatch.padFile().created().getValue()));
        at.grossPaymentAmount().setValue(padBatch.batchAmount().getValue());
        at.grossPaymentCount().setValue(padBatch.records().size());
        at.merchantAccount().setPrimaryKey(padBatch.merchantAccountKey().getValue());

        at.transactionErrorMessage().setValue(getAcknowledgmentErrorMessage(padBatch));

        Persistence.service().persist(at);

        for (PadDebitRecord debitRecord : padBatch.records()) {
            rejectPaymentRecords(debitRecord, at);
        }
    }

    @Override
    // DirectBanking is Aggregated Transfer anyway
    void acknowledgmentReject(PadDebitRecord debitRecord) {

        AggregatedTransfer at = EntityFactory.create(AggregatedTransfer.class);
        at.status().setValue(AggregatedTransferStatus.Rejected);
        at.fundsTransferType().setValue(FundsTransferType.DirectBankingPayment);
        at.paymentDate().setValue(new LogicalDate(debitRecord.padBatch().padFile().created().getValue()));
        at.grossPaymentAmount().setValue(debitRecord.amount().getValue());
        at.grossPaymentCount().setValue(1);
        at.merchantAccount().setPrimaryKey(debitRecord.padBatch().merchantAccountKey().getValue());

        at.transactionErrorMessage().setValue(getAcknowledgmentErrorMessage(debitRecord));

        Persistence.service().persist(at);

        rejectPaymentRecords(debitRecord, at);
    }

    private void rejectPaymentRecords(PadDebitRecord debitRecord, AggregatedTransfer at) {

        for (PadDebitRecordTransaction transactionRecord : debitRecord.transactionRecords()) {
            PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, transactionRecord.paymentRecordKey().getValue());

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

            log.info("Payment {} {} Queued", paymentRecord.id().getValue(), paymentRecord.amount().getValue());
        }
    }

}

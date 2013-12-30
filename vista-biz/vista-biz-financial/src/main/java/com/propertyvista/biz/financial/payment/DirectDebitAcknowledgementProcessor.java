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
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecordProcessing;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferBatch;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecord;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecordTransaction;

public class DirectDebitAcknowledgementProcessor extends AbstractAcknowledgementProcessor {

    private static final Logger log = LoggerFactory.getLogger(DirectDebitAcknowledgementProcessor.class);

    DirectDebitAcknowledgementProcessor(ExecutionMonitor executionMonitor) {
        super(FundsTransferType.DirectBankingPayment, executionMonitor);
    }

    @Override
    protected void retrieveOperationsPadBatchDetails(FundsTransferBatch padBatch) {
        for (FundsTransferRecord padDebitRecord : padBatch.records()) {
            Persistence.service().retrieveMember(padDebitRecord.transactionRecords());
        }
    }

    // TODO this two functions are nearly the same make them more unified

    @Override
    protected void createRejectedAggregatedTransfer(FundsTransferBatch padBatch) {
        AggregatedTransfer at = EntityFactory.create(AggregatedTransfer.class);
        at.status().setValue(AggregatedTransferStatus.Rejected);
        at.fundsTransferType().setValue(FundsTransferType.DirectBankingPayment);
        at.paymentDate().setValue(new LogicalDate(padBatch.padFile().created().getValue()));
        at.grossPaymentAmount().setValue(padBatch.batchAmount().getValue());
        at.grossPaymentCount().setValue(padBatch.records().size());
        at.merchantAccount().setPrimaryKey(padBatch.merchantAccountKey().getValue());

        at.transactionErrorMessage().setValue(getAcknowledgmentErrorMessage(padBatch));

        Persistence.service().persist(at);

        for (FundsTransferRecord padDebitRecord : padBatch.records()) {
            rejectPaymentRecords(padDebitRecord, at);
        }
    }

    @Override
    // DirectBanking is Aggregated Transfer anyway
    protected void acknowledgmentReject(FundsTransferRecord padDebitRecord) {

        AggregatedTransfer at = EntityFactory.create(AggregatedTransfer.class);
        at.status().setValue(AggregatedTransferStatus.Rejected);
        at.fundsTransferType().setValue(FundsTransferType.DirectBankingPayment);
        at.paymentDate().setValue(new LogicalDate(padDebitRecord.padBatch().padFile().created().getValue()));
        at.grossPaymentAmount().setValue(padDebitRecord.amount().getValue());
        at.grossPaymentCount().setValue(1);
        at.merchantAccount().setPrimaryKey(padDebitRecord.padBatch().merchantAccountKey().getValue());

        at.transactionErrorMessage().setValue(getAcknowledgmentErrorMessage(padDebitRecord));

        Persistence.service().persist(at);

        rejectPaymentRecords(padDebitRecord, at);
    }

    private void rejectPaymentRecords(FundsTransferRecord padDebitRecord, AggregatedTransfer at) {

        for (FundsTransferRecordTransaction transactionRecord : padDebitRecord.transactionRecords()) {
            PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, transactionRecord.paymentRecordKey().getValue());

            if (paymentRecord == null) {
                throw new Error("Payment transaction '" + padDebitRecord.transactionId().getValue() + "' not found");
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

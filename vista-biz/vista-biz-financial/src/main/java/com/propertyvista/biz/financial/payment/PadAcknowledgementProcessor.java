/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecordProcessing;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.operations.domain.payment.pad.FundsTransferBatch;
import com.propertyvista.operations.domain.payment.pad.FundsTransferRecord;

class PadAcknowledgementProcessor extends AbstractAcknowledgementProcessor {

    private static final Logger log = LoggerFactory.getLogger(PadAcknowledgementProcessor.class);

    PadAcknowledgementProcessor(ExecutionMonitor executionMonitor) {
        super(FundsTransferType.PreAuthorizedDebit, executionMonitor);
    }

    @Override
    protected void createRejectedAggregatedTransfer(FundsTransferBatch padBatch) {
        AggregatedTransfer at = EntityFactory.create(AggregatedTransfer.class);
        at.status().setValue(AggregatedTransferStatus.Rejected);
        at.fundsTransferType().setValue(FundsTransferType.PreAuthorizedDebit);
        at.paymentDate().setValue(new LogicalDate(padBatch.padFile().created().getValue()));
        at.grossPaymentAmount().setValue(padBatch.batchAmount().getValue());
        at.grossPaymentCount().setValue(padBatch.records().size());
        at.merchantAccount().setPrimaryKey(padBatch.merchantAccountKey().getValue());

        at.transactionErrorMessage().setValue(getAcknowledgmentErrorMessage(padBatch));

        Persistence.service().persist(at);

        for (FundsTransferRecord debitRecord : padBatch.records()) {
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

    @Override
    protected void acknowledgmentReject(FundsTransferRecord debitRecord) {
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

        paymentRecord.transactionErrorMessage().setValue(getAcknowledgmentErrorMessage(debitRecord));

        Persistence.service().merge(paymentRecord);

        try {
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, false);
        } catch (ARException e) {
            throw new Error("Processed payment can't be rejected", e);
        }

        log.info("Payment {} {} Rejected", paymentRecord.id().getValue(), paymentRecord.amount().getValue());
    }
}

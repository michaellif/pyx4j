/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;

class ScheduledPaymentsManager {

    private static final Logger log = LoggerFactory.getLogger(ScheduledPaymentsManager.class);

    void processScheduledPayments(ExecutionMonitor executionMonitor, PaymentType paymentType) {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Scheduled));
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentMethod().type(), paymentType));
        criteria.add(PropertyCriterion.le(criteria.proto().targetDate(), SystemDateManager.getDate()));

        ICursorIterator<PaymentRecord> paymentRecordIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (paymentRecordIterator.hasNext()) {
                processScheduledPayment(paymentRecordIterator.next(), executionMonitor);
            }
        } finally {
            paymentRecordIterator.close();
        }
    }

    private void processScheduledPayment(final PaymentRecord paymentRecord, final ExecutionMonitor executionMonitor) {
        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, PaymentException>() {

                @Override
                public Void execute() throws PaymentException {
                    if (paymentRecord.amount().getValue().compareTo(BigDecimal.ZERO) == 0) {
                        ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecord);
                        executionMonitor.addProcessedEvent("Canceled");
                    } else {
                        PaymentRecord processedPaymentRecord = ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);
                        if (processedPaymentRecord.paymentStatus().getValue() == PaymentRecord.PaymentStatus.Rejected) {
                            executionMonitor.addFailedEvent("Rejected", processedPaymentRecord.amount().getValue(),
                                    SimpleMessageFormat.format("Payment was rejected"));
                        } else {
                            executionMonitor.addProcessedEvent("Processed", processedPaymentRecord.amount().getValue(),
                                    SimpleMessageFormat.format("Payment was processed"));
                        }
                    }
                    return null;
                }
            });
        } catch (PaymentException e) {
            log.error("Preauthorised payment creation failed", e);
            executionMonitor.addErredEvent("Erred", paymentRecord.amount().getValue(), e);
        }
    }
}

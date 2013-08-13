/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 30, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.payment.PaymentBatchPosting.ProcessPaymentRecordInBatch;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.DirectDebitInfo;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.operations.domain.payment.dbp.DirectDebitRecord;
import com.propertyvista.operations.domain.payment.dbp.DirectDebitRecordProcessingStatus;
import com.propertyvista.server.jobs.TaskRunner;

public class DirectDebitPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(DirectDebitReceiveProcessor.class);

    public void processDirectDebitRecords(final ExecutionMonitor executionMonitor) {

        final Pmc pmc = VistaDeployment.getCurrentPmc();
        List<DirectDebitRecord> debitRecords = TaskRunner.runInOperationsNamespace(new Callable<List<DirectDebitRecord>>() {
            @Override
            public List<DirectDebitRecord> call() {
                EntityQueryCriteria<DirectDebitRecord> criteria = EntityQueryCriteria.create(DirectDebitRecord.class);
                criteria.eq(criteria.proto().processingStatus(), DirectDebitRecordProcessingStatus.Received);
                criteria.eq(criteria.proto().pmc(), pmc);
                return Persistence.service().query(criteria);
            }
        });

        for (final DirectDebitRecord debitRecord : debitRecords) {
            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        createPaymentRecord(debitRecord);
                        debitRecord.processingStatus().setValue(DirectDebitRecordProcessingStatus.Processed);
                        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                            @Override
                            public Void call() {
                                Persistence.service().persist(debitRecord);
                                return null;
                            }
                        });

                        executionMonitor.addProcessedEvent("DirectDebit", debitRecord.amount().getValue());
                        return null;
                    }

                });
            } catch (Throwable e) {
                log.error("DirectDebitRecord transaction '" + debitRecord.paymentReferenceNumber().getValue() + "' processing error", e);
                executionMonitor.addErredEvent("DirectDebit", debitRecord.amount().getValue(), e);
            }

        }

        processPayments(executionMonitor);
    }

    private void createPaymentRecord(DirectDebitRecord debitRecord) {
        EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
        criteria.eq(criteria.proto().accountNumber(), debitRecord.accountNumber());
        BillingAccount billingAccount = Persistence.service().retrieve(criteria);
        Validate.notNull(billingAccount);

        Persistence.service().retrieve(billingAccount.lease());
        Persistence.service().retrieve(billingAccount.lease().currentTerm().version().tenants());

        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        paymentRecord.billingAccount().set(billingAccount);
        paymentRecord.amount().setValue(debitRecord.amount().getValue());

        // TODO find tenant by name  in debitRecord.customerName()
        LeaseTermTenant leaseTermParticipant = billingAccount.lease().currentTerm().version().tenants().get(0);

        paymentRecord.leaseTermParticipant().set(leaseTermParticipant);

        paymentRecord.paymentMethod().customer().set(leaseTermParticipant.leaseParticipant().customer());
        paymentRecord.paymentMethod().type().setValue(PaymentType.DirectBanking);
        DirectDebitInfo details = EntityFactory.create(DirectDebitInfo.class);
        details.nameOn().setValue(debitRecord.customerName().getValue());
        details.traceNumber().setValue(debitRecord.trace().traceNumber().getValue());
        details.locationCode().setValue(debitRecord.trace().locationCode().getValue());
        paymentRecord.paymentMethod().details().set(details);

        paymentRecord.transactionAuthorizationNumber().setValue(debitRecord.paymentReferenceNumber().getValue());

        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
    }

    private void processPayments(final ExecutionMonitor executionMonitor) {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Submitted);
        criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.DirectBanking);
        criteria.asc(criteria.proto().billingAccount().lease().unit().building());

        PaymentBatchPosting.processPaymentsInBatch(executionMonitor, criteria, new ProcessPaymentRecordInBatch() {

            @Override
            public void processPayment(PaymentRecord paymentRecord, PaymentBatchContext paymentBatchContext) throws PaymentException {
                PaymentRecord processedPaymentRecord = ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, paymentBatchContext);
                if (processedPaymentRecord.paymentStatus().getValue() == PaymentRecord.PaymentStatus.Rejected) {
                    executionMonitor.addFailedEvent("PostRejected", processedPaymentRecord.amount().getValue(),
                            SimpleMessageFormat.format("Payment {0} was rejected", paymentRecord.id()));
                } else {
                    executionMonitor.addInfoEvent("Posted", null, processedPaymentRecord.amount().getValue());
                }
            }
        });
    }

}

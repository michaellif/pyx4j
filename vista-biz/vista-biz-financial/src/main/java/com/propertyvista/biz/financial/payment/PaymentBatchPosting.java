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

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.CompensationHandler;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.shared.config.VistaFeatures;

class PaymentBatchPosting {

    private static final Logger log = LoggerFactory.getLogger(PaymentBatchPosting.class);

    interface ProcessPaymentRecordInBatch {

        void processPayment(PaymentRecord paymentRecord, PaymentBatchContext paymentBatchContext) throws PaymentException;

    }

    private final boolean paymentBatchContextRequired;

    PaymentBatchPosting() {
        this.paymentBatchContextRequired = VistaFeatures.instance().yardiIntegration();
    }

    /**
     * 
     * @param criteria
     *            should be sorted by building
     * @param canCancel
     *            if zero amount or no merchant account should be canceled, disabled for DirectDebit
     * @param executionMonitor
     */
    void processPayments(EntityQueryCriteria<PaymentRecord> criteria, boolean canCancel, final ExecutionMonitor executionMonitor) {
        if (!paymentBatchContextRequired) {
            processPaymentsNoBatch(criteria, canCancel, executionMonitor);
        } else {
            processPaymentsInBatch(criteria, canCancel, executionMonitor);
        }
    }

    private void processPaymentsNoBatch(EntityQueryCriteria<PaymentRecord> criteria, boolean canCancel, final ExecutionMonitor executionMonitor) {
        // Simple flow one transaction per record
        ICursorIterator<PaymentRecord> paymentRecordIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (paymentRecordIterator.hasNext()) {
                PaymentRecord paymentRecord = paymentRecordIterator.next();
                if (canCancel && shouldBeCanceled(paymentRecord, executionMonitor)) {
                    continue;
                }
                processPaymentTransaction(paymentRecord, null, executionMonitor);
                if (executionMonitor.isTerminationRequested()) {
                    break;
                }
            }
        } finally {
            paymentRecordIterator.close();
        }
    }

    private void processPaymentsInBatch(EntityQueryCriteria<PaymentRecord> criteria, final boolean canCancel, final ExecutionMonitor executionMonitor) {
        // Flow with single transaction per batch
        final AtomicReference<PaymentRecord> iteratorPushBack = new AtomicReference<PaymentRecord>();
        final ICursorIterator<PaymentRecord> paymentRecordIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        final AtomicReference<Building> lastFailedBuilding = new AtomicReference<Building>();
        try {

            while (paymentRecordIterator.hasNext() || (iteratorPushBack.get() != null)) {
                if (executionMonitor.isTerminationRequested()) {
                    break;
                }
                // Single transaction that pool multiple paymentRecords
                final AtomicReference<String> curentBuildingCode = new AtomicReference<String>();

                try {
                    new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, ARException>() {

                        @Override
                        public Void execute() throws ARException {

                            PaymentRecord firstPaymentRecord = iteratorPushBack.get();
                            if (firstPaymentRecord == null) {
                                firstPaymentRecord = paymentRecordIterator.next();
                            } else {
                                iteratorPushBack.set(null);
                            }

                            if (canCancel && shouldBeCanceled(firstPaymentRecord, executionMonitor)) {
                                return null;
                            }

                            Building batchBuilding = getBuilding(firstPaymentRecord);
                            curentBuildingCode.set(batchBuilding.propertyCode().getValue());

                            // Ignore all the records in failed building
                            if (batchBuilding.equals(lastFailedBuilding.get())) {
                                executionMonitor.addFailedEvent("Skipped", firstPaymentRecord.amount().getValue());
                                return null;
                            }

                            // Create Batch
                            final PaymentBatchContext paymentBatchContext;
                            try {
                                paymentBatchContext = ServerSideFactory.create(ARFacade.class).createPaymentBatchContext(batchBuilding);
                            } catch (ARException e) {
                                // We may get error "Interface Entity does not have access to Yardi Property ZZZZZZ"
                                // Ignore all the records in same building
                                lastFailedBuilding.set(batchBuilding);
                                throw e;
                            }
                            lastFailedBuilding.set(null);

                            UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {

                                @Override
                                public Void execute() {
                                    try {
                                        paymentBatchContext.cancelBatch();
                                    } catch (Throwable e) {
                                        log.error("Unable to cancel batch", e);
                                        executionMonitor.addErredEvent("Batch", e);
                                    }
                                    return null;
                                }
                            });

                            //ExecutionMonitor that will be copied to main ExecutionMonitor for each successful batch 
                            ExecutionMonitor batchExecutionMonitor = new ExecutionMonitor();

                            processPaymentTransaction(firstPaymentRecord, paymentBatchContext, batchExecutionMonitor);

                            while ((!paymentBatchContext.isBatchFull()) && paymentRecordIterator.hasNext()) {
                                PaymentRecord paymentRecord = paymentRecordIterator.next();

                                if (!batchBuilding.equals(getBuilding(paymentRecord))) {
                                    iteratorPushBack.set(paymentRecord);
                                    break;
                                }

                                if (canCancel && shouldBeCanceled(paymentRecord, executionMonitor)) {
                                    // 
                                } else {
                                    processPaymentTransaction(paymentRecord, paymentBatchContext, batchExecutionMonitor);
                                }

                                if (executionMonitor.isTerminationRequested()) {
                                    break;
                                }
                            }

                            try {
                                paymentBatchContext.postBatch();
                                executionMonitor.addInfoEvent("Batch", null);

                                executionMonitor.add(batchExecutionMonitor);

                            } catch (ARException e) {
                                log.error("Unable to post batch for propertyCode {}", curentBuildingCode.get(), e);
                                executionMonitor.addErredEvent("Batch", null, "propertyCode " + curentBuildingCode.get(), e);
                            }
                            curentBuildingCode.set(null);
                            return null;
                        }
                    });
                } catch (Throwable e) {
                    log.error("Unable to create batch for propertyCode {}", curentBuildingCode.get(), e);
                    executionMonitor.addErredEvent("Batch", null, "BuildingCode " + curentBuildingCode.get(), e);
                }
            }

        } finally {
            paymentRecordIterator.close();
        }
    }

    private static Building getBuilding(PaymentRecord payment) {
        Persistence.ensureRetrieve(payment.billingAccount(), AttachLevel.Attached);
        Persistence.ensureRetrieve(payment.billingAccount().lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(payment.billingAccount().lease().unit(), AttachLevel.Attached);
        Persistence.ensureRetrieve(payment.billingAccount().lease().unit().building(), AttachLevel.Attached);
        return payment.billingAccount().lease().unit().building();
    }

    private TransactionScopeOption transactionScopeOption() {
        if (paymentBatchContextRequired) {
            return TransactionScopeOption.Nested;
        } else {
            return TransactionScopeOption.RequiresNew;
        }
    }

    private boolean shouldBeCanceled(final PaymentRecord paymentRecord, final ExecutionMonitor executionMonitor) {
        if (paymentRecord.paymentMethod().type().getValue() == PaymentType.DirectBanking) {
            return false;
        } else if (paymentRecord.amount().getValue().compareTo(BigDecimal.ZERO) <= 0) {

            new UnitOfWork(transactionScopeOption()).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecord);
                    return null;
                }
            });

            executionMonitor.addFailedEvent("Canceled Zero amount", (String) null);
            return true;
        } else if (!PaymentUtils.isElectronicPaymentsSetup(paymentRecord.billingAccount())) {

            new UnitOfWork(transactionScopeOption()).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    ServerSideFactory.create(PaymentFacade.class).cancel(paymentRecord);
                    return null;
                }
            });

            executionMonitor.addFailedEvent("Canceled ElectronicPayments Not Setup", (String) null);
            return true;
        } else {
            return false;
        }
    }

    private void processPaymentTransaction(final PaymentRecord paymentRecord, final PaymentBatchContext paymentBatchContext,
            final ExecutionMonitor executionMonitor) {
        try {
            new UnitOfWork(transactionScopeOption()).execute(new Executable<Void, PaymentException>() {

                @Override
                public Void execute() throws PaymentException {

                    PaymentRecord processedPaymentRecord = ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, paymentBatchContext);
                    if (processedPaymentRecord.paymentStatus().getValue() == PaymentRecord.PaymentStatus.Rejected) {
                        executionMonitor.addFailedEvent("PaymentRejected", processedPaymentRecord.amount().getValue(),
                                SimpleMessageFormat.format("Payment {0} was rejected", paymentRecord.id()));
                    } else {
                        executionMonitor.addProcessedEvent("PaymentPosted", processedPaymentRecord.amount().getValue());
                    }

                    return null;
                }
            });
        } catch (Throwable e) {
            log.error("Payment {} processing failed", paymentRecord, e);
            executionMonitor.addErredEvent("Erred", paymentRecord.amount().getValue(), SimpleMessageFormat.format("PaymentRecord {0} ", paymentRecord.id()), e);
        }
    }

}

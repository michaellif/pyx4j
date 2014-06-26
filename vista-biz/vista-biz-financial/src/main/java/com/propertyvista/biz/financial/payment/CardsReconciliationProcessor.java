/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 24, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.eft.cards.CardsReconciliationRecord;
import com.propertyvista.operations.domain.eft.cards.CardsReconciliationRecordProcessingStatus;
import com.propertyvista.server.TaskRunner;

class CardsReconciliationProcessor {

    private static final Logger log = LoggerFactory.getLogger(CardsReconciliationProcessor.class);

    private final ExecutionMonitor executionMonitor;

    private final Pmc pmc;

    CardsReconciliationProcessor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
        this.pmc = VistaDeployment.getCurrentPmc();
    }

    public void processPmcReconciliation() {
        List<CardsReconciliationRecord> unpocessedRecords = TaskRunner.runInOperationsNamespace(new Callable<List<CardsReconciliationRecord>>() {
            @Override
            public List<CardsReconciliationRecord> call() throws Exception {
                EntityQueryCriteria<CardsReconciliationRecord> criteria = EntityQueryCriteria.create(CardsReconciliationRecord.class);
                criteria.eq(criteria.proto().status(), CardsReconciliationRecordProcessingStatus.Received);
                criteria.eq(criteria.proto().merchantAccount().pmc(), pmc);
                return Persistence.service().query(criteria);
            }
        });

        for (final CardsReconciliationRecord reconciliationRecord : unpocessedRecords) {

            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        createAggregatedTransfer(reconciliationRecord);

                        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                            @Override
                            public Void call() {
                                reconciliationRecord.status().setValue(CardsReconciliationRecordProcessingStatus.Processed);
                                Persistence.service().persist(reconciliationRecord);
                                return null;
                            }
                        });

                        return null;
                    }

                });

                executionMonitor.addProcessedEvent("AggregatedTransfer", reconciliationRecord.totalDeposit().getValue());

            } catch (Throwable e) {
                log.error("AggregatedTransfer {} creation failed", reconciliationRecord.id().getValue(), e);
                executionMonitor.addErredEvent(
                        "AggregatedTransfer",
                        reconciliationRecord.totalDeposit().getValue(),
                        SimpleMessageFormat.format("AggregatedTransferReconciliation {0} {1}", reconciliationRecord.id(),
                                reconciliationRecord.merchantTerminalId()), e);
            }
        }
    }

    private void createAggregatedTransfer(CardsReconciliationRecord reconciliationRecord) {
        AggregatedTransfer at = EntityFactory.create(AggregatedTransfer.class);
        at.fundsTransferType().setValue(FundsTransferType.Cards);
        at.padReconciliationSummaryKey().setValue(reconciliationRecord.getPrimaryKey());
        at.status().setValue(AggregatedTransferStatus.Paid);
        at.paymentDate().setValue(reconciliationRecord.date().getValue());
        // Find MerchantAccount
        {
            EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
            OrCriterion or = criteria.or();
            or.left().eq(criteria.proto().merchantTerminalId(), reconciliationRecord.merchantTerminalId());
            or.right().eq(criteria.proto().merchantTerminalIdConvenienceFee(), reconciliationRecord.merchantTerminalId());
            at.merchantAccount().set(Persistence.service().retrieve(criteria));
            if (at.merchantAccount().isNull()) {
                throw new Error("Merchant Account '" + reconciliationRecord.merchantTerminalId().getValue() + "' not found");
            }
        }
        at.grossPaymentCount().setValue(0);
        at.grossPaymentAmount().setValue(BigDecimal.ZERO);
        at.netAmount().setValue(reconciliationRecord.totalDeposit().getValue());
        at.grossPaymentFee().setValue(reconciliationRecord.totalFee().getValue());
        // TODO all card type details

        Persistence.service().persist(at);

        attachPaymentRecords(at, reconciliationRecord);

        // AggregatedTransfer updated
        Persistence.service().persist(at);
    }

    private void attachPaymentRecords(AggregatedTransfer at, CardsReconciliationRecord reconciliationRecord) {
        LogicalDate transactionsDate = new LogicalDate(DateUtils.addDays(reconciliationRecord.date().getValue(), -1));
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.eq(criteria.proto().finalizeDate(), transactionsDate);
        criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.CreditCard);
        criteria.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Cleared);
        criteria.eq(criteria.proto().merchantAccount(), at.merchantAccount());
        criteria.isNull(criteria.proto().aggregatedTransfer());

        ICursorIterator<PaymentRecord> it = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (it.hasNext()) {
                PaymentRecord paymentRecord = it.next();
                paymentRecord.aggregatedTransfer().set(at);
                Persistence.service().persist(paymentRecord);

                at.grossPaymentCount().setValue(at.grossPaymentCount().getValue() + 1);
                at.grossPaymentAmount().setValue(at.grossPaymentAmount().getValue().add(paymentRecord.amount().getValue()));

                executionMonitor.addInfoEvent("PaymentRecord", null, paymentRecord.amount().getValue());
            }
        } finally {
            it.close();
        }
    }

}

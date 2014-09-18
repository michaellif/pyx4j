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
import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.CardsAggregatedTransfer;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.eft.cards.CardsReconciliationRecord;
import com.propertyvista.operations.domain.eft.cards.CardsReconciliationRecordProcessingStatus;
import com.propertyvista.server.TaskRunner;

class CardsReconciliationProcessor {

    private static final Logger log = LoggerFactory.getLogger(CardsReconciliationProcessor.class);

    private final ExecutionMonitor executionMonitor;

    private final Pmc pmc;

    private static class MerchantTotals {

        BigDecimal totalAmount = BigDecimal.ZERO;

        BigDecimal visaAmount = BigDecimal.ZERO;

        BigDecimal visaConvenienceFee = BigDecimal.ZERO;

        BigDecimal mastercardAmount = BigDecimal.ZERO;

        BigDecimal mastercardConvenienceFee = BigDecimal.ZERO;

    }

    CardsReconciliationProcessor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
        this.pmc = VistaDeployment.getCurrentPmc();
    }

    @SuppressWarnings("serial")
    private static class ValidationFailedRollback extends RuntimeException {
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
                final ExecutionMonitor batchExecutionMonitor = new ExecutionMonitor();

                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        createAggregatedTransfer(reconciliationRecord, batchExecutionMonitor);

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

                executionMonitor.add(batchExecutionMonitor);
                executionMonitor.addProcessedEvent("AggregatedTransfer", reconciliationRecord.totalDeposit().getValue());

            } catch (ValidationFailedRollback e) {
                log.error("AggregatedTransfer {} creation failed {}", reconciliationRecord.id().getValue(), DataDump.xmlStringView(reconciliationRecord), e);
            } catch (Throwable e) {
                log.error("AggregatedTransfer {} creation failed {}", reconciliationRecord.id().getValue(), DataDump.xmlStringView(reconciliationRecord), e);
                executionMonitor.addErredEvent(
                        "AggregatedTransfer",
                        reconciliationRecord.totalDeposit().getValue(),
                        SimpleMessageFormat.format("AggregatedTransferReconciliation {0} {1} {2}", reconciliationRecord.id(),
                                reconciliationRecord.merchantTerminalId(), reconciliationRecord.date()), e);
            }
        }
    }

    private void createAggregatedTransfer(CardsReconciliationRecord reconciliationRecord, ExecutionMonitor batchExecutionMonitor) {
        log.debug("creating CardsAggregatedTransfer {} {} {}", reconciliationRecord.merchantTerminalId(), reconciliationRecord.date(),
                reconciliationRecord.totalDeposit());
        CardsAggregatedTransfer at = EntityFactory.create(CardsAggregatedTransfer.class);
        at.fundsTransferType().setValue(FundsTransferType.Cards);
        at.cardsReconciliationRecordKey().setValue(reconciliationRecord.getPrimaryKey());
        at.status().setValue(AggregatedTransferStatus.Paid);
        at.paymentDate().setValue(reconciliationRecord.date().getValue());
        // Find MerchantAccount
        {
            EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
            criteria.eq(criteria.proto().id(), reconciliationRecord.merchantAccount().merchantAccountKey());
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
        at.netAmount().setValue(
                reconciliationRecord.totalDeposit().getValue(BigDecimal.ZERO).subtract(reconciliationRecord.totalFee().getValue(BigDecimal.ZERO)));
        at.grossPaymentFee().setValue(reconciliationRecord.totalFee().getValue(BigDecimal.ZERO));
        // all card type details

        at.visaDeposit().setValue(reconciliationRecord.visaDeposit().getValue(BigDecimal.ZERO));
        at.visaFee().setValue(reconciliationRecord.visaFee().getValue(BigDecimal.ZERO));
        at.mastercardDeposit().setValue(reconciliationRecord.mastercardDeposit().getValue(BigDecimal.ZERO));
        at.mastercardFee().setValue(reconciliationRecord.mastercardFee().getValue(BigDecimal.ZERO));

        Persistence.service().persist(at);

        MerchantTotals totals = new MerchantTotals();
        attachPaymentRecords(at, reconciliationRecord, totals, batchExecutionMonitor);

        // AggregatedTransfer updated
        Persistence.service().persist(at);

        if (reconciliationRecord.totalDeposit().getValue().compareTo(totals.totalAmount) != 0) {
            executionMonitor.addErredEvent("DailyTotals", totals.totalAmount.subtract(reconciliationRecord.totalDeposit().getValue()), //
                    SimpleMessageFormat.format("Merchant {0} {1} deposit {2} does not match transactions total {3}",//
                            reconciliationRecord.merchantTerminalId(), reconciliationRecord.date(), reconciliationRecord.totalDeposit(), totals.totalAmount));
            throw new ValidationFailedRollback();
        }

        // Validate Card Types Totals.
        if (reconciliationRecord.visaDeposit().getValue(BigDecimal.ZERO).compareTo(totals.visaAmount) != 0) {
            executionMonitor.addErredEvent("DailyTotals", totals.visaAmount.subtract(reconciliationRecord.visaDeposit().getValue(BigDecimal.ZERO)), //
                    SimpleMessageFormat.format("Merchant {0} {1} Visa Deposit {2} does not match transactions total {3}",//
                            reconciliationRecord.merchantTerminalId(), reconciliationRecord.date(), reconciliationRecord.visaDeposit(), totals.visaAmount));
            throw new ValidationFailedRollback();
        }

        if (reconciliationRecord.mastercardDeposit().getValue(BigDecimal.ZERO).compareTo(totals.mastercardAmount) != 0) {
            executionMonitor.addErredEvent("DailyTotals", totals.mastercardAmount.subtract(reconciliationRecord.mastercardDeposit().getValue(BigDecimal.ZERO)), //
                    SimpleMessageFormat.format(
                            "Merchant {0} {1} MasterCard Deposit {2} does not match transactions total {3}",//
                            reconciliationRecord.merchantTerminalId(), reconciliationRecord.date(), reconciliationRecord.mastercardDeposit(),
                            totals.mastercardAmount));
            throw new ValidationFailedRollback();
        }
    }

    private void attachPaymentRecords(CardsAggregatedTransfer at, CardsReconciliationRecord reconciliationRecord, MerchantTotals totals,
            ExecutionMonitor batchExecutionMonitor) {
        LogicalDate transactionsDate = new LogicalDate(DateUtils.addDays(reconciliationRecord.date().getValue(), -1));
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.eq(criteria.proto().finalizeDate(), transactionsDate);
        criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.CreditCard);
        criteria.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Cleared);
        criteria.eq(criteria.proto().merchantAccount(), at.merchantAccount());
        criteria.isNull(criteria.proto().aggregatedTransfer());
        if (reconciliationRecord.convenienceFeeAccount().getValue()) {
            criteria.isNotNull(criteria.proto().convenienceFeeReferenceNumber());
        } else {
            criteria.isNull(criteria.proto().convenienceFeeReferenceNumber());
        }

        ICursorIterator<PaymentRecord> it = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (it.hasNext()) {
                PaymentRecord paymentRecord = it.next();
                paymentRecord.aggregatedTransfer().set(at);
                Persistence.service().persist(paymentRecord);
                log.debug("Add paymentRecord {} {}", paymentRecord.id(), paymentRecord.amount());

                at.grossPaymentCount().setValue(at.grossPaymentCount().getValue() + 1);
                at.grossPaymentAmount().setValue(at.grossPaymentAmount().getValue().add(paymentRecord.amount().getValue()));

                totals.totalAmount = totals.totalAmount.add(paymentRecord.amount().getValue());

                switch (paymentRecord.paymentMethod().details().<CreditCardInfo> cast().cardType().getValue()) {
                case MasterCard:
                    totals.mastercardAmount = totals.mastercardAmount.add(paymentRecord.amount().getValue());
                    totals.mastercardConvenienceFee = totals.mastercardConvenienceFee.add(paymentRecord.convenienceFee().getValue(BigDecimal.ZERO));
                    break;
                case Visa:
                case VisaDebit:
                    totals.visaAmount = totals.visaAmount.add(paymentRecord.amount().getValue());
                    totals.visaConvenienceFee = totals.visaConvenienceFee.add(paymentRecord.convenienceFee().getValue(BigDecimal.ZERO));
                    break;
                }

                batchExecutionMonitor.addInfoEvent("PaymentRecord", paymentRecord.amount().getValue(), null);
            }
        } finally {
            it.close();
        }
    }

}

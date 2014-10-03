/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.Validate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.payment.CreditCardFacade.ReferenceNumberPrefix;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.AggregatedTransferNonVistaTransaction;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.eft.cards.CardsClearanceRecord;
import com.propertyvista.operations.domain.eft.cards.CardsClearanceRecord.CardsClearanceRecordType;
import com.propertyvista.operations.domain.eft.cards.CardsClearanceRecordProcessingStatus;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportRecord.DailyReportCardType;
import com.propertyvista.server.TaskRunner;

public class CardsDailyReportProcessor {

    private static final Logger log = LoggerFactory.getLogger(CardsDailyReportProcessor.class);

    private final ExecutionMonitor executionMonitor;

    private final Pmc pmc;

    CardsDailyReportProcessor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
        this.pmc = VistaDeployment.getCurrentPmc();
    }

    public void processPmcClearanceRecords() {
        List<CardsClearanceRecord> unpocessedRecords = TaskRunner.runInOperationsNamespace(new Callable<List<CardsClearanceRecord>>() {
            @Override
            public List<CardsClearanceRecord> call() throws Exception {
                EntityQueryCriteria<CardsClearanceRecord> criteria = EntityQueryCriteria.create(CardsClearanceRecord.class);
                criteria.eq(criteria.proto().status(), CardsClearanceRecordProcessingStatus.Received);
                criteria.eq(criteria.proto().merchantAccount().pmc(), pmc);
                return Persistence.service().query(criteria);
            }
        });

        for (final CardsClearanceRecord clearanceRecord : unpocessedRecords) {

            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        procesRecord(clearanceRecord);

                        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                            @Override
                            public Void call() {
                                clearanceRecord.status().setValue(CardsClearanceRecordProcessingStatus.Processed);
                                Persistence.service().persist(clearanceRecord);
                                return null;
                            }
                        });

                        return null;
                    }

                });

                executionMonitor.addProcessedEvent("CardClearance", clearanceRecord.amount().getValue());

            } catch (Throwable e) {
                log.error("CardClearance {} {} failed; {}", clearanceRecord.id().getValue(), clearanceRecord.referenceNumber(),
                        DataDump.xmlStringView(clearanceRecord), e);
                executionMonitor.addErredEvent("CardClearance", clearanceRecord.amount().getValue(),
                        SimpleMessageFormat.format("CardClearance {0} {1}", clearanceRecord.referenceNumber(), clearanceRecord.merchantID()), e);
            }
        }
    }

    protected void procesRecord(CardsClearanceRecord clearanceRecord) {
        if (ServerSideFactory.create(CreditCardFacade.class).isVistaRecordId(clearanceRecord.referenceNumber().getValue())) {
            procesRentPaymentsRecord(clearanceRecord);
        } else {
            procesNonVistaTransaction(clearanceRecord);
        }
    }

    protected void procesNonVistaTransaction(CardsClearanceRecord clearanceRecord) {
        if (!clearanceRecord.approved().getValue() || clearanceRecord.voided().getValue()) {
            log.debug("NonVistaTransaction clearanceRecord ignored {}", clearanceRecord);
            return;
        }

        if ((clearanceRecord.transactionType().getValue() != CardsClearanceRecordType.Return) //
                && ((clearanceRecord.transactionAuthorizationNumber().isNull()) //
                || (clearanceRecord.responseMessage().getValue().equals("DECLINE")))) {
            log.debug("NonVistaTransaction clearanceRecord ignored {}", clearanceRecord);
        }

        AggregatedTransferNonVistaTransaction record = EntityFactory.create(AggregatedTransferNonVistaTransaction.class);
        record.cardsClearanceRecordKey().setValue(clearanceRecord.getPrimaryKey());
        record.aggregatedTransfer().set(null);
        record.merchantAccount().id().setValue(clearanceRecord.merchantAccount().merchantAccountKey().getValue());
        record.amount().setValue(clearanceRecord.amount().getValue());
        record.cardType().setValue(getCardType(clearanceRecord.cardType().getValue()));
        record.transactionDate().setValue(clearanceRecord.clearanceDate().getValue());
        record.reconciliationDate().setValue(new LogicalDate(clearanceRecord.clearanceDate().getValue()));
        record.details().setValue(
                CommonsStringUtils.nvl_concat(clearanceRecord.cardType().getStringView(), clearanceRecord.referenceNumber().getStringView(), " "));
        Persistence.service().persist(record);

    }

    private CreditCardType getCardType(DailyReportCardType caledonCardType) {
        switch (caledonCardType) {
        case MCRD:
            return CreditCardType.MasterCard;
        case VISA:
            return CreditCardType.Visa;
        default:
            throw new Error("Can't process type " + caledonCardType);
        }
    }

    protected void procesRentPaymentsRecord(CardsClearanceRecord clearanceRecord) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(
                PaymentRecord.class,
                ServerSideFactory.create(CreditCardFacade.class).getProdAndTestVistaRecordId(ReferenceNumberPrefix.RentPayments,
                        clearanceRecord.referenceNumber().getValue()));

        if (paymentRecord == null) {
            throw new Error("Card Payment for transaction '" + clearanceRecord.referenceNumber().getValue() + "' not found");
        }
        Validate.isEquals(PaymentType.CreditCard, paymentRecord.paymentMethod().type().getValue(), "PaymentRecord {0} type", paymentRecord.id());
        Validate.isEquals(paymentRecord.amount().getValue(), clearanceRecord.amount().getValue(), "PaymentRecord {0} amount", paymentRecord.id());

        CreditCardType creditCardType = paymentRecord.paymentMethod().details().<CreditCardInfo> cast().cardType().getValue();
        switch (clearanceRecord.cardType().getValue()) {
        case MCRD:
            Validate.contains(EnumSet.of(CreditCardType.MasterCard), creditCardType, "PaymentRecord {0} cardType", paymentRecord.id());
            break;
        case VISA:
            Validate.contains(EnumSet.of(CreditCardType.Visa, CreditCardType.VisaDebit), creditCardType, "PaymentRecord {0} cardType", paymentRecord.id());
            break;
        default:
            throw new Error("Can't process type " + clearanceRecord.cardType().getValue());
        }

        if (clearanceRecord.approved().getValue() //
                && !clearanceRecord.voided().getValue() //
                && (!clearanceRecord.transactionAuthorizationNumber().isNull()) //
                && (!clearanceRecord.responseMessage().getValue().equals("DECLINE"))) {
            clearRecord(clearanceRecord, paymentRecord);
        } else {
            if (!EnumSet.of(PaymentRecord.PaymentStatus.Rejected, PaymentRecord.PaymentStatus.Void).contains(paymentRecord.paymentStatus().getValue())) {
                throw new Error(paymentRecord.paymentStatus().getValue() + " paymentRecord '" + paymentRecord.id().getValue() + "' expected to be Rejected");
            } else {
                log.debug("clearanceRecord ignored {}", clearanceRecord);
            }
        }
    }

    protected void clearRecord(CardsClearanceRecord clearanceRecord, PaymentRecord paymentRecord) {
        Validate.isEquals(PaymentRecord.PaymentStatus.Received, paymentRecord.paymentStatus().getValue(), "PaymentRecord {0} status", paymentRecord.id());

        paymentRecord.padReconciliationDebitRecordKey().setValue(clearanceRecord.getPrimaryKey());

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Cleared);
        paymentRecord.lastStatusChangeDate().setValue(SystemDateManager.getLogicalDate());
        paymentRecord.finalizeDate().setValue(new LogicalDate(clearanceRecord.clearanceDate().getValue()));
        Persistence.service().merge(paymentRecord);
        log.info("Payment {} {} {} Cleared", PaymentType.CreditCard.name(), paymentRecord.id().getValue(), paymentRecord.amount().getValue());
    }
}

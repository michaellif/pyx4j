/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 14, 2013
 * @author vlads
 */
package com.propertyvista.biz.financial.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.Validate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.CompensationHandler;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.payment.CreditCardFacade.ReferenceNumberPrefix;
import com.propertyvista.biz.system.OperationsAlertFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.eft.cards.CardTransactionRecord;
import com.propertyvista.server.TaskRunner;

class PaymentCreditCard {

    private final static Logger log = LoggerFactory.getLogger(PaymentCreditCard.class);

    static void processPayment(final PaymentRecord paymentRecord) {
        MerchantAccount account = PaymentUtils.retrieveValidMerchantAccount(paymentRecord);
        final String merchantTerminalId;
        if (paymentRecord.convenienceFeeReferenceNumber().isNull()) {
            merchantTerminalId = account.merchantTerminalId().getValue();
        } else {
            merchantTerminalId = account.merchantTerminalIdConvenienceFee().getValue();
        }

        final Pmc pmc = VistaDeployment.getCurrentPmc();

        final CardTransactionRecord transactionRecord = TaskRunner.runUnitOfWorkInOperationstNamespace(TransactionScopeOption.RequiresNew,
                new Executable<CardTransactionRecord, RuntimeException>() {
                    @Override
                    public CardTransactionRecord execute() {
                        CardTransactionRecord transactionRecord;
                        if (paymentRecord.convenienceFeeReferenceNumber().isNull()) {
                            transactionRecord = EntityFactory.create(CardTransactionRecord.class);
                            transactionRecord.amount().setValue(paymentRecord.amount().getValue());
                            transactionRecord.cardType().setValue(paymentRecord.paymentMethod().details().<CreditCardInfo> cast().cardType().getValue());
                            transactionRecord.merchantTerminalId().setValue(merchantTerminalId);
                        } else {
                            transactionRecord = Persistence.service().retrieve(
                                    CardTransactionRecord.class,
                                    ServerSideFactory.create(CreditCardFacade.class).getVistaRecordId(ReferenceNumberPrefix.RentPayments,
                                            paymentRecord.convenienceFeeReferenceNumber().getValue()));
                            Validate.isEquals(transactionRecord.amount().getValue(), transactionRecord.amount().getValue(), "Convenience Fee Reference");
                        }
                        transactionRecord.pmc().set(pmc);
                        transactionRecord.paymentTransactionId().setValue(
                                ServerSideFactory.create(CreditCardFacade.class).getTransactionreferenceNumber(ReferenceNumberPrefix.RentPayments,
                                        paymentRecord.id()));
                        Persistence.service().persist(transactionRecord);
                        return transactionRecord;
                    }
                });

        final CreditCardTransactionResponse saleResponse = ServerSideFactory.create(CreditCardFacade.class).realTimeSale(
                merchantTerminalId, //
                paymentRecord.amount().getValue(),
                paymentRecord.convenienceFee().getValue(), //
                ReferenceNumberPrefix.RentPayments, paymentRecord.id(), paymentRecord.convenienceFeeReferenceNumber().getValue(),
                paymentRecord.paymentMethod().details().<CreditCardInfo> cast());

        transactionRecord.completionDate().setValue(SystemDateManager.getDate());

        TaskRunner.runUnitOfWorkInOperationstNamespace(TransactionScopeOption.RequiresNew, new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                transactionRecord.saleResponseCode().setValue(saleResponse.code().getValue());
                transactionRecord.saleResponseText().setValue(saleResponse.message().getValue());
                Persistence.service().persist(transactionRecord);
                return null;
            }
        });

        if (saleResponse.success().getValue()) {
            log.debug("ccTransaction accepted {}", saleResponse.authorizationNumber());
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
            paymentRecord.lastStatusChangeDate().setValue(SystemDateManager.getLogicalDate());
            paymentRecord.transactionAuthorizationNumber().setValue(saleResponse.authorizationNumber().getValue());
            paymentRecord.convenienceFeeTransactionAuthorizationNumber().setValue(saleResponse.convenienceFeeAuthorizationNumber().getValue());
            ServerSideFactory.create(NotificationFacade.class).paymentCleared(paymentRecord);
        } else {
            log.debug("ccTransaction rejected {}", saleResponse.code(), saleResponse.message());
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.ProcessingReject);
            paymentRecord.lastStatusChangeDate().setValue(SystemDateManager.getLogicalDate());
            paymentRecord.transactionAuthorizationNumber().setValue(saleResponse.code().getValue());
            paymentRecord.transactionErrorMessage().setValue(saleResponse.message().getValue());
        }
        Persistence.service().persist(paymentRecord);

        if (saleResponse.success().getValue(false)) {
            UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {

                @Override
                public Void execute() {
                    try {
                        CreditCardTransactionResponse voidResponse = ServerSideFactory.create(CreditCardFacade.class).voidTransaction(merchantTerminalId, //
                                paymentRecord.amount().getValue(), paymentRecord.convenienceFee().getValue(),//
                                ReferenceNumberPrefix.RentPayments, paymentRecord.id(), paymentRecord.convenienceFeeReferenceNumber().getValue());

                        if (voidResponse.success().getValue()) {
                            log.info("transaction {} successfully voided {}", paymentRecord.id(), voidResponse.message());
                            saveVodedStatus(transactionRecord, paymentRecord, saleResponse);
                        } else {
                            log.error("Unable to void Card transaction {} {} {}; response {} {}", //
                                    merchantTerminalId, //
                                    paymentRecord.id(), //
                                    paymentRecord.amount(), //
                                    voidResponse.code(), //
                                    voidResponse.message());

                            ServerSideFactory.create(OperationsAlertFacade.class).record(paymentRecord,
                                    "Unable to void Card transaction {0} {1} {2}; response {3} {4}",//
                                    merchantTerminalId, //
                                    paymentRecord.id(), //
                                    paymentRecord.amount(), //
                                    voidResponse.code(), //
                                    voidResponse.message());
                        }
                    } catch (Throwable e) {
                        log.error("Unable to void Card transaction {} {} {}", merchantTerminalId, paymentRecord.id(), paymentRecord.amount(), e);

                        ServerSideFactory.create(OperationsAlertFacade.class).record(paymentRecord,
                                "Unable to void Card transaction {0} {1} {2}; response {3}",//
                                merchantTerminalId, //
                                paymentRecord.id(), //
                                paymentRecord.amount(), //
                                e);
                    }

                    return null;
                }

            });
        }

    }

    private static void saveVodedStatus(final CardTransactionRecord transactionRecord, PaymentRecord paymentRecord, CreditCardTransactionResponse saleResponse) {
        TaskRunner.runUnitOfWorkInOperationstNamespace(TransactionScopeOption.RequiresNew, new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                transactionRecord.voided().setValue(true);
                Persistence.service().persist(transactionRecord);
                return null;
            }
        });

        PaymentRecord record = Persistence.service().retrieve(PaymentRecord.class, paymentRecord.getPrimaryKey());
        record.transactionAuthorizationNumber().setValue(saleResponse.authorizationNumber().getValue());
        record.paymentStatus().setValue(PaymentRecord.PaymentStatus.Void);
        LogicalDate now = SystemDateManager.getLogicalDate();
        record.lastStatusChangeDate().setValue(now);
        record.receivedDate().setValue(now);
        record.finalizedDate().setValue(now);
        Persistence.service().persist(record);

    }
}

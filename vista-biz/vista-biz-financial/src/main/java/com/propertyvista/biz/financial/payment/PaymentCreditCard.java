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
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.CompensationHandler;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.financial.payment.CreditCardFacade.ReferenceNumberPrefix;
import com.propertyvista.biz.system.OperationsAlertFacade;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;

class PaymentCreditCard {

    private final static Logger log = LoggerFactory.getLogger(PaymentCreditCard.class);

    static void processPayment(final PaymentRecord paymentRecord) {
        final MerchantAccount account = PaymentUtils.retrieveValidMerchantAccount(paymentRecord);

        final CreditCardTransactionResponse saleResponse = ServerSideFactory.create(CreditCardFacade.class).realTimeSale(
                account.merchantTerminalId().getValue(), //
                paymentRecord.amount().getValue(), paymentRecord.convenienceFee().getValue(), //
                ReferenceNumberPrefix.RentPayments, paymentRecord.id().getStringView(), paymentRecord.paymentMethod().details().<CreditCardInfo> cast());

        if (saleResponse.success().getValue()) {
            log.debug("ccTransaction accepted {}", saleResponse);
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Cleared);
            paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
            paymentRecord.transactionAuthorizationNumber().setValue(saleResponse.authorizationNumber().getValue());
        } else {
            log.debug("ccTransaction rejected {}", saleResponse);
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
            paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
            paymentRecord.transactionAuthorizationNumber().setValue(saleResponse.code().getValue());
            paymentRecord.transactionErrorMessage().setValue(saleResponse.message().getValue());
        }

        if (saleResponse.success().getValue(false)) {
            UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {

                @Override
                public Void execute() {
                    try {
                        CreditCardTransactionResponse voidResponse = ServerSideFactory.create(CreditCardFacade.class).voidTransaction(
                                account.merchantTerminalId().getValue(), paymentRecord.amount().getValue(), //
                                ReferenceNumberPrefix.RentPayments, paymentRecord.id().getStringView());

                        if (voidResponse.success().getValue()) {
                            log.info("transaction {} successfully voided {}", paymentRecord.id(), voidResponse.message());
                            PaymentRecord record = Persistence.service().retrieve(PaymentRecord.class, paymentRecord.getPrimaryKey());
                            record.transactionAuthorizationNumber().setValue(saleResponse.authorizationNumber().getValue());
                            record.paymentStatus().setValue(PaymentRecord.PaymentStatus.Void);
                            LogicalDate now = new LogicalDate(SystemDateManager.getDate());
                            record.lastStatusChangeDate().setValue(now);
                            record.receivedDate().setValue(now);
                            record.finalizeDate().setValue(now);
                            Persistence.service().persist(record);
                        } else {
                            log.error("Unable to void CC transaction {} {} {}; response {} {}", //
                                    account.merchantTerminalId(), //
                                    paymentRecord.id(), //
                                    paymentRecord.amount(), //
                                    voidResponse.code(), //
                                    voidResponse.message());

                            ServerSideFactory.create(OperationsAlertFacade.class).record(paymentRecord,
                                    "Unable to void CC transaction {0} {1} {2}; response {3} {4}",//
                                    account.merchantTerminalId(), //
                                    paymentRecord.id(), // 
                                    paymentRecord.amount(), //
                                    voidResponse.code(), //
                                    voidResponse.message());
                        }
                    } catch (Throwable e) {
                        log.error("Unable to void CC transaction {} {} {}", account.merchantTerminalId(), paymentRecord.id(), paymentRecord.amount(), e);

                        ServerSideFactory.create(OperationsAlertFacade.class).record(paymentRecord, "Unable to void CC transaction {0} {1} {2}; response {3}",//
                                account.merchantTerminalId(), //
                                paymentRecord.id(), //
                                paymentRecord.amount(), //
                                e);
                    }

                    return null;
                }
            });
        }

    }
}

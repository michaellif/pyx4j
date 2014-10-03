/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 27, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.mock.cards;

import static com.propertyvista.eft.mock.cards.PaymentResponseHelper.createResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.unit.shared.UniqueInteger;

import com.propertyvista.biz.system.SftpTransportConnectionException;
import com.propertyvista.eft.caledoncards.reports.simulator.CardReconciliationSimulationManager;
import com.propertyvista.eft.mock.cards.CardTransactionMock.TransactionStatus;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationTO;
import com.propertyvista.operations.domain.eft.cards.to.CreditCardPaymentInstrument;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportTO;
import com.propertyvista.operations.domain.eft.cards.to.Merchant;
import com.propertyvista.operations.domain.eft.cards.to.PaymentInstrument;
import com.propertyvista.operations.domain.eft.cards.to.PaymentRequest;
import com.propertyvista.operations.domain.eft.cards.to.PaymentResponse;
import com.propertyvista.operations.domain.eft.cards.to.TokenPaymentInstrument;

class PCIMock {

    private static final Logger log = LoggerFactory.getLogger(PCIMock.class);

    private static class SingletonHolder {
        public static final PCIMock INSTANCE = new PCIMock();
    }

    static PCIMock instance() {
        return SingletonHolder.INSTANCE;
    }

    private final List<CardAccountMock> accounts = new ArrayList<>();

    private final Map<String, CardAccountMock> accountsByInstrument = new HashMap<>();

    private final Map<String, CardAccountMock> accountsByTransaction = new HashMap<>();

    private PCIMock() {
    }

    void reset() {
        accounts.clear();
        accountsByInstrument.clear();
        accountsByTransaction.clear();
    }

    CardAccountMock getAccount(PaymentInstrument paymentInstrument) {
        if (paymentInstrument.isInstanceOf(TokenPaymentInstrument.class)) {
            return accountsByInstrument.get("T" + paymentInstrument.<TokenPaymentInstrument> cast().code().getValue());
        } else {
            CreditCardPaymentInstrument ccinfo = paymentInstrument.<CreditCardPaymentInstrument> cast();
            CardAccountMock account = accountsByInstrument.get("C" + ccinfo.creditCardNumber().getValue());
            if (account == null) {
                account = new CardAccountMock(ccinfo);
                accounts.add(account);
                accountsByInstrument.put("C" + ccinfo.creditCardNumber().getValue(), account);
            }
            return account;
        }
    }

    PaymentResponse createToken(CreditCardPaymentInstrument ccinfo, TokenPaymentInstrument token) {
        if (accountsByInstrument.containsKey("T" + token.code().getValue())) {
            return createResponse("1102", "TOKEN ALREADY EXISTS");
        } else {
            CardAccountMock account = getAccount(ccinfo);
            accountsByInstrument.put("T" + token.code().getValue(), account);
            return createResponse("0000", "TOKEN ADDED");
        }
    }

    PaymentResponse realTimeSale(Merchant merchant, PaymentRequest request) {
        boolean convenienceFee = true;
        if (request.convenienceFee().isNull() && request.convenienceFeeReferenceNumber().isNull()) {
            convenienceFee = false;
        }
        CardAccountMock account = getAccount(request.paymentInstrument());
        if (account == null) {
            return createResponse("1101", "TOKEN NOT FOUND");
        } else {
            if (account.sale(merchant.terminalID().getValue(), request.amount().getValue(), request.referenceNumber().getValue())) {
                if (convenienceFee) {
                    account.sale(null, request.convenienceFee().getValue(), request.convenienceFeeReferenceNumber().getValue());
                    ConvenienceFeeMock.instance().addFee(request);
                }
                accountsByTransaction.put(request.referenceNumber().getValue(), account);
                PaymentResponse r = createResponse("0000", "OK");
                r.authorizationNumber().setValue(account.getTransaction(request.referenceNumber().getValue()).authorizationNumber);
                return r;
            } else {
                return createResponse("0001", "Credit limit exceeded");
            }

        }
    }

    PaymentResponse realTimePreAuthorization(Merchant merchant, PaymentRequest request) {
        CardAccountMock account = getAccount(request.paymentInstrument());
        if (account == null) {
            return createResponse("1101", "TOKEN NOT FOUND");
        } else {
            if (account.preAuthorization(request.amount().getValue(), request.referenceNumber().getValue())) {
                accountsByTransaction.put(request.referenceNumber().getValue(), account);
                return createResponse("0000", "OK");
            } else {
                return createResponse("0001", "Credit limit exceeded");
            }

        }
    }

    PaymentResponse realTimePreAuthorizationCompletion(Merchant merchant, PaymentRequest request) {
        CardAccountMock account = accountsByTransaction.get(request.referenceNumber().getValue());
        if (account == null) {
            return createResponse("1016", "COMPLETION NO MATCH");
        } else {
            if (account.completion(merchant.terminalID().getValue(), request.amount().getValue(), request.referenceNumber().getValue())) {
                accountsByTransaction.put(request.referenceNumber().getValue(), account);
                return createResponse("0000", "OK");
            } else {
                return createResponse("0001", "Credit limit exceeded");
            }

        }
    }

    public PaymentResponse returnTransaction(Merchant merchant, PaymentRequest request) {
        boolean convenienceFee = true;
        if (request.convenienceFee().isNull() && request.convenienceFeeReferenceNumber().isNull()) {
            convenienceFee = false;
        } else {
            throw new UnsupportedOperationException();
        }
        CardAccountMock account = getAccount(request.paymentInstrument());
        if (account == null) {
            return createResponse("1101", "TOKEN NOT FOUND");
        } else {
            if (account.returnTransaction(merchant.terminalID().getValue(), request.amount().getValue(), request.referenceNumber().getValue())) {
                if (convenienceFee) {
                    account.returnTransaction(null, request.convenienceFee().getValue(), request.convenienceFeeReferenceNumber().getValue());
                    ConvenienceFeeMock.instance().addFee(request);
                }
                accountsByTransaction.put(request.referenceNumber().getValue(), account);
                PaymentResponse r = createResponse("0000", "RETURN        $" + request.amount().getValue());
                //r.authorizationNumber().setValue(account.getTransaction(request.referenceNumber().getValue()).authorizationNumber);
                return r;
            } else {
                return createResponse("0001", "Credit limit exceeded");
            }

        }
    }

    public PaymentResponse voidTransaction(Merchant merchant, PaymentRequest request) {
        boolean convenienceFee = true;
        if (request.convenienceFee().isNull() && request.convenienceFeeReferenceNumber().isNull()) {
            convenienceFee = false;
        }
        CardAccountMock account = accountsByTransaction.get(request.referenceNumber().getValue());
        if (account == null) {
            log.debug("transaction {} not found", request.referenceNumber());
            return createResponse("1017", "NO MATCH " + request.referenceNumber().getValue());
        } else {
            if (account.voidTransaction(request.amount().getValue(), request.referenceNumber().getValue())) {
                if (convenienceFee) {
                    if (!account.voidTransaction(request.convenienceFee().getValue(), request.convenienceFeeReferenceNumber().getValue())) {
                        return createResponse("1017", "NO convenienceFee transaction");
                    }
                    ConvenienceFeeMock.instance().voidFee(request);
                }
                return createResponse("0000", "VOID OK");
            } else {
                return createResponse("1017", "transaction was not completed");
            }

        }
    }

    public CardsReconciliationTO receiveCardsReconciliationFiles(String cardsReconciliationId) throws SftpTransportConnectionException {
        LogicalDate transactionsDate = DateUtils.daysAdd(SystemDateManager.getLogicalDate(), -1);
        List<CardServiceSimulationTransaction> transactions = new ArrayList<>();
        for (CardAccountMock account : accounts) {
            for (CardTransactionMock transactionMock : account.transactions.values()) {
                if (transactionMock.date.equals(transactionsDate) && !transactionMock.reconciliationSent //
                        && (transactionMock.status == TransactionStatus.Compleated || transactionMock.status == TransactionStatus.Return)) {
                    transactions.add(SimulationBridge.toSimulation(account, transactionMock));
                    transactionMock.reconciliationSent = true;
                }
            }
        }
        if (transactions.size() == 0) {
            return null;
        } else {
            CardsReconciliationTO to = new CardReconciliationSimulationManager().createReport(transactions);

            to.fileNameMerchantTotal().setValue(UniqueInteger.getInstance("CardsReconciliationFile").nextAsString());
            to.fileNameCardTotal().setValue(to.fileNameMerchantTotal().getValue());
            to.remoteFileDateCardTotal().setValue(SystemDateManager.getDate());
            to.remoteFileDateMerchantTotal().setValue(SystemDateManager.getDate());
            return to;
        }
    }

    public DailyReportTO receiveCardsDailyReportFile(String cardsReconciliationId) {
        LogicalDate transactionsDate = DateUtils.daysAdd(SystemDateManager.getLogicalDate(), -1);
        List<CardServiceSimulationTransaction> transactions = new ArrayList<>();
        for (CardAccountMock account : accounts) {
            for (CardTransactionMock transactionMock : account.transactions.values()) {
                if (transactionMock.date.equals(transactionsDate) && !transactionMock.clearenceSent) {
                    transactions.add(SimulationBridge.toSimulation(account, transactionMock));
                    transactionMock.clearenceSent = true;
                }
            }
        }
        if (transactions.size() == 0) {
            return null;
        } else {
            DailyReportTO to = new CardReconciliationSimulationManager().createDailyReport(transactions);

            to.fileName().setValue(UniqueInteger.getInstance("DailyReportFile").nextAsString());
            to.remoteFileDate().setValue(SystemDateManager.getDate());
            return to;
        }
    }
}

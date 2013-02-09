/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services.sim;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.admin.domain.dev.CardServiceSimulationCard;
import com.propertyvista.admin.domain.dev.CardServiceSimulationMerchantAccount;
import com.propertyvista.admin.domain.dev.CardServiceSimulationToken;
import com.propertyvista.admin.domain.dev.CardServiceSimulationTransaction;
import com.propertyvista.admin.domain.dev.CardServiceSimulationTransaction.SimpulationTransactionType;
import com.propertyvista.payment.caledon.CaledonRequest;
import com.propertyvista.payment.caledon.CaledonRequestToken;
import com.propertyvista.payment.caledon.CaledonResponse;
import com.propertyvista.payment.caledon.CaledonTokenAction;
import com.propertyvista.payment.caledon.CaledonTransactionType;

public class CardServiceSimulationProcessor {

    private static final Logger log = LoggerFactory.getLogger(CardServiceSimulationProcessor.class);

    public static CaledonResponse execute(CaledonRequestToken caledonRequest) {
        CaledonResponse caledonResponse;
        try {
            CardServiceSimulationMerchantAccount merchantAccount = ehshureMerchantAccount(caledonRequest.terminalID);
            if (!merchantAccount.responseCode().isNull()) {
                caledonResponse = new CaledonResponse();
                caledonResponse.code = merchantAccount.responseCode().getValue();
                caledonResponse.text = "Simulator " + caledonResponse.code;
            } else {
                CaledonTransactionType transactionType = findCaledonTransactionType(caledonRequest.transactionType);
                switch (transactionType) {
                case TOKEN:
                    caledonResponse = processTokenAction(merchantAccount, caledonRequest);
                    break;
                case PREAUTH:
                case SALE:
                case AUTH_REVERSE:
                case COMPLETION:
                    caledonResponse = processCard(merchantAccount, caledonRequest);
                    break;
                default:
                    throw new Error("Unsupported transactionType '" + transactionType + "'");
                }
            }

        } finally {
            Persistence.service().commit();
        }
        return caledonResponse;
    }

    private static CaledonTransactionType findCaledonTransactionType(String transactionTypeChar) {
        for (CaledonTransactionType transactionType : EnumSet.allOf(CaledonTransactionType.class)) {
            if (transactionType.getValue().equals(transactionTypeChar)) {
                return transactionType;
            }
        }
        throw new Error("Invalid transactionType '" + transactionTypeChar + "'");
    }

    private static CaledonTokenAction findCaledonTokenAction(String tokenAction) {
        for (CaledonTokenAction type : EnumSet.allOf(CaledonTokenAction.class)) {
            if (type.getValue().equals(tokenAction)) {
                return type;
            }
        }
        throw new Error("Invalid tokenAction '" + tokenAction + "'");
    }

    private static CardServiceSimulationMerchantAccount ehshureMerchantAccount(String terminalID) {
        if (CommonsStringUtils.isEmpty(terminalID)) {
            throw new Error("No terminalID");
        }
        EntityQueryCriteria<CardServiceSimulationMerchantAccount> criteria = EntityQueryCriteria.create(CardServiceSimulationMerchantAccount.class);
        criteria.eq(criteria.proto().terminalID(), terminalID);
        CardServiceSimulationMerchantAccount merchantAccount = Persistence.service().retrieve(criteria);
        if (merchantAccount == null) {
            merchantAccount = EntityFactory.create(CardServiceSimulationMerchantAccount.class);
            merchantAccount.terminalID().setValue(terminalID);
            merchantAccount.balance().setValue(BigDecimal.ZERO);
            Persistence.service().persist(merchantAccount);
        }
        return merchantAccount;
    }

    private static CaledonResponse processTokenAction(CardServiceSimulationMerchantAccount merchantAccount, CaledonRequestToken caledonRequest) {
        CaledonResponse caledonResponse = new CaledonResponse();
        if (CommonsStringUtils.isEmpty(caledonRequest.token)) {
            throw new Error("No token");
        }

        EntityQueryCriteria<CardServiceSimulationToken> criteria = EntityQueryCriteria.create(CardServiceSimulationToken.class);
        criteria.eq(criteria.proto().token(), caledonRequest.token);
        criteria.eq(criteria.proto().card().merchant(), merchantAccount);
        CardServiceSimulationToken token = Persistence.service().retrieve(criteria);

        CaledonTokenAction tokenAction = findCaledonTokenAction(caledonRequest.tokenAction);
        switch (tokenAction) {
        case ADD:
            if (token != null) {
                caledonResponse.code = "1102";
                caledonResponse.text = "TOKEN ALREADY EXISTS";
            } else {
                token = EntityFactory.create(CardServiceSimulationToken.class);
                CardServiceSimulationCard card = createCard(merchantAccount, caledonRequest);
                token.token().setValue(caledonRequest.token);
                token.active().setValue(Boolean.TRUE);
                card.tokens().add(token);

                Persistence.service().persist(card);

                caledonResponse.code = "0000";
                caledonResponse.text = "TOKEN ADDED";
            }
            break;
        case DEACTIVATE:
            if (token == null) {
                caledonResponse.code = "1101";
                caledonResponse.text = "TOKEN NOT FOUND";
            } else {
                token.active().setValue(Boolean.FALSE);
                Persistence.service().persist(token);

                caledonResponse.code = "0000";
                caledonResponse.text = "TOKEN DEACTIVATED";
            }
            break;
        case REACTIVATE:
            if (token == null) {
                caledonResponse.code = "1101";
                caledonResponse.text = "TOKEN NOT FOUND";
            } else {
                token.active().setValue(Boolean.TRUE);
                Persistence.service().persist(token);

                caledonResponse.code = "0000";
                caledonResponse.text = "TOKEN REACTIVATED";
            }
            break;
        case UPDATE:
            if (token == null) {
                caledonResponse.code = "1101";
                caledonResponse.text = "TOKEN NOT FOUND";
            } else {
                token.card().number().setValue(caledonRequest.creditCardNumber);
                token.card().expiryDate().setValue(parsDate(caledonRequest.expiryDate));
                Persistence.service().persist(token.card());

                caledonResponse.code = "0000";
                caledonResponse.text = "TOKEN UPDATED";
            }
            break;
        }
        return caledonResponse;
    }

    private static LogicalDate parsDate(String expiryDate) {
        try {
            Date date = new SimpleDateFormat("MMyydd").parse(expiryDate + "01");
            return new LogicalDate(date);
        } catch (ParseException e) {
            throw new Error(e);
        }
    }

    private static BigDecimal parsAmount(String amount) {
        String valueCents;
        String valueDollars;
        int len = amount.length();
        if (len <= 2) {
            valueCents = amount;
            valueDollars = "0";
        } else {
            valueCents = amount.substring(len - 2, len);
            valueDollars = amount.substring(0, len - 2);
        }

        BigDecimal money = new BigDecimal(valueDollars + "." + valueCents);
        return money.setScale(2);
    }

    private static CaledonResponse processCard(CardServiceSimulationMerchantAccount merchantAccount, CaledonRequest caledonRequest) {
        CaledonResponse caledonResponse = new CaledonResponse();
        CardServiceSimulationCard card = ehshureCard(merchantAccount, caledonRequest);
        if (card == null) {
            caledonResponse.code = "1101";
            caledonResponse.text = "TOKEN NOT FOUND";
            return caledonResponse;
        }

        CaledonTransactionType transactionType = findCaledonTransactionType(caledonRequest.transactionType);

        CardServiceSimulationTransaction transaction = EntityFactory.create(CardServiceSimulationTransaction.class);
        transaction.card().set(card);
        transaction.amount().setValue(parsAmount(caledonRequest.amount));
        transaction.reference().setValue(caledonRequest.referenceNumber);

        switch (transactionType) {
        case SALE:
            transaction.transactionType().setValue(SimpulationTransactionType.sale);
            break;
        case PREAUTH:
            transaction.transactionType().setValue(SimpulationTransactionType.preAuthorization);
            break;
        case AUTH_REVERSE:
            transaction.transactionType().setValue(SimpulationTransactionType.preAuthorizationReversal);
            break;
        case COMPLETION:
            transaction.transactionType().setValue(SimpulationTransactionType.completion);
            break;
        default:
            throw new Error("Unsupported transactionType '" + transactionType + "'");
        }

        if (!card.responseCode().isNull()) {
            caledonResponse.code = card.responseCode().getStringView();
            caledonResponse.text = "Simulated code '" + card.responseCode().getStringView() + "'";
        } else if (card.expiryDate().getValue().after(getExpiryMonthEnd())) {
            caledonResponse.code = "1254";
            caledonResponse.text = "EXPIRED CARD";
        } else {

            BigDecimal newBalance;
            switch (transactionType) {
            case SALE:
                newBalance = card.balance().getValue().subtract(transaction.amount().getValue());
                if (newBalance.compareTo(BigDecimal.ZERO) == -1) {
                    caledonResponse.code = "0001";
                    caledonResponse.text = "Credit limit exceeded";
                } else {
                    card.balance().setValue(newBalance);
                    caledonResponse.code = "0000";
                    caledonResponse.authorizationNumber = "T" + new Random().nextInt(99999);
                    caledonResponse.text = caledonResponse.authorizationNumber + " $" + transaction.amount().getStringView();
                    moveMoney(merchantAccount, transaction.amount().getValue());
                }
                break;
            case PREAUTH:
                newBalance = card.balance().getValue().subtract(transaction.amount().getValue()).subtract(card.reserved().getValue(BigDecimal.ZERO));
                if (newBalance.compareTo(BigDecimal.ZERO) == -1) {
                    caledonResponse.code = "0001";
                    caledonResponse.text = "Credit limit exceeded";
                } else {
                    card.reserved().setValue(transaction.amount().getValue().add(card.reserved().getValue(BigDecimal.ZERO)));
                    caledonResponse.code = "0000";
                    caledonResponse.authorizationNumber = "T" + new Random().nextInt(99999);
                    caledonResponse.text = caledonResponse.authorizationNumber + " $" + transaction.amount().getStringView();
                }
                break;
            case AUTH_REVERSE:
                caledonResponse.code = "0000";
                caledonResponse.text = "REVERSE OK";
                break;
            case COMPLETION:
                caledonResponse.code = "0000";
                moveMoney(merchantAccount, transaction.amount().getValue());
                break;
            default:
                throw new Error("Unsupported transactionType '" + transactionType + "'");
            }
        }

        transaction.authorizationNumber().setValue(caledonResponse.authorizationNumber);
        transaction.responseCode().setValue(caledonResponse.code);
        Persistence.service().persist(transaction);
        Persistence.service().persist(card);
        return caledonResponse;
    }

    private static Date getExpiryMonthEnd() {
        Calendar c = Calendar.getInstance();
        c.setTime(Persistence.service().getTransactionSystemTime());
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);
        return c.getTime();
    }

    private static void moveMoney(CardServiceSimulationMerchantAccount merchantAccount, BigDecimal value) {
        merchantAccount.balance().setValue(merchantAccount.balance().getValue(BigDecimal.ZERO).add(value));
        Persistence.service().persist(merchantAccount);
    }

    private static CardServiceSimulationCard createCard(CardServiceSimulationMerchantAccount merchantAccount, CaledonRequest caledonRequest) {
        CardServiceSimulationCard card = EntityFactory.create(CardServiceSimulationCard.class);
        card.merchant().set(merchantAccount);
        card.number().setValue(caledonRequest.creditCardNumber);
        card.expiryDate().setValue(parsDate(caledonRequest.expiryDate));
        card.balance().setValue(new BigDecimal("10000"));
        card.reserved().setValue(BigDecimal.ZERO);
        return card;
    }

    private static CardServiceSimulationCard ehshureCard(CardServiceSimulationMerchantAccount merchantAccount, CaledonRequest caledonRequest) {
        if (CommonsStringUtils.isStringSet(caledonRequest.token)) {
            EntityQueryCriteria<CardServiceSimulationToken> criteria = EntityQueryCriteria.create(CardServiceSimulationToken.class);
            criteria.eq(criteria.proto().token(), caledonRequest.token);
            criteria.eq(criteria.proto().card().merchant(), merchantAccount);
            CardServiceSimulationToken token = Persistence.service().retrieve(criteria);
            if (token != null) {
                return token.card();
            } else {
                return null;
            }
        } else {
            EntityQueryCriteria<CardServiceSimulationCard> criteria = EntityQueryCriteria.create(CardServiceSimulationCard.class);
            criteria.eq(criteria.proto().number(), caledonRequest.creditCardNumber);
            criteria.eq(criteria.proto().merchant(), merchantAccount);
            CardServiceSimulationCard card = Persistence.service().retrieve(criteria);
            if (card == null) {
                card = createCard(merchantAccount, caledonRequest);
                Persistence.service().persist(card);
            }
            return card;
        }
    }
}

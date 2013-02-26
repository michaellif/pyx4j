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
package com.propertyvista.operations.server.services.sim;

import java.math.BigDecimal;
import java.util.Random;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.operations.domain.dev.CardServiceSimulationCard;
import com.propertyvista.operations.domain.dev.CardServiceSimulationMerchantAccount;
import com.propertyvista.operations.domain.dev.CardServiceSimulationToken;
import com.propertyvista.operations.domain.dev.CardServiceSimulationTransaction;
import com.propertyvista.payment.caledon.CaledonRequest;
import com.propertyvista.payment.caledon.CaledonRequestToken;
import com.propertyvista.payment.caledon.CaledonResponse;
import com.propertyvista.payment.caledon.CaledonTokenAction;
import com.propertyvista.payment.caledon.CaledonTransactionType;

public class CardServiceSimulationProcessor {

    public static CaledonResponse execute(CaledonRequestToken caledonRequest) {
        CaledonResponse caledonResponse;
        try {
            CardServiceSimulationMerchantAccount merchantAccount = ehshureMerchantAccount(caledonRequest.terminalID);
            if (!merchantAccount.responseCode().isNull()) {
                caledonResponse = new CaledonResponse();
                caledonResponse.code = merchantAccount.responseCode().getValue();
                caledonResponse.text = "Simulated merchant code '" + caledonResponse.code + "'";
            } else {
                CaledonTransactionType transactionType = CardServiceSimulationUtils.toCaledonTransactionType(caledonRequest.transactionType);
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

        CaledonTokenAction tokenAction = CardServiceSimulationUtils.toCaledonTokenAction(caledonRequest.tokenAction);
        switch (tokenAction) {
        case ADD:
            if (token != null) {
                caledonResponse.code = "1102";
                caledonResponse.text = "TOKEN ALREADY EXISTS";
            } else {
                token = EntityFactory.create(CardServiceSimulationToken.class);
                CardServiceSimulationCard card = ensureUnAttachedCard(merchantAccount, caledonRequest);
                if (card.cardType().isNull()) {
                    caledonResponse.code = "1020";
                    caledonResponse.text = "CARD NUMBER INVALID";
                } else {
                    token.token().setValue(caledonRequest.token);
                    token.active().setValue(Boolean.TRUE);
                    card.tokens().add(token);

                    Persistence.service().persist(card);

                    caledonResponse.code = "0000";
                    caledonResponse.text = "TOKEN ADDED";
                }
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
                token.card().expiryDate().setValue(CardServiceSimulationUtils.parsDate(caledonRequest.expiryDate));
                Persistence.service().persist(token.card());

                caledonResponse.code = "0000";
                caledonResponse.text = "TOKEN UPDATED";
            }
            break;
        }
        return caledonResponse;
    }

    private static CaledonResponse processCard(CardServiceSimulationMerchantAccount merchantAccount, CaledonRequest caledonRequest) {
        CaledonResponse caledonResponse = new CaledonResponse();
        CardServiceSimulationCard card = ensureCard(merchantAccount, caledonRequest);
        if (card == null) {
            caledonResponse.code = "1101";
            caledonResponse.text = "TOKEN NOT FOUND";
            return caledonResponse;
        }
        if (card.cardType().isNull()) {
            caledonResponse.code = "1020";
            caledonResponse.text = "CARD NUMBER INVALID";
            return caledonResponse;
        }

        CaledonTransactionType transactionType = CardServiceSimulationUtils.toCaledonTransactionType(caledonRequest.transactionType);

        CardServiceSimulationTransaction createdTransaction = findSimulatedResponce(card, caledonRequest);
        if (createdTransaction != null) {
            caledonResponse.code = createdTransaction.responseCode().getValue();
            caledonResponse.text = "Simulated Tx code '" + createdTransaction.responseCode().getStringView() + "'";

            createdTransaction.amount().setValue(CardServiceSimulationUtils.parsAmount(caledonRequest.amount));
            createdTransaction.reference().setValue(caledonRequest.referenceNumber);
            createdTransaction.transactionType().setValue(CardServiceSimulationUtils.toSimTransactionType(transactionType));
            createdTransaction.scheduledSimulatedResponce().setValue(Boolean.FALSE);
            Persistence.service().persist(createdTransaction);
            return caledonResponse;
        }

        CardServiceSimulationTransaction transaction = EntityFactory.create(CardServiceSimulationTransaction.class);
        transaction.card().set(card);
        transaction.amount().setValue(CardServiceSimulationUtils.parsAmount(caledonRequest.amount));
        transaction.reference().setValue(caledonRequest.referenceNumber);
        transaction.transactionType().setValue(CardServiceSimulationUtils.toSimTransactionType(transactionType));

        if (!card.responseCode().isNull()) {
            caledonResponse.code = card.responseCode().getStringView();
            caledonResponse.text = "Simulated Card code '" + card.responseCode().getStringView() + "'";
        } else if (card.expiryDate().getValue().after(CardServiceSimulationUtils.getExpiryMonthEnd())) {
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
            case AUTH_REVERSE: {
                CardServiceSimulationTransaction preAuthorizationTransaction = findPreAuthorization(card, caledonRequest.referenceNumber);
                if (preAuthorizationTransaction != null) {
                    BigDecimal reversalAmount;
                    //Pre-authorization reversal full amount (amount of '0' is used as the replacement amount)
                    if (transaction.amount().getValue().compareTo(BigDecimal.ZERO) == 0) {
                        reversalAmount = preAuthorizationTransaction.amount().getValue();
                    } else {
                        reversalAmount = transaction.amount().getValue();
                    }
                    card.reserved().setValue(card.reserved().getValue(BigDecimal.ZERO).subtract(reversalAmount));
                    caledonResponse.code = "0000";
                    caledonResponse.text = "REVERSE OK";
                } else {
                    caledonResponse.code = "1016";
                    caledonResponse.text = "REVERSE NO MATCH";
                }
            }
                break;
            case COMPLETION:
                CardServiceSimulationTransaction preAuthorizationTransaction = findPreAuthorization(card, caledonRequest.referenceNumber);
                if (preAuthorizationTransaction != null) {
                    newBalance = card.balance().getValue().subtract(transaction.amount().getValue());
                    card.balance().setValue(newBalance);
                    card.reserved().setValue(card.reserved().getValue(BigDecimal.ZERO).subtract(transaction.amount().getValue()));

                    caledonResponse.code = "0000";
                    moveMoney(merchantAccount, transaction.amount().getValue());
                } else {
                    caledonResponse.code = "1016";
                    caledonResponse.text = "COMPLETION NO MATCH";
                }
                break;
            default:
                throw new Error("Unsupported transactionType '" + transactionType + "'");
            }
        }

        transaction.authorizationNumber().setValue(caledonResponse.authorizationNumber);
        transaction.responseCode().setValue(caledonResponse.code);
        transaction.scheduledSimulatedResponce().setValue(Boolean.FALSE);
        Persistence.service().persist(transaction);
        Persistence.service().persist(card);
        return caledonResponse;
    }

    private static CardServiceSimulationTransaction findSimulatedResponce(CardServiceSimulationCard card, CaledonRequest caledonRequest) {
        CardServiceSimulationTransaction transaction;
        {
            EntityQueryCriteria<CardServiceSimulationTransaction> criteria = EntityQueryCriteria.create(CardServiceSimulationTransaction.class);
            criteria.eq(criteria.proto().card(), card);
            criteria.eq(criteria.proto().transactionType(), CardServiceSimulationUtils.toSimTransactionType(caledonRequest.transactionType));
            criteria.eq(criteria.proto().scheduledSimulatedResponce(), Boolean.TRUE);
            transaction = Persistence.service().retrieve(criteria);
            if (transaction != null) {
                return transaction;
            }
        }
        {
            EntityQueryCriteria<CardServiceSimulationTransaction> criteria = EntityQueryCriteria.create(CardServiceSimulationTransaction.class);
            criteria.eq(criteria.proto().card(), card);
            criteria.eq(criteria.proto().scheduledSimulatedResponce(), Boolean.TRUE);
            transaction = Persistence.service().retrieve(criteria);
            if (transaction != null) {
                return transaction;
            }
        }
        return null;
    }

    private static CardServiceSimulationTransaction findPreAuthorization(CardServiceSimulationCard card, String referenceNumber) {
        EntityQueryCriteria<CardServiceSimulationTransaction> criteria = EntityQueryCriteria.create(CardServiceSimulationTransaction.class);
        criteria.eq(criteria.proto().card(), card);
        criteria.eq(criteria.proto().reference(), referenceNumber);
        return Persistence.service().retrieve(criteria);
    }

    private static void moveMoney(CardServiceSimulationMerchantAccount merchantAccount, BigDecimal value) {
        merchantAccount.balance().setValue(merchantAccount.balance().getValue(BigDecimal.ZERO).add(value));
        Persistence.service().persist(merchantAccount);
    }

    private static CardServiceSimulationCard createCard(CardServiceSimulationMerchantAccount merchantAccount, CaledonRequest caledonRequest) {
        CardServiceSimulationCard card = EntityFactory.create(CardServiceSimulationCard.class);
        card.merchant().set(merchantAccount);
        card.number().setValue(caledonRequest.creditCardNumber);
        card.cardType().setValue(CardServiceSimulationUtils.detectCardType(caledonRequest.creditCardNumber));
        card.expiryDate().setValue(CardServiceSimulationUtils.parsDate(caledonRequest.expiryDate));
        card.balance().setValue(new BigDecimal("10000"));
        card.reserved().setValue(BigDecimal.ZERO);
        return card;
    }

    private static CardServiceSimulationCard ensureUnAttachedCard(CardServiceSimulationMerchantAccount merchantAccount, CaledonRequest caledonRequest) {
        CardServiceSimulationCard card;
        {
            EntityQueryCriteria<CardServiceSimulationCard> criteria = EntityQueryCriteria.create(CardServiceSimulationCard.class);
            criteria.eq(criteria.proto().number(), caledonRequest.creditCardNumber);
            criteria.isNull(criteria.proto().merchant());
            card = Persistence.service().retrieve(criteria);
            if (card != null) {
                card.merchant().set(merchantAccount);
                Persistence.service().persist(card);
                return card;
            }
        }
        {
            EntityQueryCriteria<CardServiceSimulationCard> criteria = EntityQueryCriteria.create(CardServiceSimulationCard.class);
            criteria.eq(criteria.proto().number(), caledonRequest.creditCardNumber);
            criteria.eq(criteria.proto().merchant(), merchantAccount);
            criteria.notExists(criteria.proto().tokens());
            card = Persistence.service().retrieve(criteria);
            if (card != null) {
                return card;
            }
        }
        card = createCard(merchantAccount, caledonRequest);
        if (!card.cardType().isNull()) {
            Persistence.service().persist(card);
        }
        return card;
    }

    private static CardServiceSimulationCard ensureCard(CardServiceSimulationMerchantAccount merchantAccount, CaledonRequest caledonRequest) {
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
            return ensureUnAttachedCard(merchantAccount, caledonRequest);
        }
    }
}

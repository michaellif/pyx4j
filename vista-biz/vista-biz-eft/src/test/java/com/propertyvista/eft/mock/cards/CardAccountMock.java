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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.unit.shared.UniqueInteger;

import com.propertyvista.eft.mock.cards.CardTransactionMock.TransactionStatus;
import com.propertyvista.operations.domain.eft.cards.to.CreditCardPaymentInstrument;

class CardAccountMock {

    final String number;

    CreditCardPaymentInstrument ccinfo;

    BigDecimal balance = BigDecimal.ZERO;

    BigDecimal creditLimit = new BigDecimal("-10000.00");

    BigDecimal reserved = BigDecimal.ZERO;

    Map<String, CardTransactionMock> transactions = new HashMap<>();

    CardAccountMock(CreditCardPaymentInstrument ccinfo) {
        this.number = UniqueInteger.getInstance("CardAccountMock").nextAsString();
        this.ccinfo = ccinfo;
    }

    CardTransactionMock getTransaction(String referenceNumber) {
        return transactions.get(referenceNumber);
    }

    boolean sale(String terminalID, BigDecimal amount, String referenceNumber) {
        if (transactions.containsKey(referenceNumber)) {
            throw new Error("Duplicate transaction " + referenceNumber);
        }
        BigDecimal newBalance = balance.subtract(amount);
        if (newBalance.compareTo(creditLimit) == -1) {
            return false;
        } else {
            balance = newBalance;
            CardTransactionMock t = new CardTransactionMock();
            t.referenceNumber = referenceNumber;
            t.terminalID = terminalID;
            t.amount = amount;
            t.status = TransactionStatus.Compleated;
            t.authorizationNumber = UniqueInteger.getInstance("AuthorizationNumberMock").nextAsString();
            transactions.put(referenceNumber, t);
            return true;
        }
    }

    boolean returnTransaction(String terminalID, BigDecimal amount, String referenceNumber) {
        if (transactions.containsKey(referenceNumber)) {
            throw new Error("Duplicate transaction " + referenceNumber);
        }
        BigDecimal newBalance = balance.add(amount);
        balance = newBalance;
        CardTransactionMock t = new CardTransactionMock();
        t.referenceNumber = referenceNumber;
        t.terminalID = terminalID;
        t.amount = amount;
        t.status = TransactionStatus.Compleated;
        t.authorizationNumber = UniqueInteger.getInstance("AuthorizationNumberMock").nextAsString();
        transactions.put(referenceNumber, t);
        return true;
    }

    boolean preAuthorization(BigDecimal amount, String referenceNumber) {
        if (transactions.containsKey(referenceNumber)) {
            throw new Error("Duplicate transaction " + referenceNumber);
        }
        BigDecimal newBalance = balance.subtract(amount).subtract(reserved);
        if (newBalance.compareTo(creditLimit) == -1) {
            return false;
        } else {
            reserved = amount.add(reserved);
            return true;
        }
    }

    boolean completion(String terminalID, BigDecimal amount, String referenceNumber) {
        if (sale(terminalID, amount, referenceNumber)) {
            reserved = reserved.subtract(amount);
            return true;
        } else {
            return false;
        }
    }

    boolean voidTransaction(BigDecimal amount, String referenceNumber) {
        CardTransactionMock t = transactions.get(referenceNumber);
        if ((t == null) || (t.status != TransactionStatus.Compleated)) {
            return false;
        } else {
            BigDecimal newBalance = balance.add(amount);
            balance = newBalance;
            t.status = TransactionStatus.Voided;
            return true;
        }
    }

    @Override
    public String toString() {
        return "CardAccountMock #" + number + " " + ccinfo.cardType().getValue() + "; balance=" + balance + "; transactions=" + transactions.size();
    }
}

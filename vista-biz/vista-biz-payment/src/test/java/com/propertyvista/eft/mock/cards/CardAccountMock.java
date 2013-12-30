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

import com.propertyvista.eft.mock.cards.CardTransactionMock.TransactionStatus;
import com.propertyvista.operations.domain.eft.cards.to.CreditCardPaymentInstrument;

class CardAccountMock {

    CreditCardPaymentInstrument ccinfo;

    BigDecimal balance = BigDecimal.ZERO;

    BigDecimal creditLimit = new BigDecimal("-10000.00");

    Map<String, CardTransactionMock> transactions = new HashMap<String, CardTransactionMock>();

    CardAccountMock(CreditCardPaymentInstrument ccinfo) {
        this.ccinfo = ccinfo;
    }

    boolean sale(BigDecimal amount, String referenceNumber) {
        if (transactions.containsKey(referenceNumber)) {
            throw new Error("Duplicate transaction " + referenceNumber);
        }
        BigDecimal newBalance = balance.subtract(amount);
        if (newBalance.compareTo(creditLimit) == -1) {
            return false;
        } else {
            balance = newBalance;
            CardTransactionMock t = new CardTransactionMock();
            t.amount = amount;
            t.status = TransactionStatus.compleated;
            transactions.put(referenceNumber, t);
            return true;
        }
    }

    boolean voidTransaction(BigDecimal amount, String referenceNumber) {
        CardTransactionMock t = transactions.get(referenceNumber);
        if ((t == null) || (t.status != TransactionStatus.compleated)) {
            return false;
        } else {
            BigDecimal newBalance = balance.add(amount);
            balance = newBalance;
            t.status = TransactionStatus.voided;
            return true;
        }
    }
}

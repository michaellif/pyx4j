/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 9, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.mock.cards;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction.SimulationTransactionType;

class SimulationBridge {

    static CardServiceSimulationTransaction toSimulation(CardAccountMock account, CardTransactionMock transactionMock) {
        CardServiceSimulationTransaction transaction = EntityFactory.create(CardServiceSimulationTransaction.class);
        transaction.card().cardType().setValue(account.ccinfo.cardType().getValue());
        transaction.card().cardNumber().setValue(account.ccinfo.creditCardNumber().getValue());
        transaction.card().expiryDate().setValue(account.ccinfo.creditCardExpiryDate().getValue());
        transaction.merchant().terminalID().setValue(transactionMock.terminalID);
        transaction.amount().setValue(transactionMock.amount);
        transaction.transactionDate().setValue(transactionMock.date);
        transaction.reference().setValue(transactionMock.referenceNumber);
        transaction.authorizationNumber().setValue(transactionMock.authorizationNumber);

        switch (transactionMock.status) {
        case Compleated:
            transaction.transactionType().setValue(SimulationTransactionType.Sale);
            transaction.responseCode().setValue("0000");
            break;
        case PreAuthorization:
            transaction.transactionType().setValue(SimulationTransactionType.PreAuthorization);
            break;
        case Return:
            transaction.transactionType().setValue(SimulationTransactionType.Return);
            break;
        case Voided:
            transaction.transactionType().setValue(SimulationTransactionType.Void);
            break;
        }

        return transaction;
    }

}

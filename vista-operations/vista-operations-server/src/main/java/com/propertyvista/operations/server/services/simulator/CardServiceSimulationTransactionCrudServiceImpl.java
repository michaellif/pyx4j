/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-06
 * @author ArtyomB
 */
package com.propertyvista.operations.server.services.simulator;

import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationCard;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationMerchantAccount;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction.SimulationTransactionType;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationTransactionCrudService;

public class CardServiceSimulationTransactionCrudServiceImpl extends AbstractCrudServiceImpl<CardServiceSimulationTransaction> implements
        CardServiceSimulationTransactionCrudService {

    public CardServiceSimulationTransactionCrudServiceImpl() {
        super(CardServiceSimulationTransaction.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected CardServiceSimulationTransaction init(InitializationData initializationData) {
        CardServiceSimulationTransactionInitializationData initData = (CardServiceSimulationTransactionInitializationData) initializationData;

        CardServiceSimulationTransaction tx = EntityFactory.create(CardServiceSimulationTransaction.class);

        tx.scheduledSimulatedResponce().setValue(Boolean.TRUE);

        if (initData.returnOf().isNull()) {
            tx.card().set(Persistence.service().retrieve(CardServiceSimulationCard.class, initData.card().getPrimaryKey()));
            tx.merchant().set(Persistence.service().retrieve(CardServiceSimulationMerchantAccount.class, initData.merchantAccount().getPrimaryKey()));
        } else {
            CardServiceSimulationTransaction returnOf = Persistence.service().retrieve(CardServiceSimulationTransaction.class,
                    initData.returnOf().getPrimaryKey());

            tx.card().set(returnOf.card());
            tx.merchant().set(returnOf.merchant());

            tx.transactionType().setValue(SimulationTransactionType.Return);
            tx.amount().setValue(returnOf.amount().getValue());
            tx.authorizationNumber().setValue("RETURN  $" + returnOf.amount().getValue());
            tx.responseCode().setValue("0000");
            tx.reference().setValue(returnOf.reference().getValue());
        }
        tx.transactionDate().setValue(SystemDateManager.getDate());
        return tx;
    }
}

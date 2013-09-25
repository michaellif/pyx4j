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
 * @version $Id$
 */
package com.propertyvista.operations.server.services.simulator;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.operations.domain.dev.CardServiceSimulationCard;
import com.propertyvista.operations.domain.dev.CardServiceSimulationTransaction;
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
        tx.card().set(Persistence.service().retrieve(CardServiceSimulationCard.class, initData.card().getPrimaryKey()));

        return tx;
    }
}

/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services.simulator;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.eft.caledoncards.reports.simulator.CardReconciliationSimulationManager;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationReconciliationRecord;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationReconciliationCreateTO;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationReconciliationCrudService;

public class CardServiceSimulationReconciliationCrudServiceImpl extends AbstractCrudServiceImpl<CardServiceSimulationReconciliationRecord> implements
        CardServiceSimulationReconciliationCrudService {

    public CardServiceSimulationReconciliationCrudServiceImpl() {
        super(CardServiceSimulationReconciliationRecord.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    public void createCardsReconciliationReport(AsyncCallback<String> callback, CardServiceSimulationReconciliationCreateTO to) {
        callback.onSuccess(new CardReconciliationSimulationManager().createReports(to.fromDate().getValue(), to.toDate().getValue()));
    }

}

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
package com.propertyvista.operations.client.activity.crud.cardservicesimulation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.ui.crud.cardservicesimulation.CardServiceSimulationTransactionEditorView;
import com.propertyvista.operations.client.viewfactories.crud.SimulationViewFactory;
import com.propertyvista.operations.domain.dev.CardServiceSimulationCard;
import com.propertyvista.operations.domain.dev.CardServiceSimulationTransaction;
import com.propertyvista.operations.rpc.services.sim.CardServiceSimulationCardCrudService;
import com.propertyvista.operations.rpc.services.sim.CardServiceSimulationTransactionCrudService;

public class CardServiceSimulationTransactionEditorActivity extends AbstractEditorActivity<CardServiceSimulationTransaction> {

    public CardServiceSimulationTransactionEditorActivity(CrudAppPlace place) {
        super(place, SimulationViewFactory.instance(CardServiceSimulationTransactionEditorView.class), GWT
                .<CardServiceSimulationTransactionCrudService> create(CardServiceSimulationTransactionCrudService.class),
                CardServiceSimulationTransaction.class);
    }

    @Override
    protected void createNewEntity(final AsyncCallback<CardServiceSimulationTransaction> callback) {
        CardServiceSimulationCardCrudService srv = GWT.create(CardServiceSimulationCardCrudService.class);
        srv.retrieve(new DefaultAsyncCallback<CardServiceSimulationCard>() {
            @Override
            public void onSuccess(CardServiceSimulationCard result) {
                CardServiceSimulationTransaction tx = EntityFactory.create(CardServiceSimulationTransaction.class);
                tx.scheduledSimulatedResponce().setValue(Boolean.TRUE);
                tx.card().set(result);
                callback.onSuccess(tx);

            }
        }, getParentId(), AbstractCrudService.RetrieveTraget.View);
    }
}

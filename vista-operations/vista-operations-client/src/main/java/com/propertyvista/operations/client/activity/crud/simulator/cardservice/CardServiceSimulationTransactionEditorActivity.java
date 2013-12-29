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
package com.propertyvista.operations.client.activity.crud.simulator.cardservice;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationTransactionEditorView;
import com.propertyvista.operations.domain.dev.CardServiceSimulationCard;
import com.propertyvista.operations.domain.dev.CardServiceSimulationTransaction;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationTransactionCrudService;

public class CardServiceSimulationTransactionEditorActivity extends AbstractEditorActivity<CardServiceSimulationTransaction> {

    public CardServiceSimulationTransactionEditorActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().getView(CardServiceSimulationTransactionEditorView.class), GWT
                .<CardServiceSimulationTransactionCrudService> create(CardServiceSimulationTransactionCrudService.class),
                CardServiceSimulationTransaction.class);
    }

    @Override
    protected void obtainInitializationData(AsyncCallback<InitializationData> callback) {
        CardServiceSimulationTransactionCrudService.CardServiceSimulationTransactionInitializationData ind = EntityFactory
                .create(CardServiceSimulationTransactionCrudService.CardServiceSimulationTransactionInitializationData.class);
        ind.card().set(EntityFactory.createIdentityStub(CardServiceSimulationCard.class, getParentId()));
        callback.onSuccess(ind);
    }
}

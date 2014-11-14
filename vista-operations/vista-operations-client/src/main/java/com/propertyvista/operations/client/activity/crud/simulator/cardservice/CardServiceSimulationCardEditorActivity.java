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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationCardEditorView;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationCard;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationMerchantAccount;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationCardCrudService;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationTransactionCrudService.CardServiceSimulationTransactionInitializationData;

public class CardServiceSimulationCardEditorActivity extends AbstractPrimeEditorActivity<CardServiceSimulationCard> implements
        CardServiceSimulationCardEditorView.Presenter {

    public CardServiceSimulationCardEditorActivity(CrudAppPlace place) {
        super(CardServiceSimulationCard.class, place, OperationsSite.getViewFactory().getView(CardServiceSimulationCardEditorView.class), GWT
                .<CardServiceSimulationCardCrudService> create(CardServiceSimulationCardCrudService.class));
    }

    @Override
    public void addTransaction(CardServiceSimulationMerchantAccount merchantAccount) {
        CardServiceSimulationTransactionInitializationData init = EntityFactory.create(CardServiceSimulationTransactionInitializationData.class);
        init.merchantAccount().set(merchantAccount.createIdentityStub());
        init.card().set(getView().getValue().createIdentityStub());
        AppSite.getPlaceController().goTo(new OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationTransaction().formNewItemPlace(init));
    }

}

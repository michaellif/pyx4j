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
 */
package com.propertyvista.operations.client.activity.crud.simulator.cardservice;

import com.google.gwt.core.client.GWT;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulationReconciliationListerView;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationReconciliationRecord;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationReconciliationCreateTO;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationReconciliationCrudService;

public class CardServiceSimulationReconciliationListerActivity extends AbstractPrimeListerActivity<CardServiceSimulationReconciliationRecord> implements
        CardServiceSimulationReconciliationListerView.Presenter {

    public CardServiceSimulationReconciliationListerActivity(AppPlace place) {
        super(CardServiceSimulationReconciliationRecord.class, place, OperationsSite.getViewFactory().getView(
                CardServiceSimulationReconciliationListerView.class));
    }

    @Override
    public void createReconciliations(CardServiceSimulationReconciliationCreateTO to) {
        GWT.<CardServiceSimulationReconciliationCrudService> create(CardServiceSimulationReconciliationCrudService.class).createCardsReconciliationReport(
                new DefaultAsyncCallback<String>() {
                    @Override
                    public void onSuccess(String message) {
                        MessageDialog.info(message);
                        refresh();
                    }
                }, to);
    }

}

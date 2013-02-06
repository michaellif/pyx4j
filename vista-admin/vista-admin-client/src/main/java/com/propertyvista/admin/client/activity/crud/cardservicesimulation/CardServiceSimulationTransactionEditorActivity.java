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
package com.propertyvista.admin.client.activity.crud.cardservicesimulation;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.activity.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.ui.crud.cardservicesimulation.CardServiceSimulationTransactionEditorView;
import com.propertyvista.admin.client.viewfactories.crud.SimulationViewFactory;
import com.propertyvista.admin.domain.dev.CardServiceSimulationTransaction;
import com.propertyvista.admin.rpc.services.sim.CardServiceSimulationTransactionCrudService;

public class CardServiceSimulationTransactionEditorActivity extends EditorActivityBase<CardServiceSimulationTransaction> {

    public CardServiceSimulationTransactionEditorActivity(CrudAppPlace place) {
        super(place, SimulationViewFactory.instance(CardServiceSimulationTransactionEditorView.class), GWT
                .<CardServiceSimulationTransactionCrudService> create(CardServiceSimulationTransactionCrudService.class),
                CardServiceSimulationTransaction.class);
    }

}

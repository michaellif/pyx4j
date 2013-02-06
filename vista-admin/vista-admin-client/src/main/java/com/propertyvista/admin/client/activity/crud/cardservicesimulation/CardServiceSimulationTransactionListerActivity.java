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
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.ListerActivityBase;

import com.propertyvista.admin.client.ui.crud.cardservicesimulation.CardServiceSimulationTransactionListerView;
import com.propertyvista.admin.client.viewfactories.crud.SimulationViewFactory;
import com.propertyvista.admin.domain.dev.CardServiceSimulationTransaction;
import com.propertyvista.admin.rpc.services.sim.CardServiceSimulationTransactionCrudService;

public class CardServiceSimulationTransactionListerActivity extends ListerActivityBase<CardServiceSimulationTransaction> {

    public CardServiceSimulationTransactionListerActivity(Place place) {
        super(place, SimulationViewFactory.instance(CardServiceSimulationTransactionListerView.class), GWT
                .<AbstractCrudService<CardServiceSimulationTransaction>> create(CardServiceSimulationTransactionCrudService.class),
                CardServiceSimulationTransaction.class);
    }
}

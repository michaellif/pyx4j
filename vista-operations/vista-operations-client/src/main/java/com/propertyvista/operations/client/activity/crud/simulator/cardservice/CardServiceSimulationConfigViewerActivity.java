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

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeViewerActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.simulator.cardservice.CardServiceSimulatorConfigViewerView;
import com.propertyvista.operations.rpc.dto.CardServiceSimulatorConfigDTO;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationConfigService;

public class CardServiceSimulationConfigViewerActivity extends AbstractPrimeViewerActivity<CardServiceSimulatorConfigDTO> implements
        CardServiceSimulatorConfigViewerView.IViewerPresenter {

    public CardServiceSimulationConfigViewerActivity(CrudAppPlace place) {
        super(CardServiceSimulatorConfigDTO.class, place, OperationsSite.getViewFactory().getView(CardServiceSimulatorConfigViewerView.class), GWT
                .<CardServiceSimulationConfigService> create(CardServiceSimulationConfigService.class));
    }

}

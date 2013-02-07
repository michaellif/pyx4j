/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.residents.financial;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.dto.LeaseYardiFinancialInfoDTO;
import com.propertyvista.portal.client.ui.residents.financial.yardi.FinancialStatusView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.services.resident.FinancialStatusService;

/**
 * Used to display financial status of Tenants from Yardi integrated accounts.
 */
public class FinancialStatusActivity extends AbstractActivity {

    private final FinancialStatusView view;

    private final FinancialStatusService service;

    public FinancialStatusActivity(Place place) {
        view = PortalViewFactory.instance(FinancialStatusView.class);
        service = GWT.<FinancialStatusService> create(FinancialStatusService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {

        service.getFinancialStatus(new DefaultAsyncCallback<LeaseYardiFinancialInfoDTO>() {

            @Override
            public void onSuccess(LeaseYardiFinancialInfoDTO result) {
                view.populate(result);
                panel.setWidget(view);
            }

        });

    }
}

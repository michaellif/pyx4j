/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.ptapp.client.ui.ApplicationStatusView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;
import com.propertyvista.portal.rpc.ptapp.dto.ApplicationStatusSummaryDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationStatusService;

public class ApplicationStatusActivity extends AbstractActivity implements ApplicationStatusView.Presenter {

    private final ApplicationStatusView view;

    private final ApplicationStatusService service;

    public ApplicationStatusActivity(AppPlace place) {
        this.view = PtAppViewFactory.instance(ApplicationStatusView.class);
        this.service = GWT.create(ApplicationStatusService.class);

        view.setPresenter(this);

        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

        service.retrieveStatus(new DefaultAsyncCallback<ApplicationStatusSummaryDTO>() {
            @Override
            public void onSuccess(ApplicationStatusSummaryDTO result) {
                view.populate(result);
            }
        });

    }

    public Activity withPlace(AppPlace place) {
        return this;
    }
}

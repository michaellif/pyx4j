/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity;

import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.portal.ptapp.client.ui.ApplicationSelectionView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;
import com.propertyvista.portal.rpc.ptapp.dto.OnlineApplicationContextDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationSelectionService;

public class ApplicationSelectionActivity extends AbstractActivity implements ApplicationSelectionView.Presenter {

    private final ApplicationSelectionService service;

    private final ApplicationSelectionView view;

    public ApplicationSelectionActivity() {
        view = PtAppViewFactory.instance(ApplicationSelectionView.class);
        service = GWT.<ApplicationSelectionService> create(ApplicationSelectionService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setPresenter(this);
        view.setApplications(new Vector<OnlineApplicationContextDTO>(1));
        populate();
    }

    @Override
    public void populate() {
        service.getApplications(new DefaultAsyncCallback<Vector<OnlineApplicationContextDTO>>() {

            @Override
            public void onSuccess(Vector<OnlineApplicationContextDTO> applications) {
                view.setApplications(applications);
            }

        });
    }

    @Override
    public void selectApplication(OnlineApplication onlineApplicationStub) {

        service.setApplicationContext(new DefaultAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
                AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
            }

        }, onlineApplicationStub);
    }

}

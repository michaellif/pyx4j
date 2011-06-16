/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.ptapp.client.ui.LoginView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;
import com.propertyvista.portal.rpc.portal.services.AuthenticationService;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class LoginActivity extends AbstractActivity implements LoginView.Presenter {

    private final LoginView view;

    public LoginActivity(Place place) {
        view = (LoginView) PtAppViewFactory.instance(LoginView.class);
        assert (view != null);
        view.setPresenter(this);
        withPlace(place);
    }

    public LoginActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void login(AuthenticationRequest request) {
        AsyncCallback<AuthenticationResponse> callback = new DefaultAsyncCallback<AuthenticationResponse>() {

            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof ChallengeVerificationRequired) {
                    view.challengeVerificationRequired();
                }
                throw new UnrecoverableClientError(caught);
            }

        };

        ((AuthenticationService) GWT.create(AuthenticationService.class)).authenticate(callback, request);

    }

    @Override
    public void gotoRetrievePassword() {
        AppSite.getPlaceController().goTo(new PtSiteMap.RetrievePassword());
    }
}

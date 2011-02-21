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
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.propertyvista.portal.client.ptapp.PtAppWizardManager;
import com.propertyvista.portal.client.ptapp.SiteMap;
import com.propertyvista.portal.client.ptapp.ui.LoginView;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.site.client.place.AppPlace;

public class LoginActivity extends AbstractActivity implements LoginView.Presenter {

    private final LoginView view;

    private final PlaceController placeController;

    private final Provider<PtAppWizardManager> wizardManagerProvider;

    @Inject
    public LoginActivity(LoginView view, PlaceController placeController, Provider<PtAppWizardManager> wizardManagerProvider) {
        this.view = view;
        this.placeController = placeController;
        this.wizardManagerProvider = wizardManagerProvider;
        view.setPresenter(this);
    }

    public LoginActivity withPlace(AppPlace place) {
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
        RPCManager.execute(AuthenticationServices.Authenticate.class, request, callback);

    }

    @Override
    public void gotoRetrievePassword() {
        placeController.goTo(new SiteMap.RetrievePassword());
    }
}

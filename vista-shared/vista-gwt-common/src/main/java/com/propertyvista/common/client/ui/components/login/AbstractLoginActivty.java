/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.login;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

public abstract class AbstractLoginActivty extends AbstractActivity implements LoginView.Presenter {

    private final AuthenticationService authService;

    private final AppPlace passwordResetRequestPlace;

    private final LoginView loginView;

    /**
     * @param authService
     * @param passwordResetRequestPlace
     *            a place to a activity that manages password reset request view.
     */
    protected AbstractLoginActivty(Place place, LoginView loginView, AuthenticationService authService, AppPlace passwordResetRequestPlace) {
        this.authService = authService;
        this.passwordResetRequestPlace = passwordResetRequestPlace;
        this.loginView = loginView;
        this.loginView.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(loginView);
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
                    loginView.enableHumanVerification();
                }
                throw new UnrecoverableClientError(caught);
            }

        };
        authService.authenticate(callback, ClientContext.getClientSystemInfo(), request);
    }

    @Override
    public void gotoResetPassword() {
        AppSite.getPlaceController().goTo(passwordResetRequestPlace);
    }
}

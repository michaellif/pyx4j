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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.webstorage.client.HTML5Storage;
import com.pyx4j.widgets.client.CaptchaComposite;

public abstract class AbstractLoginActivty extends AbstractActivity implements LoginView.Presenter {

    private static String HTML5_KEY = AppSite.instance().getAppId() + ".userid";

    private final AuthenticationService authService;

    private final AppPlace passwordResetRequestPlace;

    private final LoginView loginView;

    private String userId;

    /**
     * @param authService
     * @param passwordResetRequestPlace
     *            a place to a activity that manages password reset request view.
     */
    protected AbstractLoginActivty(Place place, LoginView loginView, AuthenticationService authService, AppPlace passwordResetRequestPlace) {
        this.authService = authService;
        this.passwordResetRequestPlace = passwordResetRequestPlace;
        this.loginView = loginView;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {

        if (HTML5Storage.isSupported()) {
            String userIdFromStorage = HTML5Storage.getLocalStorage().getItem(AbstractLoginActivty.HTML5_KEY);
            if (CommonsStringUtils.isStringSet(userIdFromStorage)) {
                userId = userIdFromStorage;
            }
        }
        loginView.setPresenter(this);
        panel.setWidget(loginView);
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void login(final AuthenticationRequest request) {
        AsyncCallback<Boolean> callback = new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                if (HTML5Storage.isSupported()) {
                    if (request.rememberID().getValue()) {
                        HTML5Storage.getLocalStorage().setItem(AbstractLoginActivty.HTML5_KEY, request.email().getValue());
                    } else {
                        HTML5Storage.getLocalStorage().removeItem(AbstractLoginActivty.HTML5_KEY);
                    }
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof ChallengeVerificationRequired) {
                    enableHumanVerification();
                } else {
                    super.onFailure(caught);
                }
            }

        };
        ClientContext.authenticate(authService, request, callback);
    }

    protected void enableHumanVerification() {
        if (CaptchaComposite.isPublicKeySet()) {
            loginView.enableHumanVerification();
        } else {
            AsyncCallback<String> callback = new DefaultAsyncCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    CaptchaComposite.setPublicKey(result);
                    loginView.enableHumanVerification();
                }

            };
            authService.obtainRecaptchaPublicKey(callback);
        }
    }

    @Override
    public void gotoResetPassword() {
        AppSite.getPlaceController().goTo(passwordResetRequestPlace);
    }
}

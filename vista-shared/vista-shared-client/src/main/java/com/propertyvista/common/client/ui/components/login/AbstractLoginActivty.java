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

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.webstorage.client.HTML5Storage;
import com.pyx4j.widgets.client.CaptchaComposite;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.DemoData.UserType;
import com.propertyvista.shared.config.VistaDemo;

public abstract class AbstractLoginActivty extends AbstractActivity implements LoginView.Presenter {

    public static class DevLoginCredentialsImpl implements LoginView.DevLoginCredentials {

        private final DemoData.UserType user;

        public DevLoginCredentialsImpl(DemoData.UserType user) {
            this.user = user;
        }

        @Override
        public UserType getUserType() {
            return user;
        }
    }

    private static final I18n i18n = I18n.get(AbstractLoginActivty.class);

    private static String HTML5_KEY = AppSite.instance().getAppId() + ".userid";

    private final AppPlace passwordResetRequestPlace;

    private final LoginView loginView;

    private String userId;

    /**
     * @param authService
     * @param passwordResetRequestPlace
     *            a place to a activity that manages password reset request view.
     */
    protected AbstractLoginActivty(Place place, LoginView loginView, AppPlace passwordResetRequestPlace) {
        this.passwordResetRequestPlace = passwordResetRequestPlace;
        this.loginView = loginView;
    }

    public LoginView getLoginView() {
        return loginView;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        loginView.setPresenter(this);
        if (HTML5Storage.isSupported()) {
            HTML5Storage.getSessionStorage().clear();
            String userIdFromStorage = HTML5Storage.getLocalStorage().getItem(AbstractLoginActivty.HTML5_KEY);
            if (CommonsStringUtils.isStringSet(userIdFromStorage)) {
                userId = userIdFromStorage;
            }
        }
        loginView.reset(userId, userId != null);
        if (isDevLoginRequired()) {
            loginView.setDevLogin(getDevLoginCredentials(), getApplicationModeName());
        } else {
            loginView.setDevLogin(null, null);
        }

        loginView.setWallMessage(ClientContext.getSystemWallMessage());

        panel.setWidget(loginView);
    }

    @Override
    public void login(final AuthenticationRequest request) {
        AsyncCallback<Boolean> callback = new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                if (HTML5Storage.isSupported()) {
                    if (request.rememberID().isBooleanTrue()) {
                        HTML5Storage.getLocalStorage().setItem(AbstractLoginActivty.HTML5_KEY, request.email().getValue());
                    } else {
                        HTML5Storage.getLocalStorage().removeItem(AbstractLoginActivty.HTML5_KEY);
                    }
                }
                onSuccessLogin();
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof ChallengeVerificationRequired) {
                    enableHumanVerification();
                } else if (caught instanceof UserRuntimeException) {
                    MessageDialog.error(i18n.tr("Login Failed"), caught);
                } else {
                    super.onFailure(caught);
                }
            }

        };
        ClientContext.authenticate(request, callback);
    }

    protected void onSuccessLogin() {
    };

    protected boolean isDevLoginRequired() {
        return ApplicationMode.isDevelopment() || VistaDemo.isDemo();
    }

    protected String getApplicationModeName() {
        return DemoData.applicationModeName();
    }

    protected List<? extends LoginView.DevLoginCredentials> getDevLoginCredentials() {
        return new LinkedList<LoginView.DevLoginCredentials>();
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
            ClientContext.getAuthenticationService().obtainRecaptchaPublicKey(callback);
        }
    }

    @Override
    public void gotoResetPassword() {
        AppSite.getPlaceController().goTo(passwordResetRequestPlace);
    }
}

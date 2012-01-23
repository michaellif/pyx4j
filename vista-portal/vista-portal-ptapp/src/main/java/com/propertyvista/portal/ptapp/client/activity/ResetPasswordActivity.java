/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.login.NewPasswordForm.ConversationType;
import com.propertyvista.portal.ptapp.client.PtAppSite;
import com.propertyvista.portal.ptapp.client.ui.NewPasswordView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.services.ActivationService;
import com.propertyvista.portal.rpc.ptapp.services.PtAuthenticationService;

public class ResetPasswordActivity extends AbstractActivity implements NewPasswordView.Presenter {

    private static final I18n i18n = I18n.get(ResetPasswordActivity.class);

    private final NewPasswordView view;

    public ResetPasswordActivity(Place place) {
        view = PtAppViewFactory.instance(NewPasswordView.class);
        assert (view != null);
        view.setConversationType(ConversationType.RESET);
        view.setPresenter(this);
        withPlace(place);
    }

    public ResetPasswordActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        String authToken = Window.Location.getParameter(ActivationService.PASSWORD_TOKEN);
        if (CommonsStringUtils.isEmpty(authToken)) {
            PtAppSite.instance().showMessageDialog(i18n.tr("The URL You Tried To Use Is Either Incorrect Or No Longer Valid"), i18n.tr("Error"),
                    i18n.tr("Log In"), new Command() {
                        @Override
                        public void execute() {
                            AppSite.getPlaceController().goTo(new PtSiteMap.Login());
                        }
                    });
        }
        panel.setWidget(view);
        authenticateWithPasswordResetToken(authToken);
    }

    private void authenticateWithPasswordResetToken(String authToken) {
        GWT.<PtAuthenticationService> create(PtAuthenticationService.class).authenticateWithToken(new DefaultAsyncCallback<AuthenticationResponse>() {

            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                PtAppSite.instance().showMessageDialog(

                i18n.tr("Authentication failed, probably the URL you tried is either incorrect or no longer valid"),

                i18n.tr("Error"),

                i18n.tr("Log In"),

                new Command() {
                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(new PtSiteMap.Login());
                    }
                });
                super.onFailure(caught);
            }
        }, ClientContext.getClientSystemInfo(), authToken);
    }

    @Override
    public void passwordReset(PasswordChangeRequest request) {
        AsyncCallback<AuthenticationResponse> callback = new DefaultAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
            }
        };

        GWT.<ActivationService> create(ActivationService.class).passwordReset(callback, request);
    }

    @Override
    public void passwordChange(PasswordChangeRequest request) {
        // never supposed to happen during reset password activity
    }

}

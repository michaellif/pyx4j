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
package com.propertyvista.crm.client.activity.login;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.login.LoginView;
import com.propertyvista.crm.client.ui.viewfactories.LoginViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;

public class LoginActivity extends AbstractActivity implements LoginView.Presenter {

    private final static I18n i18n = I18n.get(LoginActivity.class);

    private final LoginView view;

    public LoginActivity(Place place) {
        this.view = LoginViewFactory.instance(LoginView.class);
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
        AsyncCallback<Boolean> callback = new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof ChallengeVerificationRequired) {
                    view.enableHumanVerification();
                } else {
                    MessageDialog.error(i18n.tr("Login Failed"), caught);
                }
            }

        };
        ClientContext.authenticate((CrmAuthenticationService) GWT.create(CrmAuthenticationService.class), request, callback);
    }

    @Override
    public void gotoResetPassword() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.PasswordResetRequest());
    }
}

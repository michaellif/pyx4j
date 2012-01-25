/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.login;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.login.LoginWithTokenView;
import com.propertyvista.crm.client.ui.viewfactories.LoginViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;

public class LoginWithTokenActivity extends AbstractActivity {

    private static final I18n i18n = I18n.get(LoginWithTokenActivity.class);

    private final LoginWithTokenView view;

    private final String authToken;

    public LoginWithTokenActivity(Place place) {
        assert (place instanceof AppPlace);
        authToken = ((AppPlace) place).getFirstArg(AuthenticationService.AUTH_TOKEN_ARG);
        view = LoginViewFactory.instance(LoginWithTokenView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

        GWT.<AuthenticationService> create(CrmAuthenticationService.class).authenticateWithToken(new DefaultAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
                AppSite.getPlaceController().goTo(new CrmSiteMap.PasswordReset());
            }

            @Override
            public void onFailure(Throwable caught) {
                AppSite.getPlaceController().goTo(new CrmSiteMap.Login());
                MessageDialog.error(i18n.tr("Authentication failed"), i18n.tr("The URL you have used is either incorrect or expired."));
            }
        }, ClientContext.getClientSystemInfo(), authToken);
    }

}

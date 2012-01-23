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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.login.LoginWithTokenView;
import com.propertyvista.crm.client.ui.viewfactories.LoginVeiwFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;

public class LoginWithTokenActivity extends AbstractActivity {

    private static final I18n i18n = I18n.get(LoginWithTokenActivity.class);

    private static final String AUTH_TOKEN_TOKEN = "authToken";

    private final LoginWithTokenView view;

    private final String authToken;

    public LoginWithTokenActivity(AppPlace place) {
        authToken = Window.Location.getParameter(AUTH_TOKEN_TOKEN);
        view = LoginVeiwFactory.instance(LoginWithTokenView.class);
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
                CrmSite.instance().showMessageDialog(

                i18n.tr("Authentication failed, probably the URL you tried is either incorrect or expired"),

                i18n.tr("Error"),

                i18n.tr("Log In"),

                new Command() {
                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(new CrmSiteMap.Login());
                    }
                });
            }
        }, ClientContext.getClientSystemInfo(), authToken);
    }

}

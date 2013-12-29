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
package com.propertyvista.common.client.ui.components.login;

import com.google.gwt.activity.shared.AbstractActivity;
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

public class AbstractLoginWithTokenActivity extends AbstractActivity {

    private static final I18n i18n = I18n.get(AbstractLoginWithTokenActivity.class);

    private final LoginWithTokenView view;

    private final String authToken;

    private final AppPlace loginPlace;

    // TODO clean this up: the redirection to password reset place works via *SiteDispatcher that should forward user to special password related place: passwordResetPlace arg is not required here
    public AbstractLoginWithTokenActivity(Place place, LoginWithTokenView view, AppPlace passwordResetPlace, AppPlace loginPlace) {
        assert (place instanceof AppPlace);
        this.authToken = ((AppPlace) place).getFirstArg(AuthenticationService.AUTH_TOKEN_ARG);
        this.view = view;
        this.loginPlace = loginPlace;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

        ClientContext.getAuthenticationService().authenticateWithToken(new DefaultAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                onLoginFailure(caught);
            }
        }, ClientContext.getClientSystemInfo(), authToken);
    }

    protected void onLoginFailure(Throwable caught) {
        MessageDialog.error(i18n.tr("Authentication failed"), i18n.tr("The URL you have used is either incorrect or expired."));
        AppSite.getPlaceController().goTo(loginPlace);
    }
}

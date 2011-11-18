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
package com.propertyvista.crm.client.activity.login;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.login.NewPasswordForm.ConversationType;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.login.NewPasswordView;
import com.propertyvista.crm.client.ui.viewfactories.LoginVeiwFactory;
import com.propertyvista.crm.rpc.ActivationServices;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class ResetPasswordActivity extends AbstractActivity implements NewPasswordView.Presenter {

    private static I18n i18n = I18n.get(ResetPasswordActivity.class);

    private final NewPasswordView view;

    private String token;

    public ResetPasswordActivity(Place place) {
        this.view = (NewPasswordView) LoginVeiwFactory.instance(NewPasswordView.class);
        view.setConversationType(ConversationType.RESET);
        view.setPresenter(this);
        withPlace(place);
    }

    public ResetPasswordActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        token = Window.Location.getParameter(ActivationServices.PASSWORD_TOKEN);
        if (CommonsStringUtils.isEmpty(token)) {
            CrmSite.instance().showMessageDialog(i18n.tr("The URL You Tried To Use Is Either Incorrect Or No Longer Valid"), i18n.tr("Error"),
                    i18n.tr("Log In"), new Command() {
                        @Override
                        public void execute() {
                            AppSite.getPlaceController().goTo(new CrmSiteMap.Login());
                        }
                    });
        }

        panel.setWidget(view);
    }

    @Override
    public void passwordReset(PasswordChangeRequest request) {
        request.token().setValue(token);
        AsyncCallback<AuthenticationResponse> callback = new DefaultAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
            }
        };

        RPCManager.execute(ActivationServices.PasswordReset.class, request, callback);

    }

    @Override
    public void passwordChange(PasswordChangeRequest request) {
        // TODO later
    }

}

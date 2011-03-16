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
package com.propertyvista.crm.client.activity;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.crm.client.ui.CreateAccountView;
import com.propertyvista.crm.rpc.AccountCreationRequest;
import com.propertyvista.crm.rpc.ActivationServices;
import com.propertyvista.crm.rpc.SiteMap;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

public class CreateAccountActivity extends AbstractActivity implements CreateAccountView.Presenter {

    private static I18n i18n = I18nFactory.getI18n(CreateAccountActivity.class);

    private final CreateAccountView view;

    @Inject
    public CreateAccountActivity(CreateAccountView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public CreateAccountActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void goToSignin() {
        AppSite.getPlaceController().goTo(new SiteMap.Login());
    }

    @Override
    public void createAccount(AccountCreationRequest request) {
        AsyncCallback<AuthenticationResponse> callback = new DefaultAsyncCallback<AuthenticationResponse>() {

            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
            }

        };
        RPCManager.execute(ActivationServices.CreateAccount.class, request, callback);

    }

}

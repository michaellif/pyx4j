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
package com.propertyvista.crm.client.activity.security;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.rpc.PasswordResetService;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.security.PasswordResetView;
import com.propertyvista.crm.client.ui.viewfactories.LoginViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.security.CrmPasswordResetService;

public class PasswordResetActivity extends AbstractActivity implements PasswordResetView.Presenter {

    private static final I18n i18n = I18n.get(PasswordResetActivity.class);

    private final PasswordResetView view;

    public PasswordResetActivity(Place place) {
        this.view = LoginViewFactory.instance(PasswordResetView.class);
        view.setPresenter(this);
        withPlace(place);
    }

    public PasswordResetActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.discard();
        panel.setWidget(view);
    }

    @Override
    public void passwordReset(PasswordChangeRequest request) {
        AsyncCallback<AuthenticationResponse> callback = new DefaultAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                // ClientContext.authenticated(result);
                CrmSite.instance().showMessageDialog(i18n.tr("Your password has been reset successfully!"), i18n.tr("Success!"), i18n.tr("Log In"),
                        new Command() {
                            @Override
                            public void execute() {
                                // FIXME logout before going to  login screen?
                                AppSite.getPlaceController().goTo(new CrmSiteMap.Login());
                            }
                        });
                AppSite.getPlaceController().goTo(new CrmSiteMap.Login());
            }
        };

        GWT.<PasswordResetService> create(CrmPasswordResetService.class).resetPassword(callback, request);

    }

}

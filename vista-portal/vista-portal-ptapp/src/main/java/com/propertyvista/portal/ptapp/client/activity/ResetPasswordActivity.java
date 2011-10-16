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
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.ptapp.client.PtAppSite;
import com.propertyvista.portal.ptapp.client.ui.NewPasswordView;
import com.propertyvista.portal.ptapp.client.ui.NewPasswordView.ConversationType;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;
import com.propertyvista.portal.rpc.ptapp.PasswordChangeRequest;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.services.ActivationService;

public class ResetPasswordActivity extends AbstractActivity implements NewPasswordView.Presenter {

    private static I18n i18n = I18n.get(ResetPasswordActivity.class);

    private final NewPasswordView view;

    private String token;

    public ResetPasswordActivity(Place place) {
        view = (NewPasswordView) PtAppViewFactory.instance(NewPasswordView.class);
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
        token = Window.Location.getParameter(ActivationService.PASSWORD_TOKEN);
        if (CommonsStringUtils.isEmpty(token)) {
            PtAppSite.instance().showMessageDialog(i18n.tr("The URL you tried to use is either incorrect or no longer valid."), i18n.tr("Error"),
                    i18n.tr("Log In"), new Command() {
                        @Override
                        public void execute() {
                            AppSite.getPlaceController().goTo(new PtSiteMap.Login());
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

        ((ActivationService) GWT.create(ActivationService.class)).passwordReset(callback, request);
    }

    @Override
    public void passwordChange(PasswordChangeRequest request) {
        // TODO later
    }

}

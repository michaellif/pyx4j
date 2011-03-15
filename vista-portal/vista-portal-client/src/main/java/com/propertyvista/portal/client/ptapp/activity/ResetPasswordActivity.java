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
package com.propertyvista.portal.client.ptapp.activity;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.PtAppSite;
import com.propertyvista.portal.client.ptapp.ui.NewPasswordView;
import com.propertyvista.portal.client.ptapp.ui.NewPasswordView.ConversationType;
import com.propertyvista.portal.rpc.pt.PasswordChangeRequest;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.services.ActivationServices;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.rpc.AppPlace;

public class ResetPasswordActivity extends AbstractActivity implements NewPasswordView.Presenter {

    private static I18n i18n = I18nFactory.getI18n(ResetPasswordActivity.class);

    private final NewPasswordView view;

    private final PlaceController placeController;

    private String token;

    @Inject
    public ResetPasswordActivity(NewPasswordView view, PlaceController placeController) {
        this.view = view;
        view.setConversationType(ConversationType.RESET);
        this.placeController = placeController;
        view.setPresenter(this);
    }

    public ResetPasswordActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        token = Window.Location.getParameter(ActivationServices.PASSWORD_TOKEN);
        if (CommonsStringUtils.isEmpty(token)) {
            PtAppSite.instance().showMessageDialog(i18n.tr("The URL you tried to use is either incorrect or no longer valid."), i18n.tr("Error"),
                    i18n.tr("LogIn"), new Command() {
                        @Override
                        public void execute() {
                            placeController.goTo(new SiteMap.Login());
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

        ((ActivationServices) GWT.create(ActivationServices.class)).passwordReset(callback, request);
    }

    @Override
    public void passwordChange(PasswordChangeRequest request) {
        // TODO later
    }

}

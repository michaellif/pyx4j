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
package com.propertyvista.portal.client.activity;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.client.ui.residents.CreateAccountView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.ptapp.AccountCreationRequest;

@Deprecated
public class CreateAccountActivity extends AbstractActivity implements CreateAccountView.Presenter {

    private static I18n i18n = I18nFactory.getI18n(CreateAccountActivity.class);

    private final CreateAccountView view;

    public CreateAccountActivity(Place place) {
        view = (CreateAccountView) PortalViewFactory.instance(CreateAccountView.class);
        assert (view != null);
        view.setPresenter(this);
        withPlace(place);
    }

    public CreateAccountActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void goToLogin() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Login());
    }

    @Override
    public void createAccount(AccountCreationRequest request) {
        AsyncCallback<AuthenticationResponse> callback = new DefaultAsyncCallback<AuthenticationResponse>() {

            @Override
            public void onSuccess(AuthenticationResponse result) {
                ClientContext.authenticated(result);
            }

        };
        //((ActivationService) GWT.create(ActivationService.class)).createAccount(callback, request);
    }
}

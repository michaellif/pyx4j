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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.login.PasswordResetRequestView;
import com.propertyvista.crm.client.ui.viewfactories.LoginViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;

public class PasswordResetRequestActivity extends AbstractActivity implements PasswordResetRequestView.Presenter {

    private final PasswordResetRequestView view;

    public PasswordResetRequestActivity(Place place) {
        this.view = LoginViewFactory.instance(PasswordResetRequestView.class);
        view.setPresenter(this);
        withPlace(place);
    }

    public PasswordResetRequestActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.getForm().populateNew();
    }

    @Override
    public void requestPasswordReset(PasswordRetrievalRequest request) {
        AsyncCallback<VoidSerializable> callback = new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                AppPlace happyPlace = new CrmSiteMap.PasswordResetRequestResult();
                happyPlace.arg(CrmSiteMap.PasswordResetRequestResult.RESULT_TYPE_ARG, CrmSiteMap.PasswordResetRequestResult.SUCCESS);
                AppSite.getPlaceController().goTo(happyPlace);
            }

            @Override
            public void onFailure(Throwable caught) {
                AppPlace sadPlace = new CrmSiteMap.PasswordResetRequestResult();
                sadPlace.arg(CrmSiteMap.PasswordResetRequestResult.RESULT_TYPE_ARG, CrmSiteMap.PasswordResetRequestResult.FAILURE);
                AppSite.getPlaceController().goTo(sadPlace);
            }

        };
        AuthenticationService authService = GWT.<AuthenticationService> create(CrmAuthenticationService.class);
        authService.requestPasswordReset(callback, request);
    }
}

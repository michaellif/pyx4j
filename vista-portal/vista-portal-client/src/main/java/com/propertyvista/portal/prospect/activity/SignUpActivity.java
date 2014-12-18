/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 */
package com.propertyvista.portal.prospect.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationRequest;

import com.propertyvista.portal.prospect.ui.signup.SignUpView;
import com.propertyvista.portal.prospect.ui.signup.SignUpView.SignUpPresenter;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.dto.ProspectSignUpDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ProspectSignUpService;
import com.propertyvista.portal.shared.PortalSite;

public class SignUpActivity extends AbstractActivity implements SignUpPresenter {

    private final SignUpView view;

    public SignUpActivity(Place place) {
        this.view = PortalSite.getViewFactory().getView(SignUpView.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(SignUpActivity.this);
        panel.setWidget(view);
    }

    @Override
    public void signUp(final ProspectSignUpDTO value) {
        value.ilsBuildingId().setValue(Window.Location.getParameter(ProspectPortalSiteMap.ARG_ILS_BUILDING_ID));
        value.ilsFloorplanId().setValue(Window.Location.getParameter(ProspectPortalSiteMap.ARG_ILS_FLOORPLAN_ID));
        value.ilsUnitId().setValue(Window.Location.getParameter(ProspectPortalSiteMap.ARG_ILS_UNIT_ID));

        GWT.<ProspectSignUpService> create(ProspectSignUpService.class).signUp(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                authenticate(value);
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    view.showError(((UserRuntimeException) caught).getMessage());
                } else {
                    super.onFailure(caught);
                }
            }
        }, value);
    }

    private void authenticate(AuthenticationRequest request) {
        ClientContext.authenticate(request, new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                // ClientContext is redirecting to new place
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    view.showError(((UserRuntimeException) caught).getMessage());
                } else {
                    super.onFailure(caught);
                }
            }
        });
    }

}

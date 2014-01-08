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
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.site.client.NavigationUri;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.signup.SignUpView;
import com.propertyvista.portal.resident.ui.signup.SignUpView.SignUpPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.ResidentSelfRegistrationDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.resident.services.ResidentSelfRegistrationService;
import com.propertyvista.portal.rpc.shared.EntityValidationException;

public class SignUpActivity extends AbstractActivity implements SignUpPresenter {

    private final SignUpView view;

    public SignUpActivity(Place place) {
        this.view = ResidentPortalSite.getViewFactory().getView(SignUpView.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {

        GWT.<ResidentSelfRegistrationService> create(ResidentSelfRegistrationService.class).obtainBuildings(
                new DefaultAsyncCallback<EntitySearchResult<SelfRegistrationBuildingDTO>>() {

                    @Override
                    public void onSuccess(EntitySearchResult<SelfRegistrationBuildingDTO> result) {
                        view.setPresenter(SignUpActivity.this);
                        view.init(result.getData());
                        panel.setWidget(view);
                    }
                });

    }

    @Override
    public void register(final ResidentSelfRegistrationDTO value) {
        GWT.<ResidentSelfRegistrationService> create(ResidentSelfRegistrationService.class).selfRegistration(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                authenticate(value);
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof EntityValidationException) {
                    view.showValidationError((EntityValidationException) caught);
                } else if (caught instanceof UserRuntimeException) {
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

    @Override
    public void showVistaTerms() {
        Window.open(AppPlaceInfo.absoluteUrl(NavigationUri.getHostPageURL(), false, getPortalTermsPlace()), "_blank", null);
    }

    @Override
    public final Class<? extends Place> getPortalTermsPlace() {
        return ResidentPortalSiteMap.ResidentPortalTerms.ResidentTermsAndConditions.class;
    }
}

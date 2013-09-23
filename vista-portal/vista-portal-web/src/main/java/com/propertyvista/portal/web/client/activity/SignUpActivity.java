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
package com.propertyvista.portal.web.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.client.NavigationUri;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.web.dto.SelfRegistrationDTO;
import com.propertyvista.portal.rpc.portal.web.services.PortalAuthenticationService;
import com.propertyvista.portal.rpc.portal.web.services.SelfRegistrationBuildingsSourceService;
import com.propertyvista.portal.rpc.shared.EntityValidationException;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.ui.signup.SignUpView;

public class SignUpActivity extends AbstractActivity implements SignUpView.SignUpPresenter {

    private static final I18n i18n = I18n.get(I18n.class);

    private final SignUpView view;

    public SignUpActivity(Place place) {
        this.view = PortalWebSite.getViewFactory().instantiate(SignUpView.class);
        withPlace(place);

    }

    public SignUpActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {

        GWT.<SelfRegistrationBuildingsSourceService> create(SelfRegistrationBuildingsSourceService.class).obtainBuildings(
                new DefaultAsyncCallback<EntitySearchResult<SelfRegistrationBuildingDTO>>() {

                    @Override
                    public void onSuccess(EntitySearchResult<SelfRegistrationBuildingDTO> result) {
                        view.setPresenter(SignUpActivity.this);
                        view.setBuildingOptions(result.getData());
                        panel.setWidget(view);
                    }
                });

    }

    @Override
    public void register(final SelfRegistrationDTO value) {
        GWT.<PortalAuthenticationService> create(PortalAuthenticationService.class).selfRegistration(new DefaultAsyncCallback<VoidSerializable>() {
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
        ClientContext.authenticate(GWT.<AuthenticationService> create(PortalAuthenticationService.class), request, new DefaultAsyncCallback<Boolean>() {

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
        return PortalSiteMap.PortalTermsAndConditions.class;
    }
}

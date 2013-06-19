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
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.client.NavigationUri;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.services.PortalAuthenticationService;
import com.propertyvista.portal.rpc.portal.services.resident.SelfRegistrationBuildingsSourceService;
import com.propertyvista.portal.rpc.shared.EntityValidationException;
import com.propertyvista.portal.web.client.ui.residents.registration.TenantRegistrationView;
import com.propertyvista.portal.web.client.ui.viewfactories.ResidentsViewFactory;

public class TenantRegistrationActivity extends AbstractActivity implements TenantRegistrationView.Presenter {

    private static final I18n i18n = I18n.get(I18n.class);

    private final TenantRegistrationView view;

    public TenantRegistrationActivity(Place place) {
        this.view = ResidentsViewFactory.instance(TenantRegistrationView.class);
        withPlace(place);

    }

    public TenantRegistrationActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {

        GWT.<SelfRegistrationBuildingsSourceService> create(SelfRegistrationBuildingsSourceService.class).obtainBuildings(
                new DefaultAsyncCallback<EntitySearchResult<SelfRegistrationBuildingDTO>>() {

                    @Override
                    public void onSuccess(EntitySearchResult<SelfRegistrationBuildingDTO> result) {
                        view.setPresenter(TenantRegistrationActivity.this);
                        view.populate(result.getData());
                        panel.setWidget(view);
                    }
                });

    }

    @Override
    public void onRegister() {
        ClientContext.authenticate(GWT.<AuthenticationService> create(PortalAuthenticationService.class), view.getValue(), new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {

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
        });
    }

    @Override
    public void onShowVistaTerms() {
        Window.open(AppPlaceInfo.absoluteUrl(NavigationUri.getHostPageURL(), false, getPortalTermsPlace()), "_blank", null);
    }

    @Override
    public final Class<? extends Place> getPortalTermsPlace() {
        return PortalSiteMap.PortalTermsAndConditions.class;
    }
}

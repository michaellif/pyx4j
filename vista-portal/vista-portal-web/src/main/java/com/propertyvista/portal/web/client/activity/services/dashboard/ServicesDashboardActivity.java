/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.services.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.ServicesDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.services.DashboardService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.services.dashboard.ServicesDashboardView;
import com.propertyvista.portal.web.client.ui.services.dashboard.ServicesDashboardView.ServicesDashboardPresenter;

public class ServicesDashboardActivity extends SecurityAwareActivity implements ServicesDashboardPresenter {

    private final ServicesDashboardView view;

    private final DashboardService srv;

    public ServicesDashboardActivity(Place place) {
        this.view = PortalWebSite.getViewFactory().instantiate(ServicesDashboardView.class);
        this.view.setPresenter(this);
        srv = GWT.create(DashboardService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        view.setPresenter(this);

        srv.retrieveServicesDashboard(new DefaultAsyncCallback<ServicesDashboardDTO>() {
            @Override
            public void onSuccess(ServicesDashboardDTO result) {
                view.populate(result);
            }
        });
    }

    @Override
    public void getTenantSure() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePurchase());
    }

    @Override
    public void addThirdPartyTenantInsuranceCertificate() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ResidentServices.TenantInsurance.Other.UploadCertificate());
    }

    @Override
    public void updateThirdPartyTenantInsuranceCeritificate() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ResidentServices.TenantInsurance.Other.UploadCertificate());
    }

}

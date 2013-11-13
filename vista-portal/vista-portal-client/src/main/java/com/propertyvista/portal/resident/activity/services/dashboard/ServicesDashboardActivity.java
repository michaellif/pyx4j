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
package com.propertyvista.portal.resident.activity.services.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.services.dashboard.ServicesDashboardView;
import com.propertyvista.portal.resident.ui.services.dashboard.ServicesDashboardView.ServicesDashboardPresenter;
import com.propertyvista.portal.rpc.portal.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.resident.services.services.InsuranceService;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class ServicesDashboardActivity extends SecurityAwareActivity implements ServicesDashboardPresenter {

    private final ServicesDashboardView view;

    public ServicesDashboardActivity(Place place) {
        this.view = ResidentPortalSite.getViewFactory().getView(ServicesDashboardView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        view.setPresenter(this);

        populate();

    }

    private void populate() {
        ((InsuranceService) GWT.create(InsuranceService.class)).retreiveInsuranceStatus(new DefaultAsyncCallback<InsuranceStatusDTO>() {
            @Override
            public void onSuccess(InsuranceStatusDTO result) {
                view.populateInsuranceGadget(result);
            }
        });
    }

    @Override
    public void buyTenantSure() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSureWizard());
    }

    @Override
    public void addThirdPartyTenantInsuranceCertificate() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.ResidentServices.TenantInsurance.GeneralPolicyWizard());
    }

}

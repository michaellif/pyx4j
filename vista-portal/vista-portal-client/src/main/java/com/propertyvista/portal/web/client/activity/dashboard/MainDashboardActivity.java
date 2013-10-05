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
package com.propertyvista.portal.web.client.activity.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.ResidentSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.BillingService;
import com.propertyvista.portal.rpc.portal.web.services.maintenance.MaintenanceRequestCrudService;
import com.propertyvista.portal.rpc.portal.web.services.profile.ResidentSummaryService;
import com.propertyvista.portal.rpc.portal.web.services.services.InsuranceService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.dashboard.MainDashboardView;

public class MainDashboardActivity extends SecurityAwareActivity implements MainDashboardView.DashboardPresenter {

    private final MainDashboardView view;

    public MainDashboardActivity(Place place) {
        this.view = PortalWebSite.getViewFactory().instantiate(MainDashboardView.class);
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
        ((ResidentSummaryService) GWT.create(ResidentSummaryService.class)).retreiveProfileSummary(new DefaultAsyncCallback<ResidentSummaryDTO>() {
            @Override
            public void onSuccess(ResidentSummaryDTO result) {
                view.populateProfileGadget(result);
            }
        });
        ((BillingService) GWT.create(BillingService.class)).retreiveBillingSummary(new DefaultAsyncCallback<BillingSummaryDTO>() {
            @Override
            public void onSuccess(BillingSummaryDTO result) {
                view.populateBillingGadget(result);
            }
        });
        ((InsuranceService) GWT.create(InsuranceService.class)).retreiveInsuranceStatus(new DefaultAsyncCallback<InsuranceStatusDTO>() {
            @Override
            public void onSuccess(InsuranceStatusDTO result) {
                view.populateInsuranceGadget(result);
            }
        });
        ((MaintenanceRequestCrudService) GWT.create(MaintenanceRequestCrudService.class))
                .retreiveMaintenanceSummary(new DefaultAsyncCallback<MaintenanceSummaryDTO>() {
                    @Override
                    public void onSuccess(MaintenanceSummaryDTO result) {
                        view.populateMaintenanceGadget(result);
                    }
                });
    }

    @Override
    public void payNow() {
        if (SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values())) {
            AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.Payment.PayNow());
        }
    }

    @Override
    public void setAutopay() {
        if (SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values())) {
            AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.PreauthorizedPayments.NewPreauthorizedPayment());
        }
    }

    @Override
    public void buyTenantSure() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.TenantSureWizard());
    }

    @Override
    public void addThirdPartyTenantInsuranceCertificate() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ResidentServices.TenantInsurance.GeneralPolicyWizard());
    }

    @Override
    public void createMaintenanceRequest() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Maintenance.MaintenanceRequestWizard());
    }
}

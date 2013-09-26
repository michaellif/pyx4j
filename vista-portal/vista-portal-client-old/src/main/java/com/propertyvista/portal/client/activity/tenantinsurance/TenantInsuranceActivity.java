/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.tenantinsurance;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.views.TenantInsuranceCoveredByOtherTenantView;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.TenantInsuranceService;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.GeneralInsuranceCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.TenantSureCertificateSummaryDTO;

// TODO maybe this dispatching should be done on navig activity level: i.e. put the place that corresponds to current status in the navig bar 
public class TenantInsuranceActivity extends AbstractActivity {

    private final TenantInsuranceService service;

    public TenantInsuranceActivity() {
        service = GWT.<TenantInsuranceService> create(TenantInsuranceService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        service.getTenantInsuranceStatus(new DefaultAsyncCallback<InsuranceStatusDTO>() {

            @Override
            public void onSuccess(InsuranceStatusDTO status) {
                if (status.certificates().size() == 0) {
                    AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ResidentServices.TenantInsurance.ProvideTenantInsurance());
                } else {
                    if ((status.certificates().get(0)).isOwner().isBooleanTrue()) {
                        if (status.certificates().get(0).isInstanceOf(TenantSureCertificateSummaryDTO.class)) {
                            AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage());
                        } else if (status.certificates().get(0).isInstanceOf(GeneralInsuranceCertificateSummaryDTO.class)) {
                            AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ResidentServices.TenantInsurance.ProvideTenantInsurance());
                        } else {
                            throw new Error("got unknown insurance status");
                        }
                    } else {
                        TenantInsuranceCoveredByOtherTenantView view = PortalSite.getViewFactory().instantiate(TenantInsuranceCoveredByOtherTenantView.class);
                        view.populate(status);
                        panel.setWidget(view);
                    }
                }
            }
        });
    }
}

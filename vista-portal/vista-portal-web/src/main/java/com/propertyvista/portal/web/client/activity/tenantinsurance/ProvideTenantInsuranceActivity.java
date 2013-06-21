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
package com.propertyvista.portal.web.client.activity.tenantinsurance;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.TenantInsuranceService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureTenantInsuranceStatusShortDTO;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.views.ProvideTenantInsuranceView;
import com.propertyvista.portal.web.client.ui.viewfactories.PortalWebViewFactory;
import com.propertyvista.shared.config.VistaFeatures;

public class ProvideTenantInsuranceActivity extends AbstractActivity implements ProvideTenantInsuranceView.Presenter {

    private final ProvideTenantInsuranceView view;

    private final TenantInsuranceService service;

    public ProvideTenantInsuranceActivity() {
        view = PortalWebViewFactory.instance(ProvideTenantInsuranceView.class);
        service = GWT.<TenantInsuranceService> create(TenantInsuranceService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        service.getTenantInsuranceStatus(new DefaultAsyncCallback<TenantInsuranceStatusDTO>() {
            @Override
            public void onSuccess(TenantInsuranceStatusDTO status) {
                if (!status.isInstanceOf(TenantSureTenantInsuranceStatusShortDTO.class)) {
                    view.setPresenter(ProvideTenantInsuranceActivity.this);
                    view.setTenantSureInvitationEnabled(VistaFeatures.instance().tenantSure());
                    view.populate(status);
                    panel.setWidget(view);
                } else {
                    AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.TenantInsurance());
                }
            }
        });
    }

    @Override
    public void onPurchaseTenantSure() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.TenantInsurance.TenantSure.TenantSurePurchase());
    }

    @Override
    public void onUpdateInsuranceByOtherProvider() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.TenantInsurance.Other.UploadCertificate());
    }

}

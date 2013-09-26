/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.tenantinsurance.otherprovider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.tenant.insurance.InsuranceGeneric;
import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.otherprovider.views.TenantInsuranceByOtherProviderUpdateView;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.TenantInsuranceByOtherProviderManagementService;
import com.propertyvista.portal.rpc.portal.services.resident.TenantInsuranceService;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.OtherProviderInsuranceRequirementsDTO;

public class TenantInsuranceByOtherProvdierUpdateActivity extends SecurityAwareActivity implements TenantInsuranceByOtherProviderUpdateView.Presenter {

    private static final I18n i18n = I18n.get(TenantInsuranceByOtherProvdierUpdateActivity.class);

    private final TenantInsuranceByOtherProviderUpdateView view;

    private final TenantInsuranceService insuranceService;

    private final TenantInsuranceByOtherProviderManagementService insuranceByOtherProviderService;

    public TenantInsuranceByOtherProvdierUpdateActivity(Place place) {
        view = PortalSite.getViewFactory().instantiate(TenantInsuranceByOtherProviderUpdateView.class);
        insuranceByOtherProviderService = GWT.<TenantInsuranceByOtherProviderManagementService> create(TenantInsuranceByOtherProviderManagementService.class);
        insuranceService = GWT.<TenantInsuranceService> create(TenantInsuranceService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        insuranceService.getTenantInsuranceRequirements(new DefaultAsyncCallback<OtherProviderInsuranceRequirementsDTO>() {
            @Override
            public void onSuccess(final OtherProviderInsuranceRequirementsDTO requirements) {
                insuranceByOtherProviderService.get(new DefaultAsyncCallback<InsuranceGeneric>() {
                    @Override
                    public void onSuccess(InsuranceGeneric result) {
                        view.setPresenter(TenantInsuranceByOtherProvdierUpdateActivity.this);
                        view.setMinRequiredLiability(requirements.minLiability().getValue());
                        view.populate(result);
                        panel.setWidget(view);
                    }
                });
            }
        });

    }

    @Override
    public void save(InsuranceGeneric certificate) {
        insuranceByOtherProviderService.save(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                PortalSite.instance().showInfoDialog(i18n.tr("Your insurance details were saved successfully"), "");
                PortalSite.getPlaceController().goTo(new PortalSiteMap.NotificationPlace());
            }

        }, certificate);
    }

    @Override
    public void cancel() {
        History.back();
    }

}

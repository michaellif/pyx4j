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
package com.propertyvista.portal.client.activity.tenantinsurance.tenantsure;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureManagementView;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSureManagementService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceStatusDetailedDTO;

public class TenantSureManagementActivity extends AbstractActivity implements TenantSureManagementView.Presenter {

    private final TenantSureManagementView view;

    private final TenantSureManagementService service;

    public TenantSureManagementActivity() {
        view = PortalSite.getViewFactory().instantiate(TenantSureManagementView.class);
        service = GWT.<TenantSureManagementService> create(TenantSureManagementService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
        populateStatus();
    }

    @Override
    public void updateCreditCardDetails() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.Management.UpdateCreditCard());
    }

    @Override
    public void cancelTenantSure() {
        service.cancelTenantSure(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                TenantSureManagementActivity.this.populateStatus();
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    view.reportCancelFailure(((UserRuntimeException) caught).getMessage());
                } else {
                    super.onFailure(caught);
                }
            }
        });
    }

    @Override
    public void sendCertificate(String email) {
        service.sendCertificate(new DefaultAsyncCallback<String>() {

            @Override
            public void onSuccess(String resultEmailAddress) {
                view.reportSendCertificateSuccess(resultEmailAddress);
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    view.reportError(((UserRuntimeException) caught).getMessage());
                } else {
                    super.onFailure(caught);
                }
            }

        }, email);
    }

    @Override
    public void reinstate() {
        service.reinstate(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populateStatus();
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    view.reportError(((UserRuntimeException) caught).getMessage());
                } else {
                    super.onFailure(caught);
                }
            }
        });

    }

    @Override
    public void viewFaq() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.Faq());
    }

    @Override
    public void viewAboutTenantSure() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.About());
    }

    private void populateStatus() {
        service.getStatus(new DefaultAsyncCallback<TenantSureTenantInsuranceStatusDetailedDTO>() {
            @Override
            public void onSuccess(TenantSureTenantInsuranceStatusDetailedDTO status) {
                view.populate(status);
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    view.reportError(((UserRuntimeException) caught).getMessage());
                } else {
                    super.onFailure(caught);
                }
            }
        });
    }

}

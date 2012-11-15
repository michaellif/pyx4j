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
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.TenantInsuranceService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.NoInsuranceTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.OtherProviderTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureTenantInsuranceStatusShortDTO;

public class TenantInsuranceActivity extends AbstractActivity {

    private final TenantInsuranceService service;

    public TenantInsuranceActivity() {
        service = GWT.<TenantInsuranceService> create(TenantInsuranceService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        service.getTenantInsuranceStatus(new DefaultAsyncCallback<TenantInsuranceStatusDTO>() {

            @Override
            public void onSuccess(TenantInsuranceStatusDTO status) {
                Place dispatchTo = null;
                if (status instanceof NoInsuranceTenantInsuranceStatusDTO) {
                    dispatchTo = new PortalSiteMap.Residents.TenantInsurance.ProvideTenantInsurance();
                } else if (status instanceof TenantSureTenantInsuranceStatusShortDTO) {
                    dispatchTo = new PortalSiteMap.Residents.TenantInsurance.TenantSureManagement();
                } else if (status instanceof OtherProviderTenantInsuranceStatusDTO) {
                    dispatchTo = new PortalSiteMap.Residents.TenantInsurance.UploadProofOfInsurance();
                } else {
                    throw new Error("got unknown insurance status");
                }
                AppSite.getPlaceController().goTo(dispatchTo);
            }

        });
    }
}

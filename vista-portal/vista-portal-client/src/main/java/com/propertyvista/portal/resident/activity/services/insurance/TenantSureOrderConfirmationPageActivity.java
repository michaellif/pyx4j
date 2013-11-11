/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity.services.insurance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.services.insurance.TenantSureOrderConfirmationPageView;
import com.propertyvista.portal.resident.ui.services.insurance.TenantSureOrderConfirmationPageView.TenantSureOrderConfirmationPagePresenter;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.TenantSureInsurancePolicyCrudService;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class TenantSureOrderConfirmationPageActivity extends SecurityAwareActivity implements TenantSureOrderConfirmationPagePresenter {

    private final TenantSureOrderConfirmationPageView view;

    private final Key entityId;

    private final TenantSureInsurancePolicyCrudService service;

    public TenantSureOrderConfirmationPageActivity(AppPlace place) {
        view = ResidentPortalSite.getViewFactory().getView(TenantSureOrderConfirmationPageView.class);
        view.setPresenter(this);

        service = GWT.create(TenantSureInsurancePolicyCrudService.class);

        entityId = place.getItemId();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        service.retrieve(new DefaultAsyncCallback<TenantSureInsurancePolicyDTO>() {
            @Override
            public void onSuccess(TenantSureInsurancePolicyDTO result) {
                view.populate(result);
            }
        }, entityId, RetrieveTarget.View);
    }

    @Override
    public void back() {
        // TODO VISTA-3597 What to would back action do in this activity?
    }

}

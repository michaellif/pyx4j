/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 30, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.financial.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;
import com.propertyvista.portal.rpc.portal.web.dto.financial.BillViewDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.BillingService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.financial.dashboard.views.BillView;

public class BillViewActivity extends SecurityAwareActivity implements BillView.Presenter {

    private final BillView view;

    private final Key entityId;

    public BillViewActivity(AppPlace place) {
        this.view = PortalWebSite.getViewFactory().instantiate(BillView.class);
        this.view.setPresenter(this);

        this.entityId = place.getItemId();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        GWT.<BillingService> create(BillingService.class).retreiveBill(new DefaultAsyncCallback<BillViewDTO>() {
            @Override
            public void onSuccess(BillViewDTO result) {
                view.populate(result);
            }
        }, (entityId != null ? EntityFactory.createIdentityStub(Bill.class, entityId) : null));
    }

    @Override
    public void payBill() {
        AppSite.getPlaceController().goTo(new Financial.Payment.PayNow());
    }
}

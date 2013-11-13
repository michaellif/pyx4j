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
package com.propertyvista.portal.resident.activity.financial.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.financial.views.BillingHistoryView;
import com.propertyvista.portal.rpc.portal.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.BillingHistoryDTO;
import com.propertyvista.portal.rpc.portal.resident.services.financial.BillingService;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class BillingHistoryViewActivity extends SecurityAwareActivity implements BillingHistoryView.Presenter {

    private final BillingHistoryView view;

    public BillingHistoryViewActivity(AppPlace place) {
        this.view = ResidentPortalSite.getViewFactory().getView(BillingHistoryView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        GWT.<BillingService> create(BillingService.class).retreiveBillingHistory(new DefaultAsyncCallback<BillingHistoryDTO>() {
            @Override
            public void onSuccess(BillingHistoryDTO result) {
                view.populate(result);
            }
        });
    }

    @Override
    public void viewBill(Bill itemId) {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial.BillingHistory.BillView().formPlace(itemId.getPrimaryKey()));
    }
}

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.residents.billing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.domain.dto.financial.FinancialSummaryDTO;
import com.propertyvista.portal.domain.dto.financial.PvBillingFinancialSummaryDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;
import com.propertyvista.portal.rpc.portal.services.resident.BillSummaryService;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.residents.billing.BillSummaryView;
import com.propertyvista.portal.web.client.ui.viewfactories.PortalWebViewFactory;

public class BillSummaryActivity extends SecurityAwareActivity implements BillSummaryView.Presenter {

    private final BillSummaryView view;

    private final BillSummaryService srv;

    public BillSummaryActivity(Place place) {
        this.view = PortalWebViewFactory.instance(BillSummaryView.class);
        this.view.setPresenter(this);
        srv = GWT.create(BillSummaryService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.retrieve(new DefaultAsyncCallback<FinancialSummaryDTO>() {
            @Override
            public void onSuccess(FinancialSummaryDTO result) {
                // TODO merge all financial summary related views activies to the same class
                view.populate(result.duplicate(PvBillingFinancialSummaryDTO.class));
            }
        });
    }

    @Override
    public void viewCurrentBill() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.BillingHistory.ViewBill());
    }

    @Override
    public void payNow() {
        AppSite.getPlaceController().goTo(new Financial.PayNow());
    }

    @Override
    public void edit(Key id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void back() {
        // TODO Auto-generated method stub

    }
}

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

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.BillingService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.financial.views.TransactionHistoryView;

public class TransactionHistoryViewActivity extends SecurityAwareActivity implements TransactionHistoryView.Presenter {

    private final TransactionHistoryView view;

    public TransactionHistoryViewActivity(AppPlace place) {
        this.view = PortalWebSite.getViewFactory().instantiate(TransactionHistoryView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        GWT.<BillingService> create(BillingService.class).retreiveTransactionHistory(new DefaultAsyncCallback<TransactionHistoryDTO>() {
            @Override
            public void onSuccess(TransactionHistoryDTO result) {
                view.populate(result);
            }
        });
    }
}

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
package com.propertyvista.portal.client.activity.residents.billing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.billing.BillSummaryView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.dto.BillSummaryDTO;
import com.propertyvista.portal.rpc.portal.services.resident.BillSummaryService;

public class BillSummaryActivity extends SecurityAwareActivity implements BillSummaryView.Presenter {

    private final BillSummaryView view;

    private final BillSummaryService srv;

    public BillSummaryActivity(Place place) {
        this.view = PortalViewFactory.instance(BillSummaryView.class);
        this.view.setPresenter(this);
        srv = GWT.create(BillSummaryService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.retrieve(new DefaultAsyncCallback<BillSummaryDTO>() {
            @Override
            public void onSuccess(BillSummaryDTO result) {
                view.populate(result);
            }
        });
    }

    @Override
    public void payBill() {
        // TODO Implement
    }

    @Override
    public void viewCurrentBill() {
        // TODO Auto-generated method stub

    }
}

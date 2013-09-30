/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 19, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.residents.billing;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.billing.BillingHistoryView;
import com.propertyvista.portal.domain.dto.BillDataDTO;
import com.propertyvista.portal.domain.dto.BillListDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.BillingHistoryService;

public class BillingHistoryActivity extends SecurityAwareActivity implements BillingHistoryView.Presenter {

    private final BillingHistoryView view;

    private final BillingHistoryService srv;

    public BillingHistoryActivity(Place place) {
        this.view = PortalSite.getViewFactory().instantiate(BillingHistoryView.class);
        this.view.setPresenter(this);
        srv = GWT.create(BillingHistoryService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.listBills(new DefaultAsyncCallback<Vector<BillDataDTO>>() {
            @Override
            public void onSuccess(Vector<BillDataDTO> result) {
                BillListDTO billHistory = EntityFactory.create(BillListDTO.class);
                billHistory.bills().addAll(result);
                view.populate(billHistory);
            }
        });
    }

    @Override
    public void view(BillDataDTO item) {
        AppPlace place = new PortalSiteMap.Resident.Financial.BillingHistory.BillView();
        AppSite.getPlaceController().goTo(place.formPlace(item.getPrimaryKey()));
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

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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.dto.BillDTO;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.billing.ViewBillView;
import com.propertyvista.portal.client.ui.viewfactories.ResidentsViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Residents.Financial;
import com.propertyvista.portal.rpc.portal.services.resident.ViewBillService;

public class ViewBillActivity extends SecurityAwareActivity implements ViewBillView.Presenter {

    private final ViewBillView view;

    protected final ViewBillService srv;

    private Key entityId;

    public ViewBillActivity(Place place) {
        this.view = ResidentsViewFactory.instance(ViewBillView.class);
        this.view.setPresenter(this);
        srv = GWT.create(ViewBillService.class);

        String val;
        assert (place instanceof AppPlace);
        if ((val = ((AppPlace) place).getFirstArg(PortalSiteMap.ARG_ENTITY_ID)) != null) {
            entityId = new Key(val);
        }
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.retrieve(new DefaultAsyncCallback<BillDTO>() {
            @Override
            public void onSuccess(BillDTO result) {
                view.populate(result);
            }
        }, entityId, AbstractCrudService.RetrieveTraget.View);
    }

    @Override
    public void save(BillDTO entity) {
        // nothing to do here...
    }

    @Override
    public void cancel() {
        History.back();
    }

    @Override
    public void payBill() {
        AppSite.getPlaceController().goTo(new Financial.PayNow());
    }
}

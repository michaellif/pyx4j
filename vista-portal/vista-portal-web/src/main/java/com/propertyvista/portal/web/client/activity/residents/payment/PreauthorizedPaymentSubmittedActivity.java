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
package com.propertyvista.portal.web.client.activity.residents.payment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentSubmittedService;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.residents.payment.autopay.PreauthorizedPaymentSubmittedView;
import com.propertyvista.portal.web.client.ui.viewfactories.PortalWebViewFactory;

public class PreauthorizedPaymentSubmittedActivity extends SecurityAwareActivity implements PreauthorizedPaymentSubmittedView.Presenter {

    private final PreauthorizedPaymentSubmittedView view;

    protected final PreauthorizedPaymentSubmittedService srv;

    private final Key entityId;

    public PreauthorizedPaymentSubmittedActivity(AppPlace place) {
        this.view = PortalWebViewFactory.instance(PreauthorizedPaymentSubmittedView.class);
        this.view.setPresenter(this);

        srv = GWT.create(PreauthorizedPaymentSubmittedService.class);

        entityId = place.getItemId();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        assert (entityId != null);
        srv.retrieve(new DefaultAsyncCallback<PreauthorizedPaymentDTO>() {
            @Override
            public void onSuccess(PreauthorizedPaymentDTO result) {
                view.populate(result);
            }
        }, entityId);
    }

    @Override
    public void edit(Key id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void back() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.PreauthorizedPayments());
    }

    @Override
    public void save(PreauthorizedPaymentDTO value) {
        // TODO Auto-generated method stub

    }
}

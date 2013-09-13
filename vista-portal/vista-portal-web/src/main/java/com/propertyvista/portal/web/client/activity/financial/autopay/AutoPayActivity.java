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
package com.propertyvista.portal.web.client.activity.financial.autopay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPayDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.AutoPayService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.financial.autopay.AutoPayConfirmationView;

public class AutoPayActivity extends SecurityAwareActivity implements AutoPayConfirmationView.Presenter {

    private final AutoPayConfirmationView view;

    protected final AutoPayService srv;

    private final Key entityId;

    public AutoPayActivity(AppPlace place) {
        this.view = PortalWebSite.getViewFactory().instantiate(AutoPayConfirmationView.class);
        this.view.setPresenter(this);

        srv = GWT.create(AutoPayService.class);

        entityId = place.getItemId();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        assert (entityId != null);
        srv.retreiveAutoPay(new DefaultAsyncCallback<AutoPayDTO>() {
            @Override
            public void onSuccess(AutoPayDTO result) {
                view.populate(result);
            }
        }, EntityFactory.createIdentityStub(PreauthorizedPayment.class, entityId));
    }

    @Override
    public void back() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.PreauthorizedPayments());
    }

}

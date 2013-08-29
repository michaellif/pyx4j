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
package com.propertyvista.portal.client.activity.residents.payment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.payment.PaymentSubmittingView;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentSubmissionService;

public class PaymentSubmittingActivity extends SecurityAwareActivity implements PaymentSubmittingView.Presenter {

    private final PaymentSubmittingView view;

    protected final PaymentSubmissionService srv;

    private final Key entityId;

    public PaymentSubmittingActivity(AppPlace place) {
        this.view = PortalSite.getViewFactory().instantiate(PaymentSubmittingView.class);
        this.view.setPresenter(this);

        srv = GWT.create(PaymentSubmissionService.class);

        entityId = place.getItemId();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        assert (entityId != null);
        srv.retrieve(new DefaultAsyncCallback<PaymentRecordDTO>() {
            @Override
            public void onSuccess(PaymentRecordDTO result) {
                view.populate(result);
            }
        }, entityId);
    }

    @Override
    public void goToAutoPay() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Financial.PreauthorizedPayments.NewPreauthorizedPayment());
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

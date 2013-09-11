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
package com.propertyvista.portal.web.client.activity.services.insurance;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.services.insurance.TenantSureConfirmationView;
import com.propertyvista.portal.web.client.ui.services.insurance.TenantSureConfirmationView.TenantSureConfirmationPresenter;

public class TenantSureConfirmationActivity extends SecurityAwareActivity implements TenantSureConfirmationPresenter {

    private final TenantSureConfirmationView view;

//    protected final PaymentRetrieveService srv;

    private final Key entityId;

    public TenantSureConfirmationActivity(AppPlace place) {
        view = PortalWebSite.getViewFactory().instantiate(TenantSureConfirmationView.class);
        view.setPresenter(this);

//        srv = GWT.create(PaymentRetrieveService.class);

        entityId = place.getItemId();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        assert (entityId != null);
//        srv.retrieve(new DefaultAsyncCallback<TenantSureAgreementDTO>() {
//            @Override
//            public void onSuccess(TenantSureAgreementDTO result) {
//                view.populate(result);
//            }
//        }, entityId);
    }

    @Override
    public void back() {
        // TODO Auto-generated method stub

    }

}

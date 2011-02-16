/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.ui.ApartmentView;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.rpc.pt.PotencialTenantServices;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.site.client.place.AppPlace;

public class ApartmentActivity extends AbstractActivity implements ApartmentView.Presenter {

    private final ApartmentView view;

    @Inject
    public ApartmentActivity(ApartmentView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public ApartmentActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

        // TODO move this to proper initialization place.
        RPCManager.execute(PotencialTenantServices.GetCurrentApplication.class, null, new DefaultAsyncCallback<Application>() {
            @Override
            public void onSuccess(Application result) {
            }
        });
    }

}

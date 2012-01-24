/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 24, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.security;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.crm.client.ui.security.PasswordChangeView;
import com.propertyvista.crm.client.ui.viewfactories.SecurityViewFactory;
import com.propertyvista.crm.rpc.services.organization.CrmUserService;
import com.propertyvista.crm.rpc.services.organization.ManagedCrmUserService;

public class PasswordChangeActivity extends AbstractActivity implements PasswordChangeView.Presenter {

    private final PasswordChangeView view;

    public PasswordChangeActivity(Place place) {
        view = SecurityViewFactory.instance(PasswordChangeView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.discard();
        panel.setWidget(view);
    }

    @Override
    public void passwordChange(PasswordChangeRequest request) {
        // TODO find a way to determine if this is self administration (i.e. user tries to change his own password) to set the following var
        boolean isSelfAdminstrated = true;
        DefaultAsyncCallback<VoidSerializable> callback = new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                // TODO Auto-generated method stub

            }
        };
        if (isSelfAdminstrated) {
            GWT.<CrmUserService> create(CrmUserService.class).changePassword(callback, request);
        } else {
            GWT.<ManagedCrmUserService> create(ManagedCrmUserService.class).changePassword(callback, request);
        }
    }
}

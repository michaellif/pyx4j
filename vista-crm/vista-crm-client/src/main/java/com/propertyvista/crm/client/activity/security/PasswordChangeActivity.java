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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.security.PasswordChangeView;
import com.propertyvista.crm.client.ui.viewfactories.SecurityViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.organization.CrmUserService;
import com.propertyvista.crm.rpc.services.organization.ManagedCrmUserService;

public class PasswordChangeActivity extends AbstractActivity implements PasswordChangeView.Presenter {

    private final static I18n i18n = I18n.get(PasswordChangeActivity.class);

    private final PasswordChangeView view;

    private final Key userPk;

    public PasswordChangeActivity(Place place) {
        assert place instanceof CrmSiteMap.PasswordChange;
        userPk = ((CrmSiteMap.PasswordChange) place).getUserPk();

        view = SecurityViewFactory.instance(PasswordChangeView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        PasswordChangeRequest newRequest = EntityFactory.create(PasswordChangeRequest.class);
        newRequest.userPk().setValue(userPk);
        view.populate(newRequest);
        panel.setWidget(view);
    }

    @Override
    public void passwordChange(PasswordChangeRequest request) {
        DefaultAsyncCallback<VoidSerializable> callback = new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                MessageDialog.info(i18n.tr("Password was changed successfully"));
                History.back();
            }
        };
        if (isSelfAdmin()) {
            GWT.<CrmUserService> create(CrmUserService.class).changePassword(callback, request);
        } else {
            GWT.<ManagedCrmUserService> create(ManagedCrmUserService.class).changePassword(callback, request);
        }
    }

    private boolean isSelfAdmin() {
        return view.getValue().userPk().isNull()
                || EqualsHelper.equals(view.getValue().userPk().getValue(), ClientContext.getUserVisit().getPrincipalPrimaryKey());
    }

}

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AbstractPasswordChangeService;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.common.client.ui.components.security.TenantPasswordStrengthRule;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;
import com.propertyvista.portal.rpc.portal.services.PasswordChangeUserService;

public class PasswordChangeActivity extends AbstractActivity implements PasswordChangeView.Presenter {

    private final static I18n i18n = I18n.get(PasswordChangeActivity.class);

    private final PasswordChangeView view;

    public PasswordChangeActivity(Place place) {
        view = PtAppViewFactory.instance(PasswordChangeView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setAskForCurrentPassword(false);
        view.setAskForRequireChangePasswordOnNextSignIn(false, null, null);
        view.setPasswordStrengthRule(new TenantPasswordStrengthRule(ClientContext.getUserVisit().getName(), ClientContext.getUserVisit().getName()));

        view.initialize(ClientContext.getUserVisit().getPrincipalPrimaryKey(), null);
        panel.setWidget(view);
    }

    @Override
    public void changePassword(PasswordChangeRequest request) {
        AbstractPasswordChangeService service = GWT.<AbstractPasswordChangeService> create(PasswordChangeUserService.class);
        service.changePassword(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                MessageDialog.info(i18n.tr("Password was changed successfully"));
                History.back();
            }

            @Override
            public void onFailure(Throwable caught) {
                MessageDialog.error(i18n.tr("Failed to change the password"), caught);
            }
        }, request);
    }

    @Override
    public void cancel() {
        History.back();
    }
}

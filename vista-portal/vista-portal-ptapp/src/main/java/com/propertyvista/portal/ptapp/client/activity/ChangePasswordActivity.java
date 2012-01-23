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
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.login.NewPasswordForm.ConversationType;
import com.propertyvista.portal.ptapp.client.ui.NewPasswordView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;
import com.propertyvista.portal.rpc.ptapp.services.UserService;

public class ChangePasswordActivity extends AbstractActivity implements NewPasswordView.Presenter {

    private static final I18n i18n = I18n.get(ChangePasswordActivity.class);

    private final NewPasswordView view;

    public ChangePasswordActivity(Place place) {
        view = PtAppViewFactory.instance(NewPasswordView.class);
        assert (view != null);
        view.setConversationType(ConversationType.CHANGE);
        view.setPresenter(this);
        withPlace(place);
    }

    public ChangePasswordActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void passwordReset(PasswordChangeRequest request) {
        // never supposed to happen in the context of change password activity
    }

    @Override
    public void passwordChange(PasswordChangeRequest request) {
        GWT.<UserService> create(UserService.class).passwordReset(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                MessageDialog.info(i18n.tr("The password was changed successfully"));
            }
        }, request);
    }

    public void setConversationType(ConversationType type) {
        view.setConversationType(type);
    }
}

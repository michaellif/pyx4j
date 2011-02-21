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
package com.propertyvista.portal.client.ptapp.activity;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.propertyvista.portal.client.ptapp.PtAppWizardManager;
import com.propertyvista.portal.client.ptapp.ui.NewPasswordView;
import com.propertyvista.portal.client.ptapp.ui.NewPasswordView.ConversationType;
import com.propertyvista.portal.rpc.pt.ActivationServices;
import com.propertyvista.portal.rpc.pt.PasswordChangeRequest;
import com.propertyvista.portal.rpc.pt.SiteMap;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.rpc.AppPlace;

public class ChangePasswordActivity extends AbstractActivity implements NewPasswordView.Presenter {

    private static I18n i18n = I18nFactory.getI18n(ChangePasswordActivity.class);

    private final NewPasswordView view;

    private final PlaceController placeController;

    private ConversationType conversationType;

    private final Provider<PtAppWizardManager> wizardManagerProvider;

    private String token;

    @Inject
    public ChangePasswordActivity(NewPasswordView view, PlaceController placeController, Provider<PtAppWizardManager> wizardManagerProvider) {
        this.view = view;
        view.setConversationType(ConversationType.CHANGE);
        this.placeController = placeController;
        this.wizardManagerProvider = wizardManagerProvider;
        view.setPresenter(this);
    }

    public ChangePasswordActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        //TODO
        panel.setWidget(view);
    }

    @Override
    public void passwordReset(PasswordChangeRequest request) {
        request.token().setValue(token);
        AsyncCallback<VoidSerializable> callback = new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                //TODO
            }
        };

        RPCManager.execute(ActivationServices.PasswordChange.class, request, callback);

    }

    @Override
    public void passwordChange(PasswordChangeRequest request) {
        // TODO later
    }

    public void setConversationType(ConversationType type) {
        conversationType = type;
        view.setConversationType(type);
    }
}

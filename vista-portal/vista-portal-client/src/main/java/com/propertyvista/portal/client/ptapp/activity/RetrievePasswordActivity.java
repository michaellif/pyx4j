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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.propertyvista.portal.client.ptapp.PtAppWizardManager;
import com.propertyvista.portal.client.ptapp.ui.RetrievePasswordView;
import com.propertyvista.portal.rpc.pt.ActivationServices;
import com.propertyvista.portal.rpc.pt.PasswordRetrievalRequest;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.rpc.AppPlace;

public class RetrievePasswordActivity extends AbstractActivity implements RetrievePasswordView.Presenter {

    private final RetrievePasswordView view;

    private final PlaceController placeController;

    private final Provider<PtAppWizardManager> wizardManagerProvider;

    @Inject
    public RetrievePasswordActivity(RetrievePasswordView view, PlaceController placeController, Provider<PtAppWizardManager> wizardManagerProvider) {
        this.view = view;
        this.placeController = placeController;
        this.wizardManagerProvider = wizardManagerProvider;
        view.setPresenter(this);
    }

    public RetrievePasswordActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void retrievePassword(PasswordRetrievalRequest request) {
        AsyncCallback<VoidSerializable> callback = new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                wizardManagerProvider.get().showMessageDialog("Please check your email", "", null, null);
            }
        };
        RPCManager.execute(ActivationServices.PasswordReminder.class, request, callback);
    }
}

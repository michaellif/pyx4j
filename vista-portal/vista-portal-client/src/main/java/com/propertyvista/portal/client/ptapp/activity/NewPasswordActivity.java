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
import com.propertyvista.portal.client.ptapp.PtAppWizardManager;
import com.propertyvista.portal.client.ptapp.ui.NewPasswordView;
import com.propertyvista.portal.rpc.pt.PasswordChangeRequest;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.place.AppPlace;

public class NewPasswordActivity extends AbstractActivity implements NewPasswordView.Presenter {

    private final NewPasswordView view;

    private final PlaceController placeController;

    @Inject
    public NewPasswordActivity(NewPasswordView view, PlaceController placeController, PtAppWizardManager manager) {
        this.view = view;
        this.placeController = placeController;
        view.setPresenter(this);
    }

    public NewPasswordActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void newPassword(PasswordChangeRequest request) {
        AsyncCallback<VoidSerializable> callback = new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                // TODO Auto-generated method stub
            }
        };
        // TODO: uncomment when AuthenticationServices.PasswordChange will be implemented!!!
        //        RPCManager.execute(AuthenticationServices.PasswordChange.class, request, callback);

    }
}

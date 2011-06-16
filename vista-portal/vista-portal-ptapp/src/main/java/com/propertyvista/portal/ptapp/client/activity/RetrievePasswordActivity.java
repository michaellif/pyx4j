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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.portal.ptapp.client.PtAppSite;
import com.propertyvista.portal.ptapp.client.ui.RetrievePasswordView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;
import com.propertyvista.portal.rpc.ptapp.PasswordRetrievalRequest;
import com.propertyvista.portal.rpc.ptapp.services.ActivationService;

public class RetrievePasswordActivity extends AbstractActivity implements RetrievePasswordView.Presenter {

    private final RetrievePasswordView view;

    public RetrievePasswordActivity(Place place) {
        view = (RetrievePasswordView) PtAppViewFactory.instance(RetrievePasswordView.class);
        assert (view != null);
        view.setPresenter(this);
        withPlace(place);
    }

    public RetrievePasswordActivity withPlace(Place place) {
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
                PtAppSite.instance().showMessageDialog("Please check your email", "", null, null);
            }
        };
        ((ActivationService) GWT.create(ActivationService.class)).passwordReminder(callback, request);
    }
}

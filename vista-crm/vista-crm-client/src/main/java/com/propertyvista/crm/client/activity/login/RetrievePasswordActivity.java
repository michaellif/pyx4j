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
package com.propertyvista.crm.client.activity.login;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.login.RetrievePasswordView;
import com.propertyvista.crm.client.ui.viewfactories.LoginVeiwFactory;
import com.propertyvista.crm.rpc.ActivationServices;
import com.propertyvista.crm.rpc.PasswordRetrievalRequest;

public class RetrievePasswordActivity extends AbstractActivity implements RetrievePasswordView.Presenter {

    private static I18n i18n = I18n.get(RetrievePasswordActivity.class);

    private final RetrievePasswordView view;

    public RetrievePasswordActivity(Place place) {
        this.view = (RetrievePasswordView) LoginVeiwFactory.instance(RetrievePasswordView.class);
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
                CrmSite.instance().showMessageDialog(i18n.tr("Please check your email"), "", null, null);
            }
        };
        RPCManager.execute(ActivationServices.PasswordReminder.class, request, callback);
    }
}

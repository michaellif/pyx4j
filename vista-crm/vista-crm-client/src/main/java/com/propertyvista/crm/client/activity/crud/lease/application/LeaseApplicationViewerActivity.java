/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease.application;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.lease.application.LeaseApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationCrudService;
import com.propertyvista.dto.ApplicationUserDTO;
import com.propertyvista.dto.LeaseApplicationDTO;

public class LeaseApplicationViewerActivity extends CrmViewerActivity<LeaseApplicationDTO> implements LeaseApplicationViewerView.Presenter {

    private static final I18n i18n = I18n.get(LeaseApplicationViewerActivity.class);

    @SuppressWarnings("unchecked")
    public LeaseApplicationViewerActivity(Place place) {
        super(place, LeaseViewFactory.instance(LeaseApplicationViewerView.class), (AbstractCrudService<LeaseApplicationDTO>) GWT
                .create(LeaseApplicationCrudService.class));
    }

    // Actions:

    @Override
    public void startOnlineApplication() {
        ((LeaseApplicationCrudService) service).startOnlineApplication(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId);
    }

    @Override
    public void retrieveUsers(final AsyncCallback<List<ApplicationUserDTO>> callback) {
        ((LeaseApplicationCrudService) service).retrieveUsers(new DefaultAsyncCallback<Vector<ApplicationUserDTO>>() {
            @Override
            public void onSuccess(Vector<ApplicationUserDTO> result) {
                callback.onSuccess(result);
            }
        }, entityId);
    }

    @Override
    public void inviteUsers(List<ApplicationUserDTO> users) {
        Vector<ApplicationUserDTO> vector = new Vector<ApplicationUserDTO>(users);
        ((LeaseApplicationCrudService) service).inviteUsers(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId, vector);
    }

    @Override
    public void applicationAction(LeaseApplicationActionDTO action) {
        ((LeaseApplicationCrudService) service).applicationAction(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, action);
    }
}

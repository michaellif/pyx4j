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
package com.propertyvista.admin.client.activity.crud.pmc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.admin.client.activity.crud.AdminViewerActivity;
import com.propertyvista.admin.client.ui.crud.pmc.PmcViewerView;
import com.propertyvista.admin.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcCrudService;

public class PmcViewerActivity extends AdminViewerActivity<PmcDTO> implements PmcViewerView.Presenter {

    @SuppressWarnings("unchecked")
    public PmcViewerActivity(Place place) {
        super(place, ManagementVeiwFactory.instance(PmcViewerView.class), (AbstractCrudService<PmcDTO>) GWT.create(PmcCrudService.class));

    }

    @Override
    public void resetCache() {
        ((PmcCrudService) getService()).resetCache(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                Window.alert("The cache was reset successfully");
            }
        }, entityId);

    }

    @Override
    public void activate() {
        ((PmcCrudService) getService()).activate(new DefaultAsyncCallback<PmcDTO>() {

            @Override
            public void onSuccess(PmcDTO result) {
                populateView(result);
            }
        }, entityId);

    }

    @Override
    public void suspend() {
        ((PmcCrudService) getService()).suspend(new DefaultAsyncCallback<PmcDTO>() {

            @Override
            public void onSuccess(PmcDTO result) {
                populateView(result);
            }
        }, entityId);
    }
}

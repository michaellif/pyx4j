/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.admin.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.ui.crud.IEditorView;

import com.propertyvista.admin.client.ui.administration.MaintenanceView;
import com.propertyvista.admin.client.viewfactories.AdministrationVeiwFactory;
import com.propertyvista.admin.rpc.services.VistaAdminService;

public class MaintenanceActivity extends AbstractActivity implements IEditorView.Presenter {

    private final MaintenanceView view;

    protected final VistaAdminService service;

    public MaintenanceActivity(Place place) {
        view = (MaintenanceView) AdministrationVeiwFactory.instance(MaintenanceView.class);
        assert (view != null);
        view.setPresenter(this);

        service = GWT.create(VistaAdminService.class);
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        container.setWidget(view);
        populate();
    }

    public MaintenanceActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void setPlace(Place place) {
        // TODO Auto-generated method stub

    }

    @Override
    public void populate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void apply() {
        // TODO Auto-generated method stub

    }

    @Override
    public void save() {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub

    }
}

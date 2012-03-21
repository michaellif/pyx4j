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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.ui.crud.form.IEditorView;

import com.propertyvista.admin.client.ui.administration.MaintenanceView;
import com.propertyvista.admin.client.viewfactories.crud.AdministrationVeiwFactory;
import com.propertyvista.admin.rpc.services.VistaAdminService;

public class MaintenanceActivity extends AbstractActivity implements IEditorView.Presenter {

    private final static I18n i18n = I18n.get(MaintenanceActivity.class);

    private final MaintenanceView view;

    protected final VistaAdminService service;

    public MaintenanceActivity(Place place) {
        view = AdministrationVeiwFactory.instance(MaintenanceView.class);
        assert (view != null);
        view.setPresenter(this);

        service = GWT.create(VistaAdminService.class);
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        view.reset();
        view.setPresenter(this);
        populate();
        container.setWidget(view);
    }

    public MaintenanceActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void populate() {
        service.getSystemMaintenanceState(new AsyncCallback<SystemMaintenanceState>() {
            @Override
            public void onSuccess(SystemMaintenanceState result) {
                view.populate(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        });
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub
    }

    @Override
    public void apply() {
        trySave(true);
    }

    @Override
    public void save() {
        trySave(false);
    }

    @Override
    public void cancel() {
        History.back();
    }

    public void trySave(final boolean apply) {
        service.setSystemMaintenanceState(new AsyncCallback<SystemMaintenanceState>() {
            @Override
            public void onSuccess(SystemMaintenanceState result) {

            }

            @Override
            public void onFailure(Throwable caught) {
                if (!view.onSaveFail(caught)) {
                    throw new UnrecoverableClientError(caught);
                }
            }
        }, view.getValue());
    }

    public void resetGlobalCache() {
        VistaAdminService service = GWT.create(VistaAdminService.class);
        service.resetGlobalCache(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                Window.alert(i18n.tr("The global cache was reset succsessfully"));
            }
        });
    }
}

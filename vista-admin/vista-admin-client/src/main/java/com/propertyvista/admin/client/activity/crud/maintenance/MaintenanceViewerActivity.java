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
package com.propertyvista.admin.client.activity.crud.maintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;

import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.admin.client.activity.crud.AdminViewerActivity;
import com.propertyvista.admin.client.ui.crud.maintenance.MaintenanceViewerView;
import com.propertyvista.admin.client.viewfactories.crud.AdministrationVeiwFactory;
import com.propertyvista.admin.rpc.services.MaintenanceCrudService;

public class MaintenanceViewerActivity extends AdminViewerActivity<SystemMaintenanceState> implements MaintenanceViewerView.Presenter {

    private final static I18n i18n = I18n.get(MaintenanceViewerActivity.class);

    public MaintenanceViewerActivity(Place place) {
        super(place, AdministrationVeiwFactory.instance(MaintenanceViewerView.class), GWT.<MaintenanceCrudService> create(MaintenanceCrudService.class));
    }

    @Override
    public void resetGlobalCache() {
        ((MaintenanceCrudService) service).resetGlobalCache(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                Window.alert(i18n.tr("The global cache was reset successfully"));
            }
        });
    }
}

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.admin.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.activity.AccountActivity;
import com.propertyvista.admin.client.activity.AlertActivity;
import com.propertyvista.admin.client.activity.MaintenanceActivity;
import com.propertyvista.admin.client.activity.MessageActivity;
import com.propertyvista.admin.client.activity.SettingsActivity;
import com.propertyvista.admin.client.activity.crud.pmc.PmcEditorActivity;
import com.propertyvista.admin.client.activity.crud.pmc.PmcListerActivity;
import com.propertyvista.admin.client.activity.crud.pmc.PmcViewerActivity;
import com.propertyvista.admin.rpc.AdminSiteMap;

public class MainActivityMapper implements AppActivityMapper {

    public MainActivityMapper() {
    }

    @Override
    public void obtainActivity(final Place place, final AsyncCallback<Activity> callback) {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                Activity activity = null;
                if (place instanceof AdminSiteMap.Management.PMC) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new PmcEditorActivity(place);
                        break;
                    case viewer:
                        activity = new PmcViewerActivity(place);
                        break;
                    case lister:
                        activity = new PmcListerActivity(place);
                        break;
                    }

                    // - Administration:
                } else if (place instanceof AdminSiteMap.Administration.Maintenance) {
                    activity = new MaintenanceActivity(place);

                    // - Settings:
                } else if (place instanceof AdminSiteMap.Settings) {
                    activity = new SettingsActivity(place);

                    // - Other:
                } else if (place instanceof AdminSiteMap.Account) {
                    activity = new AccountActivity(place);
                } else if (place instanceof AdminSiteMap.Alert) {
                    activity = new AlertActivity(place);
                } else if (place instanceof AdminSiteMap.Message) {
                    activity = new MessageActivity(place);
                }

                callback.onSuccess(activity);
            }

            @Override
            public void onFailure(Throwable reason) {
                callback.onFailure(reason);
            }
        });

    }
}

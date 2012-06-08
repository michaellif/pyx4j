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

import com.propertyvista.admin.client.activity.AlertActivity;
import com.propertyvista.admin.client.activity.MessageActivity;
import com.propertyvista.admin.client.activity.SettingsActivity;
import com.propertyvista.admin.client.activity.crud.adminusers.AdminUserEditorActivity;
import com.propertyvista.admin.client.activity.crud.adminusers.AdminUserListerActivity;
import com.propertyvista.admin.client.activity.crud.adminusers.AdminUserViewerActivity;
import com.propertyvista.admin.client.activity.crud.maintenance.MaintenanceEditorActivity;
import com.propertyvista.admin.client.activity.crud.maintenance.MaintenanceViewerActivity;
import com.propertyvista.admin.client.activity.crud.onboardingusers.OnBoardingUserViewerActivity;
import com.propertyvista.admin.client.activity.crud.onboardingusers.OnboardingUserEditorActivity;
import com.propertyvista.admin.client.activity.crud.onboardingusers.OnboardingUserListerActivity;
import com.propertyvista.admin.client.activity.crud.pmc.PmcEditorActivity;
import com.propertyvista.admin.client.activity.crud.pmc.PmcListerActivity;
import com.propertyvista.admin.client.activity.crud.pmc.PmcViewerActivity;
import com.propertyvista.admin.client.activity.crud.scheduler.RunListerActivity;
import com.propertyvista.admin.client.activity.crud.scheduler.RunViewerActivity;
import com.propertyvista.admin.client.activity.crud.scheduler.TriggerEditorActivity;
import com.propertyvista.admin.client.activity.crud.scheduler.TriggerListerActivity;
import com.propertyvista.admin.client.activity.crud.scheduler.TriggerViewerActivity;
import com.propertyvista.admin.client.activity.crud.simulateddatapreload.SimulatedDataPreloadActivity;
import com.propertyvista.admin.client.activity.crud.simulatedpad.PadBatchEditorActivity;
import com.propertyvista.admin.client.activity.crud.simulatedpad.PadBatchViewerActivity;
import com.propertyvista.admin.client.activity.crud.simulatedpad.PadFileEditorActivity;
import com.propertyvista.admin.client.activity.crud.simulatedpad.PadFileListerActivity;
import com.propertyvista.admin.client.activity.crud.simulatedpad.PadFileViewerActivity;
import com.propertyvista.admin.client.activity.crud.simulation.SimulationEditorActivity;
import com.propertyvista.admin.client.activity.crud.simulation.SimulationViewerActivity;
import com.propertyvista.admin.client.activity.security.PasswordChangeActivity;
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
                if (place instanceof CrudAppPlace) {
                    CrudAppPlace crudPlace = (CrudAppPlace) place;
                    if (place instanceof AdminSiteMap.Management.PMC) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new PmcEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PmcViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new PmcListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof AdminSiteMap.Management.Trigger) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new TriggerEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new TriggerViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new TriggerListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof AdminSiteMap.Management.Run) {
                        switch (crudPlace.getType()) {
                        case viewer:
                            activity = new RunViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new RunListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof AdminSiteMap.Management.OnboardingUsers) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new OnboardingUserEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new OnBoardingUserViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new OnboardingUserListerActivity(crudPlace);
                            break;
                        }
// - Administration:
                    } else if (place instanceof AdminSiteMap.Administration.Maintenance) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new MaintenanceEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new MaintenanceViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof AdminSiteMap.Administration.Simulation) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new SimulationEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new SimulationViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof AdminSiteMap.Administration.SimulatedDataPreload) {
                        activity = new SimulatedDataPreloadActivity();

                    } else if (place instanceof AdminSiteMap.Administration.PadSimulation.PadSimFile) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new PadFileEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PadFileViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new PadFileListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof AdminSiteMap.Administration.PadSimulation.PadSimBatch) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new PadBatchEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PadBatchViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof AdminSiteMap.Administration.AdminUsers) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new AdminUserEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new AdminUserViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new AdminUserListerActivity(crudPlace);
                            break;
                        }
                    }
// - Settings:
                } else if (place instanceof AdminSiteMap.Settings) {
                    activity = new SettingsActivity(place);

// - Other:
                } else if (place instanceof AdminSiteMap.Account) {
                    // is supposed to be AdminUser*Activity for  AdminSiteMap.Administration.AdminUsers                    
                } else if (place instanceof AdminSiteMap.Alert) {
                    activity = new AlertActivity(place);
                } else if (place instanceof AdminSiteMap.Message) {
                    activity = new MessageActivity(place);

                } else if (place instanceof AdminSiteMap.PasswordChange) {
                    activity = new PasswordChangeActivity(place);
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

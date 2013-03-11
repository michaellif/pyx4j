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
package com.propertyvista.field.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.field.rpc.FieldSiteMap;

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

// - Property-related:
                    if (crudPlace instanceof FieldSiteMap.Properties.Building) {
                        switch (crudPlace.getType()) {
                        case editor:
                            //activity = new BuildingEditorActivity(crudPlace);
                            break;
                        case viewer:
                            //activity = new BuildingViewerActivity(crudPlace);
                            break;
                        case lister:
                            //activity = new BuildingListerActivity(crudPlace);
                            break;
                        }
// - Unit-related:
                    } else if (crudPlace instanceof FieldSiteMap.Properties.Unit) {
                        switch (crudPlace.getType()) {
                        case editor:
                            //activity = new UnitEditorActivity(crudPlace);
                            break;
                        case viewer:
                            //activity = new UnitViewerActivity(crudPlace);
                            break;
                        case lister:
                            //activity = new UnitListerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof FieldSiteMap.Properties.UnitItem) {
                        switch (crudPlace.getType()) {
                        case editor:
                            //activity = new UnitItemEditorActivity(crudPlace);
                            break;
                        case viewer:
                            //activity = new UnitItemViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof FieldSiteMap.Properties.UnitOccupancy) {
                        switch (crudPlace.getType()) {
                        case editor:
                            //activity = new UnitOccupancyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            //activity = new UnitOccupancyViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }
// - Tenant-related:
                    } else if (crudPlace instanceof FieldSiteMap.Tenants.Tenant) {
                        switch (crudPlace.getType()) {
                        case editor:
                            //activity = new TenantEditorActivity(crudPlace);
                            break;
                        case viewer:
                            //activity = new TenantViewerActivity(crudPlace);
                            break;
                        case lister:
                            //activity = new TenantListerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof FieldSiteMap.Tenants.Guarantor) {
                        switch (crudPlace.getType()) {
                        case editor:
                            //activity = new GuarantorEditorActivity(crudPlace);
                            break;
                        case viewer:
                            //activity = new GuarantorViewerActivity(crudPlace);
                            break;
                        case lister:
                            //activity = new GuarantorListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof FieldSiteMap.Tenants.Lease) {
                        switch (crudPlace.getType()) {
                        case lister:
                            //activity = new LeaseListerActivity(crudPlace);
                            break;
                        case viewer:
                            //activity = new LeaseViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof FieldSiteMap.Tenants.LeaseTerm) {
                        switch (crudPlace.getType()) {
                        case viewer:
                            //activity = new LeaseTermViewerActivity(crudPlace);
                            break;
                        case editor:
                            //activity = new LeaseTermEditorActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof FieldSiteMap.Tenants.MaintenanceRequest) {
                        switch (crudPlace.getType()) {
                        case lister:
                            //activity = new MaintenanceRequestListerActivity(crudPlace);
                            break;
                        case viewer:
                            //activity = new MaintenanceRequestViewerActivity(crudPlace);
                            break;
                        case editor:
                            //activity = new MaintenanceRequestEditorActivity(crudPlace);
                            break;
                        }
                    } // CRUD APP PLACE IF ENDS HERE

// - Other:
                } else if (place instanceof FieldSiteMap.PasswordChange) {
                    //activity = new PasswordChangeActivity(place);
                } else if (place instanceof FieldSiteMap.Alert) {
                    //activity = new AlertActivity(place);
                } else if (place instanceof FieldSiteMap.Message) {
                    //activity = new MessageActivity(place);
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

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
package com.propertyvista.crm.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.AccountActivity;
import com.propertyvista.crm.client.activity.AlertActivity;
import com.propertyvista.crm.client.activity.MessageActivity;
import com.propertyvista.crm.client.activity.crud.building.BoilerEditorActivity;
import com.propertyvista.crm.client.activity.crud.building.BoilerViewerActivity;
import com.propertyvista.crm.client.activity.crud.building.BuildingEditorActivity;
import com.propertyvista.crm.client.activity.crud.building.BuildingListerActivity;
import com.propertyvista.crm.client.activity.crud.building.BuildingViewerActivity;
import com.propertyvista.crm.client.activity.crud.building.ElevatorEditorActivity;
import com.propertyvista.crm.client.activity.crud.building.ElevatorViewerActivity;
import com.propertyvista.crm.client.activity.crud.building.LockerAreaEditorActivity;
import com.propertyvista.crm.client.activity.crud.building.LockerAreaViewerActivity;
import com.propertyvista.crm.client.activity.crud.building.LockerEditorActivity;
import com.propertyvista.crm.client.activity.crud.building.LockerListerActivity;
import com.propertyvista.crm.client.activity.crud.building.LockerViewerActivity;
import com.propertyvista.crm.client.activity.crud.building.ParkingEditorActivity;
import com.propertyvista.crm.client.activity.crud.building.ParkingSpotEditorActivity;
import com.propertyvista.crm.client.activity.crud.building.ParkingSpotListerActivity;
import com.propertyvista.crm.client.activity.crud.building.ParkingSpotViewerActivity;
import com.propertyvista.crm.client.activity.crud.building.ParkingViewerActivity;
import com.propertyvista.crm.client.activity.crud.building.RoofEditorActivity;
import com.propertyvista.crm.client.activity.crud.building.RoofViewerActivity;
import com.propertyvista.crm.client.activity.crud.floorplan.FloorplanEditorActivity;
import com.propertyvista.crm.client.activity.crud.floorplan.FloorplanViewerActivity;
import com.propertyvista.crm.client.activity.crud.marketing.ConcessionEditorActivity;
import com.propertyvista.crm.client.activity.crud.marketing.ConcessionViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.ContentActivity;
import com.propertyvista.crm.client.activity.crud.settings.ContentEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.ApplicationEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.ApplicationListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.ApplicationViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.InquiryEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.InquiryListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.InquiryViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.LeaseEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.LeaseListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.LeaseViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.TenantEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.TenantListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.TenantViewerActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitEditorActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitItemEditorActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitItemViewerActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitListerActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitOccupancyEditorActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitOccupancyViewerActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitViewerActivity;
import com.propertyvista.crm.client.activity.dashboard.DashboardEditorActivity;
import com.propertyvista.crm.client.activity.dashboard.DashboardManagementActivity;
import com.propertyvista.crm.client.activity.dashboard.DashboardViewActivity;
import com.propertyvista.crm.client.activity.login.ResetPasswordActivity;
import com.propertyvista.crm.client.activity.report.ReportEditorActivity;
import com.propertyvista.crm.client.activity.report.ReportManagementActivity;
import com.propertyvista.crm.client.activity.report.ReportViewActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class MainActivityMapper implements AppActivityMapper {

    public MainActivityMapper() {
    }

    @Override
    public void obtainActivity(final Place place, final AsyncCallback<Activity> callback) {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                Activity activity = null;
                if (place instanceof CrmSiteMap.ResetPassword) {
                    activity = new ResetPasswordActivity(place);
// - Building-related:
                } else if (place instanceof CrmSiteMap.Properties.Building) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new BuildingEditorActivity(place);
                        break;
                    case viewer:
                        activity = new BuildingViewerActivity(place);
                        break;
                    case lister:
                        activity = new BuildingListerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Properties.Elevator) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new ElevatorEditorActivity(place);
                        break;
                    case viewer:
                        activity = new ElevatorViewerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Properties.Boiler) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new BoilerEditorActivity(place);
                        break;
                    case viewer:
                        activity = new BoilerViewerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Properties.Roof) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new RoofEditorActivity(place);
                        break;
                    case viewer:
                        activity = new RoofViewerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Properties.Parking) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new ParkingEditorActivity(place);
                        break;
                    case viewer:
                        activity = new ParkingViewerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Properties.ParkingSpot) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new ParkingSpotEditorActivity(place);
                        break;
                    case viewer:
                        activity = new ParkingSpotViewerActivity(place);
                        break;
                    case lister:
                        activity = new ParkingSpotListerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Properties.LockerArea) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new LockerAreaEditorActivity(place);
                        break;
                    case viewer:
                        activity = new LockerAreaViewerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Properties.Locker) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new LockerEditorActivity(place);
                        break;
                    case viewer:
                        activity = new LockerViewerActivity(place);
                        break;
                    case lister:
                        activity = new LockerListerActivity(place);
                        break;
                    }
// - Floorplan-related:

                } else if (place instanceof CrmSiteMap.Properties.Floorplan) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new FloorplanEditorActivity(place);
                        break;
                    case viewer:
                        activity = new FloorplanViewerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Properties.Concession) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new ConcessionEditorActivity(place);
                        break;
                    case viewer:
                        activity = new ConcessionViewerActivity(place);
                        break;
                    }

// - Unit-related:
                } else if (place instanceof CrmSiteMap.Properties.Unit) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new UnitEditorActivity(place);
                        break;
                    case viewer:
                        activity = new UnitViewerActivity(place);
                        break;
                    case lister:
                        activity = new UnitListerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Properties.UnitItem) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new UnitItemEditorActivity(place);
                        break;
                    case viewer:
                        activity = new UnitItemViewerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Properties.UnitOccupancy) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new UnitOccupancyEditorActivity(place);
                        break;
                    case viewer:
                        activity = new UnitOccupancyViewerActivity(place);
                        break;
                    }

// - Tenant-related:
                } else if (place instanceof CrmSiteMap.Tenants.Tenant) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new TenantEditorActivity(place);
                        break;
                    case viewer:
                        activity = new TenantViewerActivity(place);
                        break;
                    case lister:
                        activity = new TenantListerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Tenants.Lease) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new LeaseEditorActivity(place);
                        break;
                    case viewer:
                        activity = new LeaseViewerActivity(place);
                        break;
                    case lister:
                        activity = new LeaseListerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Tenants.Application) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new ApplicationEditorActivity(place);
                        break;
                    case viewer:
                        activity = new ApplicationViewerActivity(place);
                        break;
                    case lister:
                        activity = new ApplicationListerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Tenants.Inquiry) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new InquiryEditorActivity(place);
                        break;
                    case viewer:
                        activity = new InquiryViewerActivity(place);
                        break;
                    case lister:
                        activity = new InquiryListerActivity(place);
                        break;
                    }
// Reports:
                } else if (place instanceof CrmSiteMap.Report) {
                    activity = new ReportViewActivity(place);
                } else if (place instanceof CrmSiteMap.Report.Edit) {
                    activity = new ReportEditorActivity(place);
                } else if (place instanceof CrmSiteMap.Report.Management) {
                    activity = new ReportManagementActivity(place);
                } else if (place instanceof CrmSiteMap.Report.System) {
                    activity = new ReportViewActivity(place);
// Dashboards:
                } else if (place instanceof CrmSiteMap.Dashboard) {
                    activity = new DashboardViewActivity(place);
                } else if (place instanceof CrmSiteMap.Dashboard.Edit) {
                    activity = new DashboardEditorActivity(place);
                } else if (place instanceof CrmSiteMap.Dashboard.Management) {
                    activity = new DashboardManagementActivity(place);
                } else if (place instanceof CrmSiteMap.Dashboard.System) {
                    activity = new DashboardViewActivity(place);
                } else if (place instanceof CrmSiteMap.Dashboard.Building) {
                    activity = new DashboardViewActivity(place);
// - Other:
                } else if (place instanceof CrmSiteMap.Account) {
                    activity = new AccountActivity(place);
                } else if (place instanceof CrmSiteMap.Alert) {
                    activity = new AlertActivity(place);
                } else if (place instanceof CrmSiteMap.Message) {
                    activity = new MessageActivity(place);

// - Settings:
                } else if (place instanceof CrmSiteMap.Settings.Content) {
                    if (((AppPlace) place).getArg(CrudAppPlace.ARG_NAME_ITEM_ID) == null) {
                        activity = new ContentActivity(place);
                    } else {
                        activity = new ContentEditorActivity(place);
                    }
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

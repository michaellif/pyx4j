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
import com.google.inject.Inject;

import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.activity.AccountActivity;
import com.propertyvista.crm.client.activity.AlertActivity;
import com.propertyvista.crm.client.activity.DashboardActivity;
import com.propertyvista.crm.client.activity.MessageActivity;
import com.propertyvista.crm.client.activity.ReportActivity;
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
import com.propertyvista.crm.client.activity.login.ResetPasswordActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class MainActivityMapper implements AppActivityMapper {

    @Inject
    public MainActivityMapper() {
        super();
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
                } else if (place instanceof CrmSiteMap.Properties.Buildings) {
                    activity = new BuildingListerActivity(place);
                } else if (place instanceof CrmSiteMap.Viewers.Building) {
                    activity = new BuildingViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.Building) {
                    activity = new BuildingEditorActivity(place);

                } else if (place instanceof CrmSiteMap.Viewers.Elevator) {
                    activity = new ElevatorViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.Elevator) {
                    activity = new ElevatorEditorActivity(place);

                } else if (place instanceof CrmSiteMap.Viewers.Boiler) {
                    activity = new BoilerViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.Boiler) {
                    activity = new BoilerEditorActivity(place);

                } else if (place instanceof CrmSiteMap.Viewers.Roof) {
                    activity = new RoofViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.Roof) {
                    activity = new RoofEditorActivity(place);

                } else if (place instanceof CrmSiteMap.Viewers.Parking) {
                    activity = new ParkingViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.Parking) {
                    activity = new ParkingEditorActivity(place);

                } else if (place instanceof CrmSiteMap.Properties.ParkingSpots) {
                    activity = new ParkingSpotListerActivity(place);
                } else if (place instanceof CrmSiteMap.Viewers.ParkingSpot) {
                    activity = new ParkingSpotViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.Parking) {
                    activity = new ParkingSpotEditorActivity(place);

                } else if (place instanceof CrmSiteMap.Viewers.LockerArea) {
                    activity = new LockerAreaViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.LockerArea) {
                    activity = new LockerAreaEditorActivity(place);

                } else if (place instanceof CrmSiteMap.Properties.Lockers) {
                    activity = new LockerListerActivity(place);
                } else if (place instanceof CrmSiteMap.Viewers.Locker) {
                    activity = new LockerViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.Locker) {
                    activity = new LockerEditorActivity(place);

                    // - Unit-related:
                } else if (place instanceof CrmSiteMap.Properties.Units) {
                    activity = new UnitListerActivity(place);
                } else if (place instanceof CrmSiteMap.Viewers.Unit) {
                    activity = new UnitViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.Unit) {
                    activity = new UnitEditorActivity(place);

                } else if (place instanceof CrmSiteMap.Viewers.UnitItem) {
                    activity = new UnitItemViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.UnitItem) {
                    activity = new UnitItemEditorActivity(place);

                } else if (place instanceof CrmSiteMap.Viewers.UnitOccupancy) {
                    activity = new UnitOccupancyViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.UnitOccupancy) {
                    activity = new UnitOccupancyEditorActivity(place);

                } else if (place instanceof CrmSiteMap.Viewers.Concession) {
                    activity = new ConcessionViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.Concession) {
                    activity = new ConcessionEditorActivity(place);

                    // - Tenant-related:
                } else if (place instanceof CrmSiteMap.Tenants.AllTenants) {
                    activity = new TenantListerActivity(place);
                } else if (place instanceof CrmSiteMap.Viewers.Tenant) {
                    activity = new TenantViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.Tenant) {
                    activity = new TenantEditorActivity(place);

                } else if (place instanceof CrmSiteMap.Tenants.Leases) {
                    activity = new LeaseListerActivity(place);
                } else if (place instanceof CrmSiteMap.Viewers.Lease) {
                    activity = new LeaseViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.Lease) {
                    activity = new LeaseEditorActivity(place);

                } else if (place instanceof CrmSiteMap.Tenants.Applications) {
                    activity = new ApplicationListerActivity(place);
                } else if (place instanceof CrmSiteMap.Viewers.Application) {
                    activity = new ApplicationViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.Application) {
                    activity = new ApplicationEditorActivity(place);

                } else if (place instanceof CrmSiteMap.Tenants.Inquiries) {
                    activity = new InquiryListerActivity(place);
                } else if (place instanceof CrmSiteMap.Viewers.Inquiry) {
                    activity = new InquiryViewerActivity(place);
                } else if (place instanceof CrmSiteMap.Editors.Inquiry) {
                    activity = new InquiryEditorActivity(place);

                    // - Other:
                } else if (place instanceof CrmSiteMap.Dashboard) {
                    activity = new DashboardActivity(place);
                } else if (place instanceof CrmSiteMap.Report) {
                    activity = new ReportActivity(place);
                } else if (place instanceof CrmSiteMap.Account) {
                    activity = new AccountActivity(place);
                } else if (place instanceof CrmSiteMap.Alert) {
                    activity = new AlertActivity(place);
                } else if (place instanceof CrmSiteMap.Message) {
                    activity = new MessageActivity(place);

                    // - Settings:
                } else if (place instanceof CrmSiteMap.Settings.Content) {
                    if (((AppPlace) place).getArgs().isEmpty()) {
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

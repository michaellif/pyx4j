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
import com.propertyvista.crm.client.activity.crud.building.catalog.ConcessionEditorActivity;
import com.propertyvista.crm.client.activity.crud.building.catalog.ConcessionViewerActivity;
import com.propertyvista.crm.client.activity.crud.building.catalog.FeatureEditorActivity;
import com.propertyvista.crm.client.activity.crud.building.catalog.FeatureViewerActivity;
import com.propertyvista.crm.client.activity.crud.building.catalog.ServiceEditorActivity;
import com.propertyvista.crm.client.activity.crud.building.catalog.ServiceViewerActivity;
import com.propertyvista.crm.client.activity.crud.floorplan.FloorplanEditorActivity;
import com.propertyvista.crm.client.activity.crud.floorplan.FloorplanViewerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.EmployeeEditorActivity;
import com.propertyvista.crm.client.activity.crud.organisation.EmployeeListerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.EmployeeViewerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.PortfolioEditorActivity;
import com.propertyvista.crm.client.activity.crud.organisation.PortfolioListerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.PortfolioViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.FeatureItemTypeEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.FeatureItemTypeViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.PageEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.PageViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.ServiceDictionaryViewActivity;
import com.propertyvista.crm.client.activity.crud.settings.ServiceItemTypeEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.ServiceItemTypeViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.SiteActivity;
import com.propertyvista.crm.client.activity.crud.settings.SiteEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.SiteViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.InquiryEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.InquiryListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.InquiryViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.TenantEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.TenantListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.TenantScreeningEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.TenantScreeningViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.TenantViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lead.AppointmentEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lead.AppointmentListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lead.AppointmentViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lead.LeadEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lead.LeadListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lead.LeadViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lead.ShowingEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lead.ShowingListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lead.ShowingViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lease.LeaseEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lease.LeaseListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lease.LeaseViewerActivity;
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

                } else if (place instanceof CrmSiteMap.Properties.Floorplan) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new FloorplanEditorActivity(place);
                        break;
                    case viewer:
                        activity = new FloorplanViewerActivity(place);
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

// - Marketing-related:
                } else if (place instanceof CrmSiteMap.Properties.Service) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new ServiceEditorActivity(place);
                        break;
                    case viewer:
                        activity = new ServiceViewerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Properties.Feature) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new FeatureEditorActivity(place);
                        break;
                    case viewer:
                        activity = new FeatureViewerActivity(place);
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

// - Tenant-related:
                } else if (place instanceof CrmSiteMap.Tenants.Lead) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new LeadEditorActivity(place);
                        break;
                    case viewer:
                        activity = new LeadViewerActivity(place);
                        break;
                    case lister:
                        activity = new LeadListerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Tenants.Appointment) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new AppointmentEditorActivity(place);
                        break;
                    case viewer:
                        activity = new AppointmentViewerActivity(place);
                        break;
                    case lister:
                        activity = new AppointmentListerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Tenants.Showing) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new ShowingEditorActivity(place);
                        break;
                    case viewer:
                        activity = new ShowingViewerActivity(place);
                        break;
                    case lister:
                        activity = new ShowingListerActivity(place);
                        break;
                    }

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

                } else if (place instanceof CrmSiteMap.Tenants.TenantScreening) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new TenantScreeningEditorActivity(place);
                        break;
                    case viewer:
                        activity = new TenantScreeningViewerActivity(place);
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
// - Organisation-related:
                } else if (place instanceof CrmSiteMap.Organisation.Employee) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new EmployeeEditorActivity(place);
                        break;
                    case viewer:
                        activity = new EmployeeViewerActivity(place);
                        break;
                    case lister:
                        activity = new EmployeeListerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Organisation.Portfolio) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new PortfolioEditorActivity(place);
                        break;
                    case viewer:
                        activity = new PortfolioViewerActivity(place);
                        break;
                    case lister:
                        activity = new PortfolioListerActivity(place);
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
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new SiteEditorActivity(place);
                        break;
                    case viewer:
                        activity = new SiteViewerActivity(place);
                        break;
                    case lister:
                        activity = new SiteActivity(place);
                        break;
                    }
                } else if (place instanceof CrmSiteMap.Settings.Page) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new PageEditorActivity(place);
                        break;
                    case viewer:
                        activity = new PageViewerActivity(place);
                        break;
                    }
                } else if (place instanceof CrmSiteMap.Settings.ServiceDictionary) {
                    activity = new ServiceDictionaryViewActivity(place);
                } else if (place instanceof CrmSiteMap.Settings.ServiceItemType) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new ServiceItemTypeEditorActivity(place);
                        break;
                    case viewer:
                        activity = new ServiceItemTypeViewerActivity(place);
                        break;
                    }
                } else if (place instanceof CrmSiteMap.Settings.FeatureItemType) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new FeatureItemTypeEditorActivity(place);
                        break;
                    case viewer:
                        activity = new FeatureItemTypeViewerActivity(place);
                        break;
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

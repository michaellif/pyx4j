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
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.activity.AccountActivity;
import com.propertyvista.crm.client.activity.AlertActivity;
import com.propertyvista.crm.client.activity.DashboardActivity;
import com.propertyvista.crm.client.activity.MessageActivity;
import com.propertyvista.crm.client.activity.ReportActivity;
import com.propertyvista.crm.client.activity.ResetPasswordActivity;
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
import com.propertyvista.crm.rpc.CrmSiteMap;

public class MainActivityMapper implements ActivityMapper {

    //  Provider<LoginActivity> loginActivityProvider;

    //  Provider<RetrievePasswordActivity> retrievePasswordActivityProvider;

    Provider<ResetPasswordActivity> resetPasswordActivityProvider;

// ----- Other:
    Provider<DashboardActivity> dashboardActivityProvider;

    Provider<ReportActivity> reportActivityProvider;

    Provider<AccountActivity> accountActivityProvider;

    Provider<AlertActivity> alertActivityProvider;

    Provider<MessageActivity> messageActivityProvider;

    @Inject
    public MainActivityMapper(

    final Provider<ResetPasswordActivity> resetPasswordActivityProvider,
/*
 * ----- Other:
 */
    final Provider<DashboardActivity> dashboardActivityProvider,

    final Provider<ReportActivity> reportActivityProvider,

    final Provider<AccountActivity> accountActivityProvider,

    final Provider<AlertActivity> alertActivityProvider,

    final Provider<MessageActivity> messageActivityProvider) {
        super();

        this.resetPasswordActivityProvider = resetPasswordActivityProvider;
// ---- Other:
        this.dashboardActivityProvider = dashboardActivityProvider;
        this.reportActivityProvider = reportActivityProvider;
        this.accountActivityProvider = accountActivityProvider;
        this.alertActivityProvider = alertActivityProvider;
        this.messageActivityProvider = messageActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof CrmSiteMap.ResetPassword) {
            return resetPasswordActivityProvider.get().withPlace((AppPlace) place);

// - Building-related:
        } else if (place instanceof CrmSiteMap.Properties.Buildings) {
            return new BuildingListerActivity(place);
        } else if (place instanceof CrmSiteMap.Viewers.Building) {
            return new BuildingViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.Building) {
            return new BuildingEditorActivity(place);

        } else if (place instanceof CrmSiteMap.Viewers.Elevator) {
            return new ElevatorViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.Elevator) {
            return new ElevatorEditorActivity(place);

        } else if (place instanceof CrmSiteMap.Viewers.Boiler) {
            return new BoilerViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.Boiler) {
            return new BoilerEditorActivity(place);

        } else if (place instanceof CrmSiteMap.Viewers.Roof) {
            return new RoofViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.Roof) {
            return new RoofEditorActivity(place);

        } else if (place instanceof CrmSiteMap.Viewers.Parking) {
            return new ParkingViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.Parking) {
            return new ParkingEditorActivity(place);

        } else if (place instanceof CrmSiteMap.Properties.ParkingSpots) {
            return new ParkingSpotListerActivity(place);
        } else if (place instanceof CrmSiteMap.Viewers.ParkingSpot) {
            return new ParkingSpotViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.Parking) {
            return new ParkingSpotEditorActivity(place);

        } else if (place instanceof CrmSiteMap.Viewers.LockerArea) {
            return new LockerAreaViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.LockerArea) {
            return new LockerAreaEditorActivity(place);

        } else if (place instanceof CrmSiteMap.Properties.Lockers) {
            return new LockerListerActivity(place);
        } else if (place instanceof CrmSiteMap.Viewers.Locker) {
            return new LockerViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.Locker) {
            return new LockerEditorActivity(place);

// - Unit-related:
        } else if (place instanceof CrmSiteMap.Properties.Units) {
            return new UnitListerActivity(place);
        } else if (place instanceof CrmSiteMap.Viewers.Unit) {
            return new UnitViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.Unit) {
            return new UnitEditorActivity(place);

        } else if (place instanceof CrmSiteMap.Viewers.UnitItem) {
            return new UnitItemViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.UnitItem) {
            return new UnitItemEditorActivity(place);

        } else if (place instanceof CrmSiteMap.Viewers.UnitOccupancy) {
            return new UnitOccupancyViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.UnitOccupancy) {
            return new UnitOccupancyEditorActivity(place);

        } else if (place instanceof CrmSiteMap.Viewers.Concession) {
            return new ConcessionViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.Concession) {
            return new ConcessionEditorActivity(place);

// - Tenant-related:
        } else if (place instanceof CrmSiteMap.Tenants.AllTenants) {
            return new TenantListerActivity(place);
        } else if (place instanceof CrmSiteMap.Viewers.Tenant) {
            return new TenantViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.Tenant) {
            return new TenantEditorActivity(place);

        } else if (place instanceof CrmSiteMap.Tenants.Leases) {
            return new LeaseListerActivity(place);
        } else if (place instanceof CrmSiteMap.Viewers.Lease) {
            return new LeaseViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.Lease) {
            return new LeaseEditorActivity(place);

        } else if (place instanceof CrmSiteMap.Tenants.Applications) {
            return new ApplicationListerActivity(place);
        } else if (place instanceof CrmSiteMap.Viewers.Application) {
            return new ApplicationViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.Application) {
            return new ApplicationEditorActivity(place);

        } else if (place instanceof CrmSiteMap.Tenants.Inquiries) {
            return new InquiryListerActivity(place);
        } else if (place instanceof CrmSiteMap.Viewers.Inquiry) {
            return new InquiryViewerActivity(place);
        } else if (place instanceof CrmSiteMap.Editors.Inquiry) {
            return new InquiryEditorActivity(place);

// - Other:
        } else if (place instanceof CrmSiteMap.Dashboard) {
            return dashboardActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Report) {
            return reportActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Account) {
            return accountActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Alert) {
            return alertActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Message) {
            return messageActivityProvider.get().withPlace(place);

// - Settings:
        } else if (place instanceof CrmSiteMap.Settings.Content) {
            if (((AppPlace) place).getArgs().isEmpty()) {
                return new ContentActivity(place);
            } else {
                return new ContentEditorActivity(place);
            }
        }
        //TODO what to do on other place
        return null;
    }
}

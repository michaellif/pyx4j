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
import com.propertyvista.crm.client.activity.editors.ApplicationEditorActivity;
import com.propertyvista.crm.client.activity.editors.BoilerEditorActivity;
import com.propertyvista.crm.client.activity.editors.BuildingEditorActivity;
import com.propertyvista.crm.client.activity.editors.ConcessionEditorActivity;
import com.propertyvista.crm.client.activity.editors.ElevatorEditorActivity;
import com.propertyvista.crm.client.activity.editors.InquiryEditorActivity;
import com.propertyvista.crm.client.activity.editors.LeaseEditorActivity;
import com.propertyvista.crm.client.activity.editors.LockerAreaEditorActivity;
import com.propertyvista.crm.client.activity.editors.LockerEditorActivity;
import com.propertyvista.crm.client.activity.editors.ParkingEditorActivity;
import com.propertyvista.crm.client.activity.editors.ParkingSpotEditorActivity;
import com.propertyvista.crm.client.activity.editors.RoofEditorActivity;
import com.propertyvista.crm.client.activity.editors.TenantEditorActivity;
import com.propertyvista.crm.client.activity.editors.UnitEditorActivity;
import com.propertyvista.crm.client.activity.editors.UnitItemEditorActivity;
import com.propertyvista.crm.client.activity.editors.UnitOccupancyEditorActivity;
import com.propertyvista.crm.client.activity.listers.ApplicationListerActivity;
import com.propertyvista.crm.client.activity.listers.ArrearsListerActivity;
import com.propertyvista.crm.client.activity.listers.BuildingListerActivity;
import com.propertyvista.crm.client.activity.listers.InquiryListerActivity;
import com.propertyvista.crm.client.activity.listers.LeaseListerActivity;
import com.propertyvista.crm.client.activity.listers.LockerListerActivity;
import com.propertyvista.crm.client.activity.listers.ParkingSpotListerActivity;
import com.propertyvista.crm.client.activity.listers.TenantListerActivity;
import com.propertyvista.crm.client.activity.listers.UnitListerActivity;
import com.propertyvista.crm.client.activity.settings.ContentActivity;
import com.propertyvista.crm.client.activity.settings.ContentEditorActivity;
import com.propertyvista.crm.client.activity.viewers.ApplicationViewerActivity;
import com.propertyvista.crm.client.activity.viewers.BoilerViewerActivity;
import com.propertyvista.crm.client.activity.viewers.BuildingViewerActivity;
import com.propertyvista.crm.client.activity.viewers.ConcessionViewerActivity;
import com.propertyvista.crm.client.activity.viewers.ElevatorViewerActivity;
import com.propertyvista.crm.client.activity.viewers.InquiryViewerActivity;
import com.propertyvista.crm.client.activity.viewers.LeaseViewerActivity;
import com.propertyvista.crm.client.activity.viewers.LockerAreaViewerActivity;
import com.propertyvista.crm.client.activity.viewers.LockerViewerActivity;
import com.propertyvista.crm.client.activity.viewers.ParkingSpotViewerActivity;
import com.propertyvista.crm.client.activity.viewers.ParkingViewerActivity;
import com.propertyvista.crm.client.activity.viewers.RoofViewerActivity;
import com.propertyvista.crm.client.activity.viewers.TenantViewerActivity;
import com.propertyvista.crm.client.activity.viewers.UnitItemViewerActivity;
import com.propertyvista.crm.client.activity.viewers.UnitOccupancyViewerActivity;
import com.propertyvista.crm.client.activity.viewers.UnitViewerActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class MainActivityMapper implements ActivityMapper {

    //  Provider<LoginActivity> loginActivityProvider;

    //  Provider<RetrievePasswordActivity> retrievePasswordActivityProvider;

    Provider<ResetPasswordActivity> resetPasswordActivityProvider;

// ----- Building-related:
    Provider<BuildingListerActivity> buildingListerActivityProvider;

    Provider<BuildingViewerActivity> buildingViewerActivityProvider;

    Provider<BuildingEditorActivity> buildingEditorActivityProvider;

    Provider<ElevatorViewerActivity> elevatorViewerActivityProvider;

    Provider<ElevatorEditorActivity> elevatorEditorActivityProvider;

    Provider<BoilerViewerActivity> boilerViewerActivityProvider;

    Provider<BoilerEditorActivity> boilerEditorActivityProvider;

    Provider<RoofViewerActivity> roofViewerActivityProvider;

    Provider<RoofEditorActivity> roofEditorActivityProvider;

    Provider<ParkingViewerActivity> parkingViewerActivityProvider;

    Provider<ParkingEditorActivity> parkingEditorActivityProvider;

    Provider<ParkingSpotListerActivity> parkingSpotListerActivityProvider;

    Provider<ParkingSpotViewerActivity> parkingSpotViewerActivityProvider;

    Provider<ParkingSpotEditorActivity> parkingSpotEditorActivityProvider;

    Provider<LockerAreaViewerActivity> lockerAreaViewerActivityProvider;

    Provider<LockerAreaEditorActivity> lockerAreaEditorActivityProvider;

    Provider<LockerListerActivity> lockerListerActivityProvider;

    Provider<LockerViewerActivity> lockerViewerActivityProvider;

    Provider<LockerEditorActivity> lockerEditorActivityProvider;

// ----- Unit-related:
    Provider<UnitListerActivity> unitListerActivityProvider;

    Provider<UnitViewerActivity> unitViewerActivityProvider;

    Provider<UnitEditorActivity> unitEditorActivityProvider;

    Provider<UnitItemViewerActivity> unitItemViewerActivityProvider;

    Provider<UnitItemEditorActivity> unitItemEditorActivityProvider;

    Provider<UnitOccupancyViewerActivity> unitOccupancyViewerActivityProvider;

    Provider<UnitOccupancyEditorActivity> unitOccupancyEditorActivityProvider;

    Provider<ConcessionViewerActivity> concessionViewerActivityProvider;

    Provider<ConcessionEditorActivity> concessionEditorActivityProvider;

// ----- Tenant-related:
    Provider<TenantListerActivity> tenantListerActivityProvider;

    Provider<TenantViewerActivity> tenantViewerActivityProvider;

    Provider<TenantEditorActivity> tenantEditorActivityProvider;

    Provider<LeaseListerActivity> leaseListerActivityProvider;

    Provider<LeaseViewerActivity> leaseViewerActivityProvider;

    Provider<LeaseEditorActivity> leaseEditorActivityProvider;

    Provider<ApplicationListerActivity> applicationListerActivityProvider;

    Provider<ApplicationViewerActivity> applicationViewerActivityProvider;

    Provider<ApplicationEditorActivity> applicationEditorActivityProvider;

    Provider<InquiryListerActivity> inquiryListerActivityProvider;

    Provider<InquiryViewerActivity> inquiryViewerActivityProvider;

    Provider<InquiryEditorActivity> inquiryEditorActivityProvider;

// ----- Other:
    Provider<ArrearsListerActivity> arrearsListerActivityProvider;

    Provider<DashboardActivity> dashboardActivityProvider;

    Provider<ReportActivity> reportActivityProvider;

    Provider<AccountActivity> accountActivityProvider;

    Provider<AlertActivity> alertActivityProvider;

    Provider<MessageActivity> messageActivityProvider;

// ----- Settings:
    Provider<ContentActivity> contentActivityProvider;

    Provider<ContentEditorActivity> contentEditorActivityProvider;

    @Inject
    public MainActivityMapper(

/*
 * final Provider<LoginActivity> loginActivityProvider,
 * 
 * final Provider<RetrievePasswordActivity> retrievePasswordActivityProvider,
 */

    final Provider<ResetPasswordActivity> resetPasswordActivityProvider,
/*
 * ----- Building-related:
 */
    final Provider<BuildingListerActivity> buildingListerActivityProvider,

    final Provider<BuildingViewerActivity> buildingViewerActivityProvider,

    final Provider<BuildingEditorActivity> buildingEditorActivityProvider,

    final Provider<ElevatorViewerActivity> elevatorViewerActivityProvider,

    final Provider<ElevatorEditorActivity> elevatorEditorActivityProvider,

    final Provider<BoilerViewerActivity> boilerViewerActivityProvider,

    final Provider<BoilerEditorActivity> boilerEditorActivityProvider,

    final Provider<RoofViewerActivity> roofViewerActivityProvider,

    final Provider<RoofEditorActivity> roofEditorActivityProvider,

    final Provider<ParkingViewerActivity> parkingViewerActivityProvider,

    final Provider<ParkingEditorActivity> parkingEditorActivityProvider,

    final Provider<ParkingSpotListerActivity> parkingSpotListerActivityProvider,

    final Provider<ParkingSpotViewerActivity> parkingSpotViewerActivityProvider,

    final Provider<ParkingSpotEditorActivity> parkingSpotEditorActivityProvider,

    final Provider<LockerAreaViewerActivity> lockerAreaViewerActivityProvider,

    final Provider<LockerAreaEditorActivity> lockerAreaEditorActivityProvider,

    final Provider<LockerListerActivity> lockerListerActivityProvider,

    final Provider<LockerViewerActivity> lockerViewerActivityProvider,

    final Provider<LockerEditorActivity> lockerEditorActivityProvider,
/*
 * ----- Unit-related:
 */
    final Provider<UnitListerActivity> unitListerActivityProvider,

    final Provider<UnitViewerActivity> unitViewerActivityProvider,

    final Provider<UnitEditorActivity> unitEditorActivityProvider,

    final Provider<UnitItemViewerActivity> unitItemViewerActivityProvider,

    final Provider<UnitItemEditorActivity> unitItemEditorActivityProvider,

    final Provider<UnitOccupancyViewerActivity> unitOccupancyViewerActivityProvider,

    final Provider<UnitOccupancyEditorActivity> unitOccupancyEditorActivityProvider,

    final Provider<ConcessionViewerActivity> concessionViewerActivityProvider,

    final Provider<ConcessionEditorActivity> concessionEditorActivityProvider,
/*
 * ----- Tenant-related:
 */
    final Provider<TenantListerActivity> tenantListerActivityProvider,

    final Provider<TenantViewerActivity> tenantViewerActivityProvider,

    final Provider<TenantEditorActivity> tenantEditorActivityProvider,

    final Provider<LeaseListerActivity> leaseListerActivityProvider,

    final Provider<LeaseViewerActivity> leaseViewerActivityProvider,

    final Provider<LeaseEditorActivity> leaseEditorActivityProvider,

    final Provider<ApplicationListerActivity> applicationListerActivityProvider,

    final Provider<ApplicationViewerActivity> applicationViewerActivityProvider,

    final Provider<ApplicationEditorActivity> applicationEditorActivityProvider,

    final Provider<InquiryListerActivity> inquiryListerActivityProvider,

    final Provider<InquiryViewerActivity> inquiryViewerActivityProvider,

    final Provider<InquiryEditorActivity> inquiryEditorActivityProvider,
/*
 * ----- Other:
 */
    final Provider<ArrearsListerActivity> arrearsListerActivityProvider,

    final Provider<DashboardActivity> dashboardActivityProvider,

    final Provider<ReportActivity> reportActivityProvider,

    final Provider<AccountActivity> accountActivityProvider,

    final Provider<AlertActivity> alertActivityProvider,

    final Provider<MessageActivity> messageActivityProvider,
/*
 * ----- Settings:
 */
    final Provider<ContentActivity> contentActivityProvider,

    final Provider<ContentEditorActivity> contentEditorActivityProvider) {
        super();

//        this.loginActivityProvider = loginActivityProvider;
//        this.retrievePasswordActivityProvider = retrievePasswordActivityProvider;

        this.resetPasswordActivityProvider = resetPasswordActivityProvider;
// ---- Building-related:
        this.buildingListerActivityProvider = buildingListerActivityProvider;
        this.buildingViewerActivityProvider = buildingViewerActivityProvider;
        this.buildingEditorActivityProvider = buildingEditorActivityProvider;
        this.elevatorViewerActivityProvider = elevatorViewerActivityProvider;
        this.elevatorEditorActivityProvider = elevatorEditorActivityProvider;
        this.boilerViewerActivityProvider = boilerViewerActivityProvider;
        this.boilerEditorActivityProvider = boilerEditorActivityProvider;
        this.roofViewerActivityProvider = roofViewerActivityProvider;
        this.roofEditorActivityProvider = roofEditorActivityProvider;
        this.parkingViewerActivityProvider = parkingViewerActivityProvider;
        this.parkingEditorActivityProvider = parkingEditorActivityProvider;
        this.parkingSpotListerActivityProvider = parkingSpotListerActivityProvider;
        this.parkingSpotViewerActivityProvider = parkingSpotViewerActivityProvider;
        this.parkingSpotEditorActivityProvider = parkingSpotEditorActivityProvider;
        this.lockerAreaViewerActivityProvider = lockerAreaViewerActivityProvider;
        this.lockerAreaEditorActivityProvider = lockerAreaEditorActivityProvider;
        this.lockerListerActivityProvider = lockerListerActivityProvider;
        this.lockerViewerActivityProvider = lockerViewerActivityProvider;
        this.lockerEditorActivityProvider = lockerEditorActivityProvider;
// ---- Unit-related:
        this.unitListerActivityProvider = unitListerActivityProvider;
        this.unitViewerActivityProvider = unitViewerActivityProvider;
        this.unitEditorActivityProvider = unitEditorActivityProvider;
        this.unitItemViewerActivityProvider = unitItemViewerActivityProvider;
        this.unitItemEditorActivityProvider = unitItemEditorActivityProvider;
        this.unitOccupancyViewerActivityProvider = unitOccupancyViewerActivityProvider;
        this.unitOccupancyEditorActivityProvider = unitOccupancyEditorActivityProvider;
        this.concessionViewerActivityProvider = concessionViewerActivityProvider;
        this.concessionEditorActivityProvider = concessionEditorActivityProvider;
// ---- Tenant-related:
        this.tenantListerActivityProvider = tenantListerActivityProvider;
        this.tenantViewerActivityProvider = tenantViewerActivityProvider;
        this.tenantEditorActivityProvider = tenantEditorActivityProvider;
        this.leaseListerActivityProvider = leaseListerActivityProvider;
        this.leaseViewerActivityProvider = leaseViewerActivityProvider;
        this.leaseEditorActivityProvider = leaseEditorActivityProvider;
        this.applicationListerActivityProvider = applicationListerActivityProvider;
        this.applicationViewerActivityProvider = applicationViewerActivityProvider;
        this.applicationEditorActivityProvider = applicationEditorActivityProvider;
        this.inquiryListerActivityProvider = inquiryListerActivityProvider;
        this.inquiryViewerActivityProvider = inquiryViewerActivityProvider;
        this.inquiryEditorActivityProvider = inquiryEditorActivityProvider;
// ---- Other:
        this.arrearsListerActivityProvider = arrearsListerActivityProvider;
        this.dashboardActivityProvider = dashboardActivityProvider;
        this.reportActivityProvider = reportActivityProvider;
        this.accountActivityProvider = accountActivityProvider;
        this.alertActivityProvider = alertActivityProvider;
        this.messageActivityProvider = messageActivityProvider;
// ---- settings:
        this.contentActivityProvider = contentActivityProvider;
        this.contentEditorActivityProvider = contentEditorActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
/*
 * if (place instanceof CrmSiteMap.Login) {
 * return loginActivityProvider.get().withPlace((AppPlace) place);
 * } else if (place instanceof CrmSiteMap.RetrievePassword) {
 * return retrievePasswordActivityProvider.get().withPlace((AppPlace) place);
 * } else
 */     if (place instanceof CrmSiteMap.ResetPassword) {
            return resetPasswordActivityProvider.get().withPlace((AppPlace) place);
// - Building-related:
        } else if (place instanceof CrmSiteMap.Properties.Buildings) {
            return buildingListerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Viewers.Building) {
            return buildingViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Building) {
            return buildingEditorActivityProvider.get().withPlace(place);

        } else if (place instanceof CrmSiteMap.Viewers.Elevator) {
            return elevatorViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Elevator) {
            return elevatorEditorActivityProvider.get().withPlace(place);

        } else if (place instanceof CrmSiteMap.Viewers.Boiler) {
            return boilerViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Boiler) {
            return boilerEditorActivityProvider.get().withPlace(place);

        } else if (place instanceof CrmSiteMap.Viewers.Roof) {
            return roofViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Roof) {
            return roofEditorActivityProvider.get().withPlace(place);

        } else if (place instanceof CrmSiteMap.Viewers.Parking) {
            return parkingViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Parking) {
            return parkingEditorActivityProvider.get().withPlace(place);

        } else if (place instanceof CrmSiteMap.Properties.ParkingSpots) {
            return parkingSpotListerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Viewers.ParkingSpot) {
            return parkingSpotViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Parking) {
            return parkingSpotEditorActivityProvider.get().withPlace(place);

        } else if (place instanceof CrmSiteMap.Viewers.LockerArea) {
            return lockerAreaViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.LockerArea) {
            return lockerAreaEditorActivityProvider.get().withPlace(place);

        } else if (place instanceof CrmSiteMap.Properties.Lockers) {
            return lockerListerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Viewers.Locker) {
            return lockerViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Locker) {
            return lockerEditorActivityProvider.get().withPlace(place);
// - Unit-related:
        } else if (place instanceof CrmSiteMap.Properties.Units) {
            return unitListerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Viewers.Unit) {
            return unitViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Unit) {
            return unitEditorActivityProvider.get().withPlace(place);

        } else if (place instanceof CrmSiteMap.Viewers.UnitItem) {
            return unitItemViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.UnitItem) {
            return unitItemEditorActivityProvider.get().withPlace(place);

        } else if (place instanceof CrmSiteMap.Viewers.UnitOccupancy) {
            return unitOccupancyViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.UnitOccupancy) {
            return unitOccupancyEditorActivityProvider.get().withPlace(place);

        } else if (place instanceof CrmSiteMap.Viewers.Concession) {
            return concessionViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Concession) {
            return concessionEditorActivityProvider.get().withPlace(place);
// - Tenant-related:
        } else if (place instanceof CrmSiteMap.Tenants.AllTenants) {
            return tenantListerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Viewers.Tenant) {
            return tenantViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Tenant) {
            return tenantEditorActivityProvider.get().withPlace(place);

        } else if (place instanceof CrmSiteMap.Tenants.Leases) {
            return leaseListerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Viewers.Lease) {
            return leaseViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Lease) {
            return leaseEditorActivityProvider.get().withPlace(place);

        } else if (place instanceof CrmSiteMap.Tenants.Applications) {
            return applicationListerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Viewers.Application) {
            return applicationViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Application) {
            return applicationEditorActivityProvider.get().withPlace(place);

        } else if (place instanceof CrmSiteMap.Tenants.Inquiries) {
            return inquiryListerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Viewers.Inquiry) {
            return inquiryViewerActivityProvider.get().withPlace(place);
        } else if (place instanceof CrmSiteMap.Editors.Inquiry) {
            return inquiryEditorActivityProvider.get().withPlace(place);
// - Other:
        } else if (place instanceof CrmSiteMap.Properties.Arrears) {
            return arrearsListerActivityProvider.get().withPlace(place);
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
                return contentActivityProvider.get().withPlace(place);
            } else {
                return contentEditorActivityProvider.get().withPlace(place);
            }
        }
        //TODO what to do on other place
        return null;
    }
}

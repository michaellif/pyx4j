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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.AccountEditorActivity;
import com.propertyvista.crm.client.activity.AccountViewerActivity;
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
import com.propertyvista.crm.client.activity.crud.complex.ComplexEditorAcitvity;
import com.propertyvista.crm.client.activity.crud.complex.ComplexListerActivity;
import com.propertyvista.crm.client.activity.crud.complex.ComplexViewerActivity;
import com.propertyvista.crm.client.activity.crud.floorplan.FloorplanEditorActivity;
import com.propertyvista.crm.client.activity.crud.floorplan.FloorplanViewerActivity;
import com.propertyvista.crm.client.activity.crud.maintenance.MaintenanceRequestEditorActivity;
import com.propertyvista.crm.client.activity.crud.maintenance.MaintenanceRequestListerActivity;
import com.propertyvista.crm.client.activity.crud.maintenance.MaintenanceRequestViewerActivity;
import com.propertyvista.crm.client.activity.crud.marketing.lead.AppointmentEditorActivity;
import com.propertyvista.crm.client.activity.crud.marketing.lead.AppointmentListerActivity;
import com.propertyvista.crm.client.activity.crud.marketing.lead.AppointmentViewerActivity;
import com.propertyvista.crm.client.activity.crud.marketing.lead.LeadEditorActivity;
import com.propertyvista.crm.client.activity.crud.marketing.lead.LeadListerActivity;
import com.propertyvista.crm.client.activity.crud.marketing.lead.LeadViewerActivity;
import com.propertyvista.crm.client.activity.crud.marketing.lead.ShowingEditorActivity;
import com.propertyvista.crm.client.activity.crud.marketing.lead.ShowingViewerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.EmployeeEditorActivity;
import com.propertyvista.crm.client.activity.crud.organisation.EmployeeListerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.EmployeeViewerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.PortfolioEditorActivity;
import com.propertyvista.crm.client.activity.crud.organisation.PortfolioListerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.PortfolioViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.PolicyManagementActivity;
import com.propertyvista.crm.client.activity.crud.settings.content.PageEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.content.PageViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.content.SiteActivity;
import com.propertyvista.crm.client.activity.crud.settings.content.SiteEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.content.SiteViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.dictionary.FeatureItemTypeEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.dictionary.FeatureItemTypeViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.dictionary.ProductDictionaryViewActivity;
import com.propertyvista.crm.client.activity.crud.settings.dictionary.ServiceItemTypeEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.dictionary.ServiceItemTypeViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.role.CrmRoleEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.role.CrmRoleListerActivity;
import com.propertyvista.crm.client.activity.crud.settings.role.CrmRoleViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.ChargeCodeEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.ChargeCodeListerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.ChargeCodeViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.GlCodeEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.GlCodeListerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.GlCodeViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.TaxEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.TaxListerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.TaxViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.TenantEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.TenantListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.TenantViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.application.ApplicationEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.application.ApplicationListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.application.ApplicationViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.application.MasterApplicationEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.application.MasterApplicationListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.application.MasterApplicationViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lease.LeaseEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lease.LeaseListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lease.LeaseViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lease.bill.BillEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.lease.bill.BillViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.screening.EquifaxResultViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.screening.PersonScreeningEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.screening.PersonScreeningViewerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.screening.guarantor.GuarantorEditorActivity;
import com.propertyvista.crm.client.activity.crud.tenant.screening.guarantor.GuarantorListerActivity;
import com.propertyvista.crm.client.activity.crud.tenant.screening.guarantor.GuarantorViewerActivity;
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
import com.propertyvista.crm.client.activity.policies.applicationdocumentation.ApplicationDocumentationPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.applicationdocumentation.ApplicationDocumentationPolicyListerActivicty;
import com.propertyvista.crm.client.activity.policies.applicationdocumentation.ApplicationDocumentationPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.charges.ChargePolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.charges.ChargePolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.charges.ChargePolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.emailtemplates.EmailTemplatesPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.emailtemplates.EmailTemplatesPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.emailtemplates.EmailTemplatesPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.leaseterms.LeaseTermsPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.leaseterms.LeaseTermsPolicyListerActivicty;
import com.propertyvista.crm.client.activity.policies.leaseterms.LeaseTermsPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.misc.MiscPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.misc.MiscPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.misc.MiscPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.pet.PetPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.pet.PetPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.pet.PetPolicyViewerActivity;
import com.propertyvista.crm.client.activity.report.ReportEditorActivity;
import com.propertyvista.crm.client.activity.report.ReportManagementActivity;
import com.propertyvista.crm.client.activity.report.ReportViewActivity;
import com.propertyvista.crm.client.activity.security.PasswordChangeActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.misc.EquifaxResult;

public class MainActivityMapper implements AppActivityMapper {

    public MainActivityMapper() {
    }

    //TODO create a better two directional mapping
    public static CrudAppPlace getCrudAppPlace(Class<? extends IEntity> entityClass) {

        if (entityClass.equals(Complex.class)) {
            return AppSite.getHistoryMapper().createPlace(CrmSiteMap.Properties.Complex.class);
        } else if (entityClass.equals(Building.class)) {
            return AppSite.getHistoryMapper().createPlace(CrmSiteMap.Properties.Building.class);
        } else if (entityClass.equals(Floorplan.class)) {
            return AppSite.getHistoryMapper().createPlace(CrmSiteMap.Properties.Floorplan.class);
        } else if (entityClass.equals(AptUnit.class)) {
            return AppSite.getHistoryMapper().createPlace(CrmSiteMap.Properties.Unit.class);
        } else if (entityClass.equals(LockerArea.class)) {
            return AppSite.getHistoryMapper().createPlace(CrmSiteMap.Properties.LockerArea.class);
        } else if (entityClass.equals(Parking.class)) {
            return AppSite.getHistoryMapper().createPlace(CrmSiteMap.Properties.Parking.class);
        } else if (entityClass.equals(Roof.class)) {
            return AppSite.getHistoryMapper().createPlace(CrmSiteMap.Properties.Roof.class);
        } else if (entityClass.equals(MaintenanceRequest.class)) {
            return AppSite.getHistoryMapper().createPlace(CrmSiteMap.Properties.MaintenanceRequest.class);

        } else if (entityClass.equals(Tenant.class)) {
            return AppSite.getHistoryMapper().createPlace(CrmSiteMap.Tenants.Tenant.class);
        } else if (entityClass.equals(Guarantor.class)) {
            return AppSite.getHistoryMapper().createPlace(CrmSiteMap.Tenants.Guarantor.class);
        } else if (entityClass.equals(Lease.class)) {
            return AppSite.getHistoryMapper().createPlace(CrmSiteMap.Tenants.Lease.class);
        } else if (entityClass.equals(MasterApplication.class)) {
            return AppSite.getHistoryMapper().createPlace(CrmSiteMap.Tenants.MasterApplication.class);
        } else if (entityClass.equals(EquifaxResult.class)) {
            return AppSite.getHistoryMapper().createPlace(CrmSiteMap.Tenants.EquifaxResult.class);
        }

        return null;
    }

    @Override
    public void obtainActivity(final Place place, final AsyncCallback<Activity> callback) {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                Activity activity = null;
// - Building-related:
                if (place instanceof CrmSiteMap.Properties.Building) {
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

                } else if (place instanceof Marketing.Lead) {
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

                } else if (place instanceof Marketing.Appointment) {
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

                } else if (place instanceof Marketing.Showing) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new ShowingEditorActivity(place);
                        break;
                    case viewer:
                        activity = new ShowingViewerActivity(place);
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

                } else if (place instanceof CrmSiteMap.Tenants.Guarantor) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new GuarantorEditorActivity(place);
                        break;
                    case viewer:
                        activity = new GuarantorViewerActivity(place);
                        break;
                    case lister:
                        activity = new GuarantorListerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Tenants.Screening) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new PersonScreeningEditorActivity(place);
                        break;
                    case viewer:
                        activity = new PersonScreeningViewerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Tenants.Bill) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new BillEditorActivity(place);
                        break;
                    case viewer:
                        activity = new BillViewerActivity(place);
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

                } else if (place instanceof CrmSiteMap.Tenants.MasterApplication) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new MasterApplicationEditorActivity(place);
                        break;
                    case viewer:
                        activity = new MasterApplicationViewerActivity(place);
                        break;
                    case lister:
                        activity = new MasterApplicationListerActivity(place);
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

                } else if (place instanceof CrmSiteMap.Tenants.EquifaxResult) {
                    switch (((CrudAppPlace) place).getType()) {
                    case viewer:
                        activity = new EquifaxResultViewerActivity(place);
                        break;
                    }

// - Organisation-related:
                } else if (place instanceof CrmSiteMap.Organization.Employee) {
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

                } else if (place instanceof CrmSiteMap.Organization.Portfolio) {
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

                } else if (place instanceof CrmSiteMap.PasswordChange) {
                    activity = new PasswordChangeActivity(place);
// Reports:
                } else if (place instanceof CrmSiteMap.Report) {
                    activity = new ReportViewActivity(place);
                } else if (place instanceof CrmSiteMap.Report.Edit) {
                    activity = new ReportEditorActivity(place);
                } else if (place instanceof CrmSiteMap.Report.Management) {
                    activity = new ReportManagementActivity(place);
// Dashboards:
                } else if (place instanceof CrmSiteMap.Dashboard) {
                    activity = new DashboardViewActivity(place);
                } else if (place instanceof CrmSiteMap.Dashboard.Edit) {
                    activity = new DashboardEditorActivity(place);
                } else if (place instanceof CrmSiteMap.Dashboard.Management) {
                    activity = new DashboardManagementActivity(place);
// - Other:
                } else if (place instanceof CrmSiteMap.Account) {
                    // the service that the Account related activites use doesn't care about the 'id' arg,
                    // but nevertheless the base "Activity" classes need it, so we just add a value let them be happy
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        ((CrmSiteMap.Account) place).formEditorPlace(new Key(1));
                        activity = new AccountEditorActivity(place);
                        break;
                    case viewer:
                    case lister: /* this is required hack, don't remove!!! */
                        ((CrmSiteMap.Account) place).formViewerPlace(new Key(1));
                        activity = new AccountViewerActivity(place);
                        break;
                    }

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

                } else if (place instanceof CrmSiteMap.Settings.Policy) {
                    activity = new PolicyManagementActivity(place);
                } else if (place instanceof CrmSiteMap.Settings.ProductDictionary) {
                    activity = new ProductDictionaryViewActivity(place);
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

                } else if (place instanceof CrmSiteMap.Settings.UserRole) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new CrmRoleEditorActivity(place);
                        break;
                    case viewer:
                        activity = new CrmRoleViewerActivity(place);
                        break;
                    case lister:
                        activity = new CrmRoleListerActivity(place);
                        break;

                    }

                } else if (place instanceof CrmSiteMap.Settings.Tax) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new TaxEditorActivity(place);
                        break;
                    case viewer:
                        activity = new TaxViewerActivity(place);
                        break;
                    case lister:
                        activity = new TaxListerActivity(place);
                        break;

                    }

                } else if (place instanceof CrmSiteMap.Settings.GlCode) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new GlCodeEditorActivity(place);
                        break;
                    case viewer:
                        activity = new GlCodeViewerActivity(place);
                        break;
                    case lister:
                        activity = new GlCodeListerActivity(place);
                        break;

                    }

                } else if (place instanceof CrmSiteMap.Settings.ChargeCode) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new ChargeCodeEditorActivity(place);
                        break;
                    case viewer:
                        activity = new ChargeCodeViewerActivity(place);
                        break;
                    case lister:
                        activity = new ChargeCodeListerActivity(place);
                        break;

                    }
// - Complex:
                } else if (place instanceof CrmSiteMap.Properties.Complex) {
                    switch (((CrudAppPlace) place).getType()) {
                    case editor:
                        activity = new ComplexEditorAcitvity(place);
                        break;
                    case viewer:
                        activity = new ComplexViewerActivity(place);
                        break;
                    case lister:
                        activity = new ComplexListerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Properties.MaintenanceRequest) {
                    switch (((CrudAppPlace) place).getType()) {
                    case lister:
                        activity = new MaintenanceRequestListerActivity(place);
                        break;
                    case viewer:
                        activity = new MaintenanceRequestViewerActivity(place);
                        break;
                    case editor:
                        activity = new MaintenanceRequestEditorActivity(place);
                        break;
                    }

// - Policies:
                } else if (place instanceof CrmSiteMap.Settings.Policies.ApplicationDocumentation) {
                    switch (((CrudAppPlace) place).getType()) {
                    case lister:
                        activity = new ApplicationDocumentationPolicyListerActivicty(place);
                        break;
                    case editor:
                        activity = new ApplicationDocumentationPolicyEditorActivity(place);
                        break;
                    case viewer:
                        activity = new ApplicationDocumentationPolicyViewerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Settings.Policies.LeaseTerms) {
                    switch (((CrudAppPlace) place).getType()) {
                    case lister:
                        activity = new LeaseTermsPolicyListerActivicty(place);
                        break;
                    case editor:
                        activity = new LeaseTermsPolicyEditorActivity(place);
                        break;
                    case viewer:
                        activity = new LeaseTermsPolicyViewerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Settings.Policies.PetPolicy) {
                    switch (((CrudAppPlace) place).getType()) {
                    case lister:
                        activity = new PetPolicyListerActivity(place);
                        break;
                    case editor:
                        activity = new PetPolicyEditorActivity(place);
                        break;
                    case viewer:
                        activity = new PetPolicyViewerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Settings.Policies.EmailTemplates) {
                    switch (((CrudAppPlace) place).getType()) {
                    case lister:
                        activity = new EmailTemplatesPolicyListerActivity(place);
                        break;
                    case editor:
                        activity = new EmailTemplatesPolicyEditorActivity(place);
                        break;
                    case viewer:
                        activity = new EmailTemplatesPolicyViewerActivity(place);
                        break;
                    }

                } else if (place instanceof CrmSiteMap.Settings.Policies.Misc) {
                    switch (((CrudAppPlace) place).getType()) {
                    case lister:
                        activity = new MiscPolicyListerActivity(place);
                        break;
                    case editor:
                        activity = new MiscPolicyEditorActivity(place);
                        break;
                    case viewer:
                        activity = new MiscPolicyViewerActivity(place);
                        break;
                    }
                } else if (place instanceof CrmSiteMap.Settings.Policies.Charges) {
                    switch (((CrudAppPlace) place).getType()) {
                    case lister:
                        activity = new ChargePolicyListerActivity(place);
                        break;
                    case editor:
                        activity = new ChargePolicyEditorActivity(place);
                        break;
                    case viewer:
                        activity = new ChargePolicyViewerActivity(place);
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

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
import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.AccountEditorActivity;
import com.propertyvista.crm.client.activity.AccountViewerActivity;
import com.propertyvista.crm.client.activity.AlertActivity;
import com.propertyvista.crm.client.activity.MessageActivity;
import com.propertyvista.crm.client.activity.crud.billing.BillViewerActivity;
import com.propertyvista.crm.client.activity.crud.billing.PaymentEditorActivity;
import com.propertyvista.crm.client.activity.crud.billing.PaymentListerActivity;
import com.propertyvista.crm.client.activity.crud.billing.PaymentViewerActivity;
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
import com.propertyvista.crm.client.activity.crud.customer.guarantor.GuarantorEditorActivity;
import com.propertyvista.crm.client.activity.crud.customer.guarantor.GuarantorListerActivity;
import com.propertyvista.crm.client.activity.crud.customer.guarantor.GuarantorViewerActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.AppointmentEditorActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.AppointmentListerActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.AppointmentViewerActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.LeadEditorActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.LeadListerActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.LeadViewerActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.ShowingEditorActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.ShowingViewerActivity;
import com.propertyvista.crm.client.activity.crud.customer.screening.EquifaxResultViewerActivity;
import com.propertyvista.crm.client.activity.crud.customer.screening.PersonScreeningEditorActivity;
import com.propertyvista.crm.client.activity.crud.customer.screening.PersonScreeningViewerActivity;
import com.propertyvista.crm.client.activity.crud.customer.tenant.FutureTenantListerActivity;
import com.propertyvista.crm.client.activity.crud.customer.tenant.PastTenantListerActivity;
import com.propertyvista.crm.client.activity.crud.customer.tenant.TenantEditorActivity;
import com.propertyvista.crm.client.activity.crud.customer.tenant.TenantListerActivity;
import com.propertyvista.crm.client.activity.crud.customer.tenant.TenantViewerActivity;
import com.propertyvista.crm.client.activity.crud.floorplan.FloorplanEditorActivity;
import com.propertyvista.crm.client.activity.crud.floorplan.FloorplanViewerActivity;
import com.propertyvista.crm.client.activity.crud.lease.LeaseEditorActivity;
import com.propertyvista.crm.client.activity.crud.lease.LeaseListerActivity;
import com.propertyvista.crm.client.activity.crud.lease.LeaseViewerActivity;
import com.propertyvista.crm.client.activity.crud.lease.PastLeaseListerActivity;
import com.propertyvista.crm.client.activity.crud.lease.application.LeaseApplicationEditorActivity;
import com.propertyvista.crm.client.activity.crud.lease.application.LeaseApplicationListerActivity;
import com.propertyvista.crm.client.activity.crud.lease.application.LeaseApplicationViewerActivity;
import com.propertyvista.crm.client.activity.crud.maintenance.MaintenanceRequestEditorActivity;
import com.propertyvista.crm.client.activity.crud.maintenance.MaintenanceRequestListerActivity;
import com.propertyvista.crm.client.activity.crud.maintenance.MaintenanceRequestViewerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.EmployeeEditorActivity;
import com.propertyvista.crm.client.activity.crud.organisation.EmployeeListerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.EmployeeViewerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.PortfolioEditorActivity;
import com.propertyvista.crm.client.activity.crud.organisation.PortfolioListerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.PortfolioViewerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.VendorEditorActivity;
import com.propertyvista.crm.client.activity.crud.organisation.VendorListerActivity;
import com.propertyvista.crm.client.activity.crud.organisation.VendorViewerActivity;
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
import com.propertyvista.crm.client.activity.crud.settings.tax.GlCodeCategoryEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.GlCodeCategoryListerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.GlCodeCategoryViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.LeaseAdjustmentReasonEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.LeaseAdjustmentReasonListerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.LeaseAdjustmentReasonViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.TaxEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.TaxListerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.TaxViewerActivity;
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
import com.propertyvista.crm.client.activity.policies.backgroundcheck.BackgroundCheckPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.backgroundcheck.BackgroundCheckPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.backgroundcheck.BackgroundCheckPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.deposit.DepositPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.deposit.DepositPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.deposit.DepositPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.emailtemplates.EmailTemplatesPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.emailtemplates.EmailTemplatesPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.emailtemplates.EmailTemplatesPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.idassignment.IdAssignmentPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.idassignment.IdAssignmentPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.idassignment.IdAssignmentPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.leaseadjustment.LeaseAdjustmentPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.leaseadjustment.LeaseAdjustmentPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.leaseadjustment.LeaseAdjustmentPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.leasebilling.LeaseBillingPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.leasebilling.LeaseBillingPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.leasebilling.LeaseBillingPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.leaseterms.LeaseTermsPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.leaseterms.LeaseTermsPolicyListerActivicty;
import com.propertyvista.crm.client.activity.policies.leaseterms.LeaseTermsPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.misc.MiscPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.misc.MiscPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.misc.MiscPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.pet.PetPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.pet.PetPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.pet.PetPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.producttax.ProductTaxPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.producttax.ProductTaxPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.producttax.ProductTaxPolicyViewerActivity;
import com.propertyvista.crm.client.activity.report.ReportEditorActivity;
import com.propertyvista.crm.client.activity.report.ReportManagementActivity;
import com.propertyvista.crm.client.activity.report.ReportViewActivity;
import com.propertyvista.crm.client.activity.security.PasswordChangeActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;

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

                    // - Building-related:
                    if (place instanceof CrmSiteMap.Properties.Building) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new BuildingEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new BuildingViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new BuildingListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Properties.Elevator) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ElevatorEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ElevatorViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Properties.Boiler) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new BoilerEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new BoilerViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Properties.Roof) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new RoofEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new RoofViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Properties.Parking) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ParkingEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ParkingViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Properties.ParkingSpot) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ParkingSpotEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ParkingSpotViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new ParkingSpotListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Properties.LockerArea) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new LockerAreaEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LockerAreaViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Properties.Locker) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new LockerEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LockerViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new LockerListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Properties.Floorplan) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new FloorplanEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new FloorplanViewerActivity(crudPlace);
                            break;
                        }

                        // - Unit-related:
                    } else if (place instanceof CrmSiteMap.Properties.Unit) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new UnitEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new UnitViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new UnitListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Properties.UnitItem) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new UnitItemEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new UnitItemViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Properties.UnitOccupancy) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new UnitOccupancyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new UnitOccupancyViewerActivity(crudPlace);
                            break;
                        }

                        // - Marketing-related:
                    } else if (place instanceof CrmSiteMap.Properties.Service) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ServiceEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ServiceViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Properties.Feature) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new FeatureEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new FeatureViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Properties.Concession) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ConcessionEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ConcessionViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof Marketing.Lead) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new LeadEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LeadViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new LeadListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof Marketing.Appointment) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new AppointmentEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new AppointmentViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new AppointmentListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof Marketing.Showing) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ShowingEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ShowingViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Marketing.FutureTenant) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new FutureTenantListerActivity(crudPlace);
                            break;
                        }

                        // - Tenant-related:
                    } else if (place instanceof CrmSiteMap.Tenants.Tenant) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new TenantEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new TenantViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new TenantListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Tenants.Guarantor) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new GuarantorEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new GuarantorViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new GuarantorListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Tenants.Screening) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new PersonScreeningEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PersonScreeningViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Tenants.Bill) {
                        switch (crudPlace.getType()) {
                        case viewer:
                            activity = new BillViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Tenants.Payment) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new PaymentEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PaymentViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new PaymentListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Tenants.Lease) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new LeaseEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LeaseViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new LeaseListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Tenants.LeaseApplication) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new LeaseApplicationEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LeaseApplicationViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new LeaseApplicationListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Tenants.EquifaxResult) {
                        switch (crudPlace.getType()) {
                        case viewer:
                            activity = new EquifaxResultViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Tenants.MaintenanceRequest) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new MaintenanceRequestListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new MaintenanceRequestViewerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new MaintenanceRequestEditorActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Tenants.PastTenant) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new PastTenantListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Tenants.PastLease) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new PastLeaseListerActivity(crudPlace);
                            break;
                        }

                        // - Organization-related:
                    } else if (place instanceof CrmSiteMap.Organization.Employee) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new EmployeeEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new EmployeeViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new EmployeeListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Organization.Portfolio) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new PortfolioEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PortfolioViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new PortfolioListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Organization.Vendor) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new VendorEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new VendorViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new VendorListerActivity(crudPlace);
                            break;
                        }
                    } else if (place instanceof CrmSiteMap.Report.Edit) {
                        activity = new ReportEditorActivity(crudPlace);
                    } else if (place instanceof CrmSiteMap.Dashboard.Edit) {
                        activity = new DashboardEditorActivity(crudPlace);
                    } else if (place instanceof CrmSiteMap.Account) {
                        // the service that the Account related activities use doesn't care about the 'id' arg,
                        // but nevertheless the base "Activity" classes need it, so we just add a value let them be happy
                        switch (crudPlace.getType()) {
                        case editor:
                            ((CrmSiteMap.Account) place).formEditorPlace(new Key(1));
                            activity = new AccountEditorActivity(crudPlace);
                            break;
                        case viewer:
                        case lister: /* this is required hack, don't remove!!! */
                            ((CrmSiteMap.Account) place).formViewerPlace(new Key(1));
                            activity = new AccountViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Settings.Content) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new SiteEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new SiteViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new SiteActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Settings.Page) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new PageEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PageViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Settings.ServiceItemType) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ServiceItemTypeEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ServiceItemTypeViewerActivity(crudPlace);
                            break;
                        }
                    } else if (place instanceof CrmSiteMap.Settings.FeatureItemType) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new FeatureItemTypeEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new FeatureItemTypeViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Settings.UserRole) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new CrmRoleEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new CrmRoleViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new CrmRoleListerActivity(crudPlace);
                            break;

                        }

                    } else if (place instanceof CrmSiteMap.Settings.Tax) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new TaxEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new TaxViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new TaxListerActivity(crudPlace);
                            break;

                        }

                    } else if (place instanceof CrmSiteMap.Settings.GlCodeCategory) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new GlCodeCategoryEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new GlCodeCategoryViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new GlCodeCategoryListerActivity(crudPlace);
                            break;

                        }

                    } else if (place instanceof CrmSiteMap.Settings.LeaseAdjustmentReason) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new LeaseAdjustmentReasonEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LeaseAdjustmentReasonViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new LeaseAdjustmentReasonListerActivity(crudPlace);
                            break;

                        }
                        // - Complex:
                    } else if (place instanceof CrmSiteMap.Properties.Complex) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ComplexEditorAcitvity(crudPlace);
                            break;
                        case viewer:
                            activity = new ComplexViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new ComplexListerActivity(crudPlace);
                            break;
                        }

                        // - Policies:
                    } else if (place instanceof CrmSiteMap.Settings.Policies.ApplicationDocumentation) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new ApplicationDocumentationPolicyListerActivicty(crudPlace);
                            break;
                        case editor:
                            activity = new ApplicationDocumentationPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ApplicationDocumentationPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Settings.Policies.LeaseTerms) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new LeaseTermsPolicyListerActivicty(crudPlace);
                            break;
                        case editor:
                            activity = new LeaseTermsPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LeaseTermsPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Settings.Policies.PetPolicy) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new PetPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new PetPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PetPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Settings.Policies.EmailTemplates) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new EmailTemplatesPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new EmailTemplatesPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new EmailTemplatesPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Settings.Policies.Misc) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new MiscPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new MiscPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new MiscPolicyViewerActivity(crudPlace);
                            break;
                        }
                    } else if (place instanceof CrmSiteMap.Settings.Policies.ProductTax) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new ProductTaxPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new ProductTaxPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ProductTaxPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Settings.Policies.LeaseAdjustment) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new LeaseAdjustmentPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new LeaseAdjustmentPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LeaseAdjustmentPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Settings.Policies.Deposits) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new DepositPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new DepositPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new DepositPolicyViewerActivity(crudPlace);
                            break;
                        }
                    } else if (place instanceof CrmSiteMap.Settings.Policies.BackgroundCheck) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new BackgroundCheckPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new BackgroundCheckPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new BackgroundCheckPolicyViewerActivity(crudPlace);
                            break;
                        }
                    } else if (place instanceof CrmSiteMap.Settings.Policies.LeaseBilling) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new LeaseBillingPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new LeaseBillingPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LeaseBillingPolicyViewerActivity(crudPlace);
                            break;
                        }
                    } else if (place instanceof CrmSiteMap.Settings.Policies.IdAssignment) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new IdAssignmentPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new IdAssignmentPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new IdAssignmentPolicyViewerActivity(crudPlace);
                            break;
                        }
                        // Reports:
                    } else if (place instanceof CrmSiteMap.Report.Management) {
                        activity = new ReportManagementActivity(place);
                        // Dashboards:
                    } else if (place instanceof CrmSiteMap.Dashboard.Management) {
                        activity = new DashboardManagementActivity(place);

                    } // CRUD APP PLACE IF ENDS HERE

                } else if (place instanceof CrmSiteMap.Report) {
                    activity = new ReportViewActivity(place);
                } else if (place instanceof CrmSiteMap.Dashboard) {
                    activity = new DashboardViewActivity(place);

                } else if (place instanceof CrmSiteMap.PasswordChange) {
                    activity = new PasswordChangeActivity(place);
// - Other:
                } else if (place instanceof CrmSiteMap.Alert) {
                    activity = new AlertActivity(place);
                } else if (place instanceof CrmSiteMap.Message) {
                    activity = new MessageActivity(place);

// - Settings:
                } else if (place instanceof CrmSiteMap.Settings.ProductDictionary) {
                    activity = new ProductDictionaryViewActivity(place);
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

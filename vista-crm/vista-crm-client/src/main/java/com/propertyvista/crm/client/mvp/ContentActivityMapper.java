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
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.ReportsAppPlace;

import com.propertyvista.crm.client.activity.MessageActivity;
import com.propertyvista.crm.client.activity.NotificationsActivity;
import com.propertyvista.crm.client.activity.RuntimeErrorActivity;
import com.propertyvista.crm.client.activity.crud.account.AccountEditorActivity;
import com.propertyvista.crm.client.activity.crud.account.AccountViewerActivity;
import com.propertyvista.crm.client.activity.crud.account.LoginAttemptsListerActivity;
import com.propertyvista.crm.client.activity.crud.account.MandatoryAccountRecoveryOptionsSetupActivity;
import com.propertyvista.crm.client.activity.crud.auditrecords.CrmAuditRecordsListerActivity;
import com.propertyvista.crm.client.activity.crud.billing.adjustment.LeaseAdjustmentEditorActivity;
import com.propertyvista.crm.client.activity.crud.billing.adjustment.LeaseAdjustmentViewerActivity;
import com.propertyvista.crm.client.activity.crud.billing.bill.BillViewerActivity;
import com.propertyvista.crm.client.activity.crud.billing.cycle.BillingCycleBillListerActivity;
import com.propertyvista.crm.client.activity.crud.billing.cycle.BillingCycleLeaseListerActivity;
import com.propertyvista.crm.client.activity.crud.billing.cycle.BillingCycleListerActivity;
import com.propertyvista.crm.client.activity.crud.billing.cycle.BillingCycleViewerActivity;
import com.propertyvista.crm.client.activity.crud.billing.payment.PaymentEditorActivity;
import com.propertyvista.crm.client.activity.crud.billing.payment.PaymentListerActivity;
import com.propertyvista.crm.client.activity.crud.billing.payment.PaymentViewerActivity;
import com.propertyvista.crm.client.activity.crud.billing.transfer.AggregatedTransferListerActivity;
import com.propertyvista.crm.client.activity.crud.billing.transfer.AggregatedTransferViewerActivity;
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
import com.propertyvista.crm.client.activity.crud.complex.ComplexEditorActivity;
import com.propertyvista.crm.client.activity.crud.complex.ComplexListerActivity;
import com.propertyvista.crm.client.activity.crud.complex.ComplexViewerActivity;
import com.propertyvista.crm.client.activity.crud.customer.creditcheck.CustomerCreditCheckLongReportViewerActivity;
import com.propertyvista.crm.client.activity.crud.customer.guarantor.FormerGuarantorListerActivity;
import com.propertyvista.crm.client.activity.crud.customer.guarantor.GuarantorEditorActivity;
import com.propertyvista.crm.client.activity.crud.customer.guarantor.GuarantorListerActivity;
import com.propertyvista.crm.client.activity.crud.customer.guarantor.GuarantorViewerActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.AppointmentEditorActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.AppointmentViewerActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.LeadEditorActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.LeadListerActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.LeadViewerActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.ShowingEditorActivity;
import com.propertyvista.crm.client.activity.crud.customer.lead.ShowingViewerActivity;
import com.propertyvista.crm.client.activity.crud.customer.screening.CustomerScreeningEditorActivity;
import com.propertyvista.crm.client.activity.crud.customer.screening.CustomerScreeningViewerActivity;
import com.propertyvista.crm.client.activity.crud.customer.tenant.FormerTenantListerActivity;
import com.propertyvista.crm.client.activity.crud.customer.tenant.PotentialTenantListerActivity;
import com.propertyvista.crm.client.activity.crud.customer.tenant.TenantEditorActivity;
import com.propertyvista.crm.client.activity.crud.customer.tenant.TenantListerActivity;
import com.propertyvista.crm.client.activity.crud.customer.tenant.TenantViewerActivity;
import com.propertyvista.crm.client.activity.crud.floorplan.FloorplanEditorActivity;
import com.propertyvista.crm.client.activity.crud.floorplan.FloorplanViewerActivity;
import com.propertyvista.crm.client.activity.crud.lease.FormerLeaseListerActivity;
import com.propertyvista.crm.client.activity.crud.lease.LeaseListerActivity;
import com.propertyvista.crm.client.activity.crud.lease.LeaseViewerActivity;
import com.propertyvista.crm.client.activity.crud.lease.application.LeaseApplicationListerActivity;
import com.propertyvista.crm.client.activity.crud.lease.application.LeaseApplicationViewerActivity;
import com.propertyvista.crm.client.activity.crud.lease.common.LeaseTermEditorActivity;
import com.propertyvista.crm.client.activity.crud.lease.common.LeaseTermViewerActivity;
import com.propertyvista.crm.client.activity.crud.lease.common.deposit.DepositLifecycleEditorActivity;
import com.propertyvista.crm.client.activity.crud.lease.common.deposit.DepositLifecycleViewerActivity;
import com.propertyvista.crm.client.activity.crud.lease.financial.InvoiceCreditViewerActivity;
import com.propertyvista.crm.client.activity.crud.lease.financial.InvoiceDebitViewerActivity;
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
import com.propertyvista.crm.client.activity.crud.settings.arcode.ARCodeEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.arcode.ARCodeListerActivity;
import com.propertyvista.crm.client.activity.crud.settings.arcode.ARCodeViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.creditcheck.CustomerCreditCheckListerActivity;
import com.propertyvista.crm.client.activity.crud.settings.creditcheck.CustomerCreditCheckViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.ils.ILSConfigDefaultActivity;
import com.propertyvista.crm.client.activity.crud.settings.ils.ILSConfigEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.ils.ILSConfigViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.merchantaccount.MerchantAccountEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.merchantaccount.MerchantAccountListerActivity;
import com.propertyvista.crm.client.activity.crud.settings.merchantaccount.MerchantAccountViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.role.CrmRoleEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.role.CrmRoleListerActivity;
import com.propertyvista.crm.client.activity.crud.settings.role.CrmRoleViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.GlCodeCategoryEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.GlCodeCategoryListerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.GlCodeCategoryViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.TaxEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.TaxListerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tax.TaxViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.tenantsecurity.TenantSecurityViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.branding.BrandingActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.branding.BrandingEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.branding.BrandingViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.content.CityIntroPageEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.content.CityIntroPageViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.content.ContentActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.content.ContentEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.content.ContentViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.content.HomePageGadgetEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.content.HomePageGadgetViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.content.PageEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.content.PageViewerActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.general.GeneralActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.general.GeneralEditorActivity;
import com.propertyvista.crm.client.activity.crud.settings.website.general.GeneralViewerActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitEditorActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitItemEditorActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitItemViewerActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitListerActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitOccupancyEditorActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitOccupancyViewerActivity;
import com.propertyvista.crm.client.activity.crud.unit.UnitViewerActivity;
import com.propertyvista.crm.client.activity.dashboard.DashboardActivity;
import com.propertyvista.crm.client.activity.dashboard.DashboardManagementEditorActivity;
import com.propertyvista.crm.client.activity.dashboard.DashboardManagementListerActivity;
import com.propertyvista.crm.client.activity.dashboard.DashboardManagementViewerActivity;
import com.propertyvista.crm.client.activity.login.LoginActivity;
import com.propertyvista.crm.client.activity.login.LoginWithTokenActivity;
import com.propertyvista.crm.client.activity.policies.applicationdocumentation.ApplicationDocumentationPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.applicationdocumentation.ApplicationDocumentationPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.applicationdocumentation.ApplicationDocumentationPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.ar.ARPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.ar.ARPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.ar.ARPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.autopaychange.AutoPayPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.autopaychange.AutoPayPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.autopaychange.AutoPayPolicyViewerActivity;
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
import com.propertyvista.crm.client.activity.policies.leasetermination.LeaseTerminationPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.leasetermination.LeaseTerminationPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.leasetermination.LeaseTerminationPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.leaseterms.LegalDocumentationPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.leaseterms.LegalDocumentationPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.leaseterms.LegalDocumentationPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.misc.DatesPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.misc.DatesPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.misc.DatesPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.n4.N4PolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.n4.N4PolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.n4.N4PolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.paymenttypeselection.PaymentTypeSelectionPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.paymenttypeselection.PaymentTypeSelectionPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.paymenttypeselection.PaymentTypeSelectionPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.pet.PetPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.pet.PetPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.pet.PetPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.producttax.ProductTaxPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.producttax.ProductTaxPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.producttax.ProductTaxPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.restrictions.RestrictionsPolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.restrictions.RestrictionsPolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.restrictions.RestrictionsPolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.tenantinsurance.TenantInsurancePolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.tenantinsurance.TenantInsurancePolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.tenantinsurance.TenantInsurancePolicyViewerActivity;
import com.propertyvista.crm.client.activity.policies.yardiinterface.YardiInterfacePolicyEditorActivity;
import com.propertyvista.crm.client.activity.policies.yardiinterface.YardiInterfacePolicyListerActivity;
import com.propertyvista.crm.client.activity.policies.yardiinterface.YardiInterfacePolicyViewerActivity;
import com.propertyvista.crm.client.activity.profile.PmcPaymentMethodsEditorActivity;
import com.propertyvista.crm.client.activity.profile.PmcPaymentMethodsViewerActivity;
import com.propertyvista.crm.client.activity.reports.AutoPayChangesReportActivity;
import com.propertyvista.crm.client.activity.reports.AutoPayReviewActivity;
import com.propertyvista.crm.client.activity.reports.AvailabilityReportActivity;
import com.propertyvista.crm.client.activity.reports.CustomerCreditCheckReportActivity;
import com.propertyvista.crm.client.activity.reports.EftReportActivity;
import com.propertyvista.crm.client.activity.reports.EftVarianceReportActivity;
import com.propertyvista.crm.client.activity.reports.ResidentInsuranceReportActivity;
import com.propertyvista.crm.client.activity.security.PasswordChangeActivity;
import com.propertyvista.crm.client.activity.security.PasswordResetActivity;
import com.propertyvista.crm.client.activity.security.PasswordResetRequestActivity;
import com.propertyvista.crm.client.activity.wizard.creditcheck.CreditCheckActivity;
import com.propertyvista.crm.client.activity.wizard.creditcheck.CreditCheckStatusViewerActivity;
import com.propertyvista.crm.client.activity.wizard.creditcheck.CreditCheckWizardActivity;
import com.propertyvista.crm.client.activity.wizard.onlinepayment.OnlinePaymentWizardActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Financial;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Security;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Website;
import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;
import com.propertyvista.domain.reports.CustomerCreditCheckReportMetadata;
import com.propertyvista.domain.reports.EftReportMetadata;
import com.propertyvista.domain.reports.EftVarianceReportMetadata;
import com.propertyvista.domain.reports.ResidentInsuranceReportMetadata;

public class ContentActivityMapper implements AppActivityMapper {

    public ContentActivityMapper() {
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
                    if (crudPlace instanceof CrmSiteMap.Properties.Complex) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ComplexEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ComplexViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new ComplexListerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Properties.Building) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Properties.Elevator) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ElevatorEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ElevatorViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Properties.Boiler) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new BoilerEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new BoilerViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Properties.Roof) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new RoofEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new RoofViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Properties.Parking) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ParkingEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ParkingViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Properties.ParkingSpot) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Properties.LockerArea) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new LockerAreaEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LockerAreaViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Properties.Locker) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Properties.Floorplan) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new FloorplanEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new FloorplanViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                        // - Unit-related:
                    } else if (crudPlace instanceof CrmSiteMap.Properties.Unit) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Properties.UnitItem) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new UnitItemEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new UnitItemViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Properties.UnitOccupancy) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new UnitOccupancyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new UnitOccupancyViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                        // - Marketing-related:
                    } else if (crudPlace instanceof CrmSiteMap.Properties.Service) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ServiceEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ServiceViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Properties.Feature) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new FeatureEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new FeatureViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Properties.Concession) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ConcessionEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ConcessionViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

// - Marketing-related:
                    } else if (crudPlace instanceof Marketing.Lead) {
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

                    } else if (crudPlace instanceof Marketing.Appointment) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new AppointmentEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new AppointmentViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof Marketing.Showing) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ShowingEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ShowingViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Marketing.PotentialTenant) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new PotentialTenantListerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

// - Tenant-related:
                    } else if (crudPlace instanceof CrmSiteMap.Tenants.Tenant) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Tenants.Guarantor) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Tenants.FormerGuarantor) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new FormerGuarantorListerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Tenants.Screening) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new CustomerScreeningEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new CustomerScreeningViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (place instanceof CrmSiteMap.Tenants.Lease) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new LeaseListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LeaseViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Tenants.LeaseTerm) {
                        switch (crudPlace.getType()) {
                        case viewer:
                            activity = new LeaseTermViewerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new LeaseTermEditorActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Tenants.LeaseApplication) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new LeaseApplicationListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LeaseApplicationViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Tenants.Lease.InvoiceCredit) {
                        switch (crudPlace.getType()) {
                        default:
                            activity = new InvoiceCreditViewerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Tenants.Lease.InvoiceDebit) {
                        switch (crudPlace.getType()) {
                        default:
                            activity = new InvoiceDebitViewerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Tenants.MaintenanceRequest) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Tenants.FormerTenant) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new FormerTenantListerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Tenants.FormerLease) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new FormerLeaseListerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Tenants.CustomerCreditCheckLongReport) {
                        switch (crudPlace.getType()) {
                        case viewer:
                            activity = new CustomerCreditCheckLongReportViewerActivity(crudPlace);
                            break;
                        default:
                            if (ApplicationMode.isDevelopment()) {
                                throw new Error("view is not defined");
                            }
                            break;
                        }

// - Financial-related:

                    } else if (crudPlace instanceof CrmSiteMap.Finance.BillingCycle) {
                        switch (crudPlace.getType()) {
                        case viewer:
                            activity = new BillingCycleViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new BillingCycleListerActivity(crudPlace);
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Finance.BillingCycle.Bills) {
                        activity = new BillingCycleBillListerActivity(crudPlace);

                    } else if (crudPlace instanceof CrmSiteMap.Finance.BillingCycle.Leases) {
                        activity = new BillingCycleLeaseListerActivity(crudPlace);

                    } else if (crudPlace instanceof CrmSiteMap.Finance.Bill) {
                        switch (crudPlace.getType()) {
                        case viewer:
                            activity = new BillViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Finance.Payment) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new PaymentEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PaymentViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new PaymentListerActivity(crudPlace);
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Finance.LeaseDeposit) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new DepositLifecycleEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new DepositLifecycleViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Finance.LeaseAdjustment) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new LeaseAdjustmentEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LeaseAdjustmentViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Finance.AggregatedTransfer) {
                        switch (crudPlace.getType()) {
                        case viewer:
                            activity = new AggregatedTransferViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new AggregatedTransferListerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

// - Organization-related:
                    } else if (crudPlace instanceof CrmSiteMap.Organization.Employee) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Organization.Portfolio) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Organization.Vendor) {
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

// - Dashboard-related:
                    } else if (crudPlace instanceof CrmSiteMap.Dashboard.Manage) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new DashboardManagementEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new DashboardManagementViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new DashboardManagementListerActivity(crudPlace);
                        default:

                            break;
                        }

// - Settings:
                    } else if (crudPlace instanceof CrmSiteMap.Account.AccountData) {
                        // the service that the Account related activities use doesn't care about the 'id' arg,
                        // but nevertheless the base "Activity" classes need it, so we just add a value let them be happy
                        switch (crudPlace.getType()) {
                        case editor:
                            ((CrmSiteMap.Account.AccountData) place).formEditorPlace(new Key(1));
                            activity = new AccountEditorActivity(crudPlace);
                            break;
                        case viewer:
                            ((CrmSiteMap.Account.AccountData) place).formViewerPlace(new Key(1));
                            activity = new AccountViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Report.CustomerCreditCheck) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new CustomerCreditCheckListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new CustomerCreditCheckViewerActivity(crudPlace);
                            break;
                        case editor:
                        default:
                            if (ApplicationMode.isDevelopment()) {
                                throw new Error("view is not defined");
                            }
                            break;
                        }

                    } else if (crudPlace instanceof Website.General) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new GeneralEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new GeneralViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new GeneralActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof Website.Branding) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new BrandingEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new BrandingViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new BrandingActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof Website.Content) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ContentEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ContentViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new ContentActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof Website.Content.Page) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new PageEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PageViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof Website.Content.HomePageGadgets) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new HomePageGadgetEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new HomePageGadgetViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof Website.Content.CityIntroPage) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new CityIntroPageEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new CityIntroPageViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof Financial.ARCode) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new ARCodeListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new ARCodeEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ARCodeViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof Security.UserRole) {
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

                    } else if (crudPlace instanceof Financial.MerchantAccount) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new MerchantAccountEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new MerchantAccountViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new MerchantAccountListerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof Financial.Tax) {
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

                    } else if (crudPlace instanceof Financial.GlCodeCategory) {
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
// - Policies:
                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.ApplicationDocumentation) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new ApplicationDocumentationPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new ApplicationDocumentationPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ApplicationDocumentationPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.LegalDocumentation) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new LegalDocumentationPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new LegalDocumentationPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LegalDocumentationPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.N4) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new N4PolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new N4PolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new N4PolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.Pet) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.EmailTemplates) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.Dates) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new DatesPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new DatesPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new DatesPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.Restrictions) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new RestrictionsPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new RestrictionsPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new RestrictionsPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.ProductTax) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.LeaseTermination) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new LeaseTerminationPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new LeaseTerminationPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new LeaseTerminationPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.LeaseAdjustment) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.Deposits) {
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
                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.BackgroundCheck) {
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
                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.Billing) {
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
                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.IdAssignment) {
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

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.AR) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new ARPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new ARPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ARPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.TenantInsurance) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new TenantInsurancePolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new TenantInsurancePolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new TenantInsurancePolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.PaymentTypeSelection) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new PaymentTypeSelectionPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new PaymentTypeSelectionPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PaymentTypeSelectionPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.AutoPay) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new AutoPayPolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new AutoPayPolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new AutoPayPolicyViewerActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Policies.YardiInterface) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new YardiInterfacePolicyListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new YardiInterfacePolicyEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new YardiInterfacePolicyViewerActivity(crudPlace);
                            break;
                        }

// - Security          
                    } else if (crudPlace instanceof CrmSiteMap.Account.LoginAttemptsLog) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new LoginAttemptsListerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Security.AuditRecords) {
                        activity = new CrmAuditRecordsListerActivity(place);

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Settings.CreditCheck.Status) {
                        activity = new CreditCheckStatusViewerActivity(crudPlace);

                    } else if (crudPlace instanceof Administration.Settings.ILSConfig) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new ILSConfigEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new ILSConfigViewerActivity(crudPlace);
                            break;
                        default:
                            activity = new ILSConfigDefaultActivity(crudPlace);
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Profile.PaymentMethods) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new PmcPaymentMethodsEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PmcPaymentMethodsViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (crudPlace instanceof CrmSiteMap.Administration.Security.TenantSecurity) {
                        activity = new TenantSecurityViewerActivity(place);

                        // reports
                    } else if (place instanceof CrmSiteMap.Reports.AutoPayChanges) {
                        activity = new AutoPayChangesReportActivity((ReportsAppPlace<AutoPayChangesReportMetadata>) place);
                    } else if (place instanceof CrmSiteMap.Reports.Availability) {
                        activity = new AvailabilityReportActivity((ReportsAppPlace<AvailabilityReportMetadata>) place);
                    } else if (place instanceof CrmSiteMap.Reports.CustomerCreditCheck) {
                        activity = new CustomerCreditCheckReportActivity((ReportsAppPlace<CustomerCreditCheckReportMetadata>) place);
                    } else if (place instanceof CrmSiteMap.Reports.Eft) {
                        activity = new EftReportActivity((ReportsAppPlace<EftReportMetadata>) place);
                    } else if (place instanceof CrmSiteMap.Reports.EftVariance) {
                        activity = new EftVarianceReportActivity((ReportsAppPlace<EftVarianceReportMetadata>) place);
                    } else if (place instanceof CrmSiteMap.Reports.ResidentInsurance) {
                        activity = new ResidentInsuranceReportActivity((ReportsAppPlace<ResidentInsuranceReportMetadata>) place);

                    } // CRUD APP PLACE IF ENDS HERE

                    // Dashboard related stuff again
                } else if (place instanceof CrmSiteMap.Dashboard.View) {
                    activity = new DashboardActivity((CrmSiteMap.Dashboard.View) place);
                } else if (place instanceof CrmSiteMap.AutoPayReview) {
                    activity = new AutoPayReviewActivity((AppPlace) place);
                } else if (place instanceof Administration.Settings.CreditCheck) {
                    activity = new CreditCheckActivity();
                } else if (place instanceof Administration.Settings.CreditCheck.Setup) {
                    activity = new CreditCheckWizardActivity((Administration.Settings.CreditCheck.Setup) place);
                } else if (place instanceof Administration.Settings.OnlinePaymentSetup) {
                    activity = new OnlinePaymentWizardActivity((Administration.Settings.OnlinePaymentSetup) place);
                } else if (place instanceof CrmSiteMap.PasswordChange) {
                    activity = new PasswordChangeActivity(place);

// - Other:
                } else if (place instanceof CrmSiteMap.Login) {
                    activity = new LoginActivity(place);
                } else if (place instanceof CrmSiteMap.PasswordResetRequest) {
                    activity = new PasswordResetRequestActivity(place);
                } else if (place instanceof CrmSiteMap.PasswordReset) {
                    activity = new PasswordResetActivity(place);
                } else if (place instanceof CrmSiteMap.Account.AccountRecoveryOptionsRequired) {
                    activity = new MandatoryAccountRecoveryOptionsSetupActivity();
                } else if (place instanceof CrmSiteMap.LoginWithToken) {
                    activity = new LoginWithTokenActivity(place);
                } else if (place instanceof CrmSiteMap.RuntimeError) {
                    activity = new RuntimeErrorActivity((CrmSiteMap.RuntimeError) place);

                } else if (place instanceof CrmSiteMap.Notifications) {
                    activity = new NotificationsActivity(place);
                } else if (place instanceof CrmSiteMap.Message) {
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

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import com.pyx4j.entity.rpc.ReferenceDataService;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.essentials.rpc.download.DownloadableService;
import com.pyx4j.essentials.rpc.report.ReportServices;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessService;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.ServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.crm.rpc.services.CityIntroPageCrudService;
import com.propertyvista.crm.rpc.services.FeedbackService;
import com.propertyvista.crm.rpc.services.HomePageGadgetCrudService;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.crm.rpc.services.MaintenanceRequestPictureUploadService;
import com.propertyvista.crm.rpc.services.MediaUploadBuildingService;
import com.propertyvista.crm.rpc.services.MediaUploadFloorplanService;
import com.propertyvista.crm.rpc.services.NoteAttachmentUploadService;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.crm.rpc.services.PmcDocumentFileUploadService;
import com.propertyvista.crm.rpc.services.PmcTermsOfServiceService;
import com.propertyvista.crm.rpc.services.UpdateUploadService;
import com.propertyvista.crm.rpc.services.admin.ARCodeCrudService;
import com.propertyvista.crm.rpc.services.admin.CrmRoleCrudService;
import com.propertyvista.crm.rpc.services.admin.CustomerCreditCheckCrudService;
import com.propertyvista.crm.rpc.services.admin.GlCodeCategoryCrudService;
import com.propertyvista.crm.rpc.services.admin.MerchantAccountCrudService;
import com.propertyvista.crm.rpc.services.admin.PmcCompanyInfoCrudService;
import com.propertyvista.crm.rpc.services.admin.PmcPaymentMethodsCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteBrandingCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteContentCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteGeneralCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.crm.rpc.services.admin.TaxCrudService;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.crm.rpc.services.billing.BillPreviewService;
import com.propertyvista.crm.rpc.services.billing.BillingCycleBillListService;
import com.propertyvista.crm.rpc.services.billing.BillingCycleCrudService;
import com.propertyvista.crm.rpc.services.billing.BillingCycleLeaseListService;
import com.propertyvista.crm.rpc.services.billing.BillingExecutionService;
import com.propertyvista.crm.rpc.services.billing.LeaseAdjustmentCrudService;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.crm.rpc.services.breadcrumbs.BreadcrumbsService;
import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.crm.rpc.services.building.ComplexCrudService;
import com.propertyvista.crm.rpc.services.building.FloorplanCrudService;
import com.propertyvista.crm.rpc.services.building.LandlordCrudService;
import com.propertyvista.crm.rpc.services.building.LandlordMediaUploadService;
import com.propertyvista.crm.rpc.services.building.LockerAreaCrudService;
import com.propertyvista.crm.rpc.services.building.LockerCrudService;
import com.propertyvista.crm.rpc.services.building.ParkingCrudService;
import com.propertyvista.crm.rpc.services.building.ParkingSpotCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.ConcessionCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.FeatureCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.ServiceCrudService;
import com.propertyvista.crm.rpc.services.building.communityevent.CommunityEventCrudService;
import com.propertyvista.crm.rpc.services.building.mech.BoilerCrudService;
import com.propertyvista.crm.rpc.services.building.mech.ElevatorCrudService;
import com.propertyvista.crm.rpc.services.building.mech.RoofCrudService;
import com.propertyvista.crm.rpc.services.customer.ActiveGuarantorCrudService;
import com.propertyvista.crm.rpc.services.customer.ActiveTenantCrudService;
import com.propertyvista.crm.rpc.services.customer.CustomerCreditCheckLongReportService;
import com.propertyvista.crm.rpc.services.customer.CustomerPictureCrmUploadService;
import com.propertyvista.crm.rpc.services.customer.ExportTenantsService;
import com.propertyvista.crm.rpc.services.customer.FormerGuarantorCrudService;
import com.propertyvista.crm.rpc.services.customer.FormerTenantCrudService;
import com.propertyvista.crm.rpc.services.customer.GuarantorCrudService;
import com.propertyvista.crm.rpc.services.customer.InsuranceCertificateScanCrmUploadService;
import com.propertyvista.crm.rpc.services.customer.PreauthorizedPaymentsVisorService;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.crm.rpc.services.customer.TenantPadFileDownloadService;
import com.propertyvista.crm.rpc.services.customer.TenantPadFileUploadService;
import com.propertyvista.crm.rpc.services.customer.TenantPasswordChangeService;
import com.propertyvista.crm.rpc.services.customer.lead.AppointmentCrudService;
import com.propertyvista.crm.rpc.services.customer.lead.LeadCrudService;
import com.propertyvista.crm.rpc.services.customer.lead.ShowingCrudService;
import com.propertyvista.crm.rpc.services.customer.screening.CustomerScreeningCrudService;
import com.propertyvista.crm.rpc.services.customer.screening.CustomerScreeningVersionService;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.crm.rpc.services.dashboard.GadgetMetadataService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ApplicationsGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.BuildingResidentInsuranceListService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.CollectionsGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.DelinquentLeaseListService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeadsAndRentalsGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeaseExpirationGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.MaintenanceGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.NoticesGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentRecordsGadgetListService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentRecordsSummaryGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitAvailabilityStatusListService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitAvailabilitySummaryGadgetService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitTurnoverAnalysisGadgetService;
import com.propertyvista.crm.rpc.services.financial.AggregatedTransferCrudService;
import com.propertyvista.crm.rpc.services.financial.AutoPayCrudService;
import com.propertyvista.crm.rpc.services.financial.AutoPayReviewService;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchCrudService;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchDepositSlipPrintService;
import com.propertyvista.crm.rpc.services.financial.MoneyInToolService;
import com.propertyvista.crm.rpc.services.financial.PaymentRecordListService;
import com.propertyvista.crm.rpc.services.financial.RevealAccountNumberService;
import com.propertyvista.crm.rpc.services.lease.IdentificationDocumentCrmUploadService;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.LeaseTermAgreementDocumentUploadService;
import com.propertyvista.crm.rpc.services.lease.LeaseTermBlankAgreementDocumentDownloadService;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.ProofOfAssetDocumentCrmUploadService;
import com.propertyvista.crm.rpc.services.lease.ProofOfEmploymentDocumentCrmUploadService;
import com.propertyvista.crm.rpc.services.lease.common.DepositLifecycleCrudService;
import com.propertyvista.crm.rpc.services.lease.common.LeaseTermCrudService;
import com.propertyvista.crm.rpc.services.lease.financial.InvoiceCreditCrudService;
import com.propertyvista.crm.rpc.services.lease.financial.InvoiceDebitCrudService;
import com.propertyvista.crm.rpc.services.legal.L1FormDataReviewWizardService;
import com.propertyvista.crm.rpc.services.legal.N4CreateBatchService;
import com.propertyvista.crm.rpc.services.legal.N4DownloadToolService;
import com.propertyvista.crm.rpc.services.notes.NotesAndAttachmentsCrudService;
import com.propertyvista.crm.rpc.services.organization.CrmUserService;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.crm.rpc.services.organization.EmployeeSignatureUploadService;
import com.propertyvista.crm.rpc.services.organization.ManagedCrmUserService;
import com.propertyvista.crm.rpc.services.organization.PortfolioCrudService;
import com.propertyvista.crm.rpc.services.organization.SelectCrmRoleListService;
import com.propertyvista.crm.rpc.services.organization.VendorCrudService;
import com.propertyvista.crm.rpc.services.policies.CrmPolicyRetrieveService;
import com.propertyvista.crm.rpc.services.policies.emailtemplates.EmailTemplateManagerService;
import com.propertyvista.crm.rpc.services.policies.policy.BackgroundCheckPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.EmailTemplatesPolicyCrudService;
import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;
import com.propertyvista.crm.rpc.services.reports.CrmReportsService;
import com.propertyvista.crm.rpc.services.reports.CrmReportsSettingsPersistenceService;
import com.propertyvista.crm.rpc.services.security.CrmAccountRecoveryOptionsUserService;
import com.propertyvista.crm.rpc.services.security.CrmAuditRecordsListerService;
import com.propertyvista.crm.rpc.services.security.CrmLoginAttemptsListerService;
import com.propertyvista.crm.rpc.services.security.CrmPasswordChangeUserService;
import com.propertyvista.crm.rpc.services.security.CrmPasswordResetService;
import com.propertyvista.crm.rpc.services.selections.SelectBuildingListService;
import com.propertyvista.crm.rpc.services.selections.SelectBuildingUtilityListService;
import com.propertyvista.crm.rpc.services.selections.SelectCityListService;
import com.propertyvista.crm.rpc.services.selections.SelectConcessionListService;
import com.propertyvista.crm.rpc.services.selections.SelectCustomerListService;
import com.propertyvista.crm.rpc.services.selections.SelectEmployeeListService;
import com.propertyvista.crm.rpc.services.selections.SelectFeatureListService;
import com.propertyvista.crm.rpc.services.selections.SelectFloorplanListService;
import com.propertyvista.crm.rpc.services.selections.SelectGlCodeListService;
import com.propertyvista.crm.rpc.services.selections.SelectLeaseAdjustmentReasonListService;
import com.propertyvista.crm.rpc.services.selections.SelectLeaseTermListService;
import com.propertyvista.crm.rpc.services.selections.SelectPortfolioListService;
import com.propertyvista.crm.rpc.services.selections.SelectProductCodeListService;
import com.propertyvista.crm.rpc.services.selections.SelectProvinceListService;
import com.propertyvista.crm.rpc.services.selections.SelectTaxListService;
import com.propertyvista.crm.rpc.services.selections.SelectTenantListService;
import com.propertyvista.crm.rpc.services.selections.SelectUnitListService;
import com.propertyvista.crm.rpc.services.selections.version.ConcessionVersionService;
import com.propertyvista.crm.rpc.services.selections.version.FeatureVersionService;
import com.propertyvista.crm.rpc.services.selections.version.LeaseTermVersionService;
import com.propertyvista.crm.rpc.services.selections.version.ServiceVersionService;
import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.crm.rpc.services.unit.UnitItemCrudService;
import com.propertyvista.crm.rpc.services.unit.UnitOccupancyCrudService;
import com.propertyvista.crm.rpc.services.unit.UnitOccupancyManagerService;
import com.propertyvista.crm.rpc.services.vista2pmc.CreditCheckStatusCrudService;
import com.propertyvista.crm.rpc.services.vista2pmc.CreditCheckStatusService;
import com.propertyvista.crm.rpc.services.vista2pmc.CreditCheckWizardService;
import com.propertyvista.crm.rpc.services.vista2pmc.ILSConfigCrudService;
import com.propertyvista.crm.rpc.services.vista2pmc.OnlinePaymentWizardService;
import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BuildingAgingBuckets;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.LeaseAgingBuckets;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.legal.n4.N4LegalLetter;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.note.NoteAttachment;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;
import com.propertyvista.domain.property.Landlord;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.VistaDataAccessBehavior;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.insurance.GeneralInsuranceCertificate;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.operations.domain.security.AuditRecord;
import com.propertyvista.portal.rpc.shared.services.CreditCardValidationService;
import com.propertyvista.shared.config.VistaDemo;

public class VistaCrmAccessControlList extends ServletContainerAclBuilder {

    // TODO Change this if you want to make it work temporary. Build will fail!
    private static final boolean allowAllDuringDevelopment = false;

    private static final boolean allowAllEntityDuringDevelopment = true;

    public VistaCrmAccessControlList() {

        if (allowAllDuringDevelopment || VistaDemo.isDemo()) {
            // Debug
            grant(VistaBasicBehavior.CRM, new IServiceExecutePermission("*"));
            grant(VistaBasicBehavior.CRM, new ServiceExecutePermission("*"));
            grant(VistaBasicBehavior.CRM, new EntityPermission("*", EntityPermission.ALL));
            grant(VistaBasicBehavior.CRM, new EntityPermission("*", EntityPermission.READ));
        }

        if (allowAllEntityDuringDevelopment) {
            grant(VistaBasicBehavior.CRM, new EntityPermission("*", EntityPermission.ALL));
            grant(VistaBasicBehavior.CRM, new EntityPermission("*", EntityPermission.READ));
        }

        grant(VistaCrmBehavior.PropertyVistaSupport, new IServiceExecutePermission("*"));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(DeferredProcessService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ReferenceDataService.class));
        grant(VistaBasicBehavior.CRM, new ServiceExecutePermission(ReportServices.class, "*"));

        grant(new IServiceExecutePermission(CrmAuthenticationService.class));
        grant(VistaBasicBehavior.CRMPasswordChangeRequired, new IServiceExecutePermission(CrmPasswordResetService.class));

        grant(VistaApplication.crm, new IServiceExecutePermission(FeedbackService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmPolicyRetrieveService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(BreadcrumbsService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(PmcTermsOfServiceService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(DownloadableService.class));

// - Reports:
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmReportsService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmReportsSettingsPersistenceService.class));

// - Legal:
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(N4CreateBatchService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(N4DownloadToolService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(L1FormDataReviewWizardService.class));

// - Financial:
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(AutoPayCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(AutoPayReviewService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(MoneyInToolService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(MoneyInBatchCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(MoneyInBatchDepositSlipPrintService.class));

// - Dashboard:
        // we want owners (dashboard creator) to have full access to dashboards they own, and other users only read-only access and only for shared.
        grant(VistaBasicBehavior.CRM, new EntityPermission(DashboardMetadata.class, new DashboardOwnerInstanceAccess(), EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new EntityPermission(DashboardMetadata.class, new DashboardUserInstanceAccess(), EntityPermission.READ));
        grant(VistaBasicBehavior.CRM, new DashboardDatasetAccessRule(), DashboardMetadata.class);

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(DashboardMetadataService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(DashboardMetadataCrudService.class));

// - Gadgets:
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(GadgetMetadataService.class));

        // TODO define correct behaviors
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ApplicationsGadgetService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ArrearsGadgetService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ArrearsReportService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(UnitTurnoverAnalysisGadgetService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(UnitAvailabilitySummaryGadgetService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(UnitAvailabilityStatusListService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CollectionsGadgetService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(DelinquentLeaseListService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LeadsAndRentalsGadgetService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LeaseExpirationGadgetService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(MaintenanceGadgetService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(NoticesGadgetService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(PaymentRecordsSummaryGadgetService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(PaymentRecordsGadgetListService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(BuildingResidentInsuranceListService.class));

// - Building-related:
        grant(VistaBasicBehavior.CRM, new EntityPermission(Complex.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ComplexCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Landlord.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LandlordCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LandlordMediaUploadService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Building.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(BuildingCrudService.class));

        grant(VistaCrmBehavior.Tenants, new IServiceExecutePermission(SelectBuildingListService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Elevator.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ElevatorCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Boiler.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(BoilerCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Roof.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(RoofCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Parking.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ParkingCrudService.class));
        grant(VistaBasicBehavior.CRM, new EntityPermission(ParkingSpot.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ParkingSpotCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(LockerArea.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LockerAreaCrudService.class));
        grant(VistaBasicBehavior.CRM, new EntityPermission(Locker.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LockerCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Floorplan.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(FloorplanCrudService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(SelectFloorplanListService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(SelectBuildingUtilityListService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(CommunityEvent.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CommunityEventCrudService.class));

        grant(VistaCrmBehavior.PropertyManagement, new IServiceExecutePermission(UpdateUploadService.class));
        grant(VistaCrmBehavior.PropertyVistaSupport, new IServiceExecutePermission(UpdateUploadService.class));

// - Unit-related:
        grant(VistaBasicBehavior.CRM, new EntityPermission(AptUnit.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(UnitCrudService.class));
        grant(VistaBasicBehavior.CRM, new EntityPermission(AptUnitItem.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(UnitItemCrudService.class));
        grant(VistaBasicBehavior.CRM, new EntityPermission(AptUnitOccupancySegment.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(UnitOccupancyCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(UnitOccupancyManagerService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(SelectUnitListService.class));

// - Tenant-related:
        grant(VistaBasicBehavior.CRM, new EntityPermission(Lead.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LeadCrudService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ExportTenantsService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Appointment.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(AppointmentCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Showing.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ShowingCrudService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(TenantCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ActiveTenantCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(FormerTenantCrudService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(SelectTenantListService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(GuarantorCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ActiveGuarantorCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(FormerGuarantorCrudService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Customer.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new EntityPermission(LeaseTermTenant.class, EntityPermission.ALL));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(SelectCustomerListService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(CustomerScreening.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CustomerScreeningCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CustomerScreeningVersionService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Lease.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LeaseViewerCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LeaseApplicationViewerCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ProofOfEmploymentDocumentCrmUploadService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ProofOfAssetDocumentCrmUploadService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(IdentificationDocumentCrmUploadService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LeaseTermBlankAgreementDocumentDownloadService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LeaseTermAgreementDocumentUploadService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(LeaseTerm.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LeaseTermCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LeaseTermVersionService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(InvoiceCredit.class, EntityPermission.READ));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(InvoiceCreditCrudService.class));
        grant(VistaBasicBehavior.CRM, new EntityPermission(InvoiceDebit.class, EntityPermission.READ));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(InvoiceDebitCrudService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(SelectLeaseTermListService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(NoteAttachmentUploadService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(LeaseAdjustmentCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(DepositLifecycleCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(PaymentCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CreditCardValidationService.class));

        grant(VistaCrmBehavior.Tenants, new IServiceExecutePermission(MaintenanceCrudService.class));
        grant(VistaCrmBehavior.Tenants, new IServiceExecutePermission(TenantPasswordChangeService.class));

        grant(VistaCrmBehavior.PropertyVistaSupport, new IServiceExecutePermission(TenantPadFileDownloadService.class));
        grant(VistaCrmBehavior.PropertyVistaSupport, new IServiceExecutePermission(TenantPadFileUploadService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(PreauthorizedPaymentsVisorService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CustomerPictureCrmUploadService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(InsuranceCertificateScanCrmUploadService.class));

// - Billing
        grant(VistaCrmBehavior.Billing, new EntityPermission(Bill.class, EntityPermission.READ));
        grant(VistaCrmBehavior.Billing, new IServiceExecutePermission(BillCrudService.class));
        grant(VistaCrmBehavior.Billing, new IServiceExecutePermission(BillingExecutionService.class));
        grant(VistaCrmBehavior.Billing, new IServiceExecutePermission(BillingCycleCrudService.class));
        grant(VistaCrmBehavior.Billing, new IServiceExecutePermission(BillingCycleBillListService.class));
        grant(VistaCrmBehavior.Billing, new IServiceExecutePermission(BillingCycleLeaseListService.class));

        grant(VistaCrmBehavior.Tenants, new IServiceExecutePermission(BillPreviewService.class));

        grant(VistaCrmBehavior.OrganizationFinancial, VistaCrmBehavior.AggregatedTransfer);
        grant(VistaCrmBehavior.AggregatedTransfer, new IServiceExecutePermission(AggregatedTransferCrudService.class));
        grant(VistaCrmBehavior.AggregatedTransfer, new IServiceExecutePermission(PaymentRecordListService.class));
        grant(VistaCrmBehavior.Billing, new IServiceExecutePermission(PaymentRecordListService.class));

        grant(VistaCrmBehavior.Billing, new IServiceExecutePermission(RevealAccountNumberService.class));

// - Service-related:
        grant(VistaBasicBehavior.CRM, new EntityPermission(Service.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ServiceCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ServiceVersionService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Feature.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(FeatureCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(FeatureVersionService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Concession.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ConcessionCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ConcessionVersionService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(SelectFeatureListService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(SelectConcessionListService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(SelectLeaseAdjustmentReasonListService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(SelectProvinceListService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(SelectCityListService.class));

// - Organization:
        grant(VistaBasicBehavior.CRM, new EntityPermission(Employee.class, EntityPermission.READ));
        grant(VistaCrmBehavior.Organization, new EntityPermission(Employee.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(EmployeeCrudService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(SelectEmployeeListService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(EmployeeSignatureUploadService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Portfolio.class, EntityPermission.READ));
        grant(VistaCrmBehavior.Organization, new EntityPermission(Portfolio.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(PortfolioCrudService.class));

        grant(VistaCrmBehavior.Organization, new IServiceExecutePermission(SelectPortfolioListService.class));
        grant(VistaCrmBehavior.Organization, new IServiceExecutePermission(SelectCrmRoleListService.class));

        grant(VistaCrmBehavior.Organization, new IServiceExecutePermission(ManagedCrmUserService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmLoginAttemptsListerService.class));

// -- Crm Users, Self management
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmPasswordChangeUserService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmUserService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmAccountRecoveryOptionsUserService.class));
        grant(VistaBasicBehavior.CRMSetupAccountRecoveryOptionsRequired, new IServiceExecutePermission(CrmAccountRecoveryOptionsUserService.class));

// - Marketing-related:
        grant(VistaCrmBehavior.Marketing, new IServiceExecutePermission(PageDescriptorCrudService.class));
        grant(VistaCrmBehavior.Marketing, new IServiceExecutePermission(MediaUploadBuildingService.class));
        grant(VistaCrmBehavior.Marketing, new IServiceExecutePermission(MediaUploadFloorplanService.class));

// - Administration:
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ARCodeCrudService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(SelectProductCodeListService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(EmailTemplatesPolicy.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(EmailTemplatesPolicyCrudService.class));

        grant(VistaCrmBehavior.Marketing, new IServiceExecutePermission(SiteGeneralCrudService.class));
        grant(VistaCrmBehavior.Marketing, new IServiceExecutePermission(SiteBrandingCrudService.class));
        grant(VistaCrmBehavior.Marketing, new IServiceExecutePermission(SiteContentCrudService.class));
        grant(VistaCrmBehavior.Marketing, new IServiceExecutePermission(SiteImageResourceCrudService.class));
        grant(VistaCrmBehavior.Marketing, new IServiceExecutePermission(SiteImageResourceUploadService.class));
        grant(VistaCrmBehavior.Marketing, new IServiceExecutePermission(CityIntroPageCrudService.class));
        grant(VistaCrmBehavior.Marketing, new IServiceExecutePermission(HomePageGadgetCrudService.class));

        grant(VistaCrmBehavior.Organization, new IServiceExecutePermission(CrmRoleCrudService.class));

        grant(VistaCrmBehavior.OrganizationFinancial, new IServiceExecutePermission(TaxCrudService.class));

        grant(VistaCrmBehavior.OrganizationPolicy, new IServiceExecutePermission(GlCodeCategoryCrudService.class));
        grant(VistaCrmBehavior.OrganizationPolicy, new IServiceExecutePermission(BackgroundCheckPolicyCrudService.class.getPackage().getName() + ".*"));
        grant(VistaCrmBehavior.OrganizationPolicy, new IServiceExecutePermission(EmailTemplateManagerService.class));

        grant(VistaCrmBehavior.OrganizationPolicy, new IServiceExecutePermission(SelectTaxListService.class));
        grant(VistaCrmBehavior.OrganizationPolicy, new IServiceExecutePermission(SelectGlCodeListService.class));

        grant(VistaCrmBehavior.PropertyVistaAccountOwner, new IServiceExecutePermission(MerchantAccountCrudService.class));
        grant(VistaCrmBehavior.OrganizationFinancial, new IServiceExecutePermission(MerchantAccountCrudService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CreditCheckStatusService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CustomerCreditCheckLongReportService.class));

        grant(VistaCrmBehavior.PropertyVistaAccountOwner, new IServiceExecutePermission(CreditCheckStatusCrudService.class));
        grant(VistaCrmBehavior.PropertyVistaAccountOwner, new IServiceExecutePermission(CreditCheckWizardService.class));
        grant(VistaCrmBehavior.PropertyVistaAccountOwner, new IServiceExecutePermission(CreditCardValidationService.class));
        grant(VistaCrmBehavior.PropertyVistaAccountOwner, new IServiceExecutePermission(OnlinePaymentWizardService.class));

        grant(VistaCrmBehavior.PropertyVistaAccountOwner, new IServiceExecutePermission(PmcPaymentMethodsCrudService.class));
        grant(VistaCrmBehavior.PropertyVistaAccountOwner, new IServiceExecutePermission(PmcCompanyInfoCrudService.class));

        grant(VistaCrmBehavior.PropertyVistaAccountOwner, new IServiceExecutePermission(ILSConfigCrudService.class));

        grant(VistaCrmBehavior.Equifax, new IServiceExecutePermission(CustomerCreditCheckCrudService.class));
        grant(VistaCrmBehavior.OrganizationFinancial, new IServiceExecutePermission(CustomerCreditCheckCrudService.class));

        grant(VistaCrmBehavior.Organization, new IServiceExecutePermission(CrmAuditRecordsListerService.class));
        grant(VistaCrmBehavior.Organization, new EntityPermission(AuditRecord.class, EntityPermission.READ));

// - TenantInsurance:
        grant(VistaBasicBehavior.CRM, new EntityPermission(GeneralInsuranceCertificate.class, EntityPermission.ALL));

// - Other:
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(NotesAndAttachmentsCrudService.class));
        grant(VistaCrmBehavior.Organization, new EntityPermission(NoteAttachment.class, EntityPermission.READ));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(PmcDocumentFileUploadService.class));

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(MaintenanceRequestPictureUploadService.class));

        grant(VistaBasicBehavior.CRM, new EntityPermission(Company.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(VendorCrudService.class));

// - Old services:
        grant(VistaBasicBehavior.CRM, new EntityPermission(Country.class.getPackage().getName() + ".*", EntityPermission.READ));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(ReferenceDataService.class));

        // All other roles have everything the same
//        for (VistaTenantBehavior b : VistaTenantBehavior.getCrmBehaviors()) {
//            if (b != VistaBasicBehavior.CRM) {
//                grant(b, VistaBasicBehavior.CRM);
//            }
//        }

        // Data Access
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BuildingDatasetAccessRule(), Building.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BuildingElementDatasetAccessRule(), AptUnit.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BuildingElementDatasetAccessRule(), LockerArea.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BuildingElementDatasetAccessRule(), Parking.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BuildingElementDatasetAccessRule(), Roof.class);

        grant(VistaDataAccessBehavior.BuildingsAssigned, new LeaseDatasetAccessRule(), Lease.class);

        grant(VistaDataAccessBehavior.BuildingsAssigned, new CustomerDatasetAccessRule(), Customer.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new LeaseParticipantDatasetAccessRule(), Guarantor.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new LeaseParticipantDatasetAccessRule(), Tenant.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new LeaseTermParticipantDatasetAccessRule(), LeaseTermGuarantor.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new LeaseTermParticipantDatasetAccessRule(), LeaseTermTenant.class);

        grant(VistaDataAccessBehavior.BuildingsAssigned, new MaintenanceRequestDatasetAccessRule(), MaintenanceRequest.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new CommunityEventDatasetAccessRule(), CommunityEvent.class);

        grant(VistaDataAccessBehavior.BuildingsAssigned, new AggregatedTransferDatasetAccessRule(), AggregatedTransfer.class);

        // Data Access for Gadgets & Reports
        grant(VistaDataAccessBehavior.BuildingsAssigned, new AptUnitDatasetAccessRule(), AptUnit.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new AptUnitOccupancySegmentDatasetAccessRule(), AptUnitOccupancySegment.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new UnitAvailabilityStatusDatasetAccessRule(), UnitAvailabilityStatus.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new LeaseAgingBucketsDatasetAccessRule(), LeaseAgingBuckets.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BuildingAgingBucketsDatasetAccessRule(), BuildingAgingBuckets.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BuildingArrearsSnapshotDatasetAccessRule(), BuildingArrearsSnapshot.class);

        grant(VistaDataAccessBehavior.BuildingsAssigned, new LeadDatasetAccessRule(), Lead.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new AppointmentDatasetAccessRule(), Appointment.class);

        grant(VistaDataAccessBehavior.BuildingsAssigned, new CustomerCreditCheckDatasetAccessRule(), CustomerCreditCheck.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new PaymentRecordDatasetAccessRule(), PaymentRecord.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BillingCycleDatasetAccessRule(), BillingCycle.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new BillingAccountDatasetAccessRule(), BillingAccount.class);

        grant(VistaDataAccessBehavior.BuildingsAssigned, new N4LegalLetterDatasetAccessRule(), N4LegalLetter.class);

        freeze();
    }
}

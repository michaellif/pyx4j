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
 */
package com.propertyvista.crm.server.security;

import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.rpc.ReferenceDataService;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.essentials.rpc.download.DownloadableService;
import com.pyx4j.essentials.rpc.report.ReportServices;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessService;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.ServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.crm.rpc.CRMImpliedPermission;
import com.propertyvista.crm.rpc.services.FeedbackService;
import com.propertyvista.crm.rpc.services.NoteAttachmentUploadService;
import com.propertyvista.crm.rpc.services.PmcDocumentFileUploadService;
import com.propertyvista.crm.rpc.services.PmcTermsOfServiceService;
import com.propertyvista.crm.rpc.services.UpdateUploadService;
import com.propertyvista.crm.rpc.services.billing.LeaseAdjustmentCrudService;
import com.propertyvista.crm.rpc.services.billing.PaymentRecordCrudService;
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
import com.propertyvista.crm.rpc.services.customer.CustomerPictureCrmUploadService;
import com.propertyvista.crm.rpc.services.customer.GuarantorCrudService;
import com.propertyvista.crm.rpc.services.customer.InsuranceCertificateScanCrmUploadService;
import com.propertyvista.crm.rpc.services.customer.PreauthorizedPaymentsVisorService;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.crm.rpc.services.customer.TenantPadFileDownloadService;
import com.propertyvista.crm.rpc.services.customer.TenantPadFileUploadService;
import com.propertyvista.crm.rpc.services.customer.lead.AppointmentCrudService;
import com.propertyvista.crm.rpc.services.customer.lead.LeadCrudService;
import com.propertyvista.crm.rpc.services.customer.lead.ShowingCrudService;
import com.propertyvista.crm.rpc.services.customer.screening.LeaseParticipantScreeningVersionService;
import com.propertyvista.crm.rpc.services.customer.screening.LeaseParticipantScreeningViewService;
import com.propertyvista.crm.rpc.services.importer.ExportBuildingDataDownloadService;
import com.propertyvista.crm.rpc.services.importer.ImportBuildingDataService;
import com.propertyvista.crm.rpc.services.lease.BlankApplicationDocumentDownloadService;
import com.propertyvista.crm.rpc.services.lease.IdentificationDocumentCrmUploadService;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationDocumentUploadService;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.LeaseTermAgreementDocumentUploadService;
import com.propertyvista.crm.rpc.services.lease.LeaseTermBlankAgreementDocumentDownloadService;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.ProofOfAssetDocumentCrmUploadService;
import com.propertyvista.crm.rpc.services.lease.ProofOfIncomeDocumentCrmUploadService;
import com.propertyvista.crm.rpc.services.lease.common.DepositLifecycleCrudService;
import com.propertyvista.crm.rpc.services.lease.common.LeaseTermCrudService;
import com.propertyvista.crm.rpc.services.lease.financial.InvoiceCreditCrudService;
import com.propertyvista.crm.rpc.services.lease.financial.InvoiceDebitCrudService;
import com.propertyvista.crm.rpc.services.legal.L1FormDataReviewWizardService;
import com.propertyvista.crm.rpc.services.legal.LegalLetterUploadService;
import com.propertyvista.crm.rpc.services.legal.N4CreateBatchService;
import com.propertyvista.crm.rpc.services.legal.N4DownloadToolService;
import com.propertyvista.crm.rpc.services.legal.eviction.EvictionCaseCrudService;
import com.propertyvista.crm.rpc.services.legal.eviction.N4BatchCrudService;
import com.propertyvista.crm.rpc.services.notes.NotesAndAttachmentsCrudService;
import com.propertyvista.crm.rpc.services.organization.EmployeeSignatureUploadService;
import com.propertyvista.crm.rpc.services.organization.VendorCrudService;
import com.propertyvista.crm.rpc.services.policies.CrmPolicyRetrieveService;
import com.propertyvista.crm.rpc.services.policies.policy.EmailTemplatesPolicyCrudService;
import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;
import com.propertyvista.crm.rpc.services.reports.CrmAvailableReportService;
import com.propertyvista.crm.rpc.services.reports.CrmReportsService;
import com.propertyvista.crm.rpc.services.reports.CrmReportsSettingsPersistenceService;
import com.propertyvista.crm.rpc.services.security.CrmLoginAttemptsListerService;
import com.propertyvista.crm.rpc.services.security.CrmPasswordResetService;
import com.propertyvista.crm.rpc.services.selections.SelectBuildingListService;
import com.propertyvista.crm.rpc.services.selections.SelectBuildingUtilityListService;
import com.propertyvista.crm.rpc.services.selections.SelectConcessionListService;
import com.propertyvista.crm.rpc.services.selections.SelectCrmUserListService;
import com.propertyvista.crm.rpc.services.selections.SelectCustomerListService;
import com.propertyvista.crm.rpc.services.selections.SelectCustomerUserListService;
import com.propertyvista.crm.rpc.services.selections.SelectEmployeeListService;
import com.propertyvista.crm.rpc.services.selections.SelectFeatureListService;
import com.propertyvista.crm.rpc.services.selections.SelectFloorplanListService;
import com.propertyvista.crm.rpc.services.selections.SelectLeaseAdjustmentReasonListService;
import com.propertyvista.crm.rpc.services.selections.SelectLeaseTermListService;
import com.propertyvista.crm.rpc.services.selections.SelectLockerAreaListService;
import com.propertyvista.crm.rpc.services.selections.SelectParkingListService;
import com.propertyvista.crm.rpc.services.selections.SelectPortfolioListService;
import com.propertyvista.crm.rpc.services.selections.SelectProductCodeListService;
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
import com.propertyvista.crm.server.security.access.AggregatedTransferDatasetAccessRule;
import com.propertyvista.crm.server.security.access.AppointmentDatasetAccessRule;
import com.propertyvista.crm.server.security.access.AptUnitDatasetAccessRule;
import com.propertyvista.crm.server.security.access.AptUnitOccupancySegmentDatasetAccessRule;
import com.propertyvista.crm.server.security.access.BillingAccountDatasetAccessRule;
import com.propertyvista.crm.server.security.access.BillingCycleDatasetAccessRule;
import com.propertyvista.crm.server.security.access.BuildingAgingBucketsDatasetAccessRule;
import com.propertyvista.crm.server.security.access.BuildingArrearsSnapshotDatasetAccessRule;
import com.propertyvista.crm.server.security.access.BuildingDatasetAccessRule;
import com.propertyvista.crm.server.security.access.BuildingElementDatasetAccessRule;
import com.propertyvista.crm.server.security.access.CommunityEventDatasetAccessRule;
import com.propertyvista.crm.server.security.access.CustomerCreditCheckDatasetAccessRule;
import com.propertyvista.crm.server.security.access.CustomerDatasetAccessRule;
import com.propertyvista.crm.server.security.access.LeadDatasetAccessRule;
import com.propertyvista.crm.server.security.access.LeaseAgingBucketsDatasetAccessRule;
import com.propertyvista.crm.server.security.access.LeaseDatasetAccessRule;
import com.propertyvista.crm.server.security.access.LeaseParticipantDatasetAccessRule;
import com.propertyvista.crm.server.security.access.LeaseTermParticipantDatasetAccessRule;
import com.propertyvista.crm.server.security.access.MaintenanceRequestDatasetAccessRule;
import com.propertyvista.crm.server.security.access.N4LegalLetterDatasetAccessRule;
import com.propertyvista.crm.server.security.access.PaymentRecordDatasetAccessRule;
import com.propertyvista.crm.server.security.access.UnitAvailabilityStatusDatasetAccessRule;
import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.CardsAggregatedTransfer;
import com.propertyvista.domain.financial.EftAggregatedTransfer;
import com.propertyvista.domain.financial.PaymentRecord;
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
import com.propertyvista.domain.ref.CountryPolicyNode;
import com.propertyvista.domain.security.VistaDataAccessBehavior;
import com.propertyvista.domain.security.common.VistaAccessGrantedBehavior;
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
import com.propertyvista.portal.rpc.shared.services.CreditCardValidationService;
import com.propertyvista.shared.services.dev.DevConsoleService;

public class VistaCrmAccessControlList extends ServletContainerAclBuilder {

    // TODO Change this if you want to make it work temporary. Build will fail!
    private static final boolean allowAllDuringDevelopment = false;

    private static final boolean allowAllEntityDuringDevelopment = true;

    public VistaCrmAccessControlList() {

        grant(VistaAccessGrantedBehavior.CRM, new CRMImpliedPermission());

        if (allowAllDuringDevelopment || ApplicationMode.isDemo()) {
            // Debug
            grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission("*"));
            grant(VistaAccessGrantedBehavior.CRM, new ServiceExecutePermission("*"));
            grant(VistaAccessGrantedBehavior.CRM, new EntityPermission("*", ALL));
            grant(VistaAccessGrantedBehavior.CRM, new EntityPermission("*", READ));
        }

        if (allowAllEntityDuringDevelopment) {
            grant(VistaAccessGrantedBehavior.CRM, new EntityPermission("*", ALL));
            grant(VistaAccessGrantedBehavior.CRM, new EntityPermission("*", READ));
        }

        if (ApplicationMode.isDevelopment() || ApplicationMode.isDemo()) {
            grant(new IServiceExecutePermission(DevConsoleService.class));
        }

        grant(VistaBasicBehavior.PropertyVistaSupport, new IServiceExecutePermission("*"));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(DeferredProcessService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(ReferenceDataService.class));
        grant(VistaAccessGrantedBehavior.CRM, new ServiceExecutePermission(ReportServices.class, "*"));

        grant(new IServiceExecutePermission(CrmAuthenticationService.class));
        grant(VistaBasicBehavior.CRMPasswordChangeRequired, new IServiceExecutePermission(CrmPasswordResetService.class));

        grant(VistaApplication.crm, new IServiceExecutePermission(FeedbackService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(CrmPolicyRetrieveService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(BreadcrumbsService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(PmcTermsOfServiceService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(DownloadableService.class));

// - Reports:
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(CrmAvailableReportService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(CrmReportsService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(CrmReportsSettingsPersistenceService.class));

// - Legal:
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LegalLetterUploadService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(N4CreateBatchService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(N4DownloadToolService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(L1FormDataReviewWizardService.class));

// - Building-related:
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Complex.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(ComplexCrudService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Landlord.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LandlordCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LandlordMediaUploadService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Building.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(BuildingCrudService.class));

        //2014 ok
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectBuildingListService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Elevator.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(ElevatorCrudService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Boiler.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(BoilerCrudService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Roof.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(RoofCrudService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Parking.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(ParkingCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectParkingListService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(ParkingSpot.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(ParkingSpotCrudService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(LockerArea.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LockerAreaCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectLockerAreaListService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Locker.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LockerCrudService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Floorplan.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(FloorplanCrudService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectFloorplanListService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectBuildingUtilityListService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(CommunityEvent.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(CommunityEventCrudService.class));

        grant(VistaBasicBehavior.PropertyVistaSupport, new IServiceExecutePermission(UpdateUploadService.class));
        grant(VistaBasicBehavior.PropertyVistaSupport, new IServiceExecutePermission(ImportBuildingDataService.class));
        grant(VistaBasicBehavior.PropertyVistaSupport, new IServiceExecutePermission(ExportBuildingDataDownloadService.class));

// - Unit-related:
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(AptUnit.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(UnitCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(AptUnitItem.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(UnitItemCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(AptUnitOccupancySegment.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(UnitOccupancyCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(UnitOccupancyManagerService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectUnitListService.class));

// - Tenant-related:
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Lead.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LeadCrudService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Appointment.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(AppointmentCrudService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Showing.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(ShowingCrudService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(TenantCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(GuarantorCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectTenantListService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Customer.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(LeaseTermTenant.class, EntityPermission.ALL));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectCustomerListService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(CustomerScreening.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LeaseParticipantScreeningViewService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LeaseParticipantScreeningVersionService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Lease.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LeaseViewerCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LeaseApplicationViewerCrudService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(ProofOfIncomeDocumentCrmUploadService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(ProofOfAssetDocumentCrmUploadService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(IdentificationDocumentCrmUploadService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LeaseTermAgreementDocumentUploadService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LeaseTermBlankAgreementDocumentDownloadService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LeaseApplicationDocumentUploadService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(BlankApplicationDocumentDownloadService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(LeaseTerm.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LeaseTermCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LeaseTermVersionService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(InvoiceCredit.class, EntityPermission.READ));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(InvoiceCreditCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(InvoiceDebit.class, EntityPermission.READ));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(InvoiceDebitCrudService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectLeaseTermListService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(NoteAttachmentUploadService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectCrmUserListService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectPortfolioListService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(LeaseAdjustmentCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(DepositLifecycleCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(PaymentRecordCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(CreditCardValidationService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(EvictionCaseCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(N4BatchCrudService.class));

        grant(VistaBasicBehavior.PropertyVistaSupport, new IServiceExecutePermission(TenantPadFileDownloadService.class));
        grant(VistaBasicBehavior.PropertyVistaSupport, new IServiceExecutePermission(TenantPadFileUploadService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(PreauthorizedPaymentsVisorService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(CustomerPictureCrmUploadService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(InsuranceCertificateScanCrmUploadService.class));

// - Service-related:
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Service.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(ServiceCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(ServiceVersionService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Feature.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(FeatureCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(FeatureVersionService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Concession.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(ConcessionCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(ConcessionVersionService.class));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectFeatureListService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectConcessionListService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectLeaseAdjustmentReasonListService.class));

// - Organization:
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectEmployeeListService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(EmployeeSignatureUploadService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(CrmLoginAttemptsListerService.class));

// - Administration:

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectProductCodeListService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(EmailTemplatesPolicy.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(EmailTemplatesPolicyCrudService.class));

// - TenantInsurance:
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(GeneralInsuranceCertificate.class, EntityPermission.ALL));

// - Other:
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(NotesAndAttachmentsCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(NoteAttachment.class, EntityPermission.READ));

        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(PmcDocumentFileUploadService.class));

        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Company.class, EntityPermission.ALL));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(VendorCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectCustomerUserListService.class));

// - Old services:
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(CountryPolicyNode.class.getPackage().getName() + ".*", EntityPermission.READ));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(ReferenceDataService.class));

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
        grant(VistaDataAccessBehavior.BuildingsAssigned, new AggregatedTransferDatasetAccessRule(), CardsAggregatedTransfer.class);
        grant(VistaDataAccessBehavior.BuildingsAssigned, new AggregatedTransferDatasetAccessRule(), EftAggregatedTransfer.class);

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

        /***************** this is new List **************** */

        merge(new VistaCrmAdministrationAccessControlList());
        merge(new VistaCrmAdministrationPoliciesAccessControlList());
        merge(new VistaCrmAdministrationContentManagementAccessControlList());

        merge(new VistaCrmBuildingAccessControlList());

        merge(new VistaCrmLegalAccessControlList());
        merge(new VistaCrmFinancialAccessControlList());
        merge(new VistaCrmCreditCheckAccessControlList());

        merge(new VistaCrmLeaseApplicationAccessControlList());
        merge(new VistaCrmLeaseAccessControlList());
        merge(new VistaCrmMaintenanceAccessControlList());

        merge(new VistaCrmTenantAccessControlList());
        merge(new VistaCrmPotentialTenantAccessControlList());

        merge(new VistaCrmGuarantorAccessControlList());

        merge(new VistaCrmEmployeeAccessControlList());
        merge(new VistaCrmSupportAccessControlList());

        merge(new VistaCrmYardiAccessControlList());

        merge(new VistaCrmCommunicationAccessControlList());

        merge(new VistaCrmDashboardsAccessControlList());

        freeze();
    }
}

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
package com.propertyvista.portal.server.security;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.rpc.ReferenceDataService;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.security.InstanceAccess;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessService;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.IVRDelivery;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageAttachment;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.NotificationDelivery;
import com.propertyvista.domain.communication.SMSDelivery;
import com.propertyvista.domain.communication.ThreadPolicyHandle;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestPicture;
import com.propertyvista.domain.maintenance.YardiServiceRequest;
import com.propertyvista.domain.media.IdentificationDocumentFile;
import com.propertyvista.domain.media.ProofOfAssetDocumentFile;
import com.propertyvista.domain.media.ProofOfIncomeDocumentFile;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.CountryPolicyNode;
import com.propertyvista.domain.ref.ProvincePolicyNode;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.security.VistaDataAccessBehavior;
import com.propertyvista.domain.security.common.VistaAccessGrantedBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.CustomerDeliveryPreferences;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.domain.tenant.CustomerPreferences;
import com.propertyvista.domain.tenant.insurance.GeneralInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.domain.tenant.lease.AgreementDigitalSignatures;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationContextSelectionService;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationStatusService;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.rpc.portal.prospect.services.IdentificationDocumentProspectUploadService;
import com.propertyvista.portal.rpc.portal.prospect.services.ProofOfAssetDocumentProspectUploadService;
import com.propertyvista.portal.rpc.portal.prospect.services.ProofOfIncomeDocumentProspectUploadService;
import com.propertyvista.portal.rpc.portal.prospect.services.ProspectAuthenticationService;
import com.propertyvista.portal.rpc.portal.prospect.services.ProspectSignUpService;
import com.propertyvista.portal.rpc.portal.resident.ac.HelpAction;
import com.propertyvista.portal.rpc.portal.resident.services.CommunicationPortalCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.ExtraActivityPortalService;
import com.propertyvista.portal.rpc.portal.resident.services.LeaseContextSelectionService;
import com.propertyvista.portal.rpc.portal.resident.services.MessageAttachmentUploadPortalService;
import com.propertyvista.portal.rpc.portal.resident.services.QuickTipService;
import com.propertyvista.portal.rpc.portal.resident.services.ResidentAuthenticationService;
import com.propertyvista.portal.rpc.portal.resident.services.ResidentSelfRegistrationService;
import com.propertyvista.portal.rpc.portal.resident.services.financial.AutoPayWizardService;
import com.propertyvista.portal.rpc.portal.resident.services.financial.BillingService;
import com.propertyvista.portal.rpc.portal.resident.services.insurance.GeneralInsurancePolicyCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.insurance.InsuranceCertificateScanResidentUploadService;
import com.propertyvista.portal.rpc.portal.resident.services.insurance.TenantSureInsurancePolicyCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.insurance.TenantSurePaymentMethodCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.maintenance.MaintenanceRequestCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.maintenance.MaintenanceRequestPictureUploadPortalService;
import com.propertyvista.portal.rpc.portal.resident.services.movein.LeaseAgreementService;
import com.propertyvista.portal.rpc.portal.resident.services.movein.LeaseSigningCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.movein.LeaseTermBlankAgreementDocumentDownloadService;
import com.propertyvista.portal.rpc.portal.resident.services.movein.MoveInWizardService;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentProfileCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentSummaryService;
import com.propertyvista.portal.rpc.portal.shared.services.CustomerPicturePortalUploadService;
import com.propertyvista.portal.rpc.portal.shared.services.PasswordChangeUserService;
import com.propertyvista.portal.rpc.portal.shared.services.PortalContentService;
import com.propertyvista.portal.rpc.portal.shared.services.PortalPasswordResetService;
import com.propertyvista.portal.rpc.portal.shared.services.PortalPolicyRetrieveService;
import com.propertyvista.portal.rpc.portal.shared.services.PortalTermsAndPoliciesService;
import com.propertyvista.portal.rpc.portal.shared.services.SiteThemeServices;
import com.propertyvista.portal.rpc.portal.shared.services.communityevent.CommunityEventCrudService;
import com.propertyvista.portal.rpc.portal.shared.services.profile.CustomerAccountCrudService;
import com.propertyvista.portal.rpc.portal.shared.services.profile.CustomerPreferencesCrudService;
import com.propertyvista.portal.rpc.shared.services.CreditCardValidationService;
import com.propertyvista.portal.server.security.access.prospect.CustomerPictureProspectDatasetAccessRule;
import com.propertyvista.portal.server.security.access.prospect.CustomerPreferencesDatasetAccessRule;
import com.propertyvista.portal.server.security.access.prospect.IdentificationDocumentFileProspectDatasetAccessRule;
import com.propertyvista.portal.server.security.access.prospect.LeasePaymentMethodProspectDatasetAccessRule;
import com.propertyvista.portal.server.security.access.prospect.LeaseTermTenantProspectDatasetAccessRule;
import com.propertyvista.portal.server.security.access.prospect.ProofOfAssetDocumentFileProspectDatasetAccessRule;
import com.propertyvista.portal.server.security.access.prospect.ProofOfIncomeDocumentFileProspectDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.AutopayAgreementCoveredItemTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.AutopayAgreementTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.BillTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.CommunicationThreadPortalAccessRule;
import com.propertyvista.portal.server.security.access.resident.CustomerDeliveryPreferencesDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.CustomerPictureTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.GeneralInsurancePolicyDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.InsuranceCertificateScanDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.LeasePaymentMethodTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.MaintenanceRequestPictureTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.MaintenanceRequestTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.MessagePortalAccessRule;
import com.propertyvista.portal.server.security.access.resident.PaymentRecordTenantDatasetAccessRule;
import com.propertyvista.server.common.security.UserEntityInstanceAccess;
import com.propertyvista.shared.services.dev.DevConsoleService;
import com.propertyvista.shared.services.dev.MockDataGenerator;

public class VistaPortalAccessControlList extends UIAclBuilder {

    private final static int CRUD = EntityPermission.CREATE | EntityPermission.READ | EntityPermission.UPDATE;

    public VistaPortalAccessControlList() {

        grant(new IServiceExecutePermission(ResidentAuthenticationService.class));
        grant(new IServiceExecutePermission(PortalTermsAndPoliciesService.class));
        grant(new IServiceExecutePermission(ResidentSelfRegistrationService.class));

        grant(new IServiceExecutePermission(ProspectAuthenticationService.class));
        grant(new IServiceExecutePermission(ProspectSignUpService.class));

        grant(PortalResidentBehavior.LeaseSelectionRequired, new IServiceExecutePermission(LeaseContextSelectionService.class));
        grant(PortalResidentBehavior.HasMultipleLeases, new IServiceExecutePermission(LeaseContextSelectionService.class));

        grant(PortalProspectBehavior.ApplicationSelectionRequired, new IServiceExecutePermission(ApplicationContextSelectionService.class));
        grant(PortalProspectBehavior.HasMultipleApplications, new IServiceExecutePermission(ApplicationContextSelectionService.class));

        // Old TODO remove
        grant(new IServiceExecutePermission(ReferenceDataService.class));
        grant(new EntityPermission(City.class, EntityPermission.READ));
        grant(new EntityPermission(CommunicationThread.class, EntityPermission.READ));
        grant(new EntityPermission(NotificationDelivery.class, EntityPermission.READ));
        grant(new EntityPermission(Message.class, EntityPermission.READ));

        grant(new EntityPermission(CountryPolicyNode.class, EntityPermission.READ));
        grant(new EntityPermission(ProvincePolicyNode.class, EntityPermission.READ));

        grant(new IServiceExecutePermission(SiteThemeServices.class));

        grant(new IServiceExecutePermission(PortalPolicyRetrieveService.class));

        grant(VistaBasicBehavior.ResidentPortalPasswordChangeRequired, new IServiceExecutePermission(PortalPasswordResetService.class));
        grant(VistaBasicBehavior.ProspectPortalPasswordChangeRequired, new IServiceExecutePermission(PortalPasswordResetService.class));

        grant(VistaAccessGrantedBehavior.ResidentPortal, new IServiceExecutePermission(PasswordChangeUserService.class));
        grant(VistaAccessGrantedBehavior.ProspectPortal, new IServiceExecutePermission(PasswordChangeUserService.class));

        grant(VistaAccessGrantedBehavior.ResidentPortal, new IServiceExecutePermission(CustomerAccountCrudService.class));
        grant(VistaAccessGrantedBehavior.ProspectPortal, new IServiceExecutePermission(CustomerAccountCrudService.class));

        // Old TODO remove
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(ReferenceDataService.class));

        grant(PortalProspectBehavior.Prospect, new EntityPermission(CountryPolicyNode.class, EntityPermission.READ));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(ProvincePolicyNode.class, EntityPermission.READ));

        InstanceAccess userEntityAccess = new UserEntityInstanceAccess();
        grant(PortalProspectBehavior.Prospect, new EntityPermission(OnlineApplication.class, userEntityAccess, CRUD));

        grant(PortalProspectBehavior.Prospect, new EntityPermission(LeaseTermTenant.class, CRUD));

        grant(PortalProspectBehavior.Applicant, PortalProspectBehavior.Prospect);
        grant(PortalProspectBehavior.CoApplicant, PortalProspectBehavior.Prospect);
        grant(PortalProspectBehavior.Guarantor, PortalProspectBehavior.Prospect);

        // -------------

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(CreditCardValidationService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(ResidentProfileCrudService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(CustomerPreferencesCrudService.class));

        grant(PortalResidentBehavior.Guarantor, new IServiceExecutePermission(ResidentProfileCrudService.class));

        //========================= Resident Portal

        grant(VistaAccessGrantedBehavior.ResidentPortal, HelpAction.class);

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(BillingService.class));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(
                com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(
                com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentMethodWizardService.class));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(
                com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentWizardService.class));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(AutoPayWizardService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(BillingService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(ResidentSummaryService.class));
        grant(PortalResidentBehavior.Guarantor, new IServiceExecutePermission(ResidentSummaryService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(
                com.propertyvista.portal.rpc.portal.resident.services.insurance.InsuranceService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(InsuranceCertificateScanResidentUploadService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(GeneralInsurancePolicyCrudService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(MaintenanceRequestCrudService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(MoveInWizardService.class));
        grant(PortalResidentBehavior.Guarantor, new IServiceExecutePermission(MoveInWizardService.class));
        grant(PortalResidentBehavior.LeaseAgreementSigningRequired, new IServiceExecutePermission(LeaseSigningCrudService.class));
        grant(PortalResidentBehavior.LeaseAgreementSigningRequired, new IServiceExecutePermission(LeaseTermBlankAgreementDocumentDownloadService.class));
        grant(PortalResidentBehavior.LeaseAgreementSigningRequired, new EntityPermission(AgreementDigitalSignatures.class, EntityPermission.CREATE));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(LeaseTermBlankAgreementDocumentDownloadService.class));
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(LeaseTermBlankAgreementDocumentDownloadService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(LeaseAgreementService.class));
        grant(PortalResidentBehavior.Guarantor, new IServiceExecutePermission(LeaseAgreementService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(CustomerPicturePortalUploadService.class));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(MaintenanceRequestPictureUploadPortalService.class));

        grant(new IServiceExecutePermission(ExtraActivityPortalService.class));
        grant(new IServiceExecutePermission(CommunityEventCrudService.class));
        grant(new IServiceExecutePermission(CommunicationPortalCrudService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(DeferredProcessService.class));

        grant(PortalResidentBehavior.Resident, new EntityPermission(CustomerPicture.class, CRUD));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(QuickTipService.class));
        grant(PortalResidentBehavior.Guarantor, new IServiceExecutePermission(QuickTipService.class));

        // ========================= communication
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(MessageAttachmentUploadPortalService.class));
        grant(PortalResidentBehavior.Guarantor, new IServiceExecutePermission(MessageAttachmentUploadPortalService.class));
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(MessageAttachmentUploadPortalService.class));

        grant(PortalResidentBehavior.Resident, new EntityPermission(CustomerPreferences.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(DeliveryHandle.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(MessageAttachment.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(CommunicationThread.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(Message.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(MaintenanceRequestPicture.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(MessageCategory.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(ThreadPolicyHandle.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(NotificationDelivery.class, EntityPermission.READ));
        grant(PortalResidentBehavior.Resident, new EntityPermission(SMSDelivery.class, EntityPermission.READ));
        grant(PortalResidentBehavior.Resident, new EntityPermission(IVRDelivery.class, EntityPermission.READ));
        grant(PortalResidentBehavior.Resident, new EntityPermission(CustomerDeliveryPreferences.class, CRUD));

        grant(PortalResidentBehavior.Guarantor, new EntityPermission(CustomerPreferences.class, CRUD));
        grant(PortalResidentBehavior.Guarantor, new EntityPermission(DeliveryHandle.class, CRUD));
        grant(PortalResidentBehavior.Guarantor, new EntityPermission(MessageAttachment.class, CRUD));
        grant(PortalResidentBehavior.Guarantor, new EntityPermission(CommunicationThread.class, CRUD));
        grant(PortalResidentBehavior.Guarantor, new EntityPermission(Message.class, CRUD));
        grant(PortalResidentBehavior.Guarantor, new EntityPermission(MaintenanceRequestPicture.class, CRUD));
        grant(PortalResidentBehavior.Guarantor, new EntityPermission(MessageCategory.class, CRUD));
        grant(PortalResidentBehavior.Guarantor, new EntityPermission(ThreadPolicyHandle.class, CRUD));
        grant(PortalResidentBehavior.Guarantor, new EntityPermission(NotificationDelivery.class, EntityPermission.READ));
        grant(PortalResidentBehavior.Guarantor, new EntityPermission(SMSDelivery.class, EntityPermission.READ));
        grant(PortalResidentBehavior.Guarantor, new EntityPermission(IVRDelivery.class, EntityPermission.READ));
        grant(PortalResidentBehavior.Guarantor, new EntityPermission(CustomerDeliveryPreferences.class, CRUD));

        grant(PortalProspectBehavior.Prospect, new EntityPermission(CustomerPreferences.class, CRUD));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(DeliveryHandle.class, CRUD));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(MessageAttachment.class, CRUD));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(CommunicationThread.class, EntityPermission.READ | EntityPermission.UPDATE));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(Message.class, CRUD));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(MessageCategory.class, EntityPermission.READ));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(ThreadPolicyHandle.class, CRUD));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(NotificationDelivery.class, EntityPermission.READ));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(SMSDelivery.class, EntityPermission.READ));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(IVRDelivery.class, EntityPermission.READ));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(CustomerDeliveryPreferences.class, CRUD));

        //========================= Prospect Portal

        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(ApplicationWizardService.class));
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(ApplicationStatusService.class));
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(ApplicationContextSelectionService.class));

        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(CustomerPicturePortalUploadService.class));
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(DeferredProcessService.class));
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(CreditCardValidationService.class));

        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(IdentificationDocumentProspectUploadService.class));
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(ProofOfIncomeDocumentProspectUploadService.class));
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(ProofOfAssetDocumentProspectUploadService.class));

        grant(PortalProspectBehavior.Prospect, new EntityPermission(IdentificationDocumentFile.class, CRUD));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(ProofOfIncomeDocumentFile.class, CRUD));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(ProofOfAssetDocumentFile.class, CRUD));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(LeasePaymentMethod.class, CRUD));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(CustomerPicture.class, CRUD));

        //========================= Resident Portal

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(CustomerPicturePortalUploadService.class));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(MessageAttachmentUploadPortalService.class));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(LeaseTermBlankAgreementDocumentDownloadService.class));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(DeferredProcessService.class));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(CreditCardValidationService.class));

        // Tenant Insurance and TenantSure
        grant(PortalResidentBehavior.Resident, new EntityPermission(InsuranceCertificateScan.class, EntityPermission.READ));
        grant(PortalResidentBehavior.Resident, new EntityPermission(GeneralInsurancePolicy.class, CRUD));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(GeneralInsurancePolicyCrudService.class));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(TenantSureInsurancePolicyCrudService.class));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(TenantSurePaymentMethodCrudService.class));

        // Billing and Payments
        grant(PortalResidentBehavior.Resident, new EntityPermission(Bill.class, EntityPermission.READ));

        grant(PortalResidentBehavior.Resident, new EntityPermission(PaymentRecord.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(LeasePaymentMethod.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(AutopayAgreement.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(AutopayAgreementCoveredItem.class, CRUD));

        grant(PortalResidentBehavior.Resident, new EntityPermission(YardiServiceRequest.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(MaintenanceRequestPicture.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(MaintenanceRequest.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(MaintenanceRequestCategory.class, EntityPermission.READ));

        grant(PortalResidentBehavior.ResidentPrimary, PortalResidentBehavior.Resident);
        grant(PortalResidentBehavior.ResidentSecondary, PortalResidentBehavior.Resident);

        grant(PortalResidentBehavior.Guarantor, new IServiceExecutePermission(LeaseTermBlankAgreementDocumentDownloadService.class));
        grant(PortalResidentBehavior.Guarantor, new IServiceExecutePermission(DeferredProcessService.class));

        // Dev
        if (ApplicationMode.isDevelopment()) {
            grant(new IServiceExecutePermission(MockDataGenerator.class));
        }

        if (ApplicationMode.isDevelopment() || ApplicationMode.isDemo()) {
            grant(new IServiceExecutePermission(DevConsoleService.class));
        }

        // Data Access
        grant(PortalResidentBehavior.Resident, VistaDataAccessBehavior.ResidentInPortal);
        grant(VistaDataAccessBehavior.ResidentInPortal, new CustomerPictureTenantDatasetAccessRule(), CustomerPicture.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new PaymentRecordTenantDatasetAccessRule(), PaymentRecord.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new LeasePaymentMethodTenantDatasetAccessRule(), LeasePaymentMethod.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new AutopayAgreementTenantDatasetAccessRule(), AutopayAgreement.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new AutopayAgreementCoveredItemTenantDatasetAccessRule(), AutopayAgreementCoveredItem.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new BillTenantDatasetAccessRule(), Bill.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new GeneralInsurancePolicyDatasetAccessRule(), GeneralInsurancePolicy.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new InsuranceCertificateScanDatasetAccessRule(), InsuranceCertificateScan.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new MaintenanceRequestTenantDatasetAccessRule(), MaintenanceRequest.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new MaintenanceRequestPictureTenantDatasetAccessRule(), MaintenanceRequestPicture.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new MessagePortalAccessRule(), Message.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new CommunicationThreadPortalAccessRule(), CommunicationThread.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new CustomerDeliveryPreferencesDatasetAccessRule(), CustomerDeliveryPreferences.class);

        grant(PortalResidentBehavior.Guarantor, VistaDataAccessBehavior.GuarantorInPortal);
        grant(VistaDataAccessBehavior.GuarantorInPortal, new MessagePortalAccessRule(), Message.class);
        grant(VistaDataAccessBehavior.GuarantorInPortal, new CommunicationThreadPortalAccessRule(), CommunicationThread.class);

        grant(PortalProspectBehavior.Prospect, VistaDataAccessBehavior.ProspectInPortal);
        grant(VistaDataAccessBehavior.ProspectInPortal, new CustomerPictureProspectDatasetAccessRule(), CustomerPicture.class);
        grant(VistaDataAccessBehavior.ProspectInPortal, new CustomerPreferencesDatasetAccessRule(), CustomerPreferences.class);
        grant(VistaDataAccessBehavior.ProspectInPortal, new IdentificationDocumentFileProspectDatasetAccessRule(), IdentificationDocumentFile.class);
        grant(VistaDataAccessBehavior.ProspectInPortal, new ProofOfIncomeDocumentFileProspectDatasetAccessRule(), ProofOfIncomeDocumentFile.class);
        grant(VistaDataAccessBehavior.ProspectInPortal, new ProofOfAssetDocumentFileProspectDatasetAccessRule(), ProofOfAssetDocumentFile.class);
        grant(VistaDataAccessBehavior.ProspectInPortal, new LeasePaymentMethodProspectDatasetAccessRule(), LeasePaymentMethod.class);
        grant(VistaDataAccessBehavior.ProspectInPortal, new LeaseTermTenantProspectDatasetAccessRule(), LeaseTermTenant.class);
        grant(VistaDataAccessBehavior.ProspectInPortal, new MessagePortalAccessRule(), Message.class);
        grant(VistaDataAccessBehavior.ProspectInPortal, new CommunicationThreadPortalAccessRule(), CommunicationThread.class);
        grant(VistaDataAccessBehavior.ProspectInPortal, new CustomerDeliveryPreferencesDatasetAccessRule(), CustomerDeliveryPreferences.class);

        grant(new IServiceExecutePermission(PortalContentService.class));
        freeze();
    }
}

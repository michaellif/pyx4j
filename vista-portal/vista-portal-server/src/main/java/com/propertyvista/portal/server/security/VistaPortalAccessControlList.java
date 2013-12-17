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
package com.propertyvista.portal.server.security;

import com.pyx4j.entity.rpc.ReferenceDataService;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.security.InstanceAccess;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessService;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.YardiServiceRequest;
import com.propertyvista.domain.media.ProofOfEmploymentDocumentFile;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.security.VistaDataAccessBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.domain.tenant.insurance.GeneralInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationContextSelectionService;
import com.propertyvista.portal.rpc.portal.prospect.services.ProofOfEmploymentFileProspectUploadService;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationStatusService;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.rpc.portal.prospect.services.ProspectAuthenticationService;
import com.propertyvista.portal.rpc.portal.prospect.services.ProspectSignUpService;
import com.propertyvista.portal.rpc.portal.resident.services.LeaseContextSelectionService;
import com.propertyvista.portal.rpc.portal.resident.services.ResidentAuthenticationService;
import com.propertyvista.portal.rpc.portal.resident.services.ResidentSelfRegistrationService;
import com.propertyvista.portal.rpc.portal.resident.services.financial.AutoPayWizardService;
import com.propertyvista.portal.rpc.portal.resident.services.financial.BillingService;
import com.propertyvista.portal.rpc.portal.resident.services.maintenance.MaintenanceRequestCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentAccountCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentProfileCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentSummaryService;
import com.propertyvista.portal.rpc.portal.resident.services.services.GeneralInsurancePolicyCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.services.InsuranceCertificateScanResidentUploadService;
import com.propertyvista.portal.rpc.portal.resident.services.services.TenantSureInsurancePolicyCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.services.TenantSurePaymentMethodCrudService;
import com.propertyvista.portal.rpc.portal.shared.services.CustomerPicturePortalUploadService;
import com.propertyvista.portal.rpc.portal.shared.services.PasswordChangeUserService;
import com.propertyvista.portal.rpc.portal.shared.services.PortalContentService;
import com.propertyvista.portal.rpc.portal.shared.services.PortalPasswordResetService;
import com.propertyvista.portal.rpc.portal.shared.services.PortalPolicyRetrieveService;
import com.propertyvista.portal.rpc.portal.shared.services.PortalVistaTermsService;
import com.propertyvista.portal.rpc.portal.shared.services.SiteThemeServices;
import com.propertyvista.portal.rpc.shared.services.CreditCardValidationService;
import com.propertyvista.portal.server.security.access.prospect.IdentificationDocumentFileProspectDatasetAccessRule;
import com.propertyvista.portal.server.security.access.prospect.ProofOfEmploymentDocumentFileProspectDatasetAccessRule;
import com.propertyvista.portal.server.security.access.prospect.CustomerPictureProspectDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.AutopayAgreementTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.CustomerPictureTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.GeneralInsurancePolicyDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.InsuranceCertificateScanDatasetAccessRule;
import com.propertyvista.portal.server.security.access.resident.LeasePaymentMethodTenantDatasetAccessRule;
import com.propertyvista.server.common.security.UserEntityInstanceAccess;

public class VistaPortalAccessControlList extends ServletContainerAclBuilder {

    private final static int CRUD = EntityPermission.CREATE | EntityPermission.READ | EntityPermission.UPDATE;

    public VistaPortalAccessControlList() {

        grant(new IServiceExecutePermission(ResidentAuthenticationService.class));
        grant(new IServiceExecutePermission(PortalVistaTermsService.class));
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
        grant(new EntityPermission(Country.class, EntityPermission.READ));
        grant(new EntityPermission(Province.class, EntityPermission.READ));

        grant(new IServiceExecutePermission(SiteThemeServices.class));

        grant(new IServiceExecutePermission(PortalPolicyRetrieveService.class));

        grant(VistaBasicBehavior.ResidentPortalPasswordChangeRequired, new IServiceExecutePermission(PortalPasswordResetService.class));
        grant(VistaBasicBehavior.ProspectivePortalPasswordChangeRequired, new IServiceExecutePermission(PortalPasswordResetService.class));

        grant(VistaBasicBehavior.ResidentPortal, new IServiceExecutePermission(PasswordChangeUserService.class));
        grant(VistaBasicBehavior.ProspectivePortal, new IServiceExecutePermission(PasswordChangeUserService.class));

        // Old TODO remove
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(ReferenceDataService.class));

        grant(PortalProspectBehavior.Prospect, new EntityPermission(Country.class, EntityPermission.READ));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(Province.class, EntityPermission.READ));

        InstanceAccess userEntityAccess = new UserEntityInstanceAccess();
        grant(PortalProspectBehavior.Prospect, new EntityPermission(OnlineApplication.class, userEntityAccess, CRUD));

        grant(PortalProspectBehavior.Prospect, new EntityPermission(OrganizationPoliciesNode.class, EntityPermission.READ));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(IdentificationDocumentType.class, EntityPermission.READ));
        grant(PortalProspectBehavior.Prospect, new EntityPermission(LeaseTermTenant.class, CRUD));

        grant(PortalProspectBehavior.Applicant, PortalProspectBehavior.Prospect);
        grant(PortalProspectBehavior.CoApplicant, PortalProspectBehavior.Prospect);
        grant(PortalProspectBehavior.Guarantor, PortalProspectBehavior.Prospect);

        // -------------

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(CreditCardValidationService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(ResidentProfileCrudService.class));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(ResidentAccountCrudService.class));

        //========================= My Community

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

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(
                com.propertyvista.portal.rpc.portal.resident.services.services.InsuranceService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(InsuranceCertificateScanResidentUploadService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(GeneralInsurancePolicyCrudService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(MaintenanceRequestCrudService.class));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(CustomerPicturePortalUploadService.class));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(DeferredProcessService.class));

        grant(PortalResidentBehavior.Resident, new EntityPermission(CustomerPicture.class, CRUD));

        //========================= My Community Prospect Portal

        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(ApplicationStatusService.class));
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(ApplicationWizardService.class));
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(ApplicationContextSelectionService.class));
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(ProofOfEmploymentFileProspectUploadService.class));
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(CustomerPicturePortalUploadService.class));
        grant(PortalProspectBehavior.Prospect, new IServiceExecutePermission(DeferredProcessService.class));

        //=======================================

        // Tenant Insurance and TenantSure
        grant(PortalResidentBehavior.Resident, new EntityPermission(GeneralInsurancePolicy.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(InsuranceCertificateScan.class, EntityPermission.READ));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(GeneralInsurancePolicyCrudService.class));

        grant(PortalResidentBehavior.Resident, new EntityPermission(TenantSureInsurancePolicy.class, CRUD));

        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(TenantSureInsurancePolicyCrudService.class));
        grant(PortalResidentBehavior.Resident, new IServiceExecutePermission(TenantSurePaymentMethodCrudService.class));

        // Billing and Payments
        grant(PortalResidentBehavior.Resident, new EntityPermission(Bill.class, EntityPermission.READ));
        grant(PortalResidentBehavior.Resident, new EntityPermission(InvoiceLineItem.class, EntityPermission.READ));

        grant(PortalResidentBehavior.Resident, new EntityPermission(PaymentRecord.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(LeasePaymentMethod.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(AutopayAgreement.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(AutopayAgreementCoveredItem.class, CRUD));

        grant(PortalResidentBehavior.Resident, new EntityPermission(YardiServiceRequest.class, CRUD));

        grant(PortalResidentBehavior.Resident, new EntityPermission(MaintenanceRequest.class, CRUD));
        grant(PortalResidentBehavior.Resident, new EntityPermission(MaintenanceRequestCategory.class, EntityPermission.READ));

        grant(PortalResidentBehavior.Resident, new EntityPermission(ProofOfEmploymentDocumentFile.class, CRUD));

        grant(PortalResidentBehavior.ResidentPrimary, PortalResidentBehavior.Resident);
        grant(PortalResidentBehavior.ResidentSecondary, PortalResidentBehavior.Resident);

        // Data Access
        grant(PortalResidentBehavior.Resident, VistaDataAccessBehavior.ResidentInPortal);
        grant(VistaDataAccessBehavior.ResidentInPortal, new CustomerPictureTenantDatasetAccessRule(), CustomerPicture.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new LeasePaymentMethodTenantDatasetAccessRule(), LeasePaymentMethod.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new AutopayAgreementTenantDatasetAccessRule(), AutopayAgreement.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new GeneralInsurancePolicyDatasetAccessRule(), GeneralInsurancePolicy.class);
        grant(VistaDataAccessBehavior.ResidentInPortal, new InsuranceCertificateScanDatasetAccessRule(), InsuranceCertificateScan.class);

        grant(PortalProspectBehavior.Prospect, VistaDataAccessBehavior.ProspectInPortal);
        grant(VistaDataAccessBehavior.ProspectInPortal, new CustomerPictureProspectDatasetAccessRule(), CustomerPicture.class);
        if (false) {
            grant(VistaDataAccessBehavior.ProspectInPortal, new ProofOfEmploymentDocumentFileProspectDatasetAccessRule(),
                    ProofOfEmploymentDocumentFile.class);
            grant(VistaDataAccessBehavior.ProspectInPortal, new IdentificationDocumentFileProspectDatasetAccessRule(), ProofOfEmploymentDocumentFile.class);
        }

        grant(new IServiceExecutePermission(PortalContentService.class));
        freeze();
    }
}

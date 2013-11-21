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
import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.security.VistaDataAccessBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.domain.tenant.insurance.GeneralInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationContextSelectionService;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationStatusService;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.rpc.portal.prospect.services.ProspectAuthenticationService;
import com.propertyvista.portal.rpc.portal.resident.services.LeaseContextSelectionService;
import com.propertyvista.portal.rpc.portal.resident.services.ResidentAuthenticationService;
import com.propertyvista.portal.rpc.portal.resident.services.ResidentPictureUploadService;
import com.propertyvista.portal.rpc.portal.resident.services.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.resident.services.financial.AutoPayWizardService;
import com.propertyvista.portal.rpc.portal.resident.services.financial.BillingService;
import com.propertyvista.portal.rpc.portal.resident.services.maintenance.MaintenanceRequestCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentAccountCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentProfileCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentSummaryService;
import com.propertyvista.portal.rpc.portal.resident.services.services.GeneralInsurancePolicyCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.services.InsuranceCertificateScanUploadService;
import com.propertyvista.portal.rpc.portal.resident.services.services.TenantSureInsurancePolicyCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.services.TenantSurePaymentMethodCrudService;
import com.propertyvista.portal.rpc.portal.shared.services.PasswordChangeUserService;
import com.propertyvista.portal.rpc.portal.shared.services.PortalContentService;
import com.propertyvista.portal.rpc.portal.shared.services.PortalPasswordResetService;
import com.propertyvista.portal.rpc.portal.shared.services.PortalPolicyRetrieveService;
import com.propertyvista.portal.rpc.portal.shared.services.PortalVistaTermsService;
import com.propertyvista.portal.rpc.portal.shared.services.SiteThemeServices;
import com.propertyvista.portal.rpc.shared.services.CreditCardValidationService;
import com.propertyvista.portal.server.security.access.AutopayAgreementTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.CustomrPictureTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.GeneralInsurancePolicyDatasetAccessRule;
import com.propertyvista.portal.server.security.access.InsuranceCertificateScanDatasetAccessRule;
import com.propertyvista.portal.server.security.access.LeasePaymentMethodTenantDatasetAccessRule;
import com.propertyvista.server.common.security.UserEntityInstanceAccess;

public class VistaPortalAccessControlList extends ServletContainerAclBuilder {

    private final static int CRUD = EntityPermission.CREATE | EntityPermission.READ | EntityPermission.UPDATE;

    public VistaPortalAccessControlList() {

        grant(new IServiceExecutePermission(ResidentAuthenticationService.class));
        grant(new IServiceExecutePermission(PortalVistaTermsService.class));

        grant(new IServiceExecutePermission(com.propertyvista.portal.rpc.portal.resident.services.ResidentSelfRegistrationService.class));
        grant(new IServiceExecutePermission(com.propertyvista.portal.rpc.portal.resident.services.SelfRegistrationBuildingsSourceService.class));

        grant(new EntityPermission(SelfRegistrationBuildingDTO.class, EntityPermission.READ));

        grant(new IServiceExecutePermission(ProspectAuthenticationService.class));

        grant(VistaCustomerBehavior.LeaseSelectionRequired, new IServiceExecutePermission(LeaseContextSelectionService.class));
        grant(VistaCustomerBehavior.HasMultipleLeases, new IServiceExecutePermission(LeaseContextSelectionService.class));

        grant(VistaCustomerBehavior.ApplicationSelectionRequired, new IServiceExecutePermission(ApplicationContextSelectionService.class));
        grant(VistaCustomerBehavior.HasMultipleApplications, new IServiceExecutePermission(ApplicationContextSelectionService.class));

        // Old TODO remove
        grant(new IServiceExecutePermission(ReferenceDataService.class));
        grant(new EntityPermission(City.class, EntityPermission.READ));
        grant(new EntityPermission(Country.class, EntityPermission.READ));
        grant(new EntityPermission(Province.class, EntityPermission.READ));

        grant(new IServiceExecutePermission(SiteThemeServices.class));

        grant(new IServiceExecutePermission(PortalPolicyRetrieveService.class));

        grant(VistaBasicBehavior.ResidentPortalPasswordChangeRequired, new IServiceExecutePermission(PortalPasswordResetService.class));

        grant(VistaBasicBehavior.ResidentPortal, new IServiceExecutePermission(PasswordChangeUserService.class));
        grant(VistaBasicBehavior.ProspectivePortal, new IServiceExecutePermission(PasswordChangeUserService.class));

        // Old TODO remove
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(ReferenceDataService.class));

        grant(VistaCustomerBehavior.Prospective, new EntityPermission(Country.class, EntityPermission.READ));
        grant(VistaCustomerBehavior.Prospective, new EntityPermission(Province.class, EntityPermission.READ));

        InstanceAccess userEntityAccess = new UserEntityInstanceAccess();
        grant(VistaCustomerBehavior.Prospective, new EntityPermission(OnlineApplication.class, userEntityAccess, CRUD));

        InstanceAccess applicationEntityAccess = new ApplicationEntityInstanceAccess();

        grant(VistaCustomerBehavior.Prospective, new EntityPermission(OrganizationPoliciesNode.class, EntityPermission.READ));
        grant(VistaCustomerBehavior.Prospective, new EntityPermission(IdentificationDocumentType.class, EntityPermission.READ));
        grant(VistaCustomerBehavior.Prospective, new EntityPermission(LeaseTermTenant.class, applicationEntityAccess, CRUD));

        grant(VistaCustomerBehavior.ProspectiveApplicant, VistaCustomerBehavior.Prospective);
        grant(VistaCustomerBehavior.ProspectiveCoApplicant, VistaCustomerBehavior.Prospective);
        grant(VistaCustomerBehavior.Guarantor, VistaCustomerBehavior.Prospective);

        // Submitted prospective:
        grant(VistaCustomerBehavior.ProspectiveSubmitted, new EntityPermission(OrganizationPoliciesNode.class, EntityPermission.READ));
        grant(VistaCustomerBehavior.ProspectiveSubmitted, new EntityPermission(IdentificationDocumentType.class, EntityPermission.READ));

        grant(VistaCustomerBehavior.ProspectiveSubmittedApplicant, VistaCustomerBehavior.ProspectiveSubmitted);
        grant(VistaCustomerBehavior.ProspectiveSubmittedCoApplicant, VistaCustomerBehavior.ProspectiveSubmitted);
        grant(VistaCustomerBehavior.GuarantorSubmitted, VistaCustomerBehavior.ProspectiveSubmitted);

        // -------------

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(CreditCardValidationService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(ResidentProfileCrudService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(ResidentAccountCrudService.class));

        //========================= My Community

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(BillingService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(
                com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentMethodWizardService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(
                com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentWizardService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(AutoPayWizardService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(
                com.propertyvista.portal.rpc.portal.resident.services.SelfRegistrationBuildingsSourceService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(BillingService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(ResidentSummaryService.class));

        grant(VistaCustomerBehavior.Tenant,
                new IServiceExecutePermission(com.propertyvista.portal.rpc.portal.resident.services.services.InsuranceService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(InsuranceCertificateScanUploadService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(GeneralInsurancePolicyCrudService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(MaintenanceRequestCrudService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(ResidentPictureUploadService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(DeferredProcessService.class));

        grant(VistaCustomerBehavior.Tenant, new EntityPermission(CustomerPicture.class, CRUD));

        //========================= My Community Prospect Portal

        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(ApplicationStatusService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(ApplicationWizardService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(ApplicationContextSelectionService.class));

        //=======================================

        // Tenant Insurance and TenantSure
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(GeneralInsurancePolicy.class, CRUD));
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(InsuranceCertificateScan.class, EntityPermission.READ));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(GeneralInsurancePolicyCrudService.class));

        grant(VistaCustomerBehavior.Tenant, new EntityPermission(TenantSureInsurancePolicy.class, CRUD));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantSureInsurancePolicyCrudService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantSurePaymentMethodCrudService.class));

        // Billing and Payments
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(Bill.class, EntityPermission.READ));
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(InvoiceLineItem.class, EntityPermission.READ));

        grant(VistaCustomerBehavior.Tenant, new EntityPermission(PaymentRecord.class, CRUD));
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(LeasePaymentMethod.class, CRUD));
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(AutopayAgreement.class, CRUD));
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(AutopayAgreementCoveredItem.class, CRUD));

        grant(VistaCustomerBehavior.Tenant, new EntityPermission(YardiServiceRequest.class, CRUD));

        grant(VistaCustomerBehavior.Tenant, new EntityPermission(MaintenanceRequest.class, CRUD));
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(MaintenanceRequestCategory.class, EntityPermission.READ));

        grant(VistaCustomerBehavior.Tenant, new EntityPermission(ApplicationDocumentFile.class, CRUD));

        grant(VistaCustomerBehavior.TenantPrimary, VistaCustomerBehavior.Tenant);
        grant(VistaCustomerBehavior.TenantSecondary, VistaCustomerBehavior.Tenant);

        // Data Access
        grant(VistaCustomerBehavior.Tenant, VistaDataAccessBehavior.TenantInPortal);
        grant(VistaDataAccessBehavior.TenantInPortal, new CustomrPictureTenantDatasetAccessRule(), CustomerPicture.class);
        grant(VistaDataAccessBehavior.TenantInPortal, new LeasePaymentMethodTenantDatasetAccessRule(), LeasePaymentMethod.class);
        grant(VistaDataAccessBehavior.TenantInPortal, new AutopayAgreementTenantDatasetAccessRule(), AutopayAgreement.class);
        grant(VistaDataAccessBehavior.TenantInPortal, new GeneralInsurancePolicyDatasetAccessRule(), GeneralInsurancePolicy.class);
        grant(VistaDataAccessBehavior.TenantInPortal, new InsuranceCertificateScanDatasetAccessRule(), InsuranceCertificateScan.class);

        grant(new IServiceExecutePermission(PortalContentService.class));
        freeze();
    }
}

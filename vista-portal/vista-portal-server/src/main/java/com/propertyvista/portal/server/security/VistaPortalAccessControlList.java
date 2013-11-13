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
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationStatusCrudService;
import com.propertyvista.portal.rpc.portal.prospect.services.ContactsStepService;
import com.propertyvista.portal.rpc.portal.prospect.services.FinancialStepService;
import com.propertyvista.portal.rpc.portal.prospect.services.OptionsStepService;
import com.propertyvista.portal.rpc.portal.prospect.services.PaymentStepService;
import com.propertyvista.portal.rpc.portal.prospect.services.PeopleStepService;
import com.propertyvista.portal.rpc.portal.prospect.services.PersonalInfoAStepService;
import com.propertyvista.portal.rpc.portal.prospect.services.PersonalInfoBStepService;
import com.propertyvista.portal.rpc.portal.prospect.services.PmcCustomStepService;
import com.propertyvista.portal.rpc.portal.prospect.services.SummaryStepService;
import com.propertyvista.portal.rpc.portal.prospect.services.UnitStepService;
import com.propertyvista.portal.rpc.portal.services.LeaseContextSelectionService;
import com.propertyvista.portal.rpc.portal.services.PasswordChangeUserService;
import com.propertyvista.portal.rpc.portal.services.PortalPasswordResetService;
import com.propertyvista.portal.rpc.portal.services.PortalPolicyRetrieveService;
import com.propertyvista.portal.rpc.portal.services.PortalVistaTermsService;
import com.propertyvista.portal.rpc.portal.services.SiteThemeServices;
import com.propertyvista.portal.rpc.portal.services.resident.AutoPayRetrieveService;
import com.propertyvista.portal.rpc.portal.services.resident.BillingHistoryService;
import com.propertyvista.portal.rpc.portal.services.resident.CommunicationCenterService;
import com.propertyvista.portal.rpc.portal.services.resident.MaintenanceService;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodCrudService;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodRetrieveService;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodWizardService;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentRetrieveService;
import com.propertyvista.portal.rpc.portal.services.resident.PortalContentService;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentListService;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentWizardService;
import com.propertyvista.portal.rpc.portal.services.resident.TenantInsuranceByOtherProviderManagementService;
import com.propertyvista.portal.rpc.portal.services.resident.TenantInsuranceService;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSureManagementService;
import com.propertyvista.portal.rpc.portal.services.resident.ViewBillService;
import com.propertyvista.portal.rpc.portal.services.resident.WeatherService;
import com.propertyvista.portal.rpc.portal.web.services.ProspectAuthenticationService;
import com.propertyvista.portal.rpc.portal.web.services.ResidentAuthenticationService;
import com.propertyvista.portal.rpc.portal.web.services.ResidentPictureUploadService;
import com.propertyvista.portal.rpc.portal.web.services.financial.AutoPayWizardService;
import com.propertyvista.portal.rpc.portal.web.services.financial.BillingService;
import com.propertyvista.portal.rpc.portal.web.services.maintenance.MaintenanceRequestCrudService;
import com.propertyvista.portal.rpc.portal.web.services.profile.ResidentAccountCrudService;
import com.propertyvista.portal.rpc.portal.web.services.profile.ResidentProfileCrudService;
import com.propertyvista.portal.rpc.portal.web.services.profile.ResidentSummaryService;
import com.propertyvista.portal.rpc.portal.web.services.services.GeneralInsurancePolicyCrudService;
import com.propertyvista.portal.rpc.portal.web.services.services.InsuranceCertificateScanUploadService;
import com.propertyvista.portal.rpc.portal.web.services.services.TenantSureInsurancePolicyCrudService;
import com.propertyvista.portal.rpc.portal.web.services.services.TenantSurePaymentMethodCrudService;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationDocumentUploadService;
import com.propertyvista.portal.rpc.ptapp.services.PtPasswordResetService;
import com.propertyvista.portal.rpc.ptapp.services.PtPolicyRetrieveService;
import com.propertyvista.portal.rpc.shared.services.CreditCardValidationService;
import com.propertyvista.portal.server.security.access.AutopayAgreementTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.CustomrPictureTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.GeneralInsurancePolicyDatasetAccessRule;
import com.propertyvista.portal.server.security.access.InsuranceCertificateScanDatasetAccessRule;
import com.propertyvista.portal.server.security.access.LeasePaymentMethodTenantDatasetAccessRule;
import com.propertyvista.portal.server.security.access.LeaseTenantDatasetAccessRule;
import com.propertyvista.server.common.security.UserEntityInstanceAccess;

public class VistaPortalAccessControlList extends ServletContainerAclBuilder {

    private final static int CRUD = EntityPermission.CREATE | EntityPermission.READ | EntityPermission.UPDATE;

    public VistaPortalAccessControlList() {

        grant(new IServiceExecutePermission(ResidentAuthenticationService.class));
        grant(new IServiceExecutePermission(PortalVistaTermsService.class));

        grant(new IServiceExecutePermission(com.propertyvista.portal.rpc.portal.web.services.ResidentSelfRegistrationService.class));
        grant(new IServiceExecutePermission(com.propertyvista.portal.rpc.portal.web.services.SelfRegistrationBuildingsSourceService.class));

        grant(new EntityPermission(SelfRegistrationBuildingDTO.class, EntityPermission.READ));

        grant(new IServiceExecutePermission(ProspectAuthenticationService.class));

        grant(VistaCustomerBehavior.LeaseSelectionRequired, new IServiceExecutePermission(LeaseContextSelectionService.class));
        grant(VistaCustomerBehavior.HasMultipleLeases, new IServiceExecutePermission(LeaseContextSelectionService.class));

        // Old TODO remove
        grant(new IServiceExecutePermission(ReferenceDataService.class));
        grant(new EntityPermission(City.class, EntityPermission.READ));
        grant(new EntityPermission(Country.class, EntityPermission.READ));
        grant(new EntityPermission(Province.class, EntityPermission.READ));

        grant(new IServiceExecutePermission(SiteThemeServices.class));

        grant(new IServiceExecutePermission(PortalPolicyRetrieveService.class));

        grant(VistaBasicBehavior.ProspectivePortalPasswordChangeRequired, new IServiceExecutePermission(PtPasswordResetService.class));
        grant(VistaBasicBehavior.ResidentPortalPasswordChangeRequired, new IServiceExecutePermission(PortalPasswordResetService.class));

        grant(VistaBasicBehavior.ResidentPortal, new IServiceExecutePermission(PasswordChangeUserService.class));
        grant(VistaBasicBehavior.ProspectivePortal, new IServiceExecutePermission(PasswordChangeUserService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(PtPolicyRetrieveService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(ApplicationDocumentUploadService.class));

        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(UnitStepService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(OptionsStepService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(PersonalInfoAStepService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(PersonalInfoBStepService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(FinancialStepService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(PeopleStepService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(ContactsStepService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(PmcCustomStepService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(SummaryStepService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(PaymentStepService.class));

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
        grant(VistaCustomerBehavior.ProspectiveSubmitted, new IServiceExecutePermission(PtPolicyRetrieveService.class));
        grant(VistaCustomerBehavior.ProspectiveSubmitted, new EntityPermission(IdentificationDocumentType.class, EntityPermission.READ));

        grant(VistaCustomerBehavior.ProspectiveSubmittedApplicant, VistaCustomerBehavior.ProspectiveSubmitted);
        grant(VistaCustomerBehavior.ProspectiveSubmittedCoApplicant, VistaCustomerBehavior.ProspectiveSubmitted);
        grant(VistaCustomerBehavior.GuarantorSubmitted, VistaCustomerBehavior.ProspectiveSubmitted);

        // -------------

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(BillingHistoryService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(ViewBillService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(CreditCardValidationService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PaymentRetrieveService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PaymentMethodCrudService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PaymentMethodWizardService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PaymentMethodRetrieveService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PreauthorizedPaymentListService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PreauthorizedPaymentWizardService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(AutoPayRetrieveService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(MaintenanceService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(ResidentProfileCrudService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(ResidentAccountCrudService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(CommunicationCenterService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(ApplicationDocumentUploadService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(WeatherService.class));

        //========================= My Community

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(BillingService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(com.propertyvista.portal.rpc.portal.web.services.financial.PaymentService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(
                com.propertyvista.portal.rpc.portal.web.services.financial.PaymentMethodWizardService.class));
        grant(VistaCustomerBehavior.Tenant,
                new IServiceExecutePermission(com.propertyvista.portal.rpc.portal.web.services.financial.PaymentWizardService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(AutoPayWizardService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(
                com.propertyvista.portal.rpc.portal.web.services.SelfRegistrationBuildingsSourceService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(BillingService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(MaintenanceService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(ResidentSummaryService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(com.propertyvista.portal.rpc.portal.web.services.services.InsuranceService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(InsuranceCertificateScanUploadService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(GeneralInsurancePolicyCrudService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(MaintenanceRequestCrudService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(ResidentPictureUploadService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(DeferredProcessService.class));

        grant(VistaCustomerBehavior.Tenant, new EntityPermission(CustomerPicture.class, CRUD));

        //========================= My Community Prospect Portal

        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(ApplicationStatusCrudService.class));

        //=======================================

        // Tenant Insurance and TenantSure
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(GeneralInsurancePolicy.class, CRUD));
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(InsuranceCertificateScan.class, EntityPermission.READ));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(GeneralInsurancePolicyCrudService.class));

        grant(VistaCustomerBehavior.Tenant, new EntityPermission(TenantSureInsurancePolicy.class, CRUD));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantInsuranceService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantInsuranceByOtherProviderManagementService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantSureInsurancePolicyCrudService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantSurePaymentMethodCrudService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantSureManagementService.class));

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
        grant(VistaDataAccessBehavior.TenantInPortal, new LeaseTenantDatasetAccessRule(), Lease.class);
        grant(VistaDataAccessBehavior.TenantInPortal, new CustomrPictureTenantDatasetAccessRule(), CustomerPicture.class);
        grant(VistaDataAccessBehavior.TenantInPortal, new LeasePaymentMethodTenantDatasetAccessRule(), LeasePaymentMethod.class);
        grant(VistaDataAccessBehavior.TenantInPortal, new AutopayAgreementTenantDatasetAccessRule(), AutopayAgreement.class);
        grant(VistaDataAccessBehavior.TenantInPortal, new GeneralInsurancePolicyDatasetAccessRule(), GeneralInsurancePolicy.class);
        grant(VistaDataAccessBehavior.TenantInPortal, new InsuranceCertificateScanDatasetAccessRule(), InsuranceCertificateScan.class);

        grant(new IServiceExecutePermission(PortalContentService.class));
        freeze();
    }
}

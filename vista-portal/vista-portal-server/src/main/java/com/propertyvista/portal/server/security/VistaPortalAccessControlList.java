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

import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.security.InstanceAccess;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.ServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.YardiServiceRequest;
import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.insurance.InsuranceGeneric;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.PaymentInformation;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.services.LeaseContextSelectionService;
import com.propertyvista.portal.rpc.portal.services.PasswordChangeUserService;
import com.propertyvista.portal.rpc.portal.services.PortalAuthenticationService;
import com.propertyvista.portal.rpc.portal.services.PortalPasswordResetService;
import com.propertyvista.portal.rpc.portal.services.PortalPolicyRetrieveService;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;
import com.propertyvista.portal.rpc.portal.services.PortalVistaTermsService;
import com.propertyvista.portal.rpc.portal.services.SiteThemeServices;
import com.propertyvista.portal.rpc.portal.services.resident.BillSummaryService;
import com.propertyvista.portal.rpc.portal.services.resident.BillingHistoryService;
import com.propertyvista.portal.rpc.portal.services.resident.CommunicationCenterService;
import com.propertyvista.portal.rpc.portal.services.resident.DashboardService;
import com.propertyvista.portal.rpc.portal.services.resident.FooterContentService;
import com.propertyvista.portal.rpc.portal.services.resident.MaintenanceService;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodCrudService;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodSubmittedService;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentMethodWizardService;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentSubmittedService;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentWizardService;
import com.propertyvista.portal.rpc.portal.services.resident.PersonalInfoCrudService;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentListService;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentSubmittedService;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentWizardService;
import com.propertyvista.portal.rpc.portal.services.resident.SelfRegistrationBuildingsSourceService;
import com.propertyvista.portal.rpc.portal.services.resident.TenantInsuranceByOtherProviderManagementService;
import com.propertyvista.portal.rpc.portal.services.resident.TenantInsuranceService;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSureManagementService;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSurePurchaseService;
import com.propertyvista.portal.rpc.portal.services.resident.ViewBillService;
import com.propertyvista.portal.rpc.portal.services.resident.WeatherService;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationDocumentUploadService;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationSelectionService;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationService;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationStatusService;
import com.propertyvista.portal.rpc.ptapp.services.PtAuthenticationService;
import com.propertyvista.portal.rpc.ptapp.services.PtPasswordResetService;
import com.propertyvista.portal.rpc.ptapp.services.PtPolicyRetrieveService;
import com.propertyvista.portal.rpc.ptapp.services.steps.ApartmentService;
import com.propertyvista.portal.rpc.ptapp.services.steps.ChargesService;
import com.propertyvista.portal.rpc.ptapp.services.steps.PaymentService;
import com.propertyvista.portal.rpc.ptapp.services.steps.SummaryService;
import com.propertyvista.portal.rpc.ptapp.services.steps.TenantFinancialService;
import com.propertyvista.portal.rpc.ptapp.services.steps.TenantInfoService;
import com.propertyvista.portal.rpc.ptapp.services.steps.TenantService;
import com.propertyvista.portal.rpc.ptapp.services.steps.welcomewizardmockup.InsuranceService;
import com.propertyvista.portal.rpc.ptapp.services.steps.welcomewizardmockup.LeaseReviewService;
import com.propertyvista.portal.rpc.ptapp.services.steps.welcomewizardmockup.MoveInScheduleService;
import com.propertyvista.portal.rpc.ptapp.services.steps.welcomewizardmockup.ResetWizardService;
import com.propertyvista.portal.rpc.shared.services.CreditCardValidationService;
import com.propertyvista.server.common.security.UserEntityInstanceAccess;

public class VistaPortalAccessControlList extends ServletContainerAclBuilder {

    private final static int CRUD = EntityPermission.CREATE | EntityPermission.READ | EntityPermission.UPDATE;

    public VistaPortalAccessControlList() {
        if (VistaTODO.enableWelcomeWizardDemoMode) {
            grant(new IServiceExecutePermission(LeaseReviewService.class));
            grant(new IServiceExecutePermission(InsuranceService.class));
            grant(new IServiceExecutePermission(MoveInScheduleService.class));
            grant(new IServiceExecutePermission(ResetWizardService.class));
        }

        grant(new IServiceExecutePermission(PortalAuthenticationService.class));
        grant(new IServiceExecutePermission(PortalVistaTermsService.class));
        grant(new IServiceExecutePermission(SelfRegistrationBuildingsSourceService.class));
        grant(new EntityPermission(SelfRegistrationBuildingDTO.class, EntityPermission.READ));

        grant(new IServiceExecutePermission(PtAuthenticationService.class));

        grant(VistaCustomerBehavior.LeaseSelectionRequired, new IServiceExecutePermission(LeaseContextSelectionService.class));
        grant(VistaCustomerBehavior.HasMultipleLeases, new IServiceExecutePermission(LeaseContextSelectionService.class));
        grant(VistaCustomerBehavior.ApplicationSelectionRequired, new IServiceExecutePermission(ApplicationSelectionService.class));
        grant(VistaCustomerBehavior.HasMultipleApplications, new IServiceExecutePermission(ApplicationSelectionService.class));

        // Old TODO remove
        grant(new ServiceExecutePermission(EntityServices.Query.class));
        grant(new EntityPermission(City.class, EntityPermission.READ));
        grant(new EntityPermission(Country.class, EntityPermission.READ));
        grant(new EntityPermission(Province.class, EntityPermission.READ));

        grant(new IServiceExecutePermission(PortalSiteServices.class));

        grant(new IServiceExecutePermission(SiteThemeServices.class));

        grant(new IServiceExecutePermission(PortalPolicyRetrieveService.class));

        grant(VistaBasicBehavior.ProspectiveAppPasswordChangeRequired, new IServiceExecutePermission(PtPasswordResetService.class));
        grant(VistaBasicBehavior.TenantPortalPasswordChangeRequired, new IServiceExecutePermission(PortalPasswordResetService.class));

        grant(VistaBasicBehavior.TenantPortal, new IServiceExecutePermission(PasswordChangeUserService.class));
        grant(VistaBasicBehavior.ProspectiveApp, new IServiceExecutePermission(PasswordChangeUserService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(PtPolicyRetrieveService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(ApplicationService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(ApplicationDocumentUploadService.class));

        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(ApartmentService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(TenantService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(TenantInfoService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(TenantFinancialService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(ChargesService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(SummaryService.class));
        grant(VistaCustomerBehavior.Prospective, new IServiceExecutePermission(PaymentService.class));

        // Old TODO remove
        grant(VistaCustomerBehavior.Prospective, new ServiceExecutePermission(EntityServices.Query.class));

        grant(VistaCustomerBehavior.Prospective, new EntityPermission(Country.class, EntityPermission.READ));
        grant(VistaCustomerBehavior.Prospective, new EntityPermission(Province.class, EntityPermission.READ));

        InstanceAccess userEntityAccess = new UserEntityInstanceAccess();
        grant(VistaCustomerBehavior.Prospective, new EntityPermission(OnlineApplication.class, userEntityAccess, CRUD));

        InstanceAccess applicationEntityAccess = new ApplicationEntityInstanceAccess();

        grant(VistaCustomerBehavior.Prospective, new EntityPermission(OrganizationPoliciesNode.class, EntityPermission.READ));
        grant(VistaCustomerBehavior.Prospective, new EntityPermission(IdentificationDocumentType.class, EntityPermission.READ));
        grant(VistaCustomerBehavior.Prospective, new EntityPermission(LeaseTermTenant.class, applicationEntityAccess, CRUD));
        grant(VistaCustomerBehavior.Prospective, new EntityPermission(Summary.class, applicationEntityAccess, CRUD));
        grant(VistaCustomerBehavior.Prospective, new EntityPermission(Charges.class, applicationEntityAccess, CRUD));
        grant(VistaCustomerBehavior.Prospective, new EntityPermission(PaymentInformation.class, applicationEntityAccess, CRUD));

        grant(VistaCustomerBehavior.ProspectiveApplicant, VistaCustomerBehavior.Prospective);
        grant(VistaCustomerBehavior.ProspectiveCoApplicant, VistaCustomerBehavior.Prospective);
        grant(VistaCustomerBehavior.Guarantor, VistaCustomerBehavior.Prospective);

        // Submitted prospective:
        grant(VistaCustomerBehavior.ProspectiveSubmitted, new EntityPermission(OrganizationPoliciesNode.class, EntityPermission.READ));
        grant(VistaCustomerBehavior.ProspectiveSubmitted, new IServiceExecutePermission(PtPolicyRetrieveService.class));
        grant(VistaCustomerBehavior.ProspectiveSubmitted, new EntityPermission(IdentificationDocumentType.class, EntityPermission.READ));
        grant(VistaCustomerBehavior.ProspectiveSubmitted, new IServiceExecutePermission(ApplicationService.class));
        grant(VistaCustomerBehavior.ProspectiveSubmitted, new IServiceExecutePermission(ApplicationStatusService.class));
        grant(VistaCustomerBehavior.ProspectiveSubmitted, new IServiceExecutePermission(SummaryService.class));
        //grant(VistaTenantBehavior.ProspectiveSubmitted, new IServiceExecutePermission(ChargesService.class));

        grant(VistaCustomerBehavior.ProspectiveSubmitted, new EntityPermission(Summary.class, applicationEntityAccess, EntityPermission.READ));
        grant(VistaCustomerBehavior.ProspectiveSubmitted, new EntityPermission(Charges.class, applicationEntityAccess, EntityPermission.READ));

        grant(VistaCustomerBehavior.ProspectiveSubmittedApplicant, VistaCustomerBehavior.ProspectiveSubmitted);
        grant(VistaCustomerBehavior.ProspectiveSubmittedCoApplicant, VistaCustomerBehavior.ProspectiveSubmitted);
        grant(VistaCustomerBehavior.GuarantorSubmitted, VistaCustomerBehavior.ProspectiveSubmitted);

        // -------------
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(DashboardService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(BillingHistoryService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(BillSummaryService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(ViewBillService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PaymentWizardService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(CreditCardValidationService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PaymentSubmittedService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PaymentMethodCrudService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PaymentMethodWizardService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PaymentMethodSubmittedService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PreauthorizedPaymentListService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PreauthorizedPaymentWizardService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PreauthorizedPaymentSubmittedService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(MaintenanceService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PersonalInfoCrudService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(CommunicationCenterService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(ApplicationDocumentUploadService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(WeatherService.class));

        // Tenant Insurance and TenantSure
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(InsuranceGeneric.class, CRUD));
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(InsuranceTenantSure.class, CRUD));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantInsuranceService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantInsuranceByOtherProviderManagementService.class));

        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantSurePurchaseService.class));
        grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantSureManagementService.class));

        // Billing and Payments
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(Bill.class, EntityPermission.READ));
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(InvoiceLineItem.class, EntityPermission.READ));

        grant(VistaCustomerBehavior.Tenant, new EntityPermission(PaymentRecord.class, CRUD));
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(LeasePaymentMethod.class, CRUD));
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(PreauthorizedPayment.class, CRUD));
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(PreauthorizedPaymentCoveredItem.class, CRUD));

        grant(VistaCustomerBehavior.Tenant, new EntityPermission(YardiServiceRequest.class, CRUD));

        grant(VistaCustomerBehavior.Tenant, new EntityPermission(MaintenanceRequest.class, CRUD));
        grant(VistaCustomerBehavior.Tenant, new EntityPermission(MaintenanceRequestCategory.class, EntityPermission.READ));

        grant(VistaCustomerBehavior.Tenant, new EntityPermission(ApplicationDocumentFile.class, CRUD));

        grant(VistaCustomerBehavior.TenantPrimary, VistaCustomerBehavior.Tenant);
        grant(VistaCustomerBehavior.TenantSecondary, VistaCustomerBehavior.Tenant);

        grant(new IServiceExecutePermission(FooterContentService.class));
        freeze();
    }
}

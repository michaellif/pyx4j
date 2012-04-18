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

import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.IssueElement;
import com.propertyvista.domain.maintenance.IssueRepairSubject;
import com.propertyvista.domain.maintenance.IssueSubjectDetails;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.PaymentInformation;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.rpc.portal.services.PersonalInfoCrudService;
import com.propertyvista.portal.rpc.portal.services.PortalAuthenticationService;
import com.propertyvista.portal.rpc.portal.services.PortalPasswordResetService;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;
import com.propertyvista.portal.rpc.portal.services.SiteThemeServices;
import com.propertyvista.portal.rpc.portal.services.TenantDashboardService;
import com.propertyvista.portal.rpc.portal.services.TenantMaintenanceService;
import com.propertyvista.portal.rpc.portal.services.TenantPasswordChangeUserService;
import com.propertyvista.portal.rpc.portal.services.TenantPaymentMethodCrudService;
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
import com.propertyvista.server.common.security.UserEntityInstanceAccess;

public class VistaPortalAccessControlList extends ServletContainerAclBuilder {

    private final static int CRUD = EntityPermission.CREATE | EntityPermission.READ | EntityPermission.UPDATE;

    public VistaPortalAccessControlList() {
        if (VistaTODO.enableWelcomeWizardDemoMode) {
            grant(new IServiceExecutePermission("*"));
            grant(new ServiceExecutePermission(EntityServices.class, "*"));
            grant(new ServiceExecutePermission("*"));
            grant(new EntityPermission("*", EntityPermission.ALL));
            grant(new EntityPermission("*", EntityPermission.READ));
        } else {
            grant(new IServiceExecutePermission(PortalAuthenticationService.class));
            grant(new IServiceExecutePermission(PtAuthenticationService.class));

            grant(VistaCustomerBehavior.ApplicationSelectionRequired, new IServiceExecutePermission(ApplicationSelectionService.class));

            // Old TODO remove
            grant(new ServiceExecutePermission(EntityServices.Query.class));
            grant(new EntityPermission(City.class, EntityPermission.READ));
            grant(new EntityPermission(Country.class, EntityPermission.READ));
            grant(new EntityPermission(Province.class, EntityPermission.READ));

            grant(new IServiceExecutePermission(PortalSiteServices.class));

            grant(new IServiceExecutePermission(SiteThemeServices.class));

            grant(VistaBasicBehavior.ProspectiveAppPasswordChangeRequired, new IServiceExecutePermission(PtPasswordResetService.class));
            grant(VistaBasicBehavior.TenantPortalPasswordChangeRequired, new IServiceExecutePermission(PortalPasswordResetService.class));

            grant(VistaBasicBehavior.TenantPortal, new IServiceExecutePermission(TenantPasswordChangeUserService.class));
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
            grant(VistaCustomerBehavior.Prospective, new EntityPermission(Tenant.class, applicationEntityAccess, CRUD));
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

            grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantDashboardService.class));
            grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(PersonalInfoCrudService.class));
            grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantPaymentMethodCrudService.class));
            grant(VistaCustomerBehavior.Tenant, new IServiceExecutePermission(TenantMaintenanceService.class));

            grant(VistaCustomerBehavior.Tenant, new EntityPermission(IssueElement.class, EntityPermission.READ));
            grant(VistaCustomerBehavior.Tenant, new EntityPermission(IssueRepairSubject.class, EntityPermission.READ));
            grant(VistaCustomerBehavior.Tenant, new EntityPermission(IssueSubjectDetails.class, EntityPermission.READ));
            grant(VistaCustomerBehavior.Tenant, new EntityPermission(IssueClassification.class, EntityPermission.READ));

            grant(VistaCustomerBehavior.TenantPrimary, VistaCustomerBehavior.Tenant);
            grant(VistaCustomerBehavior.TenantSecondary, VistaCustomerBehavior.Tenant);

            freeze();
        }
    }
}

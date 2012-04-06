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
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
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
        grant(new IServiceExecutePermission(PortalAuthenticationService.class));
        grant(new IServiceExecutePermission(PtAuthenticationService.class));

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
        grant(VistaTenantBehavior.Prospective, new IServiceExecutePermission(PtPolicyRetrieveService.class));
        grant(VistaTenantBehavior.Prospective, new IServiceExecutePermission(ApplicationService.class));
        grant(VistaTenantBehavior.Prospective, new IServiceExecutePermission(ApplicationDocumentUploadService.class));

        grant(VistaTenantBehavior.Prospective, new IServiceExecutePermission(ApartmentService.class));
        grant(VistaTenantBehavior.Prospective, new IServiceExecutePermission(TenantService.class));
        grant(VistaTenantBehavior.Prospective, new IServiceExecutePermission(TenantInfoService.class));
        grant(VistaTenantBehavior.Prospective, new IServiceExecutePermission(TenantFinancialService.class));
        grant(VistaTenantBehavior.Prospective, new IServiceExecutePermission(ChargesService.class));
        grant(VistaTenantBehavior.Prospective, new IServiceExecutePermission(SummaryService.class));
        grant(VistaTenantBehavior.Prospective, new IServiceExecutePermission(PaymentService.class));

        // Old TODO remove
        grant(VistaTenantBehavior.Prospective, new ServiceExecutePermission(EntityServices.Query.class));

        grant(VistaTenantBehavior.Prospective, new EntityPermission(Country.class, EntityPermission.READ));
        grant(VistaTenantBehavior.Prospective, new EntityPermission(Province.class, EntityPermission.READ));

        InstanceAccess userEntityAccess = new UserEntityInstanceAccess();
        grant(VistaTenantBehavior.Prospective, new EntityPermission(OnlineApplication.class, userEntityAccess, CRUD));

        InstanceAccess applicationEntityAccess = new ApplicationEntityInstanceAccess();

        grant(VistaTenantBehavior.Prospective, new EntityPermission(OrganizationPoliciesNode.class, EntityPermission.READ));
        grant(VistaTenantBehavior.Prospective, new EntityPermission(IdentificationDocumentType.class, EntityPermission.READ));
        grant(VistaTenantBehavior.Prospective, new EntityPermission(TenantInLease.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Prospective, new EntityPermission(Summary.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Prospective, new EntityPermission(Charges.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Prospective, new EntityPermission(PaymentInformation.class, applicationEntityAccess, CRUD));

        grant(VistaTenantBehavior.ProspectiveApplicant, VistaTenantBehavior.Prospective);
        grant(VistaTenantBehavior.ProspectiveCoApplicant, VistaTenantBehavior.Prospective);
        grant(VistaTenantBehavior.Guarantor, VistaTenantBehavior.Prospective);

        // Submitted prospective:
        grant(VistaTenantBehavior.ProspectiveSubmitted, new EntityPermission(OrganizationPoliciesNode.class, EntityPermission.READ));
        grant(VistaTenantBehavior.ProspectiveSubmitted, new IServiceExecutePermission(PtPolicyRetrieveService.class));
        grant(VistaTenantBehavior.ProspectiveSubmitted, new EntityPermission(IdentificationDocumentType.class, EntityPermission.READ));
        grant(VistaTenantBehavior.ProspectiveSubmitted, new IServiceExecutePermission(ApplicationService.class));
        grant(VistaTenantBehavior.ProspectiveSubmitted, new IServiceExecutePermission(ApplicationStatusService.class));
        grant(VistaTenantBehavior.ProspectiveSubmitted, new IServiceExecutePermission(SummaryService.class));
        //grant(VistaTenantBehavior.ProspectiveSubmitted, new IServiceExecutePermission(ChargesService.class));

        grant(VistaTenantBehavior.ProspectiveSubmitted, new EntityPermission(Summary.class, applicationEntityAccess, EntityPermission.READ));
        grant(VistaTenantBehavior.ProspectiveSubmitted, new EntityPermission(Charges.class, applicationEntityAccess, EntityPermission.READ));

        grant(VistaTenantBehavior.ProspectiveSubmittedApplicant, VistaTenantBehavior.ProspectiveSubmitted);
        grant(VistaTenantBehavior.ProspectiveSubmittedCoApplicant, VistaTenantBehavior.ProspectiveSubmitted);
        grant(VistaTenantBehavior.GuarantorSubmitted, VistaTenantBehavior.ProspectiveSubmitted);

        // -------------

        grant(VistaTenantBehavior.Tenant, new IServiceExecutePermission(TenantDashboardService.class));
        grant(VistaTenantBehavior.Tenant, new IServiceExecutePermission(PersonalInfoCrudService.class));
        grant(VistaTenantBehavior.Tenant, new IServiceExecutePermission(TenantPaymentMethodCrudService.class));
        grant(VistaTenantBehavior.Tenant, new IServiceExecutePermission(TenantMaintenanceService.class));

        grant(VistaTenantBehavior.Tenant, new EntityPermission(IssueElement.class, EntityPermission.READ));
        grant(VistaTenantBehavior.Tenant, new EntityPermission(IssueRepairSubject.class, EntityPermission.READ));
        grant(VistaTenantBehavior.Tenant, new EntityPermission(IssueSubjectDetails.class, EntityPermission.READ));
        grant(VistaTenantBehavior.Tenant, new EntityPermission(IssueClassification.class, EntityPermission.READ));

        grant(VistaTenantBehavior.TenantPrimary, VistaTenantBehavior.Tenant);
        grant(VistaTenantBehavior.TenantSecondary, VistaTenantBehavior.Tenant);

        freeze();
    }
}

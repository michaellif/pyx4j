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
import com.pyx4j.security.shared.AllPermissions;
import com.pyx4j.security.shared.CoreBehavior;

import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.IssueElement;
import com.propertyvista.domain.maintenance.IssueRepairSubject;
import com.propertyvista.domain.maintenance.IssueSubjectDetails;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
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
import com.propertyvista.portal.rpc.portal.services.TenantPaymentMethodCrudService;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseListDTO;
import com.propertyvista.portal.rpc.ptapp.services.ActivationService;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationDocumentUploadService;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationService;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationStatusService;
import com.propertyvista.portal.rpc.ptapp.services.PtAuthenticationService;
import com.propertyvista.portal.rpc.ptapp.services.PtPasswordResetService;
import com.propertyvista.portal.rpc.ptapp.services.steps.ApartmentService;
import com.propertyvista.portal.rpc.ptapp.services.steps.ChargesService;
import com.propertyvista.portal.rpc.ptapp.services.steps.PaymentService;
import com.propertyvista.portal.rpc.ptapp.services.steps.SummaryService;
import com.propertyvista.portal.rpc.ptapp.services.steps.TenantFinancialService;
import com.propertyvista.portal.rpc.ptapp.services.steps.TenantInfoService;
import com.propertyvista.portal.rpc.ptapp.services.steps.TenantService;
import com.propertyvista.server.common.security.UserEntityInstanceAccess;
import com.propertyvista.server.domain.ApplicationDocumentBlob;

public class VistaPortalAccessControlList extends ServletContainerAclBuilder {

    private final static int CRUD = EntityPermission.CREATE | EntityPermission.READ | EntityPermission.UPDATE;

    public VistaPortalAccessControlList() {
        grant(new IServiceExecutePermission(ActivationService.class));
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
        grant(VistaTenantBehavior.Prospective, new EntityPermission(Application.class, userEntityAccess, CRUD));

        InstanceAccess applicationEntityAccess = new ApplicationEntityInstanceAccess();

        //TODO Fix ME
        grant(VistaTenantBehavior.Prospective, new AllPermissions());

        grant(VistaTenantBehavior.Prospective, new EntityPermission(ApplicationDocument.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Prospective, new EntityPermission(ApplicationDocumentBlob.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Prospective, new EntityPermission(TenantInLeaseListDTO.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Prospective, new EntityPermission(TenantInLease.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Prospective, new EntityPermission(TenantFinancialDTO.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Prospective, new EntityPermission(Charges.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Prospective, new EntityPermission(SummaryDTO.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Prospective, new EntityPermission(PaymentInformation.class, applicationEntityAccess, CRUD));

        grant(VistaTenantBehavior.ProspectiveApplicant, VistaTenantBehavior.Prospective);
        grant(VistaTenantBehavior.ProspectiveCoApplicant, VistaTenantBehavior.Prospective);

        // Guarantor for prospective tenant:
        grant(VistaTenantBehavior.Guarantor, new IServiceExecutePermission(ApplicationService.class));
        grant(VistaTenantBehavior.Guarantor, new IServiceExecutePermission(ApplicationDocumentUploadService.class));
        grant(VistaTenantBehavior.Guarantor, new IServiceExecutePermission(ApartmentService.class));
        grant(VistaTenantBehavior.Guarantor, new IServiceExecutePermission(TenantService.class));
        grant(VistaTenantBehavior.Guarantor, new IServiceExecutePermission(TenantInfoService.class));
        grant(VistaTenantBehavior.Guarantor, new IServiceExecutePermission(TenantFinancialService.class));
        grant(VistaTenantBehavior.Guarantor, new IServiceExecutePermission(ChargesService.class));
        grant(VistaTenantBehavior.Guarantor, new IServiceExecutePermission(SummaryService.class));
        grant(VistaTenantBehavior.Guarantor, new IServiceExecutePermission(PaymentService.class));

        //TODO Fix ME
        grant(VistaTenantBehavior.Guarantor, new AllPermissions());

        grant(VistaTenantBehavior.Guarantor, new EntityPermission(ApplicationDocument.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Guarantor, new EntityPermission(ApplicationDocumentBlob.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Guarantor, new EntityPermission(TenantInLeaseListDTO.class, applicationEntityAccess, EntityPermission.READ));
        grant(VistaTenantBehavior.Guarantor, new EntityPermission(TenantInfoDTO.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Guarantor, new EntityPermission(TenantFinancialDTO.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.Guarantor, new EntityPermission(Charges.class, applicationEntityAccess, EntityPermission.READ));
        grant(VistaTenantBehavior.Guarantor, new EntityPermission(SummaryDTO.class, applicationEntityAccess, CRUD));

        grant(VistaTenantBehavior.Guarantor, VistaTenantBehavior.Prospective);

        // Submitted prospective:
        grant(VistaTenantBehavior.ProspectiveSubmited, new IServiceExecutePermission(ApplicationService.class));
        grant(VistaTenantBehavior.ProspectiveSubmited, new IServiceExecutePermission(ApplicationStatusService.class));
        grant(VistaTenantBehavior.ProspectiveSubmited, new IServiceExecutePermission(SummaryService.class));
        grant(VistaTenantBehavior.ProspectiveSubmited, new IServiceExecutePermission(ChargesService.class));

        grant(VistaTenantBehavior.ProspectiveSubmited, new EntityPermission(MasterApplication.class, EntityPermission.READ));
        grant(VistaTenantBehavior.ProspectiveSubmited, new EntityPermission(Summary.class, EntityPermission.READ));
        grant(VistaTenantBehavior.ProspectiveSubmited, new EntityPermission(Charges.class, EntityPermission.READ));

        grant(VistaTenantBehavior.ProspectiveApplicantSubmited, VistaTenantBehavior.ProspectiveSubmited);
        grant(VistaTenantBehavior.ProspectiveCoApplicantSubmited, VistaTenantBehavior.ProspectiveSubmited);

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

        grant(CoreBehavior.DEVELOPER, new AllPermissions());

        freeze();
    }
}

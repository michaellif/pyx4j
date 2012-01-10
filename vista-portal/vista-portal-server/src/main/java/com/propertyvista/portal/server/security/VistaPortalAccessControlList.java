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
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.dto.PetsDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.PaymentInformation;
import com.propertyvista.portal.rpc.portal.services.PersonalInfoCrudService;
import com.propertyvista.portal.rpc.portal.services.PortalAuthenticationService;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;
import com.propertyvista.portal.rpc.portal.services.SiteThemeServices;
import com.propertyvista.portal.rpc.portal.services.TenantDashboardService;
import com.propertyvista.portal.rpc.portal.services.TenantMaintenanceService;
import com.propertyvista.portal.rpc.portal.services.TenantPaymentMethodCrudService;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseListDTO;
import com.propertyvista.portal.rpc.ptapp.services.ActivationService;
import com.propertyvista.portal.rpc.ptapp.services.AddonsService;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationDocumentUploadService;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationService;
import com.propertyvista.portal.rpc.ptapp.services.ChargesService;
import com.propertyvista.portal.rpc.ptapp.services.PaymentService;
import com.propertyvista.portal.rpc.ptapp.services.PtAuthenticationService;
import com.propertyvista.portal.rpc.ptapp.services.SummaryService;
import com.propertyvista.portal.rpc.ptapp.services.TenantFinancialService;
import com.propertyvista.portal.rpc.ptapp.services.TenantInfoService;
import com.propertyvista.portal.rpc.ptapp.services.TenantService;
import com.propertyvista.server.common.security.UserEntityInstanceAccess;
import com.propertyvista.server.domain.ApplicationDocumentData;

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

        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(ApplicationService.class));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(ApartmentService.class));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(TenantService.class));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(TenantInfoService.class));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(TenantFinancialService.class));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(AddonsService.class));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(ChargesService.class));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(SummaryService.class));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(PaymentService.class));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(ApplicationDocumentUploadService.class));

        // Old TODO remove
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new ServiceExecutePermission(EntityServices.Query.class));

        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new EntityPermission(Country.class, EntityPermission.READ));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new EntityPermission(Province.class, EntityPermission.READ));

        InstanceAccess userEntityAccess = new UserEntityInstanceAccess();
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new EntityPermission(Application.class, userEntityAccess, CRUD));

        InstanceAccess applicationEntityAccess = new ApplicationEntityInstanceAccess();

        //TODO Fix ME
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new AllPermissions());

        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new EntityPermission(ApplicationDocument.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new EntityPermission(ApplicationDocumentData.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new EntityPermission(TenantInLeaseListDTO.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new EntityPermission(TenantInLease.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new EntityPermission(PetsDTO.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new EntityPermission(TenantFinancialDTO.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new EntityPermission(Charges.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new EntityPermission(SummaryDTO.class, applicationEntityAccess, CRUD));
        grant(VistaTenantBehavior.PROSPECTIVE_TENANT, new EntityPermission(PaymentInformation.class, applicationEntityAccess, CRUD));

        grant(VistaTenantBehavior.TENANT, new IServiceExecutePermission(TenantDashboardService.class));
        grant(VistaTenantBehavior.TENANT, new IServiceExecutePermission(PersonalInfoCrudService.class));
        grant(VistaTenantBehavior.TENANT, new IServiceExecutePermission(TenantPaymentMethodCrudService.class));
        grant(VistaTenantBehavior.TENANT, new IServiceExecutePermission(TenantMaintenanceService.class));

        grant(VistaTenantBehavior.TENANT, new EntityPermission(IssueElement.class, EntityPermission.READ));
        grant(VistaTenantBehavior.TENANT, new EntityPermission(IssueRepairSubject.class, EntityPermission.READ));
        grant(VistaTenantBehavior.TENANT, new EntityPermission(IssueSubjectDetails.class, EntityPermission.READ));
        grant(VistaTenantBehavior.TENANT, new EntityPermission(IssueClassification.class, EntityPermission.READ));

        grant(CoreBehavior.DEVELOPER, new AllPermissions());

        freeze();
    }
}

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

import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.dto.PetsDTO;
import com.propertyvista.portal.domain.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.PaymentInfo;
import com.propertyvista.portal.rpc.portal.services.AuthenticationService;
import com.propertyvista.portal.rpc.portal.services.MaintenanceRequestCrudService;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseDTO;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseListDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantFinancialDTO;
import com.propertyvista.portal.rpc.ptapp.services.ActivationService;
import com.propertyvista.portal.rpc.ptapp.services.AddonsService;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationService;
import com.propertyvista.portal.rpc.ptapp.services.ChargesService;
import com.propertyvista.portal.rpc.ptapp.services.PaymentService;
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
        grant(new IServiceExecutePermission(AuthenticationService.class));

        // Old TODO remove
        grant(new ServiceExecutePermission(EntityServices.Query.class));
        grant(new EntityPermission(City.class, EntityPermission.READ));
        grant(new EntityPermission(Country.class, EntityPermission.READ));
        grant(new EntityPermission(Province.class, EntityPermission.READ));

        grant(new IServiceExecutePermission(PortalSiteServices.class));

        grant(VistaBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(ApplicationService.class));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(ApartmentService.class));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(TenantService.class));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(TenantInfoService.class));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(TenantFinancialService.class));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(AddonsService.class));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(ChargesService.class));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(SummaryService.class));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(PaymentService.class));
        //TODO this service should be granted to a tenant
        grant(VistaBehavior.PROSPECTIVE_TENANT, new IServiceExecutePermission(MaintenanceRequestCrudService.class));

        // Old TODO remove
        grant(VistaBehavior.PROSPECTIVE_TENANT, new ServiceExecutePermission(EntityServices.Query.class));

        grant(VistaBehavior.PROSPECTIVE_TENANT, new EntityPermission(Country.class, EntityPermission.READ));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new EntityPermission(Province.class, EntityPermission.READ));

        InstanceAccess userEntityAccess = new UserEntityInstanceAccess();
        grant(VistaBehavior.PROSPECTIVE_TENANT, new EntityPermission(Application.class, userEntityAccess, CRUD));

        InstanceAccess applicationEntityAccess = new ApplicationEntityInstanceAccess();

        //TODO Fix ME
        grant(VistaBehavior.PROSPECTIVE_TENANT, new AllPermissions());

        grant(VistaBehavior.PROSPECTIVE_TENANT, new EntityPermission(ApplicationDocument.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new EntityPermission(ApplicationDocumentData.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new EntityPermission(TenantInLeaseListDTO.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new EntityPermission(TenantInLeaseDTO.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new EntityPermission(PetsDTO.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new EntityPermission(TenantFinancialDTO.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new EntityPermission(Charges.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new EntityPermission(SummaryDTO.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.PROSPECTIVE_TENANT, new EntityPermission(PaymentInfo.class, applicationEntityAccess, CRUD));
        //TODO this service should be granted to a tenant
        grant(VistaBehavior.PROSPECTIVE_TENANT, new EntityPermission(MaintenanceRequestDTO.class, applicationEntityAccess, CRUD));

        grant(CoreBehavior.DEVELOPER, new AllPermissions());

        freeze();
    }
}

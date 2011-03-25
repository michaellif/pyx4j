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

import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.PaymentInfo;
import com.propertyvista.portal.domain.pt.Pets;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.rpc.pt.services.ApartmentServices;
import com.propertyvista.portal.rpc.pt.services.ApplicationDocumentsService;
import com.propertyvista.portal.rpc.pt.services.ApplicationServices;
import com.propertyvista.portal.rpc.pt.services.ChargesServices;
import com.propertyvista.portal.rpc.pt.services.PaymentServices;
import com.propertyvista.portal.rpc.pt.services.PetsServices;
import com.propertyvista.portal.rpc.pt.services.SummaryServices;
import com.propertyvista.portal.rpc.pt.services.TenantsFinancialServices;
import com.propertyvista.portal.rpc.pt.services.TenantsInfoServices;
import com.propertyvista.portal.rpc.pt.services.TenantsServices;
import com.propertyvista.server.common.security.UserEntityInstanceAccess;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.security.InstanceAccess;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

public class VistaPortalAccessControlList extends ServletContainerAclBuilder {

    private final static int CRUD = EntityPermission.CREATE | EntityPermission.READ | EntityPermission.UPDATE;

    public VistaPortalAccessControlList() {
        grant(new IServiceExecutePermission(ApplicationServices.class));
        grant(new IServiceExecutePermission(ApartmentServices.class));
        grant(new IServiceExecutePermission(ApplicationDocumentsService.class));
        grant(new IServiceExecutePermission(TenantsServices.class));
        grant(new IServiceExecutePermission(TenantsInfoServices.class));
        grant(new IServiceExecutePermission(TenantsFinancialServices.class));
        grant(new IServiceExecutePermission(PetsServices.class));
        grant(new IServiceExecutePermission(ChargesServices.class));
        grant(new IServiceExecutePermission(SummaryServices.class));
        grant(new IServiceExecutePermission(PaymentServices.class));

        InstanceAccess userEntityAccess = new UserEntityInstanceAccess();
        grant(VistaBehavior.POTENTIAL_TENANT, new EntityPermission(Application.class, userEntityAccess, CRUD));

        InstanceAccess applicationEntityAccess = new ApplicationEntityInstanceAccess();
        grant(VistaBehavior.POTENTIAL_TENANT, new EntityPermission(ApplicationProgress.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.POTENTIAL_TENANT, new EntityPermission(UnitSelection.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.POTENTIAL_TENANT, new EntityPermission(ApplicationDocument.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.POTENTIAL_TENANT, new EntityPermission(PotentialTenantList.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.POTENTIAL_TENANT, new EntityPermission(PotentialTenantInfo.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.POTENTIAL_TENANT, new EntityPermission(Pets.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.POTENTIAL_TENANT, new EntityPermission(PotentialTenantFinancial.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.POTENTIAL_TENANT, new EntityPermission(Charges.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.POTENTIAL_TENANT, new EntityPermission(Summary.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.POTENTIAL_TENANT, new EntityPermission(PaymentInfo.class, applicationEntityAccess, CRUD));
    }
}

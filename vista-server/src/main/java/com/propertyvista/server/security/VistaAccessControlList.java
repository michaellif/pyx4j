/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.security;

import com.propertyvista.portal.admin.rpc.VistaAdminServices;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.rpc.pt.PotentialTenantServices;
import com.propertyvista.portal.server.access.ApplicationEntityInstanceAccess;
import com.propertyvista.server.domain.UserCredential;

import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.security.InstanceAccess;
import com.pyx4j.essentials.rpc.admin.AdminServices;
import com.pyx4j.essentials.rpc.admin.DatastoreAdminServices;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessServices;
import com.pyx4j.essentials.rpc.report.ReportServices;
import com.pyx4j.log4gwt.rpc.LogServices;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.ServiceExecutePermission;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.security.server.ServletContainerAclBuilder;
import com.pyx4j.security.shared.CoreBehavior;

public class VistaAccessControlList extends ServletContainerAclBuilder {

    public final static int CRUD = EntityPermission.CREATE | EntityPermission.READ | EntityPermission.UPDATE;

    public VistaAccessControlList() {
        grant(new IServiceExecutePermission("*"));
        grant(new ServiceExecutePermission(LogServices.Log.class));
        grant(new ServiceExecutePermission(AuthenticationServices.class, "*"));
        grant(new ServiceExecutePermission(PotentialTenantServices.UnitExists.class));

        {
            // Debug
            grant(new ServiceExecutePermission(EntityServices.class, "*"));
            grant(new ServiceExecutePermission("*"));
            grant(new EntityPermission("*", EntityPermission.ALL));
            grant(new EntityPermission("*", EntityPermission.READ));
        }

        potentialTenantGrants();

        grant(VistaBehavior.EMPLOYEE, new ServiceExecutePermission(EntityServices.Query.class));
        grant(VistaBehavior.EMPLOYEE, new ServiceExecutePermission(ReportServices.class, "*"));
        grant(VistaBehavior.EMPLOYEE, new ServiceExecutePermission(DeferredProcessServices.class, "*"));

        grant(VistaBehavior.ADMIN, VistaBehavior.EMPLOYEE);
        grant(VistaBehavior.ADMIN, new ServiceExecutePermission(VistaAdminServices.class, "*"));
        grant(VistaBehavior.ADMIN, new EntityPermission(User.class, EntityPermission.ALL));
        grant(VistaBehavior.ADMIN, new EntityPermission(UserCredential.class, EntityPermission.ALL));

        grant(CoreBehavior.DEVELOPER, new ServiceExecutePermission(DatastoreAdminServices.class, "*"));
        grant(CoreBehavior.DEVELOPER, new ServiceExecutePermission(DeferredProcessServices.class, "*"));
        grant(CoreBehavior.DEVELOPER, new ServiceExecutePermission(AdminServices.class, "*"));

        freeze();
    }

    private void potentialTenantGrants() {

        grant(new ServiceExecutePermission(PotentialTenantServices.class, "*"));

        InstanceAccess userEntityAccess = new UserEntityInstanceAccess();

        grant(VistaBehavior.POTENCIAL_TENANT, new EntityPermission(Application.class, userEntityAccess, CRUD));

        InstanceAccess applicationEntityAccess = new ApplicationEntityInstanceAccess();
        grant(VistaBehavior.POTENCIAL_TENANT, new EntityPermission(PotentialTenantList.class, applicationEntityAccess, CRUD));
        grant(VistaBehavior.POTENCIAL_TENANT, new EntityPermission(PotentialTenantInfo.class, applicationEntityAccess, CRUD));
    }
}

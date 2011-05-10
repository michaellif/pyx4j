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
package com.propertyvista.crm.server.security;

import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.ServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.ref.Country;

public class VistaCrmAccessControlList extends ServletContainerAclBuilder {

    // Change this if you want to make it work temporary. Build will fail!
    private static final boolean allowAllDuringDevelopment = false;

    public VistaCrmAccessControlList() {

        if (allowAllDuringDevelopment) {
            // Debug
            grant(new IServiceExecutePermission("*"));
            grant(new ServiceExecutePermission(EntityServices.class, "*"));
            grant(new ServiceExecutePermission("*"));
            grant(new EntityPermission("*", EntityPermission.ALL));
            grant(new EntityPermission("*", EntityPermission.READ));
        }

        grant(new EntityPermission(Building.class, EntityPermission.ALL));
        grant(new IServiceExecutePermission(BuildingCrudService.class));
        grant(new IServiceExecutePermission(UnitCrudService.class));
        // Old servies
        grant(new EntityPermission(Country.class.getPackage().getName() + ".*", EntityPermission.READ));
        grant(new ServiceExecutePermission(EntityServices.class, "*"));
    }
}

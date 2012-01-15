/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.security;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.admin.rpc.services.AdminAuthenticationService;
import com.propertyvista.admin.rpc.services.ImportUploadService;
import com.propertyvista.admin.rpc.services.PmcCrudService;
import com.propertyvista.admin.rpc.services.PmcDataReportService;
import com.propertyvista.admin.rpc.services.VistaAdminService;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.server.domain.admin.Pmc;

public class VistaAdminAccessControlList extends ServletContainerAclBuilder {

    public VistaAdminAccessControlList() {
        grant(new IServiceExecutePermission(AdminAuthenticationService.class));

        grant(VistaBasicBehavior.Admin, new EntityPermission(Pmc.class, EntityPermission.ALL));

        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(VistaAdminService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(PmcCrudService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(ImportUploadService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(PmcDataReportService.class));
    }
}

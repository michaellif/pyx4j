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
import com.pyx4j.essentials.rpc.deferred.DeferredProcessService;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.security.AdminUserCredential;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.rpc.services.AdminAuthenticationService;
import com.propertyvista.admin.rpc.services.AdminPasswordChangeManagedService;
import com.propertyvista.admin.rpc.services.AdminPasswordChangeUserService;
import com.propertyvista.admin.rpc.services.AdminPasswordResetService;
import com.propertyvista.admin.rpc.services.AdminUserCrudService;
import com.propertyvista.admin.rpc.services.ImportUploadService;
import com.propertyvista.admin.rpc.services.MaintenanceCrudService;
import com.propertyvista.admin.rpc.services.OnboardingUserCrudService;
import com.propertyvista.admin.rpc.services.OnboardingUserPasswordChangeManagedService;
import com.propertyvista.admin.rpc.services.PmcCrudService;
import com.propertyvista.admin.rpc.services.PmcDataReportService;
import com.propertyvista.admin.rpc.services.SimulationService;
import com.propertyvista.admin.rpc.services.scheduler.RunCrudService;
import com.propertyvista.admin.rpc.services.scheduler.RunDataCrudService;
import com.propertyvista.admin.rpc.services.scheduler.TriggerCrudService;
import com.propertyvista.admin.rpc.services.sym.PadSymBatchCrudService;
import com.propertyvista.admin.rpc.services.sym.PadSymFileCrudService;
import com.propertyvista.domain.security.VistaBasicBehavior;

public class VistaAdminAccessControlList extends ServletContainerAclBuilder {

    public VistaAdminAccessControlList() {
        grant(new IServiceExecutePermission(AdminAuthenticationService.class));
        grant(VistaBasicBehavior.AdminPasswordChangeRequired, new IServiceExecutePermission(AdminPasswordResetService.class));

        grant(VistaBasicBehavior.Admin, new EntityPermission(Pmc.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(DeferredProcessService.class));

        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(MaintenanceCrudService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(SimulationService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(PmcCrudService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(ImportUploadService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(PmcDataReportService.class));

        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(AdminPasswordChangeUserService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(AdminPasswordChangeManagedService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(AdminUserCrudService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(OnboardingUserCrudService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(OnboardingUserPasswordChangeManagedService.class));

        grant(VistaBasicBehavior.Admin, new EntityPermission(AdminUserCredential.class, EntityPermission.ALL));
        grant(VistaBasicBehavior.Admin, new EntityPermission(OnboardingUserCredential.class, EntityPermission.ALL));

        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(TriggerCrudService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(RunCrudService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(RunDataCrudService.class));

        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(PadSymFileCrudService.class));
        grant(VistaBasicBehavior.Admin, new IServiceExecutePermission(PadSymBatchCrudService.class));
    }
}

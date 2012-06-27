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

import com.propertyvista.admin.domain.payment.pad.sim.PadSimBatch;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimFile;
import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.scheduler.Run;
import com.propertyvista.admin.domain.scheduler.RunData;
import com.propertyvista.admin.domain.scheduler.Trigger;
import com.propertyvista.admin.domain.security.AdminUserCredential;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.rpc.services.AdminAuthenticationService;
import com.propertyvista.admin.rpc.services.AdminPasswordChangeManagedService;
import com.propertyvista.admin.rpc.services.AdminPasswordChangeUserService;
import com.propertyvista.admin.rpc.services.AdminPasswordResetService;
import com.propertyvista.admin.rpc.services.AdminUserCrudService;
import com.propertyvista.admin.rpc.services.DBIntegrityCheckService;
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
import com.propertyvista.admin.rpc.services.sim.PadSimBatchCrudService;
import com.propertyvista.admin.rpc.services.sim.PadSimFileCrudService;
import com.propertyvista.admin.rpc.services.sim.SimulatedDataPreloadService;
import com.propertyvista.domain.security.VistaAdminBehavior;
import com.propertyvista.domain.security.VistaBasicBehavior;

public class VistaAdminAccessControlList extends ServletContainerAclBuilder {

    public VistaAdminAccessControlList() {
        grant(new IServiceExecutePermission(AdminAuthenticationService.class));
        grant(VistaBasicBehavior.AdminPasswordChangeRequired, new IServiceExecutePermission(AdminPasswordResetService.class));

        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(Pmc.class, EntityPermission.ALL));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(DeferredProcessService.class));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(MaintenanceCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(SimulationService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(PmcCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(ImportUploadService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(PmcDataReportService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(DBIntegrityCheckService.class));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(AdminPasswordChangeUserService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(AdminPasswordChangeManagedService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(AdminUserCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(OnboardingUserCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(OnboardingUserPasswordChangeManagedService.class));

        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(AdminUserCredential.class, EntityPermission.ALL));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(OnboardingUserCredential.class, EntityPermission.ALL));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(TriggerCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(RunCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(RunDataCrudService.class));

        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(Trigger.class, EntityPermission.ALL));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(Run.class, EntityPermission.ALL));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(RunData.class, EntityPermission.ALL));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(PadSimFileCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(PadSimBatchCrudService.class));

        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(PadSimFile.class, EntityPermission.ALL));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(PadSimBatch.class, EntityPermission.ALL));

        if (com.pyx4j.config.shared.ApplicationMode.isDevelopment()) {
            grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(SimulatedDataPreloadService.class));
        }

        grant(VistaAdminBehavior.OnboardingApi, new IServiceExecutePermission(AdminPasswordChangeUserService.class));
        grant(VistaAdminBehavior.OnboardingApi, new IServiceExecutePermission(OnboardingUserCrudService.class));
        grant(VistaAdminBehavior.OnboardingApi, new IServiceExecutePermission(OnboardingUserPasswordChangeManagedService.class));
        grant(VistaAdminBehavior.OnboardingApi, new IServiceExecutePermission(PmcCrudService.class));
        grant(VistaAdminBehavior.OnboardingApi, new EntityPermission(Pmc.class, EntityPermission.READ));
    }
}

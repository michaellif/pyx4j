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
package com.propertyvista.operations.server.security;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessService;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.operations.domain.dev.CardServiceSimulationCard;
import com.propertyvista.operations.domain.dev.CardServiceSimulationMerchantAccount;
import com.propertyvista.operations.domain.dev.CardServiceSimulationTransaction;
import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.operations.domain.payment.pad.sim.PadSimBatch;
import com.propertyvista.operations.domain.payment.pad.sim.PadSimFile;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.domain.scheduler.Trigger;
import com.propertyvista.operations.domain.security.AdminUserCredential;
import com.propertyvista.operations.domain.security.AuditRecord;
import com.propertyvista.operations.domain.security.OnboardingUserCredential;
import com.propertyvista.operations.rpc.services.AdminAuthenticationService;
import com.propertyvista.operations.rpc.services.AdminPasswordChangeManagedService;
import com.propertyvista.operations.rpc.services.AdminPasswordChangeUserService;
import com.propertyvista.operations.rpc.services.AdminPasswordResetService;
import com.propertyvista.operations.rpc.services.AdminUserCrudService;
import com.propertyvista.operations.rpc.services.AdminUserService;
import com.propertyvista.operations.rpc.services.AuditRecordListerService;
import com.propertyvista.operations.rpc.services.DBIntegrityCheckService;
import com.propertyvista.operations.rpc.services.EquifaxApprovalCrudService;
import com.propertyvista.operations.rpc.services.ExportDownloadService;
import com.propertyvista.operations.rpc.services.ImportUploadService;
import com.propertyvista.operations.rpc.services.MaintenanceCrudService;
import com.propertyvista.operations.rpc.services.OnboardingMerchantAccountCrudService;
import com.propertyvista.operations.rpc.services.OnboardingUserCrudService;
import com.propertyvista.operations.rpc.services.OnboardingUserPasswordChangeManagedService;
import com.propertyvista.operations.rpc.services.PmcCrudService;
import com.propertyvista.operations.rpc.services.PmcDataReportService;
import com.propertyvista.operations.rpc.services.SimulationService;
import com.propertyvista.operations.rpc.services.Vista2PmcService;
import com.propertyvista.operations.rpc.services.VistaTermsCrudService;
import com.propertyvista.operations.rpc.services.scheduler.RunCrudService;
import com.propertyvista.operations.rpc.services.scheduler.RunDataCrudService;
import com.propertyvista.operations.rpc.services.scheduler.SelectPmcListService;
import com.propertyvista.operations.rpc.services.scheduler.TriggerCrudService;
import com.propertyvista.operations.rpc.services.sim.CardServiceSimulationCardCrudService;
import com.propertyvista.operations.rpc.services.sim.CardServiceSimulationMerchantAccountCrudService;
import com.propertyvista.operations.rpc.services.sim.CardServiceSimulationTransactionCrudService;
import com.propertyvista.operations.rpc.services.sim.PadSimBatchCrudService;
import com.propertyvista.operations.rpc.services.sim.PadSimFileCrudService;
import com.propertyvista.operations.rpc.services.sim.SimulatedDataPreloadService;
import com.propertyvista.operations.rpc.services.version.VistaTermsVersionService;
import com.propertyvista.domain.pmc.OnboardingMerchantAccount;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.AdminUser;
import com.propertyvista.domain.security.VistaAdminBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;

public class VistaAdminAccessControlList extends ServletContainerAclBuilder {

    public VistaAdminAccessControlList() {
        grant(new IServiceExecutePermission(AdminAuthenticationService.class));
        grant(VistaBasicBehavior.OperationsPasswordChangeRequired, new IServiceExecutePermission(AdminPasswordResetService.class));

        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(Pmc.class, EntityPermission.ALL));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(DeferredProcessService.class));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(MaintenanceCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(SimulationService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(PmcCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(ImportUploadService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(PmcDataReportService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(DBIntegrityCheckService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(ExportDownloadService.class));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(AdminPasswordChangeUserService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(AdminPasswordChangeManagedService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(AdminUserService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(AdminUserCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(OnboardingUserCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(OnboardingUserPasswordChangeManagedService.class));

        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(AdminUserCredential.class, EntityPermission.ALL));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(OnboardingUserCredential.class, EntityPermission.ALL));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(TriggerCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(RunCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(RunDataCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(SelectPmcListService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(Vista2PmcService.class));

        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(Trigger.class, EntityPermission.ALL));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(Run.class, EntityPermission.ALL));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(RunData.class, EntityPermission.ALL));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(AdminUser.class, EntityPermission.READ));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(PadSimFileCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(PadSimBatchCrudService.class));

        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(PadSimFile.class, EntityPermission.ALL));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(PadSimBatch.class, EntityPermission.ALL));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(VistaTermsCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(VistaTermsVersionService.class));

        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(VistaTerms.class, EntityPermission.ALL));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(VistaTerms.VistaTermsV.class, EntityPermission.ALL));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(LegalDocument.class, EntityPermission.ALL));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(OnboardingMerchantAccountCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(OnboardingMerchantAccount.class, EntityPermission.ALL));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(AuditRecordListerService.class));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(AuditRecord.class, EntityPermission.READ));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(CardServiceSimulationCardCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(CardServiceSimulationCard.class, EntityPermission.ALL));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(CardServiceSimulationTransactionCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(CardServiceSimulationTransaction.class, EntityPermission.ALL));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(CardServiceSimulationMerchantAccountCrudService.class));
        grant(VistaAdminBehavior.SystemAdmin, new EntityPermission(CardServiceSimulationMerchantAccount.class, EntityPermission.ALL));

        if (com.pyx4j.config.shared.ApplicationMode.isDevelopment()) {
            grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(SimulatedDataPreloadService.class));
        }

        // let Onboarding API change their own user info and own password
        grant(VistaAdminBehavior.OnboardingApi, new IServiceExecutePermission(AdminUserService.class));
        grant(VistaAdminBehavior.OnboardingApi, new IServiceExecutePermission(AdminPasswordChangeUserService.class));
        grant(VistaAdminBehavior.OnboardingApi, new AdminUserAccountAccesRule(), AdminUserCredential.class);
        grant(VistaAdminBehavior.OnboardingApi, new EntityPermission(AdminUserCredential.class, EntityPermission.ALL));

        // let Onboarding API manage onboarding users
        grant(VistaAdminBehavior.OnboardingApi, new IServiceExecutePermission(OnboardingUserCrudService.class));
        grant(VistaAdminBehavior.OnboardingApi, new IServiceExecutePermission(OnboardingUserPasswordChangeManagedService.class));
        grant(VistaAdminBehavior.OnboardingApi, new EntityPermission(OnboardingUserCredential.class, EntityPermission.ALL));

        grant(VistaAdminBehavior.OnboardingApi, new IServiceExecutePermission(PmcCrudService.class));
        grant(VistaAdminBehavior.OnboardingApi, new EntityPermission(Pmc.class, EntityPermission.READ));

        grant(VistaAdminBehavior.SystemAdmin, new IServiceExecutePermission(EquifaxApprovalCrudService.class));
        grant(VistaAdminBehavior.OnboardingApi, new IServiceExecutePermission(EquifaxApprovalCrudService.class));

    }
}

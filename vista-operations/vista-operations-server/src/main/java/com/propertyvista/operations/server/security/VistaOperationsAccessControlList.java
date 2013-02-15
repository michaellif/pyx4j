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

import com.propertyvista.domain.pmc.OnboardingMerchantAccount;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.OperationsUser;
import com.propertyvista.domain.security.VistaOperationsBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
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
import com.propertyvista.operations.domain.security.AuditRecord;
import com.propertyvista.operations.domain.security.OnboardingUserCredential;
import com.propertyvista.operations.domain.security.OperationsUserCredential;
import com.propertyvista.operations.rpc.services.AdminPasswordChangeManagedService;
import com.propertyvista.operations.rpc.services.AdminPasswordChangeUserService;
import com.propertyvista.operations.rpc.services.AdminPasswordResetService;
import com.propertyvista.operations.rpc.services.AdminUserCrudService;
import com.propertyvista.operations.rpc.services.AdminUserService;
import com.propertyvista.operations.rpc.services.AuditRecordListerService;
import com.propertyvista.operations.rpc.services.DBIntegrityCheckService;
import com.propertyvista.operations.rpc.services.EncryptedStorageService;
import com.propertyvista.operations.rpc.services.EncryptedStorageServicePrivateKeyUploadService;
import com.propertyvista.operations.rpc.services.EquifaxApprovalCrudService;
import com.propertyvista.operations.rpc.services.ExportDownloadService;
import com.propertyvista.operations.rpc.services.ImportUploadService;
import com.propertyvista.operations.rpc.services.MaintenanceCrudService;
import com.propertyvista.operations.rpc.services.OnboardingMerchantAccountCrudService;
import com.propertyvista.operations.rpc.services.OnboardingUserCrudService;
import com.propertyvista.operations.rpc.services.OnboardingUserPasswordChangeManagedService;
import com.propertyvista.operations.rpc.services.OperationsAuthenticationService;
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

public class VistaOperationsAccessControlList extends ServletContainerAclBuilder {

    public VistaOperationsAccessControlList() {
        grant(new IServiceExecutePermission(OperationsAuthenticationService.class));
        grant(VistaBasicBehavior.OperationsPasswordChangeRequired, new IServiceExecutePermission(AdminPasswordResetService.class));

        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(Pmc.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(DeferredProcessService.class));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(MaintenanceCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(SimulationService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(PmcCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(ImportUploadService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(PmcDataReportService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(DBIntegrityCheckService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(ExportDownloadService.class));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(AdminPasswordChangeUserService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(AdminPasswordChangeManagedService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(AdminUserService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(AdminUserCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(OnboardingUserCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(OnboardingUserPasswordChangeManagedService.class));

        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(OperationsUserCredential.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(OnboardingUserCredential.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(TriggerCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(RunCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(RunDataCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(SelectPmcListService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(Vista2PmcService.class));

        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(Trigger.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(Run.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(RunData.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(OperationsUser.class, EntityPermission.READ));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(PadSimFileCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(PadSimBatchCrudService.class));

        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(PadSimFile.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(PadSimBatch.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(VistaTermsCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(VistaTermsVersionService.class));

        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(VistaTerms.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(VistaTerms.VistaTermsV.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(LegalDocument.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(OnboardingMerchantAccountCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(OnboardingMerchantAccount.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(CardServiceSimulationCardCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(CardServiceSimulationCard.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(CardServiceSimulationTransactionCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(CardServiceSimulationTransaction.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(CardServiceSimulationMerchantAccountCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(CardServiceSimulationMerchantAccount.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.SecurityAdmin, VistaOperationsBehavior.SystemAdmin);
        grant(VistaOperationsBehavior.SecurityAdmin, new IServiceExecutePermission(AuditRecordListerService.class));
        grant(VistaOperationsBehavior.SecurityAdmin, new EntityPermission(AuditRecord.class, EntityPermission.READ));

        grant(VistaOperationsBehavior.SecurityAdmin, new IServiceExecutePermission(EncryptedStorageService.class));
        grant(VistaOperationsBehavior.SecurityAdmin, new IServiceExecutePermission(EncryptedStorageServicePrivateKeyUploadService.class));

        if (com.pyx4j.config.shared.ApplicationMode.isDevelopment()) {
            grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(SimulatedDataPreloadService.class));
        }

        // let Onboarding change their own user info and own password
        grant(VistaOperationsBehavior.Onboarding, new IServiceExecutePermission(AdminUserService.class));
        grant(VistaOperationsBehavior.Onboarding, new IServiceExecutePermission(AdminPasswordChangeUserService.class));
        grant(VistaOperationsBehavior.Onboarding, new OperationsUserAccountAccesRule(), OperationsUserCredential.class);
        grant(VistaOperationsBehavior.Onboarding, new EntityPermission(OperationsUserCredential.class, EntityPermission.ALL));

        // let Onboarding manage onboarding users
        grant(VistaOperationsBehavior.Onboarding, new IServiceExecutePermission(OnboardingUserCrudService.class));
        grant(VistaOperationsBehavior.Onboarding, new IServiceExecutePermission(OnboardingUserPasswordChangeManagedService.class));
        grant(VistaOperationsBehavior.Onboarding, new EntityPermission(OnboardingUserCredential.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Onboarding, new IServiceExecutePermission(PmcCrudService.class));
        grant(VistaOperationsBehavior.Onboarding, new EntityPermission(Pmc.class, EntityPermission.READ));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(EquifaxApprovalCrudService.class));

    }
}

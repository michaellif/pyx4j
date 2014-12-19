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
 */
package com.propertyvista.operations.server.security;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessService;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.domain.marketing.PortalResidentMarketingTip;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.domain.pmc.payment.CustomerCreditCheckTransaction;
import com.propertyvista.domain.security.VistaOperationsBehavior;
import com.propertyvista.domain.security.common.VistaAccessGrantedBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.TenantSureTransaction;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationRecordRecord;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationSummary;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferBatch;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecord;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimBatch;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimFile;
import com.propertyvista.operations.domain.eft.cards.CardTransactionRecord;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationCard;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationCompany;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationMerchantAccount;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationReconciliationRecord;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimRecord;
import com.propertyvista.operations.domain.imports.OapiConversion;
import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.operations.domain.mail.DefaultOutgoingMailQueue;
import com.propertyvista.operations.domain.mail.OperationsOutgoingMailQueue;
import com.propertyvista.operations.domain.mail.OutgoingMailQueue;
import com.propertyvista.operations.domain.mail.TenantSureOutgoingMailQueue;
import com.propertyvista.operations.domain.scheduler.ExecutionReportMessage;
import com.propertyvista.operations.domain.scheduler.ExecutionReportSection;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.domain.scheduler.Trigger;
import com.propertyvista.operations.domain.security.AuditRecord;
import com.propertyvista.operations.domain.security.OperationsUser;
import com.propertyvista.operations.domain.security.OperationsUserCredential;
import com.propertyvista.operations.domain.tenantsure.TenantSureSubscribers;
import com.propertyvista.operations.domain.vista2pmc.OperationsAlert;
import com.propertyvista.operations.rpc.ac.UserSelfAccountAndSettings;
import com.propertyvista.operations.rpc.services.AdminPasswordChangeManagedService;
import com.propertyvista.operations.rpc.services.AdminPasswordChangeUserService;
import com.propertyvista.operations.rpc.services.AdminPasswordResetService;
import com.propertyvista.operations.rpc.services.AdminUserCrudService;
import com.propertyvista.operations.rpc.services.AdminUserService;
import com.propertyvista.operations.rpc.services.AuditRecordCrudService;
import com.propertyvista.operations.rpc.services.CustomerCreditCheckTransactionCrudService;
import com.propertyvista.operations.rpc.services.DBIntegrityCheckService;
import com.propertyvista.operations.rpc.services.EncryptedStorageService;
import com.propertyvista.operations.rpc.services.EncryptedStorageServicePrivateKeyUploadService;
import com.propertyvista.operations.rpc.services.EquifaxApprovalCrudService;
import com.propertyvista.operations.rpc.services.ExportDownloadService;
import com.propertyvista.operations.rpc.services.FundsReconciliationSummaryCrudService;
import com.propertyvista.operations.rpc.services.ImportUploadService;
import com.propertyvista.operations.rpc.services.MaintenanceCrudService;
import com.propertyvista.operations.rpc.services.MerchantAccountFileUploadService;
import com.propertyvista.operations.rpc.services.OapiConversionFileUploadService;
import com.propertyvista.operations.rpc.services.OapiCrudService;
import com.propertyvista.operations.rpc.services.OperationsAlertCrudService;
import com.propertyvista.operations.rpc.services.OperationsAuthenticationService;
import com.propertyvista.operations.rpc.services.OutgoingMailCrudService;
import com.propertyvista.operations.rpc.services.PadBatchCrudService;
import com.propertyvista.operations.rpc.services.PadDebitRecordCrudService;
import com.propertyvista.operations.rpc.services.PadFileCrudService;
import com.propertyvista.operations.rpc.services.PadReconciliationDebitRecordCrudService;
import com.propertyvista.operations.rpc.services.PadReconciliationFileCrudService;
import com.propertyvista.operations.rpc.services.PmcCardTransactionRecordCrudService;
import com.propertyvista.operations.rpc.services.PmcCrudService;
import com.propertyvista.operations.rpc.services.PmcDataReportService;
import com.propertyvista.operations.rpc.services.PmcDirectDebitRecordCrudService;
import com.propertyvista.operations.rpc.services.PmcMerchantAccountCrudService;
import com.propertyvista.operations.rpc.services.QuickTipCrudService;
import com.propertyvista.operations.rpc.services.SimulationService;
import com.propertyvista.operations.rpc.services.TenantSureCrudService;
import com.propertyvista.operations.rpc.services.TenantSureTransactionListerService;
import com.propertyvista.operations.rpc.services.Vista2PmcService;
import com.propertyvista.operations.rpc.services.VistaTermsCrudService;
import com.propertyvista.operations.rpc.services.dev.PmcYardiCredentialService;
import com.propertyvista.operations.rpc.services.scheduler.ExecutionReportMessageService;
import com.propertyvista.operations.rpc.services.scheduler.ExecutionReportSectionService;
import com.propertyvista.operations.rpc.services.scheduler.RunCrudService;
import com.propertyvista.operations.rpc.services.scheduler.RunDataCrudService;
import com.propertyvista.operations.rpc.services.scheduler.SelectPmcListService;
import com.propertyvista.operations.rpc.services.scheduler.TriggerCrudService;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationCardCrudService;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationConfigService;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationMerchantAccountCrudService;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationReconciliationCrudService;
import com.propertyvista.operations.rpc.services.simulator.CardServiceSimulationTransactionCrudService;
import com.propertyvista.operations.rpc.services.simulator.DirectDebitSimFileCrudService;
import com.propertyvista.operations.rpc.services.simulator.DirectDebitSimRecordCrudService;
import com.propertyvista.operations.rpc.services.simulator.PadSimBatchCrudService;
import com.propertyvista.operations.rpc.services.simulator.PadSimFileCrudService;
import com.propertyvista.operations.rpc.services.simulator.SimulatedDataPreloadService;
import com.propertyvista.operations.rpc.services.tools.oapi.OapiXMLFileDownloadService;
import com.propertyvista.operations.rpc.services.version.VistaTermsVersionService;

public class VistaOperationsAccessControlList extends UIAclBuilder {

    public VistaOperationsAccessControlList() {
        grant(new IServiceExecutePermission(OperationsAuthenticationService.class));
        grant(VistaBasicBehavior.OperationsPasswordChangeRequired, new IServiceExecutePermission(AdminPasswordResetService.class));

        grant(VistaAccessGrantedBehavior.Operations, new OperationsUserAccountAccesRule(), OperationsUserCredential.class);
        grant(VistaAccessGrantedBehavior.Operations, UserSelfAccountAndSettings.class);

        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(Pmc.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(DeferredProcessService.class));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(MaintenanceCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(SimulationService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(PmcCrudService.class));
// TODO check these are permissions needed for OAPI
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(OapiConversion.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(OapiCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(OapiConversionFileUploadService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(OapiXMLFileDownloadService.class));
// TODO check these are permissions needed for OAPI
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(ImportUploadService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(PmcDataReportService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(DBIntegrityCheckService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(ExportDownloadService.class));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(CustomerCreditCheckTransactionCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(CustomerCreditCheckTransaction.class, EntityPermission.READ));

        //TODO review and grant to VistaOperationsBehavior.SecurityAdmin
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(AdminPasswordChangeUserService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(AdminPasswordChangeManagedService.class));
        grant(VistaAccessGrantedBehavior.Operations, new IServiceExecutePermission(AdminUserService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(AdminUserCrudService.class));

        //TODO remove
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(OperationsUserCredential.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.ProcessAdmin, new IServiceExecutePermission(TriggerCrudService.class));
        grant(VistaOperationsBehavior.ProcessAdmin, new IServiceExecutePermission(RunCrudService.class));
        grant(VistaOperationsBehavior.ProcessAdmin, new IServiceExecutePermission(RunDataCrudService.class));
        grant(VistaOperationsBehavior.ProcessAdmin, new IServiceExecutePermission(ExecutionReportSectionService.class));
        grant(VistaOperationsBehavior.ProcessAdmin, new IServiceExecutePermission(ExecutionReportMessageService.class));
        grant(VistaOperationsBehavior.ProcessAdmin, new IServiceExecutePermission(SelectPmcListService.class));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(Vista2PmcService.class));

        grant(VistaOperationsBehavior.ProcessAdmin, new EntityPermission(Trigger.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.ProcessAdmin, new EntityPermission(Run.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.ProcessAdmin, new EntityPermission(RunData.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.ProcessAdmin, new EntityPermission(ExecutionReportSection.class, EntityPermission.READ));
        grant(VistaOperationsBehavior.ProcessAdmin, new EntityPermission(ExecutionReportMessage.class, EntityPermission.READ));
        //TODO remove
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(OperationsUser.class, EntityPermission.READ));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(PadSimFileCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(PadSimBatchCrudService.class));

        grant(VistaOperationsBehavior.Caledon, new EntityPermission(PadSimFile.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(PadSimBatch.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(QuickTipCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(PortalResidentMarketingTip.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(VistaTermsCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(VistaTermsVersionService.class));

        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(VistaTerms.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(VistaTerms.VistaTermsV.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(LegalDocument.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(PmcMerchantAccountCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(PmcMerchantAccountIndex.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(PmcCardTransactionRecordCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(CardTransactionRecord.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(PmcDirectDebitRecordCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(DirectDebitRecord.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(PadReconciliationFileCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(FundsReconciliationFile.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(PadReconciliationDebitRecordCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(FundsReconciliationRecordRecord.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(FundsReconciliationSummaryCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(FundsReconciliationSummary.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(PadFileCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(FundsTransferFile.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(PadDebitRecordCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(FundsTransferRecord.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(PadBatchCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(FundsTransferBatch.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(CardServiceSimulationConfigService.class));
        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(CardServiceSimulationCardCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(CardServiceSimulationCard.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(CardServiceSimulationTransactionCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(CardServiceSimulationTransaction.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(CardServiceSimulationMerchantAccountCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(CardServiceSimulationMerchantAccount.class, EntityPermission.ALL));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(CardServiceSimulationCompany.class, EntityPermission.READ));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(CardServiceSimulationReconciliationCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(CardServiceSimulationReconciliationRecord.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(DirectDebitSimRecordCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(DirectDebitSimRecord.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Caledon, new IServiceExecutePermission(DirectDebitSimFileCrudService.class));
        grant(VistaOperationsBehavior.Caledon, new EntityPermission(DirectDebitSimFile.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(AuditRecordCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(AuditRecord.class, EntityPermission.READ));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(OperationsAlertCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(OperationsAlert.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(TenantSureCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(TenantSureSubscribers.class, EntityPermission.READ));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(TenantSureInsurancePolicy.class, EntityPermission.READ));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(TenantSureTransactionListerService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(TenantSureTransaction.class, EntityPermission.READ));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(OutgoingMailCrudService.class));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(OutgoingMailQueue.class, EntityPermission.READ));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(DefaultOutgoingMailQueue.class, EntityPermission.READ));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(OperationsOutgoingMailQueue.class, EntityPermission.READ));
        grant(VistaOperationsBehavior.SystemAdmin, new EntityPermission(TenantSureOutgoingMailQueue.class, EntityPermission.READ));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(MerchantAccountFileUploadService.class));

        grant(VistaOperationsBehavior.SecurityAdmin, VistaOperationsBehavior.SystemAdmin);
        grant(VistaOperationsBehavior.SecurityAdmin, new IServiceExecutePermission(EncryptedStorageService.class));
        grant(VistaOperationsBehavior.SecurityAdmin, new IServiceExecutePermission(EncryptedStorageServicePrivateKeyUploadService.class));

        if (com.pyx4j.config.shared.ApplicationMode.isDevelopment()) {
            grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(SimulatedDataPreloadService.class));
            grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(PmcYardiCredentialService.class));
        }

        // let Onboarding change their own user info and own password
        grant(VistaOperationsBehavior.Onboarding, new IServiceExecutePermission(AdminUserService.class));
        grant(VistaOperationsBehavior.Onboarding, new IServiceExecutePermission(AdminPasswordChangeUserService.class));
        grant(VistaOperationsBehavior.Onboarding, new EntityPermission(OperationsUserCredential.class, EntityPermission.ALL));

        grant(VistaOperationsBehavior.Onboarding, new IServiceExecutePermission(PmcCrudService.class));
        grant(VistaOperationsBehavior.Onboarding, new EntityPermission(Pmc.class, EntityPermission.READ));

        grant(VistaOperationsBehavior.SystemAdmin, new IServiceExecutePermission(EquifaxApprovalCrudService.class));

        grant(VistaOperationsBehavior.SystemAdmin, VistaOperationsBehavior.Caledon);
        grant(VistaOperationsBehavior.SystemAdmin, VistaOperationsBehavior.ProcessAdmin);

        grant(VistaOperationsBehavior.Caledon, new EntityPermission(OperationsUserCredential.class, EntityPermission.ALL));

    }
}

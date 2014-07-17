/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.operations.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.activity.crud.account.AccountEditorActivity;
import com.propertyvista.operations.client.activity.crud.account.AccountViewerActivity;
import com.propertyvista.operations.client.activity.crud.adminusers.AdminUserEditorActivity;
import com.propertyvista.operations.client.activity.crud.adminusers.AdminUserListerActivity;
import com.propertyvista.operations.client.activity.crud.adminusers.AdminUserViewerActivity;
import com.propertyvista.operations.client.activity.crud.auditrecords.AuditRecordListerActivity;
import com.propertyvista.operations.client.activity.crud.auditrecords.AuditRecordViewerActivity;
import com.propertyvista.operations.client.activity.crud.creditcheck.CustomerCreditCheckTransactionListerActivity;
import com.propertyvista.operations.client.activity.crud.creditcheck.CustomerCreditCheckTransactionViewerActivity;
import com.propertyvista.operations.client.activity.crud.encryptedstorage.EncryptedStorageActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.cardtransactionrecords.CardTransactionRecordListerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.cardtransactionrecords.CardTransactionRecordViewerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.directdebitrecord.DirectDebitRecordListerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.directdebitrecord.DirectDebitRecordViewerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.fundsreconciliationfile.FundsReconciliationFileListerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.fundsreconciliationfile.FundsReconciliationFileViewerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.fundsreconciliationrecord.FundsReconciliationDebitRecordListerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.fundsreconciliationrecord.FundsReconciliationDebitRecordViewerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.fundsreconciliationsummary.FundsReconciliationSummaryListerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.fundsreconciliationsummary.FundsReconciliationSummaryViewerActivty;
import com.propertyvista.operations.client.activity.crud.fundstransfer.fundstransferbatch.FundsTransferBatchListerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.fundstransferbatch.FundsTransferBatchViewerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.fundstransferfile.FundsTransferFileListerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.fundstransferfile.FundsTransferFileViewerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.fundstransferrecord.FundsTransferDebitRecordListerActivity;
import com.propertyvista.operations.client.activity.crud.fundstransfer.fundstransferrecord.FundsTransferDebitRecordViewerActivity;
import com.propertyvista.operations.client.activity.crud.legal.VistaTermsDefaultActivity;
import com.propertyvista.operations.client.activity.crud.legal.VistaTermsEditorActivity;
import com.propertyvista.operations.client.activity.crud.legal.VistaTermsViewerActivity;
import com.propertyvista.operations.client.activity.crud.maintenance.MaintenanceEditorActivity;
import com.propertyvista.operations.client.activity.crud.maintenance.MaintenanceViewerActivity;
import com.propertyvista.operations.client.activity.crud.operationsalert.OperationsAlertEditorActivity;
import com.propertyvista.operations.client.activity.crud.operationsalert.OperationsAlertListerActivity;
import com.propertyvista.operations.client.activity.crud.operationsalert.OperationsAlertViewerActivity;
import com.propertyvista.operations.client.activity.crud.pmc.EquifaxApprovalViewActivity;
import com.propertyvista.operations.client.activity.crud.pmc.PmcEditorActivity;
import com.propertyvista.operations.client.activity.crud.pmc.PmcListerActivity;
import com.propertyvista.operations.client.activity.crud.pmc.PmcViewerActivity;
import com.propertyvista.operations.client.activity.crud.pmc.merchantaccount.MerchantAccountEditorActivity;
import com.propertyvista.operations.client.activity.crud.pmc.merchantaccount.MerchantAccountListerActivity;
import com.propertyvista.operations.client.activity.crud.pmc.merchantaccount.MerchantAccountViewerActivity;
import com.propertyvista.operations.client.activity.crud.scheduler.RunDataListerActivity;
import com.propertyvista.operations.client.activity.crud.scheduler.RunDataViewerActivity;
import com.propertyvista.operations.client.activity.crud.scheduler.RunListerActivity;
import com.propertyvista.operations.client.activity.crud.scheduler.RunViewerActivity;
import com.propertyvista.operations.client.activity.crud.scheduler.TriggerEditorActivity;
import com.propertyvista.operations.client.activity.crud.scheduler.TriggerListerActivity;
import com.propertyvista.operations.client.activity.crud.scheduler.TriggerViewerActivity;
import com.propertyvista.operations.client.activity.crud.simulateddatapreload.SimulatedDataPreloadActivity;
import com.propertyvista.operations.client.activity.crud.simulation.SimulationEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulation.SimulationViewerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.cardservice.CardServiceSimulationCardEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.cardservice.CardServiceSimulationCardListerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.cardservice.CardServiceSimulationConfigEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.cardservice.CardServiceSimulationConfigViewerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.cardservice.CardServiceSimulationMerchantAccountEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.cardservice.CardServiceSimulationMerchantAccountListerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.cardservice.CardServiceSimulationReconciliationListerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.cardservice.CardServiceSimulationTransactionEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.cardservice.CardServiceSimulationTransactionListerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.dbp.DirectDebitSimFileEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.dbp.DirectDebitSimFileListerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.dbp.DirectDebitSimFileViewerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.dbp.DirectDebitSimRecordEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.dbp.DirectDebitSimRecordListerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.dbp.DirectDebitSimRecordViewerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.pad.PadSimBatchEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.pad.PadSimBatchViewerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.pad.PadSimFileEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.pad.PadSimFileListerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.pad.PadSimFileViewerActivity;
import com.propertyvista.operations.client.activity.crud.systemdefaults.VistaSystemDefaultsEditorActivity;
import com.propertyvista.operations.client.activity.crud.systemdefaults.VistaSystemDefaultsViewerActivity;
import com.propertyvista.operations.client.activity.crud.tenantsure.TenantSureListerActivity;
import com.propertyvista.operations.client.activity.crud.tenantsure.TenantSureViewerActivity;
import com.propertyvista.operations.client.activity.login.LoginActivity;
import com.propertyvista.operations.client.activity.login.LoginWithTokenActivity;
import com.propertyvista.operations.client.activity.security.PasswordChangeActivity;
import com.propertyvista.operations.client.activity.security.PasswordResetActivity;
import com.propertyvista.operations.client.activity.security.PasswordResetRequesetActivity;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.OperationsSiteMap.Legal.VistaTermsAccess;

public class ContentActivityMapper implements AppActivityMapper {

    public ContentActivityMapper() {
    }

    @Override
    public void obtainActivity(final Place place, final AsyncCallback<Activity> callback) {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                Activity activity = null;
                if (place instanceof CrudAppPlace) {
                    CrudAppPlace crudPlace = (CrudAppPlace) place;
// - Management:                    
                    if (place instanceof OperationsSiteMap.Management.PMC) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new PmcEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PmcViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new PmcListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Management.EquifaxApproval) {
                        switch (crudPlace.getType()) {
                        case viewer:
                            activity = new EquifaxApprovalViewActivity(crudPlace);
                            break;

                        default:
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Management.Trigger) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new TriggerEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new TriggerViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new TriggerListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Management.TriggerRun) {
                        switch (crudPlace.getType()) {
                        case viewer:
                            activity = new RunViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new RunListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Management.TriggerRunData) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new RunDataListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new RunDataViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Management.CreditCheckTransaction) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new CustomerCreditCheckTransactionListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new CustomerCreditCheckTransactionViewerActivity(crudPlace);
                        default:
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Management.BillingSetup) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new VistaSystemDefaultsEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new VistaSystemDefaultsViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Management.PmcMerchantAccount) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new MerchantAccountListerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new MerchantAccountEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new MerchantAccountViewerActivity(crudPlace);
                            break;
                        }
// - Security:
                    } else if (place instanceof OperationsSiteMap.Security.AuditRecord) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new AuditRecordListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new AuditRecordViewerActivity(crudPlace);
                        default:
                            break;
                        }
// - Legal:
                    } else if (place instanceof OperationsSiteMap.Legal.VistaTermsAccess) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new VistaTermsDefaultActivity(crudPlace, ((VistaTermsAccess) place).getTarget());
                            break;
                        case editor:
                            activity = new VistaTermsEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new VistaTermsViewerActivity(crudPlace);
                            break;
                        }
// - Administration:
                    } else if (place instanceof OperationsSiteMap.Administration.Maintenance) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new MaintenanceEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new MaintenanceViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Administration.Simulation) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new SimulationEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new SimulationViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Administration.AdminUsers) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new AdminUserEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new AdminUserViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new AdminUserListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Administration.OperationsAlert) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new OperationsAlertListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new OperationsAlertViewerActivity(crudPlace);
                            break;
                        case editor:
                            activity = new OperationsAlertEditorActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Administration.EncryptedStorage) {
                        activity = new EncryptedStorageActivity((AppPlace) place);

                    } else if (place instanceof OperationsSiteMap.Administration.TenantSure) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new TenantSureListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new TenantSureViewerActivity(crudPlace);
                            break;
                        }

// - FundsTransfer:                        

                    } else if (place instanceof OperationsSiteMap.FundsTransfer.CardTransactionRecord) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new CardTransactionRecordListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new CardTransactionRecordViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.FundsTransfer.DirectDebitRecord) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new DirectDebitRecordListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new DirectDebitRecordViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.FundsTransfer.FundsTransferFile) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new FundsTransferFileListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new FundsTransferFileViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.FundsTransfer.FundsTransferRecord) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new FundsTransferDebitRecordListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new FundsTransferDebitRecordViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.FundsTransfer.FundsTransferBatch) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new FundsTransferBatchListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new FundsTransferBatchViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.FundsTransfer.FundsReconciliationFile) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new FundsReconciliationFileListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new FundsReconciliationFileViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.FundsTransfer.FundsReconciliationSummary) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new FundsReconciliationSummaryListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new FundsReconciliationSummaryViewerActivty(crudPlace);
                        default:
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.FundsTransfer.FundsReconciliationRecord) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new FundsReconciliationDebitRecordListerActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new FundsReconciliationDebitRecordViewerActivity(crudPlace);
                            break;
                        default:
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Account) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new AccountEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new AccountViewerActivity(crudPlace);
                            break;
                        }

                    } else if (ApplicationMode.isDevelopment() && place instanceof OperationsSiteMap.DevelopmentOnlyPlace) {
// All simulations go this this function
                        activity = createDevelopmentOnlyActivity(place);
                    }
// - Other:

                } else if (place instanceof OperationsSiteMap.Login) {
                    activity = new LoginActivity(place);
                } else if (place instanceof OperationsSiteMap.LoginWithToken) {
                    activity = new LoginWithTokenActivity(place);
                } else if (place instanceof OperationsSiteMap.PasswordResetRequest) {
                    activity = new PasswordResetRequesetActivity(place);
                } else if (place instanceof OperationsSiteMap.PasswordReset) {
                    activity = new PasswordResetActivity(place);

                } else if (place instanceof OperationsSiteMap.PasswordChange) {
                    activity = new PasswordChangeActivity(place);
                }
                callback.onSuccess(activity);
            }

            @Override
            public void onFailure(Throwable reason) {
                callback.onFailure(reason);
            }
        });

    }

    private Activity createDevelopmentOnlyActivity(Place place) {
        Activity activity = null;
        if (place instanceof CrudAppPlace) {
            CrudAppPlace crudPlace = (CrudAppPlace) place;
            if (place instanceof OperationsSiteMap.Simulator.SimulatedDataPreload) {
                activity = new SimulatedDataPreloadActivity();

            } else if (place instanceof OperationsSiteMap.Simulator.DirectBankingSimRecord) {
                switch (crudPlace.getType()) {
                case editor:
                    activity = new DirectDebitSimRecordEditorActivity(crudPlace);
                    break;
                case viewer:
                    activity = new DirectDebitSimRecordViewerActivity(crudPlace);
                    break;
                case lister:
                    activity = new DirectDebitSimRecordListerActivity(crudPlace);
                    break;
                }

            } else if (place instanceof OperationsSiteMap.Simulator.DirectBankingSimFile) {
                switch (crudPlace.getType()) {
                case editor:
                    activity = new DirectDebitSimFileEditorActivity(crudPlace);
                    break;
                case viewer:
                    activity = new DirectDebitSimFileViewerActivity(crudPlace);
                    break;
                case lister:
                    activity = new DirectDebitSimFileListerActivity(crudPlace);
                    break;
                default:
                    break;
                }

            } else if (place instanceof OperationsSiteMap.Simulator.PadSimulation.PadSimFile) {
                switch (crudPlace.getType()) {
                case editor:
                    activity = new PadSimFileEditorActivity(crudPlace);
                    break;
                case viewer:
                    activity = new PadSimFileViewerActivity(crudPlace);
                    break;
                case lister:
                    activity = new PadSimFileListerActivity(crudPlace);
                    break;
                }

            } else if (place instanceof OperationsSiteMap.Simulator.PadSimulation.PadSimBatch) {
                switch (crudPlace.getType()) {
                case editor:
                    activity = new PadSimBatchEditorActivity(crudPlace);
                    break;
                case viewer:
                    activity = new PadSimBatchViewerActivity(crudPlace);
                    break;
                }

            } else if (place instanceof OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationCard) {
                switch (crudPlace.getType()) {
                case viewer:
                case editor:
                    activity = new CardServiceSimulationCardEditorActivity((CrudAppPlace) place);
                    break;
                case lister:
                    activity = new CardServiceSimulationCardListerActivity(place);
                    break;
                }

            } else if (place instanceof OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationTransaction) {
                switch (crudPlace.getType()) {
                case viewer:
                case editor:
                    activity = new CardServiceSimulationTransactionEditorActivity((CrudAppPlace) place);
                    break;
                case lister:
                    activity = new CardServiceSimulationTransactionListerActivity(place);
                    break;
                }

            } else if (place instanceof OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationMerchantAccount) {
                switch (crudPlace.getType()) {
                case viewer:
                case editor:
                    activity = new CardServiceSimulationMerchantAccountEditorActivity((CrudAppPlace) place);
                    break;
                case lister:
                    activity = new CardServiceSimulationMerchantAccountListerActivity(place);
                    break;
                }

            } else if (place instanceof OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationReconciliation) {
                switch (crudPlace.getType()) {
                case viewer:
                    //TODO add View
                    break;
                case editor:
                    break;
                case lister:
                    activity = new CardServiceSimulationReconciliationListerActivity(place);
                    break;
                }

            } else if (place instanceof OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulatorConfiguration) {
                switch (crudPlace.getType()) {
                case viewer:
                    activity = new CardServiceSimulationConfigViewerActivity((CrudAppPlace) place);
                    break;
                case editor:
                    activity = new CardServiceSimulationConfigEditorActivity((CrudAppPlace) place);
                    break;
                case lister:
                default:
                    break;
                }

            }

        }
        return activity;
    }

}

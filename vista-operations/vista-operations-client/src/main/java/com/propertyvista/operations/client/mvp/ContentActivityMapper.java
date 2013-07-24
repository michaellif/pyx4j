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
import com.propertyvista.operations.client.activity.crud.encryptedstorage.EncryptedStorageActivity;
import com.propertyvista.operations.client.activity.crud.legal.VistaTermsDefaultActivity;
import com.propertyvista.operations.client.activity.crud.legal.VistaTermsEditorActivity;
import com.propertyvista.operations.client.activity.crud.legal.VistaTermsViewerActivity;
import com.propertyvista.operations.client.activity.crud.maintenance.MaintenanceEditorActivity;
import com.propertyvista.operations.client.activity.crud.maintenance.MaintenanceViewerActivity;
import com.propertyvista.operations.client.activity.crud.pmc.EquifaxApprovalViewActivity;
import com.propertyvista.operations.client.activity.crud.pmc.PmcEditorActivity;
import com.propertyvista.operations.client.activity.crud.pmc.PmcListerActivity;
import com.propertyvista.operations.client.activity.crud.pmc.PmcViewerActivity;
import com.propertyvista.operations.client.activity.crud.pmc.merchantaccount.MerchantAccountEditorActivity;
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
import com.propertyvista.operations.client.activity.crud.simulator.cardservice.CardServiceSimulationMerchantAccountEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.cardservice.CardServiceSimulationMerchantAccountListerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.cardservice.CardServiceSimulationTransactionEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.cardservice.CardServiceSimulationTransactionListerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.dbp.DirectDebitSimFileEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.dbp.DirectDebitSimFileListerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.dbp.DirectDebitSimFileViewerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.dbp.DirectDebitSimRecordEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.dbp.DirectDebitSimRecordListerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.dbp.DirectDebitSimRecordViewerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.pad.PadBatchEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.pad.PadBatchViewerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.pad.PadFileEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulator.pad.PadFileListerActivity;
import com.propertyvista.operations.client.activity.crud.simulator.pad.PadFileViewerActivity;
import com.propertyvista.operations.client.activity.crud.systemdefaults.VistaSystemDefaultsEditorAcitvity;
import com.propertyvista.operations.client.activity.crud.systemdefaults.VistaSystemDefaultsViewerActivty;
import com.propertyvista.operations.client.activity.login.LoginActivity;
import com.propertyvista.operations.client.activity.login.LoginWithTokenActivity;
import com.propertyvista.operations.client.activity.security.PasswordChangeActivity;
import com.propertyvista.operations.client.activity.security.PasswordResetActivity;
import com.propertyvista.operations.client.activity.security.PasswordResetRequesetActivity;
import com.propertyvista.operations.domain.legal.VistaTerms.Target;
import com.propertyvista.operations.rpc.OperationsSiteMap;

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

// - Security:
                    } else if (place instanceof OperationsSiteMap.Security.AuditRecord) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new AuditRecordListerActivity(place);
                            break;
                        case viewer:
                            activity = new AuditRecordViewerActivity(crudPlace);
                        default:
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

                    } else if (place instanceof OperationsSiteMap.Management.BillingSetup) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new VistaSystemDefaultsEditorAcitvity(crudPlace);
                            break;
                        case viewer:
                            activity = new VistaSystemDefaultsViewerActivty(crudPlace);
                            break;
                        default:
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

                    } else if (place instanceof OperationsSiteMap.Simulator.SimulatedDataPreload) {
                        activity = new SimulatedDataPreloadActivity();

                    } else if (place instanceof OperationsSiteMap.Simulator.DirectDebitSimRecord) {
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

                    } else if (place instanceof OperationsSiteMap.Simulator.DirectDebitSimFile) {
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
                            activity = new PadFileEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PadFileViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new PadFileListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Simulator.PadSimulation.PadSimBatch) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new PadBatchEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new PadBatchViewerActivity(crudPlace);
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
                    } else if (place instanceof OperationsSiteMap.Account) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new AccountEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new AccountViewerActivity(crudPlace);
                            break;
                        }

                        // TODO all these legal places should be mareged to one the type of legal terms should be arg of the place
                    } else if (place instanceof OperationsSiteMap.Legal.PortalTerms) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new VistaTermsDefaultActivity(crudPlace, Target.Tenant);
                            break;
                        case editor:
                            activity = new VistaTermsEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new VistaTermsViewerActivity(crudPlace);
                            break;
                        }
                    } else if (place instanceof OperationsSiteMap.Legal.PmcTerms) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new VistaTermsDefaultActivity(crudPlace, Target.PMC);
                            break;
                        case editor:
                            activity = new VistaTermsEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new VistaTermsViewerActivity(crudPlace);
                            break;
                        }
                    } else if (place instanceof OperationsSiteMap.Legal.PmcCaledonTermsTemplate) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new VistaTermsDefaultActivity(crudPlace, Target.PmcCaledonTemplate);
                            break;
                        case editor:
                            activity = new VistaTermsEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new VistaTermsViewerActivity(crudPlace);
                            break;
                        }
                    } else if (place instanceof OperationsSiteMap.Legal.PmcCaldedonSolePropetorshipSectionTerms) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new VistaTermsDefaultActivity(crudPlace, Target.PmcCaldedonSolePropetorshipSection);
                            break;
                        case editor:
                            activity = new VistaTermsEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new VistaTermsViewerActivity(crudPlace);
                            break;
                        }
                    } else if (place instanceof OperationsSiteMap.Legal.PmcPaymentPad) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new VistaTermsDefaultActivity(crudPlace, Target.PmcPaymentPad);
                            break;
                        case editor:
                            activity = new VistaTermsEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new VistaTermsViewerActivity(crudPlace);
                            break;
                        }
                    } else if (place instanceof OperationsSiteMap.Legal.TenantSurePreAuthorizedPayments) {
                        switch (crudPlace.getType()) {
                        case lister:
                            activity = new VistaTermsDefaultActivity(crudPlace, Target.TenantSurePreAuthorizedPaymentsAgreement);
                            break;
                        case editor:
                            activity = new VistaTermsEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new VistaTermsViewerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Management.PmcMerchantAccounts) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new MerchantAccountEditorActivity((CrudAppPlace) place);
                            break;
                        case viewer:
                            activity = new MerchantAccountViewerActivity((CrudAppPlace) place);
                            break;
                        }
// - Simulation
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
                    }

                } else if (place instanceof OperationsSiteMap.Administration.EncryptedStorage) {
                    activity = new EncryptedStorageActivity((AppPlace) place);

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
}

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

import com.propertyvista.operations.client.activity.AlertActivity;
import com.propertyvista.operations.client.activity.MessageActivity;
import com.propertyvista.operations.client.activity.SettingsActivity;
import com.propertyvista.operations.client.activity.crud.account.AccountEditorActivity;
import com.propertyvista.operations.client.activity.crud.account.AccountViewerActivity;
import com.propertyvista.operations.client.activity.crud.adminusers.AdminUserEditorActivity;
import com.propertyvista.operations.client.activity.crud.adminusers.AdminUserListerActivity;
import com.propertyvista.operations.client.activity.crud.adminusers.AdminUserViewerActivity;
import com.propertyvista.operations.client.activity.crud.auditrecords.AuditRecordsActivity;
import com.propertyvista.operations.client.activity.crud.cardservicesimulation.CardServiceSimulationCardEditorActivity;
import com.propertyvista.operations.client.activity.crud.cardservicesimulation.CardServiceSimulationCardListerActivity;
import com.propertyvista.operations.client.activity.crud.cardservicesimulation.CardServiceSimulationMerchantAccountEditorActivity;
import com.propertyvista.operations.client.activity.crud.cardservicesimulation.CardServiceSimulationMerchantAccountListerActivity;
import com.propertyvista.operations.client.activity.crud.cardservicesimulation.CardServiceSimulationTransactionEditorActivity;
import com.propertyvista.operations.client.activity.crud.cardservicesimulation.CardServiceSimulationTransactionListerActivity;
import com.propertyvista.operations.client.activity.crud.equifaxencryptedstorage.EquifaxEncryptedStorageActivity;
import com.propertyvista.operations.client.activity.crud.legal.VistaTermsDefaultActivity;
import com.propertyvista.operations.client.activity.crud.legal.VistaTermsEditorActivity;
import com.propertyvista.operations.client.activity.crud.legal.VistaTermsViewerActivity;
import com.propertyvista.operations.client.activity.crud.maintenance.MaintenanceEditorActivity;
import com.propertyvista.operations.client.activity.crud.maintenance.MaintenanceViewerActivity;
import com.propertyvista.operations.client.activity.crud.onboardingmerchantaccount.OnboardingMerchantAccountEditorActivity;
import com.propertyvista.operations.client.activity.crud.onboardingmerchantaccount.OnboardingMerchantAccountViewerActivity;
import com.propertyvista.operations.client.activity.crud.onboardingusers.OnBoardingUserViewerActivity;
import com.propertyvista.operations.client.activity.crud.onboardingusers.OnboardingUserEditorActivity;
import com.propertyvista.operations.client.activity.crud.onboardingusers.OnboardingUserListerActivity;
import com.propertyvista.operations.client.activity.crud.pmc.EquifaxApprovalViewActivity;
import com.propertyvista.operations.client.activity.crud.pmc.PmcEditorActivity;
import com.propertyvista.operations.client.activity.crud.pmc.PmcListerActivity;
import com.propertyvista.operations.client.activity.crud.pmc.PmcViewerActivity;
import com.propertyvista.operations.client.activity.crud.scheduler.RunListerActivity;
import com.propertyvista.operations.client.activity.crud.scheduler.RunViewerActivity;
import com.propertyvista.operations.client.activity.crud.scheduler.TriggerEditorActivity;
import com.propertyvista.operations.client.activity.crud.scheduler.TriggerListerActivity;
import com.propertyvista.operations.client.activity.crud.scheduler.TriggerViewerActivity;
import com.propertyvista.operations.client.activity.crud.simulateddatapreload.SimulatedDataPreloadActivity;
import com.propertyvista.operations.client.activity.crud.simulatedpad.PadBatchEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulatedpad.PadBatchViewerActivity;
import com.propertyvista.operations.client.activity.crud.simulatedpad.PadFileEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulatedpad.PadFileListerActivity;
import com.propertyvista.operations.client.activity.crud.simulatedpad.PadFileViewerActivity;
import com.propertyvista.operations.client.activity.crud.simulation.SimulationEditorActivity;
import com.propertyvista.operations.client.activity.crud.simulation.SimulationViewerActivity;
import com.propertyvista.operations.client.activity.crud.systemdefaults.VistaSystemDefaultsEditorAcitvity;
import com.propertyvista.operations.client.activity.crud.systemdefaults.VistaSystemDefaultsViewerActivty;
import com.propertyvista.operations.client.activity.security.PasswordChangeActivity;
import com.propertyvista.operations.domain.legal.VistaTerms.Target;
import com.propertyvista.operations.rpc.OperationsSiteMap;

public class MainActivityMapper implements AppActivityMapper {

    public MainActivityMapper() {
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

                    } else if (place instanceof OperationsSiteMap.Management.Run) {
                        switch (crudPlace.getType()) {
                        case viewer:
                            activity = new RunViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new RunListerActivity(crudPlace);
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Management.OnboardingUser) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new OnboardingUserEditorActivity(crudPlace);
                            break;
                        case viewer:
                            activity = new OnBoardingUserViewerActivity(crudPlace);
                            break;
                        case lister:
                            activity = new OnboardingUserListerActivity(crudPlace);
                            break;
                        }
// - Security:
                    } else if (place instanceof OperationsSiteMap.Security.AuditRecord) {
                        activity = new AuditRecordsActivity(place);
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

                    } else if (place instanceof OperationsSiteMap.Administration.SimulatedDataPreload) {
                        activity = new SimulatedDataPreloadActivity();

                    } else if (place instanceof OperationsSiteMap.Administration.PadSimulation.PadSimFile) {
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

                    } else if (place instanceof OperationsSiteMap.Administration.PadSimulation.PadSimBatch) {
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

                    } else if (place instanceof OperationsSiteMap.Management.OnboardingMerchantAccounts) {
                        switch (crudPlace.getType()) {
                        case editor:
                            activity = new OnboardingMerchantAccountEditorActivity((CrudAppPlace) place);
                            break;
                        case viewer:
                            activity = new OnboardingMerchantAccountViewerActivity((CrudAppPlace) place);
                            break;
                        }
// - Simulation
                    } else if (place instanceof OperationsSiteMap.Administration.CardServiceSimulation.CardServiceSimulationCard) {
                        switch (crudPlace.getType()) {
                        case viewer:
                        case editor:
                            activity = new CardServiceSimulationCardEditorActivity((CrudAppPlace) place);
                            break;
                        case lister:
                            activity = new CardServiceSimulationCardListerActivity(place);
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Administration.CardServiceSimulation.CardServiceSimulationTransaction) {
                        switch (crudPlace.getType()) {
                        case viewer:
                        case editor:
                            activity = new CardServiceSimulationTransactionEditorActivity((CrudAppPlace) place);
                            break;
                        case lister:
                            activity = new CardServiceSimulationTransactionListerActivity(place);
                            break;
                        }

                    } else if (place instanceof OperationsSiteMap.Administration.CardServiceSimulation.CardServiceSimulationMerchantAccount) {
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
// - Settings:
                } else if (place instanceof OperationsSiteMap.Settings) {
                    activity = new SettingsActivity(place);

                } else if (place instanceof OperationsSiteMap.Administration.EquifaxEncryptedStorage) {
                    activity = new EquifaxEncryptedStorageActivity((AppPlace) place);

// - Other:
                } else if (place instanceof OperationsSiteMap.Alert) {
                    activity = new AlertActivity(place);
                } else if (place instanceof OperationsSiteMap.Message) {
                    activity = new MessageActivity(place);

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

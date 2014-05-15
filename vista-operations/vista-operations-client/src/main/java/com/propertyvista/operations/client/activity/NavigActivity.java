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
package com.propertyvista.operations.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.security.VistaOperationsBehavior;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.NavigView;
import com.propertyvista.operations.rpc.OperationsSiteMap;

public class NavigActivity extends AbstractActivity implements NavigView.MainNavigPresenter {

    private final NavigView view;

    public NavigActivity(Place place) {
        view = OperationsSite.getViewFactory().getView(NavigView.class);
        view.setPresenter(this);
        withPlace(place);
    }

    public NavigActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setNavigFolders(createNavigFolders());
        panel.setWidget(view);
    }

    @Override
    public void navigTo(AppPlace place) {
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public AppPlace getWhere() {
        return AppSite.getPlaceController().getWhere();
    }

    private List<NavigFolder> createNavigFolders() {
        ArrayList<NavigFolder> list = new ArrayList<NavigFolder>();

        // Properties
        NavigFolder folder = new NavigFolder("PMC Management");
        folder.addNavigItem(new OperationsSiteMap.Management.PMC(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Management.PmcMerchantAccount(), VistaOperationsBehavior.Caledon);
        folder.addNavigItem(new OperationsSiteMap.Management.Trigger(), VistaOperationsBehavior.ProcessAdmin);
        folder.addNavigItem(new OperationsSiteMap.Management.TriggerRun(), VistaOperationsBehavior.ProcessAdmin);
        folder.addNavigItem(new OperationsSiteMap.Management.TriggerRunData(), VistaOperationsBehavior.ProcessAdmin);
        folder.addNavigItem(new OperationsSiteMap.Management.BillingSetup(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Management.CreditCheckTransaction(), VistaOperationsBehavior.SystemAdmin);
        list.add(folder);

        // Security
        folder = new NavigFolder("Funds Transfer");
        folder.addNavigItem(new OperationsSiteMap.FundsTransfer.DirectDebitRecord(), VistaOperationsBehavior.Caledon);
        folder.addNavigItem(new OperationsSiteMap.FundsTransfer.FundsTransferFile(), VistaOperationsBehavior.Caledon);
        folder.addNavigItem(new OperationsSiteMap.FundsTransfer.FundsTransferBatch(), VistaOperationsBehavior.Caledon);
        folder.addNavigItem(new OperationsSiteMap.FundsTransfer.FundsTransferRecord(), VistaOperationsBehavior.Caledon);
        folder.addNavigItem(new OperationsSiteMap.FundsTransfer.FundsReconciliationFile(), VistaOperationsBehavior.Caledon);
        folder.addNavigItem(new OperationsSiteMap.FundsTransfer.FundsReconciliationSummary(), VistaOperationsBehavior.Caledon);
        folder.addNavigItem(new OperationsSiteMap.FundsTransfer.FundsReconciliationRecord(), VistaOperationsBehavior.Caledon);
        list.add(folder);

        // Security
        folder = new NavigFolder("Security");
        folder.addNavigItem(new OperationsSiteMap.Security.AuditRecord(), VistaOperationsBehavior.SystemAdmin);
        list.add(folder);

        // Legal
        folder = new NavigFolder("Legal");

        folder.addNavigItem(new OperationsSiteMap.Legal.PmcTerms(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Legal.PmcPaymentPadTerms(), VistaOperationsBehavior.SystemAdmin);

        folder.addNavigItem(new OperationsSiteMap.Legal.PmcCaledonTermsTemplate(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Legal.PmcCaledonSoleProprietorshipSection(), VistaOperationsBehavior.SystemAdmin);

        folder.addNavigItem(new OperationsSiteMap.Legal.ProspectPortalTermsAndConditions(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Legal.ProspectPortalPrivacyPolicy(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Legal.ResidentPortalTermsAndConditions(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Legal.ResidentPortalPrivacyPolicy(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Legal.TenantBillingTerms(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Legal.TenantPreAuthorizedPaymentECheck(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Legal.TenantPreAuthorizedPaymentCreditCard(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Legal.TenantCaledonConvenienceFee(), VistaOperationsBehavior.SystemAdmin);

        folder.addNavigItem(new OperationsSiteMap.Legal.TenantSurePapAgreement(), VistaOperationsBehavior.SystemAdmin);

        list.add(folder);

        // Administration
        folder = new NavigFolder("System Administration");
        folder.addNavigItem(new OperationsSiteMap.Administration.Maintenance(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Administration.Simulation(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Administration.AdminUsers(), VistaOperationsBehavior.SystemAdmin);
        folder.addNavigItem(new OperationsSiteMap.Administration.EncryptedStorage(), VistaOperationsBehavior.SecurityAdmin);
        list.add(folder);

        if (ApplicationMode.isDevelopment()) {
            folder = new NavigFolder("Dev Simulation");

            folder.addNavigItem(new OperationsSiteMap.Simulator.SimulatedDataPreload(), VistaOperationsBehavior.SecurityAdmin);

            folder.addNavigItem(new OperationsSiteMap.Simulator.PadSimulation.PadSimFile(), VistaOperationsBehavior.Caledon);

            folder.addNavigItem(new OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulatorConfiguration().formViewerPlace(new Key(1)),
                    VistaOperationsBehavior.Caledon);
            folder.addNavigItem(new OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationMerchantAccount(), VistaOperationsBehavior.Caledon);
            folder.addNavigItem(new OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationCard(), VistaOperationsBehavior.Caledon);
            folder.addNavigItem(new OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationTransaction(), VistaOperationsBehavior.Caledon);

            folder.addNavigItem(new OperationsSiteMap.Simulator.DirectBankingSimRecord(), VistaOperationsBehavior.Caledon);
            folder.addNavigItem(new OperationsSiteMap.Simulator.DirectBankingSimFile(), VistaOperationsBehavior.Caledon);

            list.add(folder);
        }

        return list;
    }
}

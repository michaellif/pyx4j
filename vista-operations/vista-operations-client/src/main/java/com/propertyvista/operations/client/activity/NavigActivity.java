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

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.security.VistaOperationsBehavior;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.NavigView;
import com.propertyvista.operations.rpc.OperationsSiteMap;

public class NavigActivity extends AbstractActivity implements NavigView.MainNavigPresenter {

    private final NavigView view;

    public NavigActivity(Place place) {
        view = OperationsSite.getViewFactory().instantiate(NavigView.class);
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
        folder.addNavigItem(new OperationsSiteMap.Management.PMC());
        folder.addNavigItem(new OperationsSiteMap.Management.PmcMerchantAccount());
        folder.addNavigItem(new OperationsSiteMap.Management.Trigger());
        folder.addNavigItem(new OperationsSiteMap.Management.TriggerRun());
        folder.addNavigItem(new OperationsSiteMap.Management.TriggerRunData());
        folder.addNavigItem(new OperationsSiteMap.Management.BillingSetup());
        list.add(folder);

        // Security
        folder = new NavigFolder("Funds Transfer");
        folder.addNavigItem(new OperationsSiteMap.FundsTransfer.DirectDebitRecord());
        folder.addNavigItem(new OperationsSiteMap.FundsTransfer.FundsTransferFile());
        folder.addNavigItem(new OperationsSiteMap.FundsTransfer.FundsTransferRecord());
        folder.addNavigItem(new OperationsSiteMap.FundsTransfer.FundsTransferBatch());
        folder.addNavigItem(new OperationsSiteMap.FundsTransfer.FundsReconciliationFile());
        folder.addNavigItem(new OperationsSiteMap.FundsTransfer.FundsReconciliationRecord());
        list.add(folder);

        // Security
        folder = new NavigFolder("Security");
        folder.addNavigItem(new OperationsSiteMap.Security.AuditRecord());
        list.add(folder);

        // Legal
        folder = new NavigFolder("Legal");

        folder.addNavigItem(new OperationsSiteMap.Legal.PmcTerms());
        folder.addNavigItem(new OperationsSiteMap.Legal.PmcPadTerms());

        folder.addNavigItem(new OperationsSiteMap.Legal.PmcCaledonTermsTemplate());
        folder.addNavigItem(new OperationsSiteMap.Legal.PmcCaledonSoleProprietorshipSection());

        folder.addNavigItem(new OperationsSiteMap.Legal.TenantTerms());
        folder.addNavigItem(new OperationsSiteMap.Legal.TenantBillingTerms());
        folder.addNavigItem(new OperationsSiteMap.Legal.TenantPAD());
        folder.addNavigItem(new OperationsSiteMap.Legal.TenantCC());

        folder.addNavigItem(new OperationsSiteMap.Legal.TenantSurePapAgreement());

        list.add(folder);

        // Administration
        folder = new NavigFolder("System Administration");
        folder.addNavigItem(new OperationsSiteMap.Administration.Maintenance());
        folder.addNavigItem(new OperationsSiteMap.Administration.Simulation());
        folder.addNavigItem(new OperationsSiteMap.Administration.AdminUsers());
        if (SecurityController.checkBehavior(VistaOperationsBehavior.SecurityAdmin)) {
            folder.addNavigItem(new OperationsSiteMap.Administration.EncryptedStorage());
        }
        list.add(folder);

        if (ApplicationMode.isDevelopment()) {
            folder = new NavigFolder("Dev Simulation");

            folder.addNavigItem(new OperationsSiteMap.Simulator.SimulatedDataPreload());

            folder.addNavigItem(new OperationsSiteMap.Simulator.PadSimulation.PadSimFile());

            folder.addNavigItem(new OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationMerchantAccount());
            folder.addNavigItem(new OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationCard());
            folder.addNavigItem(new OperationsSiteMap.Simulator.CardServiceSimulation.CardServiceSimulationTransaction());

            folder.addNavigItem(new OperationsSiteMap.Simulator.DirectBankingSimRecord());
            folder.addNavigItem(new OperationsSiteMap.Simulator.DirectBankingSimFile());

            list.add(folder);
        }

        return list;
    }
}

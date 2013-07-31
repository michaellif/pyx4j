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
        panel.setWidget(view);
    }

    @Override
    public void navigTo(AppPlace place) {
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public String getNavigLabel(AppPlace place) {
        return AppSite.getHistoryMapper().getPlaceInfo(place).getNavigLabel();
    }

    @Override
    public AppPlace getWhere() {
        return AppSite.getPlaceController().getWhere();
    }

    @Override
    public List<NavigFolder> getNavigFolders() {
        ArrayList<NavigFolder> list = new ArrayList<NavigFolder>();

        // Properties
        NavigFolder folder = new NavigFolder("PMC Management");
        folder.addNavigItem(new OperationsSiteMap.Management.PMC());
        folder.addNavigItem(new OperationsSiteMap.Management.Trigger());
        folder.addNavigItem(new OperationsSiteMap.Management.TriggerRun());
        folder.addNavigItem(new OperationsSiteMap.Management.TriggerRunData());
        folder.addNavigItem(new OperationsSiteMap.Management.BillingSetup());
        folder.addNavigItem(new OperationsSiteMap.Management.DirectDebitRecord());

        list.add(folder);

        // Security
        folder = new NavigFolder("Security");
        folder.addNavigItem(new OperationsSiteMap.Security.AuditRecord());
        list.add(folder);

        // Legal
        folder = new NavigFolder("Legal");
        // TODO need one place in map for this, and here have different args for the place
        folder.addNavigItem(new OperationsSiteMap.Legal.PortalTerms());
        folder.addNavigItem(new OperationsSiteMap.Legal.PmcTerms());
        folder.addNavigItem(new OperationsSiteMap.Legal.PmcCaledonTermsTemplate());
        folder.addNavigItem(new OperationsSiteMap.Legal.PmcCaldedonSolePropetorshipSectionTerms());
        folder.addNavigItem(new OperationsSiteMap.Legal.PmcPaymentPad());
        folder.addNavigItem(new OperationsSiteMap.Legal.TenantSurePreAuthorizedPayments());
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

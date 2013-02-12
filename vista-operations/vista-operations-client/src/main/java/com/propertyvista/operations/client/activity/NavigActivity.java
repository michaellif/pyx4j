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
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.operations.client.ui.NavigView;
import com.propertyvista.operations.client.viewfactories.OperationsVeiwFactory;
import com.propertyvista.operations.rpc.OperationsSiteMap;

public class NavigActivity extends AbstractActivity implements NavigView.MainNavigPresenter {

    private final NavigView view;

    public NavigActivity(Place place) {
        view = OperationsVeiwFactory.instance(NavigView.class);
        assert (view != null);
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
        folder.addNavigItem(new OperationsSiteMap.Management.OnboardingUser());
        folder.addNavigItem(new OperationsSiteMap.Management.Trigger());
        folder.addNavigItem(new OperationsSiteMap.Management.BillingSetup());

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
        folder.addNavigItem(new OperationsSiteMap.Administration.EquifaxEncryptedStorage());
        list.add(folder);

        if (ApplicationMode.isDevelopment()) {
            folder = new NavigFolder("Dev Simulation");

            folder.addNavigItem(new OperationsSiteMap.Administration.SimulatedDataPreload());
            folder.addNavigItem(new OperationsSiteMap.Administration.PadSimulation.PadSimFile());

            folder.addNavigItem(new OperationsSiteMap.Administration.CardServiceSimulation.CardServiceSimulationMerchantAccount());
            folder.addNavigItem(new OperationsSiteMap.Administration.CardServiceSimulation.CardServiceSimulationCard());
            folder.addNavigItem(new OperationsSiteMap.Administration.CardServiceSimulation.CardServiceSimulationTransaction());

            list.add(folder);
        }

        return list;
    }
}

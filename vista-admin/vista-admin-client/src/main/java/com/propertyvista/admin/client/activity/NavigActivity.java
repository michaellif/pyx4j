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
package com.propertyvista.admin.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.admin.client.ui.NavigView;
import com.propertyvista.admin.client.viewfactories.AdminVeiwFactory;
import com.propertyvista.admin.rpc.AdminSiteMap;

public class NavigActivity extends AbstractActivity implements NavigView.MainNavigPresenter {

    private final NavigView view;

    public NavigActivity(Place place) {
        view = AdminVeiwFactory.instance(NavigView.class);
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
        folder.addNavigItem(new AdminSiteMap.Management.PMC());
        folder.addNavigItem(new AdminSiteMap.Management.OnboardingUser());
        folder.addNavigItem(new AdminSiteMap.Management.Trigger());
        folder.addNavigItem(new AdminSiteMap.Management.BillingSetup());
        list.add(folder);

        // Security
        folder = new NavigFolder("Security");
        folder.addNavigItem(new AdminSiteMap.Security.AuditRecord());
        list.add(folder);

        // Legal
        folder = new NavigFolder("Legal");
        // TODO need one place in map for this, and here have different args for the place
        folder.addNavigItem(new AdminSiteMap.Legal.PortalTerms());
        folder.addNavigItem(new AdminSiteMap.Legal.PmcTerms());
        folder.addNavigItem(new AdminSiteMap.Legal.PmcCaledonTermsTemplate());
        folder.addNavigItem(new AdminSiteMap.Legal.PmcCaldedonSolePropetorshipSectionTerms());
        folder.addNavigItem(new AdminSiteMap.Legal.PmcPaymentPad());
        folder.addNavigItem(new AdminSiteMap.Legal.TenantSurePreAuthorizedPayments());
        list.add(folder);

        // Administration
        folder = new NavigFolder("System Administration");
        folder.addNavigItem(new AdminSiteMap.Administration.Maintenance());
        folder.addNavigItem(new AdminSiteMap.Administration.Simulation());
        folder.addNavigItem(new AdminSiteMap.Administration.AdminUsers());
        list.add(folder);

        if (ApplicationMode.isDevelopment()) {
            folder = new NavigFolder("Dev Simulation");

            folder.addNavigItem(new AdminSiteMap.Administration.SimulatedDataPreload());
            folder.addNavigItem(new AdminSiteMap.Administration.PadSimulation.PadSimFile());

            folder.addNavigItem(new AdminSiteMap.Administration.CardServiceSimulation.CardServiceSimulationMerchantAccount());
            folder.addNavigItem(new AdminSiteMap.Administration.CardServiceSimulation.CardServiceSimulationCard());
            folder.addNavigItem(new AdminSiteMap.Administration.CardServiceSimulation.CardServiceSimulationTransaction());

            list.add(folder);
        }

        return list;
    }
}

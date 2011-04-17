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
package com.propertyvista.crm.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.crm.client.ui.NavigView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.CrmSiteMap.Properties.Arrears;
import com.propertyvista.crm.rpc.CrmSiteMap.Properties.Budgets;
import com.propertyvista.crm.rpc.CrmSiteMap.Properties.Buildings;
import com.propertyvista.crm.rpc.CrmSiteMap.Properties.CityOrders;
import com.propertyvista.crm.rpc.CrmSiteMap.Properties.PurchaseOrders;
import com.propertyvista.crm.rpc.CrmSiteMap.Tenants.AllTenants;
import com.propertyvista.crm.rpc.CrmSiteMap.Tenants.CurrentTenants;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

public class NavigActivity extends AbstractActivity implements NavigView.MainNavigPresenter {

    private final NavigView view;

    @Inject
    public NavigActivity(NavigView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public NavigActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void navigTo(Place place) {
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public String getNavigLabel(AppPlace place) {
        return AppSite.getHistoryMapper().getPlaceInfo(place).getNavigLabel();
    }

    @Override
    public Place getWhere() {
        return AppSite.getPlaceController().getWhere();
    }

    @Override
    public List<NavigFolder> getNavigFolders() {
        ArrayList<NavigFolder> list = new ArrayList<NavigFolder>();
        //Properties
        NavigFolder folder = new NavigFolder("Properties");
        folder.addNavigItem(new Buildings());
        folder.addNavigItem(new Arrears());
        folder.addNavigItem(new Budgets());
        folder.addNavigItem(new PurchaseOrders());
        folder.addNavigItem(new CityOrders());
        list.add(folder);

        //Tenants
        folder = new NavigFolder("Tenants");
        folder.addNavigItem(new CurrentTenants());
        folder.addNavigItem(new AllTenants());
        list.add(folder);

        //Marketing
        folder = new NavigFolder("Marketing");
        list.add(folder);

        //LegalAndCollections
        folder = new NavigFolder("Legal & Collections");
        list.add(folder);

        //Finance
        folder = new NavigFolder("Finance");
        list.add(folder);

        //Reports
        folder = new NavigFolder("Reports");
        folder.addNavigItem(new CrmSiteMap.Report());
        list.add(folder);

        //Dashboards
        folder = new NavigFolder("Dashboards");
        folder.addNavigItem(new CrmSiteMap.Dashboard());
        list.add(folder);

        return list;
    }

}

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
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.NavigView;
import com.propertyvista.crm.client.ui.viewfactories.CrmVeiwFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.DashboardMetadataService;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class NavigActivity extends AbstractActivity implements NavigView.MainNavigPresenter {

    private final NavigView view;

    public NavigActivity(Place place) {
        view = (NavigView) CrmVeiwFactory.instance(NavigView.class);
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
        NavigFolder folder = new NavigFolder("Properties", CrmImages.INSTANCE.propertiesNormal(), CrmImages.INSTANCE.propertiesHover(),
                CrmImages.INSTANCE.propertiesActive());
        folder.addNavigItem(new CrmSiteMap.Properties.Building());
        folder.addNavigItem(new CrmSiteMap.Properties.Unit());
        list.add(folder);

        //Tenants
        folder = new NavigFolder("Tenants", CrmImages.INSTANCE.tenantsNormal(), CrmImages.INSTANCE.tenantsHover(), CrmImages.INSTANCE.tenantsActive());
        folder.addNavigItem(new CrmSiteMap.Tenants.Tenant());
        folder.addNavigItem(new CrmSiteMap.Tenants.Lease());
        folder.addNavigItem(new CrmSiteMap.Tenants.Application());
        folder.addNavigItem(new CrmSiteMap.Tenants.Inquiry());
        list.add(folder);

        //Marketing
        folder = new NavigFolder("Marketing", CrmImages.INSTANCE.marketingNormal(), CrmImages.INSTANCE.marketingHover(), CrmImages.INSTANCE.marketingActive());
        list.add(folder);

        //LegalAndCollections
        folder = new NavigFolder("Legal & Collections", CrmImages.INSTANCE.legalNormal(), CrmImages.INSTANCE.legalHover(), CrmImages.INSTANCE.legalActive());
        list.add(folder);

        //Finance
        folder = new NavigFolder("Finance", CrmImages.INSTANCE.financeNormal(), CrmImages.INSTANCE.financeHover(), CrmImages.INSTANCE.financeActive());
        list.add(folder);

        //Reports
        folder = new NavigFolder("Reports", CrmImages.INSTANCE.reportsNormal(), CrmImages.INSTANCE.reportsHover(), CrmImages.INSTANCE.reportsActive());
        folder.addNavigItem(new CrmSiteMap.Report());
        list.add(folder);

        //Dashboards
        folder = new NavigFolder("Dashboards", CrmImages.INSTANCE.dashboardsNormal(), CrmImages.INSTANCE.dashboardsHover(),
                CrmImages.INSTANCE.dashboardsActive());

        folder.addNavigItem(new CrmSiteMap.Dashboard.DashboardManagement());
        folder.addNavigItem(new CrmSiteMap.Dashboard.SystemDashboard());

        fillDashboards(folder);

        // fill shared and favourite dashboards:  
        list.add(folder);

        return list;
    }

    private void fillDashboards(final NavigFolder folder) {
        DashboardMetadataService service = GWT.create(DashboardMetadataService.class);
        service.listMetadata(new AsyncCallback<Vector<DashboardMetadata>>() {
            @Override
            public void onSuccess(Vector<DashboardMetadata> result) {
                for (DashboardMetadata dmd : result) {
                    CrudAppPlace place = new CrmSiteMap.Dashboard();
                    place.formDashboardPlace(dmd.getPrimaryKey());
                    folder.addNavigItem(place);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        });
    }

}

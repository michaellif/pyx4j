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
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.event.NavigationUpdateEvent;
import com.propertyvista.crm.client.event.NavigationUpdateHandler;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.NavigView;
import com.propertyvista.crm.client.ui.viewfactories.CrmVeiwFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.crm.rpc.services.dashboard.ReportMetadataService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;

public class NavigActivity extends AbstractActivity implements NavigView.MainNavigPresenter, NavigationUpdateHandler {

    private static I18n i18n = I18n.get(NavigActivity.class);

    private final NavigView view;

    private List<NavigFolder> currentfolders;

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
        view.setNavigFolders(currentfolders = createNavigFolders());
        panel.setWidget(view);
        eventBus.addHandler(NavigationUpdateEvent.getType(), this);
    }

    @Override
    public void navigTo(Place place) {
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public String getNavigLabel(AppPlace place) {
        if (place instanceof CrmSiteMap.Report) {
            return ((CrmSiteMap.Report) place).getName();
        } else if (place instanceof CrmSiteMap.Dashboard) {
            return ((CrmSiteMap.Dashboard) place).getName();
        }
        return AppSite.getHistoryMapper().getPlaceInfo(place).getNavigLabel();
    }

    @Override
    public Place getWhere() {
        return AppSite.getPlaceController().getWhere();
    }

    public List<NavigFolder> createNavigFolders() {
        ArrayList<NavigFolder> list = new ArrayList<NavigFolder>();

        //Properties
        NavigFolder folder = new NavigFolder(i18n.tr("Properties"), CrmImages.INSTANCE.propertiesNormal(), CrmImages.INSTANCE.propertiesHover(),
                CrmImages.INSTANCE.propertiesActive());
        folder.addNavigItem(new CrmSiteMap.Properties.Building());
        folder.addNavigItem(new CrmSiteMap.Properties.Unit());
        list.add(folder);

        //Tenants
        folder = new NavigFolder(i18n.tr("Tenants"), CrmImages.INSTANCE.tenantsNormal(), CrmImages.INSTANCE.tenantsHover(), CrmImages.INSTANCE.tenantsActive());
        folder.addNavigItem(new CrmSiteMap.Tenants.Lead());
        folder.addNavigItem(new CrmSiteMap.Tenants.Tenant());
        folder.addNavigItem(new CrmSiteMap.Tenants.Lease());
        folder.addNavigItem(new CrmSiteMap.Tenants.Inquiry());
        list.add(folder);

        //Marketing
        folder = new NavigFolder(i18n.tr("Marketing"), CrmImages.INSTANCE.marketingNormal(), CrmImages.INSTANCE.marketingHover(),
                CrmImages.INSTANCE.marketingActive());
        list.add(folder);

        //LegalAndCollections
        folder = new NavigFolder(i18n.tr("Legal & Collections"), CrmImages.INSTANCE.legalNormal(), CrmImages.INSTANCE.legalHover(),
                CrmImages.INSTANCE.legalActive());
        list.add(folder);

        //Finance
        folder = new NavigFolder(i18n.tr("Finance"), CrmImages.INSTANCE.financeNormal(), CrmImages.INSTANCE.financeHover(), CrmImages.INSTANCE.financeActive());
        list.add(folder);

        //Organisation
        folder = new NavigFolder(i18n.tr("Organisation"), CrmImages.INSTANCE.companyNormal(), CrmImages.INSTANCE.companyHover(),
                CrmImages.INSTANCE.companyActive());
        folder.addNavigItem(new CrmSiteMap.Organisation.Employee());
        folder.addNavigItem(new CrmSiteMap.Organisation.Portfolio());
        list.add(folder);

        //Reports
        folder = new NavigFolder("Reports", CrmImages.INSTANCE.reportsNormal(), CrmImages.INSTANCE.reportsHover(), CrmImages.INSTANCE.reportsActive());
        folder.addNavigItem(new CrmSiteMap.Report.Management());
//        folder.addNavigItem(new CrmSiteMap.Report.System());
        fillReports(folder);
        list.add(folder);

        //Dashboards
        folder = new NavigFolder(i18n.tr("Dashboards"), CrmImages.INSTANCE.dashboardsNormal(), CrmImages.INSTANCE.dashboardsHover(),
                CrmImages.INSTANCE.dashboardsActive());
        folder.addNavigItem(new CrmSiteMap.Dashboard.Management());

// TODO: this folder is populated below (in fillDashboards())... 
//        so we should decide how many system dashborads we'll have and if > 1 
//        - should we show here link to 'most' system one ;)            
//        folder.addNavigItem(new CrmSiteMap.Dashboard.System());

        fillDashboards(folder);
        list.add(folder);

        return list;
    }

    private void fillReports(final NavigFolder folder) {
        ReportMetadataService service = GWT.create(ReportMetadataService.class);
        service.listMetadata(new AsyncCallback<Vector<DashboardMetadata>>() {
            @Override
            public void onSuccess(Vector<DashboardMetadata> result) {
                for (DashboardMetadata dmd : result) {
                    CrudAppPlace place = new CrmSiteMap.Report();
                    place.formDashboardPlace(dmd.getPrimaryKey(), dmd.name().getStringView());
                    folder.addNavigItem(place);
                }
                // update UI:
                view.setNavigFolders(currentfolders);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        });
    }

    private void fillDashboards(final NavigFolder folder) {
        DashboardMetadataService service = GWT.create(DashboardMetadataService.class);
        service.listMetadata(new AsyncCallback<Vector<DashboardMetadata>>() {
            @Override
            public void onSuccess(Vector<DashboardMetadata> result) {
                for (DashboardMetadata dmd : result) {
                    if (dmd.type().getValue() != DashboardType.embedded) {
                        CrudAppPlace place = new CrmSiteMap.Dashboard();
                        place.formDashboardPlace(dmd.getPrimaryKey(), dmd.name().getStringView());
                        folder.addNavigItem(place);
                    }
                }
                // update UI:
                view.setNavigFolders(currentfolders);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        });
    }

    @Override
    public void onNavigationUpdate(NavigationUpdateEvent event) {
        view.setNavigFolders(currentfolders = createNavigFolders());
    }
}

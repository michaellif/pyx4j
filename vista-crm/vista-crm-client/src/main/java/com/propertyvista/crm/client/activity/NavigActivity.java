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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.client.event.BoardUpdateHandler;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.NavigView;
import com.propertyvista.crm.client.ui.viewfactories.CrmVeiwFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.crm.rpc.services.dashboard.ReportMetadataService;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class NavigActivity extends AbstractActivity implements NavigView.MainNavigPresenter, BoardUpdateHandler {

    private static final I18n i18n = I18n.get(NavigActivity.class);

    private final NavigView view;

    private List<NavigFolder> currentfolders;

    public NavigActivity(Place place) {
        view = CrmVeiwFactory.instance(NavigView.class);
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
        eventBus.addHandler(BoardUpdateEvent.getType(), this);
    }

    public List<NavigFolder> createNavigFolders() {
        ArrayList<NavigFolder> list = new ArrayList<NavigFolder>();

        //Properties
        NavigFolder folder = new NavigFolder(i18n.tr("Properties"), CrmImages.INSTANCE.propertiesNormal(), CrmImages.INSTANCE.propertiesHover(),
                CrmImages.INSTANCE.propertiesActive());
        folder.addNavigItem(new CrmSiteMap.Properties.Complex());
        folder.addNavigItem(new CrmSiteMap.Properties.Building());
        folder.addNavigItem(new CrmSiteMap.Properties.Unit());
        list.add(folder);

        //Tenants
        folder = new NavigFolder(i18n.tr("Tenants & Leases"), CrmImages.INSTANCE.tenantsNormal(), CrmImages.INSTANCE.tenantsHover(),
                CrmImages.INSTANCE.tenantsActive());
        folder.addNavigItem(new CrmSiteMap.Tenants.Lease());
        folder.addNavigItem(new CrmSiteMap.Tenants.Tenant());
        folder.addNavigItem(new CrmSiteMap.Tenants.Guarantor());
        folder.addNavigItem(new CrmSiteMap.Tenants.MaintenanceRequest());
        folder.addNavigItem(new CrmSiteMap.Tenants.PastTenant());
        folder.addNavigItem(new CrmSiteMap.Tenants.PastLease());
        list.add(folder);

        //Marketing
        folder = new NavigFolder(i18n.tr("Marketing & Rentals"), CrmImages.INSTANCE.marketingNormal(), CrmImages.INSTANCE.marketingHover(),
                CrmImages.INSTANCE.marketingActive());
        folder.addNavigItem(new CrmSiteMap.Marketing.Lead());
        folder.addNavigItem(new CrmSiteMap.Tenants.LeaseApplication());
        folder.addNavigItem(new CrmSiteMap.Tenants.OnlineMasterApplication());
        folder.addNavigItem(new CrmSiteMap.Marketing.FutureTenant());
        list.add(folder);

        //LegalAndCollections
        folder = new NavigFolder(i18n.tr("Legal & Collections"), CrmImages.INSTANCE.legalNormal(), CrmImages.INSTANCE.legalHover(),
                CrmImages.INSTANCE.legalActive());
        list.add(folder);

        //Finance
        folder = new NavigFolder(i18n.tr("Finance"), CrmImages.INSTANCE.financeNormal(), CrmImages.INSTANCE.financeHover(), CrmImages.INSTANCE.financeActive());
        list.add(folder);

        //Organization
        folder = new NavigFolder(i18n.tr("Organization"), CrmImages.INSTANCE.companyNormal(), CrmImages.INSTANCE.companyHover(),
                CrmImages.INSTANCE.companyActive());
        folder.addNavigItem(new CrmSiteMap.Organization.Employee());
        folder.addNavigItem(new CrmSiteMap.Organization.Portfolio());
        folder.addNavigItem(new CrmSiteMap.Organization.Vendor());
        list.add(folder);

        //Reports
        folder = new NavigFolder(i18n.tr("Reports"), CrmImages.INSTANCE.reportsNormal(), CrmImages.INSTANCE.reportsHover(), CrmImages.INSTANCE.reportsActive());
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
        service.listMetadata(new DefaultAsyncCallback<Vector<DashboardMetadata>>() {
            @Override
            public void onSuccess(Vector<DashboardMetadata> result) {
                for (DashboardMetadata dmd : result) {
                    folder.addNavigItem(new CrmSiteMap.Report().formPlace(dmd.getPrimaryKey(), dmd.name().getStringView()));
                }
                // update UI:
                view.setNavigFolders(currentfolders);
            }
        });
    }

    static final Comparator<DashboardMetadata> ORDER_BY_NAME = new Comparator<DashboardMetadata>() {
        @Override
        public int compare(DashboardMetadata e1, DashboardMetadata e2) {
            return e1.name().getValue().toLowerCase().compareTo(e2.name().getValue().toLowerCase());
        }
    };

    private void fillDashboards(final NavigFolder folder) {
        DashboardMetadataService service = GWT.create(DashboardMetadataService.class);
        service.listMetadata(new DefaultAsyncCallback<Vector<DashboardMetadata>>() {
            @Override
            public void onSuccess(Vector<DashboardMetadata> result) {

                Collections.sort(result, ORDER_BY_NAME);
                int j = result.size();
                for (int i = 0; i < j; i++) {
                    folder.addNavigItem(new CrmSiteMap.Dashboard().formPlace(result.get(i).getPrimaryKey(), result.get(i).name().getStringView()));
                }
                // update UI:
                view.setNavigFolders(currentfolders);
            }
        });
    }

    @Override
    public void onBoardUpdate(BoardUpdateEvent event) {
        view.setNavigFolders(currentfolders = createNavigFolders());
    }
}

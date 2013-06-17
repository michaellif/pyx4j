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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.client.event.BoardUpdateHandler;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.NavigView;
import com.propertyvista.crm.client.ui.viewfactories.CrmVeiwFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;
import com.propertyvista.domain.reports.CustomerCreditCheckReportMetadata;
import com.propertyvista.domain.reports.EftReportMetadata;
import com.propertyvista.shared.config.VistaFeatures;

public class NavigActivity extends AbstractActivity implements NavigView.MainNavigPresenter, BoardUpdateHandler {

    private static final I18n i18n = I18n.get(NavigActivity.class);

    private static final Comparator<DashboardMetadata> ORDER_BY_NAME = new Comparator<DashboardMetadata>() {
        @Override
        public int compare(DashboardMetadata e1, DashboardMetadata e2) {
            return e1.name().getValue().toLowerCase().compareTo(e2.name().getValue().toLowerCase());
        }
    };

    private final NavigView view;

    private final DashboardMetadataCrudService dashboardMetadataCrudService;

    private boolean isDashboardFolderUpdateRequired;

    private static NavigFolder dashboardFolder;

    private static Key previousUserPk;

    public NavigActivity(Place place) {
        Key currentUserPk = ClientContext.getUserVisit() != null ? ClientContext.getUserVisit().getPrincipalPrimaryKey() : null;
        isDashboardFolderUpdateRequired = previousUserPk == null || currentUserPk == null || !previousUserPk.equals(currentUserPk);
        previousUserPk = currentUserPk;

        dashboardMetadataCrudService = GWT.<DashboardMetadataCrudService> create(DashboardMetadataCrudService.class);

        view = CrmVeiwFactory.instance(NavigView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        eventBus.addHandler(BoardUpdateEvent.getType(), this);
        panel.setWidget(view);
        startCreateNavigFolders();
    }

    @Override
    public void onBoardUpdate(BoardUpdateEvent event) {
        isDashboardFolderUpdateRequired = true;
        startCreateNavigFolders();
    }

    private void startCreateNavigFolders() {
        final ArrayList<NavigFolder> list = new ArrayList<NavigFolder>();

        //Properties
        NavigFolder folder = new NavigFolder(i18n.tr("Properties"), CrmImages.INSTANCE.propertiesNormal(), CrmImages.INSTANCE.propertiesHover(),
                CrmImages.INSTANCE.propertiesActive());
        if (!VistaFeatures.instance().yardiIntegration()) {
            folder.addNavigItem(new CrmSiteMap.Properties.Complex());
        }
        folder.addNavigItem(new CrmSiteMap.Properties.Building());
        folder.addNavigItem(new CrmSiteMap.Properties.Unit());
        list.add(folder);

        //Tenants
        if (VistaFeatures.instance().leases()) {
            folder = new NavigFolder(i18n.tr("Tenants & Leases"), CrmImages.INSTANCE.tenantsNormal(), CrmImages.INSTANCE.tenantsHover(),
                    CrmImages.INSTANCE.tenantsActive());
            folder.addNavigItem(new CrmSiteMap.Tenants.Lease());
            folder.addNavigItem(new CrmSiteMap.Tenants.Tenant());
            if (!VistaFeatures.instance().yardiIntegration()) {
                folder.addNavigItem(new CrmSiteMap.Tenants.Guarantor());
            }
            folder.addNavigItem(new CrmSiteMap.Tenants.MaintenanceRequest());
            if (!VistaFeatures.instance().yardiIntegration()) {
                folder.addNavigItem(new CrmSiteMap.Tenants.FormerTenant());
                folder.addNavigItem(new CrmSiteMap.Tenants.FormerGuarantor());
                folder.addNavigItem(new CrmSiteMap.Tenants.FormerLease());
            }
            list.add(folder);
        }

        //Marketing
        if (!VistaFeatures.instance().yardiIntegration()) {
            folder = new NavigFolder(i18n.tr("Marketing & Rentals"), CrmImages.INSTANCE.marketingNormal(), CrmImages.INSTANCE.marketingHover(),
                    CrmImages.INSTANCE.marketingActive());
            folder.addNavigItem(new CrmSiteMap.Marketing.Lead());
            if (VistaFeatures.instance().leases()) {
                folder.addNavigItem(new CrmSiteMap.Tenants.LeaseApplication());
                folder.addNavigItem(new CrmSiteMap.Marketing.PotentialTenant());
            }
            list.add(folder);
        }

        //LegalAndCollections
        folder = new NavigFolder(i18n.tr("Legal & Collections"), CrmImages.INSTANCE.legalNormal(), CrmImages.INSTANCE.legalHover(),
                CrmImages.INSTANCE.legalActive());
        list.add(folder);

        //Finance
        if (VistaFeatures.instance().productCatalog()) {
            folder = new NavigFolder(i18n.tr("Finance"), CrmImages.INSTANCE.financeNormal(), CrmImages.INSTANCE.financeHover(),
                    CrmImages.INSTANCE.financeActive());
            folder.addNavigItem(new CrmSiteMap.Finance.AggregatedTransfer());
            list.add(folder);
        }

        //Organization
        folder = new NavigFolder(i18n.tr("Organization"), CrmImages.INSTANCE.companyNormal(), CrmImages.INSTANCE.companyHover(),
                CrmImages.INSTANCE.companyActive());
        folder.addNavigItem(new CrmSiteMap.Organization.Employee());
        folder.addNavigItem(new CrmSiteMap.Organization.Portfolio());
        if (!VistaFeatures.instance().yardiIntegration()) {
            folder.addNavigItem(new CrmSiteMap.Organization.Vendor());
        }
        list.add(folder);

        //Reports
        folder = makeReportsFolder();
        list.add(folder);

        {
            //Dashboards
            if (isDashboardFolderUpdateRequired) {
                dashboardFolder = new NavigFolder(i18n.tr("Dashboards"), CrmImages.INSTANCE.dashboardsNormal(), CrmImages.INSTANCE.dashboardsHover(),
                        CrmImages.INSTANCE.dashboardsActive());
                dashboardFolder.addNavigItem(new CrmSiteMap.Dashboard.Manage());
                // add the rest of stuff asynchronously from the server

                dashboardMetadataCrudService.list(new DefaultAsyncCallback<EntitySearchResult<DashboardMetadata>>() {

                    @Override
                    public void onSuccess(EntitySearchResult<DashboardMetadata> result) {
                        Vector<DashboardMetadata> metadataList = result.getData();
                        Collections.sort(metadataList, ORDER_BY_NAME);
                        for (DashboardMetadata metadata : metadataList) {
                            dashboardFolder.addNavigItem(new CrmSiteMap.Dashboard.View().formPlace(metadata.getPrimaryKey(), metadata.name().getStringView()));
                        }
                        isDashboardFolderUpdateRequired = false;

                        list.add(dashboardFolder);

                        endCreateNavigFolders(list);
                    }

                }, EntityListCriteria.create(DashboardMetadata.class));

            } else {
                list.add(dashboardFolder);
                endCreateNavigFolders(list);
            }
        }
    }

    private void endCreateNavigFolders(List<NavigFolder> navigFolders) {
        view.setNavigFolders(navigFolders);
    }

    private NavigFolder makeReportsFolder() {
        NavigFolder folder = new NavigFolder(i18n.tr("Reports"), CrmImages.INSTANCE.reportsNormal(), CrmImages.INSTANCE.reportsHover(),
                CrmImages.INSTANCE.reportsActive());
        folder.addNavigItem(new CrmSiteMap.Reports(EntityFactory.create(AvailabilityReportMetadata.class)));
        folder.addNavigItem(new CrmSiteMap.Reports(EntityFactory.create(CustomerCreditCheckReportMetadata.class)));
        folder.addNavigItem(new CrmSiteMap.Reports(EntityFactory.create(EftReportMetadata.class)));
        folder.addNavigItem(new CrmSiteMap.Reports(EntityFactory.create(AutoPayChangesReportMetadata.class)));//        
        return folder;
    }
}

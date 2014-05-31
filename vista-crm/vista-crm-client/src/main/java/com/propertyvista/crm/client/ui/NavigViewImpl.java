/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 28, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.Key;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.sidemenu.SideMenuAppPlaceItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuList;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.shared.config.VistaFeatures;

public class NavigViewImpl extends ScrollPanel implements NavigView {

    private static final I18n i18n = I18n.get(NavigViewImpl.class);

    private static final Comparator<DashboardMetadata> ORDER_BY_NAME = new Comparator<DashboardMetadata>() {
        @Override
        public int compare(DashboardMetadata e1, DashboardMetadata e2) {
            return e1.name().getValue().toLowerCase().compareTo(e2.name().getValue().toLowerCase());
        }
    };

    private final SideMenuList root;

    private NavigPresenter presenter;

    private SideMenuAppPlaceItem systemDashboard;

    private SideMenuList customDashboards;

    public NavigViewImpl() {
        setStyleName(SiteViewTheme.StyleName.SiteViewSideMenu.name());

        setHeight("100%");

        root = new SideMenuList();

        {//Dashboards
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Dashboards"), CrmImages.INSTANCE.dashboardsIcon()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Dashboard.Manage(), null));

            systemDashboard = new SideMenuAppPlaceItem(new CrmSiteMap.Dashboard.View().formPlace(new Key(-1)), null);
            list.addMenuItem(systemDashboard);

            customDashboards = new SideMenuList();
            list.addMenuItem(new SideMenuItem(customDashboards, i18n.tr("Custom Dashboards"), null));

        }

        {//Properties
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Properties"), CrmImages.INSTANCE.propertiesIcon()));

            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Complex(), null));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Building(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Unit(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Landlord(), null));

        }

        {//Tenants
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Tenants & Leases"), CrmImages.INSTANCE.tenantsIcon()));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.Lease(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.Tenant(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.Guarantor(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.MaintenanceRequest(), null));
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.FormerTenant(), null));
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.FormerGuarantor(), null));
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.FormerLease(), null));
            }
        }

        {//Marketing
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Marketing & Rentals"), CrmImages.INSTANCE.marketingIcon()));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Marketing.Lead(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.LeaseApplication(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Marketing.PotentialTenant(), null));
        }

        {//LegalAndCollections
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Legal & Collections"), CrmImages.INSTANCE.legalIcon()));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.LegalAndCollections.N4GenerationTool(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.LegalAndCollections.N4DownloadTool(), null));
            if (false) { // TODO L1 implementation
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.LegalAndCollections.L1GenerationTool(), null));
            }
        }

        {//Finance
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Finance"), CrmImages.INSTANCE.financeIcon()));

            if (SecurityController.checkBehavior(VistaCrmBehavior.AggregatedTransfer)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AggregatedTransfer(), null));
            }
            if (SecurityController.checkBehavior(VistaCrmBehavior.BuildingFinancial)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AutoPay(), null));
            }
            if (SecurityController.checkBehavior(VistaCrmBehavior.Billing)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AutoPayReview(), null));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.MoneyIn(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.MoneyIn.Batch(), null));

            if (SecurityController.checkAnyBehavior(VistaCrmBehavior.AggregatedTransfer, VistaCrmBehavior.Billing)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.Payment(), null));
            }
        }

        {//Organization
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Organization"), CrmImages.INSTANCE.organizationIcon()));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Organization.Employee(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Organization.Portfolio(), null));
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Organization.Vendor(), null));
            }
        }

        {//Reports
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Reports"), CrmImages.INSTANCE.reportsIcon()));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.AutoPayChanges(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.Availability(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.CustomerCreditCheck(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.Eft(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.EftVariance(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.ResidentInsurance(), null));
        }

        add(root.asWidget());

    }

    @Override
    public void setPresenter(NavigPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void select(AppPlace appPlace) {
        root.select(appPlace);
        SideMenuItem selected = root.getSelectedLeaf();
        if (selected != null) {
            ensureVisible(selected.asWidget());
        }
    }

    @Override
    public void updateDashboards(Vector<DashboardMetadata> metadataList) {
        customDashboards.clear();
        Collections.sort(metadataList, ORDER_BY_NAME);
        for (DashboardMetadata metadata : metadataList) {
            customDashboards.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Dashboard.View().formPlace(metadata.getPrimaryKey()), metadata.name()
                    .getStringView(), null));
        }
    }
}

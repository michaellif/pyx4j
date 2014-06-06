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

import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.commons.Key;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.sidemenu.SideMenu;
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

public class NavigViewImpl extends Composite implements NavigView {

    private static final I18n i18n = I18n.get(NavigViewImpl.class);

    private static final Comparator<DashboardMetadata> ORDER_BY_NAME = new Comparator<DashboardMetadata>() {
        @Override
        public int compare(DashboardMetadata e1, DashboardMetadata e2) {
            return e1.name().getValue().toLowerCase().compareTo(e2.name().getValue().toLowerCase());
        }
    };

    private final SideMenu menu;

    private final SideMenuItem userMenuItem;

    private SideMenuAppPlaceItem systemDashboard;

    private SideMenuList customDashboards;

    public NavigViewImpl() {

        SideMenuList root = new SideMenuList();
        menu = new SideMenu(root);
        initWidget(menu);

        setStyleName(SiteViewTheme.StyleName.SiteViewSideMenu.name());

        setHeight("100%");

        {//User
            SideMenuList list = new SideMenuList();
            //  root.addMenuItem(userMenuItem = new SideMenuItem(list, "User", CrmImages.INSTANCE.userIcon()));
            root.addMenuItem(userMenuItem = new SideMenuItem(list, "User", CrmImages.INSTANCE.tenantsIcon()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Account.AccountData()));
        }

        {//Dashboards
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Dashboards"), CrmImages.INSTANCE.dashboardsIcon()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Dashboard.Manage()));

            systemDashboard = new SideMenuAppPlaceItem(new CrmSiteMap.Dashboard.View().formPlace(new Key(-1)));
            list.addMenuItem(systemDashboard);

            customDashboards = new SideMenuList();
            list.addMenuItem(new SideMenuItem(customDashboards, i18n.tr("Custom Dashboards"), null));

        }

        {//Properties
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Properties"), CrmImages.INSTANCE.propertiesIcon()));

            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Complex()));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Building()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Unit()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Landlord()));

        }

        {//Tenants
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Tenants & Leases"), CrmImages.INSTANCE.tenantsIcon()));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.Lease()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.Tenant()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.Guarantor()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.MaintenanceRequest()));
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.FormerTenant()));
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.FormerGuarantor()));
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.FormerLease()));
            }
        }

        {//Marketing
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Marketing & Rentals"), CrmImages.INSTANCE.marketingIcon()));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Marketing.Lead()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.LeaseApplication()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Marketing.PotentialTenant()));
        }

        {//LegalAndCollections
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Legal & Collections"), CrmImages.INSTANCE.legalIcon()));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.LegalAndCollections.N4GenerationTool()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.LegalAndCollections.N4DownloadTool()));
            if (false) { // TODO L1 implementation
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.LegalAndCollections.L1GenerationTool()));
            }
        }

        {//Finance
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Finance"), CrmImages.INSTANCE.financeIcon()));

            if (SecurityController.checkBehavior(VistaCrmBehavior.AggregatedTransfer)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AggregatedTransfer()));
            }
            if (SecurityController.checkBehavior(VistaCrmBehavior.BuildingFinancial)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AutoPay()));
            }
            if (SecurityController.checkBehavior(VistaCrmBehavior.Billing)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AutoPayReview()));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.MoneyIn()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.MoneyIn.Batch()));

            if (SecurityController.checkAnyBehavior(VistaCrmBehavior.AggregatedTransfer, VistaCrmBehavior.Billing)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.Payment()));
            }
        }

        {//Organization
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Organization"), CrmImages.INSTANCE.organizationIcon()));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Organization.Employee()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Organization.Portfolio()));
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Organization.Vendor()));
            }
        }

        {//Reports
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Reports"), CrmImages.INSTANCE.reportsIcon()));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.AutoPayChanges()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.Availability()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.CustomerCreditCheck()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.Eft()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.EftVariance()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.ResidentInsurance()));
        }

    }

    @Override
    public void select(AppPlace appPlace) {
        menu.select(appPlace);
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

    @Override
    public void updateUserName(String name) {
        userMenuItem.setCaption(name);
    }
}

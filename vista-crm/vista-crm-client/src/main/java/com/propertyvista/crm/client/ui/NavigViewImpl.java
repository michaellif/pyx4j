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
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.sidemenu.SideMenu;
import com.pyx4j.site.client.ui.sidemenu.SideMenuAppPlaceItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuList;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ClientLocaleUtils;
import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ComplexDTO;
import com.propertyvista.dto.LandlordDTO;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.shared.config.VistaFeatures;
import com.propertyvista.shared.i18n.CompiledLocale;

public class NavigViewImpl extends Composite implements NavigView {

    private static final I18n i18n = I18n.get(NavigViewImpl.class);

    private static final Comparator<DashboardMetadata> ORDER_BY_NAME = new Comparator<DashboardMetadata>() {
        @Override
        public int compare(DashboardMetadata e1, DashboardMetadata e2) {
            return e1.name().getValue().toLowerCase().compareTo(e2.name().getValue().toLowerCase());
        }
    };

    private NavigPresenter presenter;

    private final SideMenu menu;

    private final SideMenuItem userMenuItem;

    private final SideMenuItem adminMenuItem;

    private final SideMenuItem exitAdminMenuItem;

    private SideMenuList languagesMenuList;

    private SideMenuAppPlaceItem systemDashboard;

    private SideMenuList customDashboards;

    private LayoutType layoutType;

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

            list.addMenuItem(adminMenuItem = new SideMenuItem(new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new CrmSiteMap.Administration.Financial.ARCode());
                }
            }, i18n.tr("Administration"), null));

            list.addMenuItem(exitAdminMenuItem = new SideMenuItem(new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(CrmSite.getSystemDashboardPlace());
                }
            }, i18n.tr("Exit Administration"), null));

            list.addMenuItem(new SideMenuItem(new Command() {
                @Override
                public void execute() {
                    presenter.getSatisfaction();
                }
            }, i18n.tr("Support"), null));

            languagesMenuList = new SideMenuList();
            list.addMenuItem(new SideMenuItem(languagesMenuList, i18n.tr("Languages"), null));

            list.addMenuItem(new SideMenuItem(new Command() {
                @Override
                public void execute() {
                    presenter.logout();
                }
            }, i18n.tr("LogOut"), null));
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
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Complex(), DataModelPermission.permissionRead(ComplexDTO.class)));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Building(), DataModelPermission.permissionRead(BuildingDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Unit(), DataModelPermission.permissionRead(AptUnitDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Landlord(), DataModelPermission.permissionRead(LandlordDTO.class)));

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

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Marketing.Lead(), DataModelPermission.permissionRead(Lead.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.LeaseApplication(), DataModelPermission.permissionRead(LeaseApplicationDTO.class)));
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

            if (SecurityController.checkBehavior(VistaCrmBehavior.AggregatedTransfer_OLD)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AggregatedTransfer()));
            }
            if (SecurityController.checkBehavior(VistaCrmBehavior.BuildingFinancial_OLD)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AutoPay()));
            }
            if (SecurityController.checkBehavior(VistaCrmBehavior.Billing_OLD)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AutoPayReview()));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.MoneyIn()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.MoneyIn.Batch()));

            if (SecurityController.checkAnyBehavior(VistaCrmBehavior.AggregatedTransfer_OLD, VistaCrmBehavior.Billing_OLD)) {
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

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

    }

    @Override
    public void setPresenter(final NavigPresenter presenter) {
        this.presenter = presenter;
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
    public void setAvailableLocales(List<CompiledLocale> localeList) {
        languagesMenuList.clear();
        CompiledLocale currentLocale = ClientLocaleUtils.getCurrentLocale();
        for (final CompiledLocale compiledLocale : localeList) {
            languagesMenuList.addMenuItem(new SideMenuItem(new Command() {
                @Override
                public void execute() {
                    presenter.setLocale(compiledLocale);
                }
            }, compiledLocale.getNativeDisplayName() + (currentLocale.equals(compiledLocale) ? " V" : ""), null));
        }
    }

    @Override
    public void updateUserName(String name) {
        userMenuItem.setCaption(name);
    }

    private void doLayout(LayoutType layoutType) {
        this.layoutType = layoutType;
        calculateActionsState();
    }

    private void calculateActionsState() {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            userMenuItem.setVisible(true);
            if (presenter != null) {
                if (presenter.isAdminPlace()) {
                    exitAdminMenuItem.setVisible(true);
                    adminMenuItem.setVisible(false);
                } else {
                    exitAdminMenuItem.setVisible(false);
                    adminMenuItem.setVisible(true);
                }
            }

            break;
        default:
            userMenuItem.setVisible(false);
            exitAdminMenuItem.setVisible(false);
            adminMenuItem.setVisible(false);

            break;
        }

    }
}

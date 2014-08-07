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
import com.propertyvista.crm.rpc.CrmMainNavigationDebugId;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.company.ac.EmployeeDirectoryList;
import com.propertyvista.crm.rpc.dto.financial.AutoPayHistoryDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.services.customer.ac.FormerGuarantorListAction;
import com.propertyvista.crm.rpc.services.customer.ac.FormerTenantListAction;
import com.propertyvista.crm.rpc.services.customer.ac.PotentialTenantListAction;
import com.propertyvista.crm.rpc.services.customer.ac.TenantListAction;
import com.propertyvista.crm.rpc.services.lease.ac.FormerLeaseListAction;
import com.propertyvista.crm.rpc.services.reports.CrmReportsMapper;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.reports.AvailableCrmReport.CrmReportType;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ComplexDTO;
import com.propertyvista.dto.GuarantorDTO;
import com.propertyvista.dto.LandlordDTO;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.MessageDTO;
import com.propertyvista.dto.PaymentRecordDTO;
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

    private static final Comparator<MessageCategory> ORDER_CATEGORY_BY_NAME = new Comparator<MessageCategory>() {
        @Override
        public int compare(MessageCategory e1, MessageCategory e2) {
            return e1.topic().getValue().toLowerCase().compareTo(e2.topic().getValue().toLowerCase());
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

    private SideMenuList communicationGroups;

    private SideMenuList reports;

    private LayoutType layoutType;

    public NavigViewImpl() {

        SideMenuList root = new SideMenuList();
        menu = new SideMenu(root);
        initWidget(menu);

        setStyleName(SiteViewTheme.StyleName.SiteViewSideMenu.name());

        setHeight("100%");

        {//User
            SideMenuList list = new SideMenuList();
            root.addMenuItem(userMenuItem = new SideMenuItem(list, "User", CrmImages.INSTANCE.userIcon(), null));
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
                    AppSite.getPlaceController().goTo(CrmSite.getDefaultPlace());
                }
            }, i18n.tr("Exit Administration"), null));

            list.addMenuItem(new SideMenuItem(new Command() {
                @Override
                public void execute() {
                    presenter.getSatisfaction();
                }
            }, i18n.tr("Support"), null));

            languagesMenuList = new SideMenuList();
            list.addMenuItem(new SideMenuItem(languagesMenuList, i18n.tr("Languages"), null, null));

            list.addMenuItem(new SideMenuItem(new Command() {
                @Override
                public void execute() {
                    presenter.logout();
                }
            }, i18n.tr("LogOut"), null));
        }

        {//Dashboards
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Dashboards"), CrmImages.INSTANCE.dashboardsIcon(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Dashboard.Manage(), DataModelPermission.permissionRead(DashboardMetadata.class)));

            systemDashboard = new SideMenuAppPlaceItem(CrmSite.getSystemDashboardPlace(), DataModelPermission.permissionRead(DashboardMetadata.class));
            list.addMenuItem(systemDashboard);

            customDashboards = new SideMenuList();
            list.addMenuItem(new SideMenuItem(customDashboards, i18n.tr("Custom Dashboards"), null, null));
        }

        {//Properties
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Properties"), CrmImages.INSTANCE.propertiesIcon(), CrmMainNavigationDebugId.Properties));

            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Complex(), DataModelPermission.permissionRead(ComplexDTO.class)));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Building(), DataModelPermission.permissionRead(BuildingDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Unit(), DataModelPermission.permissionRead(AptUnitDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Landlord(), DataModelPermission.permissionRead(LandlordDTO.class)));
        }

        {//Tenants
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Tenants & Leases"), CrmImages.INSTANCE.tenantsIcon(), CrmMainNavigationDebugId.Leases));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.Lease(), DataModelPermission.permissionRead(LeaseDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.Tenant(), TenantListAction.class));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.Guarantor(), DataModelPermission.permissionRead(GuarantorDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.MaintenanceRequest(), DataModelPermission
                    .permissionRead(MaintenanceRequestDTO.class)));
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.FormerLease(), FormerLeaseListAction.class));
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.FormerTenant(), FormerTenantListAction.class));
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.FormerGuarantor(), FormerGuarantorListAction.class));
            }
        }

        {//Marketing
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Marketing & Rentals"), CrmImages.INSTANCE.marketingIcon(), CrmMainNavigationDebugId.Marketing));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Marketing.Lead(), DataModelPermission.permissionRead(Lead.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.LeaseApplication(), DataModelPermission.permissionRead(LeaseApplicationDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Marketing.PotentialTenant(), PotentialTenantListAction.class));
        }

        {//LegalAndCollections
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Legal & Collections"), CrmImages.INSTANCE.legalIcon(), null));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.LegalAndCollections.N4GenerationTool(), DataModelPermission
                    .permissionCreate(LegalNoticeCandidateDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.LegalAndCollections.N4DownloadTool(), DataModelPermission
                    .permissionRead(LegalNoticeCandidateDTO.class)));
            if (false) { // TODO L1 implementation
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.LegalAndCollections.L1GenerationTool()));
            }
        }

        {//Finance
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Finance"), CrmImages.INSTANCE.financeIcon(), CrmMainNavigationDebugId.Finance));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AggregatedTransfer(), DataModelPermission.permissionRead(AggregatedTransfer.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AutoPay(), DataModelPermission.permissionRead(AutoPayHistoryDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AutoPayReview(), DataModelPermission.permissionUpdate(MoneyInBatchDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.MoneyIn(), DataModelPermission.permissionCreate(MoneyInBatchDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.MoneyIn.Batch(), DataModelPermission.permissionRead(MoneyInBatchDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.Payment(), DataModelPermission.permissionRead(PaymentRecordDTO.class)));
        }

        {//Organization
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Organization"), CrmImages.INSTANCE.organizationIcon(), null));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Organization.Employee(), EmployeeDirectoryList.class));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Organization.Portfolio(), DataModelPermission.permissionRead(Portfolio.class)));
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Organization.Vendor()));
            }
        }

        {//Message Center
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Message Center"), CrmImages.INSTANCE.reportsIcon(), null));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Communication.Message(), DataModelPermission.permissionRead(MessageDTO.class)));
            communicationGroups = new SideMenuList();
            list.addMenuItem(new SideMenuItem(communicationGroups, i18n.tr("Groups"), null, null, DataModelPermission.permissionRead(MessageCategory.class)));
        }

        {//Reports
            reports = new SideMenuList();
            root.addMenuItem(new SideMenuItem(reports, i18n.tr("Reports"), CrmImages.INSTANCE.reportsIcon(), null));

//            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.AutoPayChanges()));
//            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.Availability()));
//            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.CustomerCreditCheck()));
//            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.Eft()));
//            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.EftVariance()));
//            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.ResidentInsurance()));
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
    public void updateAvailableReports(List<CrmReportType> reportTypes) {
        reports.clear();
        for (CrmReportType type : reportTypes) {
            reports.addMenuItem(new SideMenuAppPlaceItem(CrmReportsMapper.resolvePlace(type)));
        }
    }

    @Override
    public void updateCommunicationGroups(Vector<MessageCategory> metadataList) {
        if (!SecurityController.check(VistaCrmBehavior.Communication)) {
            return;
        }
        communicationGroups.clear();
        Collections.sort(metadataList, ORDER_CATEGORY_BY_NAME);
        for (MessageCategory metadata : metadataList) {
            communicationGroups.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Communication.Message(metadata).formListerPlace(), metadata.topic()
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
            }, compiledLocale.getNativeDisplayName() + (currentLocale.equals(compiledLocale) ? " \u2713" : ""), null));
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

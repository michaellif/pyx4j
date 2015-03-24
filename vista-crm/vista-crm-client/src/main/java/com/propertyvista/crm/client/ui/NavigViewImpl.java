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
 */
package com.propertyvista.crm.client.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.sidemenu.SideMenu;
import com.pyx4j.site.client.ui.sidemenu.SideMenuAppPlaceItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuCommand;
import com.pyx4j.site.client.ui.sidemenu.SideMenuFolderItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuList;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ClientLocaleUtils;
import com.propertyvista.common.client.WalkMe;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.rpc.CrmMainNavigationDebugId;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.company.ac.EmployeeDirectoryList;
import com.propertyvista.crm.rpc.dto.financial.AutoPayHistoryDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;
import com.propertyvista.crm.rpc.services.customer.ac.FormerGuarantorListAction;
import com.propertyvista.crm.rpc.services.customer.ac.FormerTenantListAction;
import com.propertyvista.crm.rpc.services.customer.ac.GuarantorListAction;
import com.propertyvista.crm.rpc.services.customer.ac.PotentialTenantListAction;
import com.propertyvista.crm.rpc.services.customer.ac.TenantListAction;
import com.propertyvista.crm.rpc.services.lease.ac.FormerLeaseListAction;
import com.propertyvista.crm.rpc.services.reports.CrmReportsMapper;
import com.propertyvista.domain.communication.BroadcastEvent;
import com.propertyvista.domain.communication.BroadcastTemplate;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.policy.policies.EvictionFlowPolicy;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep.EvictionStepType;
import com.propertyvista.domain.reports.AvailableCrmReport.CrmReportType;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ComplexDTO;
import com.propertyvista.dto.LandlordDTO;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.N4BatchDTO;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.communication.CommunicationThreadDTO;
import com.propertyvista.dto.communication.MessageDTO;
import com.propertyvista.misc.VistaTODO;
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
            return e1.category().getValue().toLowerCase().compareTo(e2.category().getValue().toLowerCase());
        }
    };

    private NavigPresenter presenter;

    private final SideMenu menu;

    private final SideMenuItem userMenuItem;

    private SideMenuList languagesMenuList;

    private SideMenuAppPlaceItem systemDashboard;

    private SideMenuList customDashboards;

    private SideMenuList communicationGroups;

    private SideMenuAppPlaceItem dispatchedQueue;

    private SideMenuAppPlaceItem n4batches;

    private SideMenuList reports;

    private LayoutType layoutType;

    public NavigViewImpl() {

        SideMenuList root = new SideMenuList();
        menu = new SideMenu(root);
        initWidget(menu);

        setStyleName(SiteViewTheme.StyleName.SiteViewSideMenu.name());

        setHeight("100%");

        SideMenuItem sideMenuItem;

        SideMenuList sideMenuList;

        {//User
            sideMenuList = new SideMenuList();
            root.addMenuItem(userMenuItem = new SideMenuFolderItem(sideMenuList, "User", CrmImages.INSTANCE.userIcon()));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Account.UserProfile()));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Account.AccountPreferences()));

            sideMenuList.addMenuItem(new SideMenuItem(new SideMenuCommand() {

                @Override
                public boolean execute() {
                    AppSite.getPlaceController().goTo(new CrmSiteMap.Administration.Financial.ARCode());
                    return true;
                }
            }, i18n.tr("Administration"), null));

            sideMenuList.addMenuItem(new SideMenuItem(new SideMenuCommand() {
                @Override
                public boolean execute() {
                    presenter.getSatisfaction();
                    return true;
                }
            }, i18n.tr("Support"), null));

            languagesMenuList = new SideMenuList();
            sideMenuList.addMenuItem(new SideMenuFolderItem(languagesMenuList, i18n.tr("Languages"), null));

            sideMenuList.addMenuItem(new SideMenuItem(new SideMenuCommand() {
                @Override
                public boolean execute() {
                    presenter.logout();
                    return true;
                }
            }, i18n.tr("LogOut"), null));
        }

        {//Dashboards
            sideMenuList = new SideMenuList();
            root.addMenuItem(new SideMenuFolderItem(sideMenuList, i18n.tr("Dashboards"), CrmImages.INSTANCE.dashboardsIcon()));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Dashboard.Manage(), DataModelPermission.permissionRead(DashboardMetadata.class)));

            systemDashboard = new SideMenuAppPlaceItem(CrmSite.getSystemDashboardPlace(), DataModelPermission.permissionRead(DashboardMetadata.class));
            sideMenuList.addMenuItem(systemDashboard);

            customDashboards = new SideMenuList();
            sideMenuList.addMenuItem(new SideMenuFolderItem(customDashboards, i18n.tr("Custom Dashboards"), null));
        }

        {//Properties
            sideMenuList = new SideMenuList();

            sideMenuItem = new SideMenuFolderItem(sideMenuList, i18n.tr("Properties"), CrmImages.INSTANCE.propertiesIcon());
            sideMenuItem.setDebugId(CrmMainNavigationDebugId.Properties);
            root.addMenuItem(sideMenuItem);

            if (!VistaFeatures.instance().yardiIntegration()) {
                sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Complex(), DataModelPermission.permissionRead(ComplexDTO.class)));
            }
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Building(), DataModelPermission.permissionRead(BuildingDTO.class)));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Unit(), DataModelPermission.permissionRead(AptUnitDTO.class)));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Properties.Landlord(), DataModelPermission.permissionRead(LandlordDTO.class)));
        }

        {//Tenants
            sideMenuList = new SideMenuList();

            sideMenuItem = new SideMenuFolderItem(sideMenuList, i18n.tr("Tenants & Leases"), CrmImages.INSTANCE.tenantsIcon());
            sideMenuItem.setDebugId(CrmMainNavigationDebugId.Leases);
            root.addMenuItem(sideMenuItem);

            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.Lease(), DataModelPermission.permissionRead(LeaseDTO.class)));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.Tenant(), TenantListAction.class));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.Guarantor(), GuarantorListAction.class));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.MaintenanceRequest(), DataModelPermission
                    .permissionRead(MaintenanceRequestDTO.class)));
            if (!VistaFeatures.instance().yardiIntegration()) {
                sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.FormerLease(), FormerLeaseListAction.class));
                sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.FormerTenant(), FormerTenantListAction.class));
                sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.FormerGuarantor(), FormerGuarantorListAction.class));
            }
        }

        {//Marketing
            sideMenuList = new SideMenuList();

            sideMenuItem = new SideMenuFolderItem(sideMenuList, i18n.tr("Marketing & Rentals"), CrmImages.INSTANCE.marketingIcon());
            sideMenuItem.setDebugId(CrmMainNavigationDebugId.Marketing);
            root.addMenuItem(sideMenuItem);

            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Marketing.Lead(), DataModelPermission.permissionRead(Lead.class)));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Tenants.LeaseApplication(), DataModelPermission
                    .permissionRead(LeaseApplicationDTO.class)));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Marketing.PotentialTenant(), PotentialTenantListAction.class));
        }

        {//LegalAndCollections
            sideMenuList = new SideMenuList();

            root.addMenuItem(new SideMenuFolderItem(sideMenuList, i18n.tr("Legal & Collections"), CrmImages.INSTANCE.legalIcon()));

            sideMenuList.addMenuItem(n4batches = new SideMenuAppPlaceItem(new CrmSiteMap.LegalAndCollections.N4Batches(), DataModelPermission
                    .permissionRead(N4BatchDTO.class)));
            setN4BatchesVisibility();
            if (false) { // TODO L1 implementation
                sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.LegalAndCollections.L1GenerationTool()));
            }
        }

        {//Finance
            sideMenuList = new SideMenuList();

            sideMenuItem = new SideMenuFolderItem(sideMenuList, i18n.tr("Finance"), CrmImages.INSTANCE.financeIcon());
            sideMenuItem.setDebugId(CrmMainNavigationDebugId.Finance);
            root.addMenuItem(sideMenuItem);

            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AggregatedTransfer(), DataModelPermission
                    .permissionRead(AggregatedTransfer.class)));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AutoPay(), DataModelPermission.permissionRead(AutoPayHistoryDTO.class)));
            sideMenuList
                    .addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.AutoPayReview(), DataModelPermission.permissionUpdate(PapReviewDTO.class)));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.MoneyIn(), DataModelPermission.permissionCreate(MoneyInBatchDTO.class)));
            sideMenuList
                    .addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.MoneyIn.Batch(), DataModelPermission.permissionRead(MoneyInBatchDTO.class)));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Finance.Payment(), DataModelPermission.permissionRead(PaymentRecordDTO.class)));
        }

        {//Organization
            sideMenuList = new SideMenuList();
            root.addMenuItem(new SideMenuFolderItem(sideMenuList, i18n.tr("Organization"), CrmImages.INSTANCE.organizationIcon()));

            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Organization.Employee(), EmployeeDirectoryList.class));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Organization.Portfolio(), DataModelPermission.permissionRead(Portfolio.class)));
            if (!VistaFeatures.instance().yardiIntegration()) {
                sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Organization.Vendor()));
            }
        }

        {//Message Center
            sideMenuList = new SideMenuList();
            root.addMenuItem(new SideMenuFolderItem(sideMenuList, i18n.tr("Message Center"), CrmImages.INSTANCE.messageCenterIcon()));

            sideMenuList.addMenuItem(dispatchedQueue = new SideMenuAppPlaceItem(new CrmSiteMap.Communication.Message(ClientContext.getUserVisit()).queryArg(
                    CommunicationThreadDTO.ViewScope.class.getSimpleName(), CommunicationThreadDTO.ViewScope.DispatchQueue.toString()), i18n
                    .tr("Dispatch Queue"), null, DataModelPermission.permissionRead(MessageDTO.class)));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Communication.Message().queryArg(
                    CommunicationThreadDTO.ViewScope.class.getSimpleName(), CommunicationThreadDTO.ViewScope.Messages.toString()), i18n.tr("Messages"), null,
                    DataModelPermission.permissionRead(MessageDTO.class)));
            communicationGroups = new SideMenuList();
            sideMenuList.addMenuItem(new SideMenuFolderItem(communicationGroups, i18n.tr("Groups"), null, null, DataModelPermission
                    .permissionRead(MessageCategory.class)));
            if (VistaTODO.VISTA_1288_Communication_Broadcast) {
                SideMenuList broadcastFolder = new SideMenuList();

                sideMenuList.addMenuItem(new SideMenuFolderItem(broadcastFolder, i18n.tr("Broadcast"), null, null, DataModelPermission
                        .permissionRead(BroadcastTemplate.class)));

                broadcastFolder.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Communication.BroadcastTemplate(), DataModelPermission
                        .permissionRead(BroadcastTemplate.class)));
                broadcastFolder.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Communication.BroadcastEvent(), DataModelPermission
                        .permissionRead(BroadcastEvent.class)));
            }
        }

        {//Reports
            reports = new SideMenuList();
            root.addMenuItem(new SideMenuFolderItem(reports, i18n.tr("Reports"), CrmImages.INSTANCE.reportsIcon()));

//            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.AutoPayChanges()));
//            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.Availability()));
//            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.CustomerCreditCheck()));
//            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.Eft()));
//            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.EftVariance()));
//            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.ResidentInsurance()));
        }

        {//WalkMe
            root.addMenuItem(new SideMenuItem(new SideMenuCommand() {
                @Override
                public boolean execute() {
                    WalkMe.toggleMenu();
                    return true;
                }
            }, i18n.tr("Help"), CrmImages.INSTANCE.helpIcon()));
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
            if (type.isOld()) {
                reports.addMenuItem(new SideMenuAppPlaceItem(CrmReportsMapper.resolvePlace(type)));
            } else {
                switch (type) {
                case AutoPayReconciliation:
                    reports.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Reports.AutoPayReconciliation()));
                    break;
                default:
                    throw new IllegalArgumentException("not implemented");
                }
            }
        }
    }

    @Override
    public void updateCommunicationGroups(Vector<MessageCategory> metadataList) {

        dispatchedQueue.setVisible(false);
        communicationGroups.clear();
        Collections.sort(metadataList, ORDER_CATEGORY_BY_NAME);
        for (MessageCategory metadata : metadataList) {
            AppPlace place = null;
            CategoryType cat = metadata.categoryType().getValue();
            boolean added = false;
            if (metadata.dispatchers() != null) {
                for (Employee emp : metadata.dispatchers()) {
                    if (ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(emp.user().getPrimaryKey())) {
                        dispatchedQueue.setVisible(true);
                        place = new CrmSiteMap.Communication.Message(metadata).formListerPlace().queryArg(
                                CommunicationThreadDTO.ViewScope.class.getSimpleName(),
                                CategoryType.Message.equals(cat) ? CommunicationThreadDTO.ViewScope.MessageCategory.toString()
                                        : CommunicationThreadDTO.ViewScope.TicketCategory.toString(), metadata.getPrimaryKey().toString());
                        communicationGroups.addMenuItem(new SideMenuAppPlaceItem(place, metadata.category().getStringView(), null));
                        added = true;
                    }
                }
            }
            if (!added && metadata.roles() != null) {
                for (CrmRole role : metadata.roles()) {
                    if (cat != null && SecurityController.check(role.behaviors())) {
                        place = new CrmSiteMap.Communication.Message(metadata).formListerPlace().queryArg(
                                CommunicationThreadDTO.ViewScope.class.getSimpleName(),
                                CategoryType.Message.equals(cat) ? CommunicationThreadDTO.ViewScope.MessageCategory.toString()
                                        : CommunicationThreadDTO.ViewScope.TicketCategory.toString(), metadata.getPrimaryKey().toString());
                        communicationGroups.addMenuItem(new SideMenuAppPlaceItem(place, metadata.category().getStringView(), null));
                        break;
                    }
                }
            }

        }
    }

    @Override
    public void setN4BatchesVisibility() {
        ClientPolicyManager.obtainEffectivePolicy(ClientPolicyManager.getOrganizationPoliciesNode(), EvictionFlowPolicy.class,
                new DefaultAsyncCallback<EvictionFlowPolicy>() {
                    @Override
                    public void onSuccess(EvictionFlowPolicy policy) {
                        boolean n4visible = false;
                        for (EvictionFlowStep step : policy.evictionFlow()) {
                            if (EvictionStepType.N4.equals(step.stepType().getValue())) {
                                n4visible = true;
                                break;
                            }
                        }
                        n4batches.setVisible(n4visible);
                    }
                });
    }

    @Override
    public void setAvailableLocales(List<CompiledLocale> localeList) {
        languagesMenuList.clear();
        CompiledLocale currentLocale = ClientLocaleUtils.getCurrentLocale();
        for (final CompiledLocale compiledLocale : localeList) {
            languagesMenuList.addMenuItem(new SideMenuItem(new SideMenuCommand() {
                @Override
                public boolean execute() {
                    presenter.setLocale(compiledLocale);
                    return true;
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
            break;
        default:
            userMenuItem.setVisible(false);
            break;
        }

    }

}

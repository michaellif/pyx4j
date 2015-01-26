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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.sidemenu.SideMenu;
import com.pyx4j.site.client.ui.sidemenu.SideMenuAppPlaceItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuCommand;
import com.pyx4j.site.client.ui.sidemenu.SideMenuFolderItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuList;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.ContentManagement;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Financial;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Security;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Settings;
import com.propertyvista.crm.rpc.dto.admin.PmcCompanyInfoDTO;
import com.propertyvista.crm.rpc.dto.admin.PmcPaymentMethodsDTO;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckDTO;
import com.propertyvista.crm.rpc.services.admin.ac.CrmAdministrationPolicesFinancialAccess;
import com.propertyvista.crm.rpc.services.admin.ac.CrmAdministrationPolicesOtherAccess;
import com.propertyvista.crm.rpc.services.admin.ac.CrmContentManagementAccess;
import com.propertyvista.crm.rpc.services.admin.ac.GlobalTenantSecurity;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.GlCodeCategory;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.reports.AvailableCrmReport;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.dto.AuditRecordDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class NavigAdministrationViewImpl extends Composite implements NavigAdministrationView {

    private static final I18n i18n = I18n.get(NavigAdministrationViewImpl.class);

    private NavigAdministrationPresenter presenter;

    private final SideMenu menu;

    private final SideMenuItem userMenuItem;

    private final SideMenuItem exitAdminMenuItem;

    private SideMenuList languagesMenuList;

    private LayoutType layoutType;

    public NavigAdministrationViewImpl() {

        SideMenuList root = new SideMenuList();
        menu = new SideMenu(root);
        initWidget(menu);

        setStyleName(SiteViewTheme.StyleName.SiteViewSideMenu.name());

        setHeight("100%");

        SideMenuList sideMenuList;

        {//User
            sideMenuList = new SideMenuList();
            root.addMenuItem(userMenuItem = new SideMenuFolderItem(sideMenuList, "User", CrmImages.INSTANCE.userIcon()));

            sideMenuList.addMenuItem(exitAdminMenuItem = new SideMenuItem(new SideMenuCommand() {

                @Override
                public boolean execute() {
                    AppSite.getPlaceController().goTo(CrmSite.getDefaultPlace());
                    return true;
                }
            }, i18n.tr("Exit Administration"), null));

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

        {//Profile
            sideMenuList = new SideMenuList();
            root.addMenuItem(new SideMenuFolderItem(sideMenuList, i18n.tr("Profile"), null));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Profile.CompanyInfo().formViewerPlace(new Key(-1)),
                    DataModelPermission.permissionRead(PmcCompanyInfoDTO.class)));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Profile.PaymentMethods().formViewerPlace(new Key(-1)),
                    DataModelPermission.permissionRead(PmcPaymentMethodsDTO.class)));
        }

        {//Settings
            sideMenuList = new SideMenuList();
            root.addMenuItem(new SideMenuFolderItem(sideMenuList, i18n.tr("Settings"), null));
            if (VistaTODO.ENABLE_ONBOARDING_WIZARDS_IN_DEVELOPMENT && ApplicationMode.isDevelopment()) {
                sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new Settings.OnlinePaymentSetup()));
                sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new Settings.CreditCheck()));
                sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new Settings.CreditCheck.Setup()));
                sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new Settings.CreditCheck.Status().formViewerPlace(new Key(-1))));
            }
            if (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS) {
                sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new Settings.ILSConfig()));
            }
        }

        {//Security
            sideMenuList = new SideMenuList();
            root.addMenuItem(new SideMenuFolderItem(sideMenuList, i18n.tr("Security"), null));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Security.AuditRecords(), DataModelPermission
                    .permissionRead(AuditRecordDTO.class)));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new Security.UserRole(), DataModelPermission.permissionRead(CrmRole.class)));

            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Settings.MessageCategory(), DataModelPermission
                    .permissionRead(MessageCategory.class)));

            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Security.AssignReports(), DataModelPermission
                    .permissionRead(AvailableCrmReport.class)));

            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Security.TenantSecurity(), GlobalTenantSecurity.class));
        }

        {//Financial
            sideMenuList = new SideMenuList();
            root.addMenuItem(new SideMenuFolderItem(sideMenuList, i18n.tr("Financial"), null));

            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Financial.ARCode(), DataModelPermission
                    .permissionRead(ARCode.class)));
            if (!VistaFeatures.instance().yardiIntegration()) {
                sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Financial.GlCodeCategory(), DataModelPermission
                        .permissionRead(GlCodeCategory.class)));
                sideMenuList
                        .addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Financial.Tax(), DataModelPermission.permissionRead(Tax.class)));
            }
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Financial.MerchantAccount(), DataModelPermission
                    .permissionRead(MerchantAccount.class)));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new Financial.CustomerCreditCheck(), DataModelPermission
                    .permissionRead(CustomerCreditCheckDTO.class)));
        }

        {//Content Management
            sideMenuList = new SideMenuList();
            root.addMenuItem(new SideMenuFolderItem(sideMenuList, i18n.tr("Content Management"), null));

            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new ContentManagement.General(), CrmContentManagementAccess.class));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new ContentManagement.Website(), CrmContentManagementAccess.class));
            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new ContentManagement.Portal(), CrmContentManagementAccess.class));
        }

        {//Policies
            sideMenuList = new SideMenuList();
            root.addMenuItem(new SideMenuFolderItem(sideMenuList, i18n.tr("Policies"), null));

            SideMenuList applicationLeasingList = new SideMenuList();
            sideMenuList.addMenuItem(new SideMenuFolderItem(applicationLeasingList, i18n.tr("Application/Leasing"), null));

            applicationLeasingList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.ApplicationApprovalChecklist(),
                    CrmAdministrationPolicesOtherAccess.class));
            if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
                applicationLeasingList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.BackgroundCheck(),
                        CrmAdministrationPolicesOtherAccess.class));
            }
            applicationLeasingList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.ApplicationDocumentation(),
                    CrmAdministrationPolicesOtherAccess.class));
            applicationLeasingList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.AgreementLegalTerms(),
                    CrmAdministrationPolicesOtherAccess.class));
            applicationLeasingList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.LeaseApplicationTerms(),
                    CrmAdministrationPolicesOtherAccess.class));
            applicationLeasingList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.ProspectPortal(),
                    CrmAdministrationPolicesOtherAccess.class));
            applicationLeasingList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.LegalQuestions(),
                    CrmAdministrationPolicesOtherAccess.class));
            applicationLeasingList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.Restrictions(),
                    CrmAdministrationPolicesOtherAccess.class));

            SideMenuList templatesList = new SideMenuList();
            sideMenuList.addMenuItem(new SideMenuFolderItem(templatesList, i18n.tr("Templates"), null));

            templatesList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.EmailTemplates(),
                    CrmAdministrationPolicesOtherAccess.class));

            SideMenuList financeList = new SideMenuList();
            sideMenuList.addMenuItem(new SideMenuFolderItem(financeList, i18n.tr("Finance"), null));

            if (!VistaFeatures.instance().yardiIntegration()) {
                financeList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.AR(), CrmAdministrationPolicesFinancialAccess.class));
            }
            financeList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.AutoPay(), CrmAdministrationPolicesFinancialAccess.class));
            financeList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.Billing(), CrmAdministrationPolicesFinancialAccess.class));
            financeList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.Deposits(), CrmAdministrationPolicesFinancialAccess.class));
            if (!VistaFeatures.instance().yardiIntegration()) {
                financeList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.LeaseAdjustment(),
                        CrmAdministrationPolicesFinancialAccess.class));
            }
            if (!VistaFeatures.instance().yardiIntegration()) {
                financeList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.ProductTax(),
                        CrmAdministrationPolicesFinancialAccess.class));
            }

            SideMenuList legalOntarioList = new SideMenuList();
            sideMenuList.addMenuItem(new SideMenuFolderItem(legalOntarioList, i18n.tr("Legal - Ontario"), null));

            legalOntarioList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.N4(), CrmAdministrationPolicesOtherAccess.class));

            sideMenuList
                    .addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.EvictionFlow(), CrmAdministrationPolicesOtherAccess.class));

            SideMenuList systemSettingsList = new SideMenuList();
            sideMenuList.addMenuItem(new SideMenuFolderItem(systemSettingsList, i18n.tr("System Settings"), null));

            systemSettingsList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.Dates(), CrmAdministrationPolicesOtherAccess.class));
            systemSettingsList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.IdAssignment(),
                    CrmAdministrationPolicesOtherAccess.class));
            // TODO VISTA-2187       list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Settings.Policies.LeaseTermination());

            SideMenuList portalsList = new SideMenuList();
            sideMenuList.addMenuItem(new SideMenuFolderItem(portalsList, i18n.tr("Portals"), null));

            portalsList
                    .addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.ResidentPortal(), CrmAdministrationPolicesOtherAccess.class));
            portalsList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.MaintenanceRequest(),
                    CrmAdministrationPolicesOtherAccess.class));
            portalsList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.LegalDocumentation(),
                    CrmAdministrationPolicesOtherAccess.class));
            //          list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Settings.Policies.Pet(), CrmAdministrationPolicesOtherAccess.class));

            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.PaymentTypeSelection(),
                    CrmAdministrationPolicesFinancialAccess.class));

            sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.TenantInsurance(),
                    CrmAdministrationPolicesOtherAccess.class));

            if (VistaFeatures.instance().yardiIntegration()) {
                sideMenuList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.YardiInterface(),
                        CrmAdministrationPolicesOtherAccess.class));
            }
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
    public void setPresenter(final NavigAdministrationPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void select(AppPlace appPlace) {
        menu.select(appPlace);
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
            exitAdminMenuItem.setVisible(true);
            break;
        default:
            userMenuItem.setVisible(false);
            exitAdminMenuItem.setVisible(false);
            break;
        }

    }
}

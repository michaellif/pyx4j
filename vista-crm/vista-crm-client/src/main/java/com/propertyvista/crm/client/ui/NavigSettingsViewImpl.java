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

import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.sidemenu.SideMenuAppPlaceItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuItem;
import com.pyx4j.site.client.ui.sidemenu.SideMenuList;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.ContentManagement;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Financial;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Security;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Settings;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class NavigSettingsViewImpl extends ScrollPanel implements NavigSettingsView {

    private static final I18n i18n = I18n.get(NavigSettingsViewImpl.class);

    private final SideMenuList root;

    private NavigSettingsPresenter presenter;

    public NavigSettingsViewImpl() {
        setStyleName(SiteViewTheme.StyleName.SiteViewSideMenu.name());

        setHeight("100%");

        root = new SideMenuList();

        {//Profile
            if (SecurityController.checkAnyBehavior(VistaCrmBehavior.PropertyVistaAccountOwner, VistaCrmBehavior.PropertyVistaSupport)) {
                SideMenuList list = new SideMenuList();
                root.addMenuItem(new SideMenuItem(list, i18n.tr("Profile"), null));
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Profile.CompanyInfo().formViewerPlace(new Key(-1)), null));
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Profile.PaymentMethods().formViewerPlace(new Key(-1)), null));
            }
        }

        {//Profile
            if (SecurityController.checkAnyBehavior(VistaCrmBehavior.PropertyVistaAccountOwner, VistaCrmBehavior.PropertyVistaSupport)) {
                if (VistaTODO.ENABLE_ONBOARDING_WIZARDS_IN_DEVELOPMENT && ApplicationMode.isDevelopment()) {
                    SideMenuList list = new SideMenuList();
                    root.addMenuItem(new SideMenuItem(list, i18n.tr("Settings"), null));
                    list.addMenuItem(new SideMenuAppPlaceItem(new Settings.OnlinePaymentSetup(), null));
                    list.addMenuItem(new SideMenuAppPlaceItem(new Settings.CreditCheck(), null));

                    if (ApplicationMode.isDevelopment()) {
                        list.addMenuItem(new SideMenuAppPlaceItem(new Settings.CreditCheck.Setup(), null));
                        list.addMenuItem(new SideMenuAppPlaceItem(new Settings.CreditCheck.Status().formViewerPlace(new Key(-1)), null));
                    }
                    list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Settings.CommunicationSettings(), null));

                    if (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS) {
                        list.addMenuItem(new SideMenuAppPlaceItem(new Settings.ILSConfig(), null));
                    }
                }
            }
        }

        {//Security
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Security"), null));
            if (SecurityController.checkBehavior(VistaCrmBehavior.Organization)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Security.AuditRecords(), null));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new Security.UserRole(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Security.TenantSecurity(), null));
        }

        {//Financial
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Financial"), null));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Financial.ARCode(), null));
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Financial.GlCodeCategory(), null));
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Financial.Tax(), null));
            }
            if (SecurityController.checkAnyBehavior(VistaCrmBehavior.OrganizationFinancial, VistaCrmBehavior.PropertyVistaAccountOwner)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Financial.MerchantAccount(), null));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new Financial.CustomerCreditCheck(), null));
        }

        {//Content Management
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Content Management"), null));

            list.addMenuItem(new SideMenuAppPlaceItem(new ContentManagement.General(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new ContentManagement.Website(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new ContentManagement.Portal(), null));
        }

        {//Policies
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Policies"), null));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.ApplicationDocumentation(), null));
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.AR(), null));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.AutoPay(), null));
            if (!VistaTODO.Equifax_Off_VISTA_478 && VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.BackgroundCheck(), null));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.Billing(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.Dates(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.Deposits(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.EmailTemplates(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.IdAssignment(), null));
            // TODO VISTA-2187       list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Settings.Policies.LeaseTermination());
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.LeaseAdjustment(), null));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.AgreementLegalTerms(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.LeaseApplicationTerms(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.LegalDocumentation(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.MaintenanceRequest(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.N4(), null));
//          list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Settings.Policies.Pet(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.PaymentTypeSelection(), null));
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.ProductTax(), null));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.ProspectPortal(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.Restrictions(), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.TenantInsurance(), null));
            if (VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.YardiInterface(), null));
            }
        }

        add(root.asWidget());

    }

    @Override
    public void setPresenter(NavigSettingsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void select(AppPlace appPlace) {
        root.select(appPlace);
        SideMenuItem selected = root.getSelected();
        if (selected != null) {
            ensureVisible(selected.asWidget());
        }
    }

}

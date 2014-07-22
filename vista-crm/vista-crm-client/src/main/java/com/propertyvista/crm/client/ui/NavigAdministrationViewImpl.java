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

import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.sidemenu.SideMenu;
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
import com.propertyvista.crm.rpc.dto.admin.PmcCompanyInfoDTO;
import com.propertyvista.crm.rpc.dto.admin.PmcPaymentMethodsDTO;
import com.propertyvista.crm.rpc.services.admin.ac.GlobalTenantSecurity;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.AuditRecordDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class NavigAdministrationViewImpl extends Composite implements NavigAdministrationView {

    private static final I18n i18n = I18n.get(NavigAdministrationViewImpl.class);

    private final SideMenu menu;

    public NavigAdministrationViewImpl() {

        SideMenuList root = new SideMenuList();
        menu = new SideMenu(root);
        initWidget(menu);

        setStyleName(SiteViewTheme.StyleName.SiteViewSideMenu.name());

        setHeight("100%");

        {//Profile
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Profile"), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Profile.CompanyInfo().formViewerPlace(new Key(-1)), DataModelPermission
                    .permissionRead(PmcCompanyInfoDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Profile.PaymentMethods().formViewerPlace(new Key(-1)), DataModelPermission
                    .permissionRead(PmcPaymentMethodsDTO.class)));
        }

        {//Settings
            if (SecurityController.check(VistaCrmBehavior.PropertyVistaAccountOwner_OLD, VistaCrmBehavior.PropertyVistaSupport)) {
                if (VistaTODO.ENABLE_ONBOARDING_WIZARDS_IN_DEVELOPMENT && ApplicationMode.isDevelopment()) {
                    SideMenuList list = new SideMenuList();
                    root.addMenuItem(new SideMenuItem(list, i18n.tr("Settings"), null));
                    list.addMenuItem(new SideMenuAppPlaceItem(new Settings.OnlinePaymentSetup()));
                    list.addMenuItem(new SideMenuAppPlaceItem(new Settings.CreditCheck()));

                    if (ApplicationMode.isDevelopment()) {
                        list.addMenuItem(new SideMenuAppPlaceItem(new Settings.CreditCheck.Setup()));
                        list.addMenuItem(new SideMenuAppPlaceItem(new Settings.CreditCheck.Status().formViewerPlace(new Key(-1))));
                    }
                    list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Settings.CommunicationSettings()));

                    if (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS) {
                        list.addMenuItem(new SideMenuAppPlaceItem(new Settings.ILSConfig()));
                    }
                }
            }
        }

        {//Security
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Security"), null));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Security.AuditRecords(), DataModelPermission
                    .permissionRead(AuditRecordDTO.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new Security.UserRole(), DataModelPermission.permissionRead(CrmRole.class)));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Security.TenantSecurity(), GlobalTenantSecurity.class));
        }

        {//Financial
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Financial"), null));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Financial.ARCode()));
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Financial.GlCodeCategory()));
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Financial.Tax()));
            }
            if (SecurityController.check(VistaCrmBehavior.OrganizationFinancial_OLD, VistaCrmBehavior.PropertyVistaAccountOwner_OLD)) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Financial.MerchantAccount()));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new Financial.CustomerCreditCheck()));
        }

        {//Content Management
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Content Management"), null));

            list.addMenuItem(new SideMenuAppPlaceItem(new ContentManagement.General()));
            list.addMenuItem(new SideMenuAppPlaceItem(new ContentManagement.Website()));
            list.addMenuItem(new SideMenuAppPlaceItem(new ContentManagement.Portal()));
        }

        {//Policies
            SideMenuList list = new SideMenuList();
            root.addMenuItem(new SideMenuItem(list, i18n.tr("Policies"), null));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.ApplicationDocumentation()));
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.AR()));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.AutoPay()));
            if (!VistaTODO.Equifax_Off_VISTA_478 && VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.BackgroundCheck()));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.Billing()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.Dates()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.Deposits()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.EmailTemplates()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.IdAssignment()));
            // TODO VISTA-2187       list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Settings.Policies.LeaseTermination());
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.LeaseAdjustment()));
            }

            SideMenuList legalList = new SideMenuList();
            list.addMenuItem(new SideMenuItem(legalList, i18n.tr("Legal"), null));

            legalList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.AgreementLegalTerms()));
            legalList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.LeaseApplicationTerms()));
            legalList.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.LegalDocumentation()));

            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.MaintenanceRequest()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.N4()));
//          list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Settings.Policies.Pet()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.PaymentTypeSelection()));
            if (!VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.ProductTax()));
            }
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.ProspectPortal()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.Restrictions()));
            list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.TenantInsurance()));
            if (VistaFeatures.instance().yardiIntegration()) {
                list.addMenuItem(new SideMenuAppPlaceItem(new CrmSiteMap.Administration.Policies.YardiInterface()));
            }
        }

    }

    @Override
    public void select(AppPlace appPlace) {
        menu.select(appPlace);
    }

}

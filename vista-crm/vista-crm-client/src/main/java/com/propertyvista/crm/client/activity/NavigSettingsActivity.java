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
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.NavigView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Security;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Settings;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Website;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class NavigSettingsActivity extends AbstractActivity implements NavigView.MainNavigPresenter {
    private static final I18n i18n = I18n.get(NavigSettingsActivity.class);

    private final NavigView view;

    public NavigSettingsActivity(Place place) {
        view = CrmSite.getViewFactory().instantiate(NavigView.class);
        view.setPresenter(this);
        withPlace(place);
    }

    public NavigSettingsActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setNavigFolders(createNavigFolders());
        panel.setWidget(view);
    }

    public List<NavigFolder> createNavigFolders() {
        ArrayList<NavigFolder> list = new ArrayList<NavigFolder>();

        NavigFolder folder = null;

        folder = new NavigFolder(i18n.tr("Profile"));
        if (SecurityController.checkBehavior(VistaCrmBehavior.PropertyVistaAccountOwner)) {
            folder.addNavigItem(new CrmSiteMap.Administration.Profile.PaymentMethods().formViewerPlace(new Key(-1)));
        }
        list.add(folder);

        if (SecurityController.checkBehavior(VistaCrmBehavior.PropertyVistaAccountOwner)) {
            if (VistaTODO.ENABLE_ONBOARDING_WIZARDS) {
                folder = new NavigFolder(i18n.tr("Settings"));
                folder.addNavigItem(new Settings.OnlinePaymentSetup());
                folder.addNavigItem(new Settings.CreditCheck());
                if (ApplicationMode.isDevelopment()) {
                    folder.addNavigItem(new Settings.CreditCheck.Setup());
                    folder.addNavigItem(new Settings.CreditCheck.Status().formViewerPlace(new Key(-1)));
                }
                list.add(folder);
            }
        }

        folder = new NavigFolder(i18n.tr("Security"));
        if (SecurityController.checkBehavior(VistaCrmBehavior.Organization)) {
            folder.addNavigItem(new CrmSiteMap.Administration.Security.AuditRecords());
        }
        folder.addNavigItem(new Security.UserRole());
        folder.addNavigItem(new CrmSiteMap.Administration.Security.TenantSecurity());
        list.add(folder);

        folder = new NavigFolder(i18n.tr("Financial"));
        folder.addNavigItem(new CrmSiteMap.Administration.Financial.ARCode());
        if (!VistaFeatures.instance().yardiIntegration()) {
            folder.addNavigItem(new CrmSiteMap.Administration.Financial.GlCodeCategory());
            folder.addNavigItem(new CrmSiteMap.Administration.Financial.Tax());
        }
        if (SecurityController.checkBehavior(VistaCrmBehavior.PropertyVistaAccountOwner)) {
            folder.addNavigItem(new CrmSiteMap.Administration.Financial.MerchantAccount());
        }
        list.add(folder);

        folder = new NavigFolder(i18n.tr("Website"));
        folder.addNavigItem(new Website.General());
        folder.addNavigItem(new Website.Content());
        folder.addNavigItem(new Website.Branding());
        list.add(folder);

        folder = new NavigFolder(i18n.tr("Policies"));
        if (!VistaFeatures.instance().yardiIntegration()) {
            folder.addNavigItem(new CrmSiteMap.Administration.Policies.ApplicationDocumentation());
        }
        if (!VistaFeatures.instance().yardiIntegration()) {
            folder.addNavigItem(new CrmSiteMap.Administration.Policies.AR());
        }
        folder.addNavigItem(new CrmSiteMap.Administration.Policies.AutoPay());
        if (!VistaTODO.Equifax_Off_VISTA_478 && VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            folder.addNavigItem(new CrmSiteMap.Administration.Policies.BackgroundCheck());
        }
        folder.addNavigItem(new CrmSiteMap.Administration.Policies.Billing());
        folder.addNavigItem(new CrmSiteMap.Administration.Policies.Dates());
        if (!VistaFeatures.instance().yardiIntegration()) {
            folder.addNavigItem(new CrmSiteMap.Administration.Policies.Deposits());
        }
        folder.addNavigItem(new CrmSiteMap.Administration.Policies.EmailTemplates());
        folder.addNavigItem(new CrmSiteMap.Administration.Policies.IdAssignment());
// TODO VISTA-2187       folder.addNavigItem(new CrmSiteMap.Settings.Policies.LeaseTermination());
        if (!VistaFeatures.instance().yardiIntegration()) {
            folder.addNavigItem(new CrmSiteMap.Administration.Policies.LeaseAdjustment());
        }
        folder.addNavigItem(new CrmSiteMap.Administration.Policies.LegalDocumentation());
//      folder.addNavigItem(new CrmSiteMap.Settings.Policies.Pet());
        folder.addNavigItem(new CrmSiteMap.Administration.Policies.PaymentTypeSelection());
        if (!VistaFeatures.instance().yardiIntegration()) {
            folder.addNavigItem(new CrmSiteMap.Administration.Policies.ProductTax());
        }
        if (!VistaFeatures.instance().yardiIntegration()) {
            folder.addNavigItem(new CrmSiteMap.Administration.Policies.Restrictions());
        }
        folder.addNavigItem(new CrmSiteMap.Administration.Policies.TenantInsurance());
        if (VistaFeatures.instance().yardiIntegration()) {
            folder.addNavigItem(new CrmSiteMap.Administration.Policies.YardiInterface());
        }
        folder.addNavigItem(new CrmSiteMap.Administration.Policies.ILSMarketing());

        list.add(folder);

        return list;
    }
}

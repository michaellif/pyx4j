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

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.NavigView;
import com.propertyvista.crm.client.ui.viewfactories.CrmVeiwFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class NavigSettingsActivity extends AbstractActivity implements NavigView.MainNavigPresenter {
    private static final I18n i18n = I18n.get(NavigSettingsActivity.class);

    private final NavigView view;

    public NavigSettingsActivity(Place place) {
        view = CrmVeiwFactory.instance(NavigView.class);
        assert (view != null);
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

        NavigFolder folder = new NavigFolder(i18n.tr("Settings"));
        folder.addNavigItem(new CrmSiteMap.Settings.UserRole());
        folder.addNavigItem(new CrmSiteMap.Settings.MerchantAccount());
        list.add(folder);

        folder = new NavigFolder(i18n.tr("Financial"));
        folder.addNavigItem(new CrmSiteMap.Settings.ProductDictionary());
        folder.addNavigItem(new CrmSiteMap.Settings.Tax());
        folder.addNavigItem(new CrmSiteMap.Settings.GlCodeCategory());
        folder.addNavigItem(new CrmSiteMap.Settings.LeaseAdjustmentReason());
        list.add(folder);

        folder = new NavigFolder(i18n.tr("Portal"));
        folder.addNavigItem(new CrmSiteMap.Settings.Content());
        list.add(folder);

        folder = new NavigFolder(i18n.tr("Policies"));
        folder.addNavigItem(new CrmSiteMap.Settings.Policies.AR());
        folder.addNavigItem(new CrmSiteMap.Settings.Policies.ApplicationDocumentation());
// VISTA-1627 CRM:Delete: Background Check policy as currently not in use  
//        folder.addNavigItem(new CrmSiteMap.Settings.Policies.BackgroundCheck());
        folder.addNavigItem(new CrmSiteMap.Settings.Policies.Deposits());
        folder.addNavigItem(new CrmSiteMap.Settings.Policies.EmailTemplates());
        folder.addNavigItem(new CrmSiteMap.Settings.Policies.IdAssignment());
        folder.addNavigItem(new CrmSiteMap.Settings.Policies.LeaseAdjustmentPolicy());
        folder.addNavigItem(new CrmSiteMap.Settings.Policies.LeaseBilling());
        folder.addNavigItem(new CrmSiteMap.Settings.Policies.LeaseTerms());
        folder.addNavigItem(new CrmSiteMap.Settings.Policies.Dates());
        folder.addNavigItem(new CrmSiteMap.Settings.Policies.Restrictions());
//      folder.addNavigItem(new CrmSiteMap.Settings.Policies.PetPolicy());
        folder.addNavigItem(new CrmSiteMap.Settings.Policies.ProductTax());
        list.add(folder);

        return list;
    }
}

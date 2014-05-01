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
package com.propertyvista.crm.client.ui;

import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.RootPane;
import com.pyx4j.site.client.ui.layout.RiaLayoutPanel;
import com.pyx4j.site.shared.meta.PublicPlace;

import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.crm.client.mvp.ContentActivityMapper;
import com.propertyvista.crm.client.mvp.FooterActivityMapper;
import com.propertyvista.crm.client.mvp.HeaderActivityMapper;
import com.propertyvista.crm.client.mvp.NavigActivityMapper;
import com.propertyvista.crm.client.mvp.NotificationsActivityMapper;
import com.propertyvista.crm.client.mvp.ShortCutsActivityMapper;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class CrmRootPane extends RootPane<RiaLayoutPanel> {

    public static final int MENU_WIDTH = 200;

    public static final int HEADER_HEIGHT = 50;

    public static final int NOTIFICATION_HEIGHT = 30;

    public CrmRootPane() {
        super(new RiaLayoutPanel());

        asWidget().setMenuWidth(MENU_WIDTH);
        asWidget().setHeaderHeight(HEADER_HEIGHT);

        asWidget().setStyleName(SiteViewTheme.StyleName.SiteView.name());

        bind(new HeaderActivityMapper(), asWidget().getHeaderDisplay());
        bind(new FooterActivityMapper(), asWidget().getFooterDisplay());
        bind(new NavigActivityMapper(), asWidget().getNavigDisplay());
        bind(new ShortCutsActivityMapper(), asWidget().getShortcutsDisplay());
        bind(new ContentActivityMapper(), asWidget().getContentDisplay());
        bind(new NotificationsActivityMapper(), asWidget().getNotificationsDisplay());
    }

    public void allocateNotificationsSpace(int number) {
        asWidget().setNotificationsHeight(number * NOTIFICATION_HEIGHT);
        asWidget().forceLayout();
    }

    @Override
    protected void onPlaceChange(Place place) {
        if (place instanceof PublicPlace ||

        place instanceof CrmSiteMap.PasswordReset ||

        place instanceof CrmSiteMap.Account.AccountRecoveryOptionsRequired ||

        place instanceof CrmSiteMap.RuntimeError) {
            asWidget().setMenuVisible(false);
        } else {
            asWidget().setMenuVisible(true);
        }

        asWidget().forceLayout();
    }

}

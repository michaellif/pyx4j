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
 */
package com.propertyvista.crm.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.place.shared.Place;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.RootPane;
import com.pyx4j.site.client.backoffice.ui.layout.BackOfficeLayoutPanel;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;
import com.pyx4j.site.shared.meta.PublicPlace;

import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.crm.client.mvp.CommunicationActivityMapper;
import com.propertyvista.crm.client.mvp.ContentActivityMapper;
import com.propertyvista.crm.client.mvp.FooterActivityMapper;
import com.propertyvista.crm.client.mvp.HeaderActivityMapper;
import com.propertyvista.crm.client.mvp.HelpActivityMapper;
import com.propertyvista.crm.client.mvp.NavigActivityMapper;
import com.propertyvista.crm.client.mvp.NotificationsActivityMapper;
import com.propertyvista.crm.client.mvp.ShortCutsActivityMapper;
import com.propertyvista.crm.client.mvp.SiteFeedbackMapper;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class CrmRootPane extends RootPane<BackOfficeLayoutPanel> {

    private static final I18n i18n = I18n.get(CrmRootPane.class);

    public static final int HEADER_HEIGHT = 50;

    public static final int NOTIFICATION_HEIGHT = 40;

    public CrmRootPane() {
        super(new BackOfficeLayoutPanel(i18n.tr("Shortcuts"), i18n.tr("Help"), i18n.tr("Feedback")));

        asWidget().setHeaderHeight(HEADER_HEIGHT);

        asWidget().setStyleName(SiteViewTheme.StyleName.SiteView.name());

        bind(new HeaderActivityMapper(), asWidget().getDisplay(DisplayType.header));
        bind(new FooterActivityMapper(), asWidget().getDisplay(DisplayType.footer));
        bind(new NavigActivityMapper(), asWidget().getDisplay(DisplayType.menu));
        bind(new CommunicationActivityMapper(), asWidget().getDisplay(DisplayType.communication));
        bind(new ShortCutsActivityMapper(), asWidget().getDisplay(DisplayType.extra1));
        bind(new HelpActivityMapper(), asWidget().getDisplay(DisplayType.extra2));
        bind(new SiteFeedbackMapper(), asWidget().getDisplay(DisplayType.extra4));
        bind(new ContentActivityMapper(), asWidget().getDisplay(DisplayType.content));
        bind(new NotificationsActivityMapper(), asWidget().getDisplay(DisplayType.notification));
    }

    public void allocateNotificationsSpace(int number) {
        asWidget().setNotificationsHeight(number * NOTIFICATION_HEIGHT);
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

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                asWidget().forceLayout(0);
            }
        });

    }

}

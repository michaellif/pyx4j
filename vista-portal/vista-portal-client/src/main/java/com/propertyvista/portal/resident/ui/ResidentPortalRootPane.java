/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 */
package com.propertyvista.portal.resident.ui;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;

import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.resident.mvp.CommunicationActivityMapper;
import com.propertyvista.portal.resident.mvp.CommunityEventsActivityMapper;
import com.propertyvista.portal.resident.mvp.ContentActivityMapper;
import com.propertyvista.portal.resident.mvp.MenuActivityMapper;
import com.propertyvista.portal.resident.mvp.QuickTipActivityMapper;
import com.propertyvista.portal.resident.mvp.SiteFeedbackMapper;
import com.propertyvista.portal.resident.mvp.ToolbarActivityMapper;
import com.propertyvista.portal.resident.mvp.WeatherActivityMapper;
import com.propertyvista.portal.shared.mvp.DevConsoleActivityMapper;
import com.propertyvista.portal.shared.mvp.FooterActivityMapper;
import com.propertyvista.portal.shared.mvp.HeaderActivityMapper;
import com.propertyvista.portal.shared.mvp.NotificationActivityMapper;
import com.propertyvista.portal.shared.ui.PortalRootPane;

public class ResidentPortalRootPane extends PortalRootPane {

    private static final I18n i18n = I18n.get(ResidentPortalRootPane.class);

    public ResidentPortalRootPane() {
        super(i18n.tr("Community Events"), i18n.tr("Quick Tip"), i18n.tr("Feedback"));

        bind(new HeaderActivityMapper(), asWidget().getDisplay(DisplayType.header));
        bind(new ToolbarActivityMapper(), asWidget().getDisplay(DisplayType.toolbar));
        bind(new MenuActivityMapper(), asWidget().getDisplay(DisplayType.menu));
        bind(new CommunicationActivityMapper(), asWidget().getDisplay(DisplayType.communication));
        bind(new ContentActivityMapper(), asWidget().getDisplay(DisplayType.content));
        bind(new FooterActivityMapper(), asWidget().getDisplay(DisplayType.footer));
        bind(new CommunityEventsActivityMapper(), asWidget().getDisplay(DisplayType.extra1));
        bind(new QuickTipActivityMapper(), asWidget().getDisplay(DisplayType.extra2));
        if (VistaTODO.VISTA_6054_OpenWeatherImplmentedProperly) {
            bind(new WeatherActivityMapper(), asWidget().getDisplay(DisplayType.extra3));
        }
        bind(new SiteFeedbackMapper(), asWidget().getDisplay(DisplayType.extra4));
        bind(new NotificationActivityMapper(), asWidget().getDisplay(DisplayType.notification));
        bind(new DevConsoleActivityMapper(), asWidget().getDisplay(DisplayType.devConsole));
    }

}

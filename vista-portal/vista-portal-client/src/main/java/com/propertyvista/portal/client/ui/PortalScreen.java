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
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.AppActivityManager;
import com.pyx4j.site.client.activity.AppActivityMapper;

import com.propertyvista.portal.client.mvp.CaptionActivityMapper;
import com.propertyvista.portal.client.mvp.ContentActivityMapper;
import com.propertyvista.portal.client.mvp.NavigActivityMapper;

public class PortalScreen extends SimplePanel {

    public static String DEFAULT_STYLE_PREFIX = "PortalView";

    public static enum StyleSuffix implements IStyleName {
        Display, StaticContent
    }

    public PortalScreen() {

        EventBus eventBus = AppSite.getEventBus();

        String prefix = DEFAULT_STYLE_PREFIX;

        setStyleName(prefix);

        SimplePanel mainWrap = new SimplePanel();
        mainWrap.setStyleName("vista-pmsite-mainWrap");
        setWidget(mainWrap);

        FlowPanel sidebarWrap = new FlowPanel();
        sidebarWrap.setStyleName("vista-pmsite-sidebarWrap");
        mainWrap.setWidget(sidebarWrap);

        SimplePanel sidebar = new SimplePanel();
        sidebar.setStyleName("vista-pmsite-sidebar");
        sidebarWrap.add(sidebar);

        FlowPanel main = new FlowPanel();
        main.setStyleName("vista-pmsite-main");
        sidebarWrap.add(main);

        DisplayPanel navigDisplayPanel = new DisplayPanel();
        navigDisplayPanel.setStyleName("secondaryNavig");
        sidebar.setWidget(navigDisplayPanel);

        DisplayPanel captionDisplayPanel = new DisplayPanel();
        captionDisplayPanel.setStyleName("caption");
        main.add(captionDisplayPanel);

        DisplayPanel contentDisplayPanel = new DisplayPanel();
        contentDisplayPanel.setStyleName("content");
        main.add(contentDisplayPanel);

        bind(new NavigActivityMapper(), navigDisplayPanel, eventBus);
        bind(new CaptionActivityMapper(), captionDisplayPanel, eventBus);
        bind(new ContentActivityMapper(), contentDisplayPanel, eventBus);

    }

    private static void bind(AppActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus) {
        AppActivityManager activityManager = new AppActivityManager(mapper, eventBus);
        activityManager.setDisplay(widget);
    }

    class DisplayPanel extends SimplePanel {
        DisplayPanel() {
            String prefix = DEFAULT_STYLE_PREFIX;
            setStyleName(prefix + StyleSuffix.Display);

        }
    }

}

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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.AppActivityManager;
import com.pyx4j.site.client.activity.AppActivityMapper;

import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.mvp.CaptionActivityMapper;
import com.propertyvista.portal.client.mvp.ContentActivityMapper;
import com.propertyvista.portal.client.mvp.NavigActivityMapper;
import com.propertyvista.portal.client.mvp.TopRightActivityMapper;
import com.propertyvista.portal.client.mvp.UtilityActivityMapper;

public class PortalRootPane extends SimplePanel {

    public static String DEFAULT_STYLE_PREFIX = "PortalView";

    public static enum StyleSuffix implements IStyleName {
        Display, StaticContent
    }

    DisplayPanel navigDisplayPanel;

    UtilityDisplayPanel utilityDisplay;

    DisplayPanel contentDisplayPanel;

    public PortalRootPane() {

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

        navigDisplayPanel = new DisplayPanel();
        navigDisplayPanel.setStyleName("secondaryNavig");
        sidebar.setWidget(navigDisplayPanel);

        FlowPanel main = new FlowPanel();
        main.setStyleName("vista-pmsite-main");
        sidebarWrap.add(main);

        DisplayPanel captionDisplayPanel = new DisplayPanel();
        captionDisplayPanel.setStyleName("caption");
        main.add(captionDisplayPanel);

        contentDisplayPanel = new DisplayPanel();
        contentDisplayPanel.setStyleName("content");
        main.add(contentDisplayPanel);

        //============= Container for login and retrieve password views ===========
        utilityDisplay = new UtilityDisplayPanel();
        utilityDisplay.setStyleName("content");
        utilityDisplay.setVisible(false);
        main.add(utilityDisplay);

        //============= Container for login/logout links on external HTML page ===========
        DisplayPanel topRightDisplay = new DisplayPanel();

        RootPanel topRightRoot = RootPanel.get(PortalSite.TOP_RIGHT_INSERTION_ID);

        if (topRightRoot != null) {
            topRightRoot.getElement().setInnerHTML("");
            topRightRoot.clear();
            topRightRoot.add(topRightDisplay);
        } else {
            throw new UserRuntimeException("Custom HTML page is missing <div>" + PortalSite.TOP_RIGHT_INSERTION_ID);
        }

        EventBus eventBus = AppSite.getEventBus();

        bind(new TopRightActivityMapper(), topRightDisplay, eventBus);
        bind(new UtilityActivityMapper(), utilityDisplay, eventBus);
        bind(new CaptionActivityMapper(), captionDisplayPanel, eventBus);

        bind(new NavigActivityMapper(), navigDisplayPanel, eventBus);
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

    class UtilityDisplayPanel extends SimplePanel {

        UtilityDisplayPanel() {
            String prefix = DEFAULT_STYLE_PREFIX;
            setStyleName(prefix + StyleSuffix.Display);
        }

        @Override
        public void setWidget(IsWidget w) {
            super.setWidget(w);
            if (w != null) {
                navigDisplayPanel.setVisible(false);
                contentDisplayPanel.setVisible(false);
                utilityDisplay.setVisible(true);
            } else {
                navigDisplayPanel.setVisible(true);
                contentDisplayPanel.setVisible(true);
                utilityDisplay.setVisible(false);
            }
        }

    }
}

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
package com.propertyvista.tester.client.ui;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.AppSiteView;
import com.pyx4j.widgets.client.style.Palette;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.Theme;

import com.propertyvista.tester.client.mvp.FooterActivityMapper;
import com.propertyvista.tester.client.mvp.HeaderActionActivityMapper;
import com.propertyvista.tester.client.mvp.LogActivityMapper;
import com.propertyvista.tester.client.mvp.NavigationActivityMapper;
import com.propertyvista.tester.client.mvp.TestAreaActivityMapper;

@Singleton
public class TesterView extends LayoutPanel {

    @Inject
    public TesterView(

    FooterActivityMapper footerActivityMapper,

    HeaderActionActivityMapper headerActionActivityMapper,

    NavigationActivityMapper navigationActivityMapper,

    LogActivityMapper logActivityMapper,

    TestAreaActivityMapper testareaActivityMapper,

    Theme theme, Palette palette) {

        EventBus eventBus = AppSite.getEventBus();

        StyleManger.installTheme(theme, palette);

        String prefix = AppSiteView.DEFAULT_STYLE_PREFIX;

        setStyleName(prefix);

        DockLayoutPanel contentPanel = new DockLayoutPanel(Unit.EM);
        add(contentPanel);

        //============ Header Panel ============

        FlowPanel headerPanel = new FlowPanel();
        contentPanel.addNorth(headerPanel, 5);

        DisplayPanel logoDisplayPanel = new DisplayPanel();
        logoDisplayPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        headerPanel.add(logoDisplayPanel);

        DisplayPanel actionsDisplayPanel = new DisplayPanel();
        actionsDisplayPanel.setWidth("20em");
        actionsDisplayPanel.getElement().getStyle().setFloat(Style.Float.RIGHT);
        headerPanel.add(actionsDisplayPanel);

        bind(headerActionActivityMapper, actionsDisplayPanel, eventBus);
        //============ Footer Panel ============

        DisplayPanel footerDisplayPanel = new DisplayPanel();
        contentPanel.addSouth(footerDisplayPanel, 5);
        bind(footerActivityMapper, footerDisplayPanel, eventBus);

        //============ Main ============

        SplitLayoutPanel mainPanel = new SplitLayoutPanel();

        DisplayPanel navigDisplayPanel = new DisplayPanel();
        mainPanel.addWest(navigDisplayPanel, 250);
        bind(navigationActivityMapper, navigDisplayPanel, eventBus);

        DisplayPanel logPanel = new DisplayPanel();
        mainPanel.addSouth(logPanel, 150);
        bind(logActivityMapper, logPanel, eventBus);

        DisplayPanel testPanel = new DisplayPanel();
        mainPanel.add(testPanel);
        bind(testareaActivityMapper, testPanel, eventBus);

        contentPanel.add(mainPanel);

    }

    private static void bind(ActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus) {
        ActivityManager activityManager = new ActivityManager(mapper, eventBus);
        activityManager.setDisplay(widget);

    }

    class DisplayPanel extends SimplePanel {
        DisplayPanel() {

        }
    }

}

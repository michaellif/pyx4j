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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.propertyvista.tester.client.mvp.StatusActivityMapper;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.AppSiteView;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.Theme;

@Singleton
public class TesterView extends LayoutPanel {

    @Inject
    public TesterView(

    StatusActivityMapper statusActivityMapper,

    Theme theme) {

        EventBus eventBus = AppSite.getEventBus();

        StyleManger.installTheme(theme);

        String prefix = AppSiteView.DEFAULT_STYLE_PREFIX;

        setStyleName(prefix);

        DockLayoutPanel contentPanel = new DockLayoutPanel(Unit.EM);
        add(contentPanel);

        //============ Header Panel ============

        FlowPanel headerPanel = new FlowPanel();
        contentPanel.addNorth(headerPanel, 5);

        DisplayPanel logoDisplayPanel = new DisplayPanel("Logo");
        logoDisplayPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        headerPanel.add(logoDisplayPanel);

        DisplayPanel actionsDisplayPanel = new DisplayPanel("Actions");
        actionsDisplayPanel.setWidth("20em");
        actionsDisplayPanel.getElement().getStyle().setFloat(Style.Float.RIGHT);
        headerPanel.add(actionsDisplayPanel);

        //============ Footer Panel ============

        DisplayPanel footerPanel = new DisplayPanel("Status");
        contentPanel.addSouth(footerPanel, 5);

        //============ Main ============

        SplitLayoutPanel mainPanel = new SplitLayoutPanel();

        DisplayPanel navigDisplayPanel = new DisplayPanel("Navig");
        mainPanel.addWest(navigDisplayPanel, 250);

        DisplayPanel logPanel = new DisplayPanel("Log");
        mainPanel.addSouth(logPanel, 150);

        DisplayPanel testPanel = new DisplayPanel("Test");
        mainPanel.add(testPanel);

        contentPanel.add(mainPanel);

    }

    private static void bind(ActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus) {
        ActivityManager activityManager = new ActivityManager(mapper, eventBus);
        activityManager.setDisplay(widget);

    }

    class DisplayPanel extends HTML {
        DisplayPanel(String html) {
            setText(html);
            String prefix = AppSiteView.DEFAULT_STYLE_PREFIX;

            getElement().getStyle().setColor("red");
            //setStyleName(prefix + StyleSuffix.Display);

        }
    }

}

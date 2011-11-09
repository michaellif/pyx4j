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

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.AppActivityManager;
import com.pyx4j.site.client.activity.AppActivityMapper;

import com.propertyvista.common.client.theme.CrmSitePanelTheme;
import com.propertyvista.crm.client.mvp.FooterActivityMapper;
import com.propertyvista.crm.client.mvp.LoginActivityMapper;
import com.propertyvista.crm.client.mvp.LogoActivityMapper;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.mvp.NavigActivityMapper;
import com.propertyvista.crm.client.mvp.ShortCutsActivityMapper;
import com.propertyvista.crm.client.mvp.TopRightActionsActivityMapper;

public class CrmPanel extends LayoutPanel {

    public CrmPanel() {

        EventBus eventBus = AppSite.getEventBus();

        setStyleName(CrmSitePanelTheme.StyleName.SiteView.name());

        DockLayoutPanel contentPanel = new DockLayoutPanel(Unit.EM);
        contentPanel.setStyleName(CrmSitePanelTheme.StyleName.SiteViewContent.name());
        add(contentPanel);

        //============ Header Panel ============

        FlowPanel headerPanel = new FlowPanel();
        contentPanel.addNorth(headerPanel, 5);
        headerPanel.setStyleName(CrmSitePanelTheme.StyleName.SiteViewHeader.name());

        DisplayPanel logoDisplay = new DisplayPanel();
        //VS should correspond with the logo size
        logoDisplay.setSize("30%", "100%");
        logoDisplay.getElement().getStyle().setFloat(Style.Float.LEFT);
        headerPanel.add(logoDisplay);

        DisplayPanel actionsDisplay = new DisplayPanel();
        //actionsDisplay.setWidth("20em");
        actionsDisplay.setSize("70%", "100%");
        actionsDisplay.getElement().getStyle().setFloat(Style.Float.RIGHT);
        headerPanel.add(actionsDisplay);

        /**
         * Main area of the app has to comprise two containers:
         * Split Panel for main application navigation
         * and entry point screens such as Login and Retrieve password
         * Since the center area of DockLayoutPanel accepts only one element
         * one more panel needs to be introduced
         */
        LayoutPanel centerAreaContent = new LayoutPanel();
        centerAreaContent.ensureDebugId("just_checking");
        contentPanel.add(centerAreaContent);

        //================ Main application area - splitter with navig menu and content ======= 

        SplitLayoutPanel splitPanel = new SplitLayoutPanel(3);
        splitPanel.ensureDebugId("splitPanel");
        splitPanel.setSize("100%", "100%");
        centerAreaContent.add(splitPanel);

        //============= Container for login and retrieve password views ===========
        UtilityDisplayPanel utilityDisplay = new UtilityDisplayPanel(centerAreaContent);
        utilityDisplay.ensureDebugId("entrypointpanel");

        //============ Left Panel ============

        VerticalPanel leftPanel = new VerticalPanel();
        leftPanel.setSize("100%", "100%");

        splitPanel.addWest(leftPanel, 250);
        splitPanel.setWidgetMinSize(leftPanel, 150);

        DisplayPanel navigDisplay = new DisplayPanel();
        leftPanel.add(navigDisplay);
        navigDisplay.setSize("100%", "100%");

        DisplayPanel shortcutsDisplay = new DisplayPanel();
        shortcutsDisplay.setSize("100%", "100%");
        leftPanel.add(shortcutsDisplay);

        // here goes truncated Footer Panel:
        DisplayPanel footerDisplay = new DisplayPanel();
        leftPanel.add(footerDisplay);
        footerDisplay.setSize("100%", "100%");

        // layout:
        leftPanel.setCellWidth(navigDisplay, "100%");
        leftPanel.setCellHeight(navigDisplay, "65%");

        leftPanel.setCellWidth(shortcutsDisplay, "100%");
        leftPanel.setCellHeight(shortcutsDisplay, "35%");

        leftPanel.setCellWidth(footerDisplay, "100%");
        leftPanel.setCellHeight(footerDisplay, "40px");

        leftPanel.setStyleName(CrmSitePanelTheme.StyleName.SiteViewNavigContainer.name());

        //============ Main Panel ============

        DisplayPanel mainDisplay = new DisplayPanel();
        splitPanel.add(mainDisplay);

        // Activity <-> Display bindings:
        bind(new LoginActivityMapper(), utilityDisplay, eventBus);
        bind(new LogoActivityMapper(), logoDisplay, eventBus);
        bind(new TopRightActionsActivityMapper(), actionsDisplay, eventBus);
        bind(new FooterActivityMapper(), footerDisplay, eventBus);
        bind(new NavigActivityMapper(), navigDisplay, eventBus);
        bind(new ShortCutsActivityMapper(), shortcutsDisplay, eventBus);
        bind(new MainActivityMapper(), mainDisplay, eventBus);
    }

    private static void bind(ActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus) {
        ActivityManager activityManager = new ActivityManager(mapper, eventBus);
        activityManager.setDisplay(widget);
    }

    private static void bind(AppActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus) {
        AppActivityManager activityManager = new AppActivityManager(mapper, eventBus);
        activityManager.setDisplay(widget);
    }

    class DisplayPanel extends SimplePanel implements RequiresResize, ProvidesResize {
        DisplayPanel() {
            setStyleName(CrmSitePanelTheme.StyleName.SiteViewDisplay.name());
        }

        @Override
        public void onResize() {
            Widget child = getWidget();
            if ((child != null) && (child instanceof RequiresResize)) {
                ((RequiresResize) child).onResize();
            }
        }
    }

    class UtilityDisplayPanel extends SimplePanel {

        private final LayoutPanel parent;

        UtilityDisplayPanel(LayoutPanel parent) {
            this.parent = parent;
            setStyleName(CrmSitePanelTheme.StyleName.SiteViewDisplay.name());
        }

        @Override
        public void setWidget(IsWidget w) {
            super.setWidget(w);
            if (w == null) {
                removeFromParent();
                for (int i = 0; i < parent.getWidgetCount(); i++) {
                    parent.getWidget(i).setVisible(true);
                }
            } else {
                for (int i = 0; i < parent.getWidgetCount(); i++) {
                    parent.getWidget(i).setVisible(false);
                }
                parent.add(this);
            }
        }

    }
}

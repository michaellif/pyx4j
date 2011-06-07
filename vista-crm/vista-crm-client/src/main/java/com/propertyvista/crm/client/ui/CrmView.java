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
import com.google.gwt.event.shared.EventBus;
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
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.AppSiteView;
import com.pyx4j.widgets.client.style.IStyleSuffix;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.Theme;

import com.propertyvista.crm.client.mvp.ActionsActivityMapper;
import com.propertyvista.crm.client.mvp.EntryPointActivityMapper;
import com.propertyvista.crm.client.mvp.FooterActivityMapper;
import com.propertyvista.crm.client.mvp.LogoActivityMapper;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.mvp.NavigActivityMapper;
import com.propertyvista.crm.client.mvp.ShortCutsActivityMapper;

@Singleton
public class CrmView extends LayoutPanel {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public static enum StyleSuffix implements IStyleSuffix {
        Content, Action, Header, Navigation, Footer, Display, NavigContainer;
    }

    @Inject
    public CrmView(LogoActivityMapper logoActivityMapper,

    ActionsActivityMapper actionsActivityMapper,

    NavigActivityMapper navigActivityMapper,

    MainActivityMapper mainActivityMapper,

    EntryPointActivityMapper utilityActivityMapper,

    FooterActivityMapper footerActivityMapper,

    ShortCutsActivityMapper shortcutsActivityMapper,

    Theme theme) {

        EventBus eventBus = AppSite.getEventBus();

        StyleManger.installTheme(theme);

        setStyleName(AppSiteView.DEFAULT_STYLE_PREFIX);

        DockLayoutPanel contentPanel = new DockLayoutPanel(Unit.EM);
        contentPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Content);
        add(contentPanel);

        //============ Header Panel ============

        FlowPanel headerPanel = new FlowPanel();
        contentPanel.addNorth(headerPanel, 5);
        headerPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Header);

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

        leftPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.NavigContainer);

        //============ Main Panel ============

        DisplayPanel mainDisplay = new DisplayPanel();
        splitPanel.add(mainDisplay);

        // Activity <-> Display bindings:
        bind(logoActivityMapper, logoDisplay, eventBus);
        bind(actionsActivityMapper, actionsDisplay, eventBus);
        bind(footerActivityMapper, footerDisplay, eventBus);
        bind(navigActivityMapper, navigDisplay, eventBus);
        bind(shortcutsActivityMapper, shortcutsDisplay, eventBus);
        bind(mainActivityMapper, mainDisplay, eventBus);
        bind(utilityActivityMapper, utilityDisplay, eventBus);
    }

    private static void bind(ActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus) {
        ActivityManager activityManager = new ActivityManager(mapper, eventBus);
        activityManager.setDisplay(widget);
    }

    class DisplayPanel extends SimplePanel implements RequiresResize, ProvidesResize {
        DisplayPanel() {
            String prefix = AppSiteView.DEFAULT_STYLE_PREFIX;
            setStyleName(prefix + StyleSuffix.Display);
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
            String prefix = AppSiteView.DEFAULT_STYLE_PREFIX;
            setStyleName(prefix + StyleSuffix.Display);
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

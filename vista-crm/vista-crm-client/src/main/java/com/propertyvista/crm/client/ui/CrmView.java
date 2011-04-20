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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.propertyvista.crm.client.mvp.ActionsActivityMapper;
import com.propertyvista.crm.client.mvp.EntryPointActivityMapper;
import com.propertyvista.crm.client.mvp.FooterActivityMapper;
import com.propertyvista.crm.client.mvp.Left1ActivityMapper;
import com.propertyvista.crm.client.mvp.Left2ActivityMapper;
import com.propertyvista.crm.client.mvp.LogoActivityMapper;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.mvp.NavigActivityMapper;
import com.propertyvista.crm.client.mvp.ShortCutsActivityMapper;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.AppSiteView;
import com.pyx4j.widgets.client.style.IStyleSuffix;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.Theme;

@Singleton
public class CrmView extends LayoutPanel {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public static enum StyleSuffix implements IStyleSuffix {
        Action, Header, Navigation, Footer, Display, NavigContainer;
    }

    @Inject
    public CrmView(LogoActivityMapper logoActivityMapper,

    ActionsActivityMapper actionsActivityMapper,

    NavigActivityMapper navigActivityMapper,

    MainActivityMapper mainActivityMapper,

    Left1ActivityMapper left1ActivityMapper,

    Left2ActivityMapper left2ActivityMapper,

    EntryPointActivityMapper utilityActivityMapper,

    FooterActivityMapper footerActivityMapper,

    ShortCutsActivityMapper shortcutsActivityMapper,

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

        //============ Footer Panel ============

        DisplayPanel footerDisplay = new DisplayPanel();
        contentPanel.addSouth(footerDisplay, 3.2);

        /**
         * Main area of the app has to comprise two containers:
         * Split Panel for main application navigation
         * and entry point screens such as Login and Retrieve password
         * Since the center area of DockLayoutPanel accepts only one element
         * one more panel needs to be introduced
         */
        FlowPanel centerAreaContent = new FlowPanel();
        centerAreaContent.ensureDebugId("just_checking");
        contentPanel.add(centerAreaContent);

        //================ Main application area - splitter with navig menu and content ======= 

        SplitLayoutPanel splitPanel = new SplitLayoutPanel();
        splitPanel.ensureDebugId("splitPanel");
        splitPanel.setSize("100%", "100%");
        centerAreaContent.add(splitPanel);

        //============= Container for login and retrieve password views ===========
        UtilityDisplayPanel utilityDisplay = new UtilityDisplayPanel(splitPanel);
        utilityDisplay.ensureDebugId("entrypointpanel");
        centerAreaContent.add(utilityDisplay);

        //============ Left Panel ============

        VerticalPanel leftPanel = new VerticalPanel();
        leftPanel.setSize("100%", "100%");

        splitPanel.addWest(leftPanel, 250);

        DisplayPanel navigDisplay = new DisplayPanel();
        leftPanel.add(navigDisplay);
        navigDisplay.setSize("100%", "100%");

        DisplayPanel left1Display = new DisplayPanel();
        left1Display.setSize("100%", "100%");
        leftPanel.add(left1Display);
        /**
         * VS negatevly affects layout. Uncomment and implement when needed
         */
/*
 * DisplayPanel left2Display = new DisplayPanel();
 * leftPanel.add(left2Display);
 */

        leftPanel.setCellWidth(navigDisplay, "100%");
        leftPanel.setCellHeight(navigDisplay, "65%");

        leftPanel.setCellWidth(left1Display, "100%");
        leftPanel.setCellHeight(left1Display, "35%");
        leftPanel.setSpacing(3);
        leftPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.NavigContainer);

        //============ Main ============

        DisplayPanel mainDisplay = new DisplayPanel();
        splitPanel.add(mainDisplay);

        bind(logoActivityMapper, logoDisplay, eventBus);
        bind(actionsActivityMapper, actionsDisplay, eventBus);
        bind(footerActivityMapper, footerDisplay, eventBus);
        bind(navigActivityMapper, navigDisplay, eventBus);
        bind(shortcutsActivityMapper, left1Display, eventBus);
/*
 * bind(left1ActivityMapper, left1Display, eventBus);
 * bind(left2ActivityMapper, left2Display, eventBus);
 */
        bind(mainActivityMapper, mainDisplay, eventBus);
        bind(utilityActivityMapper, utilityDisplay, eventBus);

    }

    private static void bind(ActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus) {
        ActivityManager activityManager = new ActivityManager(mapper, eventBus);
        activityManager.setDisplay(widget);

    }

    class DisplayPanel extends SimplePanel {
        DisplayPanel() {
            String prefix = AppSiteView.DEFAULT_STYLE_PREFIX;
            setStyleName(prefix + StyleSuffix.Display);
        }
    }

    class UtilityDisplayPanel extends SimplePanel {

        private final Panel panel;

        UtilityDisplayPanel(Panel panel) {
            this.panel = panel;
            String prefix = AppSiteView.DEFAULT_STYLE_PREFIX;
            setStyleName(prefix + StyleSuffix.Display);
        }

        @Override
        public void setWidget(IsWidget w) {
            super.setWidget(w);
            panel.setVisible(w == null);

        }

    }

}

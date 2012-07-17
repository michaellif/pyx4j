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
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import com.pyx4j.commons.Key;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.AppActivityManager;
import com.pyx4j.site.client.activity.AppActivityMapper;

import com.propertyvista.common.client.theme.CrmSitePanelTheme;
import com.propertyvista.crm.client.mvp.FooterActivityMapper;
import com.propertyvista.crm.client.mvp.UtilityActivityMapper;
import com.propertyvista.crm.client.mvp.LogoActivityMapper;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.mvp.NavigActivityMapper;
import com.propertyvista.crm.client.mvp.ShortCutsActivityMapper;
import com.propertyvista.crm.client.mvp.TopRightActionsActivityMapper;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class CrmPanel extends LayoutPanel {

    private final LayoutPanel centerAreaContent;

    public CrmPanel() {

        HTML feedbackWidgetContainer = new HTML();
        feedbackWidgetContainer.getElement().setAttribute("id", "feedback_widget_container"); //getSatisfaction button container
        add(feedbackWidgetContainer); //must be done before add(contentPanel) else the container blocks all interaction with site

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
        centerAreaContent = new LayoutPanel();
        centerAreaContent.ensureDebugId("just_checking");
        contentPanel.add(centerAreaContent);
        centerAreaContent.setVisible(false);

        //================ Main application area - splitter with navig menu and content =======

        SplitLayoutPanel splitPanel = new SplitLayoutPanel(4);
        splitPanel.ensureDebugId("splitPanel");
        splitPanel.setSize("100%", "100%");
        centerAreaContent.add(splitPanel);

        //============= Container for login and retrieve password views ===========
        UtilityDisplayPanel utilityDisplay = new UtilityDisplayPanel(centerAreaContent);
        utilityDisplay.ensureDebugId("entrypointpanel");

        //============ Left Panel ============

        VerticalPanel leftPanel = new VerticalPanel();
        leftPanel.setSize("100%", "100%");

        splitPanel.addWest(leftPanel, 200);
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
        bind(new UtilityActivityMapper(), utilityDisplay, eventBus);
        bind(new LogoActivityMapper(), logoDisplay, eventBus);
        bind(new TopRightActionsActivityMapper(), actionsDisplay, eventBus);
        bind(new FooterActivityMapper(), footerDisplay, eventBus);
        bind(new NavigActivityMapper(), navigDisplay, eventBus, new PlaceChangeEventFilter() {

            private Place previousPlace = null;

            private Key previousUser = null;

            
            @Override
            public boolean isHandlable(PlaceChangeEvent event) {
                if (//@formatter:off
                    (previousPlace == null | previousUser == null )
                        || !previousUser.equals(ClientContext.getUserVisit() != null ? ClientContext.getUserVisit().getPrincipalPrimaryKey() : null) 
                        | (isSettingsPlace(previousPlace) & !isSettingsPlace(event.getNewPlace()))
                        | (!isSettingsPlace(previousPlace) & isSettingsPlace(event.getNewPlace()))
                   )//@formatter:on
                {
                    previousPlace = event.getNewPlace();
                    previousUser = ClientContext.getUserVisit() != null ? ClientContext.getUserVisit().getPrincipalPrimaryKey() : null;
                    return true;
                } else {
                    previousPlace = event.getNewPlace();
                    previousUser = ClientContext.getUserVisit().getPrincipalPrimaryKey();
                    return false;
                }
            }

            private boolean isSettingsPlace(Place place) {
                return place.getClass().getName().contains(CrmSiteMap.Settings.class.getName());
            }
        });
        bind(new ShortCutsActivityMapper(), shortcutsDisplay, eventBus);

        bind(new MainActivityMapper(), mainDisplay, eventBus);
    }

    private static void bind(ActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus, final PlaceChangeEventFilter filter) {
        ActivityManager activityManager;
        if (filter == null) {
            activityManager = new ActivityManager(mapper, eventBus);
        } else {
            activityManager = new ActivityManager(mapper, eventBus) {
                @Override
                public void onPlaceChange(PlaceChangeEvent event) {
                    if (filter.isHandlable(event)) {
                        super.onPlaceChange(event);
                    }
                }
            };
        }
        activityManager.setDisplay(widget);
    }

    private static void bind(ActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus) {
        bind(mapper, widget, eventBus, null);
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

        @Override
        public void setWidget(IsWidget w) {
            super.setWidget(w);
            centerAreaContent.setVisible(true);
        }
    }

    interface PlaceChangeEventFilter {

        boolean isHandlable(PlaceChangeEvent event);

    }

    class UtilityDisplayPanel extends DisplayPanel {

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

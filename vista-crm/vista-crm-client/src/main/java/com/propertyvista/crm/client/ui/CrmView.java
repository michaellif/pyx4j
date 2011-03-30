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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.propertyvista.crm.client.mvp.ActionsActivityMapper;
import com.propertyvista.crm.client.mvp.BottomActivityMapper;
import com.propertyvista.crm.client.mvp.CaptionActivityMapper;
import com.propertyvista.crm.client.mvp.ContentActivityMapper;
import com.propertyvista.crm.client.mvp.Left1ActivityMapper;
import com.propertyvista.crm.client.mvp.Left2ActivityMapper;
import com.propertyvista.crm.client.mvp.LogoActivityMapper;
import com.propertyvista.crm.client.mvp.MainNavigActivityMapper;
import com.propertyvista.crm.client.mvp.MessageActivityMapper;
import com.propertyvista.crm.client.mvp.Right1ActivityMapper;
import com.propertyvista.crm.client.mvp.Right2ActivityMapper;
import com.propertyvista.crm.client.mvp.SecondNavigActivityMapper;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.AppSiteView;
import com.pyx4j.widgets.client.style.IStyleSuffix;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.Theme;

@Singleton
public class CrmView extends LayoutPanel {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public static enum StyleSuffix implements IStyleSuffix {
        Header, MainNavig, Caption, SecondaryNavig, Message, Content, Center, Main, Left, Right, Footer, Display
    }

    @Inject
    public CrmView(LogoActivityMapper logoActivityMapper,

    ActionsActivityMapper actionsActivityMapper,

    MainNavigActivityMapper mainNavigActivityMapper,

    SecondNavigActivityMapper secondNavigActivityMapper,

    CaptionActivityMapper captionActivityMapper,

    MessageActivityMapper messageActivityMapper,

    ContentActivityMapper contentActivityMapper,

    Left1ActivityMapper left1ActivityMapper,

    Left2ActivityMapper left2ActivityMapper,

    Right1ActivityMapper right1ActivityMapper,

    Right2ActivityMapper right2ActivityMapper,

    BottomActivityMapper bottomActivityMapper,

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

        DisplayPanel footerPanel = new DisplayPanel("Footer");
        contentPanel.addSouth(footerPanel, 5);

        //============ Left Panel ============

        VerticalPanel leftPanel = new VerticalPanel();
        contentPanel.addWest(leftPanel, 15);

        DisplayPanel mainNavigDisplayPanel = new DisplayPanel("MainNavig");
        leftPanel.add(mainNavigDisplayPanel);
        leftPanel.setCellHeight(mainNavigDisplayPanel, "100%");

        DisplayPanel communicationsDisplayPanel = new DisplayPanel("Communications");
        leftPanel.add(communicationsDisplayPanel);

        //============ Main ============

        DisplayPanel mainPanel = new DisplayPanel("Main");
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

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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.AppActivityManager;
import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.client.ui.AppSiteView;
import com.pyx4j.widgets.client.style.IStyleSuffix;
import com.pyx4j.widgets.client.style.StyleManger;

import com.propertyvista.portal.client.mvp.ActionsActivityMapper;
import com.propertyvista.portal.client.mvp.BottomActivityMapper;
import com.propertyvista.portal.client.mvp.ContentActivityMapper;
import com.propertyvista.portal.client.mvp.LogoActivityMapper;
import com.propertyvista.portal.client.mvp.MainNavigActivityMapper;
import com.propertyvista.portal.client.mvp.StaticContentActivityMapper;
import com.propertyvista.portal.client.themes.BlueColdTheme;

public class PortalView extends FlowPanel {

    public static String DEFAULT_STYLE_PREFIX = "PortalView";

    public static enum StyleSuffix implements IStyleSuffix {
        Content, Header, Footer, MainNavig, Center, Main, Left, Right, Display, SecondaryNavig, StaticContent
    }

    public PortalView() {

        EventBus eventBus = AppSite.getEventBus();

        StyleManger.installTheme(new BlueColdTheme());

        String prefix = DEFAULT_STYLE_PREFIX;

        setStyleName(prefix);

        //============ Header  ============

        FlowPanel headerWrapper = new FlowPanel();
        headerWrapper.setStyleName(prefix + StyleSuffix.Header);
        headerWrapper.setWidth("100%");
        add(headerWrapper);

        DisplayPanel logoDisplayPanel = new DisplayPanel();
        logoDisplayPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        headerWrapper.add(logoDisplayPanel);

        DisplayPanel actionsDisplayPanel = new DisplayPanel();
        actionsDisplayPanel.getElement().getStyle().setFloat(Style.Float.RIGHT);
        headerWrapper.add(actionsDisplayPanel);

        //================ Main application area ======= 
        FlowPanel mainNavigWrapper = new FlowPanel();
        mainNavigWrapper.setStyleName(prefix + StyleSuffix.MainNavig);
        add(mainNavigWrapper);

        DisplayPanel mainNavigDisplayPanel = new DisplayPanel();
        mainNavigWrapper.add(mainNavigDisplayPanel);

        SimplePanel centerWrapper = new SimplePanel();
        centerWrapper.setStyleName(prefix + StyleSuffix.Center);
        add(centerWrapper);

        FlowPanel mainWrapper = new FlowPanel();
        mainWrapper.setStyleName(prefix + StyleSuffix.Main);
        centerWrapper.add(mainWrapper);

        DisplayPanel contentDisplayPanel = new DisplayPanel();
        contentDisplayPanel.setStyleName(prefix + StyleSuffix.Content);
        mainWrapper.add(contentDisplayPanel);

        DisplayPanel statictDisplayPanel = new DisplayPanel();
        statictDisplayPanel.setStyleName(prefix + StyleSuffix.StaticContent);
        mainWrapper.add(statictDisplayPanel);

        //================== Footer ========================
        FlowPanel footerWrapper = new FlowPanel();
        footerWrapper.setStyleName(prefix + StyleSuffix.Footer);
        footerWrapper.getElement().getStyle().setProperty("clear", "left");
        add(footerWrapper);

        DisplayPanel bottomDisplayPanel = new DisplayPanel();
        footerWrapper.add(bottomDisplayPanel);

        bind(new LogoActivityMapper(), logoDisplayPanel, eventBus);
        bind(new ActionsActivityMapper(), actionsDisplayPanel, eventBus);
        bind(new MainNavigActivityMapper(), mainNavigDisplayPanel, eventBus);
        bind(new ContentActivityMapper(), contentDisplayPanel, eventBus);
        bind(new BottomActivityMapper(), bottomDisplayPanel, eventBus);
        bind(new StaticContentActivityMapper(), statictDisplayPanel, eventBus);

    }

    private static void bind(AppActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus) {
        AppActivityManager activityManager = new AppActivityManager(mapper, eventBus);
        activityManager.setDisplay(widget);
    }

    class DisplayPanel extends SimplePanel {
        DisplayPanel() {
            String prefix = AppSiteView.DEFAULT_STYLE_PREFIX;
            setStyleName(prefix + StyleSuffix.Display);

        }
    }

}

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
package com.propertyvista.portal.ptapp.client.ui;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.AppSiteView;
import com.pyx4j.widgets.client.style.IStyleSuffix;
import com.pyx4j.widgets.client.style.StyleManger;

import com.propertyvista.portal.ptapp.client.mvp.ActionsActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.BottomActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.CaptionActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.ContentActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.LogoActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.MainNavigActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.MessageActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.SecondNavigActivityMapper;
import com.propertyvista.portal.ptapp.client.themes.GainsboroTheme;

public class PtAppSiteView extends FlowPanel {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public static enum StyleSuffix implements IStyleSuffix {
        Header, MainNavig, Caption, SecondaryNavig, Message, Content, Center, Main, Left, Right, Footer, Display
    }

    public PtAppSiteView() {

        StyleManger.installTheme(new GainsboroTheme());

        EventBus eventBus = AppSite.instance().getEventBus();

        String prefix = AppSiteView.DEFAULT_STYLE_PREFIX;

        setStyleName(prefix);

        //============ Top Panel ============

        FlowPanel headerWrapper = new FlowPanel();
        headerWrapper.setStyleName(prefix + StyleSuffix.Header);
        add(headerWrapper);

        DisplayPanel logoDisplayPanel = new DisplayPanel();
        logoDisplayPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        headerWrapper.add(logoDisplayPanel);

        DisplayPanel actionsDisplayPanel = new DisplayPanel();
        actionsDisplayPanel.getElement().getStyle().setFloat(Style.Float.RIGHT);
        headerWrapper.add(actionsDisplayPanel);

        //============ Main Navig ============

        FlowPanel mainNavigWrapper = new FlowPanel();
        mainNavigWrapper.setStyleName(prefix + StyleSuffix.MainNavig);
        add(mainNavigWrapper);

        DisplayPanel mainNavigDisplayPanel = new DisplayPanel();
        mainNavigWrapper.add(mainNavigDisplayPanel);

        //============ Main ============

        SimplePanel centerWrapper = new SimplePanel();
        centerWrapper.setStyleName(prefix + StyleSuffix.Center);
        add(centerWrapper);

        FlowPanel mainWrapper = new FlowPanel();
        mainWrapper.setStyleName(prefix + StyleSuffix.Main);
        centerWrapper.add(mainWrapper);

        FlowPanel caption2navigPanel = new FlowPanel();

        DisplayPanel captionDisplayPanel = new DisplayPanel();
        captionDisplayPanel.setStyleName(prefix + StyleSuffix.Caption);
        captionDisplayPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        caption2navigPanel.add(captionDisplayPanel);

        DisplayPanel secondNavigDisplayPanel = new DisplayPanel();
        secondNavigDisplayPanel.setStyleName(prefix + StyleSuffix.SecondaryNavig);
        secondNavigDisplayPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        caption2navigPanel.add(secondNavigDisplayPanel);

        mainWrapper.add(caption2navigPanel);

        DisplayPanel messageDisplayPanel = new DisplayPanel();
        messageDisplayPanel.setStyleName(prefix + StyleSuffix.Message);
        mainWrapper.add(messageDisplayPanel);

        DisplayPanel contentDisplayPanel = new DisplayPanel();
        contentDisplayPanel.setStyleName(prefix + StyleSuffix.Content);
        mainWrapper.add(contentDisplayPanel);

        FlowPanel leftWrapper = new FlowPanel();
        leftWrapper.setStyleName(prefix + StyleSuffix.Left);
        add(leftWrapper);

        DisplayPanel left1DisplayPanel = new DisplayPanel();
        leftWrapper.add(left1DisplayPanel);
        DisplayPanel left2DisplayPanel = new DisplayPanel();
        leftWrapper.add(left2DisplayPanel);

        FlowPanel rightWrapper = new FlowPanel();
        rightWrapper.setStyleName(prefix + StyleSuffix.Right);
        add(rightWrapper);

        DisplayPanel right1DisplayPanel = new DisplayPanel();
        rightWrapper.add(right1DisplayPanel);
        DisplayPanel right2DisplayPanel = new DisplayPanel();
        rightWrapper.add(right2DisplayPanel);

        //============ Footer ============

        FlowPanel footerWrapper = new FlowPanel();
        footerWrapper.setStyleName(prefix + StyleSuffix.Footer);
        footerWrapper.getElement().getStyle().setProperty("clear", "left");
        add(footerWrapper);

        DisplayPanel bottomDisplayPanel = new DisplayPanel();
        footerWrapper.add(bottomDisplayPanel);

        // Activities<->Displays mapping:

        bind(new LogoActivityMapper(), logoDisplayPanel, eventBus);
        bind(new ActionsActivityMapper(), actionsDisplayPanel, eventBus);
        bind(new MainNavigActivityMapper(), mainNavigDisplayPanel, eventBus);
        bind(new SecondNavigActivityMapper(), secondNavigDisplayPanel, eventBus);

        bind(new CaptionActivityMapper(), captionDisplayPanel, eventBus);
        bind(new MessageActivityMapper(), messageDisplayPanel, eventBus);
        bind(new ContentActivityMapper(), contentDisplayPanel, eventBus);

        bind(new BottomActivityMapper(), bottomDisplayPanel, eventBus);
    }

    private static void bind(ActivityMapper mapper, DisplayPanel widget, EventBus eventBus) {
        ActivityManager activityManager = new ActivityManager(mapper, eventBus);
        activityManager.setDisplay(widget);
    }

    class DisplayPanel extends SimplePanel {
        DisplayPanel() {
            String prefix = AppSiteView.DEFAULT_STYLE_PREFIX;
            setStyleName(prefix + StyleSuffix.Display);
        }
    }
}

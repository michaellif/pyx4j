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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
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
public class CrmViewOrig extends FlowPanel {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public static enum StyleSuffix implements IStyleSuffix {
        Header, MainNavig, Caption, SecondaryNavig, Message, Content, Center, Main, Left, Right, Footer, Display
    }

    @Inject
    public CrmViewOrig(LogoActivityMapper logoActivityMapper,

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
        caption2navigPanel.add(captionDisplayPanel);

        DisplayPanel secondNavigDisplayPanel = new DisplayPanel();
        secondNavigDisplayPanel.setStyleName(prefix + StyleSuffix.SecondaryNavig);
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

        bind(logoActivityMapper, logoDisplayPanel, eventBus);
        bind(actionsActivityMapper, actionsDisplayPanel, eventBus);
        bind(mainNavigActivityMapper, mainNavigDisplayPanel, eventBus);
        bind(secondNavigActivityMapper, secondNavigDisplayPanel, eventBus);

        bind(captionActivityMapper, captionDisplayPanel, eventBus);
        bind(messageActivityMapper, messageDisplayPanel, eventBus);
        bind(contentActivityMapper, contentDisplayPanel, eventBus);

        bind(left1ActivityMapper, left1DisplayPanel, eventBus);
        bind(left2ActivityMapper, left2DisplayPanel, eventBus);

        bind(right1ActivityMapper, right1DisplayPanel, eventBus);
        bind(right2ActivityMapper, right2DisplayPanel, eventBus);

        bind(bottomActivityMapper, bottomDisplayPanel, eventBus);

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

}

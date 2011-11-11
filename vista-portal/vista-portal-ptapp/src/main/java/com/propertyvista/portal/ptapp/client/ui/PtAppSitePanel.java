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
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.EventBus;

import com.pyx4j.commons.css.StyleManger;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.ptapp.client.mvp.ActionsActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.BottomActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.CaptionActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.ContentActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.LogoActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.MainNavigActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.MessageActivityMapper;
import com.propertyvista.portal.ptapp.client.mvp.SecondNavigActivityMapper;
import com.propertyvista.portal.ptapp.client.themes.GainsboroPalette;
import com.propertyvista.portal.ptapp.client.themes.PtAppSitePanelTheme;
import com.propertyvista.portal.ptapp.client.themes.PtAppTheme;

public class PtAppSitePanel extends FlowPanel {

    public PtAppSitePanel() {

        StyleManger.installTheme(new PtAppTheme(), new GainsboroPalette());

        EventBus eventBus = AppSite.getEventBus();

        setStyleName(PtAppSitePanelTheme.StyleName.SitePanel.name());

        //============ Top Panel ============

        VerticalPanel headerWrapper = new VerticalPanel();
        headerWrapper.setStyleName(PtAppSitePanelTheme.StyleName.SitePanelHeader.name());
        add(headerWrapper);

        DisplayPanel actionsDisplayPanel = new DisplayPanel();
        headerWrapper.add(actionsDisplayPanel);

        DisplayPanel logoDisplayPanel = new DisplayPanel();
        headerWrapper.add(logoDisplayPanel);

        //============ Main Navig ============

        FlowPanel mainNavigWrapper = new FlowPanel();
        mainNavigWrapper.setStyleName(PtAppSitePanelTheme.StyleName.SitePanelMainNavigation.name());
        add(mainNavigWrapper);

        DisplayPanel mainNavigDisplayPanel = new DisplayPanel();
        mainNavigWrapper.add(mainNavigDisplayPanel);

        //============ Main ============

        SimplePanel centerWrapper = new SimplePanel();
        centerWrapper.setStyleName(PtAppSitePanelTheme.StyleName.SitePanelCenter.name());
        add(centerWrapper);

        FlowPanel mainWrapper = new FlowPanel();
        mainWrapper.setStyleName(PtAppSitePanelTheme.StyleName.SitePanelMain.name());
        centerWrapper.add(mainWrapper);

        FlowPanel caption2navigPanel = new FlowPanel();

        DisplayPanel captionDisplayPanel = new DisplayPanel();
        captionDisplayPanel.setStyleName(PtAppSitePanelTheme.StyleName.SitePanelCaption.name());
        captionDisplayPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        caption2navigPanel.add(captionDisplayPanel);

        DisplayPanel secondNavigDisplayPanel = new DisplayPanel();
        secondNavigDisplayPanel.setStyleName(PtAppSitePanelTheme.StyleName.SitePanelSecondaryNavigation.name());
        secondNavigDisplayPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        caption2navigPanel.add(secondNavigDisplayPanel);

        mainWrapper.add(caption2navigPanel);

        DisplayPanel messageDisplayPanel = new DisplayPanel();
        messageDisplayPanel.setStyleName(PtAppSitePanelTheme.StyleName.SitePanelMessage.name());
        mainWrapper.add(messageDisplayPanel);

        DisplayPanel contentDisplayPanel = new DisplayPanel();
        contentDisplayPanel.setStyleName(PtAppSitePanelTheme.StyleName.SitePanelContent.name());
        mainWrapper.add(contentDisplayPanel);

        //============ Footer ============

        FlowPanel footerWrapper = new FlowPanel();
        footerWrapper.setStyleName(PtAppSitePanelTheme.StyleName.SitePanelFooter.name());
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
            setStyleName(PtAppSitePanelTheme.StyleName.SitePanelDisplay.name());
        }
    }
}

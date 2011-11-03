/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Oct 6, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client.ui;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.StyleManger;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.AppActivityManager;
import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.client.ui.AppSiteView;
import com.pyx4j.tester.client.mvp.ConsoleActivityMapper;
import com.pyx4j.tester.client.mvp.MainActivityMapper;
import com.pyx4j.tester.client.mvp.NavigActivityMapper;
import com.pyx4j.tester.client.theme.TesterPalette;
import com.pyx4j.tester.client.theme.TesterTheme;

public class TesterPanel extends LayoutPanel {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public static enum StyleSuffix implements IStyleName {
        Content, Action, Header, Navigation, Footer, Display, NavigContainer;
    }

    public TesterPanel() {

        EventBus eventBus = AppSite.getEventBus();

        StyleManger.installTheme(new TesterTheme(), new TesterPalette());

        setStyleName(AppSiteView.DEFAULT_STYLE_PREFIX);

        DockLayoutPanel contentPanel = new DockLayoutPanel(Unit.EM);
        contentPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Content);
        add(contentPanel);

        SplitLayoutPanel splitPanel = new SplitLayoutPanel(3);
        splitPanel.ensureDebugId("splitPanel");
        splitPanel.setSize("100%", "100%");
        contentPanel.add(splitPanel);

        DisplayPanel navigDisplay = new DisplayPanel();
        navigDisplay.setSize("100%", "100%");

        splitPanel.addWest(navigDisplay, 250);
        splitPanel.setWidgetMinSize(navigDisplay, 150);

        DisplayPanel consoleDisplay = new DisplayPanel();
        navigDisplay.setSize("100%", "100%");

        splitPanel.addSouth(consoleDisplay, 250);
        splitPanel.setWidgetMinSize(consoleDisplay, 150);

        DisplayPanel mainDisplay = new DisplayPanel();
        splitPanel.add(mainDisplay);

        bind(new NavigActivityMapper(), navigDisplay, eventBus);
        bind(new ConsoleActivityMapper(), consoleDisplay, eventBus);
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

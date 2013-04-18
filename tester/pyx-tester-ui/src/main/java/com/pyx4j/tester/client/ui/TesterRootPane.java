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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.web.bindery.event.shared.EventBus;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.DisplayPanel;
import com.pyx4j.tester.client.mvp.ConsoleActivityMapper;
import com.pyx4j.tester.client.mvp.MainActivityMapper;
import com.pyx4j.tester.client.mvp.NavigActivityMapper;
import com.pyx4j.tester.client.theme.TesterPalette;
import com.pyx4j.tester.client.theme.TesterTheme;

public class TesterRootPane extends LayoutPanel {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public static enum StyleSuffix implements IStyleName {
        Content, Action, Header, Navigation, Footer, Display, NavigContainer;
    }

    public TesterRootPane() {

        EventBus eventBus = AppSite.getEventBus();

        StyleManager.installTheme(new TesterTheme(), new TesterPalette());

        setStyleName(DEFAULT_STYLE_PREFIX);

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

        splitPanel.addEast(consoleDisplay, 500);
        splitPanel.setWidgetMinSize(consoleDisplay, 150);

        DisplayPanel mainDisplay = new DisplayPanel();
        splitPanel.add(mainDisplay);

        DisplayPanel.bind(new NavigActivityMapper(), navigDisplay, eventBus);
        DisplayPanel.bind(new ConsoleActivityMapper(), consoleDisplay, eventBus);
        DisplayPanel.bind(new MainActivityMapper(), mainDisplay, eventBus);
    }

}

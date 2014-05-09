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
 * Created on Apr 22, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.layout.backoffice;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.site.client.DisplayPanel;
import com.pyx4j.site.client.ui.devconsole.DevConsoleTab;
import com.pyx4j.site.client.ui.devconsole.BackOfficeDevConsole;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel;

public class BackOfficeLayoutPanel extends ResponsiveLayoutPanel {

    private final DockLayoutPanel menuPanel;

    private DevConsoleTab devConsoleTab;

    private boolean menuVisible;

    private int menuWidth = 20;

    private int headerHeight = 50;

    private int notificationsHeight = 0;

    public BackOfficeLayoutPanel() {

        // ============ Header ============
        {
            DisplayPanel headerDisplay = getDisplay(DisplayType.header);

            Layer layer = getLayout().attachChild(headerDisplay.asWidget().getElement(), headerDisplay);
            headerDisplay.setLayoutData(layer);

            getChildren().add(headerDisplay);
            adopt(headerDisplay);
        }

        // ============ Notifications ============
        {
            DisplayPanel notificationsDisplay = getDisplay(DisplayType.notification);
            Layer layer = getLayout().attachChild(notificationsDisplay.asWidget().getElement(), notificationsDisplay);
            notificationsDisplay.setLayoutData(layer);

            getChildren().add(notificationsDisplay);
            adopt(notificationsDisplay);
        }

        // ============ Menu ============
        {
            menuPanel = new DockLayoutPanel(Unit.PX);

            Layer layer = getLayout().attachChild(menuPanel.asWidget().getElement(), menuPanel);
            menuPanel.setLayoutData(layer);

            getChildren().add(menuPanel);
            adopt(menuPanel);

            menuPanel.addSouth(getDisplay(DisplayType.footer), 40);

            menuPanel.addSouth(getDisplay(DisplayType.extra), 200);

            menuPanel.add(getDisplay(DisplayType.menu));

        }

        // ============ Content ============
        {
            DisplayPanel contentDisplay = getDisplay(DisplayType.content);

            Layer layer = getLayout().attachChild(contentDisplay.asWidget().getElement(), contentDisplay);
            contentDisplay.setLayoutData(layer);

            getChildren().add(contentDisplay);
            adopt(contentDisplay);
        }

        // ============ Dev Console ============
        if (ApplicationMode.isDevelopment()) {
            devConsoleTab = new DevConsoleTab(new BackOfficeDevConsole(this));
            add(devConsoleTab.asWidget(), getElement());
        }

        forceLayout(0);

    }

    @Override
    protected void doLayout() {

        int top = 0;
        int height = headerHeight;

        {
            Layer layer = (Layer) getDisplay(DisplayType.header).getLayoutData();
            layer.setTopHeight(top, Unit.PX, height, Unit.PX);
            layer.setLeftWidth(0.0, Unit.PX, 100.0, Unit.PCT);
        }

        top += height;
        height = notificationsHeight;

        {
            Layer layer = (Layer) getDisplay(DisplayType.notification).getLayoutData();
            layer.setTopHeight(top, Unit.PX, height, Unit.PX);
            layer.setLeftWidth(0.0, Unit.PX, 100.0, Unit.PCT);
        }

        top += height;

        {
            Layer layer = (Layer) menuPanel.getLayoutData();
            layer.setVisible(menuVisible);
            if (menuVisible) {
                layer.setTopBottom(top, Unit.PX, 0, Unit.PX);
                layer.setLeftWidth(0, Unit.PX, menuWidth, Unit.PX);
            }
        }

        {
            Layer layer = (Layer) getDisplay(DisplayType.content).getLayoutData();
            layer.setTopBottom(top, Unit.PX, 0, Unit.PX);
            layer.setLeftRight(menuVisible ? menuWidth : 0, Unit.PX, 0, Unit.PX);
        }
    }

    public void setMenuVisible(boolean visible) {
        this.menuVisible = visible;
    }

    public void setMenuWidth(int width) {
        this.menuWidth = width;
    }

    public void setHeaderHeight(int height) {
        this.headerHeight = height;
    }

    public void setNotificationsHeight(int height) {
        this.notificationsHeight = height;
    }

    @Override
    public void resizeComponents() {

        for (Widget child : getChildren()) {
            if (child instanceof RequiresResize) {
                ((RequiresResize) child).onResize();
            }
        }
    }

    @Override
    public void onLayoutChangeRequest(LayoutChangeRequestEvent event) {
    }

}

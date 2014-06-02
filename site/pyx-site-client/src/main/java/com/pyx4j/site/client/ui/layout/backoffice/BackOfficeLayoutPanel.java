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
import com.pyx4j.site.client.ui.devconsole.BackOfficeDevConsole;
import com.pyx4j.site.client.ui.devconsole.DevConsoleTab;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel;

public class BackOfficeLayoutPanel extends ResponsiveLayoutPanel {

    private DevConsoleTab devConsoleTab;

    private final DockLayoutPanel pageHolder;

    public BackOfficeLayoutPanel() {

        pageHolder = new DockLayoutPanel(Unit.PX);

        pageHolder.addNorth(getDisplay(DisplayType.header), 0);

        pageHolder.addNorth(getDisplay(DisplayType.notification), 0);

        pageHolder.addEast(getDisplay(DisplayType.extra), 200);

        DockLayoutPanel menuHolder = new DockLayoutPanel(Unit.PX);
        menuHolder.addSouth(getDisplay(DisplayType.footer), 40);
        menuHolder.add(getDisplay(DisplayType.menu));

        pageHolder.addWest(menuHolder, 200);

        pageHolder.add(getDisplay(DisplayType.content));

        // ============ Content ============
        {

            Layer layer = getLayout().attachChild(pageHolder.getElement(), pageHolder);
            pageHolder.setLayoutData(layer);
            getChildren().add(pageHolder);
            adopt(pageHolder);
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

        {
            Layer layer = (Layer) pageHolder.getLayoutData();
            layer.setTopBottom(0, Unit.PX, 0, Unit.PX);
            layer.setLeftRight(0, Unit.PX, 0, Unit.PX);
        }
    }

    public void setMenuVisible(boolean visible) {

    }

    public void setMenuWidth(int width) {

    }

    public void setHeaderHeight(int height) {
        pageHolder.setWidgetSize(getDisplay(DisplayType.header), height);
    }

    public void setNotificationsHeight(int height) {
        pageHolder.setWidgetSize(getDisplay(DisplayType.notification), height);
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

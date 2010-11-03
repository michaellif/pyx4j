/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Apr 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.GlassPanel;

public class Perspective {

    private Panel parent;

    private final DockLayoutPanel contentPanel;

    private final Separator headerHolder;

    private final Separator menuHolder;

    private final Separator toolbarHolder;

    private final Separator statusHolder;

    private final Separator footerHolder;

    public Perspective() {

        contentPanel = new DockLayoutPanel(Unit.EM);
        contentPanel.setSize("100%", "100%");

        //Header
        headerHolder = new Separator();
        contentPanel.addNorth(headerHolder, 0);

        //Menu
        menuHolder = new Separator();
        contentPanel.addNorth(menuHolder, 0);

        //Toolbar
        toolbarHolder = new Separator();
        contentPanel.addNorth(toolbarHolder, 0);

        //Footer
        footerHolder = new Separator();
        contentPanel.addSouth(footerHolder, 0);

        //Status
        statusHolder = new Separator();
        contentPanel.addSouth(statusHolder, 0);

        Window.enableScrolling(false);
        Window.setMargin("0px");
    }

    public void setHeaderPanel(Widget headerPanel) {
        contentPanel.insertNorth(headerPanel, 1.5, headerHolder);
    }

    public void setMenuBar(Widget menuBar) {
        contentPanel.insertNorth(menuBar, 1.5, menuHolder);
    }

    public void setToolbar(Widget actions) {
        contentPanel.insertNorth(actions, 2, toolbarHolder);
    }

    public void setMainPanel(Widget mainPanel) {
        contentPanel.add(mainPanel);
    }

    public void setStatusPanel(Widget statusPanel) {
        contentPanel.insertSouth(statusPanel, 1.5, statusHolder);
    }

    public void setFooterPanel(Widget footerPanel) {
        contentPanel.insertSouth(footerPanel, 1.5, footerHolder);
    }

    public void attachToParent(LayoutPanel parent) {
        this.parent = parent;
        parent.add(GlassPanel.instance());
        parent.add(contentPanel);
    }

    public void detachFromParent() {
        parent.remove(contentPanel);
        parent.remove(GlassPanel.instance());
        parent = null;
    }

    private static class Separator extends SimplePanel {

    }
}

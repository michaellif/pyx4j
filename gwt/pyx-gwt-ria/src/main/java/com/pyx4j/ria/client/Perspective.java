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
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.GlassPanel;

public class Perspective {

    private Panel parent;

    private final DockLayoutPanel contentPanel;

    private final SimplePanel headerMark;

    private final SimplePanel menuMark;

    private final SimplePanel toolbarMark;

    private final SimplePanel statusMark;

    private final SimplePanel footerMark;

    public Perspective() {

        contentPanel = new DockLayoutPanel(Unit.EM);
        contentPanel.setSize("100%", "100%");

        //Header
        headerMark = new SimplePanel();
        contentPanel.addNorth(headerMark, 0);

        //Menu
        menuMark = new SimplePanel();
        contentPanel.addNorth(menuMark, 0);

        //Toolbar
        toolbarMark = new SimplePanel();
        contentPanel.addNorth(toolbarMark, 0);

        //Footer
        footerMark = new SimplePanel();
        contentPanel.addSouth(footerMark, 0);

        //Status
        statusMark = new SimplePanel();
        contentPanel.addSouth(statusMark, 0);

        Window.enableScrolling(false);
        Window.setMargin("0px");
    }

    public void setHeaderPanel(Widget headerPanel) {
        contentPanel.insertNorth(headerPanel, 1.5, headerMark);
    }

    public void setMenuBar(Widget menuBar) {
        contentPanel.insertNorth(menuBar, 1.5, menuMark);
    }

    public void setToolbar(Widget actions) {
        contentPanel.insertNorth(actions, 2, toolbarMark);
    }

    public void setMainPanel(Widget mainPanel) {
        contentPanel.add(mainPanel);
    }

    public void setStatusPanel(Widget statusPanel) {
        contentPanel.insertSouth(statusPanel, 1.5, statusMark);
    }

    public void setFooterPanel(Widget footerPanel) {
        contentPanel.insertSouth(footerPanel, 1.5, footerMark);
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

}

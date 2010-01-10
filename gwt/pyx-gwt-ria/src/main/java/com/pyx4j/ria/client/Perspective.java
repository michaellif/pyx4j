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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class Perspective {

    private Panel parent;

    private final GlassPanel glassPanel;

    private final DockPanel contentPanel;

    private final SpaceHolderPanel headerHolder;

    private final SpaceHolderPanel menuHolder;

    private final SpaceHolderPanel toolbarLinksHolder;

    private final SpaceHolderPanel toolbarActionsHolder;

    private final SpaceHolderPanel mainPanelHolder;

    private final SpaceHolderPanel statusHolder;

    private final SpaceHolderPanel footerHolder;

    public Perspective() {
        glassPanel = new GlassPanel();

        contentPanel = new DockPanel();
        contentPanel.setSize("100%", "100%");

        //Header
        headerHolder = new SpaceHolderPanel();
        contentPanel.add(headerHolder, DockPanel.NORTH);

        //Menu
        menuHolder = new SpaceHolderPanel();
        contentPanel.add(menuHolder, DockPanel.NORTH);

        //Toolbar
        HorizontalPanel toolbarHolder = new HorizontalPanel();

        toolbarActionsHolder = new SpaceHolderPanel();
        toolbarHolder.add(toolbarActionsHolder);
        toolbarHolder.setCellHorizontalAlignment(toolbarActionsHolder, HasHorizontalAlignment.ALIGN_LEFT);
        toolbarHolder.setCellVerticalAlignment(toolbarActionsHolder, HasVerticalAlignment.ALIGN_MIDDLE);

        toolbarLinksHolder = new SpaceHolderPanel();
        toolbarHolder.add(toolbarLinksHolder);
        toolbarHolder.setCellHorizontalAlignment(toolbarLinksHolder, HasHorizontalAlignment.ALIGN_RIGHT);
        toolbarHolder.setCellVerticalAlignment(toolbarLinksHolder, HasVerticalAlignment.ALIGN_MIDDLE);
        toolbarHolder.setCellWidth(toolbarLinksHolder, "100%");

        contentPanel.add(toolbarHolder, DockPanel.NORTH);

        //MainPanel
        mainPanelHolder = new SpaceHolderPanel();
        mainPanelHolder.setSize("100%", "100%");
        contentPanel.add(mainPanelHolder, DockPanel.CENTER);
        contentPanel.setCellHeight(mainPanelHolder, "100%");
        contentPanel.setCellWidth(mainPanelHolder, "100%");

        //Footer
        footerHolder = new SpaceHolderPanel();
        contentPanel.add(footerHolder, DockPanel.SOUTH);

        //Status
        statusHolder = new SpaceHolderPanel();
        contentPanel.add(statusHolder, DockPanel.SOUTH);

        Window.enableScrolling(false);
        Window.setMargin("0px");
    }

    public void setHeaderPanel(Widget headerPanel) {
        headerHolder.add(headerPanel);
    }

    public void setFooterPanel(Widget footerPanel) {
        footerHolder.add(footerPanel);
    }

    public void setMenuBar(Widget menuBar) {
        menuHolder.add(menuBar);
    }

    public void setActionsToolbar(Widget actions) {
        toolbarActionsHolder.add(actions);
    }

    public void setLinksToolbar(Widget links) {
        toolbarLinksHolder.add(links);
    }

    public void setMainPanel(Widget mainPanel) {
        mainPanelHolder.add(mainPanel);
    }

    public void setStatusPanel(Widget statusPanel) {
        statusHolder.add(statusPanel);
    }

    public void attachToParent(Panel parent) {
        this.parent = parent;
        if (parent instanceof RootPanel) {
            ((RootPanel) parent).add(contentPanel, 0, 0);
            ((RootPanel) parent).add(glassPanel, 0, 0);
        } else {
            parent.add(contentPanel);
            parent.add(glassPanel);
        }
    }

    public void detachFromParent() {
        parent.remove(contentPanel);
        parent.remove(glassPanel);
        parent = null;
    }

}

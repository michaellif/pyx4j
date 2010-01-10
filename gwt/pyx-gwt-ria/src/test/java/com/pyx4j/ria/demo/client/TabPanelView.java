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
 * Created on Apr 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.demo.client;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.ria.client.AbstractView;
import com.pyx4j.widgets.client.tabpanel.BasicTab;
import com.pyx4j.widgets.client.tabpanel.TabPanelComposite;

public class TabPanelView extends AbstractView {

    public TabPanelView(String title, ImageResource imageResource) {
        super(createContentPane(), title, imageResource);
    }

    private static Widget createContentPane() {

        TabPanelComposite tabPanel = new TabPanelComposite();
        VerticalPanel panel1 = new VerticalPanel();
        Label label1 = new Label("First Tab");
        panel1.add(label1);
        panel1.add(new Label("First Tab"));
        panel1.add(new Label("First Tab"));
        panel1.add(new Label("First Tab"));
        panel1.add(new Label("First Tab"));
        panel1.add(new Label("First Tab"));
        panel1.add(new Label("First Tab"));
        panel1.add(new Label("First Tab"));
        panel1.add(new Label("First Tab"));
        tabPanel.insert(new BasicTab(panel1, "First Tab", null), 0, true);
        tabPanel.insert(new BasicTab(new Label("Second Tab"), "Second Tab", null), 1, true);
        tabPanel.insert(new BasicTab(new Label("Third Tab"), "Third Tab", null), 2, true);

        tabPanel.selectTab(0);
        tabPanel.setSize("400px", "300px");

        DockPanel content = new DockPanel();
        content.add(tabPanel, DockPanel.CENTER);

        content.setCellWidth(tabPanel, "100%");
        content.setCellHeight(tabPanel, "100%");

        DOM.setStyleAttribute(content.getElement(), "padding", "10px");

        return content;
    }

    @Override
    public Widget getToolbarPane() {
        return new Label("ToolbarPane");
    }

    @Override
    public Widget getFooterPane() {
        return null;
    }

    @Override
    public MenuBar getMenu() {
        return null;
    }

}

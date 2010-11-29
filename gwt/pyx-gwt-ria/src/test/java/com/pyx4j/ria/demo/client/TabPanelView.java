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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.ria.client.view.AbstractView;
import com.pyx4j.widgets.client.tabpanel.Tab;
import com.pyx4j.widgets.client.tabpanel.TabPanelComposite;

public class TabPanelView extends AbstractView {

    public TabPanelView(String tabTitle, ImageResource imageResource, boolean closable) {
        super(imageResource, closable);
        addPage(new Tab(createContentPane(), null, null, false));

        setTabTitle(tabTitle);
    }

    private static Widget createContentPane() {

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

        TabPanelComposite tabPanel = new TabPanelComposite();
        tabPanel.add(new Tab(new ScrollPanel(panel1), "First Tab", null, true));
        tabPanel.add(new Tab(new Label("Second Tab"), "Second Tab", null, true));
        tabPanel.add(new Tab(new Label("Third Tab"), "Third Tab", null, true));

        tabPanel.setSize("400px", "300px");

        ScrollPanel content = new ScrollPanel();
        content.add(tabPanel);

        DOM.setStyleAttribute(content.getElement(), "padding", "10px");

        return content;
    }

}

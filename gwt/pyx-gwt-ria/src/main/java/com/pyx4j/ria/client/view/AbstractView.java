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
 * Created on May 15, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client.view;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.style.theme.WindowsTheme;
import com.pyx4j.widgets.client.tabpanel.Tab;
import com.pyx4j.widgets.client.tabpanel.TabPanel;

public abstract class AbstractView extends Tab {

    private final DockLayoutPanel rootPanel;

    private final SimplePanel toolbarMark;

    private final SimplePanel tabsMark;

    private final TabPanel tabPanel;

    public AbstractView(ImageResource tabImage, boolean closable) {
        super(tabImage, closable);

        rootPanel = new DockLayoutPanel(Unit.EM);

        toolbarMark = new SimplePanel();
        rootPanel.addNorth(toolbarMark, 0);

        tabsMark = new SimplePanel();
        rootPanel.addSouth(tabsMark, 0);

        tabPanel = new TabPanel(WindowsTheme.pyx4j_TabBottom);

        rootPanel.add(tabPanel.getDeck());

        super.setContentPane(rootPanel);
    }

    @Override
    public void setContentPane(Widget contentPane) {
        throw new RuntimeException("Use AbstractView methods to set pages");
    }

    protected void setToolbarPane(Widget toolbarPane) {
        rootPanel.insertNorth(toolbarPane, 2, toolbarMark);
    }

    protected void addPage(Tab pagePane) {
        if (tabPanel.size() == 1) {
            rootPanel.insertSouth(tabPanel.getTabBar(), 1.3, tabsMark);
        }
        tabPanel.add(pagePane);
        tabPanel.select(pagePane);
    }

}

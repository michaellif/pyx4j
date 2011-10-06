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
 * Created on May 14, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.tabpanel;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Tab extends LayoutPanel {

    private String tabTitle;

    private final TabBarItem tabBarItem;

    private TabPanel parentTabPanel;

    private boolean modifyed;

    public Tab(ImageResource tabImage, boolean closable) {
        this(null, null, tabImage, closable);
    }

    public Tab(Widget contentPane, String tabTitle, ImageResource tabImage, boolean closable) {
        tabBarItem = new TabBarItem(this, tabImage, closable);
        setTabTitle(tabTitle);
        if (contentPane != null) {
            add(contentPane);
        }
    }

    public void setContentPane(Widget contentPane) {
        clear();
        add(contentPane);
    }

    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
        tabBarItem.onTabTitleChange(tabTitle);
        if (parentTabPanel != null) {
            parentTabPanel.getTabBar().layout();
        }
    }

    public void setModifyed(boolean modifyed) {
        this.modifyed = modifyed;
        tabBarItem.onModifyed(modifyed);
        if (parentTabPanel != null) {
            parentTabPanel.getTabBar().layout();
        }
    }

    public boolean isModifyed() {
        return modifyed;
    }

    public void close() {
        parentTabPanel.remove(this);
    }

    public void setSelected() {
        parentTabPanel.select(this);
    }

    public boolean isSelected() {
        return this.equals(parentTabPanel.getTabBar().getSelectedTab());
    }

    public boolean isTabVisible() {
        return parentTabPanel.getTabBar().isTabVisible(this);
    }

    protected void setParentTabPanel(TabPanel parentTabPanel) {
        this.parentTabPanel = parentTabPanel;
    }

    protected TabBarItem getTabBarItem() {
        return tabBarItem;
    }

}

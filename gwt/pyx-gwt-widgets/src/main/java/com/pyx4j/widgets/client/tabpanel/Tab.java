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

import com.pyx4j.security.shared.Permission;
import com.pyx4j.security.shared.SecurityController;

public class Tab extends LayoutPanel {

    private String tabTitle;

    private final TabBarItem tabBarItem;

    private TabPanel tabPanel;

    private boolean enabled = true;

    private boolean dirty = false;

    private boolean visible = true;

    private Permission[] permissionsVisible;

    private Permission[] permissionsEnabled;

    private String warning = null;

    public Tab(ImageResource tabImage, boolean closable) {
        this(null, null, tabImage, closable);
    }

    public Tab(Widget contentPane, String tabTitle, ImageResource tabImage, boolean closable, Permission... permissions) {
        tabBarItem = new TabBarItem(this, tabImage, closable);
        setTabTitle(tabTitle);
        if (contentPane != null) {
            add(contentPane);
        }
        // java varargs creates empty arrays,  so consider it as no permissions set
        if (permissions.length == 0) {
            permissions = null;
        }
        this.permissionsVisible = permissions;
        setTabVisible(true);
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
        if (tabPanel != null) {
            tabPanel.getTabBar().layout();
        }
    }

    public void setTabDirty(boolean dirty) {
        this.dirty = dirty;
        tabBarItem.onDirty(dirty);
        if (tabPanel != null) {
            tabPanel.getTabBar().layout();
        }
    }

    public boolean isTabDirty() {
        return dirty;
    }

    public void setTabVisible(boolean visible) {
        visible = visible && ((permissionsVisible == null) || SecurityController.check(permissionsVisible));
        this.visible = visible;
        tabBarItem.onVisible(visible);
        if (tabPanel != null) {
            tabPanel.getTabBar().layout();
        }
    }

    public boolean isTabVisible() {
        return visible;
    }

    public void setPermitVisiblePermission(Permission... permission) {
        this.permissionsVisible = permission;
        setTabVisible(isTabVisible());
    }

    public void setTabEnabled(boolean enabled) {
        enabled = enabled && ((permissionsEnabled == null) || SecurityController.check(permissionsEnabled));
        this.enabled = enabled;
        tabBarItem.onEnabled(enabled);
    }

    public boolean isTabEnabled() {
        return enabled;
    }

    public void setPermitEnabledPermission(Permission... permission) {
        this.permissionsEnabled = permission;
        setTabEnabled(isTabEnabled());
    }

    public void setTabWarning(String message) {
        this.warning = message;
        tabBarItem.onWarning(message);
    }

    public String getTabWarning() {
        return warning;
    }

    public void close() {
        tabPanel.removeTab(this);
    }

    public void setTabSelected() {
        tabPanel.selectTab(this);
    }

    public boolean isTabSelected() {
        return this.equals(tabPanel.getSelectedTab());
    }

    protected void setTabPanel(TabPanel tabPanel) {
        this.tabPanel = tabPanel;
    }

    protected TabPanel getTabPanel() {
        return tabPanel;
    }

    protected TabBarItem getTabBarItem() {
        return tabBarItem;
    }

}

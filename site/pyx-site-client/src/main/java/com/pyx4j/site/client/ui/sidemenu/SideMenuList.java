/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-12-19
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.client.ui.sidemenu;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.rpc.AppPlace;

public class SideMenuList implements ISideMenuNode {

    private final ContentPanel contentPanel;

    private final List<SideMenuItem> items;

    private int indentation = 0;

    private SideMenuItem parent;

    public SideMenuList() {
        items = new ArrayList<>();
        contentPanel = new ContentPanel();
    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

    public void addMenuItem(SideMenuItem menuItem) {
        items.add(menuItem);
        contentPanel.addNavigItem(menuItem);
        menuItem.setIndentation(indentation);
        menuItem.setParent(this);
    }

    public void clear() {
        items.clear();
        contentPanel.clear();
    }

    public List<SideMenuItem> getMenuItems() {
        return items;
    }

    public void setVisible(boolean visible) {
        contentPanel.setVisible(visible);
    }

    public boolean isEmpty() {
        return items.size() == 0;
    }

    void setIndentation(int indentation) {
        this.indentation = indentation;
        for (SideMenuItem item : items) {
            item.setIndentation(indentation);
        }
    }

    @Override
    public SideMenuItem getParent() {
        return parent;
    }

    public void setParent(SideMenuItem parent) {
        this.parent = parent;
    }

    private class ContentPanel extends ComplexPanel {
        public ContentPanel() {
            setElement(Document.get().createElement("ul"));
            setStyleName(SideMenuTheme.StyleName.SideMenuList.name());
            setVisible(true);
        }

        public void addNavigItem(SideMenuItem menuItem) {
            add(menuItem.asWidget(), getElement());
        }

    }

    public SideMenuItem getSelectedLeaf() {
        if (items == null)
            return null;
        for (SideMenuItem item : items) {
            if (item.isSelected()) {
                return item.getSelectedLeaf();
            }
        }
        return null;
    }

    public void select(AppPlace appPlace) {
        for (SideMenuItem item : items) {
            if (getParent() == null) {
                item.select(null);
            }
        }
        for (SideMenuItem item : items) {
            item.select(appPlace);
        }
    }

}
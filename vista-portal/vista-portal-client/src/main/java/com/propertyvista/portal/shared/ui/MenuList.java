/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class MenuList implements IsWidget {

    private final ContentPanel contentPanel;

    private final List<MenuItem> items;

    public MenuList() {
        contentPanel = new ContentPanel();
        items = new ArrayList<>();
    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

    public void addMenuItem(MenuItem menuItem) {
        items.add(menuItem);
        contentPanel.addNavigItem(menuItem);
    }

    public void clear() {
        items.clear();
        contentPanel.clear();
    }

    public List<MenuItem> getMenuItems() {
        return items;
    }

    public MenuItem getMenuItem(Place place) {
        if (items == null || place == null)
            return null;
        for (MenuItem item : items) {
            if (item.getPlace().equals(place)) {
                return item;
            }
        }
        return null;
    }

    public MenuItem getSelectedMenuItem() {
        if (items == null)
            return null;
        for (MenuItem item : items) {
            if (item.isSelected()) {
                return item;
            }
        }
        return null;
    }

    public void setVisible(boolean visible) {
        contentPanel.setVisible(visible);
    }

    private class ContentPanel extends ComplexPanel {
        public ContentPanel() {
            setElement(DOM.createElement("ul"));
            setStyleName(PortalRootPaneTheme.StyleName.MainMenuHolder.name());
            setVisible(true);
        }

        public void addNavigItem(MenuItem menuItem) {
            add(menuItem.asWidget(), getElement());
        }
    }

    public boolean isEmpty() {
        return items.size() == 0;
    }

}
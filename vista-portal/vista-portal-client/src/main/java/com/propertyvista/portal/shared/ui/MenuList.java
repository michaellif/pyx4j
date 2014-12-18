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
 */
package com.propertyvista.portal.shared.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;

import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class MenuList<E extends MenuItem<?>> extends ComplexPanel {

    private final List<E> items;

    public MenuList() {
        setElement(Document.get().createElement("ul"));
        setStyleName(PortalRootPaneTheme.StyleName.MainMenuHolder.name());

        items = new ArrayList<>();
    }

    public void addMenuItem(E menuItem) {
        items.add(menuItem);
        addNavigItem(menuItem);
    }

    @Override
    public void clear() {
        items.clear();
        super.clear();
    }

    public List<E> getMenuItems() {
        return items;
    }

    public E getSelectedMenuItem() {
        if (items == null)
            return null;
        for (E item : items) {
            if (item.isSelected()) {
                return item;
            }
        }
        return null;
    }

    public void addNavigItem(E menuItem) {
        add(menuItem.asWidget(), getElement());
    }

    public boolean isEmpty() {
        return items.size() == 0;
    }

}
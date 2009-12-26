/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jun 4, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.menu;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.UIObject;

/**
 * A separator that can be placed in a {@link com.google.gwt.user.client.ui.MenuBar}.
 */
public class MenuItemSeparator extends UIObject implements IMenuItem {

    private static final String STYLENAME_DEFAULT = "gwt-MenuItemSeparator";

    private Menu parentMenu;

    /**
     * Constructs a new {@link MenuItemSeparator}.
     */
    public MenuItemSeparator() {
        setElement(DOM.createTD());
        setStyleName(STYLENAME_DEFAULT);

        // Add an inner element for styling purposes
        Element div = DOM.createDiv();
        DOM.appendChild(getElement(), div);
        setStyleName(div, "menuSeparatorInner");
    }

    /**
     * Gets the menu that contains this item.
     * 
     * @return the parent menu, or <code>null</code> if none exists.
     */
    public Menu getParentMenu() {
        return parentMenu;
    }

    public void setParentMenu(Menu parentMenu) {
        this.parentMenu = parentMenu;
    }
}
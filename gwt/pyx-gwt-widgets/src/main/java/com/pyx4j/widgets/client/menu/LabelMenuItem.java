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
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.UIObject;

public class LabelMenuItem extends UIObject implements HasHTML, IMenuItem {

    private static final String DEPENDENT_STYLENAME_SELECTED_ITEM = "selected";

    private Menu parentMenu;

    public LabelMenuItem(String text, boolean asHTML) {
        setElement(DOM.createTD());
        setSelectionStyle(false);

        if (asHTML) {
            setHTML(text);
        } else {
            setText(text);
        }
        setStyleName("gwt-MenuItem");

        DOM.setElementAttribute(getElement(), "id", DOM.createUniqueId());
        // Add a11y role "menuitem"
        Accessibility.setRole(getElement(), Accessibility.ROLE_MENUITEM);
    }

    public Menu getParentMenu() {
        return parentMenu;
    }

    public void setParentMenu(Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

    public String getHTML() {
        return DOM.getInnerHTML(getElement());
    }

    public String getText() {
        return DOM.getInnerText(getElement());
    }

    public void setHTML(String html) {
        DOM.setInnerHTML(getElement(), html);
    }

    public void setText(String text) {
        DOM.setInnerText(getElement(), text);
    }

    void setSelectionStyle(boolean selected) {
        if (selected) {
            addStyleDependentName(DEPENDENT_STYLENAME_SELECTED_ITEM);
        } else {
            removeStyleDependentName(DEPENDENT_STYLENAME_SELECTED_ITEM);
        }
    }
}

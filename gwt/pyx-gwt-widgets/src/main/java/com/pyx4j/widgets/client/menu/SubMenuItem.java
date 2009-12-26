/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jun 4, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.menu;

import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.impl.FocusImpl;

public class SubMenuItem extends LabelMenuItem {

    private final Menu subMenu;

    public SubMenuItem(String text, Menu subMenu) {
        this(text, false, subMenu);
    }

    public SubMenuItem(String text, boolean asHTML, Menu subMenu) {
        super(text, asHTML);
        this.subMenu = subMenu;
        //        if (this.parentMenu != null) {
        //            this.parentMenu.updateSubmenuIcon(this);
        //        }

        // Change tab index from 0 to -1, because only the root menu is supposed to
        // be in the tab order
        FocusImpl.getFocusImplForPanel().setTabIndex(subMenu.getElement(), -1);

        // Update a11y role "haspopup"
        Accessibility.setState(this.getElement(), Accessibility.STATE_HASPOPUP, "true");
    }

    public Menu getSubMenu() {
        return subMenu;
    }

    /**
     * Also sets the Debug IDs of MenuItems in the submenu of this {@link ActionMenuItem}
     * if a submenu exists.
     * 
     * @see UIObject#onEnsureDebugId(String)
     */
    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        if (subMenu != null) {
            subMenu.setMenuItemDebugIds(baseID);
        }
    }

}
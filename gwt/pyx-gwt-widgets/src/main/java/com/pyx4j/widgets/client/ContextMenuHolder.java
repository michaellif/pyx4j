/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Mar 25, 2016
 * @author vlads
 */
package com.pyx4j.widgets.client;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.gwt.commons.HandlerRegistrationGC;
import com.pyx4j.gwt.commons.concerns.HasWidgetConcerns;
import com.pyx4j.gwt.commons.ui.SimplePanel;

public class ContextMenuHolder extends SimplePanel {

    private final MenuBar menuBar;

    private MenuItem menuItem;

    private final HandlerRegistrationGC hrgc = new HandlerRegistrationGC();

    public ContextMenuHolder() {
        setSize("0", "0");
        getStyle().setProperty("position", "relative");

        menuBar = new MenuBar(false);
        if (HasWidgetConcerns.debugMenuConcerns) {
            menuBar.setTitle("ContextMenuBar");
        }
        add(menuBar);
        menuBar.getStyle().setProperty("position", "relative");
        menuBar.setSize("0", "0");

        // This is magical number to work in CRM, Make a better positioning of Popup
        menuBar.getStyle().setPropertyPx("top", 2);

        hrgc.add(Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                positionPopup();
            }
        }));
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        hrgc.removeHandler();
    }

    public MenuBar getMenu() {
        return menuBar;
    }

    public void setMenu(MenuBar subMenu) {
        menuBar.clearItems();
        if (subMenu != null) {
            menuBar.addItem(menuItem = new MenuItem(HasWidgetConcerns.debugMenuConcerns ? "ContextSubMenu" : "", subMenu));
            menuItem.setSize("0", "0");
        } else {
            menuItem = null;
        }
    }

    public void togleMenu() {
        if (menuBar.getPopupPanel() != null && menuBar.getPopupPanel().isShowing()) {
            menuBar.getPopupPanel().hide();
        } else {
            openMenu();
        }
    }

    public void openMenu() {
        positionBelowButton();
        menuBar.openItem(menuItem);
        sizeToButton();
    }

    private void positionPopup() {
        if (menuBar.getPopupPanel() != null && menuBar.getPopupPanel().isShowing()) {
            // Just hide it , since position API is private
            menuBar.getPopupPanel().hide();
        }
    }

    // The trickiest part to position popup below button lower edge, done in constructor as 'top'
    private void positionBelowButton() {
        Widget relativeTo = this.getParent();
        if (relativeTo.getParent() instanceof Button) {
            Widget button = relativeTo.getParent();
            int left = button.getAbsoluteLeft() - relativeTo.getAbsoluteLeft();
            menuBar.getStyle().setPropertyPx("left", left);
        }
    }

    private void sizeToButton() {
        Widget relativeTo = this.getParent();
        if (relativeTo.getParent() instanceof Button) {
            Widget button = relativeTo.getParent();
            menuBar.getPopupPanel().getWidget().getElement().getStyle().setPropertyPx("minWidth", button.getOffsetWidth());
        }
    }

}

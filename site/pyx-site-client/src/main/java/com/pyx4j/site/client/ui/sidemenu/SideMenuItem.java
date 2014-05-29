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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.images.ButtonImages;

public class SideMenuItem implements IsWidget {

    private final ContentPanel contentPanel;

    private final Image icon;

    private final Label label;

    private boolean selected;

    private final Command command;

    private final ButtonImages images;

    private final SideMenuList submenu;

    private final FlowPanel itemPanel;

    private int indentation = 0;

    public SideMenuItem(final Command command, SideMenuList submenu, String caption, ButtonImages images) {
        super();
        this.command = command;
        this.submenu = submenu;
        this.images = images;

        contentPanel = new ContentPanel();
        selected = false;

        itemPanel = new FlowPanel();
        itemPanel.setStyleName(SideMenuTheme.StyleName.SideMenuItemPanel.name());
        contentPanel.add(itemPanel);

        icon = new Image(images.regular());
        icon.setStyleName(SideMenuTheme.StyleName.SideMenuIcon.name());
        itemPanel.add(icon);

        label = new Label(caption);
        label.setStyleName(SideMenuTheme.StyleName.SideMenuLabel.name());
        itemPanel.add(label);

        if (submenu != null) {
            contentPanel.add(submenu);
            setIndentation(indentation);
        }
    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

    public void setSelected(boolean select) {
        selected = select;
        if (select) {
            contentPanel.addStyleDependentName(SideMenuTheme.StyleDependent.active.name());
            icon.setResource(images.active());
        } else {
            contentPanel.removeStyleDependentName(SideMenuTheme.StyleDependent.active.name());
            icon.setResource(images.regular());
        }
    }

    public Label getLabel() {
        return label;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setVisible(boolean visible) {
        contentPanel.setVisible(visible);
    }

    private class ContentPanel extends ComplexPanel {
        private ContentPanel() {
            setElement(Document.get().createElement("li"));
            setStyleName(SideMenuTheme.StyleName.SideMenuItem.name());
            sinkEvents(Event.ONCLICK);
            getElement().getStyle().setCursor(Cursor.POINTER);
            addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (command != null) {
                        command.execute();
                    }
                    LayoutType layout = LayoutType.getLayoutType(Window.getClientWidth());
                    if (LayoutType.phonePortrait.equals(layout) || (LayoutType.phoneLandscape.equals(layout))) {
                        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideMenu));
                    }
                }
            }, ClickEvent.getType());
        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }

    }

    void setIndentation(int indentation) {
        this.indentation = indentation;
        itemPanel.getElement().getStyle().setPaddingLeft(20 + indentation * 20, Unit.PX);
        if (submenu != null) {
            submenu.setIndentation(indentation + 1);
        }
    }

}
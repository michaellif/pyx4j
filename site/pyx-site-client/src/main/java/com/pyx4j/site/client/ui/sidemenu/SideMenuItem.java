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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.images.ButtonImages;

public class SideMenuItem implements ISideMenuNode {

    private final ContentPanel contentPanel;

    private Image icon;

    private final Label label;

    private boolean selected;

    private boolean expanded;

    private Command command;

    private final ButtonImages images;

    private SideMenuList submenu;

    private final FlowPanel itemPanel;

    private int indentation = 0;

    private SideMenuList parent;

    public SideMenuItem(final Command command, String caption, ButtonImages images) {
        super();
        this.command = command;
        this.images = images;

        contentPanel = new ContentPanel();
        selected = false;

        itemPanel = new FlowPanel();
        itemPanel.setStyleName(SideMenuTheme.StyleName.SideMenuItemPanel.name());
        contentPanel.add(itemPanel);

        itemPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (SideMenuItem.this.command != null) {
                    SideMenuItem.this.command.execute();
                }
                LayoutType layout = LayoutType.getLayoutType(Window.getClientWidth());
                if (LayoutType.phonePortrait.equals(layout) || (LayoutType.phoneLandscape.equals(layout))) {
                    AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideMenu));
                }
            }
        }, ClickEvent.getType());

        if (images != null) {
            icon = new Image(images.regular());
            icon.setStyleName(SideMenuTheme.StyleName.SideMenuIcon.name());
            itemPanel.add(icon);
        }

        label = new Label(caption);
        label.setStyleName(SideMenuTheme.StyleName.SideMenuLabel.name());
        itemPanel.add(label);

    }

    public SideMenuItem(SideMenuList submenu, String caption, ButtonImages images) {
        this((Command) null, caption, images);
        this.submenu = submenu;
        if (submenu != null) {
            contentPanel.add(submenu);
            submenu.setParent(this);
            setIndentation(indentation);
        }
        this.command = new Command() {
            @Override
            public void execute() {
                setExpanded(!expanded);
            }
        };
        setExpanded(false);
    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

    public void setSelected(boolean select) {
        selected = select;
        if (select) {
            itemPanel.addStyleDependentName(SideMenuTheme.StyleDependent.active.name());
            if (images != null) {
                icon.setResource(images.active());
            }
            setExpanded(true);
        } else {
            itemPanel.removeStyleDependentName(SideMenuTheme.StyleDependent.active.name());
            if (images != null) {
                icon.setResource(images.regular());
            }
        }
        if (getParent().getParent() != null) {
            getParent().getParent().setSelected(select);
        }
    }

    public void setExpanded(boolean expanded) {
        if (submenu != null) {
            submenu.setVisible(expanded);
            this.expanded = expanded;
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public Label getLabel() {
        return label;
    }

    public void setVisible(boolean visible) {
        contentPanel.setVisible(visible);
    }

    void setIndentation(int indentation) {
        this.indentation = indentation;
        itemPanel.removeStyleDependentName(SideMenuTheme.StyleDependent.l1.name());
        itemPanel.removeStyleDependentName(SideMenuTheme.StyleDependent.l2.name());
        itemPanel.removeStyleDependentName(SideMenuTheme.StyleDependent.l3.name());
        switch (indentation) {
        case 0:
            itemPanel.addStyleDependentName(SideMenuTheme.StyleDependent.l1.name());
            break;
        case 1:
            itemPanel.addStyleDependentName(SideMenuTheme.StyleDependent.l2.name());
            break;
        case 2:
            itemPanel.addStyleDependentName(SideMenuTheme.StyleDependent.l3.name());
            break;
        default:
            break;
        }

        if (submenu != null) {
            submenu.setIndentation(indentation + 1);
        }
    }

    @Override
    public SideMenuList getParent() {
        return parent;
    }

    public void setParent(SideMenuList parent) {
        this.parent = parent;
    }

    private class ContentPanel extends ComplexPanel {
        private ContentPanel() {
            setElement(Document.get().createElement("li"));
            setStyleName(SideMenuTheme.StyleName.SideMenuItem.name());
            sinkEvents(Event.ONCLICK);
            getElement().getStyle().setCursor(Cursor.POINTER);
        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }

    }

    public void select(AppPlace appPlace) {
        if (submenu != null) {
            submenu.select(appPlace);
        }
    }

}
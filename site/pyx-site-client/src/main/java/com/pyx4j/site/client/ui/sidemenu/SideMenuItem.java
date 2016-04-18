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
 */
package com.pyx4j.site.client.ui.sidemenu;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.Image;
import com.pyx4j.gwt.commons.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.gwt.commons.concerns.AbstractConcern;
import com.pyx4j.gwt.commons.concerns.HasWidgetConcerns;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.images.ButtonImages;

public class SideMenuItem implements ISideMenuNode, HasWidgetConcerns {

    private final ContentPanel contentPanel;

    private Image icon;

    private final Label label;

    private boolean selected;

    private SideMenuCommand command;

    private final ButtonImages images;

    private final FlowPanel itemPanel;

    private SideMenuList parent;

    protected final List<AbstractConcern> concerns = new ArrayList<>();

    public SideMenuItem(final SideMenuCommand command, String caption, final ButtonImages images, Permission... permission) {
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
                    boolean hideSideMenu = SideMenuItem.this.command.execute();
                    if (hideSideMenu) {
                        LayoutType layout = LayoutType.getLayoutType(Window.getClientWidth());
                        if (LayoutType.phonePortrait.equals(layout) || LayoutType.phoneLandscape.equals(layout) || LayoutType.tabletPortrait.equals(layout)) {
                            AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideMenu));
                        }
                    }
                }
            }
        }, ClickEvent.getType());

        itemPanel.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                if (!selected && images != null) {
                    icon.setResource(images.hover());
                }
            }
        }, MouseOverEvent.getType());

        itemPanel.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                if (!selected && images != null) {
                    icon.setResource(images.regular());
                }
            }
        }, MouseOutEvent.getType());

        if (images != null) {
            icon = new Image(images.regular());
            icon.setStyleName(SideMenuTheme.StyleName.SideMenuIcon.name());
            itemPanel.add(icon);
        }

        label = new Label(caption);
        label.setStyleName(SideMenuTheme.StyleName.SideMenuLabel.name());
        itemPanel.add(label);

        setPermission(permission);
    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

    public void setDebugId(IDebugId debugId) {
        itemPanel.ensureDebugId(debugId.debugId());
    }

    public void setCaption(String text) {
        label.setText(text);
    }

    protected void setCommand(SideMenuCommand command) {
        this.command = command;
    }

    public void setSelected(boolean select) {
        selected = select;
        if (select) {
            itemPanel.addStyleDependentName(SideMenuTheme.StyleDependent.active.name());
            if (images != null) {
                icon.setResource(images.active());
            }
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

    public boolean isSelected() {
        return selected;
    }

    @Override
    public void applyVisibilityRules() {
        if (contentPanel.isAttached()) {
            contentPanel.setVisible(HasWidgetConcerns.super.isVisible());
        }
    }

    public void setPermission(Permission... permission) {
        setVisibilityPermission(permission);
    }

    @Override
    public List<AbstractConcern> concerns() {
        return concerns;
    }

    @Override
    public SideMenuList getParent() {
        return parent;
    }

    public void setParent(SideMenuList parent) {
        this.parent = parent;
    }

    protected void setIndentation(int indentation) {
        contentPanel.removeStyleDependentName(SideMenuTheme.StyleDependent.l1.name());
        contentPanel.removeStyleDependentName(SideMenuTheme.StyleDependent.l2.name());
        contentPanel.removeStyleDependentName(SideMenuTheme.StyleDependent.l3.name());
        switch (indentation) {
        case 0:
            contentPanel.addStyleDependentName(SideMenuTheme.StyleDependent.l1.name());
            break;
        case 1:
            contentPanel.addStyleDependentName(SideMenuTheme.StyleDependent.l2.name());
            break;
        case 2:
            contentPanel.addStyleDependentName(SideMenuTheme.StyleDependent.l3.name());
            break;
        default:
            break;
        }

    }

    class ContentPanel extends ComplexPanel {
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
    }

    @Override
    public String toString() {
        return label.getText();
    }

    ContentPanel getContentPanel() {
        return contentPanel;
    }

    FlowPanel getItemPanel() {
        return itemPanel;
    }

    @Override
    public void applyEnablingRules() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        throw new UnsupportedOperationException();

    }

}
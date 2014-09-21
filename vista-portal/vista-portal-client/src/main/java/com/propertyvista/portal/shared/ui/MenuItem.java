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

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;

import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class MenuItem<ICON extends IsWidget> extends ComplexPanel {

    private final ICON icon;

    private final Label label;

    private boolean selected;

    private boolean enabled = true;

    private final String color;

    public MenuItem(String caption, final Command command, ICON icon, ThemeColor color) {
        super();
        setElement(Document.get().createElement("li"));
        setStyleName(PortalRootPaneTheme.StyleName.MainMenuNavigItem.name());
        sinkEvents(Event.ONCLICK);

        addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (enabled) {
                    command.execute();
                }
            }
        }, ClickEvent.getType());

        this.color = StyleManager.getPalette().getThemeColor(color, 1);
        selected = false;

        this.icon = icon;
        add(icon);

        label = new Label(caption);
        label.setStyleName(PortalRootPaneTheme.StyleName.MainMenuLabel.name());
        add(label);
    }

    public ICON getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public void setSelected(boolean select) {
        selected = select;
        if (select) {
            addStyleDependentName(PortalRootPaneTheme.StyleDependent.active.name());
            getElement().getStyle().setProperty("background", color);
            label.getElement().getStyle().setProperty("background", color);
        } else {
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.active.name());
            getElement().getStyle().setProperty("background", "");
            label.getElement().getStyle().setProperty("background", "");
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.disabled.name());
        } else {
            addStyleDependentName(PortalRootPaneTheme.StyleDependent.disabled.name());
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public void add(Widget w) {
        super.add(w, getElement());
    }

}
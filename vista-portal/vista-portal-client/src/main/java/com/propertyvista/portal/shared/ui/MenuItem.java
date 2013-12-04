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

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.images.ButtonImages;

import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class MenuItem implements IsWidget {

    private final ContentPanel contentPanel;

    private final Image icon;

    private final Label label;

    private boolean selected;

    private final AppPlace appPlace;

    private final ButtonImages images;

    private final String color;

    public MenuItem(final AppPlace appPlace, ButtonImages images, ThemeColor color) {
        super();

        contentPanel = new ContentPanel();

        this.appPlace = appPlace;
        this.images = images;
        this.color = StyleManager.getPalette().getThemeColor(color, 1);
        selected = false;

        icon = new Image(images.regular());

        icon.setStyleName(PortalRootPaneTheme.StyleName.MainMenuIcon.name());
        contentPanel.add(icon);

        label = new Label(AppSite.getHistoryMapper().getPlaceInfo(appPlace).getNavigLabel());
        label.setStyleName(PortalRootPaneTheme.StyleName.MainMenuLabel.name());
        contentPanel.add(label);

    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

    public void setSelected(boolean select) {
        selected = select;
        if (select) {
            contentPanel.addStyleDependentName(PortalRootPaneTheme.StyleDependent.active.name());
            contentPanel.getElement().getStyle().setProperty("background", color);
            label.getElement().getStyle().setProperty("background", color);
            icon.setResource(images.active());
        } else {
            contentPanel.removeStyleDependentName(PortalRootPaneTheme.StyleDependent.active.name());
            contentPanel.getElement().getStyle().setProperty("background", "");
            label.getElement().getStyle().setProperty("background", "");
            icon.setResource(images.regular());
        }
    }

    public Label getLabel() {
        return label;
    }

    public boolean isSelected() {
        return selected;
    }

    public AppPlace getPlace() {
        return appPlace;
    }

    private class ContentPanel extends ComplexPanel {
        private ContentPanel() {
            setElement(DOM.createElement("li"));
            setStyleName(PortalRootPaneTheme.StyleName.MainMenuNavigItem.name());
            sinkEvents(Event.ONCLICK);
            getElement().getStyle().setCursor(Cursor.POINTER);
            addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    AppSite.getPlaceController().goTo(appPlace);
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

}
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
package com.propertyvista.portal.prospect.ui.application;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;
import com.propertyvista.portal.shared.themes.StepsTheme;

public class NavigStepItem implements IsWidget {

    public static enum StepStatus {
        notComplete, complete, invalid, current
    }

    private final ContentPanel contentPanel;

    private final StepIndexLabel positionLabel;

    private final Label titleLabel;

    private final Command command;

    private final StepStatus status;

    private boolean selected;

    private final String color;

    public NavigStepItem(Command command, String title, int index, StepStatus status) {
        super();

        this.command = command;
        this.status = status;

        contentPanel = new ContentPanel();

        this.color = StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1);
        selected = false;

        positionLabel = new StepIndexLabel(String.valueOf(index + 1));
        contentPanel.add(positionLabel);

        titleLabel = new Label(title);
        titleLabel.setStyleName(PortalRootPaneTheme.StyleName.MainMenuLabel.name());
        contentPanel.add(titleLabel);

        updateStatus();

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
            titleLabel.getElement().getStyle().setProperty("background", color);
            positionLabel.getElement().getStyle().setProperty("background", color);
        } else {
            contentPanel.removeStyleDependentName(PortalRootPaneTheme.StyleDependent.active.name());
            contentPanel.getElement().getStyle().setProperty("background", "");
            titleLabel.getElement().getStyle().setProperty("background", "");
            positionLabel.getElement().getStyle().setProperty("background", "");
        }
    }

    void updateStatus() {
        positionLabel.setStatus(status);

        switch (status) {
        case current:
            contentPanel.addStyleDependentName(PortalRootPaneTheme.StyleDependent.active.name());
            contentPanel.getElement().getStyle().setProperty("background", color);
            titleLabel.getElement().getStyle().setProperty("background", color);
            break;
        default:
            contentPanel.removeStyleDependentName(PortalRootPaneTheme.StyleDependent.active.name());
            contentPanel.getElement().getStyle().setProperty("background", "");
            titleLabel.getElement().getStyle().setProperty("background", "");
            break;

        }
    }

    public Label getLabel() {
        return titleLabel;
    }

    public boolean isSelected() {
        return selected;
    }

    private class ContentPanel extends ComplexPanel {
        private ContentPanel() {
            setElement(DOM.createElement("li"));
            setStyleName(PortalRootPaneTheme.StyleName.MainMenuNavigItem.name());
            sinkEvents(Event.ONCLICK);

            //TODO if (stepIndex > component.getSelectedIndex()) {
            if (false) {
                getElement().getStyle().setCursor(Cursor.DEFAULT);
            } else {
                addDomHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        command.execute();
                        LayoutType layout = LayoutType.getLayoutType(Window.getClientWidth());
                        if (LayoutType.phonePortrait.equals(layout) || (LayoutType.phoneLandscape.equals(layout))) {
                            AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideMenu));
                        }
                    }
                }, ClickEvent.getType());
                getElement().getStyle().setCursor(Cursor.POINTER);
            }

        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }
    }

}
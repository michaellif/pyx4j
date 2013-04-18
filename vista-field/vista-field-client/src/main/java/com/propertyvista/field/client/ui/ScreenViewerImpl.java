/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 13, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.field.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.DisplayPanel;

import com.propertyvista.field.client.event.EventSource;
import com.propertyvista.field.client.theme.FieldTheme;
import com.propertyvista.field.client.ui.components.alerts.AlertsInfoView;
import com.propertyvista.field.client.ui.components.alerts.AlertsScreenView;
import com.propertyvista.field.client.ui.components.menu.MenuScreenView;
import com.propertyvista.field.client.ui.viewfactories.FieldViewFactory;
import com.propertyvista.field.rpc.ScreenMode.ScreenLayout;

public class ScreenViewerImpl extends FlowPanel implements ScreenViewer {

    private final DisplayPanel header;

    private final DisplayPanel lister;

    private final DisplayPanel details;

    private final DisplayPanel fullScreen;

    private final DisplayPanel menuBackground;

    private final DisplayPanel alertsBackground;

    private final DisplayPanel alertsInfo;

    private final DockLayoutPanel overlap;

    private ScreenLayout layout;

    private ScreenPosition overlapPosition;

    public ScreenViewerImpl() {
        setSize("100%", "100%");

        header = initDisplay();
        lister = initDisplay();
        details = initDisplay();
        fullScreen = initDisplay();

        menuBackground = initDisplay(FieldTheme.StyleName.MenuScreen, FieldViewFactory.instance(MenuScreenView.class));
        alertsBackground = initDisplay(FieldTheme.StyleName.AlertsScreen, FieldViewFactory.instance(AlertsScreenView.class));
        alertsInfo = initDisplay(FieldTheme.StyleName.AlertsInfo, FieldViewFactory.instance(AlertsInfoView.class), false);
        alertsInfo.setVisible(false);

        overlap = new DockLayoutPanel(Unit.PCT);
        overlap.setSize("100%", "100%");
        overlap.addNorth(header, 10);
        overlap.addWest(lister, 100);
        overlap.addEast(details, 0);
        overlap.addSouth(fullScreen, 100);

        overlapPosition = ScreenPosition.NORMAL;
        overlap.setStyleName(FieldTheme.StyleName.OverlapScreenNormal.name());

        add(menuBackground);
        add(alertsBackground);
        add(alertsInfo);
        add(overlap);
    }

    @Override
    public DisplayPanel getHeaderDisplay() {
        return header;
    }

    @Override
    public DisplayPanel getListerDisplay() {
        return lister;
    }

    @Override
    public DisplayPanel getDetailsDisplay() {
        return details;
    }

    @Override
    public DisplayPanel getFullScreenDisplay() {
        return fullScreen;
    }

    private DisplayPanel initDisplay() {
        DisplayPanel display = new DisplayPanel();
        display.setSize("100%", "100%");
        return display;
    }

    private DisplayPanel initDisplay(FieldTheme.StyleName style, IsWidget widget) {
        return initDisplay(style, widget, true);
    }

    private DisplayPanel initDisplay(FieldTheme.StyleName style, IsWidget widget, boolean setSize) {
        DisplayPanel display = new DisplayPanel();
        if (setSize) {
            display.setSize("100%", "100%");
        }
        display.setStyleName(style.name());
        display.setWidget(widget);
        return display;
    }

    @Override
    public void setWidget(IsWidget widget) {
        if (ScreenLayout.FullScreen == layout) {
            fullScreen.setWidget(widget);
        }
    }

    private void hideDisplays() {
        overlap.setWidgetHidden(header, true);
        overlap.setWidgetHidden(lister, true);
        overlap.setWidgetHidden(details, true);
        overlap.setWidgetHidden(fullScreen, true);
        alertsBackground.setVisible(false);
    }

    @Override
    public void setScreenLayout(ScreenLayout layout) {
        this.layout = layout;

        switch (layout) {
        case FullScreen:
            initFullScreen();
            break;
        case HeaderLister:
            initHeaderLister();
            break;
        case HeaderListerDetails:
            initHeaderListerDetails();
            break;

        default:
            break;
        }
    }

    private void initFullScreen() {
        hideDisplays();
        overlap.setWidgetHidden(fullScreen, false);
    }

    private void initHeaderLister() {
        hideDisplays();
        overlap.setWidgetHidden(header, false);
        overlap.setWidgetHidden(lister, false);
    }

    private void initHeaderListerDetails() {
        hideDisplays();
        overlap.setWidgetHidden(header, false);
        overlap.setWidgetHidden(lister, false);
        overlap.setWidgetHidden(details, false);
    }

    @Override
    public void shiftScreen(EventSource eventSource) {
        overlapPosition = changeScreenPosition(eventSource);
        overlap.setStyleName(chooseStyle(overlapPosition, eventSource));

        alertsBackground.setVisible(eventSource == EventSource.AlertsImage && overlapPosition == ScreenPosition.SHIFTED);
    }

    private ScreenPosition changeScreenPosition(EventSource eventSource) {
        return overlapPosition == ScreenPosition.NORMAL ? ScreenPosition.SHIFTED : ScreenPosition.NORMAL;
    }

    @Override
    public void showAlerts() {
        alertsInfo.setVisible(true);
    }

    private String chooseStyle(ScreenPosition position, EventSource eventSource) {
        String style = FieldTheme.StyleName.OverlapScreenNormal.name();

        if (ScreenPosition.SHIFTED == position) {
            switch (eventSource) {
            case ToolbarMenuImage:
                style = FieldTheme.StyleName.OverlapScreenShiftedRight.name();
                break;
            case AlertsImage:
                style = FieldTheme.StyleName.OverlapScreenShiftedLeft.name();
                break;
            }
        }

        return style;
    }
}

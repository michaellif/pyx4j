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

import com.propertyvista.field.client.theme.FieldTheme;
import com.propertyvista.field.client.ui.components.menu.MenuScreenView;
import com.propertyvista.field.client.ui.viewfactories.FieldViewFactory;
import com.propertyvista.field.rpc.ScreenMode.ScreenLayout;

public class ScreenViewerImpl extends FlowPanel implements ScreenViewer {

    private final DisplayPanel header;

    private final DisplayPanel lister;

    private final DisplayPanel details;

    private final DisplayPanel fullScreen;

    private final DisplayPanel background;

    private final DockLayoutPanel overlap;

    private ScreenLayout layout;

    private ScreenPosition overlapPosition;

    public ScreenViewerImpl() {
        setSize("100%", "100%");

        header = initDisplay();
        lister = initDisplay();
        details = initDisplay();
        fullScreen = initDisplay();

        background = initBackgroundDisplay();

        overlap = new DockLayoutPanel(Unit.PCT);
        overlap.setSize("100%", "100%");
        overlap.addNorth(header, 10);
        overlap.addWest(lister, 100);
        overlap.addEast(details, 0);
        overlap.addSouth(fullScreen, 100);
        setOverlapStyle(ScreenPosition.NORMAL);

        add(background);
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

    private DisplayPanel initBackgroundDisplay() {
        DisplayPanel display = new DisplayPanel();
        display.setSize("100%", "100%");
        display.setStyleName(FieldTheme.StyleName.MenuScreen.name());
        display.setWidget(FieldViewFactory.instance(MenuScreenView.class));
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
    public void shiftScreen() {
        setOverlapStyle(overlapPosition == ScreenPosition.NORMAL ? ScreenPosition.SHIFTED : ScreenPosition.NORMAL);
    }

    private void setOverlapStyle(ScreenPosition position) {
        this.overlapPosition = position;
        overlap.setStyleName(position == ScreenPosition.NORMAL ? FieldTheme.StyleName.OverlapScreenNormal.name() : FieldTheme.StyleName.OverlapScreenShifted
                .name());
    }
}

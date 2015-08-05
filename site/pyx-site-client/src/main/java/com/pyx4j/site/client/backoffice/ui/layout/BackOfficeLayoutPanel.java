/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Apr 22, 2013
 * @author michaellif
 */
package com.pyx4j.site.client.backoffice.ui.layout;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.BackOfficeDevConsole;
import com.pyx4j.site.client.ui.layout.OverlayExtraHolder;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel;
import com.pyx4j.site.client.ui.layout.SidePanelHolder;
import com.pyx4j.widgets.client.DropDownPanel;

public class BackOfficeLayoutPanel extends ResponsiveLayoutPanel {

    private final DockLayoutPanel pageHolder;

    private final SidePanelHolder sideMenuHolder;

    private final DropDownPanel popupCommHolder;

    private final SidePanelHolder sideCommHolder;

    private final InlineNavigationHolder inlineNavigationHolder;

    private boolean sideMenuVisible = false;

    private boolean sideCommVisible = false;

    private final InlineExtraHolder inlineExtraHolder;

    private final OverlayExtraHolder overlayExtraHolder;

    public BackOfficeLayoutPanel(String extra1Caption, String extra2Caption, String extra4Caption) {

        pageHolder = new DockLayoutPanel(Unit.PX);

        pageHolder.addNorth(getDisplay(DisplayType.header), 0);

        pageHolder.addNorth(getDisplay(DisplayType.notification), 0);

        pageHolder.addEast(inlineExtraHolder = new InlineExtraHolder(this, extra1Caption, extra2Caption), 0);

        inlineNavigationHolder = new InlineNavigationHolder(this);

        pageHolder.addWest(inlineNavigationHolder, 200);

        overlayExtraHolder = new OverlayExtraHolder(this, extra1Caption, extra2Caption, extra4Caption, new BackOfficeDevConsole(this));

        ContentHolder contentHolder = new ContentHolder(this, overlayExtraHolder);

        pageHolder.add(contentHolder);

        popupCommHolder = new DropDownPanel();

        // ============ Content ============
        {

            Layer layer = getLayout().attachChild(pageHolder.getElement(), pageHolder);
            pageHolder.setLayoutData(layer);
            layer.setTopBottom(0, Unit.PX, 0, Unit.PX);
            getChildren().add(pageHolder);
            adopt(pageHolder);
        }

        // ============ Side Menu Layer ============
        {

            sideMenuHolder = new SidePanelHolder();
            Layer layer = getLayout().attachChild(sideMenuHolder.asWidget().getElement(), sideMenuHolder);
            sideMenuHolder.setLayoutData(layer);
            getChildren().add(sideMenuHolder);
            adopt(sideMenuHolder);
        }

        // ============ Side Communication Layer ============
        {

            sideCommHolder = new SidePanelHolder();
            Layer layer = getLayout().attachChild(sideCommHolder.asWidget().getElement(), sideCommHolder);
            sideCommHolder.setLayoutData(layer);
            getChildren().add(sideCommHolder);
            adopt(sideCommHolder);
        }

        AppSite.getEventBus().addHandler(LayoutChangeRequestEvent.TYPE, this);

        forceLayout(0);

    }

    @Override
    protected void doLayout() {
        switch (getLayoutType()) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            sideMenuHolder.setDisplay(getDisplay(DisplayType.menu));
            sideCommHolder.setDisplay(getDisplay(DisplayType.communication));
            pageHolder.setWidgetSize(inlineNavigationHolder, 0);
            break;
        default:
            setSideMenuVisible(false);
            setSideCommVisible(false);
            popupCommHolder.setWidget(getDisplay(DisplayType.communication));
            inlineNavigationHolder.layout();
            if (inlineNavigationHolder.isEmpty()) {
                pageHolder.setWidgetSize(inlineNavigationHolder, 0);
            } else {
                pageHolder.setWidgetSize(inlineNavigationHolder, 200);
            }
            break;
        }

        inlineExtraHolder.layout();
        pageHolder.setWidgetSize(inlineExtraHolder, inlineExtraHolder.isEmpty() ? 0 : 200);
        overlayExtraHolder.layout();

        Layer menuLayer = (Layer) sideMenuHolder.getLayoutData();
        Layer commLayer = (Layer) sideCommHolder.getLayoutData();
        Layer mainLayer = (Layer) pageHolder.getLayoutData();

        if (sideMenuVisible) {
            menuLayer.setLeftWidth(0.0, Unit.PCT, 60.0, Unit.PCT);
            commLayer.setLeftWidth(160.0, Unit.PCT, 60.0, Unit.PCT);
            mainLayer.setLeftWidth(60.0, Unit.PCT, 100.0, Unit.PCT);
        } else if (sideCommVisible) {
            menuLayer.setLeftWidth(-120.0, Unit.PCT, 60.0, Unit.PCT);
            commLayer.setLeftWidth(40.0, Unit.PCT, 60.0, Unit.PCT);
            mainLayer.setLeftWidth(-60.0, Unit.PCT, 100.0, Unit.PCT);
        } else {
            menuLayer.setLeftWidth(-60.0, Unit.PCT, 60.0, Unit.PCT);
            commLayer.setLeftWidth(100.0, Unit.PCT, 60.0, Unit.PCT);
            mainLayer.setLeftWidth(0.0, Unit.PCT, 100.0, Unit.PCT);
        }
    }

    private boolean isSideMenuEnabled() {
        LayoutType layoutType = LayoutType.getLayoutType(Window.getClientWidth());
        return LayoutType.phonePortrait == layoutType || LayoutType.phoneLandscape == layoutType || LayoutType.tabletPortrait == layoutType;
    }

    private boolean isSideCommEnabled() {
        LayoutType layoutType = LayoutType.getLayoutType(Window.getClientWidth());
        return LayoutType.phonePortrait == layoutType || LayoutType.phoneLandscape == layoutType || LayoutType.tabletPortrait == layoutType;
    }

    private boolean isPopupCommEnabled() {
        LayoutType layoutType = LayoutType.getLayoutType(Window.getClientWidth());
        return !(LayoutType.phonePortrait == layoutType || LayoutType.phoneLandscape == layoutType || LayoutType.tabletPortrait == layoutType);
    }

    private void setSideMenuVisible(boolean visible) {
        if (this.sideMenuVisible != visible) {
            this.sideMenuVisible = visible;
            forceLayout(ResponsiveLayoutPanel.ANIMATION_TIME);
        }
    }

    private void setSideCommVisible(boolean visible) {
        if (this.sideCommVisible != visible) {
            this.sideCommVisible = visible;
            forceLayout(ResponsiveLayoutPanel.ANIMATION_TIME);
        }
    }

    public boolean isSideCommVisible() {
        return sideCommVisible;
    }

    private void togglePopupCommVisible(final Widget anchor) {
        if (!popupCommHolder.isShowing()) {
            popupCommHolder.showRelativeTo(anchor, new PositionCallback() {
                @Override
                public void setPosition(int offsetWidth, int offsetHeight) {
                    popupCommHolder.setPopupPosition(anchor.getAbsoluteLeft() + anchor.getOffsetWidth() - popupCommHolder.getOffsetWidth(),
                            anchor.getAbsoluteTop() + anchor.getOffsetHeight());
                }
            });
        } else {
            popupCommHolder.hide();
        }
    }

    public boolean isPopupCommVisible() {
        return popupCommHolder.isShowing();
    }

    public void setHeaderHeight(int height) {
        pageHolder.setWidgetSize(getDisplay(DisplayType.header), height);
    }

    public void setNotificationsHeight(int height) {
        pageHolder.setWidgetSize(getDisplay(DisplayType.notification), height);
    }

    public void setMenuVisible(boolean visible) {
        //TODO
    }

    @Override
    public void resizeComponents() {

        for (Widget child : getChildren()) {
            if (child instanceof RequiresResize) {
                ((RequiresResize) child).onResize();
            }
        }
    }

    @Override
    public void onLayoutChangeRequest(LayoutChangeRequestEvent event) {
        switch (event.getChangeType()) {
        case hideSideMenu:
            if (isSideMenuEnabled()) {
                setSideMenuVisible(false);
            }
            break;
        case toggleSideMenu:
            if (isSideMenuEnabled()) {
                setSideMenuVisible(!sideMenuVisible);
            }
            break;
        case toggleSideComm:
            if (isSideCommEnabled()) {
                setSideCommVisible(!sideCommVisible);
            }
            break;
        case togglePopupComm:
            if (isPopupCommEnabled()) {
                togglePopupCommVisible(event.getPopupCommAnchor());
            }
            break;
        case resizeComponents:
            resizeComponents();
            break;
        default:
            break;
        }
    }

}

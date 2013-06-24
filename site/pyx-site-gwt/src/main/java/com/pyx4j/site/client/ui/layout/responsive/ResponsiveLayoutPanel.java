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
 * @version $Id$
 */
package com.pyx4j.site.client.ui.layout.responsive;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.DisplayPanel;
import com.pyx4j.widgets.client.style.theme.HorizontalAlignCenterMixin;

public class ResponsiveLayoutPanel extends ComplexPanel implements RequiresResize, ProvidesResize, LayoutChangeRerquestHandler {

    public static final int MAX_WIDTH = 1200;

    public enum LayoutType {

        phonePortrait(0, 320), phoneLandscape(321, 480), tabletPortrait(481, 768), tabletLandscape(769, 1024), monitor(1025, Integer.MAX_VALUE);

        private int minWidth;

        private int maxWidth;

        LayoutType(int minWidth, int maxWidth) {
            this.minWidth = minWidth;
            this.maxWidth = maxWidth;
        }

        public static LayoutType getLayoutType(int width) {
            for (LayoutType segment : LayoutType.values()) {
                if (width >= segment.minWidth && width <= segment.maxWidth)
                    return segment;
            }
            throw new Error("No ResponseSegment found for width " + width);
        }
    }

    public enum Display {
        header, stickyHeader, menu, content, footer, notifications, commersial, message
    }

    private static final int ANIMATION_TIME = 500;

    private final Map<Display, DisplayPanel> displays;

    private final Layout pageLayout;

    private final StickyHeaderHolder stickyHeaderHolder;

    private final InlineMenuHolder inlineMenuHolder;

    private final SideMenuHolder sideMenuHolder;

    private final PopupNotificationsHolder popupNotificationsHolder;

    private final SideMenuHolder sideNotificationsHolder;

    private final SimplePanel commercialHolder;

    private final ScrollPanel pageScroll;

    private boolean sideMenuVisible = false;

    private boolean sideNotificationsVisible = false;

    private LayoutType layoutType;

    public ResponsiveLayoutPanel() {

        setElement(Document.get().createDivElement());

        displays = new HashMap<Display, DisplayPanel>();
        for (Display display : Display.values()) {
            displays.put(display, new DisplayPanel());
        }

        pageLayout = new Layout(getElement());

        FlowPanel pagePanel = new FlowPanel();
        pagePanel.setStyleName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutMainHolder.name());

        pageScroll = new ScrollPanel(pagePanel);

        stickyHeaderHolder = new StickyHeaderHolder();

        stickyHeaderHolder.setHeaderDisplay(getStickyHeaderDisplay());
        getStickyHeaderDisplay().getElement().getStyle().setProperty("maxWidth", MAX_WIDTH + "px");
        getStickyHeaderDisplay().addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());

        stickyHeaderHolder.setMessageDisplay(getMessageDisplay());
        getMessageDisplay().getElement().getStyle().setProperty("maxWidth", MAX_WIDTH + "px");
        getMessageDisplay().addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());

        commercialHolder = new SimplePanel();
        commercialHolder.getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);
        commercialHolder.getElement().getStyle().setProperty("verticalAlign", "top");
        commercialHolder.getElement().getStyle().setPosition(Position.ABSOLUTE);
        commercialHolder.getElement().getStyle().setProperty("right", "0");

        commercialHolder.setWidget(getCommercialDisplay());

        SimplePanel contentHolder = new SimplePanel(getContentDisplay());
        contentHolder.getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);
        contentHolder.getElement().getStyle().setProperty("verticalAlign", "top");

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setPosition(Position.RELATIVE);

        contentPanel.add(contentHolder);
        contentPanel.add(commercialHolder);

        inlineMenuHolder = new InlineMenuHolder(stickyHeaderHolder);

        FlowPanel mainPanel = new FlowPanel();
        mainPanel.setStyleName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutContentHolder.name());
        mainPanel.getElement().getStyle().setProperty("maxWidth", MAX_WIDTH + "px");
        mainPanel.addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());
        mainPanel.getElement().getStyle().setPosition(Position.RELATIVE);
        mainPanel.add(contentPanel);
        mainPanel.add(inlineMenuHolder);

        pageScroll.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                stickyHeaderHolder.onPositionChange();
                inlineMenuHolder.onPositionChange();

            }
        });

        popupNotificationsHolder = new PopupNotificationsHolder();

        SimplePanel footerHolder = new SimplePanel(getFooterDisplay());
        footerHolder.setStyleName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutFooterHolder.name());
        getFooterDisplay().getElement().getStyle().setProperty("maxWidth", MAX_WIDTH + "px");
        getFooterDisplay().addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());

        pagePanel.add(getHeaderDisplay());
        pagePanel.add(stickyHeaderHolder);
        pagePanel.add(mainPanel);
        pagePanel.add(footerHolder);

        // ============ Content Layer ============
        {
            Layer layer = pageLayout.attachChild(pageScroll.asWidget().getElement(), pageScroll);
            pageScroll.setLayoutData(layer);

            getChildren().add(pageScroll);
            adopt(pageScroll);
        }

        // ============ Side Menu Layer ============
        {

            sideMenuHolder = new SideMenuHolder();

            Layer layer = pageLayout.attachChild(sideMenuHolder.asWidget().getElement(), sideMenuHolder);
            sideMenuHolder.setLayoutData(layer);

            getChildren().add(sideMenuHolder);
            adopt(sideMenuHolder);
        }

        // ============ Side Notifications Layer ============
        {

            sideNotificationsHolder = new SideMenuHolder();

            Layer layer = pageLayout.attachChild(sideNotificationsHolder.asWidget().getElement(), sideNotificationsHolder);
            sideNotificationsHolder.setLayoutData(layer);

            getChildren().add(sideNotificationsHolder);
            adopt(sideNotificationsHolder);
        }

        AppSite.getEventBus().addHandler(LayoutChangeRerquestEvent.TYPE, this);

        layoutType = LayoutType.getLayoutType(Window.getClientWidth());

    }

    public DisplayPanel getHeaderDisplay() {
        return displays.get(Display.header);
    }

    public DisplayPanel getStickyHeaderDisplay() {
        return displays.get(Display.stickyHeader);
    }

    public DisplayPanel getContentDisplay() {
        return displays.get(Display.content);
    }

    public DisplayPanel getMenuDisplay() {
        return displays.get(Display.menu);
    }

    public DisplayPanel getNotificationsDisplay() {
        return displays.get(Display.notifications);
    }

    public DisplayPanel getFooterDisplay() {
        return displays.get(Display.footer);
    }

    public DisplayPanel getCommercialDisplay() {
        return displays.get(Display.commersial);
    }

    public DisplayPanel getMessageDisplay() {
        return displays.get(Display.message);
    }

    public void forceLayout(int animationTime) {
        doLayout();
        pageLayout.layout(animationTime);
        resizeComponents();
        AppSite.getEventBus().fireEvent(new LayoutChangeEvent(layoutType));
    }

    private void doLayout() {

        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            sideMenuHolder.setWidget(getMenuDisplay());
            sideNotificationsHolder.setWidget(getNotificationsDisplay());
            getHeaderDisplay().setVisible(false);
            break;
        default:
            setSideMenuVisible(false);
            inlineMenuHolder.setWidget(getMenuDisplay());
            popupNotificationsHolder.setWidget(getNotificationsDisplay());
            getHeaderDisplay().setVisible(true);
            break;
        }

        switch (layoutType) {
        case monitor:
        case tabletLandscape:
            getCommercialDisplay().setVisible(true);
            break;
        default:
            getCommercialDisplay().setVisible(false);
            break;
        }

        Layer menuLayer = (Layer) sideMenuHolder.getLayoutData();
        Layer notifications = (Layer) sideNotificationsHolder.getLayoutData();
        Layer mainLayer = (Layer) pageScroll.getLayoutData();

        if (sideMenuVisible) {
            menuLayer.setLeftWidth(0.0, Unit.PCT, 75.0, Unit.PCT);
            notifications.setLeftWidth(175.0, Unit.PCT, 75.0, Unit.PCT);
            mainLayer.setLeftWidth(75.0, Unit.PCT, 100.0, Unit.PCT);
        } else if (sideNotificationsVisible) {
            menuLayer.setLeftWidth(-150.0, Unit.PCT, 75.0, Unit.PCT);
            notifications.setLeftWidth(25.0, Unit.PCT, 75.0, Unit.PCT);
            mainLayer.setLeftWidth(-75.0, Unit.PCT, 100.0, Unit.PCT);
        } else {
            menuLayer.setLeftWidth(-75.0, Unit.PCT, 75.0, Unit.PCT);
            notifications.setLeftWidth(100.0, Unit.PCT, 75.0, Unit.PCT);
            mainLayer.setLeftWidth(0.0, Unit.PCT, 100.0, Unit.PCT);
        }

    }

    @Override
    public void onResize() {

        LayoutType previousLayoutType = layoutType;
        layoutType = LayoutType.getLayoutType(Window.getClientWidth());

        if (previousLayoutType != layoutType) {
            forceLayout(0);
        } else {
            resizeComponents();
        }
    }

    private void resizeComponents() {
        stickyHeaderHolder.onResize();
        stickyHeaderHolder.onPositionChange();
        inlineMenuHolder.onPositionChange();

        getContentDisplay().getElement().getStyle().setMarginLeft(inlineMenuHolder.getOffsetWidth(), Unit.PX);
        getContentDisplay().getElement().getStyle().setMarginRight(commercialHolder.getOffsetWidth(), Unit.PX);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        forceLayout(0);
    }

    private boolean isSideMenuEnabled() {
        LayoutType layoutType = LayoutType.getLayoutType(Window.getClientWidth());
        return LayoutType.phonePortrait == layoutType || LayoutType.phoneLandscape == layoutType;
    }

    private boolean isSideNotificationsEnabled() {
        LayoutType layoutType = LayoutType.getLayoutType(Window.getClientWidth());
        return LayoutType.phonePortrait == layoutType || LayoutType.phoneLandscape == layoutType;
    }

    private boolean isPopupNotificationsEnabled() {
        LayoutType layoutType = LayoutType.getLayoutType(Window.getClientWidth());
        return !(LayoutType.phonePortrait == layoutType || LayoutType.phoneLandscape == layoutType);
    }

    private void setSideMenuVisible(boolean visible) {
        if (this.sideMenuVisible != visible) {
            this.sideMenuVisible = visible;
            forceLayout(ANIMATION_TIME);
        }
    }

    private void setSideNotificationsVisible(boolean visible) {
        if (this.sideNotificationsVisible != visible) {
            this.sideNotificationsVisible = visible;
            forceLayout(ANIMATION_TIME);
        }
    }

    private void togglePopupNotificationsVisible(Widget anchor) {
        if (!popupNotificationsHolder.isShowing()) {
            popupNotificationsHolder.showRelativeTo(anchor);
        } else {
            popupNotificationsHolder.hide();
        }
    }

    @Override
    public void onLayoutChangeRerquest(LayoutChangeRerquestEvent event) {
        switch (event.getChangeType()) {
        case toggleSideMenu:
            if (isSideMenuEnabled()) {
                setSideMenuVisible(!sideMenuVisible);
            }
            break;
        case toggleSideNotifications:
            if (isSideNotificationsEnabled()) {
                setSideNotificationsVisible(!sideNotificationsVisible);
            }
            break;
        case togglePopupNotifications:
            if (isPopupNotificationsEnabled()) {
                togglePopupNotificationsVisible(event.getPopupNotificationsAnchor());
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

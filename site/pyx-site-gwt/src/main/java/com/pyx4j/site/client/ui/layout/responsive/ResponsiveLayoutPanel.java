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
        header, stickyHeader, menu, content, footer, communication, commersial, notification
    }

    private static final int ANIMATION_TIME = 500;

    private final Map<Display, DisplayPanel> displays;

    private final Layout pageLayout;

    private final StickyHeaderHolder stickyHeaderHolder;

    private final InlineMenuHolder inlineMenuHolder;

    private final SidePanelHolder sideMenuHolder;

    private final PopupCommHolder popupCommHolder;

    private final SidePanelHolder sideCommHolder;

    private final CommercialHolder commercialHolder;

    private final ContentHolder contentHolder;

    private final SimplePanel footerHolder;

    private final ScrollPanel pageScroll;

    private final FlowPanel mainPanel;

    private boolean sideMenuVisible = false;

    private boolean sideCommVisible = false;

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

        stickyHeaderHolder = new StickyHeaderHolder(this);

        commercialHolder = new CommercialHolder(this);

        contentHolder = new ContentHolder(this);

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setPosition(Position.RELATIVE);

        contentPanel.add(contentHolder);
        contentPanel.add(commercialHolder);

        inlineMenuHolder = new InlineMenuHolder(this);

        mainPanel = new FlowPanel();
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

        popupCommHolder = new PopupCommHolder();

        footerHolder = new SimplePanel(getFooterDisplay());
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

            sideMenuHolder = new SidePanelHolder();

            Layer layer = pageLayout.attachChild(sideMenuHolder.asWidget().getElement(), sideMenuHolder);
            sideMenuHolder.setLayoutData(layer);

            getChildren().add(sideMenuHolder);
            adopt(sideMenuHolder);
        }

        // ============ Side Communication Layer ============
        {

            sideCommHolder = new SidePanelHolder();

            Layer layer = pageLayout.attachChild(sideCommHolder.asWidget().getElement(), sideCommHolder);
            sideCommHolder.setLayoutData(layer);

            getChildren().add(sideCommHolder);
            adopt(sideCommHolder);
        }

        AppSite.getEventBus().addHandler(LayoutChangeRerquestEvent.TYPE, this);

        layoutType = LayoutType.getLayoutType(Window.getClientWidth());

    }

    StickyHeaderHolder getStickyHeaderHolder() {
        return stickyHeaderHolder;
    }

    SimplePanel getFooterHolder() {
        return footerHolder;
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

    public DisplayPanel getCommDisplay() {
        return displays.get(Display.communication);
    }

    public DisplayPanel getFooterDisplay() {
        return displays.get(Display.footer);
    }

    public DisplayPanel getCommercialDisplay() {
        return displays.get(Display.commersial);
    }

    public DisplayPanel getNotificationDisplay() {
        return displays.get(Display.notification);
    }

    public void forceLayout(int animationTime) {
        doLayout();
        pageLayout.layout(animationTime);
        AppSite.getEventBus().fireEvent(new LayoutChangeEvent(layoutType));
        resizeComponents();
    }

    private void doLayout() {

        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            sideMenuHolder.setMenuDisplay(getMenuDisplay());
            sideCommHolder.setMenuDisplay(getCommDisplay());
            getHeaderDisplay().setVisible(false);
            break;
        default:
            setSideMenuVisible(false);
            setSideCommVisible(false);
            inlineMenuHolder.setMenuDisplay(getMenuDisplay());
            popupCommHolder.setWidget(getCommDisplay());
            getHeaderDisplay().setVisible(true);
            break;
        }

        switch (layoutType) {
        case monitor:
        case tabletLandscape:
            getCommercialDisplay().setVisible(true);
            contentHolder.getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);
            break;
        default:
            getCommercialDisplay().setVisible(false);
            contentHolder.getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.BLOCK);
            break;
        }

        Layer menuLayer = (Layer) sideMenuHolder.getLayoutData();
        Layer commLayer = (Layer) sideCommHolder.getLayoutData();
        Layer mainLayer = (Layer) pageScroll.getLayoutData();

        if (sideMenuVisible) {
            menuLayer.setLeftWidth(0.0, Unit.PCT, 75.0, Unit.PCT);
            commLayer.setLeftWidth(175.0, Unit.PCT, 75.0, Unit.PCT);
            mainLayer.setLeftWidth(75.0, Unit.PCT, 100.0, Unit.PCT);
        } else if (sideCommVisible) {
            menuLayer.setLeftWidth(-150.0, Unit.PCT, 75.0, Unit.PCT);
            commLayer.setLeftWidth(25.0, Unit.PCT, 75.0, Unit.PCT);
            mainLayer.setLeftWidth(-75.0, Unit.PCT, 100.0, Unit.PCT);
        } else {
            menuLayer.setLeftWidth(-75.0, Unit.PCT, 75.0, Unit.PCT);
            commLayer.setLeftWidth(100.0, Unit.PCT, 75.0, Unit.PCT);
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

        contentHolder.getElement().getStyle().setPaddingLeft(inlineMenuHolder.getOffsetWidth(), Unit.PX);
        contentHolder.setWidth((mainPanel.getOffsetWidth() - inlineMenuHolder.getOffsetWidth() - commercialHolder.getOffsetWidth()) + "px");

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

    private boolean isSideCommEnabled() {
        LayoutType layoutType = LayoutType.getLayoutType(Window.getClientWidth());
        return LayoutType.phonePortrait == layoutType || LayoutType.phoneLandscape == layoutType;
    }

    private boolean isPopupCommEnabled() {
        LayoutType layoutType = LayoutType.getLayoutType(Window.getClientWidth());
        return !(LayoutType.phonePortrait == layoutType || LayoutType.phoneLandscape == layoutType);
    }

    private void setSideMenuVisible(boolean visible) {
        if (this.sideMenuVisible != visible) {
            this.sideMenuVisible = visible;
            forceLayout(ANIMATION_TIME);
        }
    }

    private void setSideCommVisible(boolean visible) {
        if (this.sideCommVisible != visible) {
            this.sideCommVisible = visible;
            forceLayout(ANIMATION_TIME);
        }
    }

    private void togglePopupCommVisible(Widget anchor) {
        if (!popupCommHolder.isShowing()) {
            popupCommHolder.showRelativeTo(anchor);
        } else {
            popupCommHolder.hide();
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

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
package com.pyx4j.site.client.ui.layout.frontoffice;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.DisplayPanel;
import com.pyx4j.site.client.ui.devconsole.DevConsoleTab;
import com.pyx4j.site.client.ui.devconsole.ResponsiveLayoutDevConsole;
import com.pyx4j.site.client.ui.layout.LayoutChangeRequestEvent;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel;
import com.pyx4j.widgets.client.style.theme.HorizontalAlignCenterMixin;

public class FrontOfficeLayoutPanel extends ResponsiveLayoutPanel {

    public static final int MAX_WIDTH = 1200;

    public enum DisplayType {
        header, toolbar, menu, content, footer, communication, extra, notification
    }

    private final Map<DisplayType, DisplayPanel> displays;

    private final InlineToolbarHolder inlineToolbarHolder;

    private final StickyToolbarHolder stickyToolbarHolder;

    private final InlineMenuHolder inlineMenuHolder;

    private final SidePanelHolder sideMenuHolder;

    private final PopupCommHolder popupCommHolder;

    private final SidePanelHolder sideCommHolder;

    private final ExtraHolder extraHolder;

    private final ContentHolder contentHolder;

    private final SimplePanel footerHolder;

    private final FlowPanel pageHolder;

    private final FlowPanel pagePanel;

    private final CenterPanel centerPanel;

    private final ScrollPanel pageScroll;

    private boolean sideMenuVisible = false;

    private boolean sideCommVisible = false;

    private DevConsoleTab devConsoleTab;

    @SuppressWarnings("deprecation")
    public FrontOfficeLayoutPanel() {

        displays = new HashMap<>();
        for (DisplayType display : DisplayType.values()) {
            displays.put(display, new DisplayPanel());
        }

        pageHolder = new FlowPanel();

        pagePanel = new FlowPanel();
        pagePanel.setStyleName(FrontOfficeLayoutTheme.StyleName.ResponsiveLayoutMainHolder.name());

        pageScroll = new ScrollPanel(pagePanel);
        pageScroll.addScrollHandler(new ScrollHandler() {

            @Override
            public void onScroll(ScrollEvent event) {
                DisplayPanel headerDisplay = getDisplay(DisplayType.header);
                if (pageScroll.getVerticalScrollPosition() <= headerDisplay.getOffsetHeight()) {
                    headerDisplay.getElement().getStyle().setOpacity(1 - (double) pageScroll.getVerticalScrollPosition() / headerDisplay.getOffsetHeight());
                } else {
                    headerDisplay.getElement().getStyle().setOpacity(1);
                }
            }
        });
        pageScroll.getElement().getStyle().setOverflowY(Overflow.SCROLL);
        pageScroll.setHeight("100%");
        pageHolder.add(pageScroll);

        inlineToolbarHolder = new InlineToolbarHolder(this);

        stickyToolbarHolder = new StickyToolbarHolder(this);
        pageHolder.add(stickyToolbarHolder);

        extraHolder = new ExtraHolder(this);

        getDisplay(DisplayType.notification).getElement().getStyle().setTextAlign(TextAlign.CENTER);
        getDisplay(DisplayType.content).getElement().getStyle().setTextAlign(TextAlign.CENTER);

        getDisplay(DisplayType.toolbar).getElement().getStyle().setProperty("maxWidth", FrontOfficeLayoutPanel.MAX_WIDTH + "px");
        getDisplay(DisplayType.toolbar).addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());

        contentHolder = new ContentHolder(this);
        contentHolder.getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setPosition(Position.RELATIVE);

        contentPanel.add(contentHolder);
        contentPanel.add(extraHolder);

        inlineMenuHolder = new InlineMenuHolder(this);

        centerPanel = new CenterPanel(contentPanel, inlineMenuHolder);

        pageScroll.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                FrontOfficeLayoutPanel.this.onScroll();
            }
        });

        popupCommHolder = new PopupCommHolder();

        footerHolder = new SimplePanel(getDisplay(DisplayType.footer));
        footerHolder.setStyleName(FrontOfficeLayoutTheme.StyleName.ResponsiveLayoutFooterHolder.name());
        getDisplay(DisplayType.footer).getElement().getStyle().setProperty("maxWidth", MAX_WIDTH + "px");
        getDisplay(DisplayType.footer).addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());

        pagePanel.add(getDisplay(DisplayType.header));
        pagePanel.add(inlineToolbarHolder);
        pagePanel.add(centerPanel);
        pagePanel.add(footerHolder);

        // ============ Content Layer ============
        {
            Layer layer = getLayout().attachChild(pageHolder.asWidget().getElement(), pageHolder);
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

        if (ApplicationMode.isDevelopment()) {
            devConsoleTab = new DevConsoleTab(new ResponsiveLayoutDevConsole(this));
            add(devConsoleTab.asWidget(), getElement());
        }

    }

    SimplePanel getFooterHolder() {
        return footerHolder;
    }

    public DisplayPanel getDisplay(DisplayType displayType) {
        return displays.get(displayType);
    }

    @Override
    protected void doLayout() {

        switch (getLayoutType()) {
        case phonePortrait:
        case phoneLandscape:
            sideMenuHolder.setDisplay(getDisplay(DisplayType.menu));
            sideCommHolder.setDisplay(getDisplay(DisplayType.communication));
            getDisplay(DisplayType.header).setVisible(false);
            break;
        default:
            setSideMenuVisible(false);
            setSideCommVisible(false);
            inlineMenuHolder.setMenuDisplay(getDisplay(DisplayType.menu));
            popupCommHolder.setWidget(getDisplay(DisplayType.communication));
            getDisplay(DisplayType.header).setVisible(true);
            break;
        }

        switch (getLayoutType()) {
        case huge:
            getDisplay(DisplayType.extra).setVisible(true);
            break;
        default:
            getDisplay(DisplayType.extra).setVisible(false);
            break;
        }

        Layer menuLayer = (Layer) sideMenuHolder.getLayoutData();
        Layer commLayer = (Layer) sideCommHolder.getLayoutData();
        Layer mainLayer = (Layer) pageHolder.getLayoutData();

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
        super.onResize();
        pageScroll.onResize();
    }

    private void onScroll() {
        inlineMenuHolder.onPositionChange();
        extraHolder.onPositionChange();

        if (inlineToolbarHolder.getAbsoluteTop() > 0) {
            if (inlineToolbarHolder.getWidget() == null) {
                inlineToolbarHolder.setDisplay();
            }
        } else if (stickyToolbarHolder.getWidget() == null) {
            stickyToolbarHolder.setDisplay();
        }

    }

    @Override
    protected void resizeComponents() {
        onScroll();

        contentHolder.getElement().getStyle().setPaddingLeft(inlineMenuHolder.getMenuWidth(), Unit.PX);

        if (getDisplay(DisplayType.extra).isVisible()) {
            contentHolder.setWidth((centerPanel.getOffsetWidth() - inlineMenuHolder.getMenuWidth() - extraHolder.getOffsetWidth()) + "px");
        } else {
            contentHolder.setWidth((centerPanel.getOffsetWidth() - inlineMenuHolder.getMenuWidth()) + "px");
        }

        for (DisplayPanel displayPanel : displays.values()) {
            displayPanel.onResize();
        }

        stickyToolbarHolder.onResize();
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
            forceLayout(ResponsiveLayoutPanel.ANIMATION_TIME);
        }
    }

    private void setSideCommVisible(boolean visible) {
        if (this.sideCommVisible != visible) {
            this.sideCommVisible = visible;
            forceLayout(ResponsiveLayoutPanel.ANIMATION_TIME);
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
    public void onLayoutChangeRequest(LayoutChangeRequestEvent event) {
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

    public int getPageWidth() {
        return pagePanel.getOffsetWidth();
    }

    public void scrollToTop() {
        if (pageScroll.getVerticalScrollPosition() > getDisplay(DisplayType.header).getOffsetHeight()) {
            pageScroll.setVerticalScrollPosition(getDisplay(DisplayType.header).getOffsetHeight());
        }
    }

    public void setDevConsole(IsWidget widget) {
        if (devConsoleTab != null) {
            devConsoleTab.setDevConsole(widget);
        }
    }
}

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
package com.pyx4j.site.client.website.ui.layout;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.Window;

import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.ScrollPanel;
import com.pyx4j.gwt.commons.ui.SimplePanel;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel;
import com.pyx4j.site.client.ui.layout.SidePanelHolder;
import com.pyx4j.widgets.client.style.theme.HorizontalAlignCenterMixin;

public class WebSiteLayoutPanel extends ResponsiveLayoutPanel {

    public static final int MAX_WIDTH = 1200;

    static {
        FieldDecorator.Builder.setDefaultLabelWidth("220px");
    }

    private final FlowPanel pageHolder;

    private final SimplePanel headerHolder;

    private final InlineToolbarHolder inlineToolbarHolder;

    private final StickyToolbarHolder stickyToolbarHolder;

    private final SidePanelHolder sideMenuHolder;

    private final OverlayExtraHolder overlayExtraHolder;

    private final ContentHolder contentHolder;

    private final SimplePanel footerHolder;

    private final FlowPanel pagePanel;

    private final CenterPanel centerPanel;

    private final ScrollPanel pageScroll;

    private boolean sideMenuVisible = false;

    public WebSiteLayoutPanel(String extra1Caption, String extra2Caption, String extra4Caption) {
        pageHolder = new FlowPanel();

        pagePanel = new FlowPanel();
        pagePanel.setStyleName(WebSiteLayoutTheme.StyleName.WebSiteLayoutMainHolder.name());

        headerHolder = new SimplePanel(getDisplay(DisplayType.header));
        headerHolder.setStyleName(WebSiteLayoutTheme.StyleName.WebSiteLayoutHeaderHolder.name());

        pageScroll = new ScrollPanel(pagePanel);
        pageScroll.getStyle().setOverflowY(Overflow.SCROLL);
        pageScroll.setHeight("100%");
        pageHolder.add(pageScroll);

        inlineToolbarHolder = new InlineToolbarHolder(this);

        stickyToolbarHolder = new StickyToolbarHolder(this);
        pageHolder.add(stickyToolbarHolder);

        getDisplay(DisplayType.notification).getStyle().setTextAlign(TextAlign.CENTER);
        getDisplay(DisplayType.content).getStyle().setTextAlign(TextAlign.CENTER);

        getDisplay(DisplayType.toolbar).getStyle().setProperty("maxWidth", WebSiteLayoutPanel.MAX_WIDTH + "px");
        getDisplay(DisplayType.toolbar).addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());

        contentHolder = new ContentHolder(this);
        contentHolder.ensureDebugId(getClass().getSimpleName() + ".contentHolder");

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.ensureDebugId(getClass().getSimpleName() + ".contentPanel");
        contentPanel.getStyle().setPosition(Position.RELATIVE);

        contentPanel.add(contentHolder);

        centerPanel = new CenterPanel(contentPanel);

        pageScroll.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                WebSiteLayoutPanel.this.onScroll();
            }
        });

        footerHolder = new SimplePanel(getDisplay(DisplayType.footer));
        footerHolder.setStyleName(WebSiteLayoutTheme.StyleName.WebSiteLayoutFooterHolder.name());
        getDisplay(DisplayType.footer).getStyle().setProperty("maxWidth", MAX_WIDTH + "px");
        getDisplay(DisplayType.footer).addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());

        pagePanel.add(headerHolder);
        pagePanel.add(inlineToolbarHolder);
        pagePanel.add(centerPanel);
        pagePanel.add(footerHolder);

        overlayExtraHolder = new OverlayExtraHolder(this, extra1Caption, extra2Caption);

        pageHolder.add(overlayExtraHolder);

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

        AppSite.getEventBus().addHandler(LayoutChangeRequestEvent.TYPE, this);

        forceLayout(0);
        onScroll();
    }

    SimplePanel getFooterHolder() {
        return footerHolder;
    }

    @Override
    protected void doLayout() {

        switch (getLayoutType()) {
        case phonePortrait:
        case phoneLandscape:
            sideMenuHolder.setDisplay(getDisplay(DisplayType.menu));
            headerHolder.setVisible(false);
            break;
        default:
            setSideMenuVisible(false);
            headerHolder.setVisible(true);
            break;
        }

        overlayExtraHolder.layout();

        Layer menuLayer = (Layer) sideMenuHolder.getLayoutData();
        Layer mainLayer = (Layer) pageHolder.getLayoutData();

        if (sideMenuVisible) {
            menuLayer.setLeftWidth(0.0, Unit.PCT, 75.0, Unit.PCT);
            mainLayer.setLeftWidth(75.0, Unit.PCT, 100.0, Unit.PCT);
        } else {
            menuLayer.setLeftWidth(-75.0, Unit.PCT, 75.0, Unit.PCT);
            mainLayer.setLeftWidth(0.0, Unit.PCT, 100.0, Unit.PCT);
        }

    }

    @Override
    public void onResize() {
        super.onResize();
        pageScroll.onResize();
    }

    protected void onScroll() {
        if (inlineToolbarHolder.getAbsoluteTop() > 0) {
            if (inlineToolbarHolder.getWidget() == null) {
                inlineToolbarHolder.setDisplay();
            }
        } else if (stickyToolbarHolder.getWidget() == null) {
            stickyToolbarHolder.setDisplay();
        }
        for (DisplayType displayType : DisplayType.values()) {
            getDisplay(displayType).onScroll(pageScroll.getVerticalScrollPosition());
        }
    }

    @Override
    protected void resizeComponents() {

        onScroll();

        for (DisplayType displayType : DisplayType.values()) {
            getDisplay(displayType).onResize();
        }

        stickyToolbarHolder.onResize();
    }

    private boolean isSideMenuEnabled() {
        LayoutType layoutType = LayoutType.getLayoutType(Window.getClientWidth());
        return LayoutType.phonePortrait == layoutType || LayoutType.phoneLandscape == layoutType;
    }

    private void setSideMenuVisible(boolean visible) {
        if (this.sideMenuVisible != visible) {
            this.sideMenuVisible = visible;
            forceLayout(ResponsiveLayoutPanel.ANIMATION_TIME);
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

    public int getPageHeight() {
        return pagePanel.getOffsetHeight();
    }

    public void scrollToTop() {
        scrollToTop(pageScroll.getVerticalScrollPosition());
    }

    public void scrollToTop(int originalSchrollPosition) {
        if (originalSchrollPosition >= headerHolder.getOffsetHeight()) {
            pageScroll.setVerticalScrollPosition(headerHolder.getOffsetHeight());
        }
    }

    public int getScrollPosition() {
        return pageScroll.getVerticalScrollPosition();
    }

    public void scrollToBottom() {
        pageScroll.setVerticalScrollPosition(pagePanel.getOffsetHeight());
    }

}

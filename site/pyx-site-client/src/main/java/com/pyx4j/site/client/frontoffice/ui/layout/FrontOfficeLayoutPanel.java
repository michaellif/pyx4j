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
package com.pyx4j.site.client.frontoffice.ui.layout;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.frontoffice.ui.FrontOfficeDevConsole;
import com.pyx4j.site.client.ui.layout.OverlayExtraHolder;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel;
import com.pyx4j.site.client.ui.layout.SidePanelHolder;
import com.pyx4j.widgets.client.DropDownPanel;
import com.pyx4j.widgets.client.style.theme.HorizontalAlignCenterMixin;

public class FrontOfficeLayoutPanel extends ResponsiveLayoutPanel {

    public static final int MAX_WIDTH = 1200;

    static {
        FieldDecorator.Builder.setDefaultLabelWidth("220px");
    }

    private final FlowPanel pageHolder;

    private final SimplePanel headerHolder;

    private final InlineToolbarHolder inlineToolbarHolder;

    private final StickyToolbarHolder stickyToolbarHolder;

    private final InlineMenuHolder inlineMenuHolder;

    private final SidePanelHolder sideMenuHolder;

    private final DropDownPanel popupCommHolder;

    private final SidePanelHolder sideCommHolder;

    private final InlineExtraHolder inlineExtraHolder;

    private final OverlayExtraHolder overlayExtraHolder;

    private final ContentHolder contentHolder;

    private final SimplePanel footerHolder;

    private final FlowPanel pagePanel;

    private final CenterPanel centerPanel;

    private final ScrollPanel pageScroll;

    private boolean sideMenuVisible = false;

    private boolean sideCommVisible = false;

    public FrontOfficeLayoutPanel(String extra1Caption, String extra2Caption, String extra4Caption) {

        pageHolder = new FlowPanel();

        pagePanel = new FlowPanel();
        pagePanel.setStyleName(FrontOfficeLayoutTheme.StyleName.FrontOfficeLayoutMainHolder.name());

        headerHolder = new SimplePanel(getDisplay(DisplayType.header));
        headerHolder.setStyleName(FrontOfficeLayoutTheme.StyleName.FrontOfficeLayoutHeaderHolder.name());

        pageScroll = new ScrollPanel(pagePanel);
        pageScroll.getElement().getStyle().setOverflowY(Overflow.SCROLL);
        pageScroll.setHeight("100%");
        pageHolder.add(pageScroll);

        inlineToolbarHolder = new InlineToolbarHolder(this);

        stickyToolbarHolder = new StickyToolbarHolder(this);
        pageHolder.add(stickyToolbarHolder);

        inlineExtraHolder = new InlineExtraHolder(this, extra1Caption, extra2Caption);

        getDisplay(DisplayType.notification).getElement().getStyle().setTextAlign(TextAlign.CENTER);
        getDisplay(DisplayType.content).getElement().getStyle().setTextAlign(TextAlign.CENTER);

        getDisplay(DisplayType.toolbar).getElement().getStyle().setProperty("maxWidth", FrontOfficeLayoutPanel.MAX_WIDTH + "px");
        getDisplay(DisplayType.toolbar).addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());

        contentHolder = new ContentHolder(this);
        contentHolder.ensureDebugId(getClass().getSimpleName() + ".contentHolder");

        // Fix for MCO footer position in IE8
        if (!BrowserType.isIE8()) {
            contentHolder.getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);
        }

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.ensureDebugId(getClass().getSimpleName() + ".contentPanel");
        contentPanel.getElement().getStyle().setPosition(Position.RELATIVE);

        contentPanel.add(contentHolder);
        contentPanel.add(inlineExtraHolder);

        inlineMenuHolder = new InlineMenuHolder(this);

        centerPanel = new CenterPanel(contentPanel, inlineMenuHolder);

        pageScroll.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                FrontOfficeLayoutPanel.this.onScroll();
            }
        });

        popupCommHolder = new DropDownPanel();

        footerHolder = new SimplePanel(getDisplay(DisplayType.footer));
        footerHolder.setStyleName(FrontOfficeLayoutTheme.StyleName.FrontOfficeLayoutFooterHolder.name());
        getDisplay(DisplayType.footer).getElement().getStyle().setProperty("maxWidth", MAX_WIDTH + "px");
        getDisplay(DisplayType.footer).addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());

        pagePanel.add(headerHolder);
        pagePanel.add(inlineToolbarHolder);
        pagePanel.add(centerPanel);
        pagePanel.add(footerHolder);

        overlayExtraHolder = new OverlayExtraHolder(this, extra1Caption, extra2Caption, extra4Caption, new FrontOfficeDevConsole(this));

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
            inlineMenuHolder.setMenuDisplay(getDisplay(DisplayType.menu));
            headerHolder.setVisible(true);
            break;
        }

        switch (getLayoutType()) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            sideCommHolder.setDisplay(getDisplay(DisplayType.communication));
            break;
        default:
            setSideCommVisible(false);
            popupCommHolder.setWidget(getDisplay(DisplayType.communication));
            break;
        }

        inlineExtraHolder.layout();
        overlayExtraHolder.layout();

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

    protected void onScroll() {
        inlineMenuHolder.onPositionChange();
        inlineExtraHolder.onPositionChange();

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

        contentHolder.getElement().getStyle().setPaddingLeft(inlineMenuHolder.getMenuWidth(), Unit.PX);

        switch (getLayoutType()) {
        case huge:
            int contentHolderWidth = centerPanel.getOffsetWidth() - inlineMenuHolder.getMenuWidth() - inlineExtraHolder.getOffsetWidth();
            contentHolder.setWidth((contentHolderWidth > 0 ? --contentHolderWidth : 0) + "px");
            inlineExtraHolder.layout();
            break;
        default:
            contentHolder.setWidth((centerPanel.getOffsetWidth() - inlineMenuHolder.getMenuWidth()) + "px");
            overlayExtraHolder.layout();
            break;
        }

        for (DisplayType displayType : DisplayType.values()) {
            getDisplay(displayType).onResize();
        }

        stickyToolbarHolder.onResize();
    }

    private boolean isSideMenuEnabled() {
        LayoutType layoutType = LayoutType.getLayoutType(Window.getClientWidth());
        return LayoutType.phonePortrait == layoutType || LayoutType.phoneLandscape == layoutType;
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

    public boolean isSideCommVisible() {
        return sideCommVisible;
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

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
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.DisplayPanel;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRerquestEvent.ChangeType;

public class ResponsiveLayoutPanel extends ComplexPanel implements RequiresResize, ProvidesResize, LayoutChangeRerquestHandler {

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
        header, stickyHeader, menu, content, footer
    }

    private static final int ANIMATION_TIME = 500;

    private final Map<Display, DisplayPanel> displays;

    private final Layout pageLayout;

    private final StickyHeaderHolder stickyHeaderHolder;

    private final InlineMenuHolder inlineMenuHolder;

    private final SideMenuHolder sideMenuHolder;

    private final ScrollPanel mainScroll;

    private boolean sideMenuVisible = false;

    private LayoutType layoutType;

    public ResponsiveLayoutPanel() {
        setElement(Document.get().createDivElement());

        displays = new HashMap<Display, DisplayPanel>();
        for (Display display : Display.values()) {
            displays.put(display, new DisplayPanel());
        }

        pageLayout = new Layout(getElement());

        FlowPanel mainHolder = new FlowPanel();
        mainScroll = new ScrollPanel(mainHolder);

        sideMenuHolder = new SideMenuHolder();

        getStickyHeaderDisplay().getElement().getStyle().setZIndex(10);
        stickyHeaderHolder = new StickyHeaderHolder();
        stickyHeaderHolder.setWidget(getStickyHeaderDisplay());

        FlowPanel contentHolder = new FlowPanel();
        contentHolder.getElement().getStyle().setPosition(Position.RELATIVE);

        inlineMenuHolder = new InlineMenuHolder(stickyHeaderHolder);
        contentHolder.add(inlineMenuHolder);
        contentHolder.add(getContentDisplay());

        mainScroll.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                stickyHeaderHolder.onPositionChange();
                inlineMenuHolder.onPositionChange();

            }
        });

        mainHolder.add(getHeaderDisplay());
        mainHolder.add(stickyHeaderHolder);
        mainHolder.add(contentHolder);
        mainHolder.add(getFooterDisplay());

        // ============ Content Layer ============
        {
            Layer layer = pageLayout.attachChild(mainScroll.asWidget().getElement(), mainScroll);
            mainScroll.setLayoutData(layer);

            getChildren().add(mainScroll);
            adopt(mainScroll);
        }

        // ============ Side Menu Layer ============
        {
            Layer layer = pageLayout.attachChild(sideMenuHolder.asWidget().getElement(), sideMenuHolder);
            sideMenuHolder.setLayoutData(layer);

            getChildren().add(sideMenuHolder);
            adopt(sideMenuHolder);
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

    public DisplayPanel getFooterDisplay() {
        return displays.get(Display.footer);
    }

    public void forceLayout() {
        doLayout();
        pageLayout.layout(ANIMATION_TIME);
        resizeComponents();
        AppSite.getEventBus().fireEvent(new LayoutChangeEvent(layoutType));
    }

    private void doLayout() {

        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            sideMenuHolder.setWidget(getMenuDisplay());
            break;
        default:
            setSideMenuVisible(false);
            inlineMenuHolder.setWidget(getMenuDisplay());
            break;
        }

        Layer menuLayer = (Layer) sideMenuHolder.getLayoutData();
        Layer mainLayer = (Layer) mainScroll.getLayoutData();

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

        LayoutType previousLayoutType = layoutType;
        layoutType = LayoutType.getLayoutType(Window.getClientWidth());

        if (previousLayoutType != layoutType) {
            forceLayout();
        } else {
            resizeComponents();
        }
    }

    private void resizeComponents() {
        for (Widget child : getChildren()) {
            if (child instanceof RequiresResize) {
                ((RequiresResize) child).onResize();
            }
        }
        stickyHeaderHolder.onPositionChange();
        inlineMenuHolder.onPositionChange();

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        forceLayout();
    }

    private boolean isSideMenuEnabled() {
        LayoutType layoutType = LayoutType.getLayoutType(Window.getClientWidth());
        return LayoutType.phonePortrait == layoutType || LayoutType.phoneLandscape == layoutType;
    }

    private void setSideMenuVisible(boolean visible) {
        if (this.sideMenuVisible != visible) {
            this.sideMenuVisible = visible;
            forceLayout();
        }
    }

    @Override
    public void onLayoutChangeRerquest(LayoutChangeRerquestEvent event) {
        for (ChangeType changeType : event.getChangeTypes()) {
            switch (changeType) {
            case toggleSideMenu:
                if (isSideMenuEnabled()) {
                    setSideMenuVisible(!sideMenuVisible);
                }
                break;
            default:
                break;
            }
        }

    }
}
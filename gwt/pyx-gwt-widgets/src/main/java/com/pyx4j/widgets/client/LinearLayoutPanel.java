/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Nov 18, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Linear layout panel.
 */
public class LinearLayoutPanel extends FlowPanel implements RequiresResize, ProvidesResize {

    /**
     * Layout data.
     */
    public static class LayoutData {
        public LayoutData(SimplePanel container, double size) {
            this.container = container;
            this.size = size;
        }

        public double size = -1;

        public SimplePanel container;
    }

    @Override
    public void add(Widget w) {
        super.add(initializeWidget(w));
        doLayout();
    }

    public LinearLayoutPanel() {
        this(false);
    }

    public LinearLayoutPanel(boolean vertical) {
        if (vertical) {
            setLayoutDirection(LayoutDirection.VERTICAL);
        }
    }

    /**
     * Layout direction.
     */
    public static enum LayoutDirection {
        HORIZONTAL, VERTICAL
    }

    private LayoutDirection layoutDirection = LayoutDirection.HORIZONTAL;

    /**
     * Initialize widget.
     * 
     * @param w
     *            Widget
     * @return Container
     */
    private SimplePanel initializeWidget(Widget w) {
        Object oldLayout = w.getLayoutData();
        double size = -1;
        if (oldLayout instanceof LayoutData) {
            size = ((LayoutData) oldLayout).size;
        }

        SimplePanel container = new SimplePanel();
        LayoutData layoutData = new LayoutData(container, size);
        w.setLayoutData(layoutData);
        container.setLayoutData(layoutData);

        initializeContainer(container);

        Style style = w.getElement().getStyle();
        style.setPosition(Position.ABSOLUTE);
        style.setTop(0, Unit.PX);
        style.setBottom(0, Unit.PX);
        style.setLeft(0, Unit.PX);
        style.setRight(0, Unit.PX);

        container.setWidget(w);
        return container;
    }

    /**
     * Initialize container.
     * 
     * @param container
     *            Container
     */
    private void initializeContainer(Widget container) {
        Style style = container.getElement().getStyle();
        style.setPosition(Position.RELATIVE);

        if (layoutDirection.equals(LayoutDirection.HORIZONTAL)) {
            if (!LocaleInfo.getCurrentLocale().isRTL()) {
                style.setFloat(Float.LEFT);
            } else {
                style.setFloat(Float.RIGHT);
            }
            style.setHeight(100, Unit.PCT);
        } else {
            style.setWidth(100, Unit.PCT);
        }
        style.setOverflow(Overflow.HIDDEN);
    }

    @Override
    public boolean remove(int index) {
        SimplePanel simplePanel = (SimplePanel) super.getWidget(index);
        Widget w = simplePanel.getWidget();
        boolean remove = super.remove(index);
        simplePanel.remove(w);
        doLayout();
        return remove;
    }

    @Override
    public boolean remove(Widget w) {
        w.removeFromParent();
        LayoutData layout = getWidgetLayoutData(w);
        if (layout != null) {
            boolean remove = super.remove(layout.container);
            doLayout();
            return remove;
        }
        return false;
    }

    @Override
    public void insert(Widget w, int beforeIndex) {
        super.insert(initializeWidget(w), beforeIndex);
        doLayout();
    }

    @Override
    public int getWidgetIndex(Widget child) {
        return super.getWidgetIndex(((LayoutData) child.getLayoutData()).container);
    }

    @Override
    public Widget getWidget(int index) {
        return ((SimplePanel) super.getWidget(index)).getWidget();
    }

    private final ArrayList<LayoutData> cache = new ArrayList<LayoutData>();

    /**
     * Do layout.
     */
    private void doLayout() {
        cache.clear();
        double fractionableSize = 100;
        for (Widget widget : this) {
            LayoutData layoutData = (LayoutData) widget.getLayoutData();
            double size = layoutData.size;
            if (size >= 0) {
                if (layoutDirection.equals(LayoutDirection.HORIZONTAL)) {
                    layoutData.container.setWidth(size + "%");
                } else {
                    layoutData.container.setHeight(size + "%");
                }
                fractionableSize -= size;
            } else {
                cache.add(layoutData);
            }
        }
        int rest = (int) (fractionableSize / cache.size());
        int extra = (int) (fractionableSize % cache.size());

        for (LayoutData layoutData : cache) {
            int size = rest;
            if (extra > 0) {
                size++;
                extra--;
            }
            String restSize = size + "%";
            if (layoutDirection.equals(LayoutDirection.HORIZONTAL)) {
                layoutData.container.setWidth(restSize);
            } else {
                layoutData.container.setHeight(restSize);
            }
        }
    }

    /**
     * Set cell size.
     * 
     * @param widget
     *            Widget
     * @param size
     *            size
     * @param unit
     *            Unit
     */
    public void setCellSize(Widget widget, int size, Unit unit) {
        LayoutData layoutData = getWidgetLayoutData(widget);
        assert unit.equals(Unit.PX) || unit.equals(Unit.PCT) : "The only units allowed are PX and PCT";
        if (unit.equals(Unit.PX)) {
            layoutData.size = size * 100 / getOffsetWidth();
        } else {
            layoutData.size = size;
        }
        doLayout();
    }

    /**
     * Get widget layout data.
     * 
     * @param widget
     *            Widget
     * @return Layout data
     */
    private LayoutData getWidgetLayoutData(Widget widget) {
        LayoutData layoutData = (LayoutData) widget.getLayoutData();
        if (layoutData == null) {
            return null;
        }
        assert getWidgetIndex(layoutData.container) != -1 : "Widget isn't child of this panel";
        return layoutData;
    }

    /**
     * Restore the cell width to auto.
     * 
     * @param widget
     *            Widget
     */
    public void setCellWidthAuto(Widget widget) {
        LayoutData widgetLayoutData = getWidgetLayoutData(widget);
        widgetLayoutData.size = (-1);
        doLayout();
    }

    /**
     * @param layoutDirection
     *            the layoutDirection to set
     */
    public void setLayoutDirection(LayoutDirection layoutDirection) {
        this.layoutDirection = layoutDirection;
        for (Widget c : this) {
            initializeContainer(c);
        }
        doLayout();
    }

    /**
     * @return the layoutDirection
     */
    public LayoutDirection getLayoutDirection() {
        return layoutDirection;
    }

    @Override
    public void clear() {
        for (Widget w : this) {
            try {
                SimplePanel simplePanel = (SimplePanel) w;
                Widget widget = simplePanel.getWidget();
                simplePanel.remove(widget);
            } catch (Exception e) {
                // NOTHING
            }
        }
        super.clear();
    }

    @Override
    public void onResize() {
        for (Widget child : getChildren()) {
            if (child instanceof RequiresResize) {
                ((RequiresResize) child).onResize();
            }
        }
    }
}
/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on May 5, 2014
 * @author michaellif
 */
package com.pyx4j.site.client.ui.layout;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.gwt.commons.layout.ILayoutable;
import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestHandler;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.DisplayPanel;

public abstract class ResponsiveLayoutPanel extends ComplexPanel implements RequiresResize, ProvidesResize, LayoutChangeRequestHandler {

    public static final int ANIMATION_TIME = 500;

    private LayoutType layoutType;

    private final Layout layout;

    private final Map<DisplayType, DisplayPanel> displays;

    public enum DisplayType {
        header, toolbar, menu, content, footer, communication, devConsole, extra1, extra2, extra3, extra4, notification
    }

    public ResponsiveLayoutPanel() {
        setElement(Document.get().createDivElement());
        layout = new Layout(getElement());

        displays = new HashMap<>();
        for (DisplayType display : DisplayType.values()) {
            displays.put(display, new DisplayPanel(display));
        }

        layoutType = LayoutType.getLayoutType(Window.getClientWidth());
    }

    public LayoutType getLayoutType() {
        return layoutType;
    }

    public Layout getLayout() {
        return layout;
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

    @Override
    protected void onLoad() {
        super.onLoad();
        forceLayout(0);
    }

    public final void forceLayout(int animationTime) {
        doLayout();
        onLayout(getDisplay(DisplayType.content), getLayoutType());
        layout.layout(animationTime);
        AppSite.getEventBus().fireEvent(new LayoutChangeEvent(getLayoutType()));
        resizeComponents();
    }

    public DisplayPanel getDisplay(DisplayType displayType) {
        return displays.get(displayType);
    }

    protected abstract void resizeComponents();

    protected abstract void doLayout();

    private void onLayout(IsWidget widget, LayoutType layoutType) {
        if (widget instanceof ILayoutable) {
            ILayoutable component = (ILayoutable) widget;
            component.doLayout(layoutType);
        }
        if (widget instanceof HasWidgets) {
            for (Widget childWidget : (HasWidgets) widget) {
                onLayout(childWidget, layoutType);
            }
        }
    }

}

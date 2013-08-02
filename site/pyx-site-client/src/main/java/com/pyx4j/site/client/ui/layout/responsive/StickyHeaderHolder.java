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
 * Created on Jun 19, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.layout.responsive;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.site.client.DisplayPanel;
import com.pyx4j.widgets.client.style.theme.HorizontalAlignCenterMixin;

public class StickyHeaderHolder extends SimplePanel implements RequiresResize {

    private final SimplePanel headerHolder;

    private final DisplayPanel headerDisplay;

    public StickyHeaderHolder(ResponsiveLayoutPanel parent) {

        headerHolder = new SimplePanel();
        headerHolder.getElement().getStyle().setZIndex(10);

        headerHolder.setStyleName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutStickyHeaderHolder.name());
        setWidget(headerHolder);

        this.headerDisplay = parent.getStickyHeaderDisplay();
        headerHolder.add(headerDisplay);

        parent.getStickyHeaderDisplay().getElement().getStyle().setProperty("maxWidth", ResponsiveLayoutPanel.MAX_WIDTH + "px");
        parent.getStickyHeaderDisplay().addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());

    }

    public void onPositionChange() {
        if (getWidget() != null && isAttached()) {
            if (getAbsoluteTop() > 0) {
                getWidget().getElement().getStyle().setPosition(Position.STATIC);
                getWidget().getElement().getStyle().setProperty("width", "auto");
                getElement().getStyle().setProperty("height", "auto");
            } else {
                //keeps space for fixed child
                getElement().getStyle().setHeight(getWidget().getOffsetHeight(), Unit.PX);
                getWidget().getElement().getStyle().setTop(0, Unit.PX);
                getWidget().getElement().getStyle().setLeft(0, Unit.PX);
                getWidget().getElement().getStyle().setRight(0, Unit.PX);
                getWidget().getElement().getStyle().setPosition(Position.FIXED);
                getWidget().getElement().getStyle().setWidth(getOffsetWidth(), Unit.PX);
            }
        }
    }

    @Override
    public void onResize() {
        if (headerDisplay instanceof RequiresResize) {
            ((RequiresResize) headerDisplay).onResize();
        }
    }

}

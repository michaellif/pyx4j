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
import com.google.gwt.user.client.ui.Widget;

public class StickyHeaderHolder extends SimplePanel implements RequiresResize {

    private final SimplePanel stickyHeaderContainer;

    public StickyHeaderHolder() {
        stickyHeaderContainer = new SimplePanel();
        stickyHeaderContainer.getElement().getStyle().setZIndex(10);
        super.setWidget(stickyHeaderContainer);
        stickyHeaderContainer.setStyleName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutStickyHeaderHolder.name());

    }

    @Override
    public void setWidget(Widget w) {
        stickyHeaderContainer.setWidget(w);
    }

    public void onPositionChange() {
        if (getWidget() != null && isAttached()) {
            if (getAbsoluteTop() >= 0) {
                getWidget().getElement().getStyle().setPosition(Position.STATIC);
                getWidget().getElement().getStyle().setProperty("width", "auto");
                getElement().getStyle().setProperty("height", "auto");
            } else {
                getWidget().getElement().getStyle().setTop(0, Unit.PX);
                getWidget().getElement().getStyle().setPosition(Position.FIXED);
                getWidget().getElement().getStyle().setWidth(getOffsetWidth(), Unit.PX);
                //keeps space for fixed child
                getElement().getStyle().setHeight(getWidget().getOffsetHeight(), Unit.PX);
            }
        }
    }

    @Override
    public void onResize() {
        if (stickyHeaderContainer.getWidget() instanceof RequiresResize) {
            ((RequiresResize) stickyHeaderContainer.getWidget()).onResize();
        }
    }
}

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
package com.pyx4j.site.client.frontoffice.ui.layout;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.site.client.DisplayPanel;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;

public class InlineMenuHolder extends SimplePanel {

    private final FrontOfficeLayoutPanel parent;

    public InlineMenuHolder(FrontOfficeLayoutPanel parent) {
        this.parent = parent;
    }

    public void setMenuDisplay(DisplayPanel display) {
        super.setWidget(display);
        onPositionChange();
        getElement().getStyle().setTop(0, Unit.PX);
        getElement().getStyle().setProperty("width", "auto");
        getElement().getStyle().setPosition(Position.ABSOLUTE);
    }

    public void onPositionChange() {
        if (getWidget() != null && isAttached()) {
            int offsetTop = parent.getDisplay(DisplayType.toolbar).getOffsetHeight();
            int offsetBottom = parent.getDisplay(DisplayType.footer).getAbsoluteTop();
            getWidget().setHeight("auto");

            if (getAbsoluteTop() >= offsetTop) {
                getWidget().getElement().getStyle().setPosition(Position.STATIC);
            } else {
                getWidget().getElement().getStyle().setPosition(Position.FIXED);
                if ((offsetTop + getWidget().getOffsetHeight()) <= offsetBottom) {
                    getWidget().getElement().getStyle().setProperty("top", offsetTop + "px");
                    getWidget().getElement().getStyle().setProperty("bottom", "auto");
                } else {
                    getWidget().getElement().getStyle().setProperty("bottom", Window.getClientHeight() - offsetBottom + "px");
                    getWidget().getElement().getStyle().setProperty("top", "auto");
                }
            }

        } else {
            getElement().getStyle().setWidth(0, Unit.PX);
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        onPositionChange();
    }

    public int getMenuWidth() {
        if (getWidget() == null) {
            return 0;
        } else {
            return getWidget().getElement().getOffsetWidth();
        }
    }
}

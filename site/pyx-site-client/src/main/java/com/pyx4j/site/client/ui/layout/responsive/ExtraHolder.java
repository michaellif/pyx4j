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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;

public class ExtraHolder extends SimplePanel {

    private final ResponsiveLayoutPanel parent;

    public ExtraHolder(ResponsiveLayoutPanel parent) {
        this.parent = parent;
        setWidget(parent.getExtraDisplay());
        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        getWidget().getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
    }

    public void onPositionChange() {
        if (getWidget() != null && isAttached()) {
            int offsetTop = parent.getToolbarDisplay().getOffsetHeight();
            int offsetBottom = parent.getFooterHolder().getAbsoluteTop();
            getWidget().setHeight("auto");

            //TODO investigate why container's getAbsoluteTop() changes when child's position changes from STATIC to FIXED
            //Workaround - use 10px threshold 
            if (getAbsoluteTop() > offsetTop + 10) {
                getWidget().getElement().getStyle().setPosition(Position.STATIC);
                getElement().getStyle().setProperty("width", "auto");
            } else if (getAbsoluteTop() < offsetTop - 10) {
                getWidget().getElement().getStyle().setPosition(Position.FIXED);
                getElement().getStyle().setWidth(getWidget().getOffsetWidth(), Unit.PX);
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
}

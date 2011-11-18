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
 * Created on Apr 23, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.actionbar;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.DefaultWidgetsTheme;

/**
 * @author michaellif
 * 
 */
public class Toolbar extends FlowPanel {

    public Toolbar() {
        setStyleName(DefaultWidgetsTheme.StyleName.Toolbar.name());
    }

    public void addItem(Widget widget) {
        insertItem(widget, getWidgetCount(), false);
    }

    public void addItem(Widget widget, boolean floatRight) {
        insertItem(widget, getWidgetCount(), floatRight);
    }

    public void insertItem(Widget widget, int beforeIndex, boolean floatRight) {
        insert(widget, beforeIndex);
        widget.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        widget.getElement().getStyle().setMarginLeft(6, Unit.PX);

        if (floatRight) {
            widget.getElement().getStyle().setProperty("cssFloat", "right");
        }
    }

    public BarSeparator insertSeparator(int beforeIndex) {
        BarSeparator separator = new BarSeparator();
        insert(separator, beforeIndex);
        return separator;
    }

}
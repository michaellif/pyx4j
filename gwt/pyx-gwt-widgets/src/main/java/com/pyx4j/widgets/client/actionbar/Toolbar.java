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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
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

    @Override
    public void add(Widget widget) {
        insert(widget, getWidgetCount());
    }

    @Override
    public void insert(IsWidget widget, int beforeIndex) {
        insert(widget, beforeIndex);
    }

    @Override
    public void insert(Widget widget, int beforeIndex) {
        SimplePanel itemHolder = new SimplePanel();
        itemHolder.setStyleName(DefaultWidgetsTheme.StyleName.ToolbarItem.name());

        itemHolder.setWidget(widget);
        super.insert(itemHolder, beforeIndex);
    }

    public BarSeparator insertSeparator(int beforeIndex) {
        BarSeparator separator = new BarSeparator();
        insert(separator, beforeIndex);
        return separator;
    }

}
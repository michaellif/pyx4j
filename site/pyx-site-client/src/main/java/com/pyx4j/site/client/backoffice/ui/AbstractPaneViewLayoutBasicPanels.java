/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 8, 2016
 * @author vlads
 */
package com.pyx4j.site.client.backoffice.ui;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;

public class AbstractPaneViewLayoutBasicPanels extends DockPanel implements AbstractPaneViewLayout {

    public AbstractPaneViewLayoutBasicPanels() {
        setSize("100%", "100%");
    }

    @Override
    public void setWidgetHeight(Widget widget, double height) {
        setCellHeight(widget, String.valueOf(height));
    }

    @Override
    public void setCenter(Widget widget) {
        add(widget, DockPanel.CENTER);
    }

    @Override
    public void addNorth(Widget widget, double height) {
        add(widget, DockPanel.NORTH);
    }

    @Override
    public void addSouth(Widget widget, double height) {
        add(widget, DockPanel.SOUTH);
    }

}

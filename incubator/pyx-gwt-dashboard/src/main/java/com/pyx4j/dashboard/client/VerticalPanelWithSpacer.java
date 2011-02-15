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
 * Created on 2011-02-14
 * @author VladLL
 * @version $Id$
 */
package com.pyx4j.dashboard.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * {@link VerticalPanel} which has a permanent spacer at the end to prevent CSS collapse
 * of the panel and its parent.
 */
public class VerticalPanelWithSpacer extends FlowPanel /* VerticalPanel */{
    private static final String CSS_DASHBOARD_PANEL_SPACER = "DashboardPanel-spacer";

    public VerticalPanelWithSpacer() {
        clear();
    }

    @Override
    public void add(Widget w) {
        super.insert(w, getWidgetCount());
    }

    @Override
    public void insert(Widget w, int beforeIndex) {
        if (beforeIndex == super.getWidgetCount()) {
            --beforeIndex;
        }

        super.insert(w, beforeIndex);
    }

    @Override
    public int getWidgetCount() {
        return (super.getWidgetCount() - 1);
    }

    @Override
    public void clear() {
        super.clear();
        Label spacerLabel = new Label("");
        spacerLabel.setStylePrimaryName(CSS_DASHBOARD_PANEL_SPACER);
        super.add(spacerLabel);
    }
}

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
 * Created on Nov 12, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class DeckLayoutPanel extends LayoutPanel {

    public DeckLayoutPanel() {
        setStyleName("DeckLayoutPanel");
    }

    @Override
    public void insert(Widget widget, int beforeIndex) {
        super.insert(widget, beforeIndex);
        setWidgetVisible(widget, false);
    }

    public void showWidget(int index) {
        for (int i = 0; i < getWidgetCount(); i++) {
            if (index == i) {
                setWidgetVisible(getWidget(i), true);
            } else {
                setWidgetVisible(getWidget(i), false);
            }
        }

    }

}

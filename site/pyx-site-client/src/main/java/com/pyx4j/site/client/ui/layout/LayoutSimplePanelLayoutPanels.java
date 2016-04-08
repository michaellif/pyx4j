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
package com.pyx4j.site.client.ui.layout;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class LayoutSimplePanelLayoutPanels extends LayoutPanel implements AbstractSimpleLayoutPanel {

    @Override
    public Widget getWidget() {
        if (getWidgetCount() == 0) {
            return null;
        } else {
            return getWidget(0);
        }
    }

    @Override
    public void setWidget(IsWidget widget) {
        this.clear();
        this.add(widget);
    }

}

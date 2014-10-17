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
 * Created on Apr 18, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.frontoffice.ui.layout.RequiresScroll;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;

public class DisplayPanel extends SimplePanel implements RequiresResize, ProvidesResize {

    public DisplayPanel(DisplayType display) {
        ensureDebugId(getClass().getSimpleName() + "." + display.name());
    }

    @Override
    public void onResize() {
        Widget child = getWidget();
        if ((child != null) && (child instanceof RequiresResize)) {
            ((RequiresResize) child).onResize();
        }
    }

    public void onScroll(int scrollPosition) {
        if (getWidget() instanceof RequiresScroll) {
            ((RequiresScroll) getWidget()).onScroll(scrollPosition);
        }
    }

}
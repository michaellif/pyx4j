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
 * Created on Jun 13, 2014
 * @author michaellif
 */
package com.pyx4j.site.client.backoffice.ui.layout;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;

public class InlineNavigationHolder extends DockLayoutPanel {

    private final BackOfficeLayoutPanel parent;

    private final SimplePanel inlineMenuHolder;

    private boolean empty = true;

    public InlineNavigationHolder(BackOfficeLayoutPanel parent) {
        super(Unit.PX);
        this.parent = parent;
        inlineMenuHolder = new SimplePanel();
    }

    public void layout() {
        clear();

        if (parent.getDisplay(DisplayType.footer).getWidget() != null) {
            addSouth(parent.getDisplay(DisplayType.footer), 45);
            empty = false;
        }

        add(inlineMenuHolder);

        if (parent.getDisplay(DisplayType.menu).getWidget() != null) {
            parent.getDisplay(DisplayType.menu).setHeight("100%");
            inlineMenuHolder.setWidget(parent.getDisplay(DisplayType.menu));
            empty = false;
        }
    }

    public boolean isEmpty() {
        return empty;
    }

    @Override
    public void clear() {
        super.clear();
        empty = true;
    }
}

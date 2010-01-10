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
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ResizibleScrollPanel extends SimplePanel implements ProvidesResize, RequiresResize {

    private final SimplePanel contentPanel;

    //TODO check that RIA folder uses this class
    public ResizibleScrollPanel() {

        DOM.setStyleAttribute(getElement(), "position", "relative");
        setSize("100%", "100%");

        contentPanel = new SimplePanel();

        add(contentPanel);

        contentPanel.setSize("100%", "100%");

        DOM.setStyleAttribute(contentPanel.getElement(), "overflow", "auto");
        DOM.setStyleAttribute(contentPanel.getElement(), "position", "absolute");
        DOM.setStyleAttribute(contentPanel.getElement(), "top", "0px");
        DOM.setStyleAttribute(contentPanel.getElement(), "left", "0px");

    }

    public void onResize() {
        contentPanel.setWidth(getOffsetWidth() + "px");
        contentPanel.setHeight(getOffsetHeight() + "px");
    }

    public void setContentWidget(Widget w) {
        contentPanel.add(w);
    }
}

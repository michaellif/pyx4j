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
package com.pyx4j.site.client.frontoffice.ui.layout;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;

public class StickyToolbarHolder extends SimplePanel implements RequiresResize {

    private final FrontOfficeLayoutPanel parent;

    public StickyToolbarHolder(FrontOfficeLayoutPanel parent) {
        this.parent = parent;

        getElement().getStyle().setZIndex(10);

        setStyleName(FrontOfficeLayoutTheme.StyleName.FrontOfficeLayoutStickyToolbarHolder.name());

        getElement().getStyle().setTop(0, Unit.PX);
        getElement().getStyle().setLeft(0, Unit.PX);
        getElement().getStyle().setPosition(Position.ABSOLUTE);

    }

    public void setDisplay() {
        setWidget(parent.getDisplay(DisplayType.toolbar));
    }

    @Override
    public void onResize() {
        setWidth(parent.getPageWidth() + "px");
    }

}

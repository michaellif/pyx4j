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
 * Created on Jun 9, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.layout.backoffice;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

import com.pyx4j.site.client.DisplayPanel;
import com.pyx4j.site.client.ui.layout.OverlayExtraHolder;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;

public class ContentHolder extends FlowPanel implements RequiresResize, ProvidesResize {

    private final DisplayPanel contentPanel;

    public ContentHolder(BackOfficeLayoutPanel parent, OverlayExtraHolder overlayActionsPanel) {

        contentPanel = parent.getDisplay(DisplayType.content);
        contentPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
        contentPanel.getElement().getStyle().setTop(0, Unit.PX);
        contentPanel.getElement().getStyle().setBottom(0, Unit.PX);
        contentPanel.getElement().getStyle().setLeft(0, Unit.PX);
        contentPanel.getElement().getStyle().setRight(0, Unit.PX);
        add(contentPanel);

        add(overlayActionsPanel);
    }

    @Override
    public void onResize() {
        contentPanel.onResize();
    }

}

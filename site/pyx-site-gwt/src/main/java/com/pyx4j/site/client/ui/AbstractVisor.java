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
 * Created on Mar 14, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;

public abstract class AbstractVisor extends DockLayoutPanel {

    private final FlowPanel header;

    public AbstractVisor(IsWidget widget, String caption, final IView parent) {
        super(Unit.EM);
        setStyleName(DefaultSiteCrudPanelsTheme.StyleName.Visor.name());

        header = new FlowPanel();
        header.addStyleName(DefaultSiteCrudPanelsTheme.StyleName.VisorHeader.name());
        addNorth(header, 2.5);

        Label captionLabel = new Label(caption);
        captionLabel.addStyleName(DefaultSiteCrudPanelsTheme.StyleName.VisorCaption.name());
        header.add(captionLabel);

        add(widget.asWidget());

    }

    protected FlowPanel getHeader() {
        return header;
    }

}

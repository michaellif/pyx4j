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
 * Created on Apr 24, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.essentials.client.crud;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ActionsBarPanel extends ActionsPanel {

    private final HorizontalPanel contentPanel;

    public ActionsBarPanel() {
        setWidth("100%");
        getElement().getStyle().setPadding(2, Unit.PX);
        getElement().getStyle().setMarginBottom(10, Unit.PX);

        contentPanel = new HorizontalPanel();
        add(contentPanel);
    }

    public void addItem(String name, ClickHandler handler) {
        Anchor anchor = new Anchor(name);
        anchor.getElement().getStyle().setMargin(10, Unit.PX);

        if (handler != null) {
            anchor.addClickHandler(handler);
        }

        contentPanel.add(anchor);

    }

}

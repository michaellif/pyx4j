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
 * Created on Feb 24, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.datatable;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.entity.client.EntityCSSClass;

public class DataTableActionsBar extends HorizontalPanel {

    public DataTableActionsBar(final DataTable<?> dataTable, ClickHandler prevHandler, ClickHandler nextHandler) {
        setStyleName(EntityCSSClass.pyx4j_Entity_DataTableActionsBar.name());
        setWidth("100%");
        HorizontalPanel contentPanel = new HorizontalPanel();
        add(contentPanel);
        setCellHorizontalAlignment(contentPanel, HorizontalPanel.ALIGN_RIGHT);

        final Anchor prevAnchor = new Anchor("&lt;&nbsp;Prev", true);
        prevAnchor.setEnabled(false);
        prevAnchor.addClickHandler(prevHandler);
        contentPanel.add(prevAnchor);
        prevAnchor.getElement().getStyle().setMarginRight(10, Unit.PX);

        final Anchor nextAnchor = new Anchor("Next&nbsp;&gt;", true);
        nextAnchor.addClickHandler(nextHandler);
        contentPanel.add(nextAnchor);

        getElement().getStyle().setProperty("textAlign", "right");
        getElement().getStyle().setProperty("padding", "6px");

    }
}

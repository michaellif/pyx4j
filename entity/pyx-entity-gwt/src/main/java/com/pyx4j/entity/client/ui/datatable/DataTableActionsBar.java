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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.EntityCSSClass;

public class DataTableActionsBar extends SimplePanel {

    private final DataTable dataTable;

    public DataTableActionsBar(DataTable dataTable) {
        this.dataTable = dataTable;
        setStyleName(EntityCSSClass.pyx4j_Entity_DataTableActionsBar.name());

        setWidget(new HTML("<span style='font-size: 0.81em;'>"

        + "<a href=''>&nbsp;&lt;&nbsp;Prev&nbsp;20</a>"

        + "<span style='padding: 0px 5px 0px 5px;'><b>1</b> - <b>20</b></span>"

        + "<a href=''>&nbsp;Next&nbsp;20&nbsp;&gt;</a><span>"));
        getElement().getStyle().setProperty("textAlign", "right");
        setHeight("40px");
    }
}

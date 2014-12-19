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
 * Created on Jun 24, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.datatable.sectiontable;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.datatable.DataItem;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;

public class BasicSectionTableRow implements ISection {

    private final FlowPanel contentPanel;

    private SectionTable parent;

    private final DataItem<?> item;

    public BasicSectionTableRow(DataItem<?> item) {
        this.item = item;
        contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setProperty("display", "table-row");
    }

    @Override
    public void setParent(SectionTable parent) {
        this.parent = parent;
        render();
    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

    private void render() {
        for (ColumnDescriptor columnDescriptor : parent.getColumnDescriptors()) {
            HTML cell = new HTML(item.getCellValue(columnDescriptor).toString());
            cell.getElement().getStyle().setProperty("display", "table-cell");
            contentPanel.add(cell);
        }
    }

}

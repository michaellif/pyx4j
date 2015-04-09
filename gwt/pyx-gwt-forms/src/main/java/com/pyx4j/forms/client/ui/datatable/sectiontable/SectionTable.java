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

import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;

public class SectionTable implements IsWidget {

    private final FlowPanel contentPanel;

    private final FlowPanel sectionsPanel;

    private TableHeader tableHeader;

    private final List<ColumnDescriptor> columnDescriptors;

    public SectionTable(List<ColumnDescriptor> columnDescriptors) {
        this.columnDescriptors = columnDescriptors;
        contentPanel = new FlowPanel();

        sectionsPanel = new FlowPanel();

        if (columnDescriptors != null) {
            tableHeader = new TableHeader();
            contentPanel.add(tableHeader);

            contentPanel.add(new ScrollPanel(sectionsPanel));
        }
    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

    public void addSection(ISection section) {
        sectionsPanel.add(section);
        section.setParent(this);
    }

    public List<ColumnDescriptor> getColumnDescriptors() {
        return columnDescriptors;
    }

    protected class TableHeader extends FlowPanel {
        TableHeader() {
            getElement().getStyle().setProperty("display", "table-row");
            for (ColumnDescriptor columnDescriptor : columnDescriptors) {
                HTML cell = new HTML(columnDescriptor.getColumnTitle());
                cell.getElement().getStyle().setProperty("display", "table-cell");
                add(cell);
            }
        }
    }

}

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
 * Created on Jun 23, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.datatable;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.forms.client.ui.datatable.sectiontable.BasicSectionTableRow;
import com.pyx4j.forms.client.ui.datatable.sectiontable.SectionTable;

public class SectionTablePane<E extends IEntity> implements ITablePane<E> {

    private final SimplePanel contentHolder;

    private SectionTable contentPanel;

    private final DataTable<E> dataTable;

    public SectionTablePane(final DataTable<E> dataTable) {
        this.dataTable = dataTable;
        contentHolder = new SimplePanel();
    }

    @Override
    public Widget asWidget() {
        return contentHolder.asWidget();
    }

    @Override
    public void setItemZoomInCommand(ItemZoomInCommand<E> itemZoomInCommand) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isItemZoomInAvailable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void renderTable() {

        DataTableModel<E> model = dataTable.getDataTableModel();

        contentPanel = new SectionTable(dataTable.getColumnDescriptors());
        contentHolder.setWidget(contentPanel);

        for (DataItem<?> item : model.getData()) {
            contentPanel.addSection(new BasicSectionTableRow(item));
        }
    }

    @Override
    public void updateSelectionHighlights() {
        // TODO Auto-generated method stub

    }
}

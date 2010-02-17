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
 * Created on May 8, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.datatable;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.client.laf.DataTableUI;
import com.pyx4j.client.laf.UIManager;
import com.pyx4j.client.widgets.dataTable.DataTableModelEvent.Type;
import com.pyx4j.domain.Entity;

public class DataTable<E extends Entity> extends FlexTable implements DataTableModelListener, DataTableUI {

    private final DataTableModel<E> model;

    private int selectedRow = -1;

    private final boolean checkboxColumnShown;

    private final ArrayList<SelectionCheckBox> selectionCheckBoxes = new ArrayList<SelectionCheckBox>();

    private SelectionCheckBox selectionCheckBoxAll;

    private static final int HEADER_RAW_INDEX = 0;

    private static final String CHECK_MARK_COLUMN_SIZE = "22px";

    public DataTable(DataTableModel<E> model, boolean checkboxColumnShown) {
        super();
        this.checkboxColumnShown = checkboxColumnShown;
        this.model = model;
        this.addTableListener(model);
        model.addDataTableModelListener(this);
        DOM.setStyleAttribute(getElement(), "tableLayout", "fixed");
        renderTable();
    }

    private void renderBody() {
        List<DataItem> data = model.getData();
        List<ColumnDescriptor<E>> columnDescriptors = model.getColumnDescriptors();

        int rowIndex = 1;
        for (DataItem dataItem : data) {
            int colIndex = 0;
            if (checkboxColumnShown) {
                SelectionCheckBox selectionCheckBox = new SelectionCheckBox(rowIndex, dataItem.isChecked());
                selectionCheckBoxes.add(selectionCheckBox);

                selectionCheckBox.setWidth(CHECK_MARK_COLUMN_SIZE);
                this.setWidget(rowIndex, 0, selectionCheckBox);
                this.getCellFormatter().setWidth(rowIndex, 0, CHECK_MARK_COLUMN_SIZE);
                colIndex = 1;
            }

            for (ColumnDescriptor<?> columnDescriptor : columnDescriptors) {
                Object value = dataItem.getCellValue(columnDescriptor);
                if (value == null || value.equals("")) {
                    this.setHTML(rowIndex, colIndex, "&nbsp;");
                } else {
                    this.setHTML(rowIndex, colIndex, value.toString());
                }
                this.getCellFormatter().setWidth(rowIndex, colIndex, columnDescriptor.getWidth());
                this.getCellFormatter().setWordWrap(rowIndex, colIndex, columnDescriptor.isWordWrap());
                colIndex++;
            }
            rowIndex++;
        }
        this.ensureDebugId(model.getDebugId());
    }

    public void clearTableData() {
        selectionCheckBoxes.clear();
        setSelectedRow(-1);
        for (int row = getRowCount() - 1; row > 0; row--) {
            removeRow(row);
        }
    }

    private void renderHeader() {
        if (getRowCount() > 0) {
            removeCells(0, 0, getCellCount(0));
        }
        CellFormatter cellFormatter = getCellFormatter();
        int colIndex = 0;
        if (checkboxColumnShown) {
            colIndex = 1;
            selectionCheckBoxAll = new SelectionCheckBox(HEADER_RAW_INDEX, model.isAllChecked());
            this.setWidget(0, 0, selectionCheckBoxAll);
            this.getCellFormatter().setWidth(0, 0, CHECK_MARK_COLUMN_SIZE);
        }
        List<ColumnDescriptor<E>> columnDescriptors = model.getColumnDescriptors();
        for (ColumnDescriptor<E> columnDescriptor : columnDescriptors) {
            String columnTitle = columnDescriptor.getColumnTitle();
            StringBuffer headerText = new StringBuffer();
            headerText.append(columnTitle);
            if (columnDescriptor.equals(model.getSortColumn())) {
                if (columnDescriptor.isSortAscending()) {
                    headerText.append("&nbsp;&#x2191;");
                } else {
                    headerText.append("&nbsp;&#x2193;");
                }
            }
            this.setHTML(0, colIndex, headerText.toString());
            this.getCellFormatter().setWidth(0, colIndex, columnDescriptor.getWidth());
            this.getCellFormatter().setWordWrap(0, colIndex, false);
            cellFormatter.setAlignment(0, colIndex, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
            colIndex++;
        }

    }

    private void renderTable() {
        clear();
        clearTableData();
        renderHeader();
        renderBody();
        updateLookAndFeel();
    }

    public void onTableModelChanged(DataTableModelEvent e) {
        if (e.getType().equals(Type.REBUILD)) {
            renderTable();
        }
    }

    public void updateLookAndFeel() {
        UIManager.getLookAndFeel().getDataTableLaF().installStyle(this);
    }

    public DataTableModel<E> getDataTableModel() {
        return model;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
        updateLookAndFeel();
    }

    public boolean isCheckboxColumnShown() {
        return checkboxColumnShown;
    }

    class SelectionCheckBox extends CheckBox {

        public SelectionCheckBox(final int rowIndex, boolean checked) {
            super();
            setChecked(checked);
            addClickListener(new ClickListener() {

                public void onClick(Widget sender) {
                    if (rowIndex == HEADER_RAW_INDEX) {
                        for (DataItem dataItem : model.getData()) {
                            dataItem.setChecked(isChecked());
                            for (SelectionCheckBox selectionCheckBox : selectionCheckBoxes) {
                                selectionCheckBox.setChecked(isChecked());
                            }
                        }
                    } else {
                        boolean allChecked = true;
                        model.setRowChecked(isChecked(), rowIndex - 1);
                        for (SelectionCheckBox selectionCheckBox : selectionCheckBoxes) {
                            if (!selectionCheckBox.isChecked()) {
                                allChecked = false;
                                break;
                            }
                        }
                        selectionCheckBoxAll.setChecked(allChecked);
                    }
                }
            });
        }
    }

}

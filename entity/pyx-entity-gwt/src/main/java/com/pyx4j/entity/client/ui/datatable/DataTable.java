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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.UIObject;

import com.pyx4j.entity.client.EntityCSSClass;
import com.pyx4j.entity.shared.IEntity;

public class DataTable<E extends IEntity> extends FlexTable implements DataTableModelListener {

    private static final Logger log = LoggerFactory.getLogger(DataTable.class);

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
        this.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Cell cell = getCellForEvent(event);
                if (selectedRow >= 0) {
                    Element previous = getRowFormatter().getElement(selectedRow);
                    UIObject.setStyleName(previous, EntityCSSClass.pyx4j_Entity_DataTableRow.name() + "-selected", false);
                }
                selectedRow = cell.getRowIndex();
                Element current = getRowFormatter().getElement(selectedRow);
                UIObject.setStyleName(current, EntityCSSClass.pyx4j_Entity_DataTableRow.name() + "-selected", true);
            }
        });
        model.addDataTableModelListener(this);
        setStyleName(EntityCSSClass.pyx4j_Entity_DataTable.name());
        DOM.setStyleAttribute(getElement(), "tableLayout", "fixed");
        renderTable();
    }

    private void renderBody() {
        List<DataItem<E>> data = model.getData();
        List<ColumnDescriptor<E>> columnDescriptors = model.getColumnDescriptors();

        int rowIndex = 1;
        for (DataItem<E> dataItem : data) {
            int colIndex = 0;
            if (checkboxColumnShown) {
                SelectionCheckBox selectionCheckBox = new SelectionCheckBox(rowIndex, dataItem.isChecked());
                selectionCheckBoxes.add(selectionCheckBox);

                selectionCheckBox.setWidth(CHECK_MARK_COLUMN_SIZE);
                setWidget(rowIndex, 0, selectionCheckBox);
                getCellFormatter().setWidth(rowIndex, 0, CHECK_MARK_COLUMN_SIZE);
                colIndex = 1;
            }

            for (ColumnDescriptor<E> columnDescriptor : columnDescriptors) {
                Object value = dataItem.getCellValue(columnDescriptor);
                if (value == null || value.equals("")) {
                    setHTML(rowIndex, colIndex, "&nbsp;");
                } else {
                    setHTML(rowIndex, colIndex, value.toString());
                }
                getCellFormatter().setWidth(rowIndex, colIndex, columnDescriptor.getWidth());
                getCellFormatter().setWordWrap(rowIndex, colIndex, columnDescriptor.isWordWrap());
                colIndex++;
            }
            Element rowElement = getRowFormatter().getElement(rowIndex);
            UIObject.setStyleName(rowElement, EntityCSSClass.pyx4j_Entity_DataTableRow.name());
            if (rowIndex % 2 == 0) {
                UIObject.setStyleName(rowElement, EntityCSSClass.pyx4j_Entity_DataTableRow.name() + "-even", true);
            } else {
                UIObject.setStyleName(rowElement, EntityCSSClass.pyx4j_Entity_DataTableRow.name() + "-odd", true);
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
        Element rowElement = getRowFormatter().getElement(0);
        UIObject.setStyleName(rowElement, EntityCSSClass.pyx4j_Entity_DataTableHeader.name());

    }

    private void renderTable() {
        clear();
        clearTableData();
        renderHeader();
        renderBody();
    }

    public void onTableModelChanged(DataTableModelEvent e) {
        if (e.getType().equals(DataTableModelEvent.Type.REBUILD)) {
            renderTable();
        }
    }

    public DataTableModel<E> getDataTableModel() {
        return model;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }

    public boolean isCheckboxColumnShown() {
        return checkboxColumnShown;
    }

    class SelectionCheckBox extends CheckBox {

        public SelectionCheckBox(final int rowIndex, boolean checked) {
            super();
            setValue(checked);
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (rowIndex == HEADER_RAW_INDEX) {
                        for (DataItem dataItem : model.getData()) {
                            dataItem.setChecked(getValue());
                            for (SelectionCheckBox selectionCheckBox : selectionCheckBoxes) {
                                selectionCheckBox.setValue(getValue());
                            }
                        }
                    } else {
                        boolean allChecked = true;
                        model.setRowChecked(getValue(), rowIndex - 1);
                        for (SelectionCheckBox selectionCheckBox : selectionCheckBoxes) {
                            if (!selectionCheckBox.getValue()) {
                                allChecked = false;
                                break;
                            }
                        }
                        selectionCheckBoxAll.setValue(allChecked);
                    }
                }
            });
        }
    }

}

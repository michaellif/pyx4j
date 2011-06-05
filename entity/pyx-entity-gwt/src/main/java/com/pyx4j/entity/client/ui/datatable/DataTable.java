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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.UIObject;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class DataTable<E extends IEntity> extends FlexTable implements DataTableModelListener {

    // CSS style names: 
    public static String BASE_NAME = "pyx4j_Entity_DataTable";

    public static enum StyleSuffix implements IStyleSuffix {
        Row, Header, ActionsBar
    }

    public static enum StyleDependent implements IStyleDependent {
        disabled, selected, hover, even, odd, nodetails
    }

    private static final Logger log = LoggerFactory.getLogger(DataTable.class);

    private DataTableModel<E> model;

    private int selectedRow = -1;

    private final boolean checkboxColumnShown;

    private boolean hasDetailsNavigation;

    private final ArrayList<SelectionCheckBox> selectionCheckBoxes = new ArrayList<SelectionCheckBox>();

    private SelectionCheckBox selectionCheckBoxAll;

    private static final int HEADER_RAW_INDEX = 0;

    private static final String CHECK_MARK_COLUMN_SIZE = "22px";

    public DataTable(DataTableModel<E> model, boolean checkboxColumnShown) {
        this(checkboxColumnShown);
        setDataTableModel(model);
    }

    public DataTable(boolean checkboxColumnShown) {
        super();
        this.checkboxColumnShown = checkboxColumnShown;
        this.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Cell cell = getCellForEvent(event);
                if (cell == null || cell.getRowIndex() == 0) {
                    return; // do not process empty and header clicks!...
                }
                setSelectedRow(cell.getRowIndex() - 1); // actual table row index - without the header!...
            }
        });
        setStyleName(BASE_NAME);
        DOM.setStyleAttribute(getElement(), "tableLayout", "fixed");
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
                    setWidget(rowIndex, colIndex, new HTML("&nbsp;"));
                } else {
                    HTML contentHtml = new HTML(value.toString());
                    if (!columnDescriptor.isWordWrap()) {
                        contentHtml.getElement().getStyle().setProperty("overflow", "hidden");
                        contentHtml.getElement().getStyle().setMarginRight(5, Unit.PX);
                    }
                    setWidget(rowIndex, colIndex, contentHtml);
                }
                getCellFormatter().setWidth(rowIndex, colIndex, columnDescriptor.getWidth());
                getCellFormatter().setWordWrap(rowIndex, colIndex, columnDescriptor.isWordWrap());
                colIndex++;
            }
            Element rowElement = getRowFormatter().getElement(rowIndex);
            UIObject.setStyleName(rowElement, BASE_NAME + StyleSuffix.Row);
            if (rowIndex % 2 == 0) {
                UIObject.setStyleName(rowElement, BASE_NAME + StyleSuffix.Row + "-" + StyleDependent.even.name(), true);
            } else {
                UIObject.setStyleName(rowElement, BASE_NAME + StyleSuffix.Row + "-" + StyleDependent.odd.name(), true);
            }
            if (!hasDetailsNavigation()) {
                UIObject.setStyleName(rowElement, BASE_NAME + StyleSuffix.Row + "-" + StyleDependent.nodetails.name(), true);
            }

            rowIndex++;
        }
        this.ensureDebugId(model.getDebugId());
    }

    public void clearTableData() {
        setSelectedRow(-1);
        selectionCheckBoxes.clear();
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
        UIObject.setStyleName(rowElement, BASE_NAME + StyleSuffix.Header);

    }

    private void renderTable() {
        clear();
        clearTableData();
        renderHeader();
        renderBody();
    }

    @Override
    public void onTableModelChanged(DataTableModelEvent e) {
        if (e.getType().equals(DataTableModelEvent.Type.REBUILD)) {
            renderTable();
        }
    }

    public DataTableModel<E> getDataTableModel() {
        return model;
    }

    public void setDataTableModel(DataTableModel<E> model) {
        if (this.model != null) {
            this.model.removeDataTableModelListener(this);
        }
        this.model = model;
        model.addDataTableModelListener(this);
        renderTable();
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    protected void setSelectedRow(int selectedRow) {

        if (this.selectedRow >= 0) {
            Element previous = getRowFormatter().getElement(this.selectedRow + 1); // raw table row index - including the header!...
            UIObject.setStyleName(previous, BASE_NAME + StyleSuffix.Row + "-" + DataTable.StyleDependent.selected.name(), false);
        }

        this.selectedRow = selectedRow; // actual table row index

        if (this.selectedRow >= 0) {
            Element current = getRowFormatter().getElement(this.selectedRow + 1); // raw table row index - including the header!...
            UIObject.setStyleName(current, BASE_NAME + StyleSuffix.Row + "-" + DataTable.StyleDependent.selected.name(), true);
        }
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

    public boolean hasDetailsNavigation() {
        return hasDetailsNavigation;
    }

    public void setHasDetailsNavigation(boolean hasDetailsNavigation) {
        this.hasDetailsNavigation = hasDetailsNavigation;
    }

}

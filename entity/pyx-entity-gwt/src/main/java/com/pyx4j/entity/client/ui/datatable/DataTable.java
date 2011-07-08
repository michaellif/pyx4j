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
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class DataTable<E extends IEntity> extends FlexTable implements DataTableModelListener {

    // CSS style names: 
    public static String BASE_NAME = "pyx4j_Entity_DataTable";

    public static enum StyleSuffix implements IStyleSuffix {
        Row, Header, ActionsBar, ColumnSelector, ColumnMenu
    }

    public static enum StyleDependent implements IStyleDependent {
        disabled, selected, hover, even, odd, nodetails
    }

    // Events:
    public interface SortChangeHandler<E> {
        void onChange(ColumnDescriptor<E> column);
    }

    public interface CheckSelectionHandler {
        void onCheck(boolean isAnyChecked);
    }

    // Data:
    private static final Logger log = LoggerFactory.getLogger(DataTable.class);

    private DataTableModel<E> model;

    private List<ColumnDescriptor<E>> availableColumns;

    private int selectedRow = -1;

    private boolean hasDetailsNavigation = false;

    private boolean columnClickSorting = false;

    private boolean autoColumnsWidth = false;

    private boolean checkboxColumnShown = false;

    private final List<SelectionCheckBox> selectionCheckBoxes = new ArrayList<SelectionCheckBox>();

    private SelectionCheckBox selectionCheckBoxAll;

    private static final int HEADER_RAW_INDEX = 0;

    private static final String CHECK_MARK_COLUMN_SIZE = "22px";

    private static final String COLUMNS_SELECTOR_COLUMN_SIZE = "12px";

    private List<SortChangeHandler<E>> sortChangeHandlers;

    private List<CheckSelectionHandler> checkSelectionHandlers;

    public DataTable() {
        setStyleName(BASE_NAME);
        DOM.setStyleAttribute(getElement(), "tableLayout", "fixed");

        this.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Cell cell = getCellForEvent(event);
                if (cell == null) {
                    return; // do not process empty clicks!...
                } else if (cell.getRowIndex() == 0) {
                    if (cell.getCellIndex() >= (isCheckboxColumnShown() ? 1 : 0) && cell.getCellIndex() < getCellCount(0) - (isUseColumnSelector() ? 1 : 0)) {
                        processHeaderClick(isCheckboxColumnShown() ? cell.getCellIndex() - 1 : cell.getCellIndex()); // actual table column index - without the first check one!...
                    }
                } else if (cell.getCellIndex() >= (isCheckboxColumnShown() ? 1 : 0)) {
                    setSelectedRow(cell.getRowIndex() - 1); // actual table row index - without the header!...
                }
            }
        });
    }

    public DataTable(DataTableModel<E> model) {
        this();
        setDataTableModel(model);
    }

    public void renderTable() {
        removeAllRows();

        // auto calculate column widths (actually make them equals in % and fill all 100%):
        if (isAutoColumnsWidth() && !model.getColumnDescriptors().isEmpty()) {
            int width = (int) (100. / model.getColumnDescriptors().size() + 0.5);
            for (ColumnDescriptor<E> columnDescriptor : model.getColumnDescriptors()) {
                columnDescriptor.setWidth(width + "%");
            }
        }

        renderHeader();
        renderBody();
    }

    private void renderHeader() {
        if (getRowCount() > 0) {
            removeCells(0, 0, getCellCount(0));
        }

        int colIndex = 0;
        if (isCheckboxColumnShown()) {
            selectionCheckBoxAll = new SelectionCheckBox(HEADER_RAW_INDEX, model.isAllChecked());
            setWidget(0, 0, selectionCheckBoxAll);
            getColumnFormatter().setWidth(colIndex, CHECK_MARK_COLUMN_SIZE);
            getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
            ++colIndex;
        }

        for (ColumnDescriptor<E> columnDescriptor : model.getColumnDescriptors()) {
            String columnTitle = columnDescriptor.getColumnTitle();
            StringBuffer headerText = new StringBuffer("&nbsp;");
            headerText.append(columnTitle);
            if (columnDescriptor.equals(model.getSortColumn())) {
                if (columnDescriptor.isSortAscending()) {
                    headerText.append("&nbsp;&#x2191;");
                } else {
                    headerText.append("&nbsp;&#x2193;");
                }
            } else {
                headerText.append("&nbsp;&nbsp;&nbsp;");
            }
            setHTML(0, colIndex, headerText.toString());
            getColumnFormatter().setWidth(colIndex, columnDescriptor.getWidth());
            getCellFormatter().setWordWrap(0, colIndex, false);
            ++colIndex;
        }

        if (isUseColumnSelector()) {
            setWidget(0, colIndex, createHeaderColumnSelector());
            getColumnFormatter().setWidth(colIndex, COLUMNS_SELECTOR_COLUMN_SIZE);
            getCellFormatter().setStyleName(0, colIndex, BASE_NAME + StyleSuffix.ColumnSelector);
            getCellFormatter().setVerticalAlignment(0, colIndex, HasVerticalAlignment.ALIGN_MIDDLE);
            getCellFormatter().setHorizontalAlignment(0, colIndex, HasHorizontalAlignment.ALIGN_CENTER);
        }

        Element rowElement = getRowFormatter().getElement(0);
        UIObject.setStyleName(rowElement, BASE_NAME + StyleSuffix.Header);
    }

    private void renderBody() {
        clearTableData();

        List<DataItem<E>> data = model.getData();
        List<ColumnDescriptor<E>> columnDescriptors = model.getColumnDescriptors();

        int rowIndex = 1;
        for (DataItem<E> dataItem : data) {
            int colIndex = 0;
            if (isCheckboxColumnShown()) {
                SelectionCheckBox selectionCheckBox = new SelectionCheckBox(rowIndex, dataItem.isChecked());
                selectionCheckBoxes.add(selectionCheckBox);

                selectionCheckBox.setWidth(CHECK_MARK_COLUMN_SIZE);
                setWidget(rowIndex, 0, selectionCheckBox);
                getCellFormatter().setAlignment(rowIndex, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
                ++colIndex;
            }

            for (ColumnDescriptor<E> columnDescriptor : columnDescriptors) {
                HTML contentHtml;
                Object value = dataItem.getCellValue(columnDescriptor);
                if (value == null || value.equals("")) {
                    contentHtml = new HTML("&nbsp;");
                } else {
                    contentHtml = new HTML(value.toString());
                    if (!columnDescriptor.isWordWrap()) {
                        contentHtml.getElement().getStyle().setProperty("overflow", "hidden");
                        contentHtml.getElement().getStyle().setMarginRight(5, Unit.PX);
                    }
                }
                setWidget(rowIndex, colIndex, contentHtml);
                getCellFormatter().setWordWrap(rowIndex, colIndex, columnDescriptor.isWordWrap());
                ++colIndex;
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

            ++rowIndex;
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

    public E getSelectedItem() {
        int selectedRow = getSelectedRow();
        if (selectedRow >= 0 && selectedRow < getDataTableModel().getData().size()) {
            return getDataTableModel().getData().get(selectedRow).getEntity();
        }
        return null;
    }

    public List<E> getCheckedItems() {
        ArrayList<E> checked = new ArrayList<E>();

        for (DataItem<E> dataItem : model.getData()) {
            if (dataItem.isChecked()) {
                checked.add(dataItem.getEntity());
            }
        }

        return checked;
    }

    public void addSortChangeHandler(SortChangeHandler<E> handler) {
        if (sortChangeHandlers == null) {
            sortChangeHandlers = new ArrayList<SortChangeHandler<E>>(1);
        }

        sortChangeHandlers.add(handler);
    }

    protected void processHeaderClick(int column) {
        if (isColumnClickSorting()) {
            ColumnDescriptor<E> columnDescriptor = model.getColumnDescriptors().get(column);
            if (columnDescriptor.equals(model.getSortColumn())) {
                columnDescriptor.setSortAscending(!columnDescriptor.isSortAscending());
            } else {
                model.setSortColumn(columnDescriptor);
            }

            renderHeader();

            // notify listeners:
            if (sortChangeHandlers != null) {
                for (SortChangeHandler<E> handler : sortChangeHandlers) {
                    handler.onChange(columnDescriptor);
                }
            }
        }
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

    public boolean isColumnClickSorting() {
        return columnClickSorting;
    }

    public void setColumnClickSorting(boolean columnClickSorting) {
        this.columnClickSorting = columnClickSorting;
    }

    public boolean isCheckboxColumnShown() {
        return checkboxColumnShown;
    }

    public void setCheckboxColumnShown(boolean checkboxColumnShown) {
        this.checkboxColumnShown = checkboxColumnShown;
    }

    public boolean isUseColumnSelector() {
        return (availableColumns != null);
    }

    public void setUseColumnSelector(List<ColumnDescriptor<E>> availableColumns) {
        this.availableColumns = availableColumns;
    }

    public boolean isAutoColumnsWidth() {
        return autoColumnsWidth;
    }

    public void setAutoColumnsWidth(boolean autoColumnsWidth) {
        if (this.autoColumnsWidth = autoColumnsWidth) {
            DOM.setStyleAttribute(getElement(), "tableLayout", "auto");
        } else {
            DOM.setStyleAttribute(getElement(), "tableLayout", "fixed");
        }
    }

    public boolean hasDetailsNavigation() {
        return hasDetailsNavigation;
    }

    public void setHasDetailsNavigation(boolean hasDetailsNavigation) {
        this.hasDetailsNavigation = hasDetailsNavigation;
    }

    private Widget createHeaderColumnSelector() {
        assert (availableColumns != null);

        final Anchor selector = new Anchor("...");
        selector.addClickHandler(new ClickHandler() {

            private final List<CheckBox> columnChecksList = new ArrayList<CheckBox>();

            private final PopupPanel pp = new PopupPanel(true);
            {
                pp.setStyleName(BASE_NAME + StyleSuffix.ColumnMenu);
            }

            @Override
            public void onClick(ClickEvent event) {
                pp.setWidget(createColumnsSelectors());
                pp.setPopupPositionAndShow(new PositionCallback() {
                    @Override
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        pp.setPopupPosition(selector.getAbsoluteLeft() - offsetWidth, selector.getAbsoluteTop());
                    }
                });
                pp.addCloseHandler(new CloseHandler<PopupPanel>() {
                    @Override
                    public void onClose(CloseEvent<PopupPanel> event) {
                        ArrayList<ColumnDescriptor<E>> selectedColumns = new ArrayList<ColumnDescriptor<E>>();
                        for (int i = 0; i < columnChecksList.size(); ++i) {
                            if (columnChecksList.get(i).getValue()) {
                                selectedColumns.add(availableColumns.get(i));
                            }
                        }
                        getDataTableModel().setColumnDescriptors(selectedColumns);
                        renderTable();
                    }
                });
                pp.show();
            }

            private Widget createColumnsSelectors() {
                FlowPanel panel = new FlowPanel();

                for (ColumnDescriptor<E> column : availableColumns) {
                    CheckBox columnCheck = new CheckBox(column.getColumnTitle());
                    columnCheck.setValue(isSameColumn(column.getColumnName()));
                    columnChecksList.add(columnCheck);
                    panel.add(columnCheck);
                    panel.add(new HTML());
                }

                return panel;
            }

            private boolean isSameColumn(String columnName) {
                List<ColumnDescriptor<E>> currentColumns = model.getColumnDescriptors();
                for (ColumnDescriptor<E> column : currentColumns) {
                    if (columnName.contentEquals(column.getColumnName())) {
                        return true;
                    }
                }
                return false;
            }
        });

        return selector;
    }

    // Check box selection class: 
    public void addCheckSelectionHandler(CheckSelectionHandler handler) {
        if (checkSelectionHandlers == null) {
            checkSelectionHandlers = new ArrayList<CheckSelectionHandler>(1);
        }
        checkSelectionHandlers.add(handler);
    }

    // Check box column item class: 
    class SelectionCheckBox extends CheckBox {

        public SelectionCheckBox(final int rowIndex, boolean checked) {
            setValue(checked);

            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (rowIndex == HEADER_RAW_INDEX) {
                        for (DataItem<E> dataItem : model.getData()) {
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

                    // notify listeners:
                    if (checkSelectionHandlers != null) {
                        boolean isAnyChecked = model.isAnyChecked();
                        for (CheckSelectionHandler handler : checkSelectionHandlers) {
                            handler.onCheck(isAnyChecked);
                        }
                    }
                }
            });
        }
    }
}

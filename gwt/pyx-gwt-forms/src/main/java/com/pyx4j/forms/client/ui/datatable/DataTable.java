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
package com.pyx4j.forms.client.ui.datatable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public class DataTable<E extends IEntity> extends FlexTable implements DataTableModelListener {

    private static final Logger log = LoggerFactory.getLogger(DataTable.class);

    private static final int HEADER_RAW_INDEX = 0;

    private static final String CHECK_MARK_COLUMN_SIZE = "1px";

    private static final String COLUMNS_SELECTOR_COLUMN_SIZE = "1px";

    private DataTableModel<E> model;

    private int selectedRow = -1;

    private List<Integer> selectedRows;

    private boolean hasDetailsNavigation = false;

    private boolean hasColumnClickSorting = false;

    private boolean autoColumnsWidth = false;

    private boolean hasCheckboxColumn = false;

    private boolean markSelectedRow = true;

    private boolean columnSelectorVisible = true;

    private final List<SelectionCheckBox> selectionCheckBoxes = new ArrayList<SelectionCheckBox>();

    private SelectionCheckBox selectionCheckBoxAll;

    private List<ItemSelectionHandler> itemSelectionHandlers;

    private List<CheckSelectionHandler> checkSelectionHandlers;

    private List<SortChangeHandler<E>> sortChangeHandlers;

    private List<ColumnSelectionHandler> columnSelectionHandlers;

    public DataTable() {
        setStyleName(DefaultDataTableTheme.StyleName.DataTable.name());
        DOM.setStyleAttribute(getElement(), "tableLayout", "fixed");

        this.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Cell cell = getCellForEvent(event);
                if (cell == null) {
                    return; // do not process empty clicks!...
                } else if (cell.getRowIndex() == 0) {
                    if (cell.getCellIndex() >= (hasCheckboxColumn() ? 1 : 0) && cell.getCellIndex() < getCellCount(0) - (isColumnSelectorVisible() ? 1 : 0)) {
                        processHeaderClick(hasCheckboxColumn() ? cell.getCellIndex() - 1 : cell.getCellIndex()); // actual table column index - without the first check one!...
                    }
                } else if (cell.getCellIndex() >= (hasCheckboxColumn() ? 1 : 0)) {
                    setSelectedRow(cell.getRowIndex() - 1); // actual table row index - without the header!...
                }
            }
        });
    }

    public DataTable(DataTableModel<E> model) {
        this();
        setDataTableModel(model);
    }

// Data manipulation:

    public void setDataTableModel(DataTableModel<E> model) {
        if (this.model != null) {
            this.model.removeDataTableModelListener(this);
        }
        this.model = model;
        model.addDataTableModelListener(this);
        if (model.getColumnDescriptors() != null) {
            renderTable();
        }
    }

    public DataTableModel<E> getDataTableModel() {
        return model;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public List<Integer> getSelectedRows() {
        return selectedRows;
    }

    public E getSelectedItem() {
        int selectedRow = getSelectedRow();
        if (selectedRow >= 0 && selectedRow < model.getData().size()) {
            return model.getData().get(selectedRow).getEntity();
        }
        return null;
    }

    public List<E> getSelectedItems() {
        List<E> selected = new ArrayList<E>();

        for (int selectedRow : selectedRows) {
            if (selectedRow >= 0 && selectedRow < model.getData().size()) {
                selected.add(model.getData().get(selectedRow).getEntity());
            }
        }

        return selected;
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

    public void releaseCheckedItems() {
        for (SelectionCheckBox selectionCheckBox : selectionCheckBoxes) {
            selectionCheckBox.setValue(false, true);
        }
    }

// Events connection:

    public void addItemSelectionHandler(ItemSelectionHandler handler) {
        if (itemSelectionHandlers == null) {
            itemSelectionHandlers = new ArrayList<ItemSelectionHandler>(2);
        }
        itemSelectionHandlers.add(handler);
    }

    public void remItemSelectionHandler(ItemSelectionHandler handler) {
        if (itemSelectionHandlers != null) {
            itemSelectionHandlers.remove(handler);
        }
    }

    public void addCheckSelectionHandler(CheckSelectionHandler handler) {
        if (checkSelectionHandlers == null) {
            checkSelectionHandlers = new ArrayList<CheckSelectionHandler>(2);
        }
        checkSelectionHandlers.add(handler);
    }

    public void remCheckSelectionHandler(CheckSelectionHandler handler) {
        if (checkSelectionHandlers != null) {
            checkSelectionHandlers.remove(handler);
        }
    }

    public void addSortChangeHandler(SortChangeHandler<E> handler) {
        if (sortChangeHandlers == null) {
            sortChangeHandlers = new ArrayList<SortChangeHandler<E>>(2);
        }

        sortChangeHandlers.add(handler);
    }

    public void remSortChangeHandler(SortChangeHandler<E> handler) {
        if (sortChangeHandlers != null) {
            sortChangeHandlers.remove(handler);
        }
    }

    public void addColumnSelectionChangeHandler(ColumnSelectionHandler handler) {
        if (columnSelectionHandlers == null) {
            columnSelectionHandlers = new ArrayList<ColumnSelectionHandler>(2);
        }
        columnSelectionHandlers.add(handler);
    }

    public void remColumnSelectionChangeHandler(ColumnSelectionHandler handler) {
        if (columnSelectionHandlers != null) {
            columnSelectionHandlers.remove(handler);
        }
    }

// UI & behaviour setup:

    public boolean isMarkSelectedRow() {
        return markSelectedRow;
    }

    public void setMarkSelectedRow(boolean markSelectedRow) {
        this.markSelectedRow = markSelectedRow;
    }

    public boolean isMultiSelect() {
        return (selectedRows != null);
    }

    public void setMultiSelect(boolean isMultiSelect) {
        if (isMultiSelect) {
            if (!isMultiSelect()) {
                selectedRows = new ArrayList<Integer>(model.getPageSize());
            }
        } else {
            for (int i : selectedRows) {
                markRow(i, false);
            }
            selectedRows = null;
        }
    }

    public void releaseSelection() {
        if (isMultiSelect()) {
            for (int i : selectedRows) {
                markRow(i, false);
            }
            selectedRows.clear();
        } else {
            markRow(getSelectedRow(), false);
        }
        selectedRow = -1;
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

    public boolean hasColumnClickSorting() {
        return hasColumnClickSorting;
    }

    public void setHasColumnClickSorting(boolean hasColumnClickSorting) {
        this.hasColumnClickSorting = hasColumnClickSorting;
    }

    public boolean hasCheckboxColumn() {
        return hasCheckboxColumn;
    }

    public void setHasCheckboxColumn(boolean hasCheckboxColumn) {
        this.hasCheckboxColumn = hasCheckboxColumn;
    }

    public boolean isColumnSelectorVisible() {
        return columnSelectorVisible;
    }

    public void setColumnSelectorVisible(boolean columnSelectorVisible) {
        this.columnSelectorVisible = columnSelectorVisible;
    }

    public boolean hasDetailsNavigation() {
        return hasDetailsNavigation;
    }

    public void setHasDetailsNavigation(boolean hasDetailsNavigation) {
        this.hasDetailsNavigation = hasDetailsNavigation;
    }

//
// Internals:
//
    public void renderTable() {

        assert model.getColumnDescriptors() != null : "getColumnDescriptors() shouldn't be null";

        removeAllRows();

        if (!BrowserType.isIE()) {
            for (int i = 0; i < 30; i++) {
                getColumnFormatter().setWidth(i, "0");
            }
        } else {
            for (int i = 0; i < 30; i++) {
                DOM.setStyleAttribute(getColumnFormatter().getElement(i), "width", "0px");
            }

        }

        renderHeader();
        renderBody();
    }

    public void clearTable() {
        selectedRow = -1;
        if (selectedRows != null) {
            selectedRows.clear();
        }
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

    private void renderHeader() {
        if (getRowCount() > 0) {
            removeCells(0, 0, getCellCount(0));
        }

        int colIndex = 0;
        if (hasCheckboxColumn()) {
            selectionCheckBoxAll = new SelectionCheckBox(HEADER_RAW_INDEX, model.isAllChecked());
            setWidget(0, 0, selectionCheckBoxAll);
            if (!BrowserType.isIE()) {
                getColumnFormatter().setWidth(colIndex, CHECK_MARK_COLUMN_SIZE);
            } else {
                DOM.setStyleAttribute(getColumnFormatter().getElement(colIndex), "width", CHECK_MARK_COLUMN_SIZE);
            }
            getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
            ++colIndex;
        }

        for (ColumnDescriptor columnDescriptor : model.getColumnDescriptorsVisible()) {
            String columnTitle = columnDescriptor.getColumnTitle();
            StringBuffer headerText = new StringBuffer("&nbsp;");
            headerText.append(columnTitle);
            if (columnDescriptor.equals(model.getSortColumn())) {
                if (model.isSortAscending()) {
                    headerText.append("&nbsp;&#x2191;");
                } else {
                    headerText.append("&nbsp;&#x2193;");
                }
            } else {
                headerText.append("&nbsp;&nbsp;&nbsp;");
            }
            setHTML(0, colIndex, headerText.toString());

            if (!BrowserType.isIE()) {
                getColumnFormatter().setWidth(colIndex, columnDescriptor.getWidth());
            } else {
                DOM.setStyleAttribute(getColumnFormatter().getElement(colIndex), "width", columnDescriptor.getWidth());
            }

            getCellFormatter().setWordWrap(0, colIndex, false);
            ++colIndex;
        }

        if (isColumnSelectorVisible()) {
            setWidget(0, colIndex, createHeaderColumnSelector());
            if (!BrowserType.isIE()) {
                getColumnFormatter().setWidth(colIndex, COLUMNS_SELECTOR_COLUMN_SIZE);
            } else {
                DOM.setStyleAttribute(getColumnFormatter().getElement(colIndex), "width", COLUMNS_SELECTOR_COLUMN_SIZE);
            }

            getCellFormatter().setStyleName(0, colIndex, DefaultDataTableTheme.StyleName.DataTableColumnSelector.name());
            getCellFormatter().setVerticalAlignment(0, colIndex, HasVerticalAlignment.ALIGN_MIDDLE);
            getCellFormatter().setHorizontalAlignment(0, colIndex, HasHorizontalAlignment.ALIGN_CENTER);

        }

        if (getRowCount() > 0) {
            Element rowElement = getRowFormatter().getElement(0);
            UIObject.setStyleName(rowElement, DefaultDataTableTheme.StyleName.DataTableHeader.name());
        }
    }

    private void renderBody() {
        clearTable();
        log.trace("dataTable {} render start {}", GWTJava5Helper.getSimpleName(model.getEntityClass()), model.getData().size());

        Scheduler.get().scheduleIncremental(new RepeatingCommand() {

            final long start = System.currentTimeMillis();

            final Iterator<DataItem<E>> dataIterator = model.getData().iterator();

            final List<ColumnDescriptor> visibleColumnDescriptors = model.getColumnDescriptorsVisible();

            int rowIndex = 1;

            @Override
            public boolean execute() {
                if (!dataIterator.hasNext()) {
                    log.trace("dataTable {} render ends {} in {} msec", GWTJava5Helper.getSimpleName(model.getEntityClass()), rowIndex - 1,
                            TimeUtils.since(start));
                    return false;
                }

                DataItem<E> dataItem = dataIterator.next();

                int colIndex = 0;
                if (hasCheckboxColumn()) {
                    SelectionCheckBox selectionCheckBox = new SelectionCheckBox(rowIndex, dataItem.isChecked());
                    selectionCheckBoxes.add(selectionCheckBox);

                    selectionCheckBox.setWidth(CHECK_MARK_COLUMN_SIZE);

                    setWidget(rowIndex, 0, selectionCheckBox);
                    getCellFormatter().setAlignment(rowIndex, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
                    ++colIndex;
                }

                for (ColumnDescriptor columnDescriptor : visibleColumnDescriptors) {
                    Widget contentHtml;
                    Object value = dataItem.getCellValue(columnDescriptor);
                    if (value instanceof Widget) {
                        contentHtml = (Widget) value;
                    } else if (value == null || value.equals("")) {
                        contentHtml = new HTML("&nbsp;");
                    } else {
                        contentHtml = new HTML(SafeHtmlUtils.fromString(value.toString()));
                        if (!columnDescriptor.isWordWrap()) {
                            contentHtml.getElement().getStyle().setProperty("overflow", "hidden");
                            contentHtml.getElement().getStyle().setMarginRight(5, Unit.PX);
                        }
                    }
                    setWidget(rowIndex, colIndex, contentHtml);
                    getCellFormatter().setWordWrap(rowIndex, colIndex, columnDescriptor.isWordWrap());
                    ++colIndex;
                }

                if (rowIndex < getRowCount()) {
                    Element rowElement = getRowFormatter().getElement(rowIndex);
                    UIObject.setStyleName(rowElement, DefaultDataTableTheme.StyleName.DataTableRow.name());
                    if (rowIndex % 2 == 0) {
                        UIObject.setStyleName(rowElement,
                                DefaultDataTableTheme.StyleName.DataTableRow.name() + "-" + DefaultDataTableTheme.StyleDependent.even.name(), true);
                    } else {
                        UIObject.setStyleName(rowElement,
                                DefaultDataTableTheme.StyleName.DataTableRow.name() + "-" + DefaultDataTableTheme.StyleDependent.odd.name(), true);
                    }
                    if (!hasDetailsNavigation()) {
                        UIObject.setStyleName(rowElement, DefaultDataTableTheme.StyleName.DataTableRow.name() + "-"
                                + DefaultDataTableTheme.StyleDependent.nodetails.name(), true);
                    }

                    ++rowIndex;
                }
                return true;
            }

        });

        //TODO implement
        //this.ensureDebugId(model.getDebugId());
    }

    protected void setSelectedRow(int selectedRow) {

        if (isMultiSelect()) {
            if (selectedRows.contains(new Integer(selectedRow))) {
                selectedRows.remove(new Integer(selectedRow));
                markRow(selectedRow, false);
            } else {
                selectedRows.add(selectedRow);
                markRow(selectedRow, true);
            }
        } else {
            markRow(getSelectedRow(), false);
            this.selectedRow = selectedRow;
            markRow(getSelectedRow(), true);
        }

        // notify listeners:
        if (itemSelectionHandlers != null) {
            for (ItemSelectionHandler handler : itemSelectionHandlers) {
                handler.onSelect(getSelectedRow());
            }
        }
    }

    protected void markRow(int row, boolean selected) {
        if (isMarkSelectedRow() && row >= 0) {
            Element previous = getRowFormatter().getElement(row + 1); // raw table row index - including the header!...
            UIObject.setStyleName(previous, DefaultDataTableTheme.StyleName.DataTableRow.name() + "-" + DefaultDataTableTheme.StyleDependent.selected.name(),
                    selected);
        }
    }

    protected void processHeaderClick(int column) {

        if (hasColumnClickSorting()) {
            ColumnDescriptor columnDescriptor = model.getVisibleColumnDescriptor(column);
            if (columnDescriptor.isSortable()) {
                if (columnDescriptor.equals(model.getSortColumn())) {
                    model.setSortAscending(!model.isSortAscending());
                } else {
                    model.setSortColumn(columnDescriptor);
                }

                // notify listeners:
                if (sortChangeHandlers != null) {
                    for (SortChangeHandler<E> handler : sortChangeHandlers) {
                        handler.onChange(columnDescriptor);
                    }
                }
            }
        }
    }

    private Widget createHeaderColumnSelector() {

        final Label selector = new Label("...");
        selector.setWidth("100%");

        selector.setTitle("Select Columns");
        selector.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ColumnSelectorDialog().show();
            }
        });

        return selector;
    }

    private class ColumnSelectorDialog extends OkCancelDialog {

        private final List<CheckBox> columnChecksList = new ArrayList<CheckBox>();

        public ColumnSelectorDialog() {
            super("Select Columns");

            FlowPanel panel = new FlowPanel();
            for (ColumnDescriptor column : model.getColumnDescriptors()) {
                if (!column.isSearchableOnly()) {
                    CheckBox columnCheck = new CheckBox(column.getColumnTitle());
                    columnCheck.setValue(column.isVisible());
                    columnChecksList.add(columnCheck);
                    panel.add(columnCheck);
                    panel.add(new HTML());
                }
            }

            ScrollPanel scroll = new ScrollPanel(panel);
            scroll.getElement().getStyle().setProperty("minWidth", "20em");
            scroll.getElement().getStyle().setProperty("maxHeight", "20em");
            scroll.setStyleName(DefaultDataTableTheme.StyleName.DataTableColumnMenu.name());

            setBody(scroll.asWidget());
        }

        @Override
        public boolean onClickOk() {
            boolean hasChanged = false;
            int checksListIdx = 0;
            for (ColumnDescriptor column : model.getColumnDescriptors()) {
                if (!column.isSearchableOnly()) {
                    boolean requestedVisible = columnChecksList.get(checksListIdx).getValue();
                    if (column.isVisible() != requestedVisible) {
                        column.setVisible(requestedVisible);
                        hasChanged = true;
                    }
                    checksListIdx++;
                }
            }

            if (hasChanged) {
                renderTable();
                if (columnSelectionHandlers != null) {
                    for (ColumnSelectionHandler handler : columnSelectionHandlers) {
                        handler.onColumSelectionChanged();
                    }
                }
            }

            return true;
        }
    }

    // Check box column item class:
    protected class SelectionCheckBox extends CheckBox {

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

    // Events:
    public interface ItemSelectionHandler {
        void onSelect(int selectedRow);
    }

    public interface CheckSelectionHandler {
        void onCheck(boolean isAnyChecked);
    }

    public interface SortChangeHandler<E> {
        void onChange(ColumnDescriptor column);
    }

    public interface ColumnSelectionHandler {
        void onColumSelectionChanged();
    }
}

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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.datatable;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.gwt.commons.BrowserType;

public class FlexTablePane<E extends IEntity> implements ITablePane<E> {

    private static final Logger log = LoggerFactory.getLogger(FlexTablePane.class);

    private static final int HEADER_RAW_INDEX = 0;

    private static final int CHECK_MARK_COLUMN_SIZE = 1;

    private static final int COLUMNS_SELECTOR_COLUMN_SIZE = 1;

    private final FlexTable flexTable;

    private long renderingId;

    private final DataTable<E> dataTable;

    private ItemZoomInCommand<E> itemZoomInCommand;

    private final List<SelectionCheckBox> selectionCheckBoxes = new ArrayList<SelectionCheckBox>();

    private SelectionCheckBox selectionCheckBoxAll;

    public FlexTablePane(final DataTable<E> dataTable) {
        this.dataTable = dataTable;
        flexTable = new FlexTable();

        flexTable.setStyleName(DataTableTheme.StyleName.DataTable.name());
        flexTable.getElement().getStyle().setTableLayout(TableLayout.AUTO);

        flexTable.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Cell cell = flexTable.getCellForEvent(event);

                if (cell == null) {
                    return; // do not process empty clicks!...
                } else if (cell.getRowIndex() == 0) {
                    if (cell.getCellIndex() >= (dataTable.getDataTableModel().isMultipleSelection() ? 1 : 0)
                            && cell.getCellIndex() < flexTable.getCellCount(0) - (dataTable.isColumnSelectorVisible() ? 1 : 0)) {
                        processHeaderClick(dataTable.isMultipleSelection() ? cell.getCellIndex() - 1 : cell.getCellIndex()); // actual table column index - without the first check one!...
                    }
                } else if (cell.getCellIndex() >= (dataTable.isMultipleSelection() ? 1 : 0)) {
                    if (itemZoomInCommand != null) {
                        itemZoomInCommand.execute(dataTable.getDataTableModel().getData().get(cell.getRowIndex() - 1).getEntity());
                    } else {
                        dataTable.getDataTableModel().setRowSelected(!dataTable.getDataTableModel().isRowSelected(cell.getRowIndex() - 1),
                                cell.getRowIndex() - 1);
                    }
                }
            }
        });
    }

    @Override
    public Widget asWidget() {
        return flexTable;
    }

    private void processHeaderClick(int column) {
        if (dataTable.hasColumnClickSorting()) {
            ColumnDescriptor columnDescriptor = dataTable.getDataTableModel().getVisibleColumnDescriptor(column);
            DataTableModel<E> model = dataTable.getDataTableModel();
            if (columnDescriptor.isSortable()) {
                if (columnDescriptor.equals(model.getSortColumn())) {
                    model.setSortAscending(!model.isSortAscending());
                } else {
                    model.setSortColumn(columnDescriptor);
                }
                dataTable.onSortColumnChanged();
            }
        }
    }

    @Override
    public void setItemZoomInCommand(ItemZoomInCommand<E> itemZoomInCommand) {
        this.itemZoomInCommand = itemZoomInCommand;
    }

    @Override
    public boolean isItemZoomInAvailable() {
        return itemZoomInCommand != null;
    }

    @Override
    public void renderTable() {

        if (dataTable.getDataTableModel().getColumnDescriptors() == null) {
            return;
        }

        flexTable.removeAllRows();

        if (BrowserType.isIE()) {
            for (int i = 0; i < 30; i++) {
                flexTable.getColumnFormatter().getElement(i).getStyle().setWidth(0, Unit.PX);
            }
        } else {
            String width = BrowserType.isMsEdge() ? "1px" : "0px";
            for (int i = 0; i < 30; i++) {
                flexTable.getColumnFormatter().setWidth(i, width);
            }
        }

        renderHeader();
        renderBody();

    }

    public void clearTable() {
        dataTable.getDataTableModel().clearSelection();
        selectionCheckBoxes.clear();
        for (int row = flexTable.getRowCount() - 1; row > 0; row--) {
            flexTable.removeRow(row);
        }
    }

    private void renderHeader() {
        if (flexTable.getRowCount() > 0) {
            flexTable.removeCells(0, 0, flexTable.getCellCount(0));
        }

        int colIndex = 0;
        if (dataTable.isMultipleSelection()) {
            selectionCheckBoxAll = new SelectionCheckBox(HEADER_RAW_INDEX, dataTable.getDataTableModel().isAllRowsSelected());
            flexTable.setWidget(0, 0, selectionCheckBoxAll);
            if (!BrowserType.isIE()) {
                flexTable.getColumnFormatter().setWidth(colIndex, CHECK_MARK_COLUMN_SIZE + "px");
            } else {
                flexTable.getColumnFormatter().getElement(colIndex).getStyle().setWidth(CHECK_MARK_COLUMN_SIZE, Unit.PX);
            }
            flexTable.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
            ++colIndex;
        }

        for (ColumnDescriptor columnDescriptor : dataTable.getDataTableModel().getColumnDescriptorsVisible()) {
            String columnTitle = columnDescriptor.getColumnTitle();
            StringBuffer headerText = new StringBuffer("&nbsp;");
            headerText.append(columnTitle);
            if (columnDescriptor.equals(dataTable.getDataTableModel().getSortColumn())) {
                if (dataTable.getDataTableModel().isSortAscending()) {
                    headerText.append("&nbsp;&#x2191;");
                } else {
                    headerText.append("&nbsp;&#x2193;");
                }
            } else {
                headerText.append("&nbsp;&nbsp;&nbsp;");
            }
            flexTable.setHTML(0, colIndex, headerText.toString());

            String width = columnDescriptor.getWidth();
            if (width != null) {
                if (!BrowserType.isIE()) {
                    flexTable.getColumnFormatter().setWidth(colIndex, columnDescriptor.getWidth());
                } else {
                    flexTable.getColumnFormatter().getElement(colIndex).getStyle().setProperty("width", columnDescriptor.getWidth());
                }
            }

            flexTable.getCellFormatter().setWordWrap(0, colIndex, false);
            ++colIndex;
        }

        if (dataTable.isColumnSelectorVisible()) {
            flexTable.setWidget(0, colIndex, createHeaderColumnSelector());
            if (!BrowserType.isIE()) {
                flexTable.getColumnFormatter().setWidth(colIndex, COLUMNS_SELECTOR_COLUMN_SIZE + "px");
            } else {
                flexTable.getColumnFormatter().getElement(colIndex).getStyle().setWidth(COLUMNS_SELECTOR_COLUMN_SIZE, Unit.PX);
            }

            flexTable.getCellFormatter().setStyleName(0, colIndex, DataTableTheme.StyleName.DataTableColumnSelector.name());
            flexTable.getCellFormatter().setVerticalAlignment(0, colIndex, HasVerticalAlignment.ALIGN_MIDDLE);
            flexTable.getCellFormatter().setHorizontalAlignment(0, colIndex, HasHorizontalAlignment.ALIGN_CENTER);

        }

        if (flexTable.getRowCount() > 0) {
            Element rowElement = flexTable.getRowFormatter().getElement(0);
            rowElement.addClassName(DataTableTheme.StyleName.DataTableHeader.name());
        }
    }

    private void renderBody() {
        renderingId++;
        clearTable();

        final DataTableModel<E> model = dataTable.getDataTableModel();

        Scheduler.get().scheduleIncremental(new RepeatingCommand() {

            final long initiationRenderingId = renderingId;

            final Iterator<DataItem<E>> dataIterator = model.getData().iterator();

            final List<ColumnDescriptor> visibleColumnDescriptors = model.getColumnDescriptorsVisible();

            int rowIndex = 1;

            @Override
            public boolean execute() {
                // Prevent deferred execution of stale model.
                // Happens on slow computers.
                // Error example: IndexOutOfBoundsException: Row index: 2, Row size: 2
                if (initiationRenderingId != renderingId) {
                    return false;
                }

                if (!dataIterator.hasNext()) {
                    updateSelectionHighlights();
                    return false;
                }

                DataItem<E> dataItem;
                try {
                    dataItem = dataIterator.next();
                } catch (ConcurrentModificationException e) {
                    // this may happen in hosted mode when data changed.
                    log.debug("Data Changed, discard render");
                    return false;
                }

                int colIndex = 0;
                if (dataTable.getDataTableModel().isMultipleSelection()) {
                    SelectionCheckBox selectionCheckBox = new SelectionCheckBox(rowIndex, model.isRowSelected(dataItem));
                    selectionCheckBoxes.add(selectionCheckBox);

                    selectionCheckBox.setWidth(CHECK_MARK_COLUMN_SIZE + "px");

                    flexTable.setWidget(rowIndex, 0, selectionCheckBox);
                    flexTable.getCellFormatter().setAlignment(rowIndex, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
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
                    flexTable.setWidget(rowIndex, colIndex, contentHtml);
                    flexTable.getCellFormatter().setWordWrap(rowIndex, colIndex, columnDescriptor.isWordWrap());
                    ++colIndex;
                }

                if (rowIndex < flexTable.getRowCount()) {
                    Element rowElement = flexTable.getRowFormatter().getElement(rowIndex);
                    rowElement.addClassName(DataTableTheme.StyleName.DataTableRow.name());
                    if (rowIndex % 2 == 0) {
                        rowElement.addClassName(DataTableTheme.StyleName.DataTableRow.name() + "-" + DataTableTheme.StyleDependent.even.name());
                    } else {
                        rowElement.addClassName(DataTableTheme.StyleName.DataTableRow.name() + "-" + DataTableTheme.StyleDependent.odd.name());
                    }
                    if (!isItemZoomInAvailable()) {
                        rowElement.addClassName(DataTableTheme.StyleName.DataTableRow.name() + "-" + DataTableTheme.StyleDependent.nodetails.name());
                    }

                    ++rowIndex;
                }
                return true;
            }

        });

        //TODO implement
        //this.ensureDebugId(model.getDebugId());
    }

    @Override
    public void updateSelectionHighlights() {
        for (int i = 0; i < dataTable.getDataTableModel().getData().size(); i++) {
            highlightRow(i, dataTable.getDataTableModel().getSelectedRows().contains(dataTable.getDataTableModel().getData().get(i)));
        }

        if (dataTable.getDataTableModel().isMultipleSelection() && selectionCheckBoxAll != null) {
            selectionCheckBoxAll.setValue(dataTable.getDataTableModel().isAllRowsSelected());
        }
    }

    private void highlightRow(int row, boolean selected) {
        if (row >= 0) {
            Element previous = flexTable.getRowFormatter().getElement(row + 1); // raw table row index - including the header!...
            String className = DataTableTheme.StyleName.DataTableRow.name() + "-" + DataTableTheme.StyleDependent.selected.name();

            if (dataTable.getDataTableModel().isMultipleSelection()) {
                selectionCheckBoxes.get(row).setValue(selected);
            }

            if (selected) {
                previous.addClassName(className);
            } else {
                previous.removeClassName(className);
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
                dataTable.showColumnSelectorDialog();
            }
        });

        return selector;
    }

    public void releaseCheckedItems() {
        for (SelectionCheckBox selectionCheckBox : selectionCheckBoxes) {
            selectionCheckBox.setValue(false, true);
        }
    }

    // Check box column item class:
    protected class SelectionCheckBox extends CheckBox {

        public SelectionCheckBox(final int rowIndex, boolean checked) {
            setValue(checked);

            addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (rowIndex == HEADER_RAW_INDEX) {
                        dataTable.getDataTableModel().setAllRowsSelected(getValue());
                    } else {

                        dataTable.getDataTableModel().setRowSelected(getValue(), rowIndex - 1);
                        selectionCheckBoxAll.setValue(dataTable.getDataTableModel().isAllRowsSelected());
                    }
                }
            });

        }
    }

}

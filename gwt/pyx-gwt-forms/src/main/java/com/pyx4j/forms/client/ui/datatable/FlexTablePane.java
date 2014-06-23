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
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public class FlexTablePane<E extends IEntity> implements ITablePane {

    private static final Logger log = LoggerFactory.getLogger(FlexTablePane.class);

    private static final int HEADER_RAW_INDEX = 0;

    private static final int CHECK_MARK_COLUMN_SIZE = 1;

    private static final int COLUMNS_SELECTOR_COLUMN_SIZE = 1;

    private final FlexTable flexTable;

    private final DataTable<E> dataTable;

    private ItemZoomInCommand<E> itemZoomInCommand;

    private final List<SelectionCheckBox> selectionCheckBoxes = new ArrayList<SelectionCheckBox>();

    private SelectionCheckBox selectionCheckBoxAll;

    private int selectedRow = -1;

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
                    if (cell.getCellIndex() >= (dataTable.isMultipleSelection() ? 1 : 0)
                            && cell.getCellIndex() < flexTable.getCellCount(0) - (dataTable.isColumnSelectorVisible() ? 1 : 0)) {
                        processHeaderClick(dataTable.isMultipleSelection() ? cell.getCellIndex() - 1 : cell.getCellIndex()); // actual table column index - without the first check one!...
                    }
                } else if (cell.getCellIndex() >= (dataTable.isMultipleSelection() ? 1 : 0)) {
                    if (itemZoomInCommand != null) {
                        itemZoomInCommand.execute(dataTable.getDataTableModel().getData().get(cell.getRowIndex() - 1).getEntity());
                    } else {
                        setSelectedRow(cell.getRowIndex() - 1); // actual table row index - without the header!...
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
            dataTable.selectSortColumn(columnDescriptor);
        }
    }

    public void setItemZoomInCommand(ItemZoomInCommand<E> itemZoomInCommand) {
        this.itemZoomInCommand = itemZoomInCommand;
    }

    public boolean isItemZoomInAvailable() {
        return itemZoomInCommand != null;
    }

    public void renderTable() {

        assert dataTable.getDataTableModel().getColumnDescriptors() != null : "getColumnDescriptors() shouldn't be null";

        flexTable.removeAllRows();

        if (!BrowserType.isIE()) {
            for (int i = 0; i < 30; i++) {
                flexTable.getColumnFormatter().setWidth(i, "0");
            }
        } else {
            for (int i = 0; i < 30; i++) {
                flexTable.getColumnFormatter().getElement(i).getStyle().setWidth(0, Unit.PX);
            }

        }

        renderHeader();
        renderBody();
    }

    public void clearTable() {
        selectedRow = -1;
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
            selectionCheckBoxAll = new SelectionCheckBox(HEADER_RAW_INDEX, dataTable.getDataTableModel().isAllChecked());
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
        clearTable();

        final DataTableModel<E> model = dataTable.getDataTableModel();

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

                DataItem<E> dataItem;
                try {
                    dataItem = dataIterator.next();
                } catch (ConcurrentModificationException e) {
                    // this may happen in hosted mode when data changed.
                    log.debug("Data Changed, discard render");
                    return false;
                }

                int colIndex = 0;
                if (dataTable.isMultipleSelection()) {
                    SelectionCheckBox selectionCheckBox = new SelectionCheckBox(rowIndex, dataItem.isChecked());
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

    protected void markRow(int row, boolean selected) {
        if (dataTable.isMarkSelectedRow() && row >= 0) {
            Element previous = flexTable.getRowFormatter().getElement(row + 1); // raw table row index - including the header!...
            String className = DataTableTheme.StyleName.DataTableRow.name() + "-" + DataTableTheme.StyleDependent.selected.name();
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
                new ColumnSelectorDialog().show();
            }
        });

        return selector;
    }

    private class ColumnSelectorDialog extends OkCancelDialog {

        private final List<CheckBox> columnChecksList = new ArrayList<CheckBox>();

        public ColumnSelectorDialog() {
            super("Select Columns");

            setDialogPixelWidth(300);
            FlowPanel panel = new FlowPanel();
            for (ColumnDescriptor column : dataTable.getDataTableModel().getColumnDescriptors()) {
                if (!column.isSearchableOnly()) {
                    CheckBox columnCheck = new CheckBox(column.getColumnTitle());
                    columnCheck.setValue(column.isVisible());
                    columnChecksList.add(columnCheck);
                    panel.add(columnCheck);
                    panel.add(new HTML());
                }
            }

            ScrollPanel scroll = new ScrollPanel(panel);
            scroll.setHeight("200px");
            scroll.setStyleName(DataTableTheme.StyleName.DataTableColumnMenu.name());

            setBody(scroll.asWidget());
        }

        @Override
        public boolean onClickOk() {
            boolean hasChanged = false;
            int checksListIdx = 0;
            for (ColumnDescriptor column : dataTable.getDataTableModel().getColumnDescriptors()) {
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
                dataTable.onColumSelectionChanged();
            }

            return true;
        }
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    protected void setSelectedRow(int selectedRow) {

        markRow(getSelectedRow(), false);
        this.selectedRow = selectedRow;
        markRow(getSelectedRow(), true);

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

            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (rowIndex == HEADER_RAW_INDEX) {
                        for (DataItem<E> dataItem : dataTable.getDataTableModel().getData()) {
                            dataItem.setChecked(getValue());
                            for (SelectionCheckBox selectionCheckBox : selectionCheckBoxes) {
                                selectionCheckBox.setValue(getValue());
                            }
                        }
                    } else {
                        boolean allChecked = true;
                        dataTable.getDataTableModel().setRowChecked(getValue(), rowIndex - 1);
                        for (SelectionCheckBox selectionCheckBox : selectionCheckBoxes) {
                            if (!selectionCheckBox.getValue()) {
                                allChecked = false;
                                break;
                            }
                        }
                        selectionCheckBoxAll.setValue(allChecked);
                    }
                    dataTable.onCheckSelectionChanged();
                }
            });
        }
    }
}

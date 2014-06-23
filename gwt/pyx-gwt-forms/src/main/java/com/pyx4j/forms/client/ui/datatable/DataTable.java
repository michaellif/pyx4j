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
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.IEntity;

public class DataTable<E extends IEntity> implements IsWidget, DataTableModelListener {

    private FlexTablePane<E> tablePanel;

    private DataTableModel<E> model;

    private boolean hasColumnClickSorting = false;

    private boolean multipleSelection = false;

    private boolean markSelectedRow = true;

    private boolean columnSelectorVisible = true;

    private List<ItemSelectionHandler> itemSelectionHandlers;

    private List<CheckSelectionHandler> checkSelectionHandlers;

    private List<SortChangeHandler<E>> sortChangeHandlers;

    private List<ColumnSelectionHandler> columnSelectionHandlers;

    public static interface ItemZoomInCommand<E extends IEntity> {
        void execute(E item);
    }

    public static interface HeaderClickCommand {
        void execute(ColumnDescriptor column);
    }

    public DataTable() {
        tablePanel = new FlexTablePane<E>(this);

    }

    public DataTable(DataTableModel<E> model) {
        this();
        setDataTableModel(model);
    }

    @Override
    public Widget asWidget() {
        return tablePanel.asWidget();
    }

    public void setItemZoomInCommand(ItemZoomInCommand<E> itemZoomInCommand) {
        tablePanel.setItemZoomInCommand(itemZoomInCommand);
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

    public E getSelectedItem() {
        int selectedRow = tablePanel.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < model.getData().size()) {
            return model.getData().get(selectedRow).getEntity();
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

    public boolean hasColumnClickSorting() {
        return hasColumnClickSorting;
    }

    public void setHasColumnClickSorting(boolean hasColumnClickSorting) {
        this.hasColumnClickSorting = hasColumnClickSorting;
    }

    public boolean isMultipleSelection() {
        return multipleSelection;
    }

    public void setMultipleSelection(boolean multipleSelection) {
        this.multipleSelection = multipleSelection;
    }

    public boolean isColumnSelectorVisible() {
        return columnSelectorVisible;
    }

    public void setColumnSelectorVisible(boolean columnSelectorVisible) {
        this.columnSelectorVisible = columnSelectorVisible;
    }

    public boolean isItemZoomInAvailable() {
        return tablePanel.isItemZoomInAvailable();
    }

//
// Internals:
//

    public void renderTable() {
        tablePanel.renderTable();
    }

    @Override
    public void onTableModelChanged(DataTableModelEvent e) {
        if (e.getType().equals(DataTableModelEvent.Type.REBUILD)) {
            renderTable();
        }
    }

    protected void markRow(int row, boolean selected) {
        tablePanel.markRow(row, selected);
    }

    public void updateColumnVizibility(int offsetWidth, boolean reduceColumns) {

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

    public void selectSortColumn(ColumnDescriptor columnDescriptor) {
        if (columnDescriptor.isSortable()) {
            if (columnDescriptor.equals(getDataTableModel().getSortColumn())) {
                getDataTableModel().setSortAscending(!getDataTableModel().isSortAscending());
            } else {
                getDataTableModel().setSortColumn(columnDescriptor);
            }
            // notify listeners:
            if (sortChangeHandlers != null) {
                for (SortChangeHandler<?> handler : sortChangeHandlers) {
                    handler.onChange(columnDescriptor);
                }
            }
        }
    }

    protected void selectRow(int selectedRow) {
        tablePanel.setSelectedRow(selectedRow);
        // notify listeners:
        if (itemSelectionHandlers != null) {
            for (ItemSelectionHandler handler : itemSelectionHandlers) {
                handler.onSelect(selectedRow);
            }
        }
    }

    void onColumSelectionChanged() {
        if (columnSelectionHandlers != null) {
            for (ColumnSelectionHandler handler : columnSelectionHandlers) {
                handler.onColumSelectionChanged();
            }
        }
    }

    void onCheckSelectionChanged() {
        // notify listeners:
        if (checkSelectionHandlers != null) {
            boolean isAnyChecked = model.isAnyChecked();
            for (CheckSelectionHandler handler : checkSelectionHandlers) {
                handler.onCheck(isAnyChecked);
            }
        }
    }
}

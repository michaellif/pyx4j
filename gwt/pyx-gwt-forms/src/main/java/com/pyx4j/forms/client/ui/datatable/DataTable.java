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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.IEntity;

public class DataTable<E extends IEntity> implements IsWidget, DataTableModelListener {

    private FlexTablePane<E> tablePanel;

    private DataTableModel<E> model;

    private boolean hasColumnClickSorting = false;

    private final boolean markSelectedRow = true;

    private boolean columnSelectorVisible = true;

    private List<ItemSelectionHandler> itemSelectionHandlers;

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
            tablePanel.renderTable();
        }
    }

    public DataTableModel<E> getDataTableModel() {
        return model;
    }

    public E getSelectedItem() {
        Set<E> selectedItems = getSelectedItems();
        if (selectedItems.size() == 1) {
            return selectedItems.iterator().next();
        } else {
            return null;
        }
    }

    public Set<E> getSelectedItems() {
        HashSet<E> checked = new HashSet<E>();
        for (DataItem<E> dataItem : model.getSelectedRows()) {
            checked.add(dataItem.getEntity());
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

    public boolean hasColumnClickSorting() {
        return hasColumnClickSorting;
    }

    public void setHasColumnClickSorting(boolean hasColumnClickSorting) {
        this.hasColumnClickSorting = hasColumnClickSorting;
    }

    public boolean isMultipleSelection() {
        return model.isMultipleSelection();
    }

    public void setMultipleSelection(boolean multipleSelection) {
        model.setMultipleSelection(multipleSelection);
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

    @Override
    public void onTableModelChanged(DataTableModelEvent e) {
        if (e.getType().equals(DataTableModelEvent.Type.REBUILD)) {
            tablePanel.renderTable();
        } else if (e.getType().equals(DataTableModelEvent.Type.SELECTION)) {
            tablePanel.markSelected();
            onRowSelectionChanged();
        }

    }

    public void updateColumnVizibility(int offsetWidth, boolean reduceColumns) {

    }

    // Events:
    public interface ItemSelectionHandler {
        void onChange();
    }

    public interface SortChangeHandler<E> {
        void onChange();
    }

    public interface ColumnSelectionHandler {
        void onChange();
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
                    handler.onChange();
                }
            }
        }
    }

    void onColumnSelectionChanged() {
        if (columnSelectionHandlers != null) {
            for (ColumnSelectionHandler handler : columnSelectionHandlers) {
                handler.onChange();
            }
        }
    }

    private void onRowSelectionChanged() {
        // notify listeners:
        if (itemSelectionHandlers != null) {
            for (ItemSelectionHandler handler : itemSelectionHandlers) {
                handler.onChange();
            }
        }

    }

}

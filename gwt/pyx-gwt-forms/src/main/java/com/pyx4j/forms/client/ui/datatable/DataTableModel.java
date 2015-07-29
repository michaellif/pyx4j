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
 */
package com.pyx4j.forms.client.ui.datatable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;

/*
 * RowData defines one row in a Sortable Table
 */
public class DataTableModel<E extends IEntity> {

    public static final int PAGE_SIZE = 20;

    private DataTable<E> dataTable;

    /** List of listeners */
    private final ArrayList<DataTableModelListener> listenerList = new ArrayList<DataTableModelListener>();

    private final ArrayList<E> data = new ArrayList<E>();

    private final Collection<E> selected = new HashSet<E>();

    private boolean multipleSelection = false;

    private ColumnDescriptor sortColumn;

    private boolean sortAscending = true;

    private ColumnDescriptor secondarySortColumn;

    private boolean secondarySortAscending = true;

    private int pageNumber = 0;

    private int pageSize = PAGE_SIZE;

    private boolean hasMoreData;

    private int totalRows;

    public DataTableModel() {
    }

    public void setDataTable(DataTable<E> dataTable) {
        this.dataTable = dataTable;
    }

    public void close() {
        listenerList.clear();
        data.clear();
    }

    /**
     * Adds a listener to the list that's notified each time a change to the data model
     * occurs.
     *
     * @param l
     *            the DataTableModelListener
     */
    public void addDataTableModelListener(DataTableModelListener l) {
        listenerList.add(l);
    }

    /**
     * Removes a listener from the list that's notified each time a change to the data
     * model occurs.
     *
     * @param l
     *            the TableModelListener
     */
    public void removeDataTableModelListener(DataTableModelListener l) {
        listenerList.remove(l);
    }

    public List<DataTableModelListener> getDataTableModelListeners() {
        return listenerList;
    }

    protected void fireTableChanged(DataTableModelEvent e) {
        for (DataTableModelListener listener : listenerList) {
            listener.onDataTableModelChanged(e);
        }
    }

    public ColumnDescriptor getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(ColumnDescriptor sortColumn) {
        this.sortColumn = sortColumn;
    }

    public boolean isSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(boolean ascending) {
        this.sortAscending = ascending;
    }

    public ColumnDescriptor getSecondarySortColumn() {
        return secondarySortColumn;
    }

    public void setSecondarySortColumn(ColumnDescriptor sortColumn) {
        this.secondarySortColumn = sortColumn;
    }

    public boolean isSecondarySortAscending() {
        return secondarySortAscending;
    }

    public void setSecondarySortAscending(boolean ascending) {
        this.secondarySortAscending = ascending;
    }

    public List<Sort> getSortCriteria() {
        List<Sort> sorting = new ArrayList<Sort>(2);
        ColumnDescriptor primarySortColumn = getSortColumn();
        if (primarySortColumn != null) {
            sorting.add(new Sort(primarySortColumn.getColumnPath(), !isSortAscending()));
        }
        ColumnDescriptor secondarySortColumn = getSecondarySortColumn();
        if (secondarySortColumn != null) {
            sorting.add(new Sort(secondarySortColumn.getColumnPath(), !isSecondarySortAscending()));
        }
        return sorting;
    }

    public void setSortCriteria(List<Sort> sorts) {
        setSortColumn(null);
        setSecondarySortColumn(null);

        if (sorts != null) {
            if (sorts.size() > 0) {
                Sort sort = sorts.get(0);
                ColumnDescriptor column = dataTable.getColumnDescriptor(sort.getPropertyPath());
                setSortColumn(column);
                setSortAscending(!sort.isDescending());
            }
            if (sorts.size() > 1) {
                Sort sort = sorts.get(1);
                ColumnDescriptor column = dataTable.getColumnDescriptor(sort.getPropertyPath());
                setSecondarySortColumn(column);
                setSecondarySortAscending(!sort.isDescending());
            }
        }
    }

    public ArrayList<E> getData() {
        return data;
    }

    public int indexOf(E item) {
        return data.indexOf(item);
    }

    public void populateData(List<E> dataItems, int pageNumber, boolean hasMoreData, int totalRows) {
        data.clear();
        if (dataItems != null) {
            for (E dataItem : dataItems) {
                data.add(dataItem);
            }
        }
        this.pageNumber = pageNumber;
        this.hasMoreData = hasMoreData;
        this.totalRows = totalRows;
        fireTableChanged(new DataTableModelEvent());
    }

    public void clearData() {
        this.data.clear();
        this.pageNumber = 0;
        this.hasMoreData = false;
        fireTableChanged(new DataTableModelEvent());
    }

    public int getRowCount() {
        return data.size();
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize != 0) {
            this.pageNumber = (this.pageNumber * this.pageSize) / pageSize;
            this.pageSize = pageSize;
        }
    }

    public boolean hasMoreData() {
        return hasMoreData;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void clearSelection() {
        selected.clear();
    }

    public void setRowSelected(boolean checked, int rowIndex) {
        if (!checked) {
            selected.remove(data.get(rowIndex));
        } else {
            if (!multipleSelection) {
                selected.clear();
            }
            selected.add(data.get(rowIndex));
        }
        fireTableChanged(new DataTableModelEvent(DataTableModelEvent.Type.SELECTION));
    }

    public void selectRow(boolean checked, int rowIndex) {
        if (!checked) {
            selected.remove(data.get(rowIndex));
        } else {
            if (!multipleSelection) {
                selected.clear();
            }
            selected.add(data.get(rowIndex));
        }
    }

    public void setAllRowsSelected(boolean checked) {
        selected.clear();
        if (checked) {
            selected.addAll(data);
        }
        fireTableChanged(new DataTableModelEvent(DataTableModelEvent.Type.SELECTION));
    }

    public Collection<E> getSelectedRows() {
        return selected;
    }

    public boolean isRowSelected(E dataItem) {
        return selected.contains(dataItem);
    }

    public boolean isRowSelected(int rowIndex) {
        return isRowSelected(data.get(rowIndex));
    }

    public boolean isAllRowsSelected() {
        for (E dataItem : data) {
            if (!selected.contains(dataItem)) {
                return false;
            }
        }
        return data.size() > 0 ? true : false;
    }

    public boolean isAnyRowSelected() {
        for (E dataItem : data) {
            if (selected.contains(dataItem)) {
                return true;
            }
        }
        return false;
    }

    public boolean isMultipleSelection() {
        return multipleSelection;
    }

    public void setMultipleSelection(boolean multipleSelection) {
        if (multipleSelection != this.multipleSelection) {
            this.multipleSelection = multipleSelection;
            selected.clear();
            fireTableChanged(new DataTableModelEvent(DataTableModelEvent.Type.SELECTION));
        }
    }
}

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

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

/*
 * RowData defines one row in a Sortable Table
 */
public class DataTableModel<E extends IEntity> {

    public static final int PAGE_SIZE = 20;

    private final Class<E> clazz;

    /** List of listeners */
    private final ArrayList<DataTableModelListener> listenerList = new ArrayList<DataTableModelListener>();

    private final ArrayList<DataItem<E>> data = new ArrayList<DataItem<E>>();

    private List<ColumnDescriptor> columnDescriptors;

    private ColumnDescriptor sortColumn;

    private boolean sortAscending = true;

    private ColumnDescriptor secondarySortColumn;

    private boolean secondarySortAscending = true;

    private int pageNumber = 0;

    private int pageSize = PAGE_SIZE;

    private boolean hasMoreData;

    private int totalRows;

    public DataTableModel(Class<E> clazz) {
        this.clazz = clazz;
    }

    public void close() {
        listenerList.clear();
        data.clear();
    }

    public Class<E> getEntityClass() {
        return clazz;
    }

    public List<ColumnDescriptor> getColumnDescriptors() {
        return columnDescriptors;
    }

    public List<ColumnDescriptor> getColumnDescriptorsVisible() {
        List<ColumnDescriptor> descriptors = new ArrayList<ColumnDescriptor>();
        for (ColumnDescriptor columnDescriptor : getColumnDescriptors()) {
            if (columnDescriptor.isVisible()) {
                descriptors.add(columnDescriptor);
                continue;
            }
        }
        return descriptors;
    }

    public void setColumnDescriptors(List<ColumnDescriptor> columnDescriptors) {
        this.columnDescriptors = columnDescriptors;

    }

    public ColumnDescriptor getColumnDescriptor(int index) {
        return columnDescriptors.get(index);
    }

    public ColumnDescriptor getVisibleColumnDescriptor(int index) {
        int i = -1;
        for (ColumnDescriptor descriptor : columnDescriptors) {
            if (descriptor.isVisible()) {
                i++;
                if (index == i) {
                    return descriptor;
                }
            }
        }
        return null;
    }

    public ColumnDescriptor getColumnDescriptor(String columnName) {
        for (ColumnDescriptor descriptor : columnDescriptors) {
            if (descriptor.getColumnName().equals(columnName)) {
                return descriptor;
            }
        }
        return null;
    }

    public String getColumnName(int index) {
        return getColumnDescriptor(index).getColumnName();
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
        for (Iterator<DataTableModelListener> iter = listenerList.iterator(); iter.hasNext();) {
            DataTableModelListener listener = iter.next();
            listener.onTableModelChanged(e);
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
            sorting.add(new Sort(primarySortColumn.getColumnName(), !isSortAscending()));
        }
        ColumnDescriptor secondarySortColumn = getSecondarySortColumn();
        if (secondarySortColumn != null) {
            sorting.add(new Sort(secondarySortColumn.getColumnName(), !isSecondarySortAscending()));
        }
        return sorting;
    }

    public ArrayList<DataItem<E>> getData() {
        return data;
    }

    public void populateData(List<DataItem<E>> dataItems, int pageNumber, boolean hasMoreData, int totalRows) {
        data.clear();
        if (dataItems != null) {
            for (DataItem<E> dataItem : dataItems) {
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

    public void setRowChecked(boolean checked, int rowIndex) {
        data.get(rowIndex).setChecked(checked);
    }

    public boolean isAllChecked() {
        for (DataItem<E> dataItem : data) {
            if (!dataItem.isChecked()) {
                return false;
            }
        }
        return data.size() > 0 ? true : false;
    }

    public boolean isAnyChecked() {
        for (DataItem<E> dataItem : data) {
            if (dataItem.isChecked()) {
                return true;
            }
        }
        return false;
    }
}

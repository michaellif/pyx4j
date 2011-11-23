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
import java.util.Iterator;
import java.util.List;

import com.pyx4j.entity.shared.IEntity;

/*
 * RowData defines one row in a Sortable Table
 */
public class DataTableModel<E extends IEntity> {

    public static final int PAGE_SIZE = 20;

    /** List of listeners */
    private final ArrayList<DataTableModelListener> listenerList = new ArrayList<DataTableModelListener>();

    private final ArrayList<DataItem<E>> data = new ArrayList<DataItem<E>>();

    private List<ColumnDescriptor<E>> columnDescriptors;

    private List<ColumnDescriptor<E>> visibleColumnDescriptors;

    private ColumnDescriptor<E> sortColumn;

    private ColumnDescriptor<E> secondarySortColumn;

    private int pageNumber = 0;

    private int pageSize = PAGE_SIZE;

    private boolean hasMoreData;

    private int totalRows;

    public DataTableModel(Class<E> clazz) {
    }

    public void close() {
        listenerList.clear();
        data.clear();
    }

    public List<ColumnDescriptor<E>> getColumnDescriptors() {
        return columnDescriptors;
    }

    public void setColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors) {
        this.columnDescriptors = columnDescriptors;

        visibleColumnDescriptors = new ArrayList<ColumnDescriptor<E>>();
        for (ColumnDescriptor<E> columnDescriptor : columnDescriptors) {
            if (columnDescriptor.isVisible()) {
                visibleColumnDescriptors.add(columnDescriptor);
            }
        }
    }

    public ColumnDescriptor<E> getColumnDescriptor(int index) {
        return columnDescriptors.get(index);
    }

    public ColumnDescriptor<E> getVisibleColumnDescriptor(int index) {
        return visibleColumnDescriptors.get(index);
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

    public ColumnDescriptor<E> getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(ColumnDescriptor<E> sortColumn) {
        this.sortColumn = sortColumn;
    }

    public ColumnDescriptor<E> getSecondarySortColumn() {
        return secondarySortColumn;
    }

    public void setSecondarySortColumn(ColumnDescriptor<E> sortColumn) {
        this.secondarySortColumn = sortColumn;
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

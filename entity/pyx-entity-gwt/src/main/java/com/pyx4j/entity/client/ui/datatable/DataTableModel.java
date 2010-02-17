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
import java.util.Vector;

import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;

/*
 * RowData defines one row in a Sortable Table
 */
public class DataTableModel<E extends IEntity<IEntity<?>>> implements TableListener {

    public static final int PAGE_SIZE = 10;

    /** List of listeners */
    private final ArrayList<DataTableModelListener> listenerList = new ArrayList<DataTableModelListener>();

    private final ArrayList<DataItem> data = new ArrayList<DataItem>();

    private ColumnDescriptor<E> sortColumn;

    private ColumnDescriptor<E> secondarySortColumn;

    private int pageNumber = 1;

    private int pageSize = PAGE_SIZE;

    private int totalRows;

    private EntityMeta entityMeta;

    public DataTableModel() {
    }

    public DataTableModel(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
    }

    public void close() {
        listenerList.clear();
        data.clear();
    }

    public String getDebugId() {
        return this.factory.getEntityName() + " List";
    }

    public List<ColumnDescriptor<E>> getColumnDescriptors() {
        return factory.getColumnDescriptors();
    }

    public List<ReportColumnDescriptor<? super E>> getReportColumnDescriptors() {
        List<ReportColumnDescriptor<? super E>> r = new Vector<ReportColumnDescriptor<? super E>>();
        for (ColumnDescriptor<E> descriptor : factory.getColumnDescriptors()) {
            if (descriptor instanceof ColumnDescriptorReportAdapter) {
                r.add(((ColumnDescriptorReportAdapter<E>) descriptor).getReportColumnDescriptor());
            } else if (descriptor instanceof ReportColumnDescriptor) {
                r.add((ReportColumnDescriptor<E>) descriptor);
            }
        }
        return r;
    }

    public ColumnDescriptor<E> getColumnDescriptor(String columnName) {
        for (ColumnDescriptor<E> descriptor : factory.getColumnDescriptors()) {
            if (descriptor.getColumnName().equals(columnName)) {
                return descriptor;
            }
        }
        return null;
    }

    public ColumnDescriptor<E> getColumnDescriptor(int index) {
        return getColumnDescriptor(getColumnName(index));
    }

    public List<String> getColumnNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (ColumnDescriptor<E> descriptor : factory.getColumnDescriptors()) {
            names.add(descriptor.getColumnName());
        }
        return names;
    }

    public String getColumnName(int index) {
        return getColumnNames().get(index);
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

    public ArrayList<DataItem> getData() {
        return data;
    }

    public void populateData(List<DataItem> dataItems, int pageNumber, int totalRows) {
        data.clear();
        if (dataItems != null) {
            for (DataItem dataItem : dataItems) {
                data.add(dataItem);
            }
        }
        setPageNumber(pageNumber);
        if (pageSize < 1) {
            pageSize = PAGE_SIZE;
        }
        setTotalRows(totalRows);
        fireTableChanged(new DataTableModelEvent());
    }

    public int getRowCount() {
        return data.size();
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    /*
     * onCellClicked
     * 
     * Implementation of Table Listener Interface, this method decribes what to do when a
     * cell is clicked
     * 
     * @param sender (SourcesTableEvents)
     * 
     * @param rowIndex (int)
     * 
     * @param colIndex (int)
     */
    public void onCellClicked(SourcesTableEvents sender, int row, int col) {
    }

    public void setRowChecked(boolean checked, int rowIndex) {
        data.get(rowIndex).setChecked(checked);
    }

    public boolean isAllChecked() {
        for (DataItem dataItem : data) {
            if (!dataItem.isChecked()) {
                return false;
            }
        }
        return data.size() > 0 ? true : false;
    }

}

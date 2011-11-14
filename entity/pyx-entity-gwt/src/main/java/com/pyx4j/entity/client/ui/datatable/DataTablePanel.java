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
 * Created on Apr 24, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.datatable;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.client.ui.datatable.DataTable.ItemSelectionHandler;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;

public abstract class DataTablePanel<E extends IEntity> extends VerticalPanel {

    private final E entityPrototype;

    protected final DataTableModel<E> dataTableModel;

    private final DataTable<E> dataTable;

    private final DataTableActionsBar topActionsBar;

    private final DataTableActionsBar bottomActionsBar;

    private final DataTableFilterPanel<E> filterPanel;

    public DataTablePanel(Class<E> clazz) {
        setWidth("100%");
        entityPrototype = EntityFactory.getEntityPrototype(clazz);

        dataTableModel = new DataTableModel<E>(clazz, getColumnDescriptors());
        dataTable = new DataTable<E>(dataTableModel);

        topActionsBar = new DataTableActionsBar();
        add(topActionsBar);

        filterPanel = new DataTableFilterPanel<E>(this);
        //add(filterPanel);

        add(dataTable);

        bottomActionsBar = new DataTableActionsBar();
        add(bottomActionsBar);

        dataTable.setWidth("100%");
        setCellWidth(dataTable, "100%");

        dataTable.addItemSelectionHandler(new ItemSelectionHandler() {
            @Override
            public void onSelect(int selectedRow) {
                DataTablePanel.this.onSelect(selectedRow);
            }
        });

        topActionsBar.setDataTableModel(dataTableModel);
        bottomActionsBar.setDataTableModel(dataTableModel);
    }

    protected abstract void onSelect(int selectedRow);

    public void removeUpperActionsBar() {
        remove(topActionsBar);
    }

    public void removeLowerActionsBar() {
        remove(bottomActionsBar);
    }

    public abstract List<ColumnDescriptor<E>> getColumnDescriptors();

    public EntityMeta getEntityMeta() {
        return entityPrototype.getEntityMeta();
    }

    public E proto() {
        return entityPrototype;
    }

    public void setPrevActionHandler(ClickHandler prevActionHandler) {
        topActionsBar.setPrevActionHandler(prevActionHandler);
        bottomActionsBar.setPrevActionHandler(prevActionHandler);
    }

    public void setNextActionHandler(ClickHandler nextActionHandler) {
        topActionsBar.setNextActionHandler(nextActionHandler);
        bottomActionsBar.setNextActionHandler(nextActionHandler);
    }

    public void setPageSizeOptions(List<Integer> pageSizeOptions) {
        topActionsBar.setPageSizeOptions(pageSizeOptions);
    }

    public void setPageSizeActionHandler(ClickHandler clickHandler) {
        topActionsBar.setPageSizeActionHandler(clickHandler);
    }

    public Anchor insertUpperActionItem(String name, IDebugId debugId, ClickHandler handler) {
        return topActionsBar.insertActionItem(name, debugId, handler);
    }

    public Anchor insertLowerActionItem(String name, IDebugId debugId, ClickHandler handler) {
        return bottomActionsBar.insertActionItem(name, debugId, handler);
    }

    public DataTable<E> getDataTable() {
        return dataTable;
    }

    public DataTableModel<E> getDataTableModel() {
        return dataTableModel;
    }

    public void populateData(List<E> entityes, int pageNumber, boolean hasMoreData, int totalRows) {
        List<DataItem<E>> dataItems = new ArrayList<DataItem<E>>();
        for (E entity : entityes) {
            dataItems.add(new DataItem<E>(entity));
        }
        dataTableModel.populateData(dataItems, pageNumber, hasMoreData, totalRows);
    }

    public void clearData() {
        if (dataTableModel != null) {
            dataTableModel.clearData();
        }
    }

    public int getPageSize() {
        if (dataTableModel != null) {
            return dataTableModel.getPageSize();
        } else {
            throw new RuntimeException("dataTableModel is not set");
        }
    }

    public void setPageSize(int pageSize) {
        if (dataTableModel != null) {
            dataTableModel.setPageSize(pageSize);
        } else {
            throw new RuntimeException("dataTableModel is not set");
        }
    }

    public String toStringForPrint() {
        return dataTable.toString();
    }

    public int getPageNumber() {
        if (dataTableModel != null) {
            return dataTableModel.getPageNumber();
        } else {
            throw new RuntimeException("dataTableModel is not set");
        }
    }
}
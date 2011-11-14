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

public abstract class DataTablePanel<E extends IEntity> extends VerticalPanel {

    protected final DataTableModel<E> dataTableModel;

    private final E entityPrototype;

    private final DataTable<E> dataTable;

    private final DataTableActionsBar upperActionsBar;

    private final DataTableActionsBar lowerActionsBar;

    public DataTablePanel(Class<E> clazz) {
        setWidth("100%");

        entityPrototype = EntityFactory.getEntityPrototype(clazz);

        upperActionsBar = new DataTableActionsBar();
        add(upperActionsBar);

        dataTableModel = new DataTableModel<E>(entityPrototype.getEntityMeta(), getColumnDescriptors());
        dataTable = new DataTable<E>(dataTableModel);
        add(dataTable);

        lowerActionsBar = new DataTableActionsBar();
        add(lowerActionsBar);

        dataTable.setWidth("100%");
        setCellWidth(dataTable, "100%");

        dataTable.addItemSelectionHandler(new ItemSelectionHandler() {
            @Override
            public void onSelect(int selectedRow) {
                DataTablePanel.this.onSelect(selectedRow);
            }
        });

        upperActionsBar.setDataTableModel(dataTableModel);
        lowerActionsBar.setDataTableModel(dataTableModel);
    }

    protected abstract void onSelect(int selectedRow);

    public void removeUpperActionsBar() {
        remove(upperActionsBar);
    }

    public void removeLowerActionsBar() {
        remove(lowerActionsBar);
    }

    public abstract List<ColumnDescriptor<E>> getColumnDescriptors();

    public void setPrevActionHandler(ClickHandler prevActionHandler) {
        upperActionsBar.setPrevActionHandler(prevActionHandler);
        lowerActionsBar.setPrevActionHandler(prevActionHandler);
    }

    public void setNextActionHandler(ClickHandler nextActionHandler) {
        upperActionsBar.setNextActionHandler(nextActionHandler);
        lowerActionsBar.setNextActionHandler(nextActionHandler);
    }

    public void setPageSizeOptions(List<Integer> pageSizeOptions) {
        upperActionsBar.setPageSizeOptions(pageSizeOptions);
    }

    public void setPageSizeActionHandler(ClickHandler clickHandler) {
        upperActionsBar.setPageSizeActionHandler(clickHandler);
    }

    public Anchor insertUpperActionItem(String name, IDebugId debugId, ClickHandler handler) {
        return upperActionsBar.insertActionItem(name, debugId, handler);
    }

    public Anchor insertLowerActionItem(String name, IDebugId debugId, ClickHandler handler) {
        return lowerActionsBar.insertActionItem(name, debugId, handler);
    }

    public DataTable<E> getDataTable() {
        return dataTable;
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

    public E proto() {
        return entityPrototype;
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
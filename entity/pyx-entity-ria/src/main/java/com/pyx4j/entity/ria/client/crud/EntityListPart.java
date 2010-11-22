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
 * @version $Id: EntityListPanel.java 6955 2010-09-08 19:41:44Z michaellif $
 */
package com.pyx4j.entity.ria.client.crud;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.DataItem;
import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.entity.client.ui.datatable.DataTableModel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

public abstract class EntityListPart<E extends IEntity> extends SimplePanel {

    private final DataTableModel<E> dataTableModel;

    private final E metaEntity;

    private final DataTable<E> dataTable;

    public EntityListPart(Class<E> clazz) {
        setWidth("100%");

        metaEntity = EntityFactory.create(clazz);
        dataTable = new DataTable<E>(false);
        dataTable.setWidth("100%");

        setWidget(dataTable);

        dataTableModel = new DataTableModel<E>(metaEntity.getEntityMeta(), getColumnDescriptors());

        dataTable.setDataTableModel(dataTableModel);
        dataTable.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
            }
        });

    }

    public abstract List<ColumnDescriptor<E>> getColumnDescriptors();

    public DataTable<E> getDataTable() {
        return dataTable;
    }

    public void populateData(List<E> entityes, int pageNumber, boolean hasMoreData) {
        List<DataItem<E>> dataItems = new ArrayList<DataItem<E>>();
        for (E entity : entityes) {
            dataItems.add(new DataItem<E>(entity));
        }
        dataTableModel.populateData(dataItems, pageNumber, hasMoreData);
    }

    public void clearData() {
        if (dataTableModel != null) {
            dataTableModel.clearData();
        }
    }

    public E getMetaEntity() {
        return metaEntity;
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
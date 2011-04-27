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
package com.pyx4j.essentials.client.crud;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.DataItem;
import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.entity.client.ui.datatable.DataTableActionsBar;
import com.pyx4j.entity.client.ui.datatable.DataTableModel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AbstractSiteDispatcher;
import com.pyx4j.site.client.NavigationUri;
import com.pyx4j.site.shared.meta.NavigNode;
import com.pyx4j.site.shared.meta.NavigUtils;

public abstract class EntityListPanel<E extends IEntity> extends VerticalPanel {

    protected final DataTableModel<E> dataTableModel;

    private final E entityPrototype;

    private Class<? extends NavigNode> editorPage;

    private final DataTable<E> dataTable;

    private final DataTableActionsBar upperActionsBar;

    private final DataTableActionsBar lowerActionsBar;

    public EntityListPanel(Class<E> clazz) {
        setWidth("100%");

        entityPrototype = EntityFactory.create(clazz);

        add(upperActionsBar = new DataTableActionsBar());
        add(dataTable = new DataTable<E>(false));
        add(lowerActionsBar = new DataTableActionsBar());

        dataTable.setWidth("100%");
        setCellWidth(dataTable, "100%");

        dataTableModel = new DataTableModel<E>(entityPrototype.getEntityMeta(), getColumnDescriptors());

        dataTable.setDataTableModel(dataTableModel);
        dataTable.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (editorPage == null) {
                    return;
                }
                Cell cell = dataTable.getCellForEvent(event);
                if (cell != null && cell.getRowIndex() > 0) {
                    E entity = dataTableModel.getData().get(cell.getRowIndex() - 1).getEntity();
                    AbstractSiteDispatcher.show(new NavigationUri(editorPage, NavigUtils.ENTITY_ID, entity.getPrimaryKey().toString()));
                }
            }
        });

        upperActionsBar.setDataTableModel(dataTableModel);
        lowerActionsBar.setDataTableModel(dataTableModel);
    }

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

    public Anchor insertUpperActionItem(String name, IDebugId debugId, ClickHandler handler) {
        return upperActionsBar.insertActionItem(name, debugId, handler);
    }

    public Anchor insertLowerActionItem(String name, IDebugId debugId, ClickHandler handler) {
        return lowerActionsBar.insertActionItem(name, debugId, handler);
    }

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

    public void setEditorPageType(Class<? extends NavigNode> editorPage) {
        this.editorPage = editorPage;
        //TODO change Cursor style to arrow
        dataTable.setHasDetailsNavigation(this.editorPage != null);
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
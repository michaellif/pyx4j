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
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.essentials.client.crud;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.entity.client.ui.crud.IEntitySearchResultsPanel;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.shared.meta.NavigNode;

public abstract class EntitySearchResultsPanel<E extends IEntity> extends HorizontalPanel implements IEntitySearchResultsPanel<E> {

    private final EntityListPanel<E> listPanel;

    public EntityListWidget<E> listWidget;

    public EntitySearchResultsPanel(Class<E> clazz) {
        super();
        setWidth("100%");
        ClickHandler prevHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                listWidget.view(listPanel.getDataTable().getDataTableModel().getPageNumber() - 1, true);
            }

        };
        ClickHandler nextHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                listWidget.view(listPanel.getDataTable().getDataTableModel().getPageNumber() + 1, true);
            }

        };

        this.listPanel = new EntityListPanel<E>(clazz, prevHandler, nextHandler);
        listPanel.setColumnDescriptors(getColumnDescriptors());
        add(listPanel);

    }

    protected E getMetaEntity() {
        return listPanel.getMetaEntity();
    }

    public void populateData(List<E> entities, int pageNumber, boolean hasMoreData) {
        listPanel.populateData(entities, pageNumber, hasMoreData);
    }

    public void clearData() {
        listPanel.clearData();
    }

    public void setEditorPageType(Class<? extends NavigNode> editorPage) {
        listPanel.setEditorPageType(editorPage);
    }

    public EntityListPanel<E> getListPanel() {
        return listPanel;
    }

    public abstract List<ColumnDescriptor<E>> getColumnDescriptors();

    public int getPageSize() {
        return listPanel.getPageSize();
    }

    public int getPageNumber() {
        return listPanel.getPageNumber();
    }

    public void setListWidget(EntityListWidget<E> listWidget) {
        this.listWidget = listWidget;
    }

}

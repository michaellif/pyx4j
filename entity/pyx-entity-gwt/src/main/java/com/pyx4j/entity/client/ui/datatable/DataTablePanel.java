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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterItem;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterPanel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.images.WidgetsImages;

public class DataTablePanel<E extends IEntity> extends VerticalPanel {

    private static final I18n i18n = I18n.get(DataTableFilterItem.class);

    private final E entityPrototype;

    private final DataTable<E> dataTable;

    private final DataTableActionsBar topActionsBar;

    private final DataTableActionsBar bottomActionsBar;

    private final DataTableFilterPanel<E> filterPanel;

    private WidgetsImages images;

    private Button addButton;

    private Button filterButton;

    public DataTablePanel(Class<E> clazz) {
        this(clazz, EntityFolderImages.INSTANCE);
    }

    public DataTablePanel(Class<E> clazz, WidgetsImages images) {
        this.images = images;
        setWidth("100%");
        entityPrototype = EntityFactory.getEntityPrototype(clazz);

        dataTable = new DataTable<E>();

        topActionsBar = new DataTableActionsBar();
        add(topActionsBar);

        filterButton = new Button(i18n.tr("Filter"));

        filterPanel = new DataTableFilterPanel<E>(this);

        bottomActionsBar = new DataTableActionsBar();

        add(filterPanel);
        add(dataTable);
        add(bottomActionsBar);

        dataTable.setWidth("100%");
        setCellWidth(dataTable, "100%");

        topActionsBar.getToolbar().addItem(filterButton);

        filterButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                filterPanel.setFilters(null);
            }
        });

        setDataTableModel(new DataTableModel<E>(clazz));
    }

    protected void setDataTableModel(DataTableModel<E> model) {
        dataTable.setDataTableModel(model);
        topActionsBar.setDataTableModel(model);
        bottomActionsBar.setDataTableModel(model);
    }

    public Button getFilterButton() {
        return filterButton;
    }

    public EntityMeta getEntityMeta() {
        return entityPrototype.getEntityMeta();
    }

    public E proto() {
        return entityPrototype;
    }

    public void setAddActionHandler(ClickHandler addActionHandler) {
        topActionsBar.getToolbar().insertItem(
                addButton = new Button(new Image(EntityFolderImages.INSTANCE.addButton().hover()), i18n.tr("New {0}", entityPrototype.getEntityMeta()
                        .getCaption()), addActionHandler), 0, false);
    }

    public Button getAddButton() {
        return addButton;
    }

    public void setFilterApplyCommand(Command filterActionCommand) {
        filterPanel.setFilterApplyCommand(filterActionCommand);
    }

    public void setFirstActionHandler(Command firstActionCommand) {
        topActionsBar.getPageNavigBar().setFirstActionCommand(firstActionCommand);
        bottomActionsBar.getPageNavigBar().setFirstActionCommand(firstActionCommand);
    }

    public void setPrevActionHandler(Command prevActionCommand) {
        topActionsBar.getPageNavigBar().setPrevActionCommand(prevActionCommand);
        bottomActionsBar.getPageNavigBar().setPrevActionCommand(prevActionCommand);
    }

    public void setNextActionHandler(Command nextActionCommand) {
        topActionsBar.getPageNavigBar().setNextActionCommand(nextActionCommand);
        bottomActionsBar.getPageNavigBar().setNextActionCommand(nextActionCommand);
    }

    public void setLastActionHandler(Command lastActionCommand) {
        topActionsBar.getPageNavigBar().setLastActionCommand(lastActionCommand);
        bottomActionsBar.getPageNavigBar().setLastActionCommand(lastActionCommand);
    }

    public void setPageSizeActionHandler(Command pageSizeActionCommand) {
        topActionsBar.getPageNavigBar().setPageSizeActionCommand(pageSizeActionCommand);
        bottomActionsBar.getPageNavigBar().setPageSizeActionCommand(pageSizeActionCommand);
    }

    public void setPageSizeOptions(List<Integer> pageSizeOptions) {
        topActionsBar.getPageNavigBar().setPageSizeOptions(pageSizeOptions);
        bottomActionsBar.getPageNavigBar().setPageSizeOptions(pageSizeOptions);
    }

    public void addUpperActionItem(Widget widget) {
        topActionsBar.getToolbar().addItem(widget);
    }

    public DataTable<E> getDataTable() {
        return dataTable;
    }

    public DataTableModel<E> getDataTableModel() {
        return dataTable.getDataTableModel();
    }

    public void populateData(List<E> entityes, int pageNumber, boolean hasMoreData, int totalRows) {
        List<DataItem<E>> dataItems = new ArrayList<DataItem<E>>();
        for (E entity : entityes) {
            dataItems.add(new DataItem<E>(entity));
        }
        getDataTableModel().populateData(dataItems, pageNumber, hasMoreData, totalRows);
    }

    public void discard() {
        if (getDataTableModel() != null) {
            getDataTableModel().clearData();
        }
        filterPanel.resetFilters();
    }

    public int getPageSize() {
        if (getDataTableModel() != null) {
            return getDataTableModel().getPageSize();
        } else {
            throw new RuntimeException("dataTableModel is not set");
        }
    }

    public void setPageSize(int pageSize) {
        if (getDataTableModel() != null) {
            getDataTableModel().setPageSize(pageSize);
        } else {
            throw new RuntimeException("dataTableModel is not set");
        }
    }

    public String toStringForPrint() {
        return dataTable.toString();
    }

    public void setColumnDescriptors(List<ColumnDescriptor> columnDescriptors) {
        getDataTableModel().setColumnDescriptors(columnDescriptors);
    }

    public int getPageNumber() {
        if (getDataTableModel() != null) {
            return getDataTableModel().getPageNumber();
        } else {
            throw new RuntimeException("dataTableModel is not set");
        }
    }

    public WidgetsImages getImages() {
        return images;
    }

    public List<DataTableFilterData> getFilters() {
        return filterPanel.getFilters();
    }

    public void setFilters(List<DataTableFilterData> filters) {
        filterPanel.setFilters(filters);
    }

    public void resetFilters() {
        filterPanel.resetFilters();
    }

    public void setFilteringEnabled(boolean enabled) {
        filterButton.setVisible(enabled);
    }
}
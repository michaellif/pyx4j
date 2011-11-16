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

public abstract class DataTablePanel<E extends IEntity> extends VerticalPanel {

    protected static I18n i18n = I18n.get(DataTableFilterItem.class);

    private final E entityPrototype;

    protected final DataTableModel<E> dataTableModel;

    private final DataTable<E> dataTable;

    private final DataTableTopActionsBar topActionsBar;

    private final DataTableBottomActionsBar bottomActionsBar;

    private final DataTableFilterPanel<E> filterPanel;

    private WidgetsImages images;

    private Button filterButton;

    public DataTablePanel(Class<E> clazz) {
        this(clazz, EntityFolderImages.INSTANCE);
    }

    public DataTablePanel(Class<E> clazz, WidgetsImages images) {
        this.images = images;
        setWidth("100%");
        entityPrototype = EntityFactory.getEntityPrototype(clazz);

        dataTableModel = new DataTableModel<E>(clazz, getColumnDescriptors());
        dataTable = new DataTable<E>(dataTableModel);

        topActionsBar = new DataTableTopActionsBar();
        add(topActionsBar);

        filterButton = new Button(i18n.tr("Filter"));

        filterPanel = new DataTableFilterPanel<E>(this);

        bottomActionsBar = new DataTableBottomActionsBar();

        add(filterPanel);
        add(dataTable);
        add(bottomActionsBar);

        dataTable.setWidth("100%");
        setCellWidth(dataTable, "100%");

        topActionsBar.setDataTableModel(dataTableModel);
        topActionsBar.getToolbar().addItem(filterButton);

        filterButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                if (filterButton.isEnabled() && !filterPanel.isVisible()) {
                    filterPanel.setVisible(true);
                }
            }
        });

        bottomActionsBar.setDataTableModel(dataTableModel);

        filterPanel.setVisible(false);
    }

    public Button getFilterButton() {
        return filterButton;
    }

    public void swtVisibleTopActionsBar(boolean visible) {
        topActionsBar.setVisible(visible);
    }

    public void swtVisibleBottomActionsBar(boolean visible) {
        bottomActionsBar.setVisible(visible);
    }

    public abstract List<ColumnDescriptor<E>> getColumnDescriptors();

    public EntityMeta getEntityMeta() {
        return entityPrototype.getEntityMeta();
    }

    public E proto() {
        return entityPrototype;
    }

    public void setAddActionHandler(ClickHandler addActionHandler) {
        topActionsBar.getToolbar().insertSeparator(0);
        topActionsBar.getToolbar().insertItem(
                new Button(new Image(EntityFolderImages.INSTANCE.addHover()), i18n.tr("New") + " " + entityPrototype.getEntityMeta().getCaption(),
                        addActionHandler), 0, false);
    }

    public void setFilterActionHandler(ClickHandler filterActionHandler) {
        filterPanel.setFilterActionHandler(filterActionHandler);
    }

    public void setPrevActionHandler(ClickHandler prevActionHandler) {
        bottomActionsBar.setPrevActionHandler(prevActionHandler);
    }

    public void setNextActionHandler(ClickHandler nextActionHandler) {
        bottomActionsBar.setNextActionHandler(nextActionHandler);
    }

    public void setPageSizeOptions(List<Integer> pageSizeOptions) {
        bottomActionsBar.setPageSizeOptions(pageSizeOptions);
    }

    public void setPageSizeActionHandler(ClickHandler clickHandler) {
        bottomActionsBar.setPageSizeActionHandler(clickHandler);
    }

    public void addUpperActionItem(Widget widget) {
        topActionsBar.getToolbar().addItem(widget);
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

    public WidgetsImages getImages() {
        return images;
    }

    public List<DataTableFilterData> getFilterData() {
        return filterPanel.getFilterData();
    }

    public void setFilterData(List<DataTableFilterData> filterData) {
        filterPanel.setFilterData(filterData);
    }

    public void setFiltersVisible(boolean visible) {
        filterPanel.setVisible(visible);
    }
}
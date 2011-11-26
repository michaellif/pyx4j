/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud.lister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.entity.client.ui.datatable.DataTable.CheckSelectionHandler;
import com.pyx4j.entity.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.entity.client.ui.datatable.DataTablePanel;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;
import com.pyx4j.site.client.ui.crud.misc.IMemento;
import com.pyx4j.site.client.ui.crud.misc.MementoImpl;
import com.pyx4j.site.rpc.CrudAppPlace;

public abstract class ListerBase<E extends IEntity> extends VerticalPanel implements IListerView<E> {

    public static enum MementoKeys {
        page, filterData, sortingData
    };

    private final IMemento memento = new MementoImpl();

// Events:
    public interface ItemSelectionHandler<E> {
        void onSelect(E selectedItem);
    }

    private static final I18n i18n = I18n.get(ListerBase.class);

    protected final DataTablePanel<E> dataTablePanel;

    protected Presenter presenter;

    private List<ItemSelectionHandler<E>> itemSelectionHandlers;

    private Class<? extends CrudAppPlace> itemOpenPlaceClass;

    private boolean openEditor;

    public ListerBase(Class<E> clazz) {
        setStyleName(DefaultSiteCrudPanelsTheme.StyleName.Lister.name());

        dataTablePanel = new DataTablePanel<E>(clazz);

        dataTablePanel.setFilterActionHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getPresenter().populate(0);
            }
        });

        dataTablePanel.setPrevActionHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onPrevPage();
            }
        });
        dataTablePanel.setNextActionHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onNextPage();
            }
        });
        dataTablePanel.getDataTable().setHasCheckboxColumn(true);
        dataTablePanel.getDataTable().addCheckSelectionHandler(new CheckSelectionHandler() {
            @Override
            public void onCheck(boolean isAnyChecked) {
                setActionsActive(isAnyChecked);
            }
        });
        dataTablePanel.getDataTable().setHasColumnClickSorting(true);
        dataTablePanel.getDataTable().addSortChangeHandler(new SortChangeHandler<E>() {
            @Override
            public void onChange(ColumnDescriptor<E> column) {
                getPresenter().populate(getPageNumber());
            }
        });

        showColumnSelector(true);
        dataTablePanel.setPageSize(ApplicationMode.isDevelopment() ? 10 : 30);
        dataTablePanel.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.ListerListPanel.name());
        dataTablePanel.getDataTable().setHasCheckboxColumn(false);
        dataTablePanel.getDataTable().setMarkSelectedRow(false);
        dataTablePanel.getDataTable().setAutoColumnsWidth(true);

        add(dataTablePanel);

    }

    @SuppressWarnings("unchecked")
    public void setColumnDescriptors(ColumnDescriptor<?>... columnDescriptors) {
        dataTablePanel.setColumnDescriptors(Arrays.asList((ColumnDescriptor<E>[]) columnDescriptors));
    }

    public void setColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors) {
        dataTablePanel.setColumnDescriptors(columnDescriptors);
    }

    public E proto() {
        return dataTablePanel.proto();
    }

    public ListerBase(Class<E> clazz, Class<? extends CrudAppPlace> itemOpenPlaceClass, boolean openEditor, boolean allowAddNew) {
        this(clazz);

        this.itemOpenPlaceClass = itemOpenPlaceClass;
        this.openEditor = openEditor;

        if (itemOpenPlaceClass != null) {
            // item selection stuff:
            dataTablePanel.getDataTable().addItemSelectionHandler(new DataTable.ItemSelectionHandler() {
                @Override
                public void onSelect(int selectedRow) {
                    E item = getDataTablePanel().getDataTable().getSelectedItem();
                    if (item != null) {
                        onItemSelect(item);
                    }
                }
            });

            // new item stuff:
            if (allowAddNew) {
                dataTablePanel.setAddActionHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        onItemNew();
                    }
                });
            }

        }
    }

    public void showColumnSelector(boolean show) {
        dataTablePanel.getDataTable().setColumnSelectorVisible(show);
    }

    // selection/checking stuff:

    public boolean isSelectable() {
        return dataTablePanel.getDataTable().isMarkSelectedRow();
    }

    public void setSelectable(boolean isSelectable) {
        dataTablePanel.getDataTable().setMarkSelectedRow(isSelectable);
    }

    public boolean isMultiSelect() {
        return dataTablePanel.getDataTable().isMultiSelect();
    }

    public void setMultiSelect(boolean isMultiSelect) {
        dataTablePanel.getDataTable().setMultiSelect(isMultiSelect);
    }

    public void releaseSelection() {
        dataTablePanel.getDataTable().releaseSelection();
    }

    public E getSelectedItem() {
        return dataTablePanel.getDataTable().getSelectedItem();
    }

    public List<E> getSelectedItems() {
        return dataTablePanel.getDataTable().getSelectedItems();
    }

    public void setSelectedItem(E item) {
        // TODO - implementation here...
    }

    public void addItemSelectionHandler(ItemSelectionHandler<E> handler) {
        if (itemSelectionHandlers == null) {
            itemSelectionHandlers = new ArrayList<ItemSelectionHandler<E>>(2);
        }

        itemSelectionHandlers.add(handler);
        dataTablePanel.getDataTable().addItemSelectionHandler(new DataTable.ItemSelectionHandler() {
            @Override
            public void onSelect(int selectedRow) {
                if (itemSelectionHandlers != null) {
                    for (ItemSelectionHandler<E> handler : itemSelectionHandlers) {
                        handler.onSelect(dataTablePanel.getDataTable().getSelectedItem());
                    }
                }
            }
        });
    }

    public void addActionItem(Widget widget) {
        dataTablePanel.addUpperActionItem(widget);
    }

    public boolean hasCheckboxColumn() {
        return dataTablePanel.getDataTable().hasCheckboxColumn();
    }

    public void setHasCheckboxColumn(boolean hasCheckboxColumn) {
        dataTablePanel.getDataTable().setHasCheckboxColumn(hasCheckboxColumn);
    }

    public List<E> getCheckedItems() {
        return dataTablePanel.getDataTable().getCheckedItems();
    }

    // Memento:
    @Override
    public void storeState(Place place) {
        getMemento().setCurrentPlace(place);
        getMemento().clear();

        getMemento().putInteger(MementoKeys.page.name(), getLister().getPageNumber());
        getMemento().putObject(MementoKeys.filterData.name(), getLister().getFiltering());
        getMemento().putObject(MementoKeys.sortingData.name(), getLister().getSorting());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void restoreState() {
        int pageNumber = 0;
        List<DataTableFilterData> filters = null;
        List<Sort> sorts = null;

        if (getMemento().mayRestore()) {
            pageNumber = getMemento().getInteger(MementoKeys.page.name());
            filters = (List<DataTableFilterData>) getMemento().getObject(MementoKeys.filterData.name());
            sorts = (List<Sort>) getMemento().getObject(MementoKeys.sortingData.name());
        }

        getLister().setFiltering(filters);
        getLister().setSorting(sorts);
        // should be called last:
        getPresenter().populate(pageNumber);
    }

    public void resetState() {

        getLister().setFiltering(null);
        getLister().setSorting(null);

        getLister().getDataTablePanel().getDataTable().clearTable();

        // should be called last:
        getPresenter().populate(0);
    }

    // EntityListPanel access:
    public DataTablePanel<E> getDataTablePanel() {
        return dataTablePanel;
    }

    // Actions:
    /**
     * Override in derived class for your own select item procedure.
     */
    protected void onItemSelect(E item) {
        if (itemOpenPlaceClass != null && openEditor) {
            getPresenter().edit(itemOpenPlaceClass, item.getPrimaryKey());
        } else {
            getPresenter().view(itemOpenPlaceClass, item.getPrimaryKey());
        }
    }

    /**
     * Override in derived class for your own new item creation procedure.
     */
    protected void onItemNew() {
        getPresenter().editNew(itemOpenPlaceClass, null);
    }

// IListerView implementation:

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Presenter getPresenter() {
        return presenter;
    }

    @Override
    public ListerBase<E> getLister() {
        return this;
    }

    @Override
    public int getPageSize() {
        return getDataTablePanel().getPageSize();
    }

    @Override
    public int getPageNumber() {
        return getDataTablePanel().getPageNumber();
    }

    @Override
    public void populate(List<E> entityes, int pageNumber, boolean hasMoreData, int totalRows) {
        setActionsActive(false);
        getDataTablePanel().populateData(entityes, pageNumber, hasMoreData, totalRows);
    }

    @Override
    public void onDeleted(Key itemID, boolean isSuccessful) {
        // TODO Auto-generated method stub
    }

    @Override
    public List<DataTableFilterData> getFiltering() {
        return dataTablePanel.getFilterData();
    }

    @Override
    public void setFiltering(List<DataTableFilterData> filterData) {
        dataTablePanel.setFilterData(filterData);
    }

    @Override
    public List<Sort> getSorting() {
        return getDataTablePanel().getDataTableModel().getSortCriteria();
    }

    @Override
    public void setSorting(List<Sort> sorts) {

        getDataTablePanel().getDataTableModel().setSortColumn(null);
        getDataTablePanel().getDataTableModel().setSecondarySortColumn(null);

        if (sorts != null) {
            boolean primarySet = false;
            for (Sort sort : sorts) {
                for (ColumnDescriptor<E> column : getDataTablePanel().getDataTableModel().getColumnDescriptors()) {
                    if (column.getColumnName().compareTo(sort.getPropertyName()) == 0) {
                        column.setSortAscending(!sort.isDescending());
                        if (!primarySet) {
                            getDataTablePanel().getDataTableModel().setSortColumn(column);
                            primarySet = true;
                        } else {
                            getDataTablePanel().getDataTableModel().setSecondarySortColumn(column);
                        }
                    }
                }
            }
        }
    }

    @Override
    public IMemento getMemento() {
        return memento;
    }

    /**
     * Override in derived class to fill pages with data.
     */
    protected void onPrevPage() {
        getPresenter().populate(getDataTablePanel().getDataTableModel().getPageNumber() - 1);
    }

    protected void onNextPage() {
        getPresenter().populate(getDataTablePanel().getDataTableModel().getPageNumber() + 1);
    }

    private void setActionsActive(boolean active) {
        //TODO
//        for (Widget w : actionsPanel) {
//            if (!w.equals(btnNewItem) && w instanceof FocusWidget) {
//                ((FocusWidget) w).setEnabled(active);
//            }
//        }
    }

}

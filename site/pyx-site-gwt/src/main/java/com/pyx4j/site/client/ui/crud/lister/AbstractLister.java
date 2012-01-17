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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.EntityDataSource;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.entity.client.ui.datatable.DataTable.CheckSelectionHandler;
import com.pyx4j.entity.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.entity.client.ui.datatable.DataTablePanel;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;
import com.pyx4j.site.client.ui.crud.misc.IMemento;
import com.pyx4j.site.client.ui.crud.misc.MementoImpl;

public abstract class AbstractLister<E extends IEntity> extends VerticalPanel {

    public static enum MementoKeys {
        page, filterData, sortingData
    };

    private final DataTablePanel<E> dataTablePanel;

    private EntityDataSource<E> optionsDataSource;

    private List<ItemSelectionHandler<E>> itemSelectionHandlers;

    private Class<E> clazz;

    private final IMemento memento = new MementoImpl();

    public AbstractLister(Class<E> clazz) {
        this.clazz = clazz;
        setStyleName(DefaultSiteCrudPanelsTheme.StyleName.Lister.name());
        dataTablePanel = new DataTablePanel<E>(clazz);

        dataTablePanel.setFilterApplyCommand(new Command() {
            @Override
            public void execute() {
                obtain(0);
            }
        });

        dataTablePanel.setFirstActionHandler(new Command() {
            @Override
            public void execute() {
                obtain(0);
            }
        });
        dataTablePanel.setPrevActionHandler(new Command() {
            @Override
            public void execute() {
                obtain(dataTablePanel.getDataTableModel().getPageNumber() - 1);
            }
        });
        dataTablePanel.setNextActionHandler(new Command() {
            @Override
            public void execute() {
                obtain(dataTablePanel.getDataTableModel().getPageNumber() + 1);
            }
        });
        dataTablePanel.setLastActionHandler(new Command() {
            @Override
            public void execute() {
                obtain(dataTablePanel.getDataTableModel().getTotalRows() / dataTablePanel.getDataTableModel().getPageSize());
            }
        });

        dataTablePanel.setPageSizeActionHandler(new Command() {
            @Override
            public void execute() {
                obtain(0);
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
                obtain(getPageNumber());
            }
        });

        showColumnSelector(true);

        dataTablePanel.setPageSizeOptions(Arrays.asList(new Integer[] { 10, 25, 50 }));

        dataTablePanel.setPageSize(ApplicationMode.isDevelopment() ? 10 : 30);
        dataTablePanel.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.ListerListPanel.name());
        dataTablePanel.getDataTable().setHasCheckboxColumn(false);
        dataTablePanel.getDataTable().setMarkSelectedRow(false);
        dataTablePanel.getDataTable().setAutoColumnsWidth(true);

        add(dataTablePanel);
    }

    public AbstractLister(Class<E> clazz, boolean allowZoomIn, boolean allowAddNew) {
        this(clazz);

        if (allowZoomIn) {
            // item selection stuff:
            getDataTablePanel().getDataTable().addItemSelectionHandler(new DataTable.ItemSelectionHandler() {
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
                getDataTablePanel().setAddActionHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        onItemNew();
                    }
                });
            }
            getDataTablePanel().getDataTable().setHasDetailsNavigation(true);
        } else {
            getDataTablePanel().getDataTable().setHasDetailsNavigation(false);
        }
    }

    public void setOptionsDataSource(EntityDataSource<E> dataSource) {
        this.optionsDataSource = optionsDataSource;
    }

    protected void obtain(final int pageNumber) {
        assert optionsDataSource != null : "dataSource is not installed";

        EntityListCriteria<E> criteria = EntityListCriteria.create(clazz);
        criteria.setPageNumber(pageNumber);
        optionsDataSource.obtain(criteria, new DefaultAsyncCallback<EntitySearchResult<E>>() {
            @Override
            public void onSuccess(EntitySearchResult<E> result) {
                populateData(result.getData(), pageNumber, result.hasMoreData(), result.getTotalRows());
            }
        });
    }

    protected abstract void onItemSelect(E item);

    protected abstract void onItemNew();

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

    public int getPageSize() {
        return dataTablePanel.getPageSize();
    }

    public int getPageNumber() {
        return dataTablePanel.getPageNumber();
    }

    public List<DataTableFilterData> getFilters() {
        return dataTablePanel.getFilters();
    }

    public void setFilters(List<DataTableFilterData> filters) {
        dataTablePanel.setFilters(filters);
    }

    public void resetFilters() {
        dataTablePanel.resetFilters();
    }

    public List<Sort> getSorting() {
        return dataTablePanel.getDataTableModel().getSortCriteria();
    }

    public void setSorting(List<Sort> sorts) {

        dataTablePanel.getDataTableModel().setSortColumn(null);
        dataTablePanel.getDataTableModel().setSecondarySortColumn(null);

        if (sorts != null) {
            boolean primarySet = false;
            for (Sort sort : sorts) {
                for (ColumnDescriptor<E> column : dataTablePanel.getDataTableModel().getColumnDescriptors()) {
                    if (column.getColumnName().compareTo(sort.getPropertyName()) == 0) {
                        dataTablePanel.getDataTableModel().setSortAscending(!sort.isDescending());
                        if (!primarySet) {
                            dataTablePanel.getDataTableModel().setSortColumn(column);
                            primarySet = true;
                        } else {
                            dataTablePanel.getDataTableModel().setSecondarySortColumn(column);
                        }
                    }
                }
            }
        }
    }

    private void setActionsActive(boolean active) {
        //TODO
//        for (Widget w : actionsPanel) {
//            if (!w.equals(btnNewItem) && w instanceof FocusWidget) {
//                ((FocusWidget) w).setEnabled(active);
//            }
//        }
    }

    public void discard() {
        dataTablePanel.discard();
    }

    public IMemento getMemento() {
        return memento;
    }

    public void storeState(Place place) {
        getMemento().setCurrentPlace(place);
        getMemento().clear();

        getMemento().putInteger(MementoKeys.page.name(), getPageNumber());
        getMemento().putObject(MementoKeys.filterData.name(), getFilters());
        getMemento().putObject(MementoKeys.sortingData.name(), getSorting());
    }

    @SuppressWarnings("unchecked")
    public void restoreState() {
        int pageNumber = 0;
        List<DataTableFilterData> filters = null;
        List<Sort> sorts = null;

        if (getMemento().mayRestore()) {
            pageNumber = getMemento().getInteger(MementoKeys.page.name());
            filters = (List<DataTableFilterData>) getMemento().getObject(MementoKeys.filterData.name());
            sorts = (List<Sort>) getMemento().getObject(MementoKeys.sortingData.name());
        }

        if (filters != null && filters.size() > 0) {
            setFilters(filters);
        }
        setSorting(sorts);
        // should be called last:
        obtain(pageNumber);
    }

    public void populateData(List<E> entityes, int pageNumber, boolean hasMoreData, int totalRows) {
        setActionsActive(false);
        dataTablePanel.populateData(entityes, pageNumber, hasMoreData, totalRows);
    }

    public DataTablePanel<E> getDataTablePanel() {
        return dataTablePanel;
    }

}

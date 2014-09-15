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
package com.pyx4j.site.client.backoffice.ui.prime.lister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTable;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.forms.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.criteria.ICriteriaForm;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.backoffice.ui.PaneTheme;
import com.pyx4j.site.client.backoffice.ui.prime.IMemento;
import com.pyx4j.site.client.backoffice.ui.prime.MementoImpl;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractLister.ItemSelectionHandler;

public class EntityDataTablePanel<E extends IEntity> extends ScrollPanel {

    private static final Logger log = LoggerFactory.getLogger(EntityDataTablePanel.class);

    public static enum MementoKeys {
        page, filterData, sortingData
    };

    public static int PAGESIZE_SMALL = 10;

    public static int PAGESIZE_MEDIUM = 25;

    public static int PAGESIZE_LARGE = 50;

    private final DataTablePanel<E> dataTablePanel;

    private ListerDataSource<E> dataSource;

    private List<ItemSelectionHandler<E>> itemSelectionHandlers;

    private final Class<E> clazz;

    private final IMemento memento = new MementoImpl();

    private List<Criterion> externalFilters;

    public EntityDataTablePanel(Class<E> clazz) {
        this(clazz, false, false);
    }

    public EntityDataTablePanel(Class<E> clazz, boolean allowAddNew) {
        this(clazz, allowAddNew, false);
    }

    public EntityDataTablePanel(Class<E> clazz, boolean allowAddNew, boolean allowDelete) {
        this(clazz, null, allowAddNew, allowDelete);
    }

    public EntityDataTablePanel(Class<E> clazz, ICriteriaForm<E> criteriaForm, boolean allowAddNew, boolean allowDelete) {
        this.clazz = clazz;
        setStyleName(PaneTheme.StyleName.Lister.name());
        setSize("100%", "100%");
        dataTablePanel = new DataTablePanel<E>(clazz, criteriaForm);
        dataTablePanel.getElement().getStyle().setPaddingBottom(40, Unit.PX);

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
                obtain((dataTablePanel.getDataTableModel().getTotalRows() - 1) / dataTablePanel.getDataTableModel().getPageSize());
            }
        });

        dataTablePanel.setPageSizeActionHandler(new Command() {
            @Override
            public void execute() {
                obtain(0);
            }
        });

        dataTablePanel.getDataTable().setHasColumnClickSorting(true);
        dataTablePanel.getDataTable().addSortChangeHandler(new SortChangeHandler<E>() {
            @Override
            public void onChange() {
                obtain(getPageNumber());
            }
        });

        showColumnSelector(true);

        dataTablePanel.setPageSizeOptions(Arrays.asList(new Integer[] { PAGESIZE_SMALL, PAGESIZE_MEDIUM, PAGESIZE_LARGE }));

        dataTablePanel.setStyleName(PaneTheme.StyleName.ListerListPanel.name());

        setWidget(dataTablePanel);

        setAllowAddNew(allowAddNew);
        setAllowDelete(allowDelete);

    }

    public void setItemZoomInCommand(ItemZoomInCommand<E> itemZoomInCommand) {
        dataTablePanel.setItemZoomInCommand(itemZoomInCommand);
    }

    protected void setAllowDelete(boolean allowDelete) {
        // delete items stuff:
        if (allowDelete) {
            dataTablePanel.setDelActionCommand(new Command() {
                @Override
                public void execute() {
                    onItemsDelete(getDataTablePanel().getDataTable().getSelectedItems());
                }
            });
        }
    }

    protected void setAllowAddNew(boolean allowAddNew) {
        // new item stuff:
        if (allowAddNew) {
            dataTablePanel.setAddActionCommand(new Command() {
                @Override
                public void execute() {
                    onItemNew();
                }
            });
        }

    }

    public void setDataSource(ListerDataSource<E> dataSource) {
        this.dataSource = dataSource;
    }

    public ListerDataSource<E> getDataSource() {
        return dataSource;
    }

    public void obtain(final int pageNumber) {
        assert dataSource != null : "dataSource is not installed";

        EntityListCriteria<E> criteria = EntityListCriteria.create(clazz);
        criteria.setPageNumber(pageNumber);
        criteria.setPageSize(dataTablePanel.getPageSize());
        criteria.setSorts(getSorting());

        dataSource.obtain(updateCriteria(criteria), new DefaultAsyncCallback<EntitySearchResult<E>>() {
            @Override
            public void onSuccess(final EntitySearchResult<E> result) {
                log.trace("dataTable {} data received {}", GWTJava5Helper.getSimpleName(clazz), result.getData().size());
                // Separate RPC serialization and table painting
                Scheduler.get().scheduleFinally(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        dataTablePanel.populateData(result.getData(), pageNumber, result.hasMoreData(), result.getTotalRows());
                        onObtainSuccess();
                    }
                });
            }
        });
    }

    //TODO Misha rename to populate
    protected void onObtainSuccess() {
    }

    protected void onItemNew() {
    }

    protected void onItemsDelete(Collection<E> items) {
    }

    public void setDataTableModel(DataTableModel<E> dataTableModel) {
        dataTableModel.setPageSize(ApplicationMode.isDevelopment() ? PAGESIZE_SMALL : PAGESIZE_MEDIUM);
        dataTablePanel.setDataTableModel(dataTableModel);
    }

    public E proto() {
        return dataTablePanel.proto();
    }

    public void showColumnSelector(boolean show) {
        dataTablePanel.getDataTable().setColumnSelectorVisible(show);
    }

    // selection/checking stuff:

    public E getSelectedItem() {
        return dataTablePanel.getDataTable().getSelectedItem();
    }

    public void addItemSelectionHandler(ItemSelectionHandler<E> handler) {
        if (itemSelectionHandlers == null) {
            itemSelectionHandlers = new ArrayList<>(2);
        }

        itemSelectionHandlers.add(handler);
        dataTablePanel.getDataTable().addItemSelectionHandler(new DataTable.ItemSelectionHandler() {
            @Override
            public void onChange() {
                if (itemSelectionHandlers != null) {
                    for (ItemSelectionHandler<E> handler : itemSelectionHandlers) {
                        handler.onSelect(dataTablePanel.getDataTable().getSelectedItem());
                    }
                }
            }
        });
    }

    public Collection<E> getSelectedItems() {
        return dataTablePanel.getDataTable().getSelectedItems();
    }

    public int getPageSize() {
        return dataTablePanel.getPageSize();
    }

    public int getPageNumber() {
        return dataTablePanel.getPageNumber();
    }

    public void addActionItem(Widget widget) {
        dataTablePanel.addUpperActionItem(widget);
    }

    public List<Criterion> getFilters() {
        return dataTablePanel.getFilters();
    }

    public void setFilters(List<Criterion> filters) {
        if (filters == null || filters.isEmpty()) {
            dataTablePanel.resetFilters();
        } else {
            dataTablePanel.setFilters(filters);
        }
    }

    public void resetFilters() {
        setFilters(getDefaultFilters());
    }

    /**
     * Override in descendants to supply desired set
     * 
     * @return default filter list (null);
     */
    public List<Criterion> getDefaultFilters() {
        return null;
    }

    public List<Sort> getSorting() {
        return dataTablePanel.getDataTableModel().getSortCriteria();
    }

    public void setSorting(List<Sort> sorts) {
        dataTablePanel.getDataTableModel().setSortColumn(null);
        dataTablePanel.getDataTableModel().setSecondarySortColumn(null);

        if (sorts != null) {
            if (sorts.size() > 0) {
                Sort sort = sorts.get(0);
                ColumnDescriptor column = dataTablePanel.getDataTableModel().getColumnDescriptor(sort.getPropertyPath());
                dataTablePanel.getDataTableModel().setSortColumn(column);
                dataTablePanel.getDataTableModel().setSortAscending(!sort.isDescending());
            }
            if (sorts.size() > 1) {
                Sort sort = sorts.get(1);
                ColumnDescriptor column = dataTablePanel.getDataTableModel().getColumnDescriptor(sort.getPropertyPath());
                dataTablePanel.getDataTableModel().setSecondarySortColumn(column);
                dataTablePanel.getDataTableModel().setSecondarySortAscending(!sort.isDescending());
            }
        }
    }

    public void resetSorting() {
        setSorting(getDefaultSorting());
    }

    /**
     * Override in descendants to supply desired set
     * 
     * @return default sorting list (null);
     */
    public List<Sort> getDefaultSorting() {
        return null;
    }

    public void discard() {
        dataTablePanel.discard();
    }

    public IMemento getMemento() {
        return memento;
    }

    /**
     * Do not store and restore filters set on this lister.
     * The lister filter is created by navigation link, e.g. parent filter
     */
    public void setExternalFilters(List<Criterion> externalFilters) {
        this.externalFilters = externalFilters;
    }

    public void storeState(Place place) {
        getMemento().setCurrentPlace(place);

        if (externalFilters == null) {
            getMemento().clear();

            getMemento().putInteger(MementoKeys.page.name(), getPageNumber());
            getMemento().putObject(MementoKeys.filterData.name(), getFilters());
            getMemento().putObject(MementoKeys.sortingData.name(), getSorting());
        }
    }

    @SuppressWarnings("unchecked")
    public void restoreState() {
        int pageNumber = 0;
        List<Criterion> filters = getDefaultFilters();
        List<Sort> sorts = getDefaultSorting();

        if (getMemento().mayRestore() && externalFilters == null) {
            pageNumber = getMemento().getInteger(MementoKeys.page.name());
            filters = (List<Criterion>) getMemento().getObject(MementoKeys.filterData.name());
            sorts = (List<Sort>) getMemento().getObject(MementoKeys.sortingData.name());
        } else if (externalFilters != null) {
            filters = externalFilters;
        }

        setFilters(filters);
        setSorting(sorts);

        // should be called last:
        obtain(pageNumber);
    }

    public DataTablePanel<E> getDataTablePanel() {
        return dataTablePanel;
    }

    protected EntityListCriteria<E> updateCriteria(EntityListCriteria<E> criteria) {
        if (getFilters() != null) {
            for (Criterion fd : getFilters()) {
                if (fd instanceof PropertyCriterion) {
                    if (((PropertyCriterion) fd).isValid()) {
                        criteria.add(fd);
                    }
                } else {
                    criteria.add(fd);
                }
            }
        }

        return criteria;
    }

}

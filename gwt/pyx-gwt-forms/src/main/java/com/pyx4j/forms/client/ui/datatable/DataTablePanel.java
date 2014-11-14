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
package com.pyx4j.forms.client.ui.datatable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemSelectionHandler;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.forms.client.ui.datatable.filter.CriteriaEditableComponentFactory;
import com.pyx4j.forms.client.ui.datatable.filter.DataTableFilterItem;
import com.pyx4j.forms.client.ui.datatable.filter.DataTableFilterPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.images.WidgetsImages;
import com.pyx4j.widgets.client.memento.IMementoAware;
import com.pyx4j.widgets.client.memento.IMementoInput;
import com.pyx4j.widgets.client.memento.IMementoOutput;

public class DataTablePanel<E extends IEntity> extends FlowPanel implements RequiresResize, IMementoAware {

    private static final Logger log = LoggerFactory.getLogger(DataTablePanel.class);

    private static final I18n i18n = I18n.get(DataTableFilterItem.class);

    public static int PAGESIZE_SMALL = 10;

    public static int PAGESIZE_MEDIUM = 25;

    public static int PAGESIZE_LARGE = 50;

    private final E entityPrototype;

    private final DataTable<E> dataTable;

    private int pageNumber = 0;

    private DataTableScrollPanel dataTableScroll;

    private final DataTableActionsBar topActionsBar;

    private final DataTableActionsBar bottomActionsBar;

    private final DataTableFilterPanel<E> filterPanel;

    private IEditableComponentFactory compFactory = new CriteriaEditableComponentFactory();

    private WidgetsImages images;

    private Button addButton;

    private String addButtonCaption;

    private Button delButton;

    private Button filterButton;

    private final Class<E> clazz;

    private ListerDataSource<E> dataSource;

    private List<Criterion> externalFilters;

    public DataTablePanel(Class<E> clazz) {
        this(clazz, false, false);
    }

    public DataTablePanel(Class<E> clazz, boolean allowAddNew, boolean allowDelete) {
        this(clazz, FolderImages.INSTANCE, allowAddNew, allowDelete);
    }

    public DataTablePanel(Class<E> clazz, WidgetsImages images, boolean allowAddNew, boolean allowDelete) {
        this.clazz = clazz;
        this.images = images;
        entityPrototype = EntityFactory.getEntityPrototype(clazz);

        topActionsBar = new DataTableActionsBar();
        add(topActionsBar);

        filterPanel = new DataTableFilterPanel<E>(this);
        add(filterPanel);

        dataTable = new DataTable<E>();
        dataTableScroll = new DataTableScrollPanel();
        dataTableScroll.setStyleName(DataTableTheme.StyleName.DataTableHolder.name());
        add(dataTableScroll);

        bottomActionsBar = new DataTableActionsBar();
        add(bottomActionsBar);

        filterButton = new Button(i18n.tr("Filter"), new Command() {

            @Override
            public void execute() {
                filterPanel.setFilters(null);
            }
        });
        topActionsBar.getToolbar().addItem(filterButton);

        setAddNewActionEnabled(allowAddNew);
        setDeleteActionEnabled(allowDelete);
    }

    public Class<E> getEntityClass() {
        return clazz;
    }

    public void setDataTableModel(DataTableModel<E> model) {
        dataTable.setDataTableModel(model);
        topActionsBar.setDataTableModel(model);
        bottomActionsBar.setDataTableModel(model);
        if (delButton != null) {
            model.setMultipleSelection(true);
        }
    }

    public void addItemSelectionHandler(ItemSelectionHandler handler) {
        dataTable.addItemSelectionHandler(handler);
    }

    public void setItemZoomInCommand(ItemZoomInCommand<E> itemZoomInCommand) {
        dataTable.setItemZoomInCommand(itemZoomInCommand);
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

    public void setFilterComponentFactory(IEditableComponentFactory compFactory) {
        this.compFactory = compFactory;
    }

    public IEditableComponentFactory getFilterComponentFactory() {
        return compFactory;
    }

    public void setDeleteActionEnabled(boolean enabled) {
        if (delButton == null && enabled) {
            topActionsBar.getToolbar().insertItem(delButton = new Button(FolderImages.INSTANCE.delButton().hover(), i18n.tr("Delete Checked"), new Command() {
                @Override
                public void execute() {
                    onItemsDelete(getDataTable().getSelectedItems());
                }
            }), 1);

            delButton.setEnabled(getDataTableModel() != null && getDataTableModel().isAnyRowSelected());

            if (getDataTable().getDataTableModel() != null) {
                getDataTable().getDataTableModel().setMultipleSelection(true);
            }

            getDataTable().addItemSelectionHandler(new ItemSelectionHandler() {
                @Override
                public void onChange() {
                    delButton.setEnabled(getDataTable().getDataTableModel().isAnyRowSelected());
                }
            });

        }
        if (delButton != null) {
            delButton.setVisible(enabled);
        }
    }

    public void setAddNewActionEnabled(boolean enabled) {
        if (addButton == null && enabled) {
            if (addButtonCaption == null) {
                addButtonCaption = i18n.tr("New {0}", entityPrototype.getEntityMeta().getCaption());
            }
            topActionsBar.getToolbar().insertItem(addButton = new Button(FolderImages.INSTANCE.addButton().hover(), addButtonCaption, new Command() {
                @Override
                public void execute() {
                    onItemNew();
                }
            }), 0);
        }
        if (addButton != null) {
            addButton.setVisible(enabled);
        }
    }

    public void setAddNewActionCaption(String caption) {
        if (addButton != null) {
            addButton.setCaption(caption);
        } else {
            addButtonCaption = caption;
        }
    }

    protected void onItemNew() {
    }

    protected void onItemsDelete(Collection<E> items) {
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
        if (delButton != null) {
            delButton.setEnabled(getDataTableModel().isAnyRowSelected());
        }
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

    public String toStringForPrint() {
        return dataTable.toString();
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public WidgetsImages getImages() {
        return images;
    }

    public List<Criterion> getFilters() {
        return filterPanel.getFilters();
    }

    public void setFilters(List<Criterion> filters) {
        if (filters == null || filters.isEmpty()) {
            filterPanel.resetFilters();
        } else {
            filterPanel.setFilters(filters);
        }
    }

    public void resetFilters() {
        setFilters(getDefaultFilters());
    }

    public void setFilteringEnabled(boolean enabled) {
        filterButton.setVisible(enabled);
    }

    @Override
    public void onResize() {
        dataTableScroll.updateColumnVizibility();
    }

    public void setDataSource(ListerDataSource<E> dataSource) {
        setPageNumber(0);
        this.dataSource = dataSource;
    }

    public ListerDataSource<E> getDataSource() {
        return dataSource;
    }

    public void populate() {
        assert dataSource != null : "dataSource is not installed";

        EntityListCriteria<E> criteria = EntityListCriteria.create(clazz);
        criteria.setPageNumber(pageNumber);
        criteria.setPageSize(getDataTableModel().getPageSize());
        criteria.setSorts(getDataTableModel().getSortCriteria());

        dataSource.obtain(updateCriteria(criteria), new DefaultAsyncCallback<EntitySearchResult<E>>() {
            @Override
            public void onSuccess(final EntitySearchResult<E> result) {
                log.trace("dataTable {} data received {}", GWTJava5Helper.getSimpleName(clazz), result.getData().size());
                // Separate RPC serialization and table painting
                Scheduler.get().scheduleFinally(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        populateData(result.getData(), pageNumber, result.hasMoreData(), result.getTotalRows());
                        onPopulate();
                    }
                });
            }
        });
    }

    protected void onPopulate() {
    }

    public void setSortCriteria(List<Sort> sorts) {
        getDataTableModel().setSortCriteria(sorts);
    }

    @Override
    public void saveState(IMementoOutput memento) {
        if (getDataTableModel() != null) {
            memento.write(pageNumber);
            memento.write(getFilters());
            memento.write(getDataTableModel().getSortCriteria());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void restoreState(IMementoInput memento) {
        if (getDataTableModel() != null) {
            List<Criterion> filters = getDefaultFilters();
            List<Sort> sorts = getDefaultSorting();

            if (externalFilters == null) {
                Integer pageNumberInteger = (Integer) memento.read();
                pageNumber = pageNumberInteger == null ? 0 : pageNumberInteger;
                filters = (List<Criterion>) memento.read();
                sorts = (List<Sort>) memento.read();
            } else if (externalFilters != null) {
                filters = externalFilters;
            }

            setFilters(filters);
            getDataTableModel().setSortCriteria(sorts);
        }
    }

    public List<Criterion> getDefaultFilters() {
        return null;
    }

    public List<Sort> getDefaultSorting() {
        return null;
    }

    public void setExternalFilters(List<Criterion> externalFilters) {
        this.externalFilters = externalFilters;
    }

    public E getSelectedItem() {
        return dataTable.getSelectedItem();
    }

    public Collection<E> getSelectedItems() {
        return dataTable.getSelectedItems();
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

    class DataTableScrollPanel extends ScrollPanel {

        public DataTableScrollPanel() {
            super(dataTable.asWidget());
        }

        protected void updateColumnVizibility() {
            dataTable.updateColumnVizibility(getContainerElement().getOffsetWidth(), getMaximumHorizontalScrollPosition() > 0);
        }
    }

}
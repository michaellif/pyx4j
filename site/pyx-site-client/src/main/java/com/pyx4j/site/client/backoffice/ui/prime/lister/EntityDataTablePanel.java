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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemSelectionHandler;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.forms.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;
import com.pyx4j.site.client.backoffice.ui.PaneTheme;

public class EntityDataTablePanel<E extends IEntity> extends ScrollPanel {

    public static int PAGESIZE_SMALL = 10;

    public static int PAGESIZE_MEDIUM = 25;

    public static int PAGESIZE_LARGE = 50;

    private final DataTablePanel<E> dataTablePanel;

    public EntityDataTablePanel(Class<E> clazz) {
        this(clazz, false, false);
    }

    public EntityDataTablePanel(Class<E> clazz, boolean allowAddNew) {
        this(clazz, allowAddNew, false);
    }

    public EntityDataTablePanel(Class<E> clazz, boolean allowAddNew, boolean allowDelete) {
        setStyleName(PaneTheme.StyleName.Lister.name());
        setSize("100%", "100%");
        dataTablePanel = new DataTablePanel<E>(clazz) {
            @Override
            protected void onPopulate() {
                EntityDataTablePanel.this.onObtainSuccess();
                super.onPopulate();
            }

            @Override
            public List<Criterion> getDefaultFilters() {
                return EntityDataTablePanel.this.getDefaultFilters();
            }

            @Override
            public List<Sort> getDefaultSorting() {
                return EntityDataTablePanel.this.getDefaultSorting();
            }

            @Override
            protected void onItemNew() {
                EntityDataTablePanel.this.onItemNew();
            }

            @Override
            protected void onItemsDelete(Collection<E> items) {
                EntityDataTablePanel.this.onItemsDelete(items);
            }
        };
        dataTablePanel.getElement().getStyle().setPaddingBottom(40, Unit.PX);

        dataTablePanel.setFilterApplyCommand(new Command() {
            @Override
            public void execute() {
                populate(0);
            }
        });

        dataTablePanel.setFirstActionHandler(new Command() {
            @Override
            public void execute() {
                populate(0);
            }
        });
        dataTablePanel.setPrevActionHandler(new Command() {
            @Override
            public void execute() {
                populate(dataTablePanel.getDataTableModel().getPageNumber() - 1);
            }
        });
        dataTablePanel.setNextActionHandler(new Command() {
            @Override
            public void execute() {
                populate(dataTablePanel.getDataTableModel().getPageNumber() + 1);
            }
        });
        dataTablePanel.setLastActionHandler(new Command() {
            @Override
            public void execute() {
                populate((dataTablePanel.getDataTableModel().getTotalRows() - 1) / dataTablePanel.getDataTableModel().getPageSize());
            }
        });

        dataTablePanel.setPageSizeActionHandler(new Command() {
            @Override
            public void execute() {
                populate(0);
            }
        });

        dataTablePanel.getDataTable().setHasColumnClickSorting(true);
        dataTablePanel.getDataTable().addSortChangeHandler(new SortChangeHandler<E>() {
            @Override
            public void onChange() {
                populate(getPageNumber());
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
        dataTablePanel.setDeleteActionEnabled(allowDelete);
    }

    protected void setAllowAddNew(boolean allowAddNew) {
        dataTablePanel.setAddNewActionEnabled(allowAddNew);
    }

    public void setDataSource(ListerDataSource<E> dataSource) {
        dataTablePanel.setDataSource(dataSource);
    }

    public ListerDataSource<E> getDataSource() {
        return dataTablePanel.getDataSource();
    }

    public void populate(final int pageNumber) {
        dataTablePanel.setPageNumber(pageNumber);
        dataTablePanel.populate();
    }

    public void populate() {
        dataTablePanel.populate();
    }

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

    public void addItemSelectionHandler(ItemSelectionHandler handler) {
        dataTablePanel.addItemSelectionHandler(handler);
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
        dataTablePanel.setFilters(filters);
    }

    public void resetFilters() {
        dataTablePanel.resetFilters();
    }

    public void setFilterComponentFactory(IEditableComponentFactory compFactory) {
        dataTablePanel.setFilterComponentFactory(compFactory);
    }

    /**
     * Override in descendants to supply desired set
     * 
     * @return default filter list (null);
     */
    public List<Criterion> getDefaultFilters() {
        return null;
    }

    public List<Sort> getSortCriteria() {
        return dataTablePanel.getDataTableModel().getSortCriteria();
    }

    public void setSortCriteria(List<Sort> sorts) {
        dataTablePanel.getDataTableModel().setSortCriteria(sorts);
    }

    public void resetSorting() {
        setSortCriteria(getDefaultSorting());
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

    /**
     * Do not store and restore filters set on this lister.
     * The lister filter is created by navigation link, e.g. parent filter
     */
    public void setExternalFilters(List<Criterion> externalFilters) {
        dataTablePanel.setExternalFilters(externalFilters);
    }

    public DataTablePanel<E> getDataTablePanel() {
        return dataTablePanel;
    }

    protected EntityListCriteria<E> updateCriteria(EntityListCriteria<E> criteria) {
        return dataTablePanel.updateCriteria(criteria);
    }

}

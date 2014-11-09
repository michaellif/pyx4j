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
package com.pyx4j.site.client.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.forms.client.ui.datatable.DataTable.SortChangeHandler;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.PaneTheme;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class SiteDataTablePanel<E extends IEntity> extends DataTablePanel<E> {

    private static final I18n i18n = I18n.get(SiteDataTablePanel.class);

    private Class<? extends CrudAppPlace> itemOpenPlaceClass;

    public SiteDataTablePanel(Class<E> clazz) {
        this(clazz, false);
    }

    public SiteDataTablePanel(Class<E> clazz, boolean allowAddNew) {
        this(clazz, allowAddNew, false);
    }

    public SiteDataTablePanel(Class<E> clazz, boolean allowAddNew, boolean allowDelete) {
        super(clazz);

        setStyleName(PaneTheme.StyleName.Lister.name());

        setFilterApplyCommand(new Command() {
            @Override
            public void execute() {
                populate(0);
            }
        });

        setFirstActionHandler(new Command() {
            @Override
            public void execute() {
                populate(0);
            }
        });
        setPrevActionHandler(new Command() {
            @Override
            public void execute() {
                populate(getDataTableModel().getPageNumber() - 1);
            }
        });
        setNextActionHandler(new Command() {
            @Override
            public void execute() {
                populate(getDataTableModel().getPageNumber() + 1);
            }
        });
        setLastActionHandler(new Command() {
            @Override
            public void execute() {
                populate((getDataTableModel().getTotalRows() - 1) / getDataTableModel().getPageSize());
            }
        });

        setPageSizeActionHandler(new Command() {
            @Override
            public void execute() {
                populate(0);
            }
        });

        getDataTable().setHasColumnClickSorting(true);
        getDataTable().addSortChangeHandler(new SortChangeHandler<E>() {
            @Override
            public void onChange() {
                populate(getPageNumber());
            }
        });

        showColumnSelector(true);

        setPageSizeOptions(Arrays.asList(new Integer[] { DataTablePanel.PAGESIZE_SMALL, DataTablePanel.PAGESIZE_MEDIUM, DataTablePanel.PAGESIZE_LARGE }));

        setStyleName(PaneTheme.StyleName.ListerListPanel.name());

        setAddNewActionEnabled(allowAddNew);
        setDeleteActionEnabled(allowDelete);

        this.itemOpenPlaceClass = AppPlaceEntityMapper.resolvePlaceClass(clazz);
        setItemZoomInCommand(new ItemZoomInCommand<E>() {
            @Override
            public void execute(E item) {
                //TODO
            }
        });
    }

    @Override
    protected void onItemsDelete(final Collection<E> items) {
        MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Do you really want to delete checked items?"), new Command() {
            @Override
            public void execute() {
                for (E item : items) {
                    //TODO
                }
            }
        });
    }

    public Class<? extends CrudAppPlace> getItemOpenPlaceClass() {
        return itemOpenPlaceClass;
    }

    public void populate(final int pageNumber) {
        setPageNumber(pageNumber);
        super.populate();
    }

    @Override
    public void setDataTableModel(DataTableModel<E> dataTableModel) {
        dataTableModel.setPageSize(ApplicationMode.isDevelopment() ? DataTablePanel.PAGESIZE_SMALL : DataTablePanel.PAGESIZE_MEDIUM);
        super.setDataTableModel(dataTableModel);
    }

    public void showColumnSelector(boolean show) {
        getDataTable().setColumnSelectorVisible(show);
    }

    // selection/checking stuff:

    @Override
    public E getSelectedItem() {
        return getDataTable().getSelectedItem();
    }

    @Override
    public Collection<E> getSelectedItems() {
        return getDataTable().getSelectedItems();
    }

    /**
     * Override in descendants to supply desired set
     * 
     * @return default filter list (null);
     */
    @Override
    public List<Criterion> getDefaultFilters() {
        return null;
    }

    public List<Sort> getSortCriteria() {
        return getDataTableModel().getSortCriteria();
    }

    public void resetSorting() {
        setSortCriteria(getDefaultSorting());
    }

    /**
     * Override in descendants to supply desired set
     * 
     * @return default sorting list (null);
     */
    @Override
    public List<Sort> getDefaultSorting() {
        return null;
    }

}

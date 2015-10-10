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
 */
package com.pyx4j.site.client.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.entity.shared.IntegrityConstraintUserRuntimeException;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.PaneTheme;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class SiteDataTablePanel<E extends IEntity> extends DataTablePanel<E> {

    private static final I18n i18n = I18n.get(SiteDataTablePanel.class);

    private AbstractListCrudService<E> service;

    public SiteDataTablePanel(Class<E> entityClass, AbstractListCrudService<E> service) {
        this(entityClass, service, false);
    }

    public SiteDataTablePanel(Class<E> entityClass, AbstractListCrudService<E> service, boolean allowAddNew) {
        this(entityClass, service, allowAddNew, false);
    }

    public SiteDataTablePanel(Class<E> entityClass, AbstractListCrudService<E> service, boolean allowAddNew, boolean allowDelete) {
        super(entityClass);

        this.service = service;

        setStyleName(PaneTheme.StyleName.ListerListPanel.name());

        getDataTable().setHasColumnClickSorting(true);

        setPageSizeOptions(Arrays.asList(new Integer[] { DataTablePanel.PAGESIZE_SMALL, DataTablePanel.PAGESIZE_MEDIUM, DataTablePanel.PAGESIZE_LARGE }));

        setAddNewActionEnabled(allowAddNew);
        setDeleteActionEnabled(allowDelete);

        setItemZoomInCommand(new ItemZoomInCommand<E>() {
            @Override
            public void execute(E item) {
                view(getItemOpenPlaceClass(), item.getPrimaryKey());
            }
        });

        setDataSource(new ListerDataSource<E>(entityClass, service) {
            @Override
            protected void export() {
                // TODO This should be EntityQueryCriteria or Filters
                EntityListCriteria<E> criteria = EntityListCriteria.create(getEntityClass());
                criteria.setSorts(getDataTableModel().getSortCriteria());
                updateCriteria(SiteDataTablePanel.this.updateCriteria(criteria));
                DataTableDocCreation.createExcelExport(getDataSource().getDocCreationService(), criteria, getDataTable().getColumnDescriptors());
            }
        });

        setExportActionEnabled(getDataSource().getDocCreationService() != null);
    }

    protected void view(Class<? extends CrudAppPlace> openPlaceClass, Key itemID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formViewerPlace(itemID));
    }

    protected void editNew(Class<? extends CrudAppPlace> openPlaceClass) {
        if (canCreateNewItem()) {
            if (getDataSource().getParentEntityClass() != null) {
                AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formNewItemPlace(getDataSource().getParentEntityId(),
                        getDataSource().getParentEntityClass()));
            } else {
                AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formNewItemPlace(getDataSource().getParentEntityId()));
            }
        }
    }

    protected void editNew(Class<? extends CrudAppPlace> openPlaceClass, InitializationData initializationData) {
        if (canCreateNewItem()) {
            AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formNewItemPlace(initializationData));
        }
    }

    protected boolean canCreateNewItem() {
        if (EntityFactory.getEntityMeta(getEntityClass()).isAnnotationPresent(SecurityEnabled.class)) {
            return SecurityController.check(DataModelPermission.permissionCreate(getEntityClass()));
        } else {
            return true;
        }
    }

    @Override
    protected void onItemNew() {
        if (canCreateNewItem()) {
            if (getDataSource().getParentEntityClass() != null) {
                AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(getItemOpenPlaceClass())
                        .formNewItemPlace(getDataSource().getParentEntityId(), getDataSource().getParentEntityClass()));
            } else {
                AppSite.getPlaceController()
                        .goTo(AppSite.getHistoryMapper().createPlace(getItemOpenPlaceClass()).formNewItemPlace(getDataSource().getParentEntityId()));
            }
        }
    }

    public void delete(final Key itemID) {
        service.delete(new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                onDeleted(itemID, true);
                populate();
            }

            @Override
            public void onFailure(Throwable caught) {
                onDeleted(itemID, false);
                if (caught instanceof IntegrityConstraintUserRuntimeException) {
                    MessageDialog.error(i18n.tr("Item Deletion"), caught.getMessage());
                } else {
                    throw new UnrecoverableClientError(caught);
                }
            }
        }, itemID);
    }

    public void onDeleted(Key itemID, boolean isSuccessful) {

    }

    @Override
    protected void onItemsDelete(final Collection<E> items) {
        MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Do you really want to delete checked items?"), new Command() {
            @Override
            public void execute() {
                for (E item : items) {
                    delete(item.getPrimaryKey());
                }
            }
        });
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

    protected AbstractListCrudService<E> getService() {
        return service;
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

    protected Class<? extends CrudAppPlace> getItemOpenPlaceClass() {
        return AppPlaceEntityMapper.resolvePlaceClass(getEntityClass());
    }

}

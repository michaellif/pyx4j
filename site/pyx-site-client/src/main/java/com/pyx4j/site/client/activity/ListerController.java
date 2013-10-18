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
 * @author VladL
 * @version $Id$
 */
package com.pyx4j.site.client.activity;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IntegrityConstraintUserRuntimeException;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog_v2;

public class ListerController<E extends IEntity> implements ILister.Presenter<E> {

    private static final I18n i18n = I18n.get(ListerController.class);

    private final ILister<E> view;

    private final ListerDataSource<E> dataSource;

    private final AbstractListService<E> service;

    private Key parentId;

    private Class<? extends IEntity> parentClass;

    public ListerController(ILister<E> view, AbstractListService<E> service, Class<E> entityClass) {
        // development correctness checks:
        assert (view != null);
        assert (service != null);
        assert (entityClass != null);

        this.parentId = null;
        this.view = view;
        this.service = service;
        this.dataSource = new ListerDataSource<E>(entityClass, service);
        view.setPresenter(this);
    }

    public ILister<E> getView() {
        return view;
    }

    public AbstractListService<E> getService() {
        return service;
    }

    @Override
    public ListerDataSource<E> getDataSource() {
        return dataSource;
    }

    @Override
    public Key getParent() {
        return parentId;
    }

    @Override
    public Class<? extends IEntity> getParentClass() {
        return parentClass;
    }

    @Override
    public void setParent(Key parentId) {
        setParent(parentId, null);
    }

    @Override
    public void setParent(Key parentID, Class<? extends IEntity> parentClass) {
        this.parentId = parentID; // save parent id for newItem creation...
        this.parentClass = parentClass; // save parent class for polymorphic queries...
        dataSource.setParentFiltering(parentID, parentClass);
    }

    @Override
    public void setPreDefinedFilters(List<Criterion> filters) {
        dataSource.setPreDefinedFilters(filters);
    }

    @Override
    public void addPreDefinedFilters(List<Criterion> filters) {
        dataSource.addPreDefinedFilters(filters);
    }

    @Override
    public void addPreDefinedFilter(Criterion filter) {
        dataSource.addPreDefinedFilter(filter);
    }

    @Override
    public void clearPreDefinedFilters() {
        dataSource.clearPreDefinedFilters();
    }

    @Override
    public void populate() {
        view.getLister().restoreState();
    }

    // TODO : check this optimization (in retrieveData):
//    protected boolean isFilterCreateEmptyDataSet() {
//        return (parentFiltering != null) && (getParent() == null);
//    }

    @Override
    public void retrieveData(int pageNumber) {

        // TODO : check this optimization:
        // Fix/Optimization for new parent Entity. e.g. Do not go to server to get empty list
//        if (isFilterCreateEmptyDataSet()) {
//            view.populateData(new Vector<E>(), pageNumber, false, 0);
//            return;
//        }

        view.getLister().obtain(pageNumber);
    }

    @Override
    public void refresh() {
        retrieveData(view.getPageNumber());
    }

    @Override
    public void view(Class<? extends CrudAppPlace> openPlaceClass, Key itemID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formViewerPlace(itemID));
    }

    @Override
    public void edit(Class<? extends CrudAppPlace> openPlaceClass, Key itemID) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formEditorPlace(itemID));
    }

    @Override
    public void editNew(Class<? extends CrudAppPlace> openPlaceClass) {
        if (canCreateNewItem()) {
            if (getParentClass() != null) {
                AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formNewItemPlace(getParent(), getParentClass()));
            } else {
                AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formNewItemPlace(getParent()));
            }
        }
    }

    @Override
    public void editNew(Class<? extends CrudAppPlace> openPlaceClass, InitializationData initializationData) {
        if (canCreateNewItem()) {
            AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formNewItemPlace(initializationData));
        }
    }

    /**
     * Empty methods, implementations don't need to call it.
     */
    @Override
    public boolean canCreateNewItem() {
        return true;
    }

    @Override
    public void delete(final Key itemID) {
        service.delete(new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                onDeleted(itemID, true);
                retrieveData(view.getPageNumber());
            }

            @Override
            public void onFailure(Throwable caught) {
                onDeleted(itemID, false);
                if (caught instanceof IntegrityConstraintUserRuntimeException) {
                    MessageDialog_v2.error(i18n.tr("Item Deletion"), caught.getMessage());
                } else {
                    throw new UnrecoverableClientError(caught);
                }
            }
        }, itemID);
    }

    protected void onDeleted(Key itemID, boolean isSuccessful) {
        view.onDeleted(itemID, isSuccessful);
    }

    @Override
    public AppPlace getPlace() {
        return null;
    }
}

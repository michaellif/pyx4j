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
package com.pyx4j.site.client.activity.crud;

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;
import com.pyx4j.site.rpc.CrudAppPlace;

public class ListerActivityBase<E extends IEntity> extends AbstractActivity implements IListerView.Presenter<E> {

    private final IListerView<E> view;

    private final ListerDataSource<E> dataSource;

    private final AbstractListService<E> service;

    private Key parentID;

    private Class<? extends IEntity> parentClass;

    public ListerActivityBase(Place place, IListerView<E> view, AbstractListService<E> service, Class<E> entityClass) {
        // development correctness checks:
        assert (view != null);
        assert (service != null);
        assert (entityClass != null);

        this.view = view;
        this.service = service;
        this.dataSource = new ListerDataSource<E>(entityClass, service);
        view.setPresenter(this);

        view.getMemento().setCurrentPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.discard();
        populate();
        containerWidget.setWidget(view);
    }

    @Override
    public void onStop() {
        view.getLister().storeState(view.getMemento().getCurrentPlace());
        view.discard();
        super.onStop();
    }

    @Override
    public ListerDataSource<E> getDataSource() {
        return dataSource;
    }

    @Override
    public void setParent(Key parentID) {
        this.parentID = parentID; // save parent id for newItem creation...
        this.parentClass = null;
        setFilterByParent(true);
    }

    @Override
    public void setParent(Key parentID, Class<? extends IEntity> parentClass) {
        this.parentID = parentID; // save parent id for newItem creation...
        this.parentClass = parentClass; // save parent class for polymorphic queries...
        setFilterByParent(true);
    }

    @Override
    public void setFilterByParent(boolean flag) {
        if (flag) {
            dataSource.setParentFiltering(parentID, parentClass);
        } else {
            dataSource.clearParentFiltering();
        }
    }

    @Override
    public List<DataTableFilterData> getPreDefinedFilters() {
        return dataSource.getPreDefinedFilters();
    }

    @Override
    public void setPreDefinedFilters(List<DataTableFilterData> preDefinedFilters) {
        dataSource.setPreDefinedFilters(preDefinedFilters);
    }

    @Override
    public void populate() {
        view.getLister().restoreState();
    }

    // TODO : check this optimization (in retrieveData):
//    protected boolean isFilterCreateEmptyDataSet() {
//        return (parentFiltering != null) && (parentID == null);
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
    public void editNew(Class<? extends CrudAppPlace> openPlaceClass, Key parentID) {
        if (canEditNew()) {
            AppSite.getPlaceController().goTo(
                    AppSite.getHistoryMapper().createPlace(openPlaceClass).formNewItemPlace(parentID != null ? parentID : this.parentID));
        }
    }

    /**
     * Empty methods, implementations don't need to call it.
     */
    @Override
    public boolean canEditNew() {
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
                throw new UnrecoverableClientError(caught);
            }
        }, itemID);
    }

    protected void onDeleted(Key itemID, boolean isSuccessful) {
        view.onDeleted(itemID, isSuccessful);
    }
}

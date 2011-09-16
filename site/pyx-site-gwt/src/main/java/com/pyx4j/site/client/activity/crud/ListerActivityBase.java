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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.FilterData;
import com.pyx4j.site.client.ui.crud.FilterData.Operands;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.services.AbstractCrudService;

public class ListerActivityBase<E extends IEntity> extends AbstractActivity implements IListerView.Presenter {

    private final IListerView<E> view;

    private final AbstractCrudService<E> service;

    private final Class<E> entityClass;

    private FilterData parentFiltering;

    private List<FilterData> preDefinedFilters;

    private Key parentID;

    public ListerActivityBase(IListerView<E> view, AbstractCrudService<E> service, Class<E> entityClass) {
        // development correctness checks:
        assert (view != null);
        assert (service != null);
        assert (entityClass != null);

        this.view = view;
        this.service = service;
        this.entityClass = entityClass;
        view.setPresenter(this);
    }

    @Override
    public void setPlace(Place place) {
        view.getMemento().setCurrentPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        populate();
    }

    @Override
    public void onStop() {
        view.getLister().storeState(view.getMemento().getCurrentPlace());
        super.onStop();
    }

    @Override
    public void setParentFiltering(Key parentID) {
        parentFiltering = new FilterData(new Path(entityClass, EntityFactory.getEntityMeta(entityClass).getOwnerMemberName()), Operands.is, parentID);
        this.parentID = parentID; // save parent id for newItem creation...
    }

    @Override
    public List<FilterData> getPreDefinedFilters() {
        return preDefinedFilters;
    }

    @Override
    public void setPreDefinedFilters(List<FilterData> preDefinedFilters) {
        this.preDefinedFilters = preDefinedFilters;
    }

    @Override
    public void populate() {
        view.getLister().restoreState();
    }

    @Override
    public void populate(final int pageNumber) {
        EntityListCriteria<E> criteria = constructSearchCriteria();
        criteria.setPageSize(view.getPageSize());
        criteria.setPageNumber(pageNumber);

        service.list(new AsyncCallback<EntitySearchResult<E>>() {
            @Override
            public void onSuccess(EntitySearchResult<E> result) {
                view.populate(result.getData(), pageNumber, result.hasMoreData(), result.getTotalRows());
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, criteria);
    }

    @Override
    public void view(Class<? extends CrudAppPlace> openPlaceClass, Key itemID) {
        CrudAppPlace place = AppSite.getHistoryMapper().createPlace(openPlaceClass);
        place.formViewerPlace(itemID);
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public void edit(Class<? extends CrudAppPlace> openPlaceClass, Key itemID) {
        CrudAppPlace place = AppSite.getHistoryMapper().createPlace(openPlaceClass);
        place.formEditorPlace(itemID);
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public void editNew(Class<? extends CrudAppPlace> openPlaceClass, Key parentID) {
        CrudAppPlace place = AppSite.getHistoryMapper().createPlace(openPlaceClass);
        place.formNewItemPlace(parentID != null ? parentID : this.parentID);
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public void delete(Key itemID) {
        service.delete(new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                populate(view.getPageNumber());
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, itemID);
    }

    protected EntityListCriteria<E> constructSearchCriteria() {
        List<FilterData> currentFilters = new ArrayList<FilterData>();

        // combine filters:
        if (parentFiltering != null) {
            currentFilters.add(parentFiltering);
        }
        if (preDefinedFilters != null) {
            currentFilters.addAll(preDefinedFilters);
        }

        List<FilterData> userDefinedFilters = view.getFiltering();
        if (userDefinedFilters != null) {
            currentFilters.addAll(userDefinedFilters);
        }

        // construct search criteria:
        EntityListCriteria<E> criteria = new EntityListCriteria<E>(entityClass);
        for (FilterData fd : currentFilters) {
            if (fd.isFilterOK()) {
                switch (fd.getOperand()) {
                case is:
                    criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.EQUAL, fd.getValue()));
                    break;
                case isNot:
                    criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.NOT_EQUAL, fd.getValue()));
                    break;
                case like:
                    criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.RDB_LIKE, fd.getValue()));
                    break;
                case greaterThen:
                    criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.GREATER_THAN, fd.getValue()));
                    break;
                case lessThen:
                    criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.LESS_THAN, fd.getValue()));
                    break;
                }
            }
        }

        // apply sorts:
        criteria.setSorts(view.getSorting());

        return criteria;
    }
}

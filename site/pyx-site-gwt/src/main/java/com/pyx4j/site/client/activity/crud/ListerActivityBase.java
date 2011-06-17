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
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
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

    private List<FilterData> preDefinedFilters;

    public ListerActivityBase(IListerView<E> view, AbstractCrudService<E> service, Class<E> entityClass) {
        this.view = view;
        this.service = service;
        this.entityClass = entityClass;
        view.setPresenter(this);
    }

    public ListerActivityBase<E> withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        populateData(0);
    }

    @Override
    public void setParentFiltering(Key parentID) {
        if (preDefinedFilters == null) {
            preDefinedFilters = new ArrayList<FilterData>();
        }

        preDefinedFilters.add(new FilterData(EntityFactory.getEntityMeta(entityClass).getOwnerMemberName(), Operands.is, parentID.toString()));
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
    public void populateData(final int pageNumber) {
        EntitySearchCriteria<E> criteria = new EntitySearchCriteria<E>(entityClass);
        criteria.setPageSize(view.getPageSize());
        criteria.setPageNumber(pageNumber);

        service.search(new AsyncCallback<EntitySearchResult<E>>() {
            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(EntitySearchResult<E> result) {
                view.populateData(result.getData(), pageNumber, result.hasMoreData());
            }
        }, criteria);
    }

    @Override
    public void applyFiltering(List<FilterData> filters) {
        List<FilterData> currentFilters = filters;
        if (preDefinedFilters != null) {
            currentFilters = new ArrayList<FilterData>();
            currentFilters.addAll(preDefinedFilters);
            currentFilters.addAll(filters);
        }

        EntityQueryCriteria<E> criteria = new EntityQueryCriteria<E>(entityClass);
        for (FilterData fd : currentFilters) {
            switch (fd.getOperand()) {
            case is:
                criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.EQUAL, fd.getValue()));
                break;
//            case isNot:
//                criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.NOT_EQUAL, fd.getValue()));
//                break;
//            case contains:
//                criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.IN, fd.getValue()));
//                break;
            case greaterThen:
                criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.GREATER_THAN, fd.getValue()));
                break;
            case lessThen:
                criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.LESS_THAN, fd.getValue()));
                break;
            }
        }

        // TODO (VladS/L) - search using formed criteria - not implemented in service still...
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
        place.formNewItemPlace(parentID);
        AppSite.getPlaceController().goTo(place);
    }
}

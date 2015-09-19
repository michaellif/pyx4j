/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jun 1, 2015
 * @author michaellif
 */
package com.pyx4j.widgets.client.selector;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.TextSearchCriterion;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.gwt.commons.UnrecoverableClientError;

public class ListServiceSuggestOptionsGrabber<E extends IEntity> implements IOptionsGrabber<E> {

    private final Class<E> entityClass;

    private final AbstractListService<E> service;

    private List<Criterion> criterions;

    public ListServiceSuggestOptionsGrabber(Class<E> entityClass, AbstractListService<E> service) {
        this.entityClass = entityClass;
        this.service = service;
    }

    public void setCriterions(List<Criterion> criterions) {
        this.criterions = criterions;
    }

    @Override
    public void grabOptions(final Request request, final Callback<E> callback) {

        EntityListCriteria<E> criteria = EntityListCriteria.create(entityClass);
        if (criterions != null) {
            criteria.addAll(criterions);
        }

        criteria.setPageSize(request.getLimit());
        if (!CommonsStringUtils.isEmpty(request.getQuery())) {
            criteria.add(new TextSearchCriterion(request.getQuery()));
        }

        service.list(new AsyncCallback<EntitySearchResult<E>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(EntitySearchResult<E> result) {
                callback.onOptionsReady(request, new Response<E>(result.getData()));
            }
        }, criteria);

    }

    @Override
    public SelectType getSelectType() {
        return SelectType.Multy;
    }
}

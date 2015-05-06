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
 * Created on Sep 5, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.selector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.selector.IOptionsGrabber;
import com.pyx4j.widgets.client.selector.SingleWordSuggestOptionsGrabber;

public abstract class EntitySingleWordSuggestOptionsGrabber<E extends IEntity> extends SingleWordSuggestOptionsGrabber<E> {

    private EntityListCriteria<E> criteria;

    public EntitySingleWordSuggestOptionsGrabber(AbstractListCrudService<E> service, EntityListCriteria<E> criteria) {
        super(service);
        this.criteria = criteria;
    }

    @Override
    public void grabOptions(IOptionsGrabber.Request request, IOptionsGrabber.Callback<E> callback) {

        AsyncCallback<EntitySearchResult<E>> callbackOptionsGrabber = new DefaultAsyncCallback<EntitySearchResult<E>>() {

            @Override
            public void onSuccess(EntitySearchResult<E> result) {
                filter(result, request.getQuery().toLowerCase());
                callback.onOptionsReady(request, new Response<E>(filtered));
            }

        };

        service.list(callbackOptionsGrabber, criteria);
    }

}

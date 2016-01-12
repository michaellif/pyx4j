/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Jan 12, 2016
 * @author vlads
 */
package com.pyx4j.entity.server;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.ListerCapability;

public class AbstractCrudServiceImplAdapter<TO extends IEntity> implements AbstractCrudService<TO> {

    @Override
    public void obtainListerCapabilities(AsyncCallback<Vector<ListerCapability>> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<TO>> callback, EntityListCriteria<TO> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init(AsyncCallback<TO> callback, com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void create(AsyncCallback<Key> callback, TO editableEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void retrieve(AsyncCallback<TO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveOperation retrieveOperation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(AsyncCallback<Key> callback, TO editableEntity) {
        throw new UnsupportedOperationException();
    }

}

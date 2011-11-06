/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Nov 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.IEntity;

public abstract class AbstractCrudServiceImpl<E extends IEntity> extends AbstractListServiceImpl<E> implements AbstractCrudService<E> {

    protected AbstractCrudServiceImpl(Class<E> entityClass) {
        super(entityClass);
    }

    protected void enhanceRetrieved(E entity) {
    }

    protected void persist(E entity) {
        EntityServicesImpl.secureSave(entity);
    }

    @Override
    public void retrieve(AsyncCallback<E> callback, Key entityId) {
        E entity = EntityServicesImpl.secureRetrieve(entityClass, entityId);
        enhanceRetrieved(entity);
        callback.onSuccess(entity);
    }

    @Override
    public void create(AsyncCallback<E> callback, E entity) {
        persist(entity);
        enhanceRetrieved(entity);
        callback.onSuccess(entity);
    }

    @Override
    public void save(AsyncCallback<E> callback, E entity) {
        persist(entity);
        enhanceRetrieved(entity);
        callback.onSuccess(entity);
    }

}

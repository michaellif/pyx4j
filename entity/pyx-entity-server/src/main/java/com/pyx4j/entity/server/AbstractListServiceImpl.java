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
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.lister.EntityLister;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.security.shared.SecurityController;

public abstract class AbstractListServiceImpl<E extends IEntity> implements AbstractListService<E> {

    protected Class<E> entityClass;

    protected AbstractListServiceImpl(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    protected void enhanceListRetrieved(E entity) {
    }

    protected void delete(E actualEntity) {
        Persistence.service().delete(actualEntity);
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        SecurityController.assertPermission(new EntityPermission(entityClass, EntityPermission.DELETE));
        E actualEntity = Persistence.service().retrieve(entityClass, entityId);
        SecurityController.assertPermission(EntityPermission.permissionDelete(actualEntity));
        delete(actualEntity);
        callback.onSuccess(true);
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<E>> callback, EntityListCriteria<E> criteria) {
        if (!criteria.getEntityClass().equals(entityClass)) {
            throw new Error("Service " + this.getClass().getName() + " declaration error. " + entityClass + "!=" + criteria.getEntityClass());
        }
        EntitySearchResult<E> result = EntityLister.secureQuery(criteria);
        for (E entity : result.getData()) {
            enhanceListRetrieved(entity);
        }
        callback.onSuccess(result);
    }
}

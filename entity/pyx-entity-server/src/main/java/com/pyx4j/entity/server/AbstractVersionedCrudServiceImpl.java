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
 * Created on 2012-03-05
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractVersionedCrudService;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.rpc.shared.VoidSerializable;

public abstract class AbstractVersionedCrudServiceImpl<E extends IVersionedEntity<?>> extends AbstractCrudServiceImpl<E> implements
        AbstractVersionedCrudService<E> {

    public AbstractVersionedCrudServiceImpl(Class<E> entityClass) {
        super(entityClass);
    }

    @Override
    public void retrieve(AsyncCallback<E> callback, Key entityId, RetrieveTraget retrieveTraget) {
        Key primaryKey;
        // Force draft for edit
        if (retrieveTraget == RetrieveTraget.Edit) {
            primaryKey = entityId.asDraftKey();
        } else {
            primaryKey = entityId;
        }
        E entity = Persistence.secureRetrieve(entityClass, primaryKey);

        enhanceRetrieved(entity);

        // If draft do not exists, return data from current version
        if (primaryKey.getVersion() == Key.VERSION_DRAFT && entity.version().isNull()) {
            E entityCurrent = Persistence.secureRetrieve(entityClass, primaryKey.asCurrentKey());
            enhanceRetrieved(entityCurrent);
            entity.version().set(EntityGraph.businessDuplicate(entityCurrent.version()));
        }

        callback.onSuccess(entity);
    }

    @Override
    public void approveFinal(AsyncCallback<VoidSerializable> callback, Key entityId) {
        E entity = Persistence.secureRetrieve(entityClass, entityId.asDraftKey());
        entity.saveAction().setValue(SaveAction.saveAsFinal);
        persist(entity);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

}

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

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractVersionedCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;

public abstract class AbstractVersionedCrudServiceImpl<E extends IVersionedEntity<?>> extends AbstractCrudServiceImpl<E> implements
        AbstractVersionedCrudService<E> {

    private static final I18n i18n = I18n.get(AbstractVersionedCrudServiceImpl.class);

    public AbstractVersionedCrudServiceImpl(Class<E> entityClass) {
        super(entityClass);
    }

    //TODO move Key handling to framework!
    @Override
    public void retrieve(AsyncCallback<E> callback, Key entityId, RetrieveTraget retrieveTraget) {
        E entity = EntityFactory.create(entityClass);
        entity.setPrimaryKey(new Key(entityId.asLong()));
        if ((entityId.getVersion() == 0) || retrieveTraget == RetrieveTraget.Edit) {
            entity.draft().setValue(Boolean.TRUE);
        } else {
            entity.forDate().setValue(new Date(entityId.getVersion()));
        }
        //TODO move to framework!

        Persistence.service().retrieve(entity);

        enhanceRetrieved(entity);
        callback.onSuccess(entity);
    }

    @Override
    public void finalize(AsyncCallback<VoidSerializable> callback, Key entityId) {
        E entity = EntityFactory.create(entityClass);
        entity.setPrimaryKey(new Key(entityId.asLong()));
        entity.draft().setValue(Boolean.TRUE);
        Persistence.service().retrieve(entity);

        entity.draft().setValue(Boolean.FALSE);
        persist(entity);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

}

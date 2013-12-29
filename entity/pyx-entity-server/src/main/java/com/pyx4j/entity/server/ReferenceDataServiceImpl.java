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
 * Created on Sep 21, 2013
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.ReferenceDataService;

public class ReferenceDataServiceImpl implements ReferenceDataService {

    protected <T extends IEntity> EntitySearchResult<T> query(EntityQueryCriteria<T> criteria) {
        EntitySearchResult<T> result = new EntitySearchResult<T>();
        for (T entity : Persistence.secureQuery(criteria, AttachLevel.ToStringMembers)) {
            entity.setAttachLevel(AttachLevel.ToStringMembers);
            result.getData().add(entity);
        }
        return result;
    }

    @Override
    public final void queryNonBlocking(AsyncCallback<EntitySearchResult<? extends IEntity>> callback, EntityQueryCriteria<? extends IEntity> criteria) {
        callback.onSuccess(query(criteria));
    }

    @Override
    public final void query(AsyncCallback<EntitySearchResult<? extends IEntity>> callback, EntityQueryCriteria<? extends IEntity> criteria) {
        callback.onSuccess(query(criteria));
    }

}

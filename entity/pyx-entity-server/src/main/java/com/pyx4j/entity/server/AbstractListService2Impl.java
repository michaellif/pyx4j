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
 * Created on Apr 13, 2015
 * @author vlads
 */
package com.pyx4j.entity.server;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.lister.IQueryCriteria;
import com.pyx4j.entity.rpc.AbstractListService2;
import com.pyx4j.entity.rpc.EntitySearchResult;

public abstract class AbstractListService2Impl<BO extends IEntity, TO extends IEntity> implements AbstractListService2<TO> {

    @Override
    public void list(AsyncCallback<EntitySearchResult<TO>> callback, IQueryCriteria<TO> criteria) {
        // TODO Auto-generated method stub

    }
}

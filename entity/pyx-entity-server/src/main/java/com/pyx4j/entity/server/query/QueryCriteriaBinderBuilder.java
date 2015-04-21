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
 * Created on Apr 21, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.query;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.lister.IQueryCriteria;
import com.pyx4j.entity.core.criterion.lister.QueryCriteriaBinder;

public class QueryCriteriaBinderBuilder<E extends IEntity, C extends IQueryCriteria<E>> {

    protected final Class<C> criteriaClass;

    protected final C criteriaProto;

    protected final E proto;

    public QueryCriteriaBinderBuilder(Class<C> criteriaClass) {
        this.criteriaClass = criteriaClass;
        criteriaProto = EntityFactory.getEntityPrototype(criteriaClass);
        proto = criteriaProto.proto();
    }

    public static <E extends IEntity, C extends IQueryCriteria<E>> QueryCriteriaBinderBuilder<E, C> create(Class<E> boClass, Class<C> criteriaClass) {
        return null;
    }

    public QueryCriteriaBinder<E, C> build() {
        return null;
    }
}

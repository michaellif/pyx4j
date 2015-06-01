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
 * Created on Jun 1, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.query;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.query.IQuery;
import com.pyx4j.entity.core.query.QueryBinder;

class QueryBinderRegistry {

    private Map<Class<?>, QueryBinder<?, ?>> registry = new HashMap<>();

    private static class SingletonHolder {
        public static final QueryBinderRegistry INSTANCE = new QueryBinderRegistry();
    }

    static QueryBinderRegistry instance() {
        return SingletonHolder.INSTANCE;
    }

    <E extends IEntity, Q extends IQuery<E>> void registerBinder(Class<Q> queryClass, QueryBinder<E, Q> binder) {
        registry.put(queryClass, binder);
    }

    @SuppressWarnings("unchecked")
    <E extends IEntity, Q extends IQuery<E>> QueryBinder<E, Q> getBinder(Class<Q> queryClass) {
        QueryBinder<?, ?> binder = registry.get(queryClass);
        if (binder == null) {
            throw new Error("No registered QueryBinder for IQuery class " + queryClass.getName());
        }
        return (QueryBinder<E, Q>) binder;
    }

    @SuppressWarnings("unchecked")
    <E extends IEntity, Q extends IQuery<E>> QueryBinder<E, Q> getBinder(Q query) {
        return getBinder((Class<Q>) query.getInstanceValueClass());
    }
}

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
 * Created on May 28, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.query;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.query.AbstractQueryColumnStorage;
import com.pyx4j.entity.core.query.ICondition;
import com.pyx4j.entity.core.query.IQuery;
import com.pyx4j.entity.core.query.QueryBinder;
import com.pyx4j.entity.core.query.QueryStorage;

/**
 * see PersistableQueryTestCase for usage example
 *
 */
public interface PersistableQueryFacade {

    public <E extends IEntity, Q extends IQuery<E>> EntityQueryCriteria<E> convertToCriteria(Q query, QueryBinder<E, Q> binder);

    /**
     * Use already registered QueryBinder
     *
     * @param query
     * @return
     */
    public <E extends IEntity, Q extends IQuery<E>> EntityQueryCriteria<E> convertToCriteria(Q query);

    /**
     * @param query
     *            PersistableQuery to save
     * @param queryStorage
     *            persists and update this object so app can save pointer.
     */
    public <Q extends IQuery<? extends IEntity>> void persistQuery(Q query, QueryStorage queryStorage);

    public <Q extends IQuery<? extends IEntity>> Q retriveQuery(Class<Q> queryClass, QueryStorage queryStorageId);

    public void registerColumnStorageClass(Class<? extends AbstractQueryColumnStorage> persistableEntityClass);

    public void preloadColumnStorage();

    public <C extends ICondition> void registerCondition(Class<C> conditionClass, ConditionTranslation<C> conditionTranslation);

    public <E extends IEntity, Q extends IQuery<E>> void registerBinder(Class<Q> queryClass, QueryBinder<E, Q> binder);
}

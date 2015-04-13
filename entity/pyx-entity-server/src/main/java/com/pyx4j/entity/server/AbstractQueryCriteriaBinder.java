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

import java.io.Serializable;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.criterion.lister.IQueryCriteria;
import com.pyx4j.entity.core.criterion.lister.QueryCriteriaBinder;

public abstract class AbstractQueryCriteriaBinder<BO extends IEntity, C extends IQueryCriteria<BO>> implements QueryCriteriaBinder<BO, C> {

    protected Class<BO> boClass;

    protected Class<C> criteriaClass;

    protected final BO boProto;

    protected final C criteriaProto;

    protected AbstractQueryCriteriaBinder(Class<BO> boClass, Class<C> criteriaClass) {
        this.boClass = boClass;
        this.criteriaClass = criteriaClass;

        boProto = EntityFactory.getEntityPrototype(boClass);
        criteriaProto = EntityFactory.getEntityPrototype(criteriaClass);
    }

    protected final <TYPE extends Serializable, TCO extends IEntity> void bind(IPrimitive<TYPE> boMember, TCO criteriaMember) {
        //TODO
    }
}

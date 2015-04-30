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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.query.IQueryFilterList;
import com.pyx4j.entity.core.query.QueryCriteriaBinder;

public class QueryCriteriaBinderBuilder<E extends IEntity, C extends IQueryFilterList<E>> {

    protected final Class<C> criteriaClass;

    protected final C criteriaProto;

    protected final E proto;

    private final Map<Path, Path> pathBinding = new HashMap<>();

    @SuppressWarnings("unchecked")
    public QueryCriteriaBinderBuilder(Class<C> criteriaClass) {
        this.criteriaClass = criteriaClass;
        criteriaProto = EntityFactory.getEntityPrototype(criteriaClass);
        proto = EntityFactory.getEntityPrototype((Class<E>) criteriaProto.proto().getValueClass());
    }

    public final C criteriaProto() {
        return criteriaProto;
    }

    public final E proto() {
        return proto;
    }

    public final <TYPE extends Serializable, TCO extends IEntity> void map(IPrimitive<TYPE> boMember, TCO criteriaMember) {
        assert criteriaMember.getPath().getRootEntityClass() == criteriaClass;
        assert boMember.getPath().getRootEntityClass() == proto().getValueClass() : "BO member expected; got path from " + boMember.getPath();
        pathBinding.put(criteriaMember.getPath(), boMember.getPath());
    }

    public QueryCriteriaBinder<E, C> build() {
        return new DefaultQueryCriteriaBinder<>(criteriaClass, pathBinding);
    }
}

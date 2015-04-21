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
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.lister.IQueryCriteria;
import com.pyx4j.entity.core.criterion.lister.QueryCriteriaBinder;

public class AbstractQueryCriteriaBinder<E extends IEntity, C extends IQueryCriteria<E>> implements QueryCriteriaBinder<E, C> {

    protected Class<E> boClass;

    protected Class<C> criteriaClass;

    protected final E boProto;

    protected final C criteriaProto;

    private final Map<Path, Path> pathBinding = new HashMap<>();

    protected AbstractQueryCriteriaBinder(Class<E> boClass, Class<C> criteriaClass) {
        this.boClass = boClass;
        this.criteriaClass = criteriaClass;

        boProto = EntityFactory.getEntityPrototype(boClass);
        criteriaProto = EntityFactory.getEntityPrototype(criteriaClass);
    }

    protected final <TYPE extends Serializable, TCO extends IEntity> void map(IPrimitive<TYPE> boMember, TCO criteriaMember) {
        pathBinding.put(criteriaMember.getPath(), boMember.getPath());
    }

    @Override
    public Path toEntityPath(Path criteriaPath) {
        assert criteriaPath.getRootEntityClass() == criteriaClass;
        return pathBinding.get(criteriaPath);
    }
}

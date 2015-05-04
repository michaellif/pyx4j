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
 * Created on Apr 27, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.filter;

import java.util.Map;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.filter.IQueryFilterList;
import com.pyx4j.entity.core.filter.QueryFilterBinder;

public class DefaultQueryCriteriaBinder<E extends IEntity, C extends IQueryFilterList<E>> implements QueryFilterBinder<E, C> {

    private final Map<Path, Path> pathBinding;

    private final Class<C> criteriaClass;

    DefaultQueryCriteriaBinder(Class<C> criteriaClass, Map<Path, Path> pathBinding) {
        this.criteriaClass = criteriaClass;
        this.pathBinding = pathBinding;
    }

    @Override
    public Path toEntityPath(Path criteriaPath) {
        assert criteriaPath.getRootEntityClass() == criteriaClass;
        return pathBinding.get(criteriaPath);
    }

}

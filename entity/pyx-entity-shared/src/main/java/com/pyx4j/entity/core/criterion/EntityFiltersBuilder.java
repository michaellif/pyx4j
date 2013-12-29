/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-10-18
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.core.criterion;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;

public class EntityFiltersBuilder<E extends IEntity> extends AndCriterion {

    private static final long serialVersionUID = 1L;

    private transient E entityPrototype;

    protected EntityFiltersBuilder() {

    }

    public EntityFiltersBuilder(Class<E> entityClass) {
        this.entityPrototype = EntityFactory.getEntityPrototype(entityClass);
    }

    public static <T extends IEntity> EntityFiltersBuilder<T> create(Class<T> entityClass) {
        return new EntityFiltersBuilder<T>(entityClass);
    }

    public E proto() {
        return entityPrototype;
    }
}

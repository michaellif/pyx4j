/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Oct 17, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import com.pyx4j.entity.shared.IEntity;

public class EntityCollectionRequest<E extends IEntity> {

    private final Class<E> entityClass;

    private final Iterable<com.pyx4j.commons.Key> primaryKeys;

    public EntityCollectionRequest(Class<E> entityClass, Iterable<com.pyx4j.commons.Key> primaryKeys) {
        this.entityClass = entityClass;
        this.primaryKeys = primaryKeys;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public Iterable<com.pyx4j.commons.Key> getPrimaryKeys() {
        return primaryKeys;
    }

}

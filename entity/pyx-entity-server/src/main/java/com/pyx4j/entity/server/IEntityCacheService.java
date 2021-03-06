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
 * Created on 2010-10-13
 * @author vlads
 */
package com.pyx4j.entity.server;

import java.util.Map;

import com.pyx4j.entity.core.IEntity;

public interface IEntityCacheService {

    public boolean isDisabled();

    public void setDisabled(boolean disabled);

    public <T extends IEntity> T get(Class<T> entityClass, com.pyx4j.commons.Key primaryKey);

    public <T extends IEntity> Map<com.pyx4j.commons.Key, T> get(Class<T> entityClass, Iterable<com.pyx4j.commons.Key> primaryKeys);

    public Map<EntityCollectionRequest<IEntity>, Map<com.pyx4j.commons.Key, IEntity>> get(Iterable<EntityCollectionRequest<IEntity>> requests);

    public <T extends IEntity> void put(T entity);

    /**
     * Does not assume that all entities are of the same type
     */
    public void put(Iterable<IEntity> entityList);

    public <T extends IEntity> void remove(T entity);

    public <T extends IEntity> void remove(Class<T> entityClass, com.pyx4j.commons.Key primaryKey);

    public <T extends IEntity> void remove(Class<T> entityClass, Iterable<com.pyx4j.commons.Key> primaryKeys);
}

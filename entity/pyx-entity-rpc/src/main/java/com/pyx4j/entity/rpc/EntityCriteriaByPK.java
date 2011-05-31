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
 * Created on Feb 23, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rpc;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

/**
 * Special case criteria for EntityServices.RetrieveByPK service.
 */
public class EntityCriteriaByPK<E extends IEntity> extends EntityQueryCriteria<E> {

    private static final long serialVersionUID = -3985273227849938461L;

    private Key primaryKey;

    protected EntityCriteriaByPK() {

    }

    public EntityCriteriaByPK(Class<E> entityClass) {
        super(entityClass);
    }

    public static <T extends IEntity> EntityCriteriaByPK<T> create(Class<T> entityClass) {
        return new EntityCriteriaByPK<T>(entityClass);
    }

    public static <T extends IEntity> EntityCriteriaByPK<T> create(Class<T> entityClass, Key primaryKey) {
        EntityCriteriaByPK<T> c = new EntityCriteriaByPK<T>(entityClass);
        c.setPrimaryKey(primaryKey);
        return c;
    }

    public static <T extends IEntity> EntityCriteriaByPK<T> create(Class<T> entityClass, T entity) {
        EntityCriteriaByPK<T> c = new EntityCriteriaByPK<T>(entityClass);
        if ((entity != null) && (entity.getPrimaryKey() != null)) {
            c.setPrimaryKey(entity.getPrimaryKey());
        }
        return c;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEntity> EntityCriteriaByPK<T> create(T entity) {
        EntityCriteriaByPK<T> c = new EntityCriteriaByPK<T>((Class<T>) entity.getValueClass());
        c.setPrimaryKey(entity.getPrimaryKey());
        return c;
    }

    public Key getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Key primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public EntityQueryCriteria<E> add(Criterion criterion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityQueryCriteria<E> sort(EntityQueryCriteria.Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        if (!(o instanceof EntityCriteriaByPK<?>)) {
            return false;
        } else {
            return primaryKey == ((EntityCriteriaByPK<?>) o).primaryKey;
        }
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode *= 0x1F;
        hashCode += primaryKey.hashCode();
        return hashCode;
    }
}

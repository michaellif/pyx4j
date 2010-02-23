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

import java.io.Serializable;

import com.pyx4j.entity.shared.IEntity;

/**
 * Special case criteria for RetrieveByPK service.
 */
@SuppressWarnings("serial")
public class EntityCriteriaByPK<E extends IEntity> implements Serializable {

    private String domainName;

    private long primaryKey;

    protected EntityCriteriaByPK() {

    }

    public EntityCriteriaByPK(Class<E> entityClass) {
        this.domainName = entityClass.getName();
    }

    public static <T extends IEntity> EntityCriteriaByPK<T> create(Class<T> entityClass) {
        return new EntityCriteriaByPK<T>(entityClass);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEntity> EntityCriteriaByPK<T> create(T entity) {
        EntityCriteriaByPK<T> c = new EntityCriteriaByPK<T>((Class<T>) entity.getValueClass());
        c.setPrimaryKey(entity.getPrimaryKey());
        return c;
    }

    public String getDomainName() {
        return domainName;
    }

    public long getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(long primaryKey) {
        this.primaryKey = primaryKey;
    }
}

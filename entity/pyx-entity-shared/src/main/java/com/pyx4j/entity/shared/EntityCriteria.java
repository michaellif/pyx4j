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
 * Created on Jan 7, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.shared.criterion.Criterion;

/**
 * Representation of a query criterion.
 * 
 * Translates to org.hibernate.Criteria in RDBMS or Query in GAE
 */
@SuppressWarnings("serial")
public class EntityCriteria<E extends IEntity> implements Serializable {

    private String domainName;

    private List<Criterion> filters;

    private List<Sort> sorts;

    private transient Class<E> entityClass;

    private transient E metaEntity;

    public static class Sort implements Serializable {

        private String propertyName;

        private boolean descending;

        public Sort() {

        }

        public Sort(String propertyName, boolean descending) {
            this.propertyName = propertyName;
            this.descending = descending;
        }

        public String getPropertyName() {
            return this.propertyName;
        }

        public boolean isDescending() {
            return this.descending;
        }

    }

    protected EntityCriteria() {

    }

    public EntityCriteria(Class<E> entityClass) {
        this.entityClass = entityClass;
        this.domainName = entityClass.getName();
    }

    public static <T extends IEntity> EntityCriteria<T> create(Class<T> entityClass) {
        return new EntityCriteria<T>(entityClass);
    }

    public E meta() {
        if (metaEntity == null) {
            metaEntity = EntityFactory.create(entityClass);
        }
        return metaEntity;
    }

    public EntityCriteria<E> add(Criterion criterion) {
        if (filters == null) {
            filters = new Vector<Criterion>();
        }
        filters.add(criterion);
        return this;
    }

    public EntityCriteria<E> asc(String propertyName) {
        return sort(new Sort(propertyName, false));
    }

    public EntityCriteria<E> desc(String propertyName) {
        return sort(new Sort(propertyName, true));
    }

    public EntityCriteria<E> sort(Sort sort) {
        if (sorts == null) {
            sorts = new Vector<Sort>();
        }
        sorts.add(sort);
        return this;
    }

    public String getDomainName() {
        return domainName;
    }

    public List<Criterion> getFilters() {
        return filters;
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof EntityCriteria)) {
            return false;
        }
        return equals((EntityCriteria<E>) o);
    }

    public boolean equals(EntityCriteria<E> t) {
        if (t == this) {
            return true;
        }
        if ((domainName == null) || !domainName.equals(t.getDomainName())) {
            return false;
        }
        if (!EqualsHelper.equals(filters, t.filters)) {
            return false;
        }
        if (!EqualsHelper.equals(sorts, t.sorts)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (domainName != null) {
            hashCode += domainName.hashCode();
        }
        hashCode *= 0x1F;
        if (filters != null) {
            hashCode += filters.hashCode();
        }
        hashCode *= 0x1F;
        if (sorts != null) {
            hashCode += sorts.hashCode();
        }
        return hashCode;
    }
}

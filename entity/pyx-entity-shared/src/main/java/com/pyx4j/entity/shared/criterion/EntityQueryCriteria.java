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
package com.pyx4j.entity.shared.criterion;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.IHaveServiceCallMarker;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

/**
 * Representation of a query criterion.
 * 
 * Translates to org.hibernate.Criteria in RDBMS or Query in GAE
 */
public class EntityQueryCriteria<E extends IEntity> implements Serializable, IHaveServiceCallMarker {

    private static final long serialVersionUID = -6101566214650608853L;

    private Vector<Criterion> filters;

    private Vector<Sort> sorts;

    private E entityPrototype;

    public static class Sort implements Serializable {

        private static final long serialVersionUID = -9007568149350718889L;

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

        @Override
        public String toString() {
            return getPropertyName() + (isDescending() ? " Desc" : " Asc");
        }

    }

    protected EntityQueryCriteria() {

    }

    public EntityQueryCriteria(Class<E> entityClass) {
        this.entityPrototype = EntityFactory.getEntityPrototype(entityClass);
    }

    public static <T extends IEntity> EntityQueryCriteria<T> create(Class<T> entityClass) {
        return new EntityQueryCriteria<T>(entityClass);
    }

    public E proto() {
        return entityPrototype;
    }

    @SuppressWarnings("unchecked")
    public Class<E> getEntityClass() {
        return (Class<E>) entityPrototype.getObjectClass();
    }

    public EntityQueryCriteria<E> add(Criterion criterion) {
        if (filters == null) {
            filters = new Vector<Criterion>();
        }
        filters.add(criterion);
        return this;
    }

    public void resetCriteria() {
        if (filters != null) {
            filters.clear();
        }
    }

    public EntityQueryCriteria<E> asc(String propertyName) {
        return sort(new Sort(propertyName, false));
    }

    public EntityQueryCriteria<E> desc(String propertyName) {
        return sort(new Sort(propertyName, true));
    }

    public EntityQueryCriteria<E> sort(Sort sort) {
        if (sorts == null) {
            sorts = new Vector<Sort>();
        }
        sorts.add(sort);
        return this;
    }

    public boolean hasCriteria() {
        return ((filters != null) && (filters.size() > 0)) || ((sorts != null) && (sorts.size() > 0));
    }

    public List<Criterion> getFilters() {
        return filters;
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(List<Sort> sorts) {
        if (sorts == null) {
            this.sorts = null;
            return;
        } else {
            if (this.sorts == null) {
                this.sorts = new Vector<Sort>();
            } else {
                this.sorts.clear();
            }
            this.sorts.addAll(sorts);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof EntityQueryCriteria)) {
            return false;
        }
        return equals((EntityQueryCriteria<E>) o);
    }

    public boolean equals(EntityQueryCriteria<E> t) {
        if (t == this) {
            return true;
        }
        if (this.entityPrototype.getEntityMeta() != t.entityPrototype.getEntityMeta()) {
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
        hashCode += this.entityPrototype.getEntityMeta().hashCode();
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

    // N.B. Do not use @Override for GWT to compile
    //@Override
    @Override
    public Object clone() {
        EntityQueryCriteria<E> c = create(getEntityClass());
        if (this.getSorts() != null) {
            for (Sort s : this.getSorts()) {
                c.sort(s);
            }
        }
        if (this.getFilters() != null) {
            for (Criterion f : this.getFilters()) {
                c.add(f);
            }
        }
        return c;
    }

    @Override
    public String getServiceCallMarker() {
        return this.entityPrototype.getEntityMeta().getCaption().replace(' ', '_');
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("domainName=").append(this.entityPrototype.getEntityMeta().getCaption());
        builder.append(" filters=").append(getFilters());
        builder.append(" sorts=").append(getSorts());
        return builder.toString();
    }
}

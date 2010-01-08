/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 7, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.shared.criterion.Criterion;

/**
 * Representation of a query criterion.
 * 
 * Translates to org.hibernate.Criteria in RDBMS or Query in GAE
 */
@SuppressWarnings("serial")
public class EntityCriteria<E extends IEntity<?>> implements Serializable {

    private String domainName;

    private List<Criterion> filters;

    private List<Sort> sorts;

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
        domainName = entityClass.getName();
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
}

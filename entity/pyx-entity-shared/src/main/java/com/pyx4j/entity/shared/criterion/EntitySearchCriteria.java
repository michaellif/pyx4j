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
 * Created on Feb 24, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.criterion;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.pyx4j.commons.IHaveServiceCallMarker;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

public class EntitySearchCriteria<E extends IEntity> implements Serializable, IHaveServiceCallMarker {

    private static final long serialVersionUID = 7483364285263499506L;

    private E entityPrototype;

    private int pageNumber;

    private int pageSize = -1;

    private String encodedCursorReference;

    private TreeMap<PathSearch, Serializable> filters = new TreeMap<PathSearch, Serializable>();

    private Vector<Sort> sorts;

    protected EntitySearchCriteria() {

    }

    public EntitySearchCriteria(Class<E> entityClass) {
        this.entityPrototype = EntityFactory.getEntityPrototype(entityClass);
    }

    public static <T extends IEntity> EntitySearchCriteria<T> create(Class<T> entityClass) {
        return new EntitySearchCriteria<T>(entityClass);
    }

    public E proto() {
        return entityPrototype;
    }

    @SuppressWarnings("unchecked")
    public Class<E> getEntityClass() {
        return (Class<E>) entityPrototype.getObjectClass();
    }

    public Map<PathSearch, Serializable> getFilters() {
        return filters;
    }

    protected void setFilters(TreeMap<PathSearch, Serializable> filters) {
        this.filters = filters;
    }

    public void setValue(PathSearch path, Object value) {
        if ((value instanceof Serializable) || (value == null)) {
            filters.put(path, (Serializable) value);
        } else {
            throw new IllegalArgumentException("Serializable expected fopr path " + path.toString());
        }
    }

    public void removeValue(PathSearch path) {
        filters.remove(path);
    }

    public Object getValue(PathSearch path) {
        return filters.get(path);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public EntitySearchCriteria<E> asc(IObject<?> member) {
        return asc(member.getFieldName());
    }

    public EntitySearchCriteria<E> asc(String propertyName) {
        return sort(new Sort(propertyName, false));
    }

    public EntitySearchCriteria<E> desc(String propertyName) {
        return sort(new Sort(propertyName, true));
    }

    public EntitySearchCriteria<E> sort(Sort sort) {
        if (sorts == null) {
            sorts = new Vector<Sort>();
        }
        sorts.add(sort);
        return this;
    }

    public Vector<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(Vector<Sort> sorts) {
        this.sorts = sorts;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("domainName=").append(this.entityPrototype.getEntityMeta().getCaption());
        builder.append(" pageSize=").append(getPageSize());
        builder.append(" pageNumber=").append(getPageNumber());
        builder.append(" filters=").append(getFilters());
        return builder.toString();
    }

    @Override
    public String getServiceCallMarker() {
        return this.entityPrototype.getEntityMeta().getCaption().replace(' ', '_');
    }

    public String getEncodedCursorReference() {
        return encodedCursorReference;
    }

    public void setEncodedCursorReference(String encodedCursorReference) {
        this.encodedCursorReference = encodedCursorReference;
    }
}

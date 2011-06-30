/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-06-30
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.criterion;

import com.pyx4j.entity.shared.IEntity;

public class EntityListCriteria<E extends IEntity> extends EntityQueryCriteria<E> {

    private static final long serialVersionUID = 3082240473531435889L;

    private int pageNumber;

    private int pageSize = -1;

    protected EntityListCriteria() {

    }

    public EntityListCriteria(Class<E> entityClass) {
        super(entityClass);
    }

    public static <T extends IEntity> EntityListCriteria<T> create(Class<T> entityClass) {
        return new EntityListCriteria<T>(entityClass);
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

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof EntityListCriteria)) {
            return false;
        } else {
            return equals((EntityListCriteria<E>) o);
        }
    }

    public boolean equals(EntityListCriteria<E> t) {
        if (t == this) {
            return true;
        } else if ((pageSize != t.getPageSize()) || (pageNumber != t.getPageNumber())) {
            return false;
        } else {
            return super.equals(t);
        }
    }

    @Override
    public int hashCode() {
        int hashCode = pageSize;
        hashCode *= 0x1F;
        hashCode += pageNumber;
        hashCode *= 0x1F;
        return hashCode + super.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append(" pageSize=").append(getPageSize());
        builder.append(" pageNumber=").append(getPageNumber());
        return builder.toString();
    }
}

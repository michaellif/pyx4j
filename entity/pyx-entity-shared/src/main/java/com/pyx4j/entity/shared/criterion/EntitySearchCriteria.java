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
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.shared.IEntity;

public class EntitySearchCriteria<E extends IEntity> implements Serializable {

    private static final long serialVersionUID = 7483364285263499506L;

    private String domainName;

    private final Map<PathSearch, Serializable> filters = new HashMap<PathSearch, Serializable>();

    protected EntitySearchCriteria() {

    }

    public EntitySearchCriteria(Class<E> entityClass) {
        this.domainName = entityClass.getName();
    }

    public static <T extends IEntity> EntitySearchCriteria<T> create(Class<T> entityClass) {
        return new EntitySearchCriteria<T>(entityClass);
    }

    public String getDomainName() {
        return domainName;
    }

    public Map<PathSearch, Serializable> getFilters() {
        return filters;
    }

    public void setValue(PathSearch path, Object value) {
        if (value instanceof Serializable) {
            filters.put(path, (Serializable) value);
        } else {
            throw new IllegalArgumentException("Serializable expected fopr path " + path.toString());
        }
    }

    public Object getValue(PathSearch path) {
        return filters.get(path);
    }

}

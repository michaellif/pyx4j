/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Aug 8, 2015
 * @author vlads
 */
package com.pyx4j.entity.core;

import java.io.Serializable;

import com.pyx4j.commons.GWTSerializable;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.EntityGraph.EntityGraphEqualOptions;

public class EntityValueKey<E extends IEntity> implements Serializable {

    private static final long serialVersionUID = 1L;

    private E entity;

    @GWTSerializable
    protected EntityValueKey() {

    }

    public EntityValueKey(E entity) {
        this.entity = entity.detach();
    }

    public E getEntity() {
        return entity;
    }

    @Override
    public int hashCode() {
        return entity.valueHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        } else {
            EntityValueKey<?> other = (EntityValueKey<?>) obj;
            EntityGraphEqualOptions options = new EntityGraphEqualOptions(false);
            options.ignoreTransient = false;
            options.ignoreRpcTransient = true;
            return EntityGraph.fullyEqual(entity, other.entity, options);
        }
    }

    @Override
    public String toString() {
        return entity.toString();
    }
}

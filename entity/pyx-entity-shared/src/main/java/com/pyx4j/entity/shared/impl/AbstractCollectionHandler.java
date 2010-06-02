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
 * Created on Feb 20, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;

public abstract class AbstractCollectionHandler<TYPE extends IEntity, VALUE_TYPE> extends ObjectHandler<VALUE_TYPE> implements ICollection<TYPE, VALUE_TYPE> {

    private static final long serialVersionUID = 5007742923851829758L;

    private final Class<TYPE> valueClass;

    @SuppressWarnings("unchecked")
    protected AbstractCollectionHandler(Class<? extends IObject> clazz, Class<TYPE> valueClass, IEntity parent, String fieldName) {
        super(clazz, parent, fieldName);
        this.valueClass = valueClass;
    }

    @Override
    public Class<TYPE> getValueClass() {
        return valueClass;
    }

    @Override
    public TYPE $() {
        return EntityFactory.create(getValueClass(), this, getFieldName());
    }

    //TODO move common function from  ISet or IList to this class 

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getObjectClass().getName()).append(" ");
        VALUE_TYPE value = getValue();
        if (value != null) {
            Set<Map> processed = new HashSet<Map>();
            b.append('[');
            for (Object o : (Collection<?>) value) {
                if (o instanceof Map<?, ?>) {
                    EntityValueMap.dumpMap(b, (Map<String, Object>) o, processed);
                } else {
                    b.append(o);
                }
            }
            b.append(']');
        } else {
            b.append("{null}");
        }
        return b.toString();
    }
}

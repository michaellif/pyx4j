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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.commons.ConverterUtils.ToStringConverter;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.MemberMeta;

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

    @SuppressWarnings("unchecked")
    protected TYPE createTypedEntity(Map<String, Object> entityValue) {
        TYPE entity;
        TYPE typeAttr = (TYPE) entityValue.get(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR);
        if (typeAttr == null) {
            entity = $();
        } else {
            entity = EntityFactory.create((Class<TYPE>) typeAttr.getValueClass(), this, getFieldName());
        }
        entity.setValue(entityValue);
        return entity;
    }

    protected Map<String, Object> ensureTypedValue(TYPE entity) {
        if (!entity.isInstanceOf(getValueClass())) {
            throw new ClassCastException("Collection member type expected " + getValueClass());
        }
        Map<String, Object> value = ((SharedEntityHandler) entity).ensureValue();
        if (!this.getValueClass().equals(entity.getObjectClass())) {
            value.put(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR, EntityFactory.getEntityPrototype((Class<IEntity>) entity.getObjectClass()));
        }
        ((SharedEntityHandler) entity).attachToOwner(this, this.getFieldName());

        // ensure @Owner value is set properly.
        String ownerMemberName = entity.getEntityMeta().getOwnerMemberName();
        if ((ownerMemberName != null) && (value != null) && (getMeta().isOwnedRelationships())) {
            value.put(ownerMemberName, getOwner().getValue());
        }

        return value;
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[this.size()];
        int i = 0;
        for (TYPE el : this) {
            array[i] = el;
            i++;
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size) {
            a = new ArrayList<T>(size).toArray(a);
        }
        int i = 0;
        for (TYPE el : this) {
            a[i] = (T) el;
            i++;
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    //TODO move common function from  ISet or IList to this class 

    private static class StringConverter implements ToStringConverter<IEntity> {

        @Override
        public String toString(IEntity value) {
            return value.getStringView();
        }

    }

    @Override
    public String getStringView() {
        MemberMeta mm = getMeta();
        VALUE_TYPE thisValue = getValue();
        if (thisValue == null) {
            return mm.getNullString();
        } else {
            return ConverterUtils.convertCollection((Collection<IEntity>) thisValue, new StringConverter());
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getObjectClass().getName()).append(" ");
        VALUE_TYPE value = getValue();
        if (value != null) {
            Set<Map<String, Object>> processed = new HashSet<Map<String, Object>>();
            b.append('[');
            b.append(((Collection<?>) value).size());
            b.append(' ');
            boolean first = true;
            for (Object o : (Collection<?>) value) {
                if (first) {
                    first = false;
                } else {
                    b.append(", ");
                }
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

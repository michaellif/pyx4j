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
package com.pyx4j.entity.core.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.commons.ConverterUtils.ToStringConverter;
import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.IdentityHashSet;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.meta.MemberMeta;

public abstract class AbstractCollectionHandler<TYPE extends IEntity, VALUE_TYPE> extends ObjectHandler<VALUE_TYPE> implements ICollection<TYPE, VALUE_TYPE> {

    private static final long serialVersionUID = 5007742923851829758L;

    private final Class<TYPE> valueClass;

    protected AbstractCollectionHandler(@SuppressWarnings("rawtypes") Class<? extends IObject> clazz, Class<TYPE> valueClass, IEntity parent, String fieldName) {
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

    protected Object getDetachedValue() {
        Map<String, Serializable> data = getOwner().getValue();
        if (data == null) {
            return null;
        } else {
            return data.get(getFieldName());
        }
    }

    @Override
    public int size() {
        Object value = getDetachedValue();
        if (value instanceof Integer) {
            // AttachLevel.CollectionSizeOnly
            return (Integer) value;
        } else if (value == null) {
            return 0;
        } else {
            assert (value != this) : "ICollection structure error in " + exceptionInfo();
            return ((Collection<?>) getValue()).size();
        }
    }

    @Override
    public AttachLevel getAttachLevel() {
        if (getOwner().isValueDetached()) {
            return AttachLevel.Detached;
        }
        Object value = getDetachedValue();
        if (value instanceof Integer) {
            return AttachLevel.CollectionSizeOnly;
        } else if (value == AttachLevel.Detached) {
            return AttachLevel.Detached;
        } else {
            return AttachLevel.Attached;
        }
    }

    @Override
    public void setCollectionSizeOnly(int size) {
        getOwner().setMemberValue(getFieldName(), Integer.valueOf(size));
    }

    @Override
    public void setAttachLevel(AttachLevel level) {
        switch (level) {
        case Attached:
            Map<String, Serializable> data = getOwner().getValue();
            if ((data != null) && (data.get(getFieldName()) == AttachLevel.Detached)) {
                data.put(getFieldName(), null);
            }
            break;
        case Detached:
            getOwner().setMemberValue(getFieldName(), AttachLevel.Detached);
            break;
        case CollectionSizeOnly:
            throw new IllegalArgumentException("Use setCollectionSizeOnly");
        default:
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("unchecked")
    protected TYPE createTypedEntity(Map<String, Serializable> entityValue) {
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

    protected Map<String, Serializable> ensureTypedValue(TYPE entity) {
        if (!entity.isInstanceOf(getValueClass())) {
            throw new ClassCastException("Collection member type expected " + getValueClass().getName() + ", got " + entity.getInstanceValueClass().getName());
        }
        Map<String, Serializable> value = ((SharedEntityHandler) entity).ensureValue();
        if (!this.getValueClass().equals(entity.getInstanceValueClass())) {
            value.put(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR, EntityFactory.getEntityPrototype(entity.getInstanceValueClass()));
        }

        if (PROPER_POINTERS) {
        } else {
            ((SharedEntityHandler) entity).attachToOwner(this, this.getFieldName());
        }

        // ensure @Owner value is set properly.
        String ownerMemberName = entity.getEntityMeta().getOwnerMemberName();
        if ((ownerMemberName != null) && (getMeta().isOwnedRelationships())) {
            Map<String, Serializable> ownerValue = ((SharedEntityHandler) getOwner()).ensureValue();
            value.put(ownerMemberName, (Serializable) ownerValue);
            if (!entity.getMember(ownerMemberName).getObjectClass().equals(getOwner().getInstanceValueClass())) {
                ownerValue.put(IEntity.CONCRETE_TYPE_DATA_ATTR, EntityFactory.getEntityPrototype(getOwner().getInstanceValueClass()));
            }
        }

        return value;
    }

    protected Map<String, Serializable> comparableValue(IEntity entity) {
        Map<String, Serializable> enitytValue = entity.getValue();
        if ((entity.getPrimaryKey() == null) || this.getValueClass().equals(entity.getInstanceValueClass())) {
            return enitytValue;
        } else {
            Map<String, Serializable> comparableValue = new EntityValueMap();
            comparableValue.put(IEntity.PRIMARY_KEY, enitytValue.get(IEntity.PRIMARY_KEY));
            comparableValue.put(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR, EntityFactory.getEntityPrototype(entity.getInstanceValueClass()));
            return comparableValue;
        }
    }

    @Override
    public boolean remove(Object o) {
        if ((o instanceof IEntity) && (((IEntity) o).isInstanceOf(getValueClass()))) {
            Collection<?> collectionValue = (Collection<?>) getValue();
            if (collectionValue != null) {
                Map<String, Serializable> enitytValue = comparableValue((IEntity) o);
                boolean rc = collectionValue.remove(enitytValue);
                if (rc && getMeta().isOwnedRelationships()) {
                    ((SharedEntityHandler) getOwner()).removeValueFromGraph((IEntity) o);
                }
                return rc;
            } else {
                return false;
            }
        } else {
            throw new ClassCastException("Collection member type expected " + getValueClass().getName() + ", got " + o.getClass());
        }
    }

    @Override
    public void set(ICollection<TYPE, VALUE_TYPE> typedCollection) {
        switch (typedCollection.getAttachLevel()) {
        case CollectionSizeOnly:
            setCollectionSizeOnly(typedCollection.size());
            break;
        case Detached:
            setAttachLevel(AttachLevel.Detached);
            break;
        case Attached:
            setValue(typedCollection.getValue());
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    protected String exceptionInfo() {
        return getValueClass() + " '" + this.getFieldName() + "' of " + GWTJava5Helper.getSimpleName(getOwner().getObjectClass());
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

    @SuppressWarnings("unchecked")
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

    private class StringConverter implements ToStringConverter<TYPE> {

        @Override
        public String toString(TYPE value) {
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
            return ConverterUtils.convertCollection(this, new StringConverter());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getObjectClass().getName()).append(" ");
        if (getAttachLevel() == AttachLevel.Detached) {
            b.append("{Detached}");
        } else {
            VALUE_TYPE value = getValue();
            if (value != null) {
                Set<Map<String, Serializable>> processed = new IdentityHashSet<Map<String, Serializable>>();
                b.append("[ size=");
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
                        b.append('{');
                        if (ToStringStyle.fieldMultiLine) {
                            b.append('\n');
                        }
                        EntityValueMap.dumpMap(b, (Map<String, Serializable>) o, processed, ToStringStyle.PADDING + ToStringStyle.PADDING);
                        if (ToStringStyle.fieldMultiLine) {
                            b.append(ToStringStyle.PADDING);
                        }
                        b.append('}');
                    } else {
                        b.append(o);
                    }
                }
                b.append(']');
            } else {
                b.append("{null}");
            }
        }
        return b.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toStringIds() {
        StringBuilder b = new StringBuilder();
        if (getAttachLevel() == AttachLevel.Detached) {
            b.append("{Detached}");
        } else {
            VALUE_TYPE value = getValue();
            if (value != null) {
                b.append("[");
                boolean first = true;
                for (Object o : (Collection<?>) value) {
                    if (first) {
                        first = false;
                    } else {
                        b.append(", ");
                    }
                    if (o instanceof Map<?, ?>) {
                        b.append(((Map<String, Object>) o).get(IEntity.PRIMARY_KEY));
                    } else {
                        b.append(o);
                    }
                }
                b.append(']');
            } else {
                b.append("{null}");
            }
        }
        return b.toString();
    }
}

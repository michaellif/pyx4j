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
 * Created on Oct 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;

public class SetHandler<OBJECT_TYPE extends IEntity<?>> extends ObjectHandler<ISet<OBJECT_TYPE>, Set<Map<String, ?>>> implements ISet<OBJECT_TYPE> {

    private final Class<OBJECT_TYPE> valueClass;

    private static class ElementsComparator implements Comparator<Map<String, ?>> {

        @Override
        public int compare(Map<String, ?> o1, Map<String, ?> o2) {
            return o1.equals(o2) ? 0 : (o1.hashCode() - o2.hashCode());
        }

    }

    public SetHandler(IEntity<?> parent, String fieldName, Class<OBJECT_TYPE> valueClass) {
        super(ISet.class, parent, fieldName);
        this.valueClass = valueClass;
    }

    @Override
    public Class<OBJECT_TYPE> getValueClass() {
        return valueClass;
    }

    @Override
    public Path getPath() {
        return new Path(this);
    }

    @Override
    public boolean isNull() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void set(ISet<OBJECT_TYPE> entity) {
        setValue(entity.getValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Map<String, ?>> getValue() {
        Map<String, ?> data = getParent().getValue();
        if (data == null) {
            return null;
        } else {
            return (Set<Map<String, ?>>) data.get(getFieldName());
        }
    }

    @Override
    public void setValue(Set<Map<String, ?>> value) {
        getParent().setMemberValue(getFieldName(), value);
    }

    @Override
    public boolean add(OBJECT_TYPE entity) {
        Set<Map<String, ?>> value = getValue();
        if (value == null) {
            // Use TreeSet for implementation to allow for modifiable Objects Properties (hashCode) after they are added to Set
            value = new TreeSet<Map<String, ?>>(new ElementsComparator());
            setValue(value);
        }
        return value.add(entity.getValue());
    }

    @Override
    public boolean addAll(Collection<? extends OBJECT_TYPE> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        Set<?> set = getValue();
        if (set != null) {
            set.clear();
        }
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof IEntity<?>) {
            Set<?> set = getValue();
            if (set != null) {
                return set.contains(((IEntity<?>) o).getValue());
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        Set<?> set = getValue();
        if (set != null) {
            return set.size() != 0;
        } else {
            return false;
        }
    }

    @Override
    public Iterator<OBJECT_TYPE> iterator() {
        return new Iterator<OBJECT_TYPE>() {

            final Iterator<Map<String, ?>> iter = getValue().iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public OBJECT_TYPE next() {
                Map<String, Object> entityValue = (Map<String, Object>) iter.next();
                OBJECT_TYPE entity = EntityFactory.create(getValueClass());
                entity.setValue(entityValue);
                return entity;
            }

            @Override
            public void remove() {
                iter.remove();
            }
        };
    }

    @Override
    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        Set<?> set = getValue();
        if (set != null) {
            return set.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <TT> TT[] toArray(TT[] a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return getObjectClass().getName() + getValue();
    }

}
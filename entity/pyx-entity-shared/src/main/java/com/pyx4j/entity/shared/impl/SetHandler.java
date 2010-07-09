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

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ISet;

public class SetHandler<TYPE extends IEntity> extends AbstractCollectionHandler<TYPE, Set<Map<String, Object>>> implements ISet<TYPE> {

    private static final long serialVersionUID = 1940065645661650951L;

    //TODO probably we don't need it now. Remove.
    @SuppressWarnings("serial")
    public static class ElementsComparator implements Comparator<Map<String, Object>>, Serializable {

        @Override
        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
            if (o1 == o2) {
                return 0;
            }
            //Map represent the IEntity use PRIMARY_KEY
            Object pk1 = o1.get(IEntity.PRIMARY_KEY);
            if (pk1 != null) {
                Object pk2 = o2.get(IEntity.PRIMARY_KEY);
                if (pk2 != null) {
                    if (pk1.equals(pk2)) {
                        return 0;
                    }
                }
            }
            // Fall back to general comparison
            return o1.hashCode() - o2.hashCode();
        }

    }

    public SetHandler(IEntity parent, String fieldName, Class<TYPE> valueClass) {
        super(ISet.class, valueClass, parent, fieldName);
    }

    @Override
    public boolean isNull() {
        return (getValue() == null);
    }

    @Override
    public void set(ISet<TYPE> typedSet) {
        setValue(typedSet.getValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Map<String, Object>> getValue() {
        Map<String, Object> data = getOwner().getValue();
        if (data == null) {
            return null;
        } else {
            return (Set<Map<String, Object>>) data.get(getFieldName());
        }
    }

    @Override
    public void setValue(Set<Map<String, Object>> value) {
        if ((value != null) && !(value instanceof TreeSet<?>)) {
            throw new ClassCastException("Set expects TreeSet as value");
        }
        getOwner().setMemberValue(getFieldName(), value);
    }

    /**
     * Guarantee that data holder is created before setting the value of element
     */
    private Set<Map<String, Object>> ensureValue() {
        Set<Map<String, Object>> value = getValue();
        if (value == null) {
            // Use TreeSet for implementation to allow for modifiable Objects Properties (hashCode) after they are added to Set
            value = new TreeSet<Map<String, Object>>(new ElementsComparator());
            setValue(value);
        }
        return value;
    }

    @Override
    public boolean add(TYPE entity) {
        return ensureValue().add(((SharedEntityHandler) entity).ensureValue());
    }

    @Override
    public boolean addAll(Collection<? extends TYPE> c) {
        boolean rc = false;
        Set<Map<String, Object>> value = ensureValue();
        for (TYPE el : c) {
            if (value.add(((SharedEntityHandler) el).ensureValue())) {
                rc = true;
            }
        }
        return rc;
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
        if (o instanceof IEntity) {
            Set<?> set = getValue();
            if (set != null) {
                return set.contains(((IEntity) o).getValue());
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        Set<?> value = getValue();
        if (value != null) {
            return value.isEmpty();
        } else {
            return true;
        }
    }

    @Override
    public Iterator<TYPE> iterator() {
        // iterator is also behaves likes Elvis 
        final Set<Map<String, Object>> setValue = getValue();
        if (setValue == null) {
            return new Iterator<TYPE>() {

                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public TYPE next() {
                    throw new NoSuchElementException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        return new Iterator<TYPE>() {

            final Iterator<Map<String, Object>> iter = setValue.iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public TYPE next() {
                Map<String, Object> entityValue = iter.next();
                TYPE entity = $();
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
        if ((o instanceof IEntity) && (getValueClass().equals(((IEntity) o).getObjectClass()))) {
            Set<?> set = getValue();
            if (set != null) {
                return set.remove(((IEntity) o).getValue());
            } else {
                return false;
            }
        } else {
            throw new ClassCastException("Set member type expected " + getValueClass());
        }

    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO implement this
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
    public String toString() {
        return getObjectClass().getName() + getValue();
    }

}
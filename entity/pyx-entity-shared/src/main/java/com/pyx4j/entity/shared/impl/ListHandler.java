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
 * Created on Jan 24, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;

public class ListHandler<TYPE extends IEntity<?>> extends ObjectHandler<IList<TYPE>, List<Map<String, Object>>> implements IList<TYPE> {

    private final Class<TYPE> valueClass;

    public ListHandler(IEntity<?> parent, String fieldName, Class<TYPE> valueClass) {
        super(ISet.class, parent, fieldName);
        this.valueClass = valueClass;
    }

    @Override
    public Class<TYPE> getValueClass() {
        return valueClass;
    }

    @Override
    public Path getPath() {
        return new Path(this);
    }

    @Override
    public boolean isNull() {
        return (getValue() == null);
    }

    @Override
    public void set(IList<TYPE> entity) {
        setValue(entity.getValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> getValue() {
        Map<String, Object> data = getParent().getValue();
        if (data == null) {
            return null;
        } else {
            return (List<Map<String, Object>>) data.get(getFieldName());
        }
    }

    @Override
    public void setValue(List<Map<String, Object>> value) {
        getParent().setMemberValue(getFieldName(), value);
    }

    /**
     * Guarantee that data holder is created before setting the value of element
     */
    private List<Map<String, Object>> ensureValue() {
        List<Map<String, Object>> value = getValue();
        if (value == null) {
            // TODO test  modifiable Objects Properties 
            value = new Vector<Map<String, Object>>();
            setValue(value);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean add(TYPE entity) {
        return ensureValue().add(((SharedEntityHandler) entity).ensureValue());
    }

    @Override
    public void add(int index, TYPE entity) {
        ensureValue().add(index, entity.getValue());

    }

    @Override
    public boolean addAll(Collection<? extends TYPE> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends TYPE> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        List<Map<String, Object>> value = getValue();
        if (value != null) {
            value.clear();
        }
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof IEntity<?>) {
            List<?> value = getValue();
            if (value != null) {
                return value.contains(((IEntity<?>) o).getValue());
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
    public TYPE get(int index) {
        List<Map<String, Object>> value = getValue();
        if (value != null) {
            Map<String, Object> entityValue = value.get(index);
            TYPE entity = EntityFactory.create(getValueClass());
            entity.setValue(entityValue);
            return entity;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof IEntity<?>) {
            List<?> value = getValue();
            if (value != null) {
                return value.indexOf(((IEntity<?>) o).getValue());
            } else {
                return -1;
            }
        } else {
            throw new ClassCastException("Value of class " + getValueClass() + " expected");
        }
    }

    @Override
    public boolean isEmpty() {
        List<?> value = getValue();
        if (value != null) {
            return value.isEmpty();
        } else {
            return true;
        }
    }

    @Override
    public Iterator<TYPE> iterator() {
        // iterator is also behaves likes Elvis 
        final List<Map<String, Object>> value = getValue();
        if (value == null) {
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

            final Iterator<Map<String, Object>> iter = value.iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public TYPE next() {
                Map<String, Object> entityValue = iter.next();
                TYPE entity = EntityFactory.create(getValueClass());
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
    public int lastIndexOf(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ListIterator<TYPE> listIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListIterator<TYPE> listIterator(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean remove(Object o) {
        if ((o instanceof IEntity<?>) && (getValueClass().equals(((IEntity<?>) o).getObjectClass()))) {
            List<?> value = getValue();
            if (value != null) {
                return value.remove(((IEntity<?>) o).getValue());
            } else {
                return false;
            }
        } else {
            throw new ClassCastException("List member type expected " + getValueClass());
        }
    }

    @Override
    public TYPE remove(int index) {
        TYPE entity = get(index);
        getValue().remove(index);
        return entity;
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
    public TYPE set(int index, TYPE element) {
        // TODO implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        List<?> value = getValue();
        if (value != null) {
            return value.size();
        } else {
            return 0;
        }
    }

    @Override
    public List<TYPE> subList(int fromIndex, int toIndex) {
        // TODO implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        // TODO implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO implement this
        throw new UnsupportedOperationException();
    }

}

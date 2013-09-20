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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

public class ListHandler<TYPE extends IEntity> extends AbstractCollectionHandler<TYPE, List<Map<String, Serializable>>> implements IList<TYPE> {

    private static final long serialVersionUID = 6416411665137002645L;

    public ListHandler(IEntity parent, String fieldName, Class<TYPE> valueClass) {
        super(IList.class, valueClass, parent, fieldName);
    }

    @Override
    public boolean isNull() {
        return (getValue() == null);
    }

    @Override
    public void set(IList<TYPE> typedList) {
        setValue(typedList.getValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Serializable>> getValue() {
        Map<String, Serializable> data = getOwner().getValue();
        if (data == null) {
            return null;
        } else {
            Object value = data.get(getFieldName());
            assert (value != AttachLevel.Detached) : "Access to detached IList " + exceptionInfo();
            return (List<Map<String, Serializable>>) value;
        }
    }

    @Override
    public void setValue(List<Map<String, Serializable>> value) {
        getOwner().setMemberValue(getFieldName(), (Serializable) value);
    }

    /**
     * Guarantee that data holder is created before setting the value of element
     */
    private List<Map<String, Serializable>> ensureValue() {
        List<Map<String, Serializable>> value = getValue();
        if (value == null) {
            // TODO test  modifiable Objects Properties 
            value = new Vector<Map<String, Serializable>>();
            setValue(value);
        }
        return value;
    }

    @Override
    public boolean add(TYPE entity) {
        return ensureValue().add(ensureTypedValue(entity));
    }

    @Override
    public void add(int index, TYPE entity) {
        ensureValue().add(index, ensureTypedValue(entity));

    }

    @Override
    public boolean addAll(Collection<? extends TYPE> c) {
        boolean rc = false;
        List<Map<String, Serializable>> value = ensureValue();
        for (TYPE el : c) {
            if (value.add(ensureTypedValue(el))) {
                rc = true;
            }
        }
        return rc;
    }

    @Override
    public boolean addAll(int index, Collection<? extends TYPE> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        List<Map<String, Serializable>> value = getValue();
        if (value != null) {
            value.clear();
        }
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof IEntity) {
            List<?> value = getValue();
            if (value != null) {
                return value.contains(comparableValue((IEntity) o));
            }
        }
        return false;
    }

    @Override
    public boolean containsAny(TYPE... value) {
        List<?> cvalue = getValue();
        if (cvalue != null) {
            for (TYPE v : value) {
                if (cvalue.contains(comparableValue(v))) {
                    return true;
                }
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
        List<Map<String, Serializable>> value = getValue();
        if (value != null) {
            return createTypedEntity(value.get(index));
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public TYPE get(TYPE element) {
        int index = indexOf(element);
        if (index == -1) {
            return null;
        } else {
            return get(index);
        }
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof IEntity) {
            List<?> value = getValue();
            if (value != null) {
                return value.indexOf(comparableValue((IEntity) o));
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
        final List<Map<String, Serializable>> value = getValue();
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

            final Iterator<Map<String, Serializable>> iter = value.iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public TYPE next() {
                return createTypedEntity(iter.next());
            }

            @Override
            public void remove() {
                iter.remove();
            }
        };
    }

    @Override
    public int lastIndexOf(Object o) {
        // TODO implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<TYPE> listIterator() {
        // TODO implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<TYPE> listIterator(int index) {
        // TODO implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public void move(int originalIndex, int targetIndex) {
        TYPE entity = get(originalIndex);
        getValue().remove(originalIndex);
        getValue().add(targetIndex, ensureTypedValue(entity));
    }

    @Override
    public TYPE remove(int index) {
        TYPE entity = get(index);
        getValue().remove(index);
        if (getMeta().isOwnedRelationships()) {
            ((SharedEntityHandler) getOwner()).removeValueFromGraph(entity);
        }
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
    public List<TYPE> subList(int fromIndex, int toIndex) {
        // TODO implement this
        throw new UnsupportedOperationException();
    }

}

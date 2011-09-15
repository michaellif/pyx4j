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
 * Created on Feb 1, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.commons.ConverterUtils.ToStringConverter;
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class PrimitiveSetHandler<TYPE> extends ObjectHandler<Set<TYPE>> implements IPrimitiveSet<TYPE> {

    private static final long serialVersionUID = -1321079550481643142L;

    private final Class<TYPE> valueClass;

    public PrimitiveSetHandler(IEntity parent, String fieldName, Class<TYPE> valueClass) {
        super(IPrimitiveSet.class, parent, fieldName);
        this.valueClass = valueClass;
    }

    @Override
    public Class<TYPE> getValueClass() {
        return valueClass;
    }

    @Override
    public void set(IPrimitiveSet<TYPE> typedSet) {
        getOwner().setMemberValue(getFieldName(), typedSet.getValue());
    }

    @Override
    public boolean isNull() {
        return (getValue() == null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<TYPE> getValue() {
        return (Set<TYPE>) getOwner().getMemberValue(getFieldName());
    }

    @Override
    public void setValue(Set<TYPE> value) {
        getOwner().setMemberValue(getFieldName(), value);
    }

    /**
     * Guarantee that data holder is created before setting the value of element
     */
    private Set<TYPE> ensureValue() {
        Set<TYPE> value = getValue();
        if (value == null) {
            value = new HashSet<TYPE>();
            setValue(value);
        }
        return value;
    }

    @Override
    public boolean add(TYPE e) {
        if (!getValueClass().equals(e.getClass())) {
            throw new ClassCastException("Set member type expected " + getValueClass());
        }
        return ensureValue().add(e);
    }

    @Override
    public boolean addAll(Collection<? extends TYPE> c) {
        return ensureValue().addAll(c);
    }

    @Override
    public void setArrayValue(TYPE[] value) throws ClassCastException {
        clear();
        if ((value != null) && (value.length != 0)) {
            addAll(Arrays.asList(value));
        }
    }

    @Override
    public void setCollectionValue(Collection<TYPE> c) throws ClassCastException {
        Set<?> value = getValue();
        if (value == c) {
            return;
        }
        if (value != null) {
            value.clear();
        }
        if (c != null) {
            addAll(c);
        }
    }

    @Override
    public void clear() {
        Set<?> value = getValue();
        if (value != null) {
            value.clear();
        }
    }

    @Override
    public boolean contains(Object o) {
        if (!getValueClass().equals(o.getClass())) {
            throw new ClassCastException("Set member type expected " + getValueClass());
        }
        Set<?> value = getValue();
        if (value != null) {
            return value.contains(o);
        } else {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        Set<?> value = getValue();
        if (value != null) {
            return value.containsAll(c);
        } else {
            return false;
        }
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

    /**
     * iterator behaves likes Elvis
     */
    @Override
    public Iterator<TYPE> iterator() {
        Set<TYPE> set = getValue();
        if (set == null) {
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
        } else {
            return set.iterator();
        }
    }

    @Override
    public boolean remove(Object o) {
        return getValue().remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getValue().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getValue().retainAll(c);
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
        Set<TYPE> set = getValue();
        if (set != null) {
            return set.toArray();
        } else {
            return new Object[0];
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Set<TYPE> set = getValue();
        if (set != null) {
            return set.toArray(a);
        } else {
            if (a.length == 0) {
                return a;
            } else {
                return new ArrayList<T>().toArray(a);
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        Set<TYPE> thisValue = this.getValue();
        if ((other == null) || (thisValue == null) || (!(other instanceof IPrimitiveSet<?>))
                || (!this.getValueClass().equals(((IPrimitiveSet<?>) other).getValueClass()))) {
            return false;
        }
        return EqualsHelper.equals(thisValue, ((IPrimitiveSet<?>) other).getValue());
    }

    @Override
    public int hashCode() {
        Set<TYPE> thisValue = this.getValue();
        if (thisValue == null) {
            return super.hashCode();
        } else {
            return thisValue.hashCode();
        }
    }

    private class StringConverter implements ToStringConverter<TYPE> {

        private final MemberMeta memberMeta;

        public StringConverter(MemberMeta memberMeta) {
            this.memberMeta = memberMeta;
        }

        @Override
        public String toString(TYPE value) {
            if (value == null) {
                return memberMeta.getNullString();
            } else if (memberMeta.useMessageFormat()) {
                return SimpleMessageFormat.format(memberMeta.getFormat(), value);
            } else if (value instanceof Date) {
                return SimpleMessageFormat.format("{0,date," + memberMeta.getFormat() + "}", value);
            } else if (value instanceof Number) {
                return SimpleMessageFormat.format("{0,number," + memberMeta.getFormat() + "}", value);
            } else {
                return String.valueOf(value);
            }
        }
    }

    @Override
    public String getStringView() {
        MemberMeta mm = getMeta();
        String format = mm.getFormat();
        Set<TYPE> thisValue = this.getValue();
        if (thisValue == null) {
            return mm.getNullString();
        } else if (format == null) {
            return String.valueOf(thisValue);
        } else {
            return ConverterUtils.convertCollection(thisValue, new StringConverter(mm));
        }
    }

    @Override
    public String toString() {
        return getObjectClass().getName() + " " + getValue();
    }

}

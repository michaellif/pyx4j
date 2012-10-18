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
 * Created on Jan 7, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.criterion;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Vector;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;

@SuppressWarnings("serial")
public class PropertyCriterion implements Criterion {

    public static final char WILDCARD_CHAR = '*';

    public static enum Restriction {
        LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, EQUAL, NOT_EQUAL, IN,

        RDB_LIKE, NOT_EXISTS
    }

    private String propertyPath;

    private Restriction restriction;

    private Serializable value;

    protected PropertyCriterion() {

    }

    public PropertyCriterion(String propertyPath, Restriction restriction, Serializable value) {
        this.propertyPath = propertyPath;
        this.restriction = restriction;
        if ((value instanceof IObject) && (((IObject<?>) value).isPrototype())) {
            this.value = ((IObject<?>) value).getPath();
        } else if (value instanceof IEntity) {
            this.value = ((IEntity) value).createIdentityStub();
        } else {
            this.value = value;
        }
    }

    public PropertyCriterion(String propertyPath, Restriction restriction, IPrimitive<?> value) {
        this.propertyPath = propertyPath;
        this.restriction = restriction;
        if (value.isPrototype()) {
            this.value = value.getPath();
        } else {
            this.value = (Serializable) value.getValue();
        }
    }

    public PropertyCriterion(String propertyPath, Restriction restriction, Class<? extends IEntity> value) {
        this.propertyPath = propertyPath;
        this.restriction = restriction;
        this.value = EntityFactory.create(value);
    }

    public PropertyCriterion(IObject<?> member, Restriction restriction, Serializable value) {
        this(member.getPath().toString(), restriction, value);
    }

    public PropertyCriterion(IObject<?> member, Restriction restriction, Collection<?> value) {
        this(member.getPath().toString(), restriction, createSerializableCollection(value));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Serializable createSerializableCollection(Collection<?> collection) {
        Vector result = new Vector();
        for (Object value : collection) {
            if (value instanceof IEntity) {
                value = ((IEntity) value).createIdentityStub();
            } else if (value instanceof Class) {
                value = EntityFactory.create((Class<IEntity>) value);
            }
            result.add(value);
        }
        return result;
    }

    public static PropertyCriterion eq(IObject<?> member, Class<? extends IEntity> value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.EQUAL, value);
    }

    public static PropertyCriterion eq(IObject<?> member, IPrimitive<?> value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.EQUAL, value);
    }

    public static PropertyCriterion eq(IObject<?> member, Serializable value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.EQUAL, value);
    }

    public static PropertyCriterion isNull(IObject<?> member) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.EQUAL, (Serializable) null);
    }

    public static PropertyCriterion notExists(IObject<?> member) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.NOT_EXISTS, (Serializable) null);
    }

    public static PropertyCriterion notExists(IObject<?> member, Criterion criterion) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.NOT_EXISTS, criterion);
    }

    public static PropertyCriterion isNotNull(IObject<?> member) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.NOT_EQUAL, (Serializable) null);
    }

    public static PropertyCriterion like(IObject<?> member, String value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.RDB_LIKE, value);
    }

    @Deprecated
    public static PropertyCriterion eq(String propertyName, Serializable value) {
        return new PropertyCriterion(propertyName, Restriction.EQUAL, value);
    }

    public static PropertyCriterion ne(IObject<?> member, Serializable value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.NOT_EQUAL, value);
    }

    public static <T extends Collection<?>> PropertyCriterion in(IObject<?> member, T values) {
        return new PropertyCriterion(member, Restriction.IN, values);
    }

    public static <T extends Enum<T>> PropertyCriterion in(IObject<T> member, EnumSet<T> values) {
        return new PropertyCriterion(member, Restriction.IN, (Collection<T>) values);
    }

    public static PropertyCriterion in(IObject<?> member, Serializable... value) {
        return new PropertyCriterion(member, Restriction.IN, Arrays.asList(value));
    }

    public static PropertyCriterion gt(IObject<?> member, Serializable value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.GREATER_THAN, value);
    }

    public static PropertyCriterion gt(IObject<?> member, IPrimitive<?> value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.GREATER_THAN, value);
    }

    public static PropertyCriterion ge(IObject<?> member, Serializable value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.GREATER_THAN_OR_EQUAL, value);
    }

    public static PropertyCriterion ge(IObject<?> member, IPrimitive<?> value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.GREATER_THAN_OR_EQUAL, value);
    }

    public static PropertyCriterion lt(IObject<?> member, Serializable value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.LESS_THAN, value);
    }

    public static PropertyCriterion lt(IObject<?> member, IPrimitive<?> value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.LESS_THAN, value);
    }

    public static PropertyCriterion le(IObject<?> member, Serializable value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.LESS_THAN_OR_EQUAL, value);
    }

    public static PropertyCriterion le(IObject<?> member, IPrimitive<?> value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.LESS_THAN_OR_EQUAL, value);
    }

    public String getPropertyPath() {
        return this.propertyPath;
    }

    public Restriction getRestriction() {
        return this.restriction;
    }

    public Serializable getValue() {
        return this.value;
    }

    public boolean isValid() {
        return (getPropertyPath() != null && getRestriction() != null && (getValue() != null || (getRestriction() == Restriction.EQUAL || getRestriction() == Restriction.NOT_EQUAL)));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PropertyCriterion)) {
            return false;
        }
        if ((this.restriction == null) || (!this.restriction.equals(((PropertyCriterion) o).restriction))) {
            return false;
        }
        return EqualsHelper.equals(this.value, ((PropertyCriterion) o).value) && EqualsHelper.equals(this.propertyPath, ((PropertyCriterion) o).propertyPath);
    }

    @Override
    public int hashCode() {
        return this.restriction.hashCode() + ((this.propertyPath != null) ? this.propertyPath.hashCode() : -1)
                + ((this.value != null) ? this.value.hashCode() : -1);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(propertyPath).append(' ').append(restriction).append(' ').append(value);
        return builder.toString();
    }
}

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
import java.util.Collection;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;

@SuppressWarnings("serial")
public class PropertyCriterion implements Criterion {

    public static enum Restriction {
        LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, EQUAL, NOT_EQUAL, IN,

        RDB_LIKE
    }

    private String propertyName;

    private Restriction restriction;

    private Serializable value;

    protected PropertyCriterion() {

    }

    public PropertyCriterion(String propertyName, Restriction restriction, Serializable value) {
        this.propertyName = propertyName;
        this.restriction = restriction;
        if (value instanceof IEntity) {
            //TODO detach/ make identity copy
            this.value = value;
        } else {
            this.value = value;
        }
    }

    public PropertyCriterion(String propertyName, Restriction restriction, IPrimitive<?> value) {
        this.propertyName = propertyName;
        this.restriction = restriction;
        this.value = (Serializable) value.getValue();
    }

    public PropertyCriterion(IObject<?> member, Restriction restriction, Serializable value) {
        this(member.getPath().toString(), restriction, value);
    }

    public static PropertyCriterion eq(IObject<?> member, Serializable value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.EQUAL, value);
    }

    public static PropertyCriterion eq(IObject<?> member, IPrimitive<?> value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.EQUAL, value);
    }

    @Deprecated
    public static PropertyCriterion eq(String propertyName, Serializable value) {
        return new PropertyCriterion(propertyName, Restriction.EQUAL, value);
    }

    public static PropertyCriterion ne(IObject<?> member, Serializable value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.NOT_EQUAL, value);
    }

    public static <T extends Collection<?> & Serializable> PropertyCriterion in(IObject<?> member, T values) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.IN, values);
    }

    public static PropertyCriterion in(IObject<?> member, Serializable... value) {
        return new PropertyCriterion(member.getPath().toString(), Restriction.IN, value);
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

    public String getPropertyName() {
        return this.propertyName;
    }

    public Restriction getRestriction() {
        return this.restriction;
    }

    public Serializable getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PropertyCriterion)) {
            return false;
        }
        if ((this.restriction == null) || (!this.restriction.equals(((PropertyCriterion) o).restriction))) {
            return false;
        }
        return EqualsHelper.equals(this.value, ((PropertyCriterion) o).value) && EqualsHelper.equals(this.propertyName, ((PropertyCriterion) o).propertyName);
    }

    @Override
    public int hashCode() {
        return this.restriction.hashCode() + ((this.propertyName != null) ? this.propertyName.hashCode() : -1)
                + ((this.value != null) ? this.value.hashCode() : -1);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(propertyName).append(' ').append(restriction).append(' ').append(value);
        return builder.toString();
    }
}

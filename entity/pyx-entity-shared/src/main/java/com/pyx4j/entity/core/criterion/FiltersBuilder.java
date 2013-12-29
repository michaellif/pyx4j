/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2012-10-18
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.core.criterion;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.core.IVersionedEntity;

/**
 * 
 * The name criteria_proto_member was used to simplify typing in Eclipse
 * 
 * Start with:
 * 
 * {@code EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class); }
 * 
 * Now type: {@code criteria.eq } You will get suggestion:
 * 
 * {@code criteria.eq(criteria_proto_member, values); }
 * 
 * Then just replace underscore with dot and you will have Criterion:
 * 
 * {@code criteria.eq(criteria.proto().id(), emp1.id()); }
 * 
 */
public abstract class FiltersBuilder {

    protected abstract FiltersBuilder addCriterion(Criterion criterion);

    public OrCriterion or() {
        OrCriterion criterion = new OrCriterion();
        addCriterion(criterion);
        return criterion;
    }

    public AndCriterion and() {
        AndCriterion criterion = new AndCriterion();
        addCriterion(criterion);
        return criterion;
    }

    public final void eq(IObject<?> criteria_proto_member, Class<? extends IEntity> value) {
        addCriterion(PropertyCriterion.eq(criteria_proto_member, value));
    }

    public final void eq(IObject<?> criteria_proto_member, IPrimitive<?> value) {
        addCriterion(PropertyCriterion.eq(criteria_proto_member, value));
    }

    public final void eq(IObject<?> criteria_proto_member, Serializable value) {
        addCriterion(PropertyCriterion.eq(criteria_proto_member, value));
    }

    public final void isNull(IObject<?> criteria_proto_member) {
        addCriterion(PropertyCriterion.isNull(criteria_proto_member));
    }

    public final void notExists(IObject<?> criteria_proto_member) {
        addCriterion(PropertyCriterion.notExists(criteria_proto_member));
    }

    public final void notExists(IObject<?> criteria_proto_member, Criterion criterion) {
        addCriterion(PropertyCriterion.notExists(criteria_proto_member, criterion));
    }

    public final void isNotNull(IObject<?> criteria_proto_member) {
        addCriterion(PropertyCriterion.isNotNull(criteria_proto_member));
    }

    public final void like(IObject<?> criteria_proto_member, String value) {
        addCriterion(PropertyCriterion.like(criteria_proto_member, value));
    }

    public final void ne(IObject<?> criteria_proto_member, IPrimitive<?> value) {
        addCriterion(PropertyCriterion.ne(criteria_proto_member, value));
    }

    public final void ne(IObject<?> criteria_proto_member, Serializable value) {
        addCriterion(PropertyCriterion.ne(criteria_proto_member, value));
    }

    public final <T extends Collection<?>> void in(IObject<?> criteria_proto_member, T values) {
        addCriterion(PropertyCriterion.in(criteria_proto_member, values));
    }

    public final <T extends Enum<T>> void in(IObject<T> criteria_proto_member, EnumSet<T> values) {
        addCriterion(PropertyCriterion.in(criteria_proto_member, values));
    }

    public final void in(IObject<?> criteria_proto_member, Serializable... values) {
        addCriterion(PropertyCriterion.in(criteria_proto_member, values));
    }

    public final void gt(IObject<?> criteria_proto_member, Serializable value) {
        addCriterion(PropertyCriterion.gt(criteria_proto_member, value));
    }

    public final void gt(IObject<?> criteria_proto_member, IPrimitive<?> value) {
        addCriterion(PropertyCriterion.gt(criteria_proto_member, value));
    }

    public final void ge(IObject<?> criteria_proto_member, Serializable value) {
        addCriterion(PropertyCriterion.ge(criteria_proto_member, value));
    }

    public final void ge(IObject<?> criteria_proto_member, IPrimitive<?> value) {
        addCriterion(PropertyCriterion.ge(criteria_proto_member, value));
    }

    public final void lt(IObject<?> criteria_proto_member, Serializable value) {
        addCriterion(PropertyCriterion.lt(criteria_proto_member, value));
    }

    public final void lt(IObject<?> criteria_proto_member, IPrimitive<?> value) {
        addCriterion(PropertyCriterion.lt(criteria_proto_member, value));
    }

    public final void le(IObject<?> criteria_proto_member, Serializable value) {
        addCriterion(PropertyCriterion.le(criteria_proto_member, value));
    }

    public final void le(IObject<?> criteria_proto_member, IPrimitive<?> value) {
        addCriterion(PropertyCriterion.le(criteria_proto_member, value));
    }

    public final <T extends IVersionedEntity<?>> void hasCurrentVersion(T criteria_proto_member) {
        isNotNull(criteria_proto_member.version().fromDate());
        isNull(criteria_proto_member.version().toDate());
    }

    public final <T extends IVersionData<?>> void isCurrent(T criteria_proto_member) {
        isNotNull(criteria_proto_member.fromDate());
        isNull(criteria_proto_member.toDate());
    }

    /**
     * Current or previous versions. e.g. Not Draft.
     */
    public final <T extends IVersionedEntity<?>> void hasFinalizedVersion(T criteria_proto_member) {
        isNotNull(criteria_proto_member.version().fromDate());
    }

    public final <T extends IVersionData<?>> void isFinalized(T criteria_proto_member) {
        isNotNull(criteria_proto_member.fromDate());
    }

    public final <T extends IVersionedEntity<?>> void hasDraftVersion(T criteria_proto_member) {
        isNull(criteria_proto_member.version().toDate());
        isNull(criteria_proto_member.version().fromDate());
    }

    public final <T extends IVersionData<?>> void isDraft(T criteria_proto_member) {
        isNull(criteria_proto_member.toDate());
        isNull(criteria_proto_member.fromDate());
    }
}

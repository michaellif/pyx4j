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
package com.pyx4j.entity.shared.criterion;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;

public abstract class FiltersBuilder {

    protected abstract FiltersBuilder addCriterion(Criterion criterion);

    public void eq(IObject<?> member, Class<? extends IEntity> value) {
        addCriterion(PropertyCriterion.eq(member, value));
    }

    public void eq(IObject<?> member, IPrimitive<?> value) {
        addCriterion(PropertyCriterion.eq(member, value));
    }

    public void eq(IObject<?> member, Serializable value) {
        addCriterion(PropertyCriterion.eq(member, value));
    }

    public void isNull(IObject<?> member) {
        addCriterion(PropertyCriterion.isNull(member));
    }

    public void notExists(IObject<?> member) {
        addCriterion(PropertyCriterion.notExists(member));
    }

    public void notExists(IObject<?> member, Criterion criterion) {
        addCriterion(PropertyCriterion.notExists(member, criterion));
    }

    public void isNotNull(IObject<?> member) {
        addCriterion(PropertyCriterion.isNotNull(member));
    }

    public void like(IObject<?> member, String value) {
        addCriterion(PropertyCriterion.like(member, value));
    }

    public void ne(IObject<?> member, Serializable value) {
        addCriterion(PropertyCriterion.ne(member, value));
    }

    public <T extends Collection<?>> void in(IObject<?> member, T values) {
        addCriterion(PropertyCriterion.in(member, values));
    }

    public <T extends Enum<T>> void in(IObject<T> member, EnumSet<T> values) {
        addCriterion(PropertyCriterion.in(member, values));
    }

    public void in(IObject<?> member, Serializable... values) {
        addCriterion(PropertyCriterion.in(member, values));
    }

    public void gt(IObject<?> member, Serializable value) {
        addCriterion(PropertyCriterion.gt(member, value));
    }

    public void gt(IObject<?> member, IPrimitive<?> value) {
        addCriterion(PropertyCriterion.gt(member, value));
    }

    public void ge(IObject<?> member, Serializable value) {
        addCriterion(PropertyCriterion.ge(member, value));
    }

    public void ge(IObject<?> member, IPrimitive<?> value) {
        addCriterion(PropertyCriterion.ge(member, value));
    }

    public void lt(IObject<?> member, Serializable value) {
        addCriterion(PropertyCriterion.lt(member, value));
    }

    public void lt(IObject<?> member, IPrimitive<?> value) {
        addCriterion(PropertyCriterion.lt(member, value));
    }

    public void le(IObject<?> member, Serializable value) {
        addCriterion(PropertyCriterion.le(member, value));
    }

    public void le(IObject<?> member, IPrimitive<?> value) {
        addCriterion(PropertyCriterion.le(member, value));
    }

    public <T extends IVersionedEntity<?>> void isFinal(T entity) {
//         (entity.getPrimaryKey() != null) && !entity.getPrimaryKey().isDraft() && entity.version().toDate().isNull()
//                && !entity.version().fromDate().isNull();
    }

    public static <T extends IVersionedEntity<?>> void isDraft(T entity) {
//        return (entity.getPrimaryKey() == null) || entity.getPrimaryKey().isDraft();
    }

    public <T extends IVersionData<?>> void isDraft(T entity) {
        //      return (entity.getPrimaryKey() == null) || entity.fromDate().isNull();
    }
}

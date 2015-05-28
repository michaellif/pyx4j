/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Apr 13, 2015
 * @author vlads
 */
package com.pyx4j.entity.core.query;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

/**
 * TODO
 *
 * DateRange,
 *
 * IntegerRange,
 *
 * DecimalRange,
 *
 * Enum,
 *
 * Entity
 *
 */
@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
@Table(name = "query_storage_condition")
@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface ICondition extends IEntity {

    @Owner
    @ReadOnly
    @Detached
    @Indexed
    @JoinColumn
    @MemberColumn(notNull = true)
    QueryStorage owner();

    /**
     * The link to AbstractQueryCriteriaColumnStorage.
     *
     * TODO PYX-10 cross schema queries and constraints
     */
    @MemberColumn(notNull = true)
    IPrimitive<Key> columnId();

    interface DisplayOrderId extends ColumnId {
    }

    @OrderColumn(DisplayOrderId.class)
    IPrimitive<Integer> displayOrder();

    // MetaData required to build the UI for this Condition

    @Transient
    IPrimitive<Integer> title();

}

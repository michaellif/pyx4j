/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-03-21
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.rdb.dialect.Dialect;

public class MemberCollectionOrderMeta {

    private final boolean materialized;

    private final String sqlOrderColumnName;

    private final String orderMemberName;

    public MemberCollectionOrderMeta(String sqlOrderColumnName, String orderMemberName) {
        this.sqlOrderColumnName = sqlOrderColumnName;
        this.orderMemberName = orderMemberName;
        this.materialized = true;
    }

    MemberCollectionOrderMeta(Dialect dialect, MemberMeta orderMemberMeta) {
        this.sqlOrderColumnName = dialect.getNamingConvention().sqlFieldName(EntityOperationsMeta.memberPersistenceName(orderMemberMeta));
        this.orderMemberName = orderMemberMeta.getFieldName();
        this.materialized = (orderMemberMeta.getAnnotation(OrderColumn.class) != null);
    }

    public String sqlOrderColumnName() {
        return this.sqlOrderColumnName;
    }

    public boolean isOrderMaterialized() {
        return this.materialized;
    }

    public String orderMemberName() {
        return this.orderMemberName;
    }

    @Override
    public String toString() {
        return "materialized: " + materialized + ", sqlOrderColumnName: " + sqlOrderColumnName;
    }

    static MemberMeta findOrderMember(EntityMeta entityMeta, MemberMeta memberMeta, boolean strict, EntityMeta joinEntityMeta) {
        Class<? extends ColumnId> orderColumn = null;
        OrderBy orderBy = memberMeta.getAnnotation(OrderBy.class);
        if (orderBy != null) {
            orderColumn = orderBy.value();
        }
        MemberMeta orderMemberMeta = null;
        for (String jmemberName : joinEntityMeta.getMemberNamesWithPk()) {
            MemberMeta jmemberMeta = joinEntityMeta.getMemberMeta(jmemberName);
            if (!jmemberMeta.isTransient()) {
                OrderColumn joinTableOrderColumn = jmemberMeta.getAnnotation(OrderColumn.class);
                if (orderColumnMatch(strict, joinTableOrderColumn, orderColumn)) {
                    if (orderMemberMeta != null) {
                        throw new AssertionError("Duplicate @OrderColumn member in table " + joinEntityMeta.getEntityClass().getName() + " for "
                                + memberMeta.getFieldName() + " in " + entityMeta.getEntityClass().getName());
                    }

                    if (!jmemberMeta.getValueClass().equals(Integer.class)) {
                        throw new AssertionError("Expected Integer @OrderColumn in table " + joinEntityMeta.getEntityClass().getName() + " for "
                                + memberMeta.getFieldName() + " in " + entityMeta.getEntityClass().getName());
                    }
                    orderMemberMeta = jmemberMeta;
                } else {
                    MemberColumn joinTableMemberColumn = jmemberMeta.getAnnotation(MemberColumn.class);
                    if ((joinTableMemberColumn != null) && (orderColumn != ColumnId.class) && (orderColumn == joinTableMemberColumn.value())) {
                        if (orderMemberMeta != null) {
                            throw new AssertionError("Duplicate @MemberColumn member in table " + joinEntityMeta.getEntityClass().getName() + " for "
                                    + memberMeta.getFieldName() + " in " + entityMeta.getEntityClass().getName());
                        }
                        orderMemberMeta = jmemberMeta;
                    }
                }

            }
        }
        return orderMemberMeta;
    }

    private static boolean orderColumnMatch(boolean strict, OrderColumn joinTableOrderColumn, Class<? extends ColumnId> orderColumn) {
        if (joinTableOrderColumn == null) {
            return false;
        } else if (orderColumn == joinTableOrderColumn.value()) {
            return true;
        } else if ((!strict) && (orderColumn == null) && (joinTableOrderColumn.value() == ColumnId.class)) {
            return true;
        }
        return false;
    }
}

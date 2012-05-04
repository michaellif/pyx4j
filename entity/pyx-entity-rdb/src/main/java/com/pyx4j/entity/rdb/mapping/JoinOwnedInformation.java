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
 * Created on Feb 9, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class JoinOwnedInformation extends JoinInformation {

    private final MemberMeta memberMeta;

    private final EntityMeta entityMeta;

    // Relationship is managed in CHILD table using PARENT column.
    public JoinOwnedInformation(Dialect dialect, EntityMeta entityMeta, MemberMeta memberMeta, MemberMeta ownerMemberMeta) {
        this.entityMeta = entityMeta;
        this.memberMeta = memberMeta;
        @SuppressWarnings("unchecked")
        Class<? extends IEntity> childEntityClass = (Class<IEntity>) memberMeta.getValueClass();
        EntityMeta childEntityMeta = EntityFactory.getEntityMeta(childEntityClass);

        joinTableClass = childEntityClass;
        sqlName = TableModel.getTableName(dialect, childEntityMeta);
        sqlValueName = dialect.getNamingConvention().sqlIdColumnName();
        joinTableSameAsTarget = true;

        sqlOwnerName = dialect.getNamingConvention().sqlFieldName(EntityOperationsMeta.memberPersistenceName(ownerMemberMeta));
        ownerValueAdapter = EntityOperationsMeta.createEntityValueAdapter(dialect, ownerMemberMeta);

        if (memberMeta.getObjectClassType() == ObjectClassType.EntityList) {
            MemberMeta orderMemberMeta = findOrderMember(childEntityMeta);
            if (orderMemberMeta == null) {
                throw new AssertionError("Unmapped @OrderBy member in table " + childEntityClass.getName() + " for '" + memberMeta.getFieldName() + "' in "
                        + entityMeta.getEntityClass().getName() + "\n add @OrderColumn to " + childEntityClass);
            }
            sqlOrderColumnName = dialect.getNamingConvention().sqlFieldName(EntityOperationsMeta.memberPersistenceName(orderMemberMeta));
            orderMemeberName = orderMemberMeta.getFieldName();
        }
    }

    private MemberMeta findOrderMember(EntityMeta childEntityMeta) {
        Class<? extends ColumnId> orderColumn = null;
        OrderBy orderBy = memberMeta.getAnnotation(OrderBy.class);
        if (orderBy != null) {
            orderColumn = orderBy.value();
        }
        MemberMeta orderMemberMeta = null;
        for (String jmemberName : childEntityMeta.getMemberNames()) {
            MemberMeta jmemberMeta = childEntityMeta.getMemberMeta(jmemberName);
            if (!jmemberMeta.isTransient()) {
                OrderColumn joinTableOrderColumn = jmemberMeta.getAnnotation(OrderColumn.class);
                if (orderColumnMatch(joinTableOrderColumn, orderColumn)) {
                    if (orderMemberMeta != null) {
                        throw new AssertionError("Duplicate orderColumn member in table " + childEntityMeta.getEntityClass().getName() + " for "
                                + memberMeta.getFieldName() + " in " + entityMeta.getEntityClass().getName());
                    }

                    if ((!jmemberMeta.getValueClass().equals(Integer.class)) && (!jmemberMeta.getFieldName().equals(IEntity.PRIMARY_KEY))) {
                        throw new AssertionError("Expected Integer orderColumn in table " + childEntityMeta.getEntityClass().getName() + " for "
                                + memberMeta.getFieldName() + " in " + entityMeta.getEntityClass().getName());
                    }
                    orderMemberMeta = jmemberMeta;
                }

            }
        }
        return orderMemberMeta;
    }

    private static boolean orderColumnMatch(OrderColumn joinTableOrderColumn, Class<? extends ColumnId> orderColumn) {
        if (joinTableOrderColumn == null) {
            return false;
        } else if (orderColumn == joinTableOrderColumn.value()) {
            return true;
        } else if ((orderColumn == null) && (joinTableOrderColumn.value() == ColumnId.class)) {
            return true;
        }
        return false;
    }
}

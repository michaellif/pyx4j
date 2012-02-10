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
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

class JoinTableInformation extends JoinInformation {

    JoinTableInformation(Dialect dialect, NamingConvention namingConvention, EntityMeta rootEntityMeta, EntityMeta entityMeta, MemberMeta memberMeta,
            JoinTable joinTable) {
        @SuppressWarnings("unchecked")
        Class<? extends IEntity> entityClass = (Class<IEntity>) memberMeta.getValueClass();
        Class<? extends IEntity> joinEntityClass = joinTable.value();
        EntityMeta joinEntityMeta = EntityFactory.getEntityMeta(joinEntityClass);
        sqlName = TableModel.getTableName(dialect, joinEntityMeta);

        joinTableClass = joinEntityClass;

        if (joinEntityClass == entityClass) {
            sqlValueName = IEntity.PRIMARY_KEY;
            joinTableSameAsTarget = true;
        } else {
            sqlValueName = namingConvention.sqlFieldName(EntityOperationsMeta.memberPersistenceName(findValueMember(entityMeta, memberMeta, joinTable,
                    joinEntityMeta, entityClass)));
            joinTableSameAsTarget = false;
        }
        MemberMeta ownerMemberMeta = findOwnerMember(entityMeta, memberMeta, joinTable, joinEntityMeta, rootEntityMeta);
        sqlOwnerName = namingConvention.sqlFieldName(EntityOperationsMeta.memberPersistenceName(ownerMemberMeta));
        ownerValueAdapter = EntityOperationsMeta.createEntityValueAdapter(dialect, ownerMemberMeta);

        MemberMeta orderMemberMeta = findOrderMember(entityMeta, memberMeta, joinTable, joinEntityMeta);

        if (orderMemberMeta == null && memberMeta.getObjectClassType() == ObjectClassType.EntityList) {
            throw new Error("Unmapped orderColumn member in join table '" + joinEntityMeta.getCaption() + "' for " + memberMeta.getFieldName() + " in "
                    + entityMeta.getEntityClass().getName());
        } else if (orderMemberMeta != null) {
            sqlOrderColumnName = namingConvention.sqlFieldName(EntityOperationsMeta.memberPersistenceName(orderMemberMeta));
        }
    }

    private MemberMeta findOwnerMember(EntityMeta entityMeta, MemberMeta memberMeta, JoinTable joinTable, EntityMeta joinEntityMeta, EntityMeta rootEntityMeta) {
        MemberMeta ownerMemberMeta = null;

        Class<? extends IEntity> rootEntityClass = rootEntityMeta.getEntityClass();
        Table tableAnnotation = rootEntityMeta.getAnnotation(Table.class);
        if ((tableAnnotation != null) && (tableAnnotation.expands() != null)) {
            rootEntityClass = tableAnnotation.expands();
        }

        for (String jmemberName : joinEntityMeta.getMemberNames()) {
            MemberMeta jmemberMeta = joinEntityMeta.getMemberMeta(jmemberName);
            if (!jmemberMeta.isTransient()) {
                if ((joinTable.mappedby() == ColumnId.class)
                        && ((jmemberMeta.getObjectClass().equals(rootEntityMeta.getEntityClass())) || (jmemberMeta.getObjectClass().equals(rootEntityClass)))) {
                    ownerMemberMeta = jmemberMeta;
                } else {
                    JoinColumn joinColumn = jmemberMeta.getAnnotation(JoinColumn.class);
                    if (joinColumn != null) {
                        if (joinTable.mappedby() != ColumnId.class) {
                            if (joinColumn.value() == joinTable.mappedby()) {
                                ownerMemberMeta = jmemberMeta;
                                break;
                            }
                        } else {
                            if (joinColumn.value() == ColumnId.class) {
                                if ((jmemberMeta.getObjectClass().isAssignableFrom(rootEntityMeta.getEntityClass()))
                                        || (jmemberMeta.getObjectClass().isAssignableFrom(rootEntityClass))) {
                                    ownerMemberMeta = jmemberMeta;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (ownerMemberMeta == null) {
            throw new Error("Unmapped owner member in join table '" + joinEntityMeta.getCaption() + "' for " + memberMeta.getFieldName() + " in "
                    + entityMeta.getEntityClass().getName());
        } else {
            return ownerMemberMeta;
        }
    }

    private MemberMeta findValueMember(EntityMeta entityMeta, MemberMeta memberMeta, JoinTable joinTable, EntityMeta joinEntityMeta,
            Class<? extends IEntity> entityClass) {
        MemberMeta valueMemberMeta = null;

        for (String jmemberName : joinEntityMeta.getMemberNames()) {
            MemberMeta jmemberMeta = joinEntityMeta.getMemberMeta(jmemberName);
            if (!jmemberMeta.isTransient()) {
                if (jmemberMeta.getObjectClass().equals(entityClass)) {
                    valueMemberMeta = jmemberMeta;
                }
            }
        }
        if (valueMemberMeta == null) {
            throw new Error("Unmapped value member '" + entityClass.getName() + "' in join table '" + joinEntityMeta.getCaption() + "' for "
                    + memberMeta.getFieldName() + " in " + entityMeta.getEntityClass().getName());
        } else {
            return valueMemberMeta;
        }
    }

    private MemberMeta findOrderMember(EntityMeta entityMeta, MemberMeta memberMeta, JoinTable joinTable, EntityMeta joinEntityMeta) {
        MemberMeta orderMemberMeta = null;
        for (String jmemberName : joinEntityMeta.getMemberNames()) {
            MemberMeta jmemberMeta = joinEntityMeta.getMemberMeta(jmemberName);
            if (!jmemberMeta.isTransient()) {
                OrderColumn joinTableOrderColumn = jmemberMeta.getAnnotation(OrderColumn.class);
                if ((joinTableOrderColumn != null) && (joinTable.orderColumn() != ColumnId.class) && (joinTable.orderColumn() == joinTableOrderColumn.value())) {
                    if (orderMemberMeta != null) {
                        throw new Error("Duplicate orderColumn member in join table '" + joinEntityMeta.getCaption() + "' for " + memberMeta.getFieldName()
                                + " in " + entityMeta.getEntityClass().getName());
                    }
                    if (jmemberMeta.getObjectClass().equals(Integer.class)) {
                        throw new Error("Expected Integer orderColumn in join table '" + joinEntityMeta.getCaption() + "' for " + memberMeta.getFieldName()
                                + " in " + entityMeta.getEntityClass().getName());
                    }
                    orderMemberMeta = jmemberMeta;
                }
            }
        }
        return orderMemberMeta;
    }

}
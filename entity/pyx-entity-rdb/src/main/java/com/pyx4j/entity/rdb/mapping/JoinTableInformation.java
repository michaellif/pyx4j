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
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.rdb.dialect.Dialect;

class JoinTableInformation extends JoinInformation {

    private final EntityMeta rootEntityMeta;

    private final EntityMeta entityMeta;

    private final MemberMeta memberMeta;

    private final JoinTable joinTable;

    JoinTableInformation(Dialect dialect, EntityMeta rootEntityMeta, EntityMeta entityMeta, MemberMeta memberMeta) {
        this.rootEntityMeta = rootEntityMeta;
        this.entityMeta = entityMeta;
        this.memberMeta = memberMeta;
        this.joinTable = memberMeta.getAnnotation(JoinTable.class);

        @SuppressWarnings("unchecked")
        Class<? extends IEntity> entityClass = (Class<IEntity>) memberMeta.getValueClass();
        Class<? extends IEntity> joinEntityClass = joinTable.value();
        EntityMeta joinEntityMeta = EntityFactory.getEntityMeta(joinEntityClass);
        sqlName = TableModel.getTableName(dialect, joinEntityMeta);

        joinTableClass = joinEntityClass;

        if (joinEntityClass == entityClass) {
            sqlValueName = dialect.getNamingConvention().sqlIdColumnName();
            joinTableSameAsTarget = true;
        } else {
            sqlValueName = dialect.getNamingConvention().sqlFieldName(EntityOperationsMeta.memberPersistenceName(findValueMember(joinEntityMeta, entityClass)));
            joinTableSameAsTarget = false;
        }
        MemberMeta ownerMemberMeta = findOwnerMember(joinEntityMeta);
        sqlOwnerName = dialect.getNamingConvention().sqlFieldName(EntityOperationsMeta.memberPersistenceName(ownerMemberMeta));
        ownerValueAdapter = EntityOperationsMeta.createEntityValueAdapter(dialect, rootEntityMeta, ownerMemberMeta);

        MemberMeta orderMemberMeta = MemberCollectionOrderMeta.findOrderMember(entityMeta, memberMeta, true, joinEntityMeta);

        if (orderMemberMeta == null && memberMeta.getObjectClassType() == ObjectClassType.EntityList) {
            throw new AssertionError("Unmapped @OrderBy member in join table " + joinEntityMeta.getEntityClass().getName() + " for '"
                    + memberMeta.getFieldName() + "' in " + entityMeta.getEntityClass().getName());
        } else if (orderMemberMeta != null) {
            collectionOrderMeta = new MemberCollectionOrderMeta(dialect, orderMemberMeta);
        }
    }

    private MemberMeta findOwnerMember(EntityMeta joinEntityMeta) {
        MemberMeta ownerMemberMeta = null;

        Class<? extends IEntity> rootEntityClass = rootEntityMeta.getEntityClass();
        Table tableAnnotation = rootEntityMeta.getAnnotation(Table.class);
        if ((tableAnnotation != null) && (tableAnnotation.expands() != IEntity.class)) {
            rootEntityClass = tableAnnotation.expands();
        }

        for (String jmemberName : joinEntityMeta.getMemberNames()) {
            MemberMeta jmemberMeta = joinEntityMeta.getMemberMeta(jmemberName);
            if (!jmemberMeta.isTransient()) {
                if ((joinTable.mappedBy() == ColumnId.class)
                        && ((jmemberMeta.getObjectClass().equals(rootEntityMeta.getEntityClass())) || (jmemberMeta.getObjectClass().equals(rootEntityClass)))) {
                    ownerMemberMeta = jmemberMeta;
                } else {
                    JoinColumn joinColumn = jmemberMeta.getAnnotation(JoinColumn.class);
                    if (joinColumn != null) {
                        if (joinTable.mappedBy() != ColumnId.class) {
                            if (joinColumn.value() == joinTable.mappedBy()) {
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
            throw new AssertionError("Unmapped owner member in join table '" + joinEntityMeta.getCaption() + "' for '" + memberMeta.getFieldName() + "' in "
                    + entityMeta.getEntityClass().getName());
        } else {
            return ownerMemberMeta;
        }
    }

    private MemberMeta findValueMember(EntityMeta joinEntityMeta, Class<? extends IEntity> entityClass) {
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
            throw new AssertionError("Unmapped value member '" + entityClass.getName() + "' in join table '" + joinEntityMeta.getCaption() + "' for "
                    + memberMeta.getFieldName() + " in " + entityMeta.getEntityClass().getName());
        } else {
            return valueMemberMeta;
        }
    }

}
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
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class JoinOwnerInformation extends JoinInformation {

    public JoinOwnerInformation(Dialect dialect, NamingConvention namingConvention, EntityMeta rootEntityMeta, EntityMeta entityMeta, MemberMeta memberMeta,
            Owner owner) {

        @SuppressWarnings("unchecked")
        Class<? extends IEntity> ownerEntityClass = (Class<IEntity>) memberMeta.getValueClass();
        EntityMeta ownerEntityMeta = EntityFactory.getEntityMeta(ownerEntityClass);

        joinTableClass = ownerEntityClass;

        sqlName = TableModel.getTableName(dialect, ownerEntityMeta);
        sqlValueName = IEntity.PRIMARY_KEY;
        joinTableSameAsTarget = true;

        MemberMeta ownedMemberMeta = findOwnedMember(entityMeta, memberMeta, rootEntityMeta, owner, ownerEntityMeta);
        sqlOwnerName = namingConvention.sqlFieldName(EntityOperationsMeta.memberPersistenceName(ownedMemberMeta));
        ownerValueAdapter = EntityOperationsMeta.createEntityValueAdapter(dialect, ownedMemberMeta);
    }

    private MemberMeta findOwnedMember(EntityMeta entityMeta, MemberMeta memberMeta, EntityMeta rootEntityMeta, Owner owner, EntityMeta ownerEntityMeta) {
        MemberMeta ownerMemberMeta = null;

        Class<? extends IEntity> rootEntityClass = rootEntityMeta.getEntityClass();
        Table tableAnnotation = rootEntityMeta.getAnnotation(Table.class);
        if ((tableAnnotation != null) && (tableAnnotation.expands() != null)) {
            rootEntityClass = tableAnnotation.expands();
        }

        //TODO Owned collections

        for (String jmemberName : ownerEntityMeta.getMemberNames()) {
            MemberMeta jmemberMeta = ownerEntityMeta.getMemberMeta(jmemberName);
            if (!jmemberMeta.isTransient() && (jmemberMeta.getAnnotation(Owned.class) != null)) {
                if ((owner.mappedby() == ColumnId.class)
                        && ((jmemberMeta.getObjectClass().equals(rootEntityMeta.getEntityClass())) || (jmemberMeta.getObjectClass().equals(rootEntityClass)))) {
                    ownerMemberMeta = jmemberMeta;
                } else {
                    JoinColumn joinColumn = jmemberMeta.getAnnotation(JoinColumn.class);
                    if (joinColumn != null) {
                        if (owner.mappedby() != ColumnId.class) {
                            if (joinColumn.value() == owner.mappedby()) {
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
            throw new Error("Unmapped @Owned member in join table '" + ownerEntityMeta.getCaption() + "' for " + memberMeta.getFieldName() + " in "
                    + entityMeta.getEntityClass().getName());
        } else {
            return ownerMemberMeta;
        }
    }

}

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

import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

abstract class JoinInformation {

    //Used to initialize the second entity
    Class<? extends IEntity> joinTableClass;

    boolean joinTableSameAsTarget;

    String sqlName;

    String sqlValueName = null;

    String sqlOwnerName = null;

    String sqlOrderColumnName = null;

    ValueAdapter ownerValueAdapter;

    static JoinInformation build(Dialect dialect, NamingConvention namingConvention, EntityMeta rootEntityMeta, EntityMeta entityMeta, MemberMeta memberMeta) {
        if (memberMeta.getAnnotation(JoinTable.class) != null) {
            return new JoinTableInformation(dialect, rootEntityMeta, entityMeta, memberMeta);
        }
        if (Owned.TODO) {
            //Disable all new Owned/Owner mapping until it is tested
            return null;
        }

        Owner owner = memberMeta.getAnnotation(Owner.class);
        if (owner != null) {
            JoinColumn joinColumn = memberMeta.getAnnotation(JoinColumn.class);
            if ((joinColumn != null) && (memberMeta.getObjectClassType() == ObjectClassType.Entity)) {
                // One-to-One, @Owned mappedBy this Column.
                return null;
            } else {
                return new JoinOwnerInformation(dialect, namingConvention, rootEntityMeta, entityMeta, memberMeta, owner);
            }
        }
        Owned owned = memberMeta.getAnnotation(Owned.class);
        if (owned != null) {
            @SuppressWarnings("unchecked")
            Class<? extends IEntity> childEntityClass = (Class<IEntity>) memberMeta.getValueClass();
            EntityMeta childEntityMeta = EntityFactory.getEntityMeta(childEntityClass);
            MemberMeta ownerMemberMeta = findOwnerMember(childEntityMeta, memberMeta, rootEntityMeta);
            if (ownerMemberMeta == null) {
                return null;
            }
            JoinColumn joinColumn = ownerMemberMeta.getAnnotation(JoinColumn.class);
            if (joinColumn == null) {
                // @Owned mappedBy this Column or autoGenerated table
                return null;
            } else {
                return new JoinOwnedInformation(dialect, namingConvention, entityMeta, memberMeta, ownerMemberMeta);
            }
        }
        return null;
    }

    private static MemberMeta findOwnerMember(EntityMeta childEntityMeta, MemberMeta memberMeta, EntityMeta rootEntityMeta) {
        Class<? extends IEntity> rootEntityClass = rootEntityMeta.getEntityClass();
        Table tableAnnotation = rootEntityMeta.getAnnotation(Table.class);
        if ((tableAnnotation != null) && (tableAnnotation.expands() != IEntity.class)) {
            rootEntityClass = tableAnnotation.expands();
        }
        MemberMeta ownerMemberMeta = null;
        for (String jmemberName : childEntityMeta.getMemberNames()) {
            MemberMeta jmemberMeta = childEntityMeta.getMemberMeta(jmemberName);
            if (!jmemberMeta.isTransient() && (jmemberMeta.getAnnotation(Owner.class) != null)) {
                if ((jmemberMeta.getObjectClass().equals(rootEntityMeta.getEntityClass())) || (jmemberMeta.getObjectClass().equals(rootEntityClass))) {
                    if (ownerMemberMeta != null) {
                        throw new AssertionError("Duplicate @Owner member in table " + childEntityMeta.getEntityClass().getName() + " for "
                                + memberMeta.getFieldName() + " of type " + memberMeta.getValueClass().getName());
                    }
                    ownerMemberMeta = jmemberMeta;
                } else {
                    throw new AssertionError("Invalid type @Owner member '" + jmemberName + "' in table " + childEntityMeta.getEntityClass().getName()
                            + " for " + memberMeta.getFieldName() + " of type " + memberMeta.getValueClass().getName());
                }
            }
        }
        return ownerMemberMeta;
    }

}

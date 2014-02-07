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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.ManagedColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.rdb.dialect.Dialect;

abstract class JoinInformation {

    //Used to initialize the second entity
    Class<? extends IEntity> joinTableClass;

    boolean joinTableSameAsTarget;

    String sqlName;

    String sqlValueName = null;

    String sqlOwnerName = null;

    MemberCollectionOrderMeta collectionOrderMeta = null;

    String sqlChildJoinContition = null;

    ValueAdapter ownerValueAdapter;

    static JoinInformation build(Dialect dialect, EntityMeta rootEntityMeta, EntityMeta entityMeta, MemberMeta memberMeta) {
        if (memberMeta.getAnnotation(ManagedColumn.class) != null) {
            if (!IVersionData.class.isAssignableFrom(memberMeta.getObjectClass())) {
                throw new Error("Only versioned ManagedColumn are supported '" + memberMeta.getFieldName() + "' in table "
                        + entityMeta.getEntityClass().getName());
            }
            return new JoinVersionDataInformation(dialect, rootEntityMeta, entityMeta, memberMeta);
        }

        if (memberMeta.getAnnotation(JoinTable.class) != null) {
            assert ((memberMeta.getAnnotation(Owner.class) == null) && (memberMeta.getAnnotation(Owned.class) == null)) : "Incompatible @JoinTable and @Owned/@Owner on '"
                    + memberMeta.getFieldName() + "' in table " + entityMeta.getEntityClass().getName();
            return new JoinTableInformation(dialect, rootEntityMeta, entityMeta, memberMeta);
        }

        Owner owner = memberMeta.getAnnotation(Owner.class);
        if (owner != null) {
            JoinColumn joinColumn = memberMeta.getAnnotation(JoinColumn.class);
            if ((joinColumn != null) && (memberMeta.getObjectClassType() == ObjectClassType.Entity)) {
                // One-to-One, @Owned mappedBy this Column.
                return null;
            } else {
                return new JoinOwnerInformation(dialect, rootEntityMeta, entityMeta, memberMeta, owner);
            }
        }
        Owned owned = memberMeta.getAnnotation(Owned.class);
        if (owned != null) {
            @SuppressWarnings("unchecked")
            Class<? extends IEntity> childEntityClass = (Class<IEntity>) memberMeta.getValueClass();
            EntityMeta childEntityMeta = EntityFactory.getEntityMeta(childEntityClass);
            MemberMeta ownerMemberMeta = findOwnerMember(childEntityMeta, memberMeta, rootEntityMeta, entityMeta);
            if (ownerMemberMeta == null) {
                return null;
            }
            JoinColumn joinColumn = ownerMemberMeta.getAnnotation(JoinColumn.class);
            if (joinColumn == null) {
                // @Owned mappedBy this Column or autoGenerated table
                return null;
            } else {
                return new JoinOwnedInformation(dialect, entityMeta, memberMeta, ownerMemberMeta);
            }
        }
        return null;
    }

    protected static MemberMeta findOwnerMember(EntityMeta childEntityMeta, MemberMeta memberMeta, EntityMeta rootEntityMeta, EntityMeta entityMeta) {
        Class<? extends IEntity> rootEntityClass = rootEntityMeta.getEntityClass();
        Table tableAnnotation = rootEntityMeta.getAnnotation(Table.class);
        if ((tableAnnotation != null) && (tableAnnotation.expands() != IEntity.class)) {
            rootEntityClass = tableAnnotation.expands();
        }
        MemberMeta ownerMemberMeta = null;
        for (String jmemberName : childEntityMeta.getMemberNames()) {
            MemberMeta jmemberMeta = childEntityMeta.getMemberMeta(jmemberName);
            if (!jmemberMeta.isTransient() && (jmemberMeta.getAnnotation(Owner.class) != null)) {
                @SuppressWarnings("unchecked")
                Class<? extends IEntity> entityClass = (Class<IEntity>) jmemberMeta.getObjectClass();
                if (ownerClassMatch(entityClass, rootEntityMeta.getEntityClass(), rootEntityClass)) {
                    if (ownerMemberMeta != null) {
                        throw new AssertionError("Duplicate @Owner member in table " + childEntityMeta.getEntityClass().getName() + " for "
                                + memberMeta.getFieldName() + " of type " + memberMeta.getValueClass().getName());
                    }
                    ownerMemberMeta = jmemberMeta;
                } else {
                    throw new AssertionError("Invalid type @Owner member '" + jmemberName + "' in table " + childEntityMeta.getEntityClass().getName()
                            + " for '" + memberMeta.getFieldName() + "' of type " + entityMeta.getEntityClass().getName() + ",\n expected "
                            + rootEntityClass.getName() + ", got " + entityClass.getName());
                }
            }
        }
        return ownerMemberMeta;
    }

    private static boolean ownerClassMatch(Class<?> memberClass, Class<?>... entityClasses) {
        if (memberClass.getAnnotation(Inheritance.class) != null) {
            for (Class<?> entityClass : entityClasses) {
                if (memberClass.isAssignableFrom(entityClass)) {
                    return true;
                }
            }
        } else {
            for (Class<?> entityClass : entityClasses) {
                if (memberClass.isAssignableFrom(entityClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static String buildChildJoinContition(Dialect dialect, Class<? extends IEntity> entityClass) {
        List<String> discriminatorStrings = new ArrayList<String>();
        for (Class<? extends IEntity> subclass : Mappings.getPersistableAssignableFrom(entityClass)) {
            DiscriminatorValue discriminator = subclass.getAnnotation(DiscriminatorValue.class);
            if (discriminator != null) {
                discriminatorStrings.add(discriminator.value());
            }
        }
        if (discriminatorStrings.size() == 1) {
            return dialect.sqlDiscriminatorColumnName() + " = '" + discriminatorStrings.get(0) + "'";
        } else {
            StringBuilder sqlChildJoinContition = new StringBuilder();
            sqlChildJoinContition.append(dialect.sqlDiscriminatorColumnName()).append(" IN (");
            boolean first = true;
            for (String desc : discriminatorStrings) {
                if (first) {
                    first = false;
                } else {
                    sqlChildJoinContition.append(",");
                }
                sqlChildJoinContition.append("'" + desc + "'");
            }
            sqlChildJoinContition.append(") ");
            return sqlChildJoinContition.toString();
        }
    }

}

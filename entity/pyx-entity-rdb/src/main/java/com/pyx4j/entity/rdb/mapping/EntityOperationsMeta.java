/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 2, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.adapters.IndexAdapter;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.server.AdapterFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

/**
 * Categorize member types for later use in the frequent update/select operations
 */
public class EntityOperationsMeta {

    private final List<MemberOperationsMeta> allMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> columnMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> cascadePersistMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> cascadeDeleteMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> cascadeRetrieveMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> collectionMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> indexMembers = new Vector<MemberOperationsMeta>();

    EntityOperationsMeta(NamingConvention namingConvention, EntityMeta entityMeta) {
        build(namingConvention, null, null, entityMeta);
    }

    private void build(NamingConvention namingConvention, List<String> accessPath, List<String> namesPath, EntityMeta entityMeta) {
        for (String memberName : entityMeta.getMemberNames()) {
            MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            String memberPersistenceName = memberName;
            MemberColumn memberColumn = memberMeta.getAnnotation(MemberColumn.class);
            if ((memberColumn != null) && (CommonsStringUtils.isStringSet(memberColumn.name()))) {
                memberPersistenceName = memberColumn.name();
            }

            if (memberMeta.isEmbedded()) {
                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    //TODO Embedded collections
                } else {
                    List<String> accessPathChild = new Vector<String>();
                    if (accessPath != null) {
                        accessPathChild.addAll(accessPath);
                    }
                    accessPathChild.add(memberName);

                    List<String> namesPathChild = new Vector<String>();
                    if (namesPath != null) {
                        namesPathChild.addAll(namesPath);
                    }
                    namesPathChild.add(memberPersistenceName);

                    build(namingConvention, accessPathChild, namesPathChild, EntityFactory.getEntityMeta((Class<IEntity>) memberMeta.getObjectClass()));
                }
            } else {

                EntityMemberAccess memberAccess;
                if (accessPath == null) {
                    memberAccess = new EntityMemberDirectAccess(memberName);
                } else {
                    memberAccess = new EntityMemberEmbeddedAccess(accessPath, memberName);
                }

                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    String sqlName;
                    if (namesPath != null) {
                        sqlName = namingConvention.sqlEmbededTableName(entityMeta.getPersistenceName(), namesPath, memberPersistenceName);
                    } else {
                        sqlName = namingConvention.sqlChildTableName(entityMeta.getPersistenceName(), memberPersistenceName);
                    }
                    MemberOperationsMeta member = new MemberOperationsMeta(memberAccess, sqlName, memberMeta);
                    collectionMembers.add(member);
                    allMembers.add(member);
                    if (!memberMeta.isDetached()) {
                        cascadeRetrieveMembers.add(member);
                    }
                } else if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    String sqlName;
                    if (namesPath != null) {
                        sqlName = namingConvention.sqlEmbededFieldName(namesPath, memberPersistenceName);
                    } else {
                        sqlName = namingConvention.sqlFieldName(memberPersistenceName);
                    }
                    MemberOperationsMeta member = new MemberOperationsMeta(memberAccess, sqlName, memberMeta);
                    columnMembers.add(member);
                    allMembers.add(member);
                    if (memberMeta.isOwnedRelationships()) {
                        cascadePersistMembers.add(member);
                        cascadeDeleteMembers.add(member);
                    } else if (memberMeta.getAnnotation(Reference.class) != null) {
                        cascadePersistMembers.add(member);
                    }
                    if (!memberMeta.isDetached()) {
                        cascadeRetrieveMembers.add(member);
                    }
                } else if (IPrimitiveSet.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    String sqlName;
                    if (namesPath != null) {
                        sqlName = namingConvention.sqlEmbededTableName(entityMeta.getPersistenceName(), namesPath, memberPersistenceName);
                    } else {
                        sqlName = namingConvention.sqlChildTableName(entityMeta.getPersistenceName(), memberPersistenceName);
                    }
                    MemberOperationsMeta member = new MemberOperationsMeta(memberAccess, sqlName, memberMeta);
                    collectionMembers.add(member);
                } else {
                    String sqlName;
                    if (namesPath != null) {
                        sqlName = namingConvention.sqlEmbededFieldName(namesPath, memberPersistenceName);
                    } else {
                        sqlName = namingConvention.sqlFieldName(memberPersistenceName);
                    }
                    MemberOperationsMeta member = new MemberOperationsMeta(memberAccess, sqlName, memberMeta);
                    columnMembers.add(member);
                    allMembers.add(member);
                }

                Indexed index = memberMeta.getAnnotation(Indexed.class);
                if ((index != null) && (index.adapters() != null) && (index.adapters().length > 0)) {
                    for (Class<? extends IndexAdapter<?>> adapterClass : index.adapters()) {
                        IndexAdapter<?> adapter = AdapterFactory.getIndexAdapter(adapterClass);
                        String indexedPropertyName = namingConvention.sqlFieldName(adapter.getIndexedColumnName(null, memberMeta));
                        indexMembers.add(new MemberOperationsMeta(memberAccess, indexedPropertyName, memberMeta, adapterClass, adapter.getIndexValueClass()));
                    }
                }
            }
        }
    }

    public List<MemberOperationsMeta> getAllMembers() {
        return allMembers;
    }

    public List<MemberOperationsMeta> getColumnMembers() {
        return columnMembers;
    }

    public List<MemberOperationsMeta> getCascadePersistMembers() {
        return cascadePersistMembers;
    }

    public List<MemberOperationsMeta> getCascadeDeleteMembers() {
        return cascadeDeleteMembers;
    }

    public List<MemberOperationsMeta> getCascadeRetrieveMembers() {
        return cascadeRetrieveMembers;
    }

    public List<MemberOperationsMeta> getCollectionMembers() {
        return collectionMembers;
    }

    public MemberOperationsMeta getCollectionMember(String memberName) {
        for (MemberOperationsMeta m : collectionMembers) {
            if (m.getMemberName().equals(memberName)) {
                return m;
            }
        }
        return null;
    }

    public List<MemberOperationsMeta> getIndexMembers() {
        return indexMembers;
    }

}

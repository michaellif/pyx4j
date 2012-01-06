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

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.adapters.IndexAdapter;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.server.AdapterFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.geo.GeoPoint;

/**
 * Categorize member types for later use in the frequent update/select operations
 */
public class EntityOperationsMeta {

    private final EntityMeta mainEntityMeta;

    private final List<MemberOperationsMeta> allMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> columnMembers = new Vector<MemberOperationsMeta>();

    private final Map<String, MemberOperationsMeta> membersByPath = new HashMap<String, MemberOperationsMeta>();

    private final List<MemberOperationsMeta> cascadePersistMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> cascadeDeleteMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> cascadeRetrieveMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> collectionMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> joinCollectionMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> indexMembers = new Vector<MemberOperationsMeta>();

    private final Mappings mappings;

    EntityOperationsMeta(Dialect dialect, Mappings mappings, EntityMeta entityMeta) {
        mainEntityMeta = entityMeta;
        String path = GWTJava5Helper.getSimpleName(entityMeta.getEntityClass());
        build(dialect, dialect.getNamingConvention(), entityMeta, path, null, null, entityMeta);
        this.mappings = mappings;

        // Create meta for PK
        {
            MemberMeta memberMeta = entityMeta.getMemberMeta(IEntity.PRIMARY_KEY);
            ValueAdapter valueAdapter = createValueAdapter(dialect, memberMeta);
            MemberOperationsMeta member = new MemberOperationsMeta(new EntityMemberDirectAccess(IEntity.PRIMARY_KEY), valueAdapter, IEntity.PRIMARY_KEY,
                    memberMeta, path + Path.PATH_SEPARATOR + IEntity.PRIMARY_KEY + Path.PATH_SEPARATOR);
            membersByPath.put(member.getMemberPath(), member);
        }
    }

    public EntityMeta entityMeta() {
        return mainEntityMeta;
    }

    public EntityOperationsMeta getMappedOperationsMeta(Connection connection, Class<? extends IEntity> entityClass) {
        return mappings.getTableModel(connection, entityClass).operationsMeta();
    }

    private String memberPersistenceName(MemberMeta memberMeta) {
        MemberColumn memberColumn = memberMeta.getAnnotation(MemberColumn.class);
        if ((memberColumn != null) && (CommonsStringUtils.isStringSet(memberColumn.name()))) {
            return memberColumn.name();
        } else {
            return memberMeta.getFieldName();
        }
    }

    private void build(Dialect dialect, NamingConvention namingConvention, EntityMeta rootEntityMeta, String path, List<String> accessPath,
            List<String> namesPath, EntityMeta entityMeta) {
        String ownerMemberName = entityMeta.getOwnerMemberName();
        for (String memberName : entityMeta.getMemberNames()) {
            MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            String memberPersistenceName = memberPersistenceName(memberMeta);

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

                    @SuppressWarnings("unchecked")
                    Class<? extends IEntity> entityClass = (Class<IEntity>) memberMeta.getObjectClass();
                    build(dialect, namingConvention, rootEntityMeta, path + Path.PATH_SEPARATOR + memberName, accessPathChild, namesPathChild,
                            EntityFactory.getEntityMeta(entityClass));
                }
            } else {
                EntityMemberAccess memberAccess;
                if (accessPath == null) {
                    memberAccess = new EntityMemberDirectAccess(memberName);
                } else {
                    memberAccess = new EntityMemberEmbeddedAccess(accessPath, memberName);
                }
                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {

                    @SuppressWarnings("unchecked")
                    Class<? extends IEntity> entityClass = (Class<IEntity>) memberMeta.getValueClass();
                    ValueAdapter valueAdapter;
                    if (entityClass.getAnnotation(Inheritance.class) != null) {
                        valueAdapter = new ValueAdapterEntityVirtual(dialect, entityClass);
                    } else {
                        valueAdapter = new ValueAdapterEntity(dialect, entityClass);
                    }

                    JoinTable joinTable = memberMeta.getAnnotation(JoinTable.class);
                    MemberOperationsMeta member;
                    if (joinTable != null) {
                        Class<? extends IEntity> joinEntity = joinTable.value();
                        EntityMeta joinEntityMeta = EntityFactory.getEntityMeta(joinEntity);
                        String sqlName = TableModel.getTableName(dialect, joinEntityMeta);

                        String sqlValueName = null;
                        String sqlOwnerName = null;
                        for (String jmemberName : joinEntityMeta.getMemberNames()) {
                            MemberMeta jmemberMeta = joinEntityMeta.getMemberMeta(jmemberName);
                            if (!jmemberMeta.isTransient()) {
                                if (jmemberMeta.getObjectClass().equals(entityClass)) {
                                    sqlValueName = namingConvention.sqlFieldName(memberPersistenceName(jmemberMeta));
                                } else if (jmemberMeta.getObjectClass().equals(rootEntityMeta.getEntityClass())) {
                                    sqlOwnerName = namingConvention.sqlFieldName(memberPersistenceName(jmemberMeta));
                                }
                            }
                        }
                        if (sqlValueName == null) {
                            throw new Error("Unmapped value member '" + entityClass + "' in table '" + joinEntityMeta.getCaption() + "'");
                        }
                        if (sqlOwnerName == null) {
                            throw new Error("Unmapped owner member '" + entityClass + "' in table '" + joinEntityMeta.getCaption() + "'");
                        }

                        member = new MemberCollectionOperationsMeta(memberAccess, valueAdapter, sqlName, memberMeta, path + Path.PATH_SEPARATOR + memberName
                                + Path.PATH_SEPARATOR, sqlOwnerName, sqlValueName);
                        joinCollectionMembers.add(member);
                    } else {
                        String sqlName;
                        if (namesPath != null) {
                            sqlName = namingConvention.sqlEmbededTableName(rootEntityMeta.getPersistenceName(), namesPath, memberPersistenceName);
                        } else {
                            sqlName = namingConvention.sqlChildTableName(rootEntityMeta.getPersistenceName(), memberPersistenceName);
                        }
                        member = new MemberCollectionOperationsMeta(memberAccess, valueAdapter, sqlName, memberMeta, path + Path.PATH_SEPARATOR + memberName
                                + Path.PATH_SEPARATOR, "owner", "value");
                        collectionMembers.add(member);
                        if (!memberMeta.isDetached()) {
                            cascadeRetrieveMembers.add(member);
                        }
                    }
                    membersByPath.put(member.getMemberPath(), member);
                    allMembers.add(member);
                } else if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    String sqlName;
                    if (namesPath != null) {
                        sqlName = namingConvention.sqlEmbededFieldName(namesPath, memberPersistenceName);
                    } else {
                        sqlName = namingConvention.sqlFieldName(memberPersistenceName);
                    }
                    @SuppressWarnings("unchecked")
                    Class<? extends IEntity> entityClass = (Class<IEntity>) memberMeta.getObjectClass();
                    ValueAdapter valueAdapter;
                    if (entityClass.getAnnotation(Inheritance.class) != null) {
                        valueAdapter = new ValueAdapterEntityVirtual(dialect, entityClass);
                    } else {
                        valueAdapter = new ValueAdapterEntity(dialect, entityClass);
                    }
                    MemberOperationsMeta member = new MemberOperationsMeta(memberAccess, valueAdapter, sqlName, memberMeta, path + Path.PATH_SEPARATOR
                            + memberName + Path.PATH_SEPARATOR, memberName.equals(ownerMemberName));

                    columnMembers.add(member);
                    membersByPath.put(member.getMemberPath(), member);
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
                        sqlName = namingConvention.sqlEmbededTableName(rootEntityMeta.getPersistenceName(), namesPath, memberPersistenceName);
                    } else {
                        sqlName = namingConvention.sqlChildTableName(rootEntityMeta.getPersistenceName(), memberPersistenceName);
                    }
                    ValueAdapter valueAdapter = createValueAdapter(dialect, memberMeta);
                    if (valueAdapter == null) {
                        throw new Error("Unsupported IPrimitive<" + memberMeta.getValueClass().getName() + "> " + memberName + " in "
                                + entityMeta.getEntityClass().getName());
                    }
                    MemberOperationsMeta member = new MemberOperationsMeta(memberAccess, valueAdapter, sqlName, memberMeta, path + Path.PATH_SEPARATOR
                            + memberName + Path.PATH_SEPARATOR);
                    collectionMembers.add(member);
                    membersByPath.put(member.getMemberPath(), member);
                } else {
                    String sqlName;
                    if (namesPath != null) {
                        sqlName = namingConvention.sqlEmbededFieldName(namesPath, memberPersistenceName);
                    } else {
                        sqlName = namingConvention.sqlFieldName(memberPersistenceName);
                    }
                    ValueAdapter valueAdapter = createValueAdapter(dialect, memberMeta);
                    if (valueAdapter == null) {
                        throw new Error("Unsupported IPrimitive<" + memberMeta.getValueClass().getName() + "> " + memberName + " in "
                                + entityMeta.getEntityClass().getName());
                    }
                    MemberOperationsMeta member = new MemberOperationsMeta(memberAccess, valueAdapter, sqlName, memberMeta, path + Path.PATH_SEPARATOR
                            + memberName + Path.PATH_SEPARATOR);
                    columnMembers.add(member);
                    membersByPath.put(member.getMemberPath(), member);
                    allMembers.add(member);
                }

                Indexed index = memberMeta.getAnnotation(Indexed.class);
                if ((index != null) && (index.adapters() != null) && (index.adapters().length > 0)) {
                    for (Class<? extends IndexAdapter<?>> adapterClass : index.adapters()) {
                        IndexAdapter<?> adapter = AdapterFactory.getIndexAdapter(adapterClass);
                        String indexedPropertyName = namingConvention.sqlFieldName(adapter.getIndexedColumnName(null, memberMeta));
                        indexMembers.add(new MemberOperationsMeta(memberAccess, null, indexedPropertyName, memberMeta, null, adapterClass, adapter
                                .getIndexValueClass(), false));
                    }
                }
            }
        }
    }

    private ValueAdapter createValueAdapter(Dialect dialect, MemberMeta memberMeta) {
        Class<?> valueClass = memberMeta.getValueClass();
        if (valueClass.equals(String.class)) {
            return new ValueAdapterString(dialect, memberMeta);
        } else if (valueClass.equals(Double.class)) {
            return new ValueAdapterDouble(dialect);
        } else if (valueClass.equals(Float.class)) {
            return new ValueAdapterFloat(dialect);
        } else if (valueClass.equals(Long.class)) {
            return new ValueAdapterLong(dialect);
        } else if (valueClass.equals(Key.class)) {
            return new ValueAdapterKey(dialect);
        } else if (valueClass.equals(Integer.class)) {
            return new ValueAdapterInteger(dialect);
        } else if (valueClass.equals(java.sql.Date.class)) {
            return new ValueAdapterDate(dialect);
        } else if (valueClass.equals(LogicalDate.class)) {
            return new ValueAdapterLogicalDate(dialect);
        } else if (valueClass.equals(java.util.Date.class)) {
            return new ValueAdapterTimestamp(dialect);
        } else if (valueClass.equals(java.sql.Time.class)) {
            return new ValueAdapterTime(dialect);
        } else if (valueClass.isEnum()) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Class<Enum> enumValueClass = (Class<Enum>) valueClass;
            return new ValueAdapterEnum(dialect, enumValueClass);
        } else if (valueClass.equals(Boolean.class)) {
            return new ValueAdapterBoolean(dialect);
        } else if (valueClass.equals(Short.class)) {
            return new ValueAdapterShort(dialect);
        } else if (valueClass.equals(Byte.class)) {
            return new ValueAdapterByte(dialect);
        } else if (valueClass.equals(byte[].class)) {
            return new ValueAdapterByteArray(dialect, memberMeta);
        } else if (valueClass.equals(GeoPoint.class)) {
            return new ValueAdapterGeoPoint(dialect);
        } else {
            return null;
        }
    }

    public List<MemberOperationsMeta> getAllMembers() {
        return allMembers;
    }

    public List<MemberOperationsMeta> getColumnMembers() {
        return columnMembers;
    }

    public MemberOperationsMeta getMember(String path) {
        return membersByPath.get(path);
    }

    public MemberOperationsMeta getFirstDirectMember(String path) {
        int idx = path.substring(0, path.length() - 1).lastIndexOf(Path.PATH_SEPARATOR);
        while (idx != -1) {
            MemberOperationsMeta op = membersByPath.get(path.substring(0, idx + 1));
            if (op != null) {
                return op;
            } else {
                idx = path.substring(0, idx - 1).lastIndexOf(Path.PATH_SEPARATOR);
            }
        }
        return null;
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

    public List<MemberOperationsMeta> getIndexMembers() {
        return indexMembers;
    }

}

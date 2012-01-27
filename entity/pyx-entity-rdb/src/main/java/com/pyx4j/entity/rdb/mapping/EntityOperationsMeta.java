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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Filter;
import com.pyx4j.commons.FilterIterator;
import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.adapters.IndexAdapter;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.JoinTableOrderColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.server.AdapterFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ObjectClassType;
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

    private final List<MemberOperationsMeta> detachedMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberCollectionOperationsMeta> collectionMembers = new Vector<MemberCollectionOperationsMeta>();

    private final List<MemberOperationsMeta> indexMembers = new Vector<MemberOperationsMeta>();

    private final Mappings mappings;

    private MemberOperationsMeta updatedTimestampMember;

    private MemberOperationsMeta createdTimestampMember;

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

                    ValueAdapter valueAdapter = createEntityValueAdapter(dialect, memberMeta);

                    JoinTable joinTable = memberMeta.getAnnotation(JoinTable.class);
                    MemberCollectionOperationsMeta member;
                    if (joinTable != null) {
                        @SuppressWarnings("unchecked")
                        Class<? extends IEntity> entityClass = (Class<IEntity>) memberMeta.getValueClass();
                        Class<? extends IEntity> joinEntityClass = joinTable.value();
                        EntityMeta joinEntityMeta = EntityFactory.getEntityMeta(joinEntityClass);
                        String sqlName = TableModel.getTableName(dialect, joinEntityMeta);

                        String sqlValueName = null;
                        if (joinEntityClass == entityClass) {
                            sqlValueName = IEntity.PRIMARY_KEY;
                        } else {
                            sqlValueName = namingConvention.sqlFieldName(memberPersistenceName(findValueMember(entityMeta, memberName, joinTable,
                                    joinEntityMeta, entityClass)));
                        }
                        MemberMeta ownerMemberMeta = findOwnerMember(entityMeta, memberName, joinTable, joinEntityMeta, rootEntityMeta);
                        String sqlOwnerName = namingConvention.sqlFieldName(memberPersistenceName(ownerMemberMeta));
                        ValueAdapter ownerValueAdapter = createEntityValueAdapter(dialect, ownerMemberMeta);

                        MemberMeta orderMemberMeta = findOrderMember(entityMeta, memberName, joinTable, joinEntityMeta);
                        String sqlOrderColumnName = null;
                        if (orderMemberMeta == null && memberMeta.getObjectClassType() == ObjectClassType.EntityList) {
                            throw new Error("Unmapped orderColumn member in join table '" + joinEntityMeta.getCaption() + "' for " + memberName + " in "
                                    + entityMeta.getEntityClass().getName());
                        } else if (orderMemberMeta != null) {
                            sqlOrderColumnName = namingConvention.sqlFieldName(memberPersistenceName(orderMemberMeta));
                        }

                        member = new MemberCollectionOperationsMeta(memberAccess, valueAdapter, sqlName, memberMeta, path + Path.PATH_SEPARATOR + memberName
                                + Path.PATH_SEPARATOR, sqlOwnerName, ownerValueAdapter, sqlValueName, sqlOrderColumnName);
                    } else {
                        String sqlName;
                        if (namesPath != null) {
                            sqlName = namingConvention.sqlEmbededTableName(rootEntityMeta.getPersistenceName(), namesPath, memberPersistenceName);
                        } else {
                            sqlName = namingConvention.sqlChildTableName(rootEntityMeta.getPersistenceName(), memberPersistenceName);
                        }
                        ValueAdapter ownerValueAdapter = new ValueAdapterEntity(dialect, rootEntityMeta.getEntityClass());
                        member = new MemberCollectionOperationsMeta(memberAccess, valueAdapter, sqlName, memberMeta, path + Path.PATH_SEPARATOR + memberName
                                + Path.PATH_SEPARATOR, "owner", ownerValueAdapter, "value", "seq");
                    }
                    collectionMembers.add(member);
                    switch (memberMeta.getAttachLevel()) {
                    case Attached:
                        cascadeRetrieveMembers.add(member);
                        break;
                    case Detached:
                        detachedMembers.add(member);
                        break;
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
                    ValueAdapter valueAdapter = createEntityValueAdapter(dialect, memberMeta);
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
                    ValueAdapter ownerValueAdapter = new ValueAdapterEntity(dialect, rootEntityMeta.getEntityClass());
                    MemberCollectionOperationsMeta member = new MemberCollectionOperationsMeta(memberAccess, valueAdapter, sqlName, memberMeta, path
                            + Path.PATH_SEPARATOR + memberName + Path.PATH_SEPARATOR, "owner", ownerValueAdapter, "value", null);
                    collectionMembers.add(member);
                    membersByPath.put(member.getMemberPath(), member);
                    allMembers.add(member);
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

                    if ((updatedTimestampMember == null) && (memberName.equals(mainEntityMeta.getUpdatedTimestampMember()))) {
                        updatedTimestampMember = member;
                    }
                    if ((createdTimestampMember == null) && (memberName.equals(mainEntityMeta.getCreatedTimestampMember()))) {
                        createdTimestampMember = member;
                    }
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

    private MemberMeta findOwnerMember(EntityMeta entityMeta, String memberName, JoinTable joinTable, EntityMeta joinEntityMeta, EntityMeta rootEntityMeta) {
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
            throw new Error("Unmapped owner member in join table '" + joinEntityMeta.getCaption() + "' for " + memberName + " in "
                    + entityMeta.getEntityClass().getName());
        } else {
            return ownerMemberMeta;
        }
    }

    private MemberMeta findValueMember(EntityMeta entityMeta, String memberName, JoinTable joinTable, EntityMeta joinEntityMeta,
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
            throw new Error("Unmapped value member '" + entityClass.getName() + "' in join table '" + joinEntityMeta.getCaption() + "' for " + memberName
                    + " in " + entityMeta.getEntityClass().getName());
        } else {
            return valueMemberMeta;
        }
    }

    private MemberMeta findOrderMember(EntityMeta entityMeta, String memberName, JoinTable joinTable, EntityMeta joinEntityMeta) {
        MemberMeta orderMemberMeta = null;
        for (String jmemberName : joinEntityMeta.getMemberNames()) {
            MemberMeta jmemberMeta = joinEntityMeta.getMemberMeta(jmemberName);
            if (!jmemberMeta.isTransient()) {
                JoinTableOrderColumn joinTableOrderColumn = jmemberMeta.getAnnotation(JoinTableOrderColumn.class);
                if ((joinTableOrderColumn != null) && (joinTable.orderColumn() != ColumnId.class) && (joinTable.orderColumn() == joinTableOrderColumn.value())) {
                    if (orderMemberMeta != null) {
                        throw new Error("Duplicate orderColumn member in join table '" + joinEntityMeta.getCaption() + "' for " + memberName + " in "
                                + entityMeta.getEntityClass().getName());
                    }
                    if (jmemberMeta.getObjectClass().equals(Integer.class)) {
                        throw new Error("Expected Integer orderColumn in join table '" + joinEntityMeta.getCaption() + "' for " + memberName + " in "
                                + entityMeta.getEntityClass().getName());
                    }
                    orderMemberMeta = jmemberMeta;
                }
            }
        }
        return orderMemberMeta;
    }

    private ValueAdapter createEntityValueAdapter(Dialect dialect, MemberMeta memberMeta) {
        @SuppressWarnings("unchecked")
        Class<? extends IEntity> entityClass = (Class<IEntity>) memberMeta.getValueClass();
        if (entityClass.getAnnotation(Inheritance.class) != null) {
            return new ValueAdapterEntityVirtual(dialect, entityClass);
        } else {
            return new ValueAdapterEntity(dialect, entityClass);
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

    public MemberOperationsMeta getCreatedTimestampMember() {
        return createdTimestampMember;
    }

    public MemberOperationsMeta getUpdatedTimestampMember() {
        return updatedTimestampMember;
    }

    public List<MemberOperationsMeta> getCascadeRetrieveMembers() {
        return cascadeRetrieveMembers;
    }

    public List<MemberOperationsMeta> getDetachedMembers() {
        return detachedMembers;
    }

    public List<MemberCollectionOperationsMeta> getCollectionMembers() {
        return collectionMembers;
    }

    public Iterable<MemberCollectionOperationsMeta> getManagedCollectionMembers() {
        return new Iterable<MemberCollectionOperationsMeta>() {

            @Override
            public Iterator<MemberCollectionOperationsMeta> iterator() {
                return new FilterIterator<MemberCollectionOperationsMeta>(collectionMembers.iterator(), new Filter<MemberCollectionOperationsMeta>() {

                    @Override
                    public boolean accept(MemberCollectionOperationsMeta input) {
                        return (input.getMemberMeta().getAnnotation(JoinTable.class) == null);
                    }
                });
            }
        };
    }

    public Iterable<MemberCollectionOperationsMeta> getJoinTablesCollectionMembers() {
        return new Iterable<MemberCollectionOperationsMeta>() {

            @Override
            public Iterator<MemberCollectionOperationsMeta> iterator() {
                return new FilterIterator<MemberCollectionOperationsMeta>(collectionMembers.iterator(), new Filter<MemberCollectionOperationsMeta>() {

                    @Override
                    public boolean accept(MemberCollectionOperationsMeta input) {
                        return (input.getMemberMeta().getAnnotation(JoinTable.class) != null);
                    }
                });
            }
        };
    }

    public List<MemberOperationsMeta> getIndexMembers() {
        return indexMembers;
    }

}

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

import java.math.BigDecimal;
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
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.ManagedColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.Versioned;
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

    private final List<MemberOperationsMeta> cascadePersistMembersSecondPass = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> cascadeDeleteMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> cascadeRetrieveMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> idOnlyMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> detachedMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberCollectionOperationsMeta> collectionMembers = new Vector<MemberCollectionOperationsMeta>();

    private final List<MemberExternalOperationsMeta> externalMembers = new Vector<MemberExternalOperationsMeta>();

    private final List<MemberExternalOperationsMeta> versionInfoMembers = new Vector<MemberExternalOperationsMeta>();

    private final List<MemberOperationsMeta> indexMembers = new Vector<MemberOperationsMeta>();

    private MemberOperationsMeta updatedTimestampMember;

    private MemberOperationsMeta createdTimestampMember;

    final Map<String, Class<? extends IEntity>> impClasses;

    EntityOperationsMeta(Dialect dialect, EntityMeta entityMeta) {
        mainEntityMeta = entityMeta;
        String path = GWTJava5Helper.getSimpleName(entityMeta.getEntityClass());
        build(dialect, dialect.getNamingConvention(), entityMeta, path, null, null, entityMeta);

        Inheritance inheritance = entityMeta.getAnnotation(Inheritance.class);
        if ((inheritance != null) && (inheritance.strategy() == Inheritance.InheritanceStrategy.SINGLE_TABLE)) {
            impClasses = new HashMap<String, Class<? extends IEntity>>();
            for (Class<? extends IEntity> subclass : Mappings.getPersistableAssignableFrom(entityMeta.getEntityClass())) {
                EntityMeta subclassMeta = EntityFactory.getEntityMeta(subclass);
                build(dialect, dialect.getNamingConvention(), subclassMeta, path, null, null, subclassMeta);

                // TODO this is duplicate code from ValueAdapterEntityPolymorphic
                DiscriminatorValue discriminator = subclass.getAnnotation(DiscriminatorValue.class);
                if (discriminator != null) {
                    if (CommonsStringUtils.isEmpty(discriminator.value())) {
                        throw new Error("Missing value of @DiscriminatorValue annotation on class " + subclass.getName());
                    }
                    if (impClasses.containsKey(discriminator.value())) {
                        throw new Error("Duplicate value of @DiscriminatorValue annotation on class " + subclass.getName() + "; the same as in class "
                                + impClasses.get(discriminator.value()));
                    }
                    impClasses.put(discriminator.value(), subclass);
                } else if (subclass.getAnnotation(AbstractEntity.class) == null) {
                    throw new Error("Class " + subclass.getName() + " require @AbstractEntity or @DiscriminatorValue annotation");
                }
            }
        } else {
            impClasses = null;
        }

        // Create meta for PK
        {
            MemberMeta memberMeta = entityMeta.getMemberMeta(IEntity.PRIMARY_KEY);
            ValueAdapter valueAdapter = createValueAdapter(dialect, memberMeta);
            MemberOperationsMeta member = new MemberOperationsMeta(new EntityMemberDirectAccess(IEntity.PRIMARY_KEY), valueAdapter, dialect
                    .getNamingConvention().sqlIdColumnName(), memberMeta, path + Path.PATH_SEPARATOR + IEntity.PRIMARY_KEY + Path.PATH_SEPARATOR);
            membersByPath.put(member.getMemberPath(), member);
        }
    }

    public EntityMeta entityMeta() {
        return mainEntityMeta;
    }

    static String memberPersistenceName(MemberMeta memberMeta) {
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
            String memberPathBase = path + Path.PATH_SEPARATOR + memberName;
            String memberPath = memberPathBase + Path.PATH_SEPARATOR;
            if (membersByPath.containsKey(memberPath)) {
                continue;
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

                    @SuppressWarnings("unchecked")
                    Class<? extends IEntity> entityClass = (Class<IEntity>) memberMeta.getObjectClass();
                    build(dialect, namingConvention, rootEntityMeta, memberPathBase, accessPathChild, namesPathChild, EntityFactory.getEntityMeta(entityClass));
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

                    MemberCollectionOperationsMeta member;
                    JoinInformation joinInfo = JoinInformation.build(dialect, rootEntityMeta, entityMeta, memberMeta);
                    if (joinInfo == null) {
                        String sqlName;
                        if (namesPath != null) {
                            sqlName = namingConvention.sqlEmbededTableName(rootEntityMeta.getPersistenceName(), namesPath, memberPersistenceName);
                        } else {
                            sqlName = namingConvention.sqlChildTableName(rootEntityMeta.getPersistenceName(), memberPersistenceName);
                        }
                        if (rootEntityMeta.getEntityClass().getAnnotation(Inheritance.class) != null) {
                            throw new Error("Polymorphic Owner of Managed collection not supported " + memberName + " " + rootEntityMeta.getEntityClass());
                        }
                        ValueAdapter ownerValueAdapter = new ValueAdapterEntity(dialect, rootEntityMeta.getEntityClass());
                        member = new MemberCollectionOperationsMeta(memberAccess, valueAdapter, sqlName, memberMeta, memberPath, null, false,
                                namingConvention.sqlAutoGeneratedJoinOwnerColumnName(), ownerValueAdapter,
                                namingConvention.sqlAutoGeneratedJoinValueColumnName(), namingConvention.sqlAutoGeneratedJoinOrderColumnName(), null, true);
                    } else {
                        member = new MemberCollectionOperationsMeta(memberAccess, valueAdapter, joinInfo.sqlName, memberMeta, memberPath,
                                joinInfo.joinTableClass, joinInfo.joinTableSameAsTarget, joinInfo.sqlOwnerName, joinInfo.ownerValueAdapter,
                                joinInfo.sqlValueName, joinInfo.sqlOrderColumnName, joinInfo.orderMemeberName, false);
                    }
                    collectionMembers.add(member);
                    switch (memberMeta.getAttachLevel()) {
                    case ToStringMembers:
                        cascadeRetrieveMembers.add(member);
                        break;
                    case IdOnly:
                        idOnlyMembers.add(member);
                        break;
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
                    ValueAdapter valueAdapter = createEntityValueAdapter(dialect, memberMeta);
                    MemberOperationsMeta member;
                    JoinInformation joinInfo = JoinInformation.build(dialect, rootEntityMeta, entityMeta, memberMeta);
                    if (joinInfo == null) {
                        String sqlName;
                        if (namesPath != null) {
                            sqlName = namingConvention.sqlEmbededFieldName(namesPath, memberPersistenceName);
                        } else {
                            sqlName = namingConvention.sqlFieldName(memberPersistenceName);
                        }
                        member = new MemberOperationsMeta(memberAccess, valueAdapter, sqlName, memberMeta, memberPath, memberName.equals(ownerMemberName));

                        columnMembers.add(member);
                    } else {
                        if (joinInfo instanceof JoinVersionDataInformation) {
                            member = new MemberVersionDataOperationsMeta(dialect, memberAccess, valueAdapter, joinInfo.sqlName, memberMeta, memberPath,
                                    joinInfo.joinTableClass, joinInfo.joinTableSameAsTarget, joinInfo.sqlOwnerName, joinInfo.ownerValueAdapter,
                                    joinInfo.sqlValueName);
                            versionInfoMembers.add((MemberExternalOperationsMeta) member);
                        } else {
                            member = new MemberExternalOperationsMeta(memberAccess, valueAdapter, joinInfo.sqlName, memberMeta, memberPath,
                                    joinInfo.joinTableClass, joinInfo.joinTableSameAsTarget, joinInfo.sqlOwnerName, joinInfo.ownerValueAdapter,
                                    joinInfo.sqlValueName);
                            externalMembers.add((MemberExternalOperationsMeta) member);
                        }
                    }

                    membersByPath.put(member.getMemberPath(), member);
                    allMembers.add(member);
                    if (memberMeta.isCascadePersist()) {
                        if (joinInfo == null) {
                            cascadePersistMembers.add(member);
                        } else {
                            cascadePersistMembersSecondPass.add(member);
                        }
                    } else if (memberMeta.getAnnotation(Reference.class) != null) {
                        cascadePersistMembers.add(member);
                    }

                    if (memberMeta.isCascadeDelete()) {
                        cascadeDeleteMembers.add(member);
                    }
                    switch (memberMeta.getAttachLevel()) {
                    case ToStringMembers:
                        cascadeRetrieveMembers.add(member);
                        break;
                    case IdOnly:
                        idOnlyMembers.add(member);
                        break;
                    case Attached:
                        cascadeRetrieveMembers.add(member);
                        break;
                    case Detached:
                        detachedMembers.add(member);
                        break;
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
                    MemberCollectionOperationsMeta member = new MemberCollectionOperationsMeta(memberAccess, valueAdapter, sqlName, memberMeta, memberPath,
                            null, true, namingConvention.sqlAutoGeneratedJoinOwnerColumnName(), ownerValueAdapter,
                            namingConvention.sqlAutoGeneratedJoinValueColumnName(), null, null, true);
                    collectionMembers.add(member);
                    membersByPath.put(member.getMemberPath(), member);
                    allMembers.add(member);
                } else if (memberMeta.getAnnotation(ManagedColumn.class) != null) {
                    // ignore ManagedColumn IPrimitive for now
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
                    MemberOperationsMeta member = new MemberOperationsMeta(memberAccess, valueAdapter, sqlName, memberMeta, memberPath);
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

    static ValueAdapter createEntityValueAdapter(Dialect dialect, MemberMeta memberMeta) {
        @SuppressWarnings("unchecked")
        Class<? extends IEntity> entityClass = (Class<IEntity>) memberMeta.getValueClass();
        if (entityClass.getAnnotation(Inheritance.class) != null) {
            if (memberMeta.getAnnotation(Versioned.class) != null) {
                throw new Error("@Versioned Polymorphic not supported");
            } else {
                return new ValueAdapterEntityPolymorphic(dialect, entityClass);
            }
        } else if (memberMeta.getAnnotation(Versioned.class) != null) {
            return new ValueAdapterEntityVersioned(dialect, entityClass);
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
        } else if (valueClass.equals(BigDecimal.class)) {
            return new ValueAdapterBigDecimal(dialect);
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

    public List<MemberOperationsMeta> getCascadePersistMembersSecondPass() {
        return cascadePersistMembersSecondPass;
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

    public List<MemberOperationsMeta> getIdOnlyMembers() {
        return idOnlyMembers;
    }

    public List<MemberOperationsMeta> getDetachedMembers() {
        return detachedMembers;
    }

    public List<MemberCollectionOperationsMeta> getCollectionMembers() {
        return collectionMembers;
    }

    public Iterable<MemberCollectionOperationsMeta> getAutogeneratedCollectionMembers() {
        return new Iterable<MemberCollectionOperationsMeta>() {

            @Override
            public Iterator<MemberCollectionOperationsMeta> iterator() {
                return new FilterIterator<MemberCollectionOperationsMeta>(collectionMembers.iterator(), new Filter<MemberCollectionOperationsMeta>() {

                    @Override
                    public boolean accept(MemberCollectionOperationsMeta input) {
                        return input.isAutogenerated();
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
                        return !input.isAutogenerated();
                    }
                });
            }
        };
    }

    public List<MemberExternalOperationsMeta> getExternalMembers() {
        return externalMembers;
    }

    public List<MemberExternalOperationsMeta> getVersionInfoMembers() {
        return versionInfoMembers;
    }

    public List<MemberOperationsMeta> getIndexMembers() {
        return indexMembers;
    }

    @Override
    public String toString() {
        return mainEntityMeta.getEntityClass().getName();
    }
}

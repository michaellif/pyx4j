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
 * Created on 2011-01-03
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.GeneratedValue;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.adapters.IndexAdapter;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.rdb.dialect.TypeMetaConfiguration;
import com.pyx4j.entity.server.AdapterFactory;

public class MemberOperationsMeta implements EntityMemberAccess {

    private final String memberPath;

    private final MemberMeta memberMeta;

    private final boolean ownerColumn;

    private final EntityMemberAccess memberAccess;

    private final ValueAdapter valueAdapter;

    private final String sqlName;

    private final Class<? extends IndexAdapter<?>> indexAdapterClass;

    private final Class<?> indexValueClass;

    private String sqlSequenceName;

    private final GeneratedValue generatedValue;

    private MemberOperationsMeta sortMemberOperationsMeta;

    private Set<String> subclassDiscriminators;

    public MemberOperationsMeta(EntityMemberAccess memberAccess, ValueAdapter valueAdapter, String sqlName, MemberMeta memberMeta, String memberPath) {
        this(memberAccess, valueAdapter, sqlName, memberMeta, memberPath, null, null, false);
    }

    public MemberOperationsMeta(EntityMemberAccess memberAccess, ValueAdapter valueAdapter, String sqlName, MemberMeta memberMeta, String memberPath,
            boolean ownerColumn) {
        this(memberAccess, valueAdapter, sqlName, memberMeta, memberPath, null, null, ownerColumn);
    }

    public MemberOperationsMeta(EntityMemberAccess memberAccess, ValueAdapter valueAdapter, String sqlName, MemberMeta memberMeta, String memberPath,
            Class<? extends IndexAdapter<?>> indexAdapterClass, Class<?> indexValueClass, boolean ownerColumn) {
        this.memberPath = memberPath;
        this.memberMeta = memberMeta;
        this.memberAccess = memberAccess;
        this.valueAdapter = valueAdapter;
        this.sqlName = sqlName;
        this.indexAdapterClass = indexAdapterClass;
        this.indexValueClass = indexValueClass;
        this.ownerColumn = ownerColumn;
        this.generatedValue = memberMeta.getAnnotation(GeneratedValue.class);
    }

    public MemberOperationsMeta getSortMemberOperationsMeta() {
        return sortMemberOperationsMeta;
    }

    public void setSortMemberOperationsMeta(MemberOperationsMeta sortMemberOperationsMeta) {
        this.sortMemberOperationsMeta = sortMemberOperationsMeta;
    }

    public void addSubclassDiscriminator(DiscriminatorValue subclassDiscriminator) {
        if (subclassDiscriminator == null) {
            return;
        }
        if (subclassDiscriminators == null) {
            subclassDiscriminators = new HashSet<String>();
        }
        subclassDiscriminators.add(subclassDiscriminator.value());
    }

    public Set<String> getSubclassDiscriminators() {
        return subclassDiscriminators;
    }

    public boolean isOwnerColumn() {
        return ownerColumn;
    }

    public boolean isOwnedForceCreation() {
        return memberMeta.isOwnedRelationships() && (!memberMeta.isEmbedded()) && memberMeta.getAnnotation(Owned.class).forceCreation();
    }

    public boolean isVersionData() {
        return false;
    }

    public boolean isExternal() {
        return false;
    }

    public String getMemberPath() {
        return memberPath;
    }

    @Override
    public String getMemberName() {
        return memberAccess.getMemberName();
    }

    public ValueAdapter getValueAdapter() {
        return valueAdapter;
    }

    public MemberMeta getMemberMeta() {
        return memberMeta;
    }

    public String sqlName() {
        return sqlName;
    }

    public boolean hasNotNullConstraint() {
        MemberColumn memberColumn = memberMeta.getAnnotation(MemberColumn.class);
        if (memberColumn == null) {
            return false;
        } else {
            return memberColumn.notNull();
        }
    }

    public TypeMetaConfiguration getTypeConfiguration() {
        TypeMetaConfiguration tmc = new TypeMetaConfiguration();
        tmc.setLength(memberMeta.getLength());
        MemberColumn memberColumn = memberMeta.getAnnotation(MemberColumn.class);
        if (memberColumn != null) {
            tmc.setPrecision(memberColumn.precision());
            tmc.setScale(memberColumn.scale());
        }
        return tmc;
    }

    public Serializable getPersistMemberValue(IEntity entity) {
        Serializable value = getMemberValue(entity);
        if ((value == null) && (generatedValue != null)) {
            value = ValueGenerator.generate(generatedValue, getMemberMeta());
            setMemberValue(entity, value);
        }
        return value;
    }

    @Override
    public Serializable getMemberValue(IEntity entity) {
        return memberAccess.getMemberValue(entity);
    }

    @Override
    public boolean containsMemberValue(IEntity entity) {
        return memberAccess.containsMemberValue(entity);
    }

    @Override
    public void setMemberValue(IEntity entity, Serializable value) {
        memberAccess.setMemberValue(entity, valueAdapter.ensureType(value));
    }

    @Override
    public IObject<?> getMember(IEntity entity) {
        return memberAccess.getMember(entity);
    }

    public Class<? extends IndexAdapter<?>> getIndexAdapterClass() {
        return indexAdapterClass;
    }

    public Class<?> getIndexValueClass() {
        return indexValueClass;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object getIndexedValue(IEntity entity) {
        IndexAdapter adapter = AdapterFactory.getIndexAdapter(getIndexAdapterClass());
        return adapter.getIndexedValue(entity, memberMeta, memberMeta.isEntity() ? getMember(entity) : getMemberValue(entity));
    }

    public String getSqlSequenceName() {
        return sqlSequenceName;
    }

    public void setSqlSequenceName(String sqlSequenceName) {
        this.sqlSequenceName = sqlSequenceName;
    }

    @Override
    public String toString() {
        return memberPath + " " + memberMeta.toString() + " " + valueAdapter.toString();
    }
}

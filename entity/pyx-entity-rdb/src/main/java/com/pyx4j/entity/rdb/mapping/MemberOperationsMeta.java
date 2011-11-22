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

import com.pyx4j.entity.adapters.IndexAdapter;
import com.pyx4j.entity.server.AdapterFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.MemberMeta;

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
    }

    public boolean isOwnerColumn() {
        return ownerColumn;
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

    @Override
    public Object getMemberValue(IEntity entity) {
        return memberAccess.getMemberValue(entity);
    }

    @Override
    public boolean containsMemberValue(IEntity entity) {
        return memberAccess.containsMemberValue(entity);
    }

    @Override
    public void setMemberValue(IEntity entity, Object value) {
        memberAccess.setMemberValue(entity, value);
    }

    @Override
    public IObject<?> getMember(IEntity entity) {
        return memberAccess.getMember(entity);
    }

    public Class<? extends IndexAdapter<?>> getIndexAdapter() {
        return indexAdapterClass;
    }

    public Class<?> getIndexValueClass() {
        return indexValueClass;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object getIndexedValue(IEntity entity) {
        IndexAdapter adapter = AdapterFactory.getIndexAdapter(indexAdapterClass);
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
        return memberPath + " " + memberMeta.toString();
    }
}

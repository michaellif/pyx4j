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
 * Created on Jan 11, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.impl;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityMetaImpl implements EntityMeta {

    private final Class<? extends IEntity> entityClass;

    private final String persistenceName;

    private final String caption;

    private final String description;

    private final boolean persistenceTransient;

    private final boolean rpcTransient;

    private boolean membersListCreated;

    protected final HashMap<String, MemberMeta> membersMeta = new HashMap<String, MemberMeta>();

    private List<String> toStringMemberNames;

    public EntityMetaImpl(Class<? extends IEntity> clazz) {
        entityClass = clazz;
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        if (tableAnnotation != null) {
            persistenceName = tableAnnotation.prefix()
                    + (CommonsStringUtils.isStringSet(tableAnnotation.name()) ? tableAnnotation.name() : entityClass.getSimpleName());
        } else {
            persistenceName = entityClass.getSimpleName();
        }

        Caption captionAnnotation = entityClass.getAnnotation(Caption.class);
        if ((captionAnnotation != null) && (CommonsStringUtils.isStringSet(captionAnnotation.name()))) {
            caption = captionAnnotation.name();
        } else {
            caption = EnglishGrammar.capitalize(entityClass.getSimpleName());
        }
        if (captionAnnotation != null) {
            description = captionAnnotation.description();
        } else {
            description = null;
        }
        persistenceTransient = (entityClass.getAnnotation(Transient.class) != null);
        rpcTransient = (entityClass.getAnnotation(RpcTransient.class) != null) || (entityClass.getAnnotation(RpcBlacklist.class) != null);
    }

    @Override
    public Class<? extends IEntity> getEntityClass() {
        return entityClass;
    }

    @Override
    public String getPersistenceName() {
        if (isTransient()) {
            throw new Error("Can't Persist/Retrieve Transient Entity");
        }
        return persistenceName;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isTransient() {
        return persistenceTransient;
    }

    @Override
    public boolean isRpcTransient() {
        return rpcTransient;
    }

    @Override
    public MemberMeta getMemberMeta(String memberName) {
        MemberMeta memberMeta = membersMeta.get(memberName);
        if (memberMeta == null) {
            try {
                memberMeta = new MemberMetaImpl(entityClass.getMethod(memberName, (Class[]) null));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unknown member " + memberName);
            }
            membersMeta.put(memberName, memberMeta);
        }
        return memberMeta;
    }

    private void lazyCreateMembersNamesList() {
        for (Method method : entityClass.getMethods()) {
            if (method.getDeclaringClass().equals(Object.class) || method.getDeclaringClass().isAssignableFrom(IEntity.class)) {
                continue;
            }
            Class<?> type = method.getReturnType();
            if (type == Void.class) {
                continue;
            }
            if (!membersMeta.containsKey(method.getName())) {
                membersMeta.put(method.getName(), null);
            }
        }
    }

    @Override
    public Set<String> getMemberNames() {
        if (!membersListCreated) {
            lazyCreateMembersNamesList();
            membersListCreated = true;
        }
        return membersMeta.keySet();
    }

    @Override
    public synchronized List<String> getToStringMemberNames() {
        if (toStringMemberNames == null) {
            toStringMemberNames = new Vector<String>();
            final HashMap<String, ToString> sortKeys = new HashMap<String, ToString>();
            for (Method method : entityClass.getMethods()) {
                ToString ts = method.getAnnotation(ToString.class);
                if (ts != null) {
                    toStringMemberNames.add(method.getName());
                    sortKeys.put(method.getName(), ts);
                }
            }
            if (toStringMemberNames.size() > 1) {
                Collections.sort(toStringMemberNames, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        int v1 = sortKeys.get(o1).index();
                        int v2 = sortKeys.get(o2).index();
                        return (v1 < v2 ? -1 : (v1 == v2 ? 0 : 1));
                    }
                });
            }
        }
        return toStringMemberNames;
    }
}

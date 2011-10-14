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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.i18n.annotations.I18nAnnotation;

public class EntityMetaImpl implements EntityMeta {

    private final Class<? extends IEntity> entityClass;

    private final String persistenceName;

    private final String caption;

    private final String description;

    private final String watermark;

    private final boolean persistenceTransient;

    private final boolean rpcTransient;

    protected final HashMap<String, MemberMeta> membersMeta = new HashMap<String, MemberMeta>();

    private List<String> memberNames;

    private List<String> toStringMemberNames;

    private List<String> businessEqualMemberNames;

    private String ownerMemberName = null;

    private String createdTimestampMember;

    private String updatedTimestampMember;

    public EntityMetaImpl(Class<? extends IEntity> clazz) {
        entityClass = clazz;
        String persistenceNamePrefix = ServerSideConfiguration.instance().persistenceNamePrefix();
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        if (tableAnnotation != null) {
            persistenceName = (((persistenceNamePrefix != null) && (!tableAnnotation.disableGlobalPrefix())) ? persistenceNamePrefix : "")
                    + tableAnnotation.prefix()
                    + (CommonsStringUtils.isStringSet(tableAnnotation.name()) ? tableAnnotation.name() : entityClass.getSimpleName());
        } else {
            persistenceName = ((persistenceNamePrefix != null) ? persistenceNamePrefix : "") + entityClass.getSimpleName();
        }

        Caption captionAnnotation = entityClass.getAnnotation(Caption.class);
        String captionValue = I18nAnnotation.DEFAULT_VALUE;
        if (captionAnnotation != null) {
            captionValue = captionAnnotation.name();
        }
        if (captionAnnotation != null) {
            description = captionAnnotation.description();
            watermark = captionAnnotation.watermark();
        } else {
            description = null;
            watermark = null;
        }
        if (I18nAnnotation.DEFAULT_VALUE.equals(captionValue)) {
            caption = EnglishGrammar.capitalize(EnglishGrammar.classNameToEnglish(entityClass.getSimpleName()));
        } else {
            caption = captionValue;
        }

        persistenceTransient = (entityClass.getAnnotation(Transient.class) != null);
        rpcTransient = (entityClass.getAnnotation(RpcTransient.class) != null) || (entityClass.getAnnotation(RpcBlacklist.class) != null);
    }

    @Override
    public Class<? extends IEntity> getEntityClass() {
        return entityClass;
    }

    @Override
    public <T extends IEntity> boolean isEntityClassAssignableFrom(T targetInstance) {
        if (targetInstance == null) {
            return false;
        }
        return entityClass.isAssignableFrom(targetInstance.getInstanceValueClass());
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
    public String getWatermark() {
        return watermark;
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
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return entityClass.getAnnotation(annotationClass);
    }

    @Override
    public MemberMeta getMemberMeta(String memberName) {
        MemberMeta memberMeta = membersMeta.get(memberName);
        if (memberMeta == null) {
            try {
                memberMeta = new MemberMetaImpl(entityClass.getMethod(memberName, (Class[]) null));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unknown member " + memberName + " of " + persistenceName);
            }
            membersMeta.put(memberName, memberMeta);
        }
        return memberMeta;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MemberMeta getMemberMeta(Path path) {
        //assertPath(path);
        EntityMeta em = this;
        MemberMeta mm = null;
        for (String memberName : path.getPathMembers()) {
            //TODO ICollection support
            if (mm != null) {
                Class<?> valueClass = mm.getValueClass();
                if (!(IEntity.class.isAssignableFrom(valueClass))) {
                    throw new RuntimeException("Invalid member in path " + memberName + " of " + persistenceName);
                } else {
                    em = EntityFactory.getEntityMeta((Class<? extends IEntity>) valueClass);
                }
            }
            mm = em.getMemberMeta(memberName);
        }
        return mm;
    }

    private synchronized void lazyCreateMembersNamesList() {
        if (memberNames != null) {
            return;
        }
        SharedEntityHandler anInstance = (SharedEntityHandler) EntityFactory.create(getEntityClass());
        memberNames = Collections.unmodifiableList(Arrays.asList(anInstance.getMembers()));

        //Find special members
        for (Method method : getEntityClass().getMethods()) {
            if (method.getDeclaringClass().equals(Object.class) || method.getDeclaringClass().isAssignableFrom(IEntity.class)) {
                continue;
            }
            Class<?> type = method.getReturnType();
            if (type == Void.class) {
                continue;
            }
            Timestamp ts = method.getAnnotation(Timestamp.class);
            if (ts != null) {
                switch (ts.value()) {
                case Created:
                    createdTimestampMember = method.getName();
                    break;
                case Updated:
                    updatedTimestampMember = method.getName();
                }
            }
            if (method.getAnnotation(Owner.class) != null) {
                if (ownerMemberName != null) {
                    throw new Error("Duplicate @Owner declaration " + method.getName() + " and " + ownerMemberName);
                }
                ownerMemberName = method.getName();
            }
        }
    }

    @Override
    public List<String> getMemberNames() {
        lazyCreateMembersNamesList();
        return memberNames;
    }

    @Override
    public String getToStringFormat() {
        ToStringFormat annotation = entityClass.getAnnotation(ToStringFormat.class);
        if (annotation != null) {
            return annotation.value();
        } else {
            return null;
        }
    }

    @Override
    public String getNullString() {
        ToStringFormat annotation = entityClass.getAnnotation(ToStringFormat.class);
        if (annotation != null) {
            return annotation.nil();
        } else {
            return "";
        }
    }

    @Override
    public synchronized List<String> getToStringMemberNames() {
        //TODO move this list creation to EntityImplGenerator for better performance
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

    @Override
    public synchronized List<String> getBusinessEqualMemberNames() {
        if (businessEqualMemberNames == null) {
            businessEqualMemberNames = new Vector<String>();
            for (Method method : entityClass.getMethods()) {
                BusinessEqualValue ts = method.getAnnotation(BusinessEqualValue.class);
                if (ts != null) {
                    businessEqualMemberNames.add(method.getName());
                }
            }
        }
        return businessEqualMemberNames;
    }

    @Override
    public String getOwnerMemberName() {
        lazyCreateMembersNamesList();
        return ownerMemberName;
    }

    @Override
    public String getCreatedTimestampMember() {
        lazyCreateMembersNamesList();
        return createdTimestampMember;
    }

    @Override
    public String getUpdatedTimestampMember() {
        lazyCreateMembersNamesList();
        return updatedTimestampMember;
    }

}

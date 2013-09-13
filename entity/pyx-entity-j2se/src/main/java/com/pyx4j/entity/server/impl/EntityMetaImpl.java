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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.ExtendsDBO;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.InheritedOnInterface;
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
import com.pyx4j.i18n.shared.I18n;

public class EntityMetaImpl implements EntityMeta {

    private static final I18n i18n = I18n.get(EntityMetaImpl.class);

    private final Class<? extends IEntity> entityClass;

    private final Class<? extends IEntity> expandedFromClass;

    private final Class<? extends IEntity> persistableSuperClass;

    private final String persistenceName;

    private final String i18nContext;

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

    private final ToStringFormat toStringFormat;

    public EntityMetaImpl(Class<? extends IEntity> clazz) {
        entityClass = clazz;

        Class<? extends IEntity> persistableSuperClass = null;
        DiscriminatorValue discriminator = clazz.getAnnotation(DiscriminatorValue.class);
        if ((discriminator != null) || (clazz.getAnnotation(AbstractEntity.class) != null)) {
            persistableSuperClass = findSingeTableInheritance(entityClass);
        } else {
            persistableSuperClass = null;
        }

        if (persistableSuperClass != entityClass) {
            this.persistableSuperClass = persistableSuperClass;
        } else {
            this.persistableSuperClass = null;
        }

        Class<? extends IEntity> persistableClass = persistableSuperClass;
        if (persistableClass == null) {
            persistableClass = entityClass;
        }

        String persistenceNamePrefix = ServerSideConfiguration.instance().persistenceNamePrefix();
        Table tableAnnotation = persistableClass.getAnnotation(Table.class);
        if (tableAnnotation != null) {
            persistenceName = (((persistenceNamePrefix != null) && (!tableAnnotation.disableGlobalPrefix())) ? persistenceNamePrefix : "")
                    + tableAnnotation.prefix()
                    + (CommonsStringUtils.isStringSet(tableAnnotation.name()) ? tableAnnotation.name() : persistableClass.getSimpleName());
        } else {
            persistenceName = ((persistenceNamePrefix != null) ? persistenceNamePrefix : "") + persistableClass.getSimpleName();
        }

        ExtendsDBO dtoAnnotation = entityClass.getAnnotation(ExtendsDBO.class);
        if (dtoAnnotation != null) {
            if (dtoAnnotation.value() == IEntity.class) {
                if (clazz.getInterfaces().length > 1) {
                    throw new Error("Unresolved Multiple inheritance @ExtendsDBO declaration on interface " + clazz.getName());
                } else {
                    @SuppressWarnings("unchecked")
                    Class<? extends IEntity> superClass = (Class<? extends IEntity>) clazz.getInterfaces()[0];
                    expandedFromClass = superClass;
                }
            } else {
                expandedFromClass = dtoAnnotation.value();
            }
        } else {
            expandedFromClass = entityClass;
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

        com.pyx4j.i18n.annotations.I18n trCfg = entityClass.getAnnotation(com.pyx4j.i18n.annotations.I18n.class);
        String context = null;
        if (trCfg != null) {
            context = trCfg.context();
        }
        i18nContext = context;

        persistenceTransient = (entityClass.getAnnotation(Transient.class) != null);
        rpcTransient = (entityClass.getAnnotation(RpcTransient.class) != null) || (entityClass.getAnnotation(RpcBlacklist.class) != null);
        toStringFormat = getInheritedAnnotation(entityClass, ToStringFormat.class);
    }

    /**
     * @see InheritedOnInterface
     */
    private <A extends Annotation> A getInheritedAnnotation(Class<? extends IEntity> clazz, Class<A> annotationClass) {
        A annotation = clazz.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        // Breadth-first search
        Queue<Class<?>> queue = new LinkedList<Class<?>>();
        queue.addAll(Arrays.asList(clazz.getInterfaces()));
        while (!queue.isEmpty()) {
            Class<?> superClasses = queue.remove();
            if (IEntity.class.isAssignableFrom(superClasses) && (superClasses != IEntity.class)) {
                annotation = superClasses.getAnnotation(annotationClass);
                if (annotation != null) {
                    return annotation;
                }
                queue.addAll(Arrays.asList(superClasses.getInterfaces()));
            }
        }
        return null;
    }

    private Class<? extends IEntity> findSingeTableInheritance(Class<? extends IEntity> clazz) {
        for (Class<?> superClasses : clazz.getInterfaces()) {
            if (IEntity.class.isAssignableFrom(superClasses) && (superClasses != IEntity.class)) {
                @SuppressWarnings("unchecked")
                Class<? extends IEntity> superEntityClasses = (Class<? extends IEntity>) superClasses;
                Inheritance inheritance = superClasses.getAnnotation(Inheritance.class);
                if ((inheritance != null) && (inheritance.strategy() == Inheritance.InheritanceStrategy.SINGLE_TABLE)) {
                    return superEntityClasses;
                } else {
                    Class<? extends IEntity> persistableSuperClass = findSingeTableInheritance(superEntityClasses);
                    if (persistableSuperClass != null) {
                        return persistableSuperClass;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Class<? extends IEntity> getEntityClass() {
        return entityClass;
    }

    @Override
    public Class<? extends IEntity> getDBOClass() {
        return expandedFromClass;
    }

    @Override
    public Class<? extends IEntity> getPersistableSuperClass() {
        return persistableSuperClass;
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
        return i18n.translate(i18nContext, caption);
    }

    @Override
    public String getCaptionNL() {
        return caption;
    }

    @Override
    public String getDescription() {
        return i18n.translate(i18nContext, description);
    }

    @Override
    public String getWatermark() {
        return i18n.translate(i18nContext, watermark);
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
                String methodName = memberName;
                if (methodName.equals(IEntity.CONCRETE_TYPE_DATA_ATTR)) {
                    methodName = "instanceValueClass";
                }
                memberMeta = new MemberMetaImpl(entityClass, entityClass.getMethod(methodName, (Class[]) null));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unknown member '" + memberName + "' of '" + persistenceName + "'");
            }
            membersMeta.put(memberName, memberMeta);
        }
        return memberMeta;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MemberMeta getMemberMeta(Path path) {
        EntityMeta em = this;
        MemberMeta mm = null;
        for (String memberName : path.getPathMembers()) {
            if (mm != null) {
                Class<?> valueClass = mm.getValueClass();
                if (!(IEntity.class.isAssignableFrom(valueClass))) {
                    throw new RuntimeException("Invalid member in path '" + memberName + "' of '" + persistenceName + "'");
                } else {
                    em = EntityFactory.getEntityMeta((Class<? extends IEntity>) valueClass);
                }
            }
            if (memberName.equals(Path.COLLECTION_SEPARATOR)) {
                continue;
            } else {
                mm = em.getMemberMeta(memberName);
            }
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
                    if (createdTimestampMember != null) {
                        throw new Error("Duplicate @Timestamp(Created) declaration " + method.getName() + " and " + createdTimestampMember);
                    }
                    createdTimestampMember = method.getName();
                    break;
                case Updated:
                    if (updatedTimestampMember != null) {
                        throw new Error("Duplicate @Timestamp(Updated) declaration " + method.getName() + " and " + updatedTimestampMember);
                    }
                    updatedTimestampMember = method.getName();
                    break;
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
    public List<String> getMemberNamesWithPk() {
        List<String> l = new ArrayList<String>(getMemberNames());
        l.add(IEntity.PRIMARY_KEY);
        return l;
    }

    @Override
    public String getToStringFormat() {
        if (toStringFormat != null) {
            return i18n.translate(i18nContext, toStringFormat.value());
        } else {
            return null;
        }
    }

    @Override
    public String getNullString() {
        if (toStringFormat != null) {
            return i18n.translate(i18nContext, toStringFormat.nil());
        } else {
            return "";
        }
    }

    @Override
    public synchronized List<String> getToStringMemberNames() {
        //TODO move this list creation to EntityImplGenerator for better performance
        if (toStringMemberNames == null) {
            toStringMemberNames = new Vector<String>();
            final Map<String, ToString> sortKeys = new HashMap<String, ToString>();
            for (String member : getMemberNames()) {
                Method method;
                try {
                    method = entityClass.getMethod(member);
                } catch (Throwable e) {
                    throw new Error(e);
                }
                ToString ts = method.getAnnotation(ToString.class);
                if (ts != null) {
                    sortKeys.put(method.getName(), ts);
                    toStringMemberNames.add(method.getName());
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
            for (String member : getMemberNames()) {
                Method method;
                try {
                    method = entityClass.getMethod(member);
                } catch (Throwable e) {
                    throw new Error(e);
                }
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

    @Override
    public String toString() {
        return "EntityMeta " + entityClass.getSimpleName();
    }

}

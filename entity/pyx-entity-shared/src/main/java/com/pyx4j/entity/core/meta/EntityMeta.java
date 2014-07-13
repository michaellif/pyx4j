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
package com.pyx4j.entity.core.meta;

import java.lang.annotation.Annotation;
import java.util.List;

import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;

/**
 * Represent the Meta data denoted using annotation. For consistency it is better to use
 * this class instead of using annotations directly. In GWT mode we have no other choice
 * but to use this class. Implementation of this class is generated for GWT runtime.
 * 
 * Use EntityFactory.getEntityMeta(Class) to get instances.
 */
public interface EntityMeta {

    public Class<? extends IEntity> getEntityClass();

    /**
     * @return 'This class' if this class is not annotated with @ExtendsDBO, else returns value declared in @ExtendsDBO.value
     */
    public Class<? extends IEntity> getBOClass();

    /**
     * {@link Inheritance.InheritanceStrategy.SINGLE_TABLE}
     */
    public Class<? extends IEntity> getPersistableSuperClass();

    public <T extends IEntity> boolean isEntityClassAssignableFrom(T targetInstance);

    /**
     * See com.pyx4j.entity.annotations.Table
     */
    public String getPersistenceName();

    /**
     * Localized user preventable entity name
     * 
     * See com.pyx4j.entity.annotations.Caption
     */
    public String getCaption();

    /**
     * Non Localized Caption, English name of the entity
     */
    public String getCaptionNL();

    /**
     * See com.pyx4j.entity.annotations.Caption
     */
    public String getDescription();

    /**
     * See com.pyx4j.entity.annotations.Caption
     */
    public String getWatermark();

    /**
     * See com.pyx4j.entity.annotations.Transient
     */
    public boolean isTransient();

    /**
     * See com.pyx4j.entity.annotations.RpcTransient
     */
    public boolean isRpcTransient();

    /*
     * Works only on server side.
     * 
     * Returns this element's annotation for the specified type if such an annotation is
     * present, else null.
     * 
     * in GWT (or on server) use isAnnotationPresent
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    /**
     * @param annotationClass
     *            that was marked with GwtAnnotation
     * @return true if Entity was Annotated with this annotation
     */
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);

    public List<String> getMemberNames();

    public List<String> getMemberNamesWithPk();

    public MemberMeta getMemberMeta(String memberName);

    public MemberMeta getMemberMeta(Path path);

    public String getToStringFormat();

    public String getNullString();

    public List<String> getToStringMemberNames();

    public List<String> getBusinessEqualMemberNames();

    /**
     * Return the member that @Owner annotation referred to this Object, else returns null.
     * 
     * @see com.pyx4j.entity.annotations.Owner
     */
    public String getOwnerMemberName();

    /*
     * Works only on server side.
     */
    //TODO Do we need this ever on client ?
    public String getCreatedTimestampMember();

    /*
     * Works only on server side.
     */
    //TODO Do we need this ever on client ?
    public String getUpdatedTimestampMember();

}

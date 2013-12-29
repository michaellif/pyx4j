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
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.core.meta;

import java.lang.annotation.Annotation;
import java.util.List;

import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.validator.Validator;

/**
 * Represent the Meta data denoted using annotation. For consistency it is better to use
 * this class instead of using annotations directly. Implementation of this class is
 * generated for GWT runtime.
 */
public interface MemberMeta {

    public String getFieldName();

    /**
     * Localized user preventable filed name
     * 
     * @see com.pyx4j.entity.annotations.Caption
     */
    public String getCaption();

    /**
     * Non Localized Caption, English name of the filed
     */
    public String getCaptionNL();

    /**
     * Localized user preventable filed description
     * 
     * @see com.pyx4j.entity.annotations.Caption
     */
    public String getDescription();

    /**
     * Localized watermark
     * 
     * @see com.pyx4j.entity.annotations.Caption
     */
    public String getWatermark();

    /**
     * @see com.pyx4j.entity.annotations.Transient
     */
    public boolean isTransient();

    /**
     * @see com.pyx4j.entity.annotations.RpcTransient
     */
    public boolean isRpcTransient();

    /**
     * @see com.pyx4j.entity.annotations.LogTransient
     */
    public boolean isLogTransient();

    /**
     * When @Indexed indexPrimaryValue != false
     * 
     * @see com.pyx4j.entity.annotations.Indexed
     */
    public boolean isIndexed();

    /**
     * @see com.pyx4j.entity.annotations.Detached
     * @return true if @Detached present and level != Attached
     */
    public boolean isDetached();

    public AttachLevel getAttachLevel();

    /**
     * @see com.pyx4j.entity.annotations.Owned
     * 
     * @see com.pyx4j.entity.annotations.EmbeddedEntity
     * 
     *      or IPrimitiveSet
     */
    public boolean isOwnedRelationships();

    public boolean isCascadePersist();

    public boolean isCascadeDelete();

    /**
     * @see com.pyx4j.entity.annotations.Owner
     */
    public boolean isOwner();

    /**
     * @see com.pyx4j.entity.annotations.EmbeddedEntity
     */
    public boolean isEmbedded();

    public Class<?> getValueClass();

    public boolean isEntity();

    public boolean isNumberValueClass();

    /**
     * 
     * @return IList, ISet, IPrimitive, or extends IEntity
     */
    @SuppressWarnings("rawtypes")
    public Class<? extends IObject> getObjectClass();

    public ObjectClassType getObjectClassType();

    /**
     * See com.pyx4j.entity.annotations.Length
     */
    public int getLength();

    /*
     * Works only on server side.
     * 
     * Returns this element's annotation for the specified type if such an annotation is
     * present, else null.
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    /**
     * @see com.pyx4j.entity.annotations.Format
     */
    public String getFormat();

    public boolean useMessageFormat();

    public String getNullString();

    public boolean isToStringMember();

    public EditorType getEditorType();

    public List<Validator> getValidators();

    public boolean isValidatorAnnotationPresent(Class<? extends Annotation> annotationClass);

}

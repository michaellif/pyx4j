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
package com.pyx4j.entity.client.impl;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.entity.shared.validator.Validator;

public class ClientMemberMetaImpl implements MemberMeta {

    private final String fieldName;

    private final boolean persistenceTransient;

    private final boolean rpcTransient;

    private final boolean detached;

    private final boolean ownedRelationships;

    private final boolean embedded;

    private final boolean indexed;

    private final boolean entity;

    private final Class<?> valueClass;

    private final Class<? extends IObject<?, ?>> objectClass;

    private final String caption;

    /**
     * See com.pyx4j.entity.annotations.Caption
     */
    private final String description;

    /**
     * See com.pyx4j.entity.annotations.StringLength
     */
    private final int stringLength;

    private final String format;

    private EditorType editorType;

    private Set<Class<?>> annotations;

    public ClientMemberMetaImpl(Class<?> valueClass, Class<? extends IObject<?, ?>> objectClass, boolean entity, String fieldName, String caption,
            String description, boolean persistenceTransient, boolean rpcTransient, boolean detached, boolean ownedRelationships, boolean embedded,
            boolean indexed, int stringLength, String format) {
        super();
        this.fieldName = fieldName;
        this.entity = entity;
        this.persistenceTransient = persistenceTransient;
        this.rpcTransient = rpcTransient;
        this.detached = detached;
        this.ownedRelationships = ownedRelationships;
        this.embedded = embedded;
        this.indexed = indexed;
        this.valueClass = valueClass;
        this.objectClass = objectClass;
        this.caption = caption;
        this.description = description;
        this.stringLength = stringLength;
        this.format = format;
    }

    @Override
    public String getFieldName() {
        return fieldName;
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
    public boolean isDetached() {
        return detached;
    }

    @Override
    public boolean isOwnedRelationships() {
        return ownedRelationships;
    }

    @Override
    public boolean isEmbedded() {
        return embedded;
    }

    @Override
    public boolean isIndexed() {
        return indexed;
    }

    @Override
    public boolean isEntity() {
        return entity;
    }

    @Override
    public Class<?> getValueClass() {
        return valueClass;
    }

    @Override
    public Class<? extends IObject<?, ?>> getObjectClass() {
        return objectClass;
    }

    @Override
    public int getStringLength() {
        return stringLength;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public List<Validator> getValidators() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EditorType getEditorType() {
        return this.editorType;
    }

    public void setEditorType(EditorType editorType) {
        this.editorType = editorType;
    }

    public void addValidatorAnnotation(Class<? extends Annotation> annotationClass) {
        if (annotations == null) {
            annotations = new HashSet<Class<?>>();
        }
        annotations.add(annotationClass);
    }

    @Override
    public boolean isValidatorAnnotationPresent(Class<? extends Annotation> annotationClass) {
        if (annotations == null) {
            return false;
        } else {
            return annotations.contains(annotationClass);
        }
    }

}

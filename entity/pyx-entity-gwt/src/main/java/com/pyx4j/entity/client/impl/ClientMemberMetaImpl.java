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
 */
package com.pyx4j.entity.client.impl;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pyx4j.config.shared.ApplicationBackend;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.core.meta.OwnedConstraint;
import com.pyx4j.entity.core.validator.Validator;
import com.pyx4j.i18n.shared.I18n;

public class ClientMemberMetaImpl implements MemberMeta {

    private static final I18n i18n = I18n.get(ClientMemberMetaImpl.class);

    private final String fieldName;

    private final String caption;

    /**
     * See com.pyx4j.entity.annotations.Caption
     */
    private final String description;

    /**
     * See com.pyx4j.entity.annotations.Caption
     */
    private final String watermark;

    private final MemberMetaData data;

    private final boolean indexed;

    private EditorType editorType;

    private String editorDescriptor;

    private Set<Class<?>> annotations;

    /**
     * Generic constructor
     */
    public ClientMemberMetaImpl(String fieldName, String caption, String description, String watermark, Class<?> valueClass,
            @SuppressWarnings("rawtypes") Class<? extends IObject> objectClass, ObjectClassType objectClassType, boolean valueClassIsNumber,
            boolean persistenceTransient, boolean rpcTransient, boolean logTransient, AttachLevel attachLevel, boolean ownedRelationships,
            boolean cascadePersist, boolean owner, boolean embedded, boolean indexed, int stringLength, String format, boolean useMessageFormat,
            String nullString, boolean isToStringMember, List<OwnedConstraint> ownedConstraints) {
        super();
        this.data = new MemberMetaData();
        this.data.valueClass = valueClass;
        this.data.valueClassIsNumber = valueClassIsNumber;
        this.fieldName = fieldName;
        this.data.persistenceTransient = persistenceTransient;
        this.data.rpcTransient = rpcTransient;
        this.data.logTransient = logTransient;
        this.data.attachLevel = attachLevel;
        this.data.ownedRelationships = ownedRelationships;
        this.data.cascadePersist = cascadePersist;
        this.data.owner = owner;
        this.data.embedded = embedded;
        this.indexed = indexed;
        this.data.objectClass = objectClass;
        this.data.objectClassType = objectClassType;
        this.caption = caption;
        this.description = description;
        this.watermark = watermark;
        if (stringLength == -1) {
            this.data.stringLength = ApplicationBackend.getDefaultDataStringLength();
        } else {
            this.data.stringLength = stringLength;
        }
        this.data.format = format;
        this.data.useMessageFormat = useMessageFormat;
        this.data.nullString = nullString;
        this.data.isToStringMember = isToStringMember;
        if (ownedConstraints != null) {
            this.data.ownedConstraints = Collections.unmodifiableList(ownedConstraints);
        }
    }

    public ClientMemberMetaImpl(String fieldName, String caption, String description, String watermark, boolean indexed, MemberMetaData data) {
        this.fieldName = fieldName;
        this.caption = caption;
        this.description = description;
        this.watermark = watermark;
        this.indexed = indexed;
        this.data = data;
        if (data.stringLength == -1) {
            data.stringLength = ApplicationBackend.getDefaultDataStringLength();
        }
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getCaption() {
        return i18n.tr(caption);
    }

    @Override
    public String getCaptionNL() {
        return caption;
    }

    @Override
    public String getDescription() {
        return i18n.tr(description);
    }

    @Override
    public String getWatermark() {
        return i18n.tr(watermark);
    }

    @Override
    public boolean isTransient() {
        return data.persistenceTransient;
    }

    @Override
    public boolean isRpcTransient() {
        return data.rpcTransient;
    }

    @Override
    public boolean isLogTransient() {
        return data.logTransient;
    }

    @Override
    public boolean isDetached() {
        return data.attachLevel != AttachLevel.Attached;
    }

    @Override
    public AttachLevel getAttachLevel() {
        return data.attachLevel;
    }

    @Override
    public boolean isOwnedRelationships() {
        return data.ownedRelationships;
    }

    @Override
    public List<OwnedConstraint> getOwnedConstraints() {
        return data.ownedConstraints;
    }

    @Override
    public boolean isCascadePersist() {
        return data.cascadePersist;
    }

    @Override
    public boolean isCascadeDelete() {
        return data.cascadeDelete;
    }

    @Override
    public boolean isOwner() {
        return data.owner;
    }

    @Override
    public boolean isEmbedded() {
        return data.embedded;
    }

    @Override
    public boolean isIndexed() {
        return indexed;
    }

    @Override
    public Class<?> getValueClass() {
        return data.valueClass;
    }

    @Override
    public boolean isEntity() {
        return data.objectClassType == ObjectClassType.Entity;
    }

    @Override
    public boolean isNumberValueClass() {
        return data.valueClassIsNumber;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends IObject> getObjectClass() {
        return data.objectClass;
    }

    @Override
    public ObjectClassType getObjectClassType() {
        return data.objectClassType;
    }

    @Override
    public int getLength() {
        return data.stringLength;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Annotation> T[] getAnnotations(Class<T> annotationClass) {
        throw new UnsupportedOperationException();
    }

    public void addAnnotation(Class<? extends Annotation> annotationClass) {
        if (annotations == null) {
            annotations = new HashSet<Class<?>>();
        }
        annotations.add(annotationClass);
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        if (annotations == null) {
            return false;
        } else {
            return annotations.contains(annotationClass);
        }
    }

    @Override
    public String getFormat() {
        return i18n.tr(data.format);
    }

    @Override
    public boolean useMessageFormat() {
        return data.useMessageFormat;
    }

    @Override
    public String getNullString() {
        return i18n.tr(data.nullString);
    }

    @Override
    public boolean isToStringMember() {
        return data.isToStringMember;
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

    @Override
    public String getEditorDescriptor() {
        return editorDescriptor;
    }

    public void setEditorDescriptor(String editorDescriptor) {
        this.editorDescriptor = editorDescriptor;
    }

}

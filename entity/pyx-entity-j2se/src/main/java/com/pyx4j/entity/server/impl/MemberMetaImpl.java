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
package com.pyx4j.entity.server.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.config.shared.ApplicationBackend;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.impl.PrimitiveHandler;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.entity.shared.validator.Validator;
import com.pyx4j.i18n.annotations.I18nAnnotation;

public class MemberMetaImpl implements MemberMeta {

    private final Method method;

    private final String fieldName;

    private final boolean persistenceTransient;

    private final boolean rpcTransient;

    private final boolean detached;

    private final boolean ownedRelationships;

    private final boolean owner;

    private final boolean embedded;

    private final boolean indexed;

    private final Class<?> valueClass;

    @SuppressWarnings("rawtypes")
    private final Class<? extends IObject> objectClass;

    private final ObjectClassType objectClassType;

    private final String caption;

    /**
     * See com.pyx4j.entity.annotations.Caption
     */
    private final String description;

    /**
     * See com.pyx4j.entity.annotations.Caption
     */
    private final String watermark;

    /**
     * See com.pyx4j.entity.annotations.Length
     */
    private final int length;

    @SuppressWarnings("unchecked")
    public MemberMetaImpl(Method method) {
        this.method = method;
        objectClass = (Class<? extends IObject<?>>) method.getReturnType();
        if (IPrimitive.class.equals(objectClass)) {
            valueClass = EntityImplReflectionHelper.primitiveValueClass(((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]);
            objectClassType = ObjectClassType.Primitive;
        } else if (IEntity.class.isAssignableFrom(objectClass)) {
            valueClass = objectClass;
            objectClassType = ObjectClassType.Entity;
        } else if (ISet.class.equals(objectClass)) {
            valueClass = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            objectClassType = ObjectClassType.EntitySet;
        } else if (IList.class.equals(objectClass)) {
            valueClass = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            objectClassType = ObjectClassType.EntityList;
        } else if (IPrimitiveSet.class.equals(objectClass)) {
            valueClass = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            objectClassType = ObjectClassType.PrimitiveSet;
        } else {
            throw new RuntimeException("Unknown member type" + objectClass);
        }
        fieldName = method.getName();

        // Read Annotations
        Length lengthAnnotation = method.getAnnotation(Length.class);
        if (String.class.equals(valueClass)) {
            if (lengthAnnotation != null) {
                length = lengthAnnotation.value();
            } else {
                length = ApplicationBackend.getDefaultDataStringLength();
            }
        } else if (EnumSet.of(ObjectClassType.PrimitiveSet, ObjectClassType.EntityList, ObjectClassType.EntitySet).contains(objectClassType)) {
            if (lengthAnnotation != null) {
                length = lengthAnnotation.value();
            } else {
                length = 0;
            }
        } else if ((lengthAnnotation != null) && (PrimitiveHandler.BYTE_ARRAY_CLASS.equals(valueClass))) {
            length = lengthAnnotation.value();
        } else if (lengthAnnotation != null) {
            throw new RuntimeException("Unexpected @Length annotation in member " + fieldName + " of " + method.getDeclaringClass().getSimpleName());
        } else {
            length = 0;
        }

        Caption captionAnnotation = method.getAnnotation(Caption.class);
        String captionValue = I18nAnnotation.DEFAULT_VALUE;
        if (captionAnnotation != null) {
            captionValue = captionAnnotation.name();
            description = captionAnnotation.description();
            watermark = captionAnnotation.watermark();
        } else {
            description = null;
            watermark = null;
        }
        if (I18nAnnotation.DEFAULT_VALUE.equals(captionValue)) {
            caption = EnglishGrammar.capitalize(fieldName);
        } else {
            caption = captionValue;
        }
        persistenceTransient = (method.getAnnotation(Transient.class) != null);
        rpcTransient = (method.getAnnotation(RpcTransient.class) != null);
        Owned aOwned = method.getAnnotation(Owned.class);
        boolean hasEmbedded = (method.getAnnotation(EmbeddedEntity.class) != null);
        if (hasEmbedded && ((objectClassType == ObjectClassType.Primitive) || (objectClassType == ObjectClassType.PrimitiveSet))) {
            throw new RuntimeException("Unexpected @EmbeddedEntity annotation in member " + fieldName + " of " + method.getDeclaringClass().getSimpleName());
        }
        if ((!hasEmbedded) && (objectClassType != ObjectClassType.Primitive) && (objectClassType != ObjectClassType.PrimitiveSet)) {
            hasEmbedded = (valueClass.getAnnotation(EmbeddedEntity.class) != null);
        }
        if (hasEmbedded) {
            embedded = true;
        } else if (aOwned != null) {
            embedded = aOwned.embedded();
        } else {
            embedded = false;
        }
        ownedRelationships = embedded || (aOwned != null);
        owner = (method.getAnnotation(Owner.class) != null);
        assert (!(owner == true && ownedRelationships == true));

        detached = (method.getAnnotation(Detached.class) != null);
        if ((aOwned != null) && detached) {
            //throw new RuntimeException("Unexpected @Detached annotation in member " + fieldName + " of " + method.getDeclaringClass().getSimpleName());
        }

        Indexed indexedAnnotation = method.getAnnotation(Indexed.class);
        indexed = (indexedAnnotation != null) && (indexedAnnotation.indexPrimaryValue());
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
    public boolean isDetached() {
        return detached;
    }

    @Override
    public boolean isOwnedRelationships() {
        return ownedRelationships;
    }

    @Override
    public boolean isOwner() {
        return owner;
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
    public Class<?> getValueClass() {
        return valueClass;
    }

    @Override
    public boolean isEntity() {
        return objectClassType == ObjectClassType.Entity;
    }

    @Override
    public boolean isNumberValueClass() {
        return Number.class.isAssignableFrom(valueClass);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<? extends IObject> getObjectClass() {
        return objectClass;
    }

    @Override
    public ObjectClassType getObjectClassType() {
        return objectClassType;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    @Override
    public String getFormat() {
        Format formatAnnotation = method.getAnnotation(Format.class);
        if (formatAnnotation != null) {
            return formatAnnotation.value();
        } else {
            return null;
        }
    }

    @Override
    public boolean useMessageFormat() {
        Format formatAnnotation = method.getAnnotation(Format.class);
        if (formatAnnotation != null) {
            return formatAnnotation.messageFormat();
        } else {
            return false;
        }
    }

    @Override
    public String getNullString() {
        Format formatAnnotation = method.getAnnotation(Format.class);
        if (formatAnnotation != null) {
            return formatAnnotation.nil();
        } else {
            return "";
        }
    }

    @Override
    public EditorType getEditorType() {
        Editor editorAnnotation = method.getAnnotation(Editor.class);
        if (editorAnnotation == null) {
            return null;
        } else {
            return editorAnnotation.type();
        }
    }

    @Override
    public List<Validator> getValidators() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isValidatorAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return method.isAnnotationPresent(annotationClass);
    }

    @Override
    public String toString() {
        return "MemberMeta '" + fieldName + "' " + valueClass.getSimpleName();
    }

}

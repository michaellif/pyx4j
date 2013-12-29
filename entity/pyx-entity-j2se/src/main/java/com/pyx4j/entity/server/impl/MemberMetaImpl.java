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
import com.pyx4j.entity.annotations.CascadeType;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.LogTransient;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.impl.PrimitiveHandler;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.core.validator.Validator;
import com.pyx4j.i18n.annotations.I18nAnnotation;
import com.pyx4j.i18n.shared.I18n;

public class MemberMetaImpl implements MemberMeta {

    private static final I18n i18n = I18n.get(MemberMetaImpl.class);

    private final Method method;

    private final String fieldName;

    private final boolean persistenceTransient;

    private final boolean rpcTransient;

    private final boolean logTransient;

    private final AttachLevel attachLevel;

    private final boolean ownedRelationships;

    public final boolean cascadePersist;

    public final boolean cascadeDelete;

    private final boolean owner;

    private final boolean embedded;

    private final boolean indexed;

    private final Class<?> valueClass;

    @SuppressWarnings("rawtypes")
    private final Class<? extends IObject> objectClass;

    private final ObjectClassType objectClassType;

    private final String i18nContext;

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

    private final boolean isToStringMember;

    @SuppressWarnings("unchecked")
    public MemberMetaImpl(Class<? extends IEntity> interfaceClass, Method method) {
        this.method = method;
        @SuppressWarnings("rawtypes")
        Class<? extends IObject> methodReturnType = (Class<? extends IObject<?>>) method.getReturnType();
        try {
            if (IPrimitive.class.equals(methodReturnType)) {
                valueClass = EntityImplReflectionHelper.primitiveValueClass(((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0],
                        interfaceClass);
                objectClassType = ObjectClassType.Primitive;
            } else if (IEntity.class.isAssignableFrom(methodReturnType)) {
                Class<?> genericClass = EntityImplReflectionHelper.resolveGenericType(method.getGenericReturnType(), interfaceClass);
                if (genericClass != null) {
                    valueClass = genericClass;
                    methodReturnType = (Class<? extends IObject<?>>) genericClass;
                } else {
                    valueClass = methodReturnType;
                }
                objectClassType = ObjectClassType.Entity;
            } else if (ISet.class.equals(methodReturnType)) {
                valueClass = EntityImplReflectionHelper.resolveTypeGenericArgumentType(method.getGenericReturnType(), interfaceClass);
                objectClassType = ObjectClassType.EntitySet;
            } else if (IList.class.equals(methodReturnType)) {
                valueClass = EntityImplReflectionHelper.resolveTypeGenericArgumentType(method.getGenericReturnType(), interfaceClass);
                objectClassType = ObjectClassType.EntityList;
            } else if (IPrimitiveSet.class.equals(methodReturnType)) {
                valueClass = EntityImplReflectionHelper.resolveTypeGenericArgumentType(method.getGenericReturnType(), interfaceClass);
                objectClassType = ObjectClassType.PrimitiveSet;
            } else {
                throw new RuntimeException("Unknown member type " + methodReturnType);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Unresolved member '" + method.getName() + "' of " + interfaceClass.getSimpleName(), e);
        }
        objectClass = methodReturnType;
        fieldName = method.getName();

        assert (valueClass != null) : "Data type error in method '" + method.getName() + "' of " + interfaceClass.getName();

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
            throw new RuntimeException("Unexpected @Length annotation in member '" + fieldName + "' of " + method.getDeclaringClass().getSimpleName());
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

        com.pyx4j.i18n.annotations.I18n trCfg = interfaceClass.getAnnotation(com.pyx4j.i18n.annotations.I18n.class);
        String context = null;
        if (trCfg != null) {
            context = trCfg.context();
        }
        i18nContext = context;

        persistenceTransient = (method.getAnnotation(Transient.class) != null);
        rpcTransient = (method.getAnnotation(RpcTransient.class) != null);
        logTransient = (method.getAnnotation(LogTransient.class) != null);
        Owned aOwned = method.getAnnotation(Owned.class);
        boolean hasEmbedded = (method.getAnnotation(EmbeddedEntity.class) != null);
        if (hasEmbedded && ((objectClassType == ObjectClassType.Primitive) || (objectClassType == ObjectClassType.PrimitiveSet))) {
            throw new RuntimeException("Unexpected @EmbeddedEntity annotation in member '" + fieldName + "' of " + method.getDeclaringClass().getSimpleName());
        }
        if ((!hasEmbedded) && (objectClassType != ObjectClassType.Primitive) && (objectClassType != ObjectClassType.PrimitiveSet)) {
            hasEmbedded = (valueClass.getAnnotation(EmbeddedEntity.class) != null);
        }
        embedded = hasEmbedded;
        ownedRelationships = embedded || (aOwned != null) || (objectClassType == ObjectClassType.PrimitiveSet);
        owner = (method.getAnnotation(Owner.class) != null);
        assert (!(owner == true && ownedRelationships == true));

        boolean cascadePersist = false;
        boolean cascadeDelete = false;
        if (aOwned != null) {
            for (CascadeType ct : aOwned.cascade()) {
                switch (ct) {
                case ALL:
                    cascadePersist = true;
                    cascadeDelete = true;
                    break;
                case PERSIST:
                    cascadePersist = true;
                    break;
                case DELETE:
                    cascadeDelete = true;
                    break;
                }
            }
        } else {
            JoinTable joinTable = method.getAnnotation(JoinTable.class);
            if (joinTable != null) {
                for (CascadeType ct : joinTable.cascade()) {
                    switch (ct) {
                    case ALL:
                        cascadePersist = true;
                        cascadeDelete = true;
                        break;
                    case PERSIST:
                        cascadePersist = true;
                        break;
                    case DELETE:
                        cascadeDelete = true;
                        break;
                    }
                }
            } else {
                cascadePersist = false;
                cascadeDelete = false;
            }
        }
        this.cascadePersist = cascadePersist;
        this.cascadeDelete = cascadeDelete;

        Detached detachedAnnotation = method.getAnnotation(Detached.class);
        if (detachedAnnotation == null) {
            attachLevel = AttachLevel.Attached;
        } else if (detachedAnnotation.level() == null) {
            attachLevel = AttachLevel.getDefault(objectClassType);
        } else {
            attachLevel = detachedAnnotation.level();
        }

        //if ((aOwned != null) && attachLevel != AttachLevel.Attached) {
        //throw new RuntimeException("Unexpected @Detached annotation in member '" + fieldName + "' of " + method.getDeclaringClass().getSimpleName());
        //}

        Indexed indexedAnnotation = method.getAnnotation(Indexed.class);
        indexed = (indexedAnnotation != null) && (indexedAnnotation.indexPrimaryValue());

        isToStringMember = (method.getAnnotation(ToString.class) != null);
    }

    @Override
    public final String getFieldName() {
        return fieldName;
    }

    @Override
    public final String getCaption() {
        return i18n.translate(i18nContext, caption);
    }

    @Override
    public final String getCaptionNL() {
        return caption;
    }

    @Override
    public final String getDescription() {
        return i18n.translate(i18nContext, description);
    }

    @Override
    public final String getWatermark() {
        return i18n.translate(i18nContext, watermark);
    }

    @Override
    public final boolean isTransient() {
        return persistenceTransient;
    }

    @Override
    public final boolean isRpcTransient() {
        return rpcTransient;
    }

    @Override
    public final boolean isLogTransient() {
        return logTransient;
    }

    @Override
    public final boolean isDetached() {
        return attachLevel != AttachLevel.Attached;
    }

    @Override
    public final AttachLevel getAttachLevel() {
        return attachLevel;
    }

    @Override
    public final boolean isOwnedRelationships() {
        return ownedRelationships;
    }

    @Override
    public final boolean isCascadePersist() {
        return cascadePersist;
    }

    @Override
    public final boolean isCascadeDelete() {
        return cascadeDelete;
    }

    @Override
    public final boolean isOwner() {
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
            return i18n.translate(i18nContext, formatAnnotation.value());
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
            return i18n.translate(i18nContext, formatAnnotation.nil());
        } else {
            return "";
        }
    }

    @Override
    public boolean isToStringMember() {
        return isToStringMember;
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

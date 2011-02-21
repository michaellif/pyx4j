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
 * Created on 2010-11-16
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rebind;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.StringLength;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.annotations.validator.Pattern;
import com.pyx4j.entity.client.impl.ClientEntityMetaImpl;
import com.pyx4j.entity.client.impl.ClientMemberMetaImpl;
import com.pyx4j.entity.client.impl.MemberMetaData;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityMetaWriter {

    static String META_IMPL = "_Meta" + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;

    static void createEntityMetaImpl(TreeLogger logger, ContextHelper contextHelper, JClassType interfaceType) throws UnableToCompleteException {
        TreeLogger implLogger = logger.branch(TreeLogger.DEBUG, "Creating EntityMeta implementation for " + interfaceType.getName());
        String packageName = interfaceType.getPackage().getName();
        String simpleName = interfaceType.getSimpleSourceName() + META_IMPL;
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

        composer.addImport(IObject.class.getName());
        composer.addImport(IEntity.class.getName());
        composer.addImport(MemberMeta.class.getName());
        composer.addImport(ObjectClassType.class.getName());
        composer.addImport(ClientMemberMetaImpl.class.getName());
        composer.setSuperclass(ClientEntityMetaImpl.class.getName());

        PrintWriter printWriter = contextHelper.context.tryCreate(implLogger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
        if (printWriter == null) {
            // the generated type already exists
            return;
        }
        SourceWriter writer = composer.createSourceWriter(contextHelper.context, printWriter);
        writeEntityMetaImpl(implLogger, contextHelper, writer, simpleName, interfaceType);
        writer.commit(implLogger);
    }

    static String escapeSourceString(String value) {
        if (value == null) {
            return "null";
        } else {
            return "\"" + value.replace("\"", "\\\"").replace("\n", "\\n") + "\"";
        }
    }

    static String i18nEscapeSourceString(String value) {
        String s = escapeSourceString(value);
        if (s.equals("\"\"") || s.equals("null")) {
            return s;
        } else {
            return "i18n.tr(" + s + ")";
        }
    }

    static void writeEntityMetaImpl(TreeLogger logger, ContextHelper contextHelper, SourceWriter writer, String simpleName, JClassType interfaceType)
            throws UnableToCompleteException {

        String caption;
        String description = null;
        String watermark = null;
        Caption captionAnnotation = interfaceType.getAnnotation(Caption.class);
        if ((captionAnnotation != null) && (CommonsStringUtils.isStringSet(captionAnnotation.name()))) {
            caption = captionAnnotation.name();
        } else {
            caption = EnglishGrammar.capitalize(interfaceType.getSimpleSourceName());
        }
        if (captionAnnotation != null) {
            description = captionAnnotation.description();
            watermark = captionAnnotation.watermark();
        }
        Boolean persistenceTransient = (interfaceType.getAnnotation(Transient.class) != null);
        Boolean rpcTransient = (interfaceType.getAnnotation(RpcTransient.class) != null) || (interfaceType.getAnnotation(RpcBlacklist.class) != null);

        List<String> toStringMemberNames = new Vector<String>();
        final HashMap<String, ToString> sortKeys = new HashMap<String, ToString>();

        List<JMethod> allMethods = contextHelper.getAllEntityMethods(interfaceType);

        String ownerMemberName = null;

        for (JMethod method : allMethods) {
            ToString ts = method.getAnnotation(ToString.class);
            if (ts != null) {
                toStringMemberNames.add(method.getName());
                sortKeys.put(method.getName(), ts);
            }
            if (method.getAnnotation(Owner.class) != null) {
                if (ownerMemberName != null) {
                    throw new Error("Duplicate @Owner declaration " + method.getName() + " and " + ownerMemberName);
                }
                ownerMemberName = method.getName();
            }
        }

        Collections.sort(toStringMemberNames, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int v1 = sortKeys.get(o1).index();
                int v2 = sortKeys.get(o2).index();
                return (v1 < v2 ? -1 : (v1 == v2 ? 0 : 1));
            }
        });
        StringBuilder toStringMemberNamesStringArray = new StringBuilder();
        for (String memberName : toStringMemberNames) {
            if (toStringMemberNamesStringArray.length() > 0) {
                toStringMemberNamesStringArray.append(", ");
            }
            toStringMemberNamesStringArray.append(escapeSourceString(memberName));
        }

        writer.println();
        writer.indent();
        writer.println("public " + simpleName + "() { ");
        writer.indent();
        writer.print("super(");

        writer.print(interfaceType.getName());
        writer.print(".class, ");

        writer.print(i18nEscapeSourceString(caption));
        writer.print(", ");

        writer.print(i18nEscapeSourceString(description));
        writer.print(", ");

        writer.print(i18nEscapeSourceString(watermark));
        writer.print(", ");

        writer.print(persistenceTransient.toString());
        writer.print(", ");

        writer.print(rpcTransient.toString());
        writer.print(", ");

        ToStringFormat toStringFormatAnnotation = interfaceType.getAnnotation(ToStringFormat.class);
        if (toStringFormatAnnotation != null) {
            writer.print(i18nEscapeSourceString(toStringFormatAnnotation.value()));
            writer.print(", ");
            writer.print(i18nEscapeSourceString(toStringFormatAnnotation.nil()));
        } else {
            writer.print("null, \"\"");
        }
        writer.print(", ");

        writer.print(escapeSourceString(ownerMemberName));
        writer.print(", ");

        writer.print("new String[] {");
        writer.print(toStringMemberNamesStringArray.toString());
        writer.print("}");

        writer.println(");");
        writer.outdent();
        writer.println("}");

        // Other methods

        writer.println();
        writer.println("@Override");
        writer.println("public <T extends IEntity> boolean isEntityClassAssignableFrom(T targetInstance) {");
        writer.indent();
        writer.println("return (targetInstance instanceof " + interfaceType.getName() + ");");
        writer.outdent();
        writer.println("}");

        writeEntityMemberMetaImpl(logger, contextHelper, writer, allMethods, interfaceType);

        writer.outdent();
    }

    static boolean addValidatorAnnotation(SourceWriter writer, JMethod method, Class<? extends Annotation> annotationClass) {
        if (method.isAnnotationPresent(annotationClass)) {
            writer.print("mm.addValidatorAnnotation(");
            writer.print(annotationClass.getName());
            writer.print(".class");
            writer.println(");");
            return true;
        } else {
            return false;
        }
    }

    static Map<String, MemberMetaData> defaultMembers = new HashMap<String, MemberMetaData>();

    static {
        defaultMembers.put("defaultStringMember", MemberMetaData.defaultStringMember);
        defaultMembers.put("defaultBooleanMember", MemberMetaData.defaultBooleanMember);
        defaultMembers.put("defaultDoubleMember", MemberMetaData.defaultDoubleMember);
        defaultMembers.put("defaultIntegerMember", MemberMetaData.defaultIntegerMember);
        defaultMembers.put("defaultDateMember", MemberMetaData.defaultDateMember);
        defaultMembers.put("defaultSqlDateMember", MemberMetaData.defaultSqlDateMember);
    }

    static String selectDefaultData(MemberMetaDataGeneration data) {
        for (Map.Entry<String, MemberMetaData> m : defaultMembers.entrySet()) {
            if (data.isDataEquals(m.getValue())) {
                return m.getKey();
            }
        }
        return null;
    }

    //----------
    static void writeEntityMemberMetaImpl(TreeLogger logger, ContextHelper contextHelper, SourceWriter writer, List<JMethod> allMethods,
            JClassType interfaceType) throws UnableToCompleteException {
        writer.println();
        writer.println("@Override");
        writer.println("protected MemberMeta createMemberMeta(String memberName) {");
        writer.indent();

        for (JMethod method : allMethods) {
            if (!contextHelper.isEntityMemeber(method)) {
                continue;
            }
            JClassType type = (JClassType) method.getReturnType();

            MemberMetaDataGeneration data = new MemberMetaDataGeneration();
            data.objectClassSourceName = type.getQualifiedSourceName();

            JClassType valueClass;
            // Class<?> valueClass, Class<? extends IObject<?>> objectClass,
            if (type.isAssignableTo(contextHelper.iPrimitiveInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("IPrimitive " + method.getName() + " type should be ParameterizedType in interface '"
                            + interfaceType.getQualifiedSourceName() + "'");
                }
                valueClass = ((JParameterizedType) type).getTypeArgs()[0];

                data.valueClassSourceName = valueClass.getQualifiedSourceName();
                data.valueClassIsNumber = contextHelper.isNumber(valueClass);
                data.objectClassType = ObjectClassType.Primitive;
            } else if (type.isAssignableTo(contextHelper.iPrimitiveSetInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("IPrimitiveSet " + method.getName() + " type should be ParameterizedType in interface '"
                            + interfaceType.getQualifiedSourceName() + "'");
                }
                valueClass = ((JParameterizedType) type).getTypeArgs()[0];

                data.valueClassSourceName = valueClass.getQualifiedSourceName();
                data.valueClassIsNumber = contextHelper.isNumber(valueClass);
                data.objectClassType = ObjectClassType.PrimitiveSet;
            } else if (type.isAssignableTo(contextHelper.iSetInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("ISet " + method.getName() + " type should be ParameterizedType in interface '"
                            + interfaceType.getQualifiedSourceName() + "'");
                }
                valueClass = ((JParameterizedType) type).getTypeArgs()[0];
                data.valueClassSourceName = valueClass.getQualifiedSourceName();
                data.objectClassType = ObjectClassType.EntitySet;
            } else if (type.isAssignableTo(contextHelper.iListInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("IList " + method.getName() + " type should be ParameterizedType in interface '"
                            + interfaceType.getQualifiedSourceName() + "'");
                }
                valueClass = ((JParameterizedType) type).getTypeArgs()[0];

                data.valueClassSourceName = valueClass.getQualifiedSourceName();
                data.objectClassType = ObjectClassType.EntityList;
            } else if (type.isAssignableTo(contextHelper.iEnentityInterfaceType)) {
                valueClass = type;

                data.valueClassSourceName = valueClass.getQualifiedSourceName();
                data.objectClassType = ObjectClassType.Entity;
            } else {
                logger.log(TreeLogger.Type.ERROR, "Unknown member type '" + type.getQualifiedSourceName() + "' of method '" + method.getName()
                        + "' in interface '" + interfaceType.getQualifiedSourceName() + "'");
                logger.log(TreeLogger.Type.ERROR, "Only IEntity, IPrimitive<>, IPrimitiveSet<>, ISet<>, IList<> are expected.");
                throw new UnableToCompleteException();
            }

            String memeberCaption;
            String memeberDescription = null;
            String memeberWatermark = null;
            Caption memeberCaptionAnnotation = method.getAnnotation(Caption.class);
            if (memeberCaptionAnnotation != null) {
                memeberCaption = memeberCaptionAnnotation.name();
            } else {
                memeberCaption = EnglishGrammar.capitalize(method.getName());
            }
            if (memeberCaptionAnnotation != null) {
                memeberDescription = memeberCaptionAnnotation.description();
                memeberWatermark = memeberCaptionAnnotation.watermark();
            }

            data.persistenceTransient = (method.getAnnotation(Transient.class) != null);
            if ((!data.persistenceTransient) && (contextHelper.validateReservedKeywordsMemebers)) {
                ReservedWords.validate(logger, interfaceType, method);
            }
            data.rpcTransient = (method.getAnnotation(RpcTransient.class) != null);
            data.detached = (method.getAnnotation(Detached.class) != null);
            data.embedded = (method.getAnnotation(EmbeddedEntity.class) != null) || (valueClass.getAnnotation(EmbeddedEntity.class) != null);
            data.owner = (method.getAnnotation(Owner.class) != null);
            data.ownedRelationships = (method.getAnnotation(Owned.class) != null) || (data.embedded);

            Indexed indexedAnnotation = method.getAnnotation(Indexed.class);
            boolean indexed = (indexedAnnotation != null) && (indexedAnnotation.indexPrimaryValue());

            StringLength stringLengthAnnotation = method.getAnnotation(StringLength.class);
            if (stringLengthAnnotation != null) {
                data.stringLength = stringLengthAnnotation.value();
            } else {
                data.stringLength = -1;
            }

            Format formatAnnotation = method.getAnnotation(Format.class);
            if (formatAnnotation != null) {
                data.format = formatAnnotation.value();
                data.useMessageFormat = formatAnnotation.messageFormat();
                data.nullString = formatAnnotation.nil();
            } else {
                data.nullString = "";
            }

            String useDefaultData = selectDefaultData(data);

            boolean requireAdditionalData = (method.isAnnotationPresent(Editor.class))
                    || (method.isAnnotationPresent(NotNull.class))
                    || (method.isAnnotationPresent(Pattern.class) || (method.isAnnotationPresent(ReadOnly.class)) || (method
                            .isAnnotationPresent(Timestamp.class)));

            /// Write implementation
            //writer.println("if (\"" + method.getName() + "\".equals(memberName)) {");
            writer.println("if (memberName.equals(\"" + method.getName() + "\")) {");
            writer.indent();

            if (requireAdditionalData) {
                writer.print(ClientMemberMetaImpl.class.getSimpleName());
                writer.print(" mm = new ");
            } else {
                writer.print("return new ");
            }

            writer.print(ClientMemberMetaImpl.class.getSimpleName());
            writer.print("(");

            // String fieldName, String caption, String description,
            writer.print(escapeSourceString(method.getName()));
            writer.print(", ");
            writer.print(i18nEscapeSourceString(memeberCaption));
            writer.print(", ");
            writer.print(i18nEscapeSourceString(memeberDescription));
            writer.print(", ");
            writer.print(i18nEscapeSourceString(memeberWatermark));
            writer.println(", ");
            writer.indent();

            if (useDefaultData != null) {
                writer.print(Boolean.valueOf(indexed).toString() + ", ");
                writer.print(MemberMetaData.class.getName() + "." + useDefaultData);
            } else {
                writeDataParams(writer, data, indexed);
            }
            //

            writer.println(");");
            writer.outdent();

            if (requireAdditionalData) {
                if (method.isAnnotationPresent(Editor.class)) {
                    writer.print("mm.setEditorType(");
                    writer.print(Editor.class.getName() + "." + Editor.EditorType.class.getSimpleName() + ".");
                    writer.print(method.getAnnotation(Editor.class).type().name());
                    writer.println(");");
                }

                addValidatorAnnotation(writer, method, NotNull.class);
                addValidatorAnnotation(writer, method, Pattern.class);
                addValidatorAnnotation(writer, method, ReadOnly.class);
                addValidatorAnnotation(writer, method, Timestamp.class);

                writer.println("return mm;");
            }

            writer.outdent();
            writer.println("}");
        }
        writer.println("return null;");
        writer.outdent();
        writer.println("}");
    }

    private static void writeDataParams(SourceWriter writer, MemberMetaData data, boolean indexed) {
        if (data instanceof MemberMetaDataGeneration) {
            writer.print(((MemberMetaDataGeneration) data).valueClassSourceName + ".class, ");
        } else {
            writer.print(data.valueClass.getName() + ".class, ");
        }

        writer.print("(Class<? extends IObject<?>>)");
        if (data instanceof MemberMetaDataGeneration) {
            writer.println(((MemberMetaDataGeneration) data).objectClassSourceName + ".class, ");
        } else {
            writer.println(data.objectClass.getName() + ".class, ");
        }
        writer.println(ObjectClassType.class.getSimpleName() + "." + data.objectClassType.name() + ", ");

        writer.print(Boolean.valueOf(data.valueClassIsNumber).toString() + ", ");
        writer.print(Boolean.valueOf(data.persistenceTransient).toString() + ", ");
        writer.print(Boolean.valueOf(data.rpcTransient).toString() + ", ");
        writer.print(Boolean.valueOf(data.detached).toString() + ", ");
        writer.print(Boolean.valueOf(data.ownedRelationships).toString() + ", ");
        writer.print(Boolean.valueOf(data.owner).toString() + ", ");
        writer.print(Boolean.valueOf(data.embedded).toString() + ", ");
        writer.print(Boolean.valueOf(indexed).toString() + ", ");
        writer.print(data.stringLength + ", ");
        writer.print(i18nEscapeSourceString(data.format) + ", ");
        writer.print(Boolean.valueOf(data.useMessageFormat).toString() + ", ");
        writer.print(i18nEscapeSourceString(data.nullString));
    }
}

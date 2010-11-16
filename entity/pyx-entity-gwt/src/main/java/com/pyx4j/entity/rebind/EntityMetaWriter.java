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
import java.util.Vector;

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
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.StringLength;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.annotations.validator.Pattern;
import com.pyx4j.entity.client.impl.ClientEntityMetaImpl;
import com.pyx4j.entity.client.impl.ClientMemberMetaImpl;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityMetaWriter {

    static String META_IMPL = "_Meta" + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;

    static void createEntityMetaImpl(ContextHelper contextHelper, JClassType interfaceType) {
        String packageName = interfaceType.getPackage().getName();
        String simpleName = interfaceType.getSimpleSourceName() + META_IMPL;
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

        composer.addImport(IObject.class.getName());
        composer.addImport(MemberMeta.class.getName());
        composer.addImport(ClientMemberMetaImpl.class.getName());
        composer.setSuperclass(ClientEntityMetaImpl.class.getName());

        PrintWriter printWriter = contextHelper.context.tryCreate(contextHelper.logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
        if (printWriter == null) {
            // the generated type already exists
            return;
        }
        SourceWriter writer = composer.createSourceWriter(contextHelper.context, printWriter);
        writeEntityMetaImpl(contextHelper, writer, simpleName, interfaceType);
        writer.commit(contextHelper.logger);
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

    static void writeEntityMetaImpl(ContextHelper contextHelper, SourceWriter writer, String simpleName, JClassType interfaceType) {

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

        for (JMethod method : allMethods) {
            ToString ts = method.getAnnotation(ToString.class);
            if (ts != null) {
                toStringMemberNames.add(method.getName());
                sortKeys.put(method.getName(), ts);
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

        writer.print("new String[] {");
        writer.print(toStringMemberNamesStringArray.toString());
        writer.print("}");

        writer.println(");");
        writer.outdent();
        writer.println("}");

        //----------

        writer.println();
        writer.println("@Override");
        writer.println("protected MemberMeta createMemberMeta(String memberName) {");
        writer.indent();

        for (JMethod method : allMethods) {
            if (!contextHelper.isEntityMemeber(method)) {
                continue;
            }
            JClassType type = (JClassType) method.getReturnType();
            writer.println("if (\"" + method.getName() + "\".equals(memberName)) {");
            writer.indent();

            writer.print(ClientMemberMetaImpl.class.getSimpleName());
            writer.print(" mm = new ");
            writer.print(ClientMemberMetaImpl.class.getSimpleName());
            writer.print("(");

            JClassType valueClass;
            // Class<?> valueClass, Class<? extends IObject<?>> objectClass,
            if (type.isAssignableTo(contextHelper.iPrimitiveInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("IPrimitive " + method.getName() + " type should be ParameterizedType in interface '"
                            + interfaceType.getQualifiedSourceName() + "'");
                }
                valueClass = ((JParameterizedType) type).getTypeArgs()[0];
                writer.println(valueClass.getQualifiedSourceName() + ".class, ");
                writer.print("(Class<? extends IObject<?>>)");
                writer.println(type.getQualifiedSourceName() + ".class, ");
                writer.println("false, ");
                writer.print(Boolean.valueOf(contextHelper.isNumber(valueClass)).toString());
                writer.println(", ");
            } else if (type.isAssignableTo(contextHelper.iPrimitiveSetInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("IPrimitiveSet " + method.getName() + " type should be ParameterizedType in interface '"
                            + interfaceType.getQualifiedSourceName() + "'");
                }
                valueClass = ((JParameterizedType) type).getTypeArgs()[0];
                writer.println(valueClass.getQualifiedSourceName() + ".class, ");
                writer.print("(Class<? extends IObject<?>>)");
                writer.println(type.getQualifiedSourceName() + ".class, ");
                writer.println("false, ");
                writer.print(Boolean.valueOf(contextHelper.isNumber(valueClass)).toString());
                writer.println(", ");
            } else if (type.isAssignableTo(contextHelper.iSetInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("ISet " + method.getName() + " type should be ParameterizedType in interface '"
                            + interfaceType.getQualifiedSourceName() + "'");
                }
                valueClass = ((JParameterizedType) type).getTypeArgs()[0];
                writer.println(valueClass.getQualifiedSourceName() + ".class, ");
                writer.print("(Class<? extends IObject<?>>)");
                writer.println(type.getQualifiedSourceName() + ".class, ");
                writer.println("false, ");
                writer.println("false, ");
            } else if (type.isAssignableTo(contextHelper.iListInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("IList " + method.getName() + " type should be ParameterizedType in interface '"
                            + interfaceType.getQualifiedSourceName() + "'");
                }
                valueClass = ((JParameterizedType) type).getTypeArgs()[0];
                writer.println(valueClass.getQualifiedSourceName() + ".class, ");
                writer.print("(Class<? extends IObject<?>>)");
                writer.println(type.getQualifiedSourceName() + ".class, ");
                writer.println("false, ");
                writer.println("false, ");
            } else if (type.isAssignableTo(contextHelper.iEnentityInterfaceType)) {
                valueClass = type;
                writer.println(type.getQualifiedSourceName() + ".class, ");
                writer.println(type.getQualifiedSourceName() + ".class, ");
                writer.println("true, ");
                writer.println("false, ");
            } else {
                throw new RuntimeException("Unknown member type " + method.getReturnType() + " of method '" + method.getName() + "' in interface '"
                        + interfaceType.getQualifiedSourceName() + "'");
            }

            // String fieldName, String caption, String description,
            writer.print(escapeSourceString(method.getName()));
            writer.print(", ");

            String memeberCaption;
            String memeberDescription = null;
            String memeberWatermark = null;
            Caption memeberCaptionAnnotation = method.getAnnotation(Caption.class);
            if ((memeberCaptionAnnotation != null) && (CommonsStringUtils.isStringSet(memeberCaptionAnnotation.name()))) {
                memeberCaption = memeberCaptionAnnotation.name();
            } else {
                memeberCaption = EnglishGrammar.capitalize(method.getName());
            }
            if (memeberCaptionAnnotation != null) {
                memeberDescription = memeberCaptionAnnotation.description();
                memeberWatermark = memeberCaptionAnnotation.watermark();
            }
            writer.print(i18nEscapeSourceString(memeberCaption));
            writer.print(", ");

            writer.print(i18nEscapeSourceString(memeberDescription));
            writer.println(", ");

            writer.print(i18nEscapeSourceString(memeberWatermark));
            writer.println(", ");

            // persistenceTransient, rpcTransient, detached, ownedRelationships, owner, embedded, indexed, stringLength
            writer.print(Boolean.valueOf((method.getAnnotation(Transient.class) != null)).toString());
            writer.print(", ");
            writer.print(Boolean.valueOf((method.getAnnotation(RpcTransient.class) != null)).toString());
            writer.print(", ");
            writer.print(Boolean.valueOf(method.getAnnotation(Detached.class) != null).toString());
            writer.print(", ");
            boolean embedded = (method.getAnnotation(EmbeddedEntity.class) != null) || (valueClass.getAnnotation(EmbeddedEntity.class) != null);
            writer.print(Boolean.valueOf((method.getAnnotation(Owned.class) != null) || (embedded)).toString());
            writer.print(", ");
            writer.print(Boolean.valueOf(method.getAnnotation(Owner.class) != null).toString());
            writer.print(", ");
            writer.print(Boolean.valueOf(embedded).toString());
            writer.print(", ");
            Indexed indexedAnnotation = method.getAnnotation(Indexed.class);
            writer.print(Boolean.valueOf((indexedAnnotation != null) && (indexedAnnotation.indexPrimaryValue())).toString());
            writer.print(", ");

            StringLength stringLengthAnnotation = method.getAnnotation(StringLength.class);
            if (stringLengthAnnotation != null) {
                writer.print(String.valueOf(stringLengthAnnotation.value()));
            } else {
                writer.print("-1");
            }
            writer.print(", ");

            Format formatAnnotation = method.getAnnotation(Format.class);
            if (formatAnnotation != null) {
                writer.print(i18nEscapeSourceString(formatAnnotation.value()));
                writer.print(", ");
                writer.print(Boolean.valueOf(formatAnnotation.messageFormat()).toString());
                writer.print(", ");
                writer.print(i18nEscapeSourceString(formatAnnotation.nil()));
            } else {
                writer.print("null, false, \"\"");
            }

            writer.println(");");

            if (method.isAnnotationPresent(Editor.class)) {
                writer.print("mm.setEditorType(");
                writer.print(Editor.class.getName() + "." + Editor.EditorType.class.getSimpleName() + ".");
                writer.print(method.getAnnotation(Editor.class).type().name());
                writer.println(");");
            }

            addValidatorAnnotation(writer, method, NotNull.class);
            addValidatorAnnotation(writer, method, Pattern.class);

            writer.println("return mm;");
            writer.outdent();
            writer.println("}");
        }
        writer.println("throw new RuntimeException(\"Unknown member \" + memberName);");
        writer.outdent();
        writer.println("}");
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
}

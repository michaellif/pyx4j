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
 */
package com.pyx4j.entity.rebind;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.CascadeType;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.GwtAnnotation;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.JoinWhere;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.LogTransient;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.client.impl.ClientEntityMetaImpl;
import com.pyx4j.entity.client.impl.ClientMemberMetaImpl;
import com.pyx4j.entity.client.impl.MemberMetaData;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.core.meta.OwnedConstraint;
import com.pyx4j.i18n.annotations.I18nAnnotation;

public class EntityMetaWriter {

    static String metaImplClassName(String interfaceSimpleClassName) {
        return EntityHandlerWriter.implClassName(interfaceSimpleClassName + "_Meta");
    }

    static int createEntityMetaImpl(TreeLogger logger, ContextHelper contextHelper, JClassType interfaceType) throws UnableToCompleteException {
        TreeLogger implLogger = logger.branch(TreeLogger.DEBUG, "Creating EntityMeta implementation for " + interfaceType.getName());
        String packageName = interfaceType.getPackage().getName();
        String simpleName = metaImplClassName(interfaceType.getName());
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

        composer.addImport(IObject.class.getName());
        composer.addImport(IEntity.class.getName());
        composer.addImport(MemberMeta.class.getName());
        composer.addImport(ObjectClassType.class.getName());
        composer.addImport(AttachLevel.class.getName());
        composer.addImport(ClientMemberMetaImpl.class.getName());
        composer.setSuperclass(ClientEntityMetaImpl.class.getName());

        PrintWriter printWriter = contextHelper.context.tryCreate(implLogger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
        if (printWriter == null) {
            // the generated type already exists
            return 0;
        }
        SourceWriter writer = composer.createSourceWriter(contextHelper.context, printWriter);
        int validationErrors = writeEntityMetaImpl(implLogger, contextHelper, writer, simpleName, interfaceType);
        writer.commit(implLogger);
        return validationErrors;
    }

    static String escapeSourceString(String value) {
        if (value == null) {
            return "null";
        } else {
            return "\"" + Generator.escape(value) + "\"";
        }
    }

    static int writeEntityMetaImpl(TreeLogger logger, ContextHelper contextHelper, SourceWriter writer, String simpleName, JClassType interfaceType)
            throws UnableToCompleteException {

        String caption = I18nAnnotation.DEFAULT_VALUE;
        String description = null;
        String watermark = null;
        Caption captionAnnotation = interfaceType.getAnnotation(Caption.class);
        if (captionAnnotation != null) {
            caption = captionAnnotation.name();
            description = captionAnnotation.description();
            watermark = captionAnnotation.watermark();
        }
        if (I18nAnnotation.DEFAULT_VALUE.equals(caption)) {
            caption = EnglishGrammar.capitalize(EnglishGrammar.classNameToEnglish(interfaceType.getSimpleSourceName()));
        }
        Boolean persistenceTransient = (interfaceType.getAnnotation(Transient.class) != null);
        Boolean rpcTransient = (interfaceType.getAnnotation(RpcTransient.class) != null) || (interfaceType.getAnnotation(RpcBlacklist.class) != null);

        List<String> toStringMemberNames = new Vector<String>();
        final HashMap<String, ToString> sortKeys = new HashMap<String, ToString>();
        List<String> businessEqualMemberNames = new Vector<String>();

        String expandedFromClassName = null;
        if (interfaceType.getAnnotation(ExtendsBO.class) != null) {
            Class<?> expandedFromClass = interfaceType.getAnnotation(ExtendsBO.class).value();
            if (expandedFromClass != IEntity.class) {
                expandedFromClassName = expandedFromClass.getName();
            }
            if (expandedFromClassName == null) {
                if (interfaceType.getImplementedInterfaces().length > 1) {
                    logger.log(TreeLogger.Type.ERROR, "Unresolved Multiple inheritance @ExtendsDBO declaration  on interface " + interfaceType.getName());
                    throw new UnableToCompleteException();
                } else {
                    expandedFromClassName = interfaceType.getImplementedInterfaces()[0].getQualifiedSourceName();
                }
            }
        } else {
            expandedFromClassName = interfaceType.getName();
        }

        List<JMethod> allMethods = contextHelper.getAllEntityMethods(interfaceType, true);

        String ownerMemberName = null;

        for (JMethod method : allMethods) {
            ToString ts = method.getAnnotation(ToString.class);
            if (ts != null) {
                toStringMemberNames.add(method.getName());
                sortKeys.put(method.getName(), ts);
            }
            if (method.getAnnotation(Owner.class) != null) {
                if (ownerMemberName != null) {
                    logger.log(TreeLogger.Type.ERROR, "Duplicate @Owner declaration " + method.getName() + " and " + ownerMemberName);
                    throw new UnableToCompleteException();
                }
                ownerMemberName = method.getName();
            }
            if (method.getAnnotation(BusinessEqualValue.class) != null) {
                businessEqualMemberNames.add(method.getName());
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

        StringBuilder businessEqualMemberNamesStringArray = new StringBuilder();
        for (String memberName : businessEqualMemberNames) {
            if (businessEqualMemberNamesStringArray.length() > 0) {
                businessEqualMemberNamesStringArray.append(", ");
            }
            businessEqualMemberNamesStringArray.append(escapeSourceString(memberName));
        }

        writer.println();
        writer.indent();
        writer.println("public " + simpleName + "() { ");
        writer.indent();
        writer.print("super(");

        writer.print(interfaceType.getName());
        writer.print(".class, ");

        writer.print(expandedFromClassName);
        writer.print(".class, ");

        writer.print(escapeSourceString(caption));
        writer.print(", ");

        writer.print(escapeSourceString(description));
        writer.print(", ");

        writer.print(escapeSourceString(watermark));
        writer.print(", ");

        writer.print(persistenceTransient.toString());
        writer.print(", ");

        writer.print(rpcTransient.toString());
        writer.print(", ");

        ToStringFormat toStringFormatAnnotation = contextHelper.getInheritedAnnotation(interfaceType, ToStringFormat.class);
        if (toStringFormatAnnotation != null) {
            writer.print(escapeSourceString(toStringFormatAnnotation.value()));
            writer.print(", ");
            writer.print(escapeSourceString(toStringFormatAnnotation.nil()));
        } else {
            writer.print("null, \"\"");
        }
        writer.print(", ");

        writer.print(escapeSourceString(ownerMemberName));
        writer.print(", ");

        writer.print("new String[] {");
        writer.print(toStringMemberNamesStringArray.toString());
        writer.print("}");
        writer.print(", ");

        writer.print("new String[] {");
        writer.print(businessEqualMemberNamesStringArray.toString());
        writer.print("}");

        writer.println(");");

        for (Annotation annotation : interfaceType.getAnnotations()) {
            Class<? extends Annotation> annotationClass = getAnnotationClass(annotation);
            if (annotationClass.isAnnotationPresent(GwtAnnotation.class)) {
                writer.print("super.addAnnotation(");
                writer.print(annotationClass.getName());
                writer.print(".class");
                writer.println(");");
            }
        }

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

        int validationErrors = writeEntityMemberMetaImpl(logger, contextHelper, writer, allMethods, interfaceType);

        writer.outdent();
        return validationErrors;
    }

    static Map<String, MemberMetaData> defaultMembers = new HashMap<String, MemberMetaData>();

    static {
        defaultMembers.put("defaultStringMember", MemberMetaData.defaultStringMember);
        defaultMembers.put("defaultBooleanMember", MemberMetaData.defaultBooleanMember);
        defaultMembers.put("defaultDoubleMember", MemberMetaData.defaultDoubleMember);
        defaultMembers.put("defaultIntegerMember", MemberMetaData.defaultIntegerMember);
        defaultMembers.put("defaultDateMember", MemberMetaData.defaultDateMember);
        defaultMembers.put("defaultSqlDateMember", MemberMetaData.defaultSqlDateMember);
        defaultMembers.put("defaultLogicalDateMember", MemberMetaData.defaultLogicalDateMember);
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
    static int writeEntityMemberMetaImpl(TreeLogger logger, ContextHelper contextHelper, SourceWriter writer, List<JMethod> allMethods,
            JClassType interfaceType) throws UnableToCompleteException {
        writer.println();
        writer.println("@Override");
        writer.println("protected MemberMeta createMemberMeta(String memberName) {");
        writer.indent();

        int validationErrors = 0;

        boolean embeddedType = (interfaceType.getAnnotation(EmbeddedEntity.class) != null);

        for (JMethod method : allMethods) {
            if (!contextHelper.isEntityMember(method)) {
                continue;
            }
            JClassType type = (JClassType) method.getReturnType();

            MemberMetaDataGeneration data = new MemberMetaDataGeneration();
            data.objectClassSourceName = type.getErasedType().getQualifiedSourceName();

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
                    throw new RuntimeException(
                            "ISet " + method.getName() + " type should be ParameterizedType in interface '" + interfaceType.getQualifiedSourceName() + "'");
                }
                valueClass = ((JParameterizedType) type).getTypeArgs()[0];
                data.valueClassSourceName = valueClass.getErasedType().getQualifiedSourceName();
                data.objectClassType = ObjectClassType.EntitySet;
            } else if (type.isAssignableTo(contextHelper.iListInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException(
                            "IList " + method.getName() + " type should be ParameterizedType in interface '" + interfaceType.getQualifiedSourceName() + "'");
                }
                valueClass = ((JParameterizedType) type).getTypeArgs()[0];

                data.valueClassSourceName = valueClass.getErasedType().getQualifiedSourceName();
                data.objectClassType = ObjectClassType.EntityList;
            } else if (type.isAssignableTo(contextHelper.iEnentityInterfaceType)) {
                valueClass = type;

                data.valueClassSourceName = valueClass.getErasedType().getQualifiedSourceName();
                data.objectClassType = ObjectClassType.Entity;
            } else {
                logger.log(TreeLogger.Type.ERROR, "Unknown member type '" + type.getQualifiedSourceName() + "' of method '" + method.getName()
                        + "' in interface '" + interfaceType.getQualifiedSourceName() + "'");
                logger.log(TreeLogger.Type.ERROR, "Only IEntity, IPrimitive<>, IPrimitiveSet<>, ISet<>, IList<> are expected.");
                throw new UnableToCompleteException();
            }

            String memberCaption = I18nAnnotation.DEFAULT_VALUE;
            String memberDescription = null;
            String memberWatermark = null;
            Caption memberCaptionAnnotation = method.getAnnotation(Caption.class);
            if (memberCaptionAnnotation != null) {
                memberCaption = memberCaptionAnnotation.name();
                memberDescription = memberCaptionAnnotation.description();
                memberWatermark = memberCaptionAnnotation.watermark();
            }
            if (I18nAnnotation.DEFAULT_VALUE.equals(memberCaption)) {
                memberCaption = EnglishGrammar.capitalize(method.getName());
            }
            data.embedded = (valueClass.getAnnotation(EmbeddedEntity.class) != null) || (method.getAnnotation(EmbeddedEntity.class) != null);
            data.persistenceTransient = (method.getAnnotation(Transient.class) != null);
            if ((!embeddedType) && (!data.embedded) && (!data.persistenceTransient) && (contextHelper.validateReservedKeywordsMembers)) {
                if (!ReservedWords.validate(logger, interfaceType, method, data.objectClassType)) {
                    validationErrors++;
                }
            }
            data.rpcTransient = (method.getAnnotation(RpcTransient.class) != null);
            data.logTransient = (method.getAnnotation(LogTransient.class) != null);

            Detached detachedAnnotation = method.getAnnotation(Detached.class);
            if (detachedAnnotation == null) {
                data.attachLevel = AttachLevel.Attached;
            } else if (detachedAnnotation.level() == null) {
                data.attachLevel = AttachLevel.getDefault(data.objectClassType);
            } else {
                data.attachLevel = detachedAnnotation.level();
            }

            Owned owned = method.getAnnotation(Owned.class);

            data.ownedRelationships = (owned != null) || (data.embedded) || (data.objectClassType == ObjectClassType.PrimitiveSet);

            data.owner = (method.getAnnotation(Owner.class) != null);
            assert (!(data.owner == true && data.ownedRelationships == true));

            if (owned != null) {
                for (CascadeType ct : owned.cascade()) {
                    switch (ct) {
                    case ALL:
                        data.cascadePersist = true;
                        data.cascadeDelete = true;
                        break;
                    case PERSIST:
                        data.cascadePersist = true;
                        break;
                    case DELETE:
                        data.cascadeDelete = true;
                        break;
                    }
                }
                if (owned.where().length != 0) {
                    List<OwnedConstraint> ownedConstraints = new ArrayList<>();
                    for (JoinWhere joinWhere : owned.where()) {
                        String memberName = findJoinColumnMemberName(contextHelper, valueClass, joinWhere.column());
                        if (memberName == null) {
                            throw new AssertionError("Unmapped @JoinColumn member in Entity '" + valueClass.getName() + "' '" + joinWhere.column() + "'");
                        }
                        ownedConstraints.add(new OwnedConstraint(memberName, joinWhere.value()));
                    }
                    data.ownedConstraints = Collections.unmodifiableList(ownedConstraints);
                }
            } else {
                JoinTable joinTable = method.getAnnotation(JoinTable.class);
                if (joinTable != null) {
                    for (CascadeType ct : joinTable.cascade()) {
                        switch (ct) {
                        case ALL:
                            data.cascadePersist = true;
                            data.cascadeDelete = true;
                            break;
                        case PERSIST:
                            data.cascadePersist = true;
                            break;
                        case DELETE:
                            data.cascadeDelete = true;
                            break;
                        }
                    }
                } else {
                    data.cascadePersist = false;
                    data.cascadeDelete = false;
                }
            }

            Indexed indexedAnnotation = method.getAnnotation(Indexed.class);
            boolean indexed = (indexedAnnotation != null) && (indexedAnnotation.indexPrimaryValue());

            Length stringLengthAnnotation = method.getAnnotation(Length.class);
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
            data.isToStringMember = (method.getAnnotation(ToString.class) != null);

            String useDefaultData = selectDefaultData(data);

            /// Write implementation
            //writer.println("if (\"" + method.getName() + "\".equals(memberName)) {");
            writer.println("if (memberName.equals(\"" + method.getName() + "\")) {");
            writer.indent();

            writer.print(ClientMemberMetaImpl.class.getSimpleName());
            writer.print(" mm = new ");

            writer.print(ClientMemberMetaImpl.class.getSimpleName());
            writer.print("(");

            // String fieldName, String caption, String description,
            writer.print(escapeSourceString(method.getName()));
            writer.print(", ");
            writer.print(escapeSourceString(memberCaption));
            writer.print(", ");
            writer.print(escapeSourceString(memberDescription));
            writer.print(", ");
            writer.print(escapeSourceString(memberWatermark));
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

            if (method.isAnnotationPresent(Editor.class)) {
                writer.print("mm.setEditorType(");
                writer.print(Editor.class.getName() + "." + Editor.EditorType.class.getSimpleName() + ".");
                writer.print(method.getAnnotation(Editor.class).type().name());
                writer.println(");");

                String descriptor = method.getAnnotation(Editor.class).descriptor();
                if (CommonsStringUtils.isStringSet(descriptor)) {
                    writer.print("mm.setEditorDescriptor(");
                    writer.print(escapeSourceString(descriptor));
                    writer.println(");");
                }
            }

            for (Annotation annotation : method.getAnnotations()) {
                Class<? extends Annotation> annotationClass = getAnnotationClass(annotation);
                if (annotationClass.isAnnotationPresent(GwtAnnotation.class)) {
                    writer.print("mm.addAnnotation(");
                    writer.print(annotationClass.getName());
                    writer.print(".class");
                    writer.println(");");
                }
            }

            writer.println("return mm;");

            writer.outdent();
            writer.println("}");
        }
        writer.println("return null;");
        writer.outdent();
        writer.println("}");

        return validationErrors;
    }

    private static Class<? extends Annotation> getAnnotationClass(Annotation annotation) {
        InvocationHandler h = Proxy.getInvocationHandler(annotation);
        // This is com.google.gwt.dev.javac.AnnotationProxyFactory.AnnotationProxyInvocationHandler
        try {
            Field annotationClassFiled = h.getClass().getDeclaredField("annotationClass");
            if (!annotationClassFiled.isAccessible()) {
                annotationClassFiled.setAccessible(true);
            }
            @SuppressWarnings("unchecked")
            Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) annotationClassFiled.get(h);
            return annotationClass;
        } catch (Exception e) {
            throw new Error("GWT implementation changed", e);
        }
    }

    private static String findJoinColumnMemberName(ContextHelper contextHelper, JClassType valueClass, Class<? extends ColumnId> columnId) {
        List<JMethod> allMethods = contextHelper.getAllEntityMethods(valueClass, true);
        for (JMethod method : allMethods) {
            JoinColumn joinColumn = method.getAnnotation(JoinColumn.class);
            if (joinColumn != null && joinColumn.value() == columnId) {
                return method.getName();
            }
        }
        return null;
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
        writer.print(Boolean.valueOf(data.logTransient).toString() + ", ");
        writer.print(AttachLevel.class.getSimpleName() + "." + data.attachLevel.name() + ", ");
        writer.print(Boolean.valueOf(data.ownedRelationships).toString() + ", ");
        writer.print(Boolean.valueOf(data.cascadePersist).toString() + ", ");
        writer.print(Boolean.valueOf(data.owner).toString() + ", ");
        writer.print(Boolean.valueOf(data.embedded).toString() + ", ");
        writer.print(Boolean.valueOf(indexed).toString() + ", ");
        writer.print(data.stringLength + ", ");
        writer.print(escapeSourceString(data.format) + ", ");
        writer.print(Boolean.valueOf(data.useMessageFormat).toString() + ", ");
        writer.print(escapeSourceString(data.nullString) + ", ");
        writer.print(Boolean.valueOf(data.isToStringMember).toString() + ", ");
        if (data.ownedConstraints.isEmpty()) {
            writer.print("null");
        } else {
            writer.println();
            writer.print("java.util.Arrays.asList(");
            boolean first = true;
            for (OwnedConstraint oc : data.ownedConstraints) {
                if (first) {
                    first = false;
                } else {
                    writer.print(",");
                }
                writer.print("new " + OwnedConstraint.class.getName() + "(" + escapeSourceString(oc.getMemberName()) + ", "
                        + escapeSourceString(oc.getMemberValue()) + ")");
            }
            writer.print(")");
        }

    }
}

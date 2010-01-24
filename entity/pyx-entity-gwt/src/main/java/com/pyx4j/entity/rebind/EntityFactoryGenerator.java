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
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rebind;

import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.StringLength;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.Password;
import com.pyx4j.entity.client.AbstractClientEntityFactoryImpl;
import com.pyx4j.entity.client.impl.ClientEntityMetaImpl;
import com.pyx4j.entity.client.impl.ClientMemberMetaImpl;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityFactoryGenerator extends Generator {

    private static String IMPL = IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;

    private static String META_IMPL = "_Meta" + IMPL;

    private JClassType iPrimitiveInterfaceType;

    private JClassType iSetInterfaceType;

    private JClassType iListInterfaceType;

    private JClassType iEnentityInterfaceType;

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        TypeOracle oracle = context.getTypeOracle();
        try {
            JClassType interfaceType = oracle.getType(typeName);
            String packageName = interfaceType.getPackage().getName();
            String simpleName = interfaceType.getSimpleSourceName() + IMPL;
            ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

            composer.setSuperclass(AbstractClientEntityFactoryImpl.class.getName());
            composer.addImport(IEntity.class.getName());

            PrintWriter printWriter = context.tryCreate(logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
            if (printWriter == null) {
                // the generated type already exists
                return interfaceType.getParameterizedQualifiedSourceName() + IMPL;
            }

            //TODO, this does not work!
            if (oracle.findType(RemoteService.class.getName() + "_TypeSerializer") != null) {
                logger.log(TreeLogger.Type.WARN, "RemoteService serializer already created! IEntity generated implementations would not be serializable. "
                        + "Call ClientEntityFactory.ensureIEntityImplementations(); in your code first");
            }

            iEnentityInterfaceType = oracle.getType(IEntity.class.getName());
            iPrimitiveInterfaceType = oracle.getType(IPrimitive.class.getName());
            iSetInterfaceType = oracle.getType(ISet.class.getName());
            iListInterfaceType = oracle.getType(IList.class.getName());

            List<JClassType> cases = new Vector<JClassType>();

            for (JClassType type : oracle.getTypes()) {
                if (type.isAssignableTo(iEnentityInterfaceType) && (type.isInterface() != null) && iEnentityInterfaceType != type) {
                    cases.add(type);
                    logger.log(TreeLogger.Type.DEBUG, "Creating IEntity:" + type.getName());
                    createEntityHandlerImpl(logger, context, type);
                    createEntityMetaImpl(logger, context, type);
                }
            }

            if (cases.size() == 0) {
                logger.log(TreeLogger.Type.WARN, "No IEntity implementations found");
            } else {
                logger.log(TreeLogger.Type.INFO, "Adding " + cases.size() + " IEntity generated implementations");
            }
            composer.addImport(EntityMeta.class.getName());
            SourceWriter writer = composer.createSourceWriter(context, printWriter);
            writeEntityFactoryImplImpl(writer, simpleName, cases);
            writer.commit(logger);
            return composer.getCreatedClassName();
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeEntityFactoryImplImpl(SourceWriter writer, String simpleName, List<JClassType> interfaceClasses) {
        writer.println();
        writer.indent();
        writer.println("public " + simpleName + "() { ");
        writer.indent();
        writer.println("super();");

        for (JClassType interfaceType : interfaceClasses) {
            writer.println();

            writer.print("addClassFactory(");
            writer.print(interfaceType.getQualifiedSourceName());
            writer.print(".class, ");
            writer.println("new IEntityFactoryImpl() {");

            writer.indent();
            writer.println("@SuppressWarnings(\"unchecked\")");
            writer.println("@Override");
            writer.println("public <T extends IEntity<?>> T create(Class<T> clazz){");

            writer.indent();
            writer.print("return (T)new ");
            writer.print(interfaceType.getQualifiedSourceName());
            writer.println(IMPL + "();");
            writer.outdent();

            writer.println("}");

            writer.println();
            writer.println("@Override");
            writer.println("public EntityMeta createEntityMeta(Class<? extends IEntity<?>> clazz){");

            writer.indent();
            writer.print("return new ");
            writer.print(interfaceType.getQualifiedSourceName());
            writer.println(META_IMPL + "();");
            writer.outdent();

            writer.println("}");

            writer.outdent();

            writer.println("});");
        }

        writer.outdent();
        writer.println("}");
        writer.outdent();
    }

    private boolean isEntityMemeber(JMethod method) {
        return (method.getReturnType() != JPrimitiveType.VOID) && (method.getParameters().length == 0);
    }

    private void createEntityMetaImpl(TreeLogger logger, GeneratorContext context, JClassType interfaceType) {
        String packageName = interfaceType.getPackage().getName();
        String simpleName = interfaceType.getSimpleSourceName() + META_IMPL;
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

        composer.addImport(IObject.class.getName());
        composer.addImport(MemberMeta.class.getName());
        composer.addImport(ClientMemberMetaImpl.class.getName());
        composer.setSuperclass(ClientEntityMetaImpl.class.getName());

        PrintWriter printWriter = context.tryCreate(logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
        if (printWriter == null) {
            // the generated type already exists
            return;
        }
        SourceWriter writer = composer.createSourceWriter(context, printWriter);
        writeEntityMetaImpl(writer, simpleName, interfaceType);
        writer.commit(logger);
    }

    private void writeEntityMetaImpl(SourceWriter writer, String simpleName, JClassType interfaceType) {

        String caption;
        String description = null;
        Caption captionAnnotation = interfaceType.getAnnotation(Caption.class);
        if ((captionAnnotation != null) && (CommonsStringUtils.isStringSet(captionAnnotation.name()))) {
            caption = captionAnnotation.name();
        } else {
            caption = EnglishGrammar.capitalize(interfaceType.getSimpleSourceName());
        }
        if (captionAnnotation != null) {
            description = captionAnnotation.description();
        }
        Boolean persistenceTransient = (interfaceType.getAnnotation(Transient.class) != null);
        StringBuilder membersNamesStringArray = new StringBuilder();
        for (JMethod method : interfaceType.getMethods()) {
            if (isEntityMemeber(method)) {
                if (membersNamesStringArray.length() > 0) {
                    membersNamesStringArray.append(", ");
                }
                membersNamesStringArray.append("\"").append(method.getName()).append("\"");
            }
        }

        writer.println();
        writer.indent();
        writer.println("public " + simpleName + "() { ");
        writer.indent();
        writer.print("super(");

        writer.print("\"");
        writer.print(caption);
        writer.print("\", ");

        if (description == null) {
            writer.print("null, ");
        } else {
            writer.print("\"");
            writer.print(description);
            writer.print("\", ");
        }

        writer.print(persistenceTransient.toString());
        writer.print(", ");

        writer.print("new String[] {");
        writer.print(membersNamesStringArray.toString());
        writer.print("}");

        writer.println(");");
        writer.outdent();
        writer.println("}");

        //----------

        writer.println();
        writer.println("@Override");
        writer.println("protected MemberMeta createMemberMeta(String memberName) {");
        writer.indent();
        for (JMethod method : interfaceType.getMethods()) {
            if (!isEntityMemeber(method)) {
                continue;
            }
            JClassType type = (JClassType) method.getReturnType();
            writer.println("if (\"" + method.getName() + "\".equals(memberName)) {");
            writer.indent();

            writer.print(ClientMemberMetaImpl.class.getSimpleName());
            writer.print(" mm = new ");
            writer.print(ClientMemberMetaImpl.class.getSimpleName());
            writer.print("(");

            // Class<?> valueClass, Class<? extends IObject<?, ?>> objectClass,
            if (type.isAssignableTo(iPrimitiveInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("IPrimitive " + method.getName() + " type should be ParameterizedType");
                }
                writer.println(((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName() + ".class, ");
                writer.print("(Class<? extends IObject<?, ?>>)");
                writer.println(type.getQualifiedSourceName() + ".class, ");
            } else if (type.isAssignableTo(iSetInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("ISet " + method.getName() + " type should be ParameterizedType");
                }
                writer.println(((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName() + ".class, ");
                writer.print("(Class<? extends IObject<?, ?>>)");
                writer.println(type.getQualifiedSourceName() + ".class, ");
            } else if (type.isAssignableTo(iListInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("IList " + method.getName() + " type should be ParameterizedType");
                }
                writer.println(((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName() + ".class, ");
                writer.print("(Class<? extends IObject<?, ?>>)");
                writer.println(type.getQualifiedSourceName() + ".class, ");
            } else if (type.isAssignableTo(iEnentityInterfaceType)) {
                writer.println(type.getQualifiedSourceName() + ".class, ");
                writer.println(type.getQualifiedSourceName() + ".class, ");
            } else {
                throw new RuntimeException("Unknown member type" + method.getReturnType());
            }

            // String fieldName, String caption, String description,
            writer.print("\"");
            writer.print(method.getName());
            writer.print("\", ");

            String memeberCaption;
            String memeberDescription = null;
            Caption memeberCaptionAnnotation = method.getAnnotation(Caption.class);
            if ((memeberCaptionAnnotation != null) && (CommonsStringUtils.isStringSet(memeberCaptionAnnotation.name()))) {
                memeberCaption = memeberCaptionAnnotation.name();
            } else {
                memeberCaption = EnglishGrammar.capitalize(method.getName());
            }
            if (memeberCaptionAnnotation != null) {
                memeberDescription = memeberCaptionAnnotation.description();
            }
            writer.print("\"");
            writer.print(memeberCaption);
            writer.print("\", ");

            if (memeberDescription == null) {
                writer.println("null, ");
            } else {
                writer.print("\"");
                writer.print(memeberDescription);
                writer.println("\", ");
            }

            // boolean persistenceTransient, boolean detached, boolean ownedRelationships, int stringLength
            writer.print(Boolean.valueOf((method.getAnnotation(Transient.class) != null)).toString());
            writer.print(", ");
            writer.print(Boolean.valueOf((method.getAnnotation(Detached.class) != null)).toString());
            writer.print(", ");
            writer.print(Boolean.valueOf((method.getAnnotation(Owned.class) != null)).toString());
            writer.print(", ");

            StringLength stringLengthAnnotation = method.getAnnotation(StringLength.class);
            if (stringLengthAnnotation != null) {
                writer.print(String.valueOf(stringLengthAnnotation.value()));
            } else {
                writer.print("0");
            }

            writer.println(");");

            if (method.isAnnotationPresent(Password.class)) {
                writer.print("mm.addValidatorAnnotation(");
                writer.print(Password.class.getName());
                writer.print(".class");
                writer.println(");");
            }

            writer.println("return mm;");
            writer.outdent();
            writer.println("}");
        }
        writer.println("throw new RuntimeException(\"Unknown member \" + memberName);");
        writer.outdent();
        writer.println("}");
    }

    private void createEntityHandlerImpl(TreeLogger logger, GeneratorContext context, JClassType interfaceType) {
        String packageName = interfaceType.getPackage().getName();
        String simpleName = interfaceType.getSimpleSourceName() + IMPL;
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

        composer.addImport(interfaceType.getQualifiedSourceName());
        composer.addImport(IObject.class.getName());
        composer.addImport(IEntity.class.getName());
        composer.setSuperclass(SharedEntityHandler.class.getName() + "<" + interfaceType.getName() + ">");
        composer.addImplementedInterface(interfaceType.getName());
        composer.addAnnotationDeclaration("@SuppressWarnings(\"serial\")");

        PrintWriter printWriter = context.tryCreate(logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
        if (printWriter == null) {
            // the generated type already exists
            return;
        }

        SourceWriter writer = composer.createSourceWriter(context, printWriter);
        writeEntityHandlerImpl(writer, simpleName, interfaceType);
        writer.commit(logger);
    }

    private void writeEntityHandlerImpl(SourceWriter writer, String simpleName, JClassType interfaceType) {
        writer.println();
        writer.indent();
        writer.println("public " + simpleName + "() { ");
        writer.indent();
        writer.print("super(");
        writer.print(interfaceType.getName());
        writer.println(".class);");
        writer.outdent();
        writer.println("}");

        writer.println();
        writer.println("public " + simpleName + "(IEntity<?> parent, String fieldName) { ");
        writer.indent();
        writer.print("super(");
        writer.print(interfaceType.getName());
        writer.println(".class, parent, fieldName);");
        writer.outdent();
        writer.println("}");

        // Create all members

        writer.println();
        writer.println("@Override");
        writer.println("protected IObject<?, ?> lazyCreateMember(String name) {");
        writer.indent();
        for (JMethod method : interfaceType.getMethods()) {
            if (!isEntityMemeber(method)) {
                continue;
            }
            JClassType type = (JClassType) method.getReturnType();
            writer.println("if (\"" + method.getName() + "\".equals(name)) {");
            writer.indent();
            if (type.isAssignableTo(iPrimitiveInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("IPrimitive " + method.getName() + " type should be ParameterizedType");
                }
                String valueClass = ((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName();
                writer.println("return lazyCreateMemberIPrimitive(\"" + method.getName() + "\", " + valueClass + ".class);");
            } else if (type.isAssignableTo(iSetInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("ISet " + method.getName() + " type should be ParameterizedType");
                }
                String valueClass = ((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName();
                writer.println("return lazyCreateMemberISet(\"" + method.getName() + "\", " + valueClass + ".class);");
            } else if (type.isAssignableTo(iListInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("IList " + method.getName() + " type should be ParameterizedType");
                }
                String valueClass = ((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName();
                writer.println("return lazyCreateMemberIList(\"" + method.getName() + "\", " + valueClass + ".class);");
            } else if (type.isAssignableTo(iEnentityInterfaceType)) {
                writer.println("return new " + type.getQualifiedSourceName() + IMPL + "(this, \"" + method.getName() + "\");");
            } else {
                throw new RuntimeException("Unknown member type" + method.getReturnType());
            }
            writer.outdent();
            writer.println("}");
        }
        writer.println("throw new RuntimeException(\"Unknown member \" + name);");

        writer.outdent();
        writer.println("}");

        // Members access
        for (JMethod method : interfaceType.getMethods()) {
            if (!isEntityMemeber(method)) {
                continue;
            }
            writer.println();
            writer.println("@Override");
            writer.println("@SuppressWarnings(\"unchecked\")");
            writer.print("public ");
            writer.print(method.getReturnType().getParameterizedQualifiedSourceName());
            writer.println(" " + method.getName() + "() {");
            writer.indent();

            writer.println("return (" + method.getReturnType().getParameterizedQualifiedSourceName() + ") getMember(\"" + method.getName() + "\");");

            writer.outdent();
            writer.println("}");
        }

    }
}

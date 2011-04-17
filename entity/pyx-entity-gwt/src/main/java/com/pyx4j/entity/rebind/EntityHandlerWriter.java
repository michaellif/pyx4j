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
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.entity.client.impl.EntityImplNativeHelper;
import com.pyx4j.entity.client.impl.EntityMemberMapCreator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.impl.PrimitiveHandler;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class EntityHandlerWriter {

    /**
     * When enabled -6% of code for test domain
     */
    final static boolean optimizeForJS = true;

    /**
     * When disabled adds 4% of code to generated domain.
     */
    final static boolean cacheEntityMeta = false;

    static void createEntityHandlerImpl(TreeLogger logger, ContextHelper contextHelper, JClassType interfaceType) throws UnableToCompleteException {
        TreeLogger implLogger = logger.branch(TreeLogger.DEBUG, "Creating implementation for " + interfaceType.getName());

        String packageName = interfaceType.getPackage().getName();
        String simpleName = interfaceType.getSimpleSourceName() + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

        composer.addImport(interfaceType.getQualifiedSourceName());
        composer.addImport(IObject.class.getName());
        composer.addImport(IEntity.class.getName());
        composer.addImport(EntityMeta.class.getName());
        composer.addImport(GWT.class.getName());
        composer.addImport(EntityMemberMapCreator.class.getName());
        composer.setSuperclass(SharedEntityHandler.class.getName());
        composer.addImplementedInterface(interfaceType.getName());
        composer.addAnnotationDeclaration("@SuppressWarnings(\"serial\")");

        PrintWriter printWriter = contextHelper.context.tryCreate(implLogger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
        if (printWriter == null) {
            // the generated type already exists
            return;
        }

        SourceWriter writer = composer.createSourceWriter(contextHelper.context, printWriter);
        writeEntityHandlerImpl(implLogger, contextHelper, writer, simpleName, interfaceType);
        writer.commit(implLogger);
    }

    private static void writeEntityHandlerImpl(TreeLogger logger, ContextHelper contextHelper, SourceWriter writer, String simpleName, JClassType interfaceType)
            throws UnableToCompleteException {
        writer.indent();

        //Static for optimisation
        if (cacheEntityMeta) {
            writer.println();
            writer.println("private static EntityMeta entityMeta;");
        }
        if (optimizeForJS) {
            writer.println();
            writer.println("private static EntityMemberMapCreator createMemberMap;");
        }

        writer.outdent();

        // Constructors
        writer.println();
        writer.indent();
        writer.println("public " + simpleName + "() { ");
        writer.indent();
        writer.print("super(");
        writer.print(interfaceType.getName());
        writer.println(".class, null, null);");
        writer.outdent();
        writer.println("}");

        writer.println();
        writer.println("public " + simpleName + "(IObject<?> parent, String fieldName) { ");
        writer.indent();
        writer.print("super(");
        writer.print(interfaceType.getName());
        writer.println(".class, parent, fieldName);");
        writer.outdent();
        writer.println("}");

        // Create all members
        List<JMethod> allMethods = contextHelper.getAllEntityMethods(interfaceType);
        if (optimizeForJS) {
            writer.println();

            writer.println("private static native EntityMemberMapCreator loadCreateMemberNative() /*-{");
            writer.indent();
            writer.println("var map = {}");
            nextJSMethod: for (JMethod method : allMethods) {
                JClassType type = (JClassType) method.getReturnType();
                writer.print("map[\"" + method.getName() + "\"] = ");

                if (type.isAssignableTo(contextHelper.iPrimitiveInterfaceType)) {
                    String valueClass = ((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName();
                    if (valueClass.startsWith("java.lang.") || valueClass.equals("java.util.Date") || valueClass.equals("java.sql.Date")
                            || valueClass.equals("byte[]")) {

                        if (valueClass.equals("byte[]")) {
                            valueClass = "byteArray";
                        }

                        writer.print("@");
                        writer.print(EntityImplNativeHelper.class.getName());
                        writer.print("::createMemberIPrimitive_");
                        writer.print(valueClass.replace('.', '_'));
                        writer.print("(L");
                        writer.print(SharedEntityHandler.class.getName().replace('.', '/'));
                        writer.println(";Ljava/lang/String;);");

                        continue nextJSMethod;
                    }
                }

                writer.println("function(handler, memberName) {");
                writer.print("   return handler.@");
                writer.print(SharedEntityHandler.class.getName());

                String valueClass;

                if (type.isAssignableTo(contextHelper.iPrimitiveInterfaceType)) {
                    if (!(type instanceof JParameterizedType)) {
                        logger.log(TreeLogger.Type.ERROR,
                                "IPrimitive " + method.getName() + " type should be ParameterizedType in interface '" + interfaceType.getQualifiedSourceName()
                                        + "'");
                        throw new UnableToCompleteException();
                    }
                    writer.println("::lazyCreateMemberIPrimitive(Ljava/lang/String;Ljava/lang/Class;)(memberName");
                    valueClass = ((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName();
                } else if (type.isAssignableTo(contextHelper.iPrimitiveSetInterfaceType)) {
                    writer.println("::lazyCreateMemberIPrimitiveSet(Ljava/lang/String;Ljava/lang/Class;)(memberName");
                    valueClass = ((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName();
                } else if (type.isAssignableTo(contextHelper.iSetInterfaceType)) {
                    writer.println("::lazyCreateMemberISet(Ljava/lang/String;Ljava/lang/Class;)(memberName");
                    valueClass = ((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName();
                } else if (type.isAssignableTo(contextHelper.iListInterfaceType)) {
                    writer.println("::lazyCreateMemberIList(Ljava/lang/String;Ljava/lang/Class;)(memberName");
                    valueClass = ((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName();
                } else if (type.isAssignableTo(contextHelper.iEnentityInterfaceType)) {
                    writer.println("::lazyCreateMemberIEntity(Ljava/lang/String;Ljava/lang/Class;)(memberName");
                    valueClass = type.getQualifiedSourceName();
                } else {
                    logger.log(TreeLogger.Type.ERROR, "Unknown member type '" + type.getQualifiedSourceName() + "' of method '" + method.getName()
                            + "' in interface '" + interfaceType.getQualifiedSourceName() + "'");
                    logger.log(TreeLogger.Type.ERROR, "Only IEntity, IPrimitive<>, IPrimitiveSet<>, ISet<>, IList<> are expected.");
                    throw new UnableToCompleteException();
                }

                writer.print("     ,");
                if (valueClass.equals("byte[]")) {
                    writer.print("@" + PrimitiveHandler.class.getName() + "::BYTE_ARRAY_CLASS");
                } else {
                    writer.print("@" + valueClass + "::class");
                }
                writer.println("); };");
                writer.println();
            }

            writer.println("return map;");
            writer.outdent();
            writer.println("}-*/;");

        }

        // --

        writer.println();
        writer.println("@Override");
        writer.println("protected IObject<?> lazyCreateMember(String name) {");
        writer.indent();

        if (optimizeForJS) {
            writer.println("if (GWT.isScript()) {");
            writer.indent();

            writer.println("if (createMemberMap == null) {");
            writer.indent();
            writer.println("createMemberMap = loadCreateMemberNative();");
            writer.outdent();
            writer.println("}");

            writer.println("return createMemberMap.createMember(this, name);");
            writer.outdent();
            writer.println("} else {");
            writer.indent();
        }

        for (JMethod method : allMethods) {
            JClassType type = (JClassType) method.getReturnType();
            writer.println("if (\"" + method.getName() + "\".equals(name)) {");
            writer.indent();
            if (type.isAssignableTo(contextHelper.iPrimitiveInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    logger.log(TreeLogger.Type.ERROR,
                            "IPrimitive " + method.getName() + " type should be ParameterizedType in interface '" + interfaceType.getQualifiedSourceName()
                                    + "'");
                    throw new UnableToCompleteException();
                }
                String valueClass = ((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName();
                writer.println("return lazyCreateMemberIPrimitive(\"" + method.getName() + "\", " + valueClass + ".class);");
            } else if (type.isAssignableTo(contextHelper.iPrimitiveSetInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("IPrimitiveSet " + method.getName() + " type should be ParameterizedType in interface '"
                            + interfaceType.getQualifiedSourceName() + "'");
                }
                String valueClass = ((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName();
                writer.println("return lazyCreateMemberIPrimitiveSet(\"" + method.getName() + "\", " + valueClass + ".class);");
            } else if (type.isAssignableTo(contextHelper.iSetInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("ISet " + method.getName() + " type should be ParameterizedType in interface '"
                            + interfaceType.getQualifiedSourceName() + "'");
                }
                String valueClass = ((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName();
                writer.println("return lazyCreateMemberISet(\"" + method.getName() + "\", " + valueClass + ".class);");
            } else if (type.isAssignableTo(contextHelper.iListInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("IList " + method.getName() + " type should be ParameterizedType in interface '"
                            + interfaceType.getQualifiedSourceName() + "'");
                }
                String valueClass = ((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName();
                writer.println("return lazyCreateMemberIList(\"" + method.getName() + "\", " + valueClass + ".class);");
            } else if (type.isAssignableTo(contextHelper.iEnentityInterfaceType)) {
                writer.println("return lazyCreateMemberIEntity(\"" + method.getName() + "\", " + type.getQualifiedSourceName() + ".class);");
            } else {
                logger.log(TreeLogger.Type.ERROR, "Unknown member type '" + type.getQualifiedSourceName() + "' of method '" + method.getName()
                        + "' in interface '" + interfaceType.getQualifiedSourceName() + "'");
                logger.log(TreeLogger.Type.ERROR, "Only IEntity, IPrimitive<>, IPrimitiveSet<>, ISet<>, IList<> are expected.");
                throw new UnableToCompleteException();
            }
            writer.outdent();
            writer.println("}");
        }
        writer.println("return null;");

        if (optimizeForJS) {
            writer.outdent();
            writer.println("}"); // if GWT isScript()
        }

        writer.outdent();
        writer.println("}");

        StringBuilder membersNamesStringArray = new StringBuilder();
        // Members access
        for (JMethod method : allMethods) {
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

            if (membersNamesStringArray.length() > 0) {
                membersNamesStringArray.append(", ");
            }
            membersNamesStringArray.append("\"").append(method.getName()).append("\"");
        }

        writer.println();
        writer.println("@Override");
        writer.println("public String[] getMembers() {");
        writer.indent();
        writer.print("return new String[] {");
        writer.print(membersNamesStringArray.toString());
        writer.println("};");
        writer.outdent();
        writer.println("}");

        // for optimisation
        if (cacheEntityMeta) {
            writer.println();
            writer.println("@Override");
            writer.println("public EntityMeta getEntityMeta() {");
            writer.indent();
            writer.println("if (entityMeta == null) { entityMeta = super.getEntityMeta(); }");
            writer.println("return entityMeta;");
            writer.outdent();
            writer.println("}");
        }

    }
}

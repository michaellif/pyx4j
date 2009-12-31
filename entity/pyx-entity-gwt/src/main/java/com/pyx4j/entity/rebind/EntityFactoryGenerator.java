/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
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
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.pyx4j.entity.client.AbstractClientEntityFactoryImpl;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;

public class EntityFactoryGenerator extends Generator {

    private static String IMPL = "_Impl";

    private JClassType iPrimitiveInterfaceType;

    private JClassType iSetInterfaceType;

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

            iEnentityInterfaceType = oracle.getType(IEntity.class.getName());
            iPrimitiveInterfaceType = oracle.getType(IPrimitive.class.getName());
            iSetInterfaceType = oracle.getType(ISet.class.getName());

            List<JClassType> cases = new Vector<JClassType>();

            for (JClassType type : oracle.getTypes()) {
                if (type.isAssignableTo(iEnentityInterfaceType) && (type.isInterface() != null) && iEnentityInterfaceType != type) {
                    cases.add(type);
                    logger.log(TreeLogger.Type.DEBUG, "Creating IEntity:" + type.getName());
                    createEntityHandlerImpl(logger, context, type);
                }
            }

            if (cases.size() == 0) {
                logger.log(TreeLogger.Type.WARN, "No IEntity implementations found");
            }
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
            writer.outdent();

            writer.println("});");
        }

        writer.outdent();
        writer.println("}");
        writer.outdent();
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

        writer.println();
        writer.println("@Override");
        writer.println("protected void lazyCreateMembersNamesList() {");
        writer.indent();
        for (JMethod method : interfaceType.getMethods()) {
            if ((method.getReturnType() == JPrimitiveType.VOID) || (method.getParameters().length != 0)) {
                continue;
            }
            writer.println("createMemeber(\"" + method.getName() + "\");");
        }
        writer.outdent();
        writer.println("}");

        // Create all members

        writer.println();
        writer.println("@Override");
        writer.println("protected IObject<?, ?> lazyCreateMember(String name) {");
        writer.indent();
        for (JMethod method : interfaceType.getMethods()) {
            if ((method.getReturnType() == JPrimitiveType.VOID) || (method.getParameters().length != 0)) {
                continue;
            }
            JClassType type = (JClassType) method.getReturnType();
            writer.println("if (\"" + method.getName() + "\".equals(name)) {");
            writer.indent();
            if (type.isAssignableTo(iPrimitiveInterfaceType)) {
                if (!(type instanceof JParameterizedType)) {
                    throw new RuntimeException("IPrimitive " + method.getName() + " type should be ParameterizedType");
                }
                writer.println("return lazyCreateMemberIPrimitive(\"" + method.getName() + "\", "
                        + ((JParameterizedType) type).getTypeArgs()[0].getQualifiedSourceName() + ".class);");
            } else if (type.isAssignableTo(iSetInterfaceType)) {
                writer.println("return lazyCreateMemberISet(\"" + method.getName() + "\");");
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
            if ((method.getReturnType() == JPrimitiveType.VOID) || (method.getParameters().length != 0)) {
                continue;
            }
            writer.println();
            writer.println("@Override");
            writer.println("@SuppressWarnings(\"unchecked\")");
            writer.print("public ");
            writer.print(method.getReturnType().getParameterizedQualifiedSourceName());
            writer.println(" " + method.getName() + "() {");
            writer.indent();

            //TODO
            writer.println("return (" + method.getReturnType().getParameterizedQualifiedSourceName() + ") getMember(\"" + method.getName() + "\");");

            writer.outdent();
            writer.println("}");
        }

    }
}

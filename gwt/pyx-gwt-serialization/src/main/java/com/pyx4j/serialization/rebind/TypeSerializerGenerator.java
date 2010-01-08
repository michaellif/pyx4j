/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 18, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.serialization.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.impl.Serializer;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.serialization.client.RemoteServiceTarget;

/**
 * We do nothing just really on GWT implementation to generate TypeSerializer for remote
 * services. Then we Just access the generated class.
 * 
 */
public class TypeSerializerGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        TypeOracle oracle = context.getTypeOracle();
        try {
            JClassType interfaceType = oracle.getType(typeName);

            RemoteServiceTarget target = interfaceType.getAnnotation(RemoteServiceTarget.class);
            if ((target == null) || (target.value() == null)) {
                throw new RuntimeException("Annotation RemoteServiceTarget required");
            }

            String packageName = interfaceType.getPackage().getName();
            String simpleName = interfaceType.getSimpleSourceName() + "_Impl";

            ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

            composer.addImplementedInterface(interfaceType.getName());
            composer.addImport(Serializer.class.getName());

            PrintWriter printWriter = context.tryCreate(logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
            if (printWriter == null) {
                // the generated type already exists
                return interfaceType.getParameterizedQualifiedSourceName() + "_Impl";
            }

            SourceWriter writer = composer.createSourceWriter(context, printWriter);
            writeImpl(writer, simpleName, target.value());
            writer.commit(logger);
            return composer.getCreatedClassName();
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeImpl(SourceWriter writer, String simpleName, Class<? extends RemoteService> targetServiceClass) {
        writer.println();
        writer.indent();
        writer.println("@Override");
        writer.println("public Serializer getSerializer() { ");
        writer.indent();

        writer.print("return new ");
        writer.print(targetServiceClass.getName());
        writer.print("_TypeSerializer");
        writer.println("();");

        writer.outdent();
        writer.println("}");
        writer.outdent();
    }

}

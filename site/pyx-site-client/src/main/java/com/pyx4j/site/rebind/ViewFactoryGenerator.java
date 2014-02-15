/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jul 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.site.client.IsView;
import com.pyx4j.site.client.ViewFactory;

public class ViewFactoryGenerator extends Generator {
    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        TypeOracle oracle = context.getTypeOracle();

        JClassType instantiableType = oracle.findType(IsView.class.getName());

        List<JClassType> classes = new ArrayList<>();

        for (JClassType classType : oracle.getTypes()) {
            if (classType.isAssignableTo(instantiableType) //@formatter:off
                    && classType.isInterface() == null 
                    && !classType.isAbstract()
                    && classType.getSimpleSourceName().endsWith("Impl") 
                    && hasInterfaceWithTheSameName(oracle, classType, instantiableType)) {//@formatter:on
                classes.add(classType);
            }
        }

        final String genPackageName = ViewFactory.class.getPackage().getName();
        final String genClassName = ViewFactory.class.getSimpleName() + "Impl";

        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(genPackageName, genClassName);
        composer.addImplementedInterface(ViewFactory.class.getCanonicalName());

        //  composer.addImport("com.package.client.*");

        PrintWriter printWriter = context.tryCreate(logger, genPackageName, genClassName);

        if (printWriter != null) {
            SourceWriter sourceWriter = composer.createSourceWriter(context, printWriter);
            sourceWriter.println(genClassName + "( ) {");
            sourceWriter.println("}");

            printFactoryMethod(classes, sourceWriter);

            sourceWriter.commit(logger);
        }

        return composer.getCreatedClassName();
    }

    private boolean hasInterfaceWithTheSameName(TypeOracle oracle, JClassType classType, JClassType instantiableType) {
        JClassType classTypeInterface = oracle.findType(classType.getQualifiedSourceName().replaceFirst("Impl$", ""));
        return (classTypeInterface != null) && (classTypeInterface.isInterface() != null) && classTypeInterface.isAssignableTo(instantiableType);
    }

    private void printFactoryMethod(List<JClassType> clazzes, SourceWriter sourceWriter) {
        sourceWriter.println();

        sourceWriter.println("public <T extends " + IsView.class.getName() + "> T getView( Class<T> clazz ) {");

        for (JClassType classType : clazzes) {
            if (classType.isAbstract())
                continue;

            sourceWriter.println();
            sourceWriter.indent();
            sourceWriter.println("if (\"" + classType.getQualifiedSourceName() + "\".equals(clazz.getName() + \"Impl\")) {");
            sourceWriter.indent();
            sourceWriter.println("return (T) new " + classType.getQualifiedSourceName() + "( );");
            sourceWriter.outdent();
            sourceWriter.println("}");
            sourceWriter.outdent();
            sourceWriter.println();
        }
        sourceWriter.indent();
        sourceWriter.println("return (T) null;");
        sourceWriter.outdent();
        sourceWriter.println();
        sourceWriter.println("}");
        sourceWriter.outdent();
        sourceWriter.println();
    }
}
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
 * Created on Nov 27, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rebind;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.commons.ClassName;
import com.pyx4j.commons.GWTClassNamePreserve;

public class ClassNameGenerator extends Generator {

    /**
     * Configuration property.
     */
    public static final String CLASSNAME_PRESERVE = "pyx.classNamePreserve";

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        boolean classNamePreserve;
        try {
            ConfigurationProperty prop = context.getPropertyOracle().getConfigurationProperty(CLASSNAME_PRESERVE);
            classNamePreserve = Boolean.valueOf(prop.getValues().get(0));
        } catch (BadPropertyValueException e) {
            logger.log(TreeLogger.ERROR, "The configuration property " + CLASSNAME_PRESERVE + " was not defined. Is com.pyx4j.Commons.gwt.xml inherited?");
            throw new UnableToCompleteException();
        }

        TypeOracle oracle = context.getTypeOracle();
        try {
            if (!classNamePreserve) {
                return oracle.getType(ClassName.RelyOnClassMetadata.class.getName().replace('$', '.')).getQualifiedSourceName();
            }

            JClassType interfaceType = oracle.getType(typeName);
            String packageName = interfaceType.getPackage().getName();
            String simpleName = ClassName.class.getSimpleName() + "_Impl";
            ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
            composer.addImplementedInterface(typeName);
            composer.addImport(Map.class.getName());
            composer.addImport(HashMap.class.getName());
            composer.addImport(JsArrayString.class.getName());
            composer.addImport(GWT.class.getName());
            composer.addImport(GWTClassNamePreserve.class.getName());

            PrintWriter printWriter = context.tryCreate(logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
            if (printWriter == null) {
                // the generated type already exists
                return packageName + "." + simpleName;
            }

            JClassType iMarkerInterfaceType = oracle.getType(GWTClassNamePreserve.class.getName());
            List<JClassType> preserveClasses = new Vector<JClassType>();

            for (JClassType type : oracle.getTypes()) {
                if (type.isAssignableTo(iMarkerInterfaceType) && (iMarkerInterfaceType != type)) {
                    preserveClasses.add(type);
                    logger.log(TreeLogger.Type.DEBUG, "Preserve class name:" + type.getName());
                }
            }

            SourceWriter writer = composer.createSourceWriter(context, printWriter);
            writeImpl(writer, preserveClasses);
            writer.commit(logger);
            return composer.getCreatedClassName();
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO test this
    private String optimizedStringStorage(String name) {
        return "\"" + name + "\"";
    }

    private String getClassName(JClassType type) {
        String simpleName = type.getQualifiedBinaryName();
        // strip the package name
        return simpleName.substring(simpleName.lastIndexOf(".") + 1);
    }

    private void writeImpl(SourceWriter writer, List<JClassType> preserveClasses) {
        writer.println();

        writer.println("private static final Map<Class<? extends GWTClassNamePreserve>, String> nameMapJava;");
        writer.println("private static final JsArrayString nameMapNative;");
        writer.println();

        writer.println("static {");
        writer.indent();
        writer.println("if (GWT.isScript()) {");
        writer.indent();
        writer.println("nameMapJava = null;");
        writer.println("nameMapNative = loadNamesNative();");
        writer.outdent();
        writer.println("} else {");
        writer.indent();
        writer.println("nameMapJava = loadNamesJava();");
        writer.println("nameMapNative = null;");
        writer.outdent();
        writer.println("}");
        writer.outdent();
        writer.println("}");

        //---

        writer.println();
        writer.println("@Override");
        writer.println("public String getClassName(final Class<? extends GWTClassNamePreserve> klass) {");
        writer.indent();

        writer.println("if (GWT.isScript()) {");
        writer.indent();
        writer.println("return nameMapNative.get(klass.hashCode());");
        writer.outdent();
        writer.println("} else {");
        writer.indent();
        writer.println("return nameMapJava.get(klass);");
        writer.outdent();
        writer.println("}");

        writer.outdent();
        writer.println("}");

        //---

        writer.println();
        writer.println("private static Map<Class<? extends GWTClassNamePreserve>, String> loadNamesJava() {");
        writer.indent();
        writer.println("Map<Class<? extends GWTClassNamePreserve>, String> result = new HashMap<Class<? extends GWTClassNamePreserve>, String>();");

        for (JClassType klass : preserveClasses) {
            writer.print("result.put(");
            writer.print(klass.getQualifiedSourceName() + ".class, ");
            writer.print("\"" + getClassName(klass) + "\"");
            writer.println(");");
        }

        writer.println("return result;");
        writer.outdent();
        writer.println("}");

        //---

        writer.println();
        writer.println("private static native JsArrayString loadNamesNative() /*-{");
        writer.indent();
        writer.println("var result = [];");
        for (JClassType klass : preserveClasses) {
            writer.print("result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@");
            writer.print(klass.getQualifiedSourceName() + "::class)] = ");
            writer.print(optimizedStringStorage(getClassName(klass)));
            writer.println(";");
        }
        writer.println("return result;");
        writer.outdent();
        writer.println("}-*/;");

    }

}

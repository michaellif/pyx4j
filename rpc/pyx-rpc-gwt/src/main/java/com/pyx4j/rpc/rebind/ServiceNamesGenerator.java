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
package com.pyx4j.rpc.rebind;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import com.pyx4j.rpc.shared.Service;

public class ServiceNamesGenerator extends Generator {

    public static enum GenerationType {

        classMetadata, preserve, obfuscated, trunk
    }

    /**
     * Configuration property.
     */
    public static final String GENERATION_TYPE = "pyx.elideServiceNamesFromRPC";

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        GenerationType generationType = GenerationType.classMetadata;
        try {
            ConfigurationProperty prop = context.getPropertyOracle().getConfigurationProperty(GENERATION_TYPE);
            generationType = GenerationType.valueOf(prop.getValues().get(0));
        } catch (BadPropertyValueException e) {
            logger.log(TreeLogger.ERROR, "The configuration property " + GENERATION_TYPE + " was not defined. Is com.pyx4j.rpc.RPC.gwt.xml inherited?");
            throw new UnableToCompleteException();
        }

        TypeOracle oracle = context.getTypeOracle();
        try {
            JClassType interfaceType = oracle.getType(typeName);
            String packageName = interfaceType.getPackage().getName();
            String simpleName = interfaceType.getSimpleSourceName() + "_Impl";
            ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
            composer.addImplementedInterface(typeName);
            composer.addImport(Map.class.getName());
            composer.addImport(HashMap.class.getName());
            composer.addImport(JsArrayString.class.getName());
            composer.addImport(GWT.class.getName());
            composer.addImport(Service.class.getName());

            PrintWriter printWriter = context.tryCreate(logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
            if (printWriter == null) {
                // the generated type already exists
                return packageName + "." + simpleName;
            }

            JClassType iServiceInterfaceType = oracle.getType(Service.class.getName());
            List<JClassType> serviceClasses = new Vector<JClassType>();

            for (JClassType type : oracle.getTypes()) {
                if ((type.isInterface() != null) && type.isAssignableTo(iServiceInterfaceType) && (iServiceInterfaceType != type)) {
                    serviceClasses.add(type);
                    logger.log(TreeLogger.Type.DEBUG, "Service class:" + type.getName());
                }
            }

            SourceWriter writer = composer.createSourceWriter(context, printWriter);
            Map<JClassType, String> namesMap = createNamesMap(generationType, serviceClasses);
            writeImpl(writer, generationType, serviceClasses, namesMap);
            writer.commit(logger);
            return composer.getCreatedClassName();
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<JClassType, String> createNamesMap(GenerationType generationType, List<JClassType> serviceClasses) {
        Map<JClassType, String> result = new HashMap<JClassType, String>();

        Set<String> uniqueNames = new HashSet<String>();

        int count = 0;
        for (JClassType serviceClass : serviceClasses) {
            String name;
            switch (generationType) {
            case classMetadata:
            case preserve:
                name = serviceClass.getQualifiedBinaryName();
                break;
            case obfuscated:
                name = Integer.toString(++count, Character.MAX_RADIX);
                break;
            case trunk:
                name = serviceClass.getName();
                if (uniqueNames.contains(name)) {
                    name = serviceClass.getQualifiedSourceName();
                }
                uniqueNames.add(name);
                break;
            default:
                throw new RuntimeException();
            }

            result.put(serviceClass, name);
        }

        return result;
    }

    //TODO test this
    private String optimizedStringStorage(String name) {
        return "\"" + name + "\"";
    }

    private void writeImpl(SourceWriter writer, GenerationType generationType, List<JClassType> serviceClasses, Map<JClassType, String> namesMap) {
        writer.println();

        writer.beginJavaDocComment();
        writer.print("generationType = " + generationType.name());
        writer.endJavaDocComment();

        boolean useClassMetadata = (generationType == GenerationType.classMetadata);

        if (!useClassMetadata) {
            writer.println("private static final Map<Class<? extends Service>, String> nameMapJava;");
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
        }

        //---

        writer.println();
        writer.println("@Override");
        writer.println("public String getServiceName(@SuppressWarnings(\"rawtypes\") final Class<? extends Service> serviceInterface) {");
        writer.indent();

        if (useClassMetadata) {
            writer.println("return serviceInterface.getName();");
        } else {
            writer.println("if (GWT.isScript()) {");
            writer.indent();
            writer.println("return nameMapNative.get(serviceInterface.hashCode());");
            writer.outdent();
            writer.println("} else {");
            writer.indent();
            writer.println("return nameMapJava.get(serviceInterface);");
            writer.outdent();
            writer.println("}");
        }

        writer.outdent();
        writer.println("}");

        if (!useClassMetadata) {
            //---

            writer.println();
            writer.println("private static Map<Class<? extends Service>, String> loadNamesJava() {");
            writer.indent();
            writer.println("Map<Class<? extends Service>, String> result = new HashMap<Class<? extends Service>, String>();");

            for (JClassType serviceClass : serviceClasses) {
                writer.print("result.put(");
                writer.print(serviceClass.getQualifiedSourceName() + ".class, ");
                writer.print("\"" + namesMap.get(serviceClass) + "\"");
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
            for (JClassType serviceClass : serviceClasses) {
                writer.print("result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@");
                writer.print(serviceClass.getQualifiedSourceName() + "::class)] = ");
                writer.print(optimizedStringStorage(namesMap.get(serviceClass)));
                writer.println(";");
            }
            writer.println("return result;");
            writer.outdent();
            writer.println("}-*/;");
        }
    }
}

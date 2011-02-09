/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Feb 4, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.site.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.site.client.NavigationItem;
import com.pyx4j.site.client.place.AppPlace;
import com.pyx4j.site.client.place.AppPlaceHistoryMapper;
import com.pyx4j.site.client.place.AppPlaceInfo;

public class AppPlaceListingGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
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
            composer.addImport(AppPlaceHistoryMapper.class.getName());
            composer.addImport(AppPlace.class.getName());
            composer.addImport(NavigationItem.class.getName());
            composer.addImport(AppPlaceInfo.class.getName());

            PrintWriter printWriter = context.tryCreate(logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
            if (printWriter == null) {
                // the generated type already exists
                return packageName + "." + simpleName;
            }

            JClassType placeType = oracle.getType(AppPlace.class.getName());
            List<JClassType> serviceClasses = new Vector<JClassType>();

            for (JClassType type : oracle.getTypes()) {
                if ((type.isClass() != null) && type.isAssignableTo(placeType) && (placeType != type)) {
                    serviceClasses.add(type);
                    logger.log(TreeLogger.Type.DEBUG, "Place class: " + type.getName());
                }
            }

            SourceWriter writer = composer.createSourceWriter(context, printWriter);
            writeImpl(writer, serviceClasses);
            writer.commit(logger);
            return composer.getCreatedClassName();
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeImpl(SourceWriter writer, List<JClassType> serviceClasses) {
        writer.println();

        //getPlace()
        writer.println("@Override");
        writer.println("public AppPlace getPlace(String token) {");
        writer.indent();

        for (JClassType jClassType : serviceClasses) {
            String type = jClassType.getPackage().getName() + "." + jClassType.getName();
            writer.println("if (token.equals(AppPlaceHistoryMapper.getPlaceId(" + type + ".class))) {");
            writer.indent();
            writer.println("return new " + type + "();");
            writer.outdent();
            writer.println("}");
        }

        writer.println("return null;");
        writer.outdent();
        writer.println("}");

        writer.println();

        //getPlaceInfo()
        writer.println("@Override");
        writer.println("public AppPlaceInfo getPlaceInfo(AppPlace place) {");
        writer.indent();

        for (JClassType jClassType : serviceClasses) {
            String type = jClassType.getPackage().getName() + "." + jClassType.getName();
            writer.println("if (place.getClass() == " + type + ".class) {");
            writer.indent();
            writer.println("return new AppPlaceInfo(\"" + jClassType.getAnnotation(NavigationItem.class).navigLabel() + "\", \""
                    + jClassType.getAnnotation(NavigationItem.class).caption() + "\", \"" + jClassType.getAnnotation(NavigationItem.class).type() + "\", \""
                    + jClassType.getAnnotation(NavigationItem.class).resource() + "\");");
            writer.outdent();
            writer.println("}");
        }

        writer.println("return null;");
        writer.outdent();
        writer.println("}");

        writer.println();

        //getPlacesByType()
        Map<String, List<String>> classByType = new HashMap<String, List<String>>();
        for (JClassType jClassType : serviceClasses) {
            String type = jClassType.getAnnotation(NavigationItem.class).type();
            List<String> typeClasses = classByType.get(type);
            if (typeClasses == null) {
                typeClasses = new ArrayList<String>();
                classByType.put(type, typeClasses);
            }
            typeClasses.add(jClassType.getPackage().getName() + "." + jClassType.getName());
        }

        writer.println("@Override");
        writer.println("public AppPlace[] getPlacesByType(String type) {");
        writer.indent();

        for (String type : classByType.keySet()) {
            writer.println("if (type.equals(\"" + type + "\")) {");
            writer.indent();
            writer.println("return new AppPlace[] { ");
            writer.indent();
            for (String clazz : classByType.get(type)) {
                writer.println("new " + clazz + "(),");
            }
            writer.outdent();
            writer.println("};");
            writer.outdent();
            writer.println("}");
        }

        writer.println("return null;");
        writer.outdent();
        writer.println("}");
    }
}

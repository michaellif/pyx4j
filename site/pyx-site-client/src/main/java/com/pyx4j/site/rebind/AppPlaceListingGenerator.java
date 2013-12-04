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
 * @version $Id$
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

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.i18n.annotations.I18nAnnotation;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.place.AppPlaceFactory;
import com.pyx4j.site.client.place.AppPlaceHistoryMapper;
import com.pyx4j.site.client.place.AppPlaceListingImplBase;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.SiteMap;

public class AppPlaceListingGenerator extends Generator {

    private JClassType placeType;

    private JClassType siteMapType;

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        TypeOracle oracle = context.getTypeOracle();
        try {
            JClassType interfaceType = oracle.getType(typeName);
            String packageName = interfaceType.getPackage().getName();
            String simpleName = interfaceType.getSimpleSourceName() + "_Impl";
            ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
            composer.addImplementedInterface(typeName);
            composer.setSuperclass(AppPlaceListingImplBase.class.getName());
            composer.addImport(Map.class.getName());
            composer.addImport(List.class.getName());
            composer.addImport(ArrayList.class.getName());
            composer.addImport(HashMap.class.getName());
            composer.addImport(JsArrayString.class.getName());
            composer.addImport(GWT.class.getName());
            composer.addImport(AppPlaceHistoryMapper.class.getName());
            composer.addImport(AppPlace.class.getName());
            composer.addImport(AppPlaceInfo.class.getName());
            composer.addImport(PlaceProperties.class.getName());
            composer.addImport(SiteMap.class.getName());
            composer.addImport(I18n.class.getName());

            PrintWriter printWriter = context.tryCreate(logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
            if (printWriter == null) {
                // the generated type already exists
                return packageName + "." + simpleName;
            }

            siteMapType = oracle.getType(SiteMap.class.getName());
            placeType = oracle.getType(AppPlace.class.getName());
            List<JClassType> placeClasses = new Vector<JClassType>();

            for (JClassType type : oracle.getTypes()) {
                if ((type.isClass() != null) && type.isAssignableTo(placeType) && (placeType != type)) {
                    placeClasses.add(type);
                    logger.log(TreeLogger.Type.DEBUG, "Place class: " + type.getName());
                }
            }

            SourceWriter writer = composer.createSourceWriter(context, printWriter);
            writeImpl(writer, simpleName, placeClasses);
            writer.commit(logger);
            return composer.getCreatedClassName();
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
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

    JClassType getSiteMapClass(JClassType placeClassType) {
        JClassType type = placeClassType.getEnclosingType();
        while (type != null) {
            if (type.isAssignableTo(siteMapType)) {
                return type;
            }
            type = type.getEnclosingType();
        }
        return null;
    }

    private void writeImpl(SourceWriter writer, String simpleName, List<JClassType> placeClasses) {
        writer.println();

        writer.indent();
        writer.println("public " + simpleName + "() { ");
        writer.indent();
        writer.println("super();");

        for (JClassType jClassType : placeClasses) {
            JClassType siteMap = getSiteMapClass(jClassType);
            if (!jClassType.isAbstract() && (siteMap != null)) {
                writer.print("map(");

                writer.print(siteMap.getQualifiedSourceName());
                writer.print(".class, ");

                writer.print(jClassType.getQualifiedSourceName());
                writer.print(".class, ");

                writer.print("new " + AppPlaceFactory.class.getName() + "(){");
                writer.print(" @Override public AppPlace create() { return new ");
                writer.print(jClassType.getQualifiedSourceName());
                writer.print("(); } }");

                writer.println(");");

            }
        }

        writer.println("");

        for (JClassType jClassType : placeClasses) {
            if (!jClassType.isAbstract()) {

                writer.print("map(");
                writer.print(jClassType.getQualifiedSourceName());
                writer.print(".class, ");

                String caption = null;
                String navigLabel = null;
                String staticContent = null;

                PlaceProperties placeProperties = jClassType.getAnnotation(PlaceProperties.class);
                if (placeProperties != null) {
                    caption = placeProperties.caption();
                    navigLabel = placeProperties.navigLabel();
                    staticContent = placeProperties.staticContent();
                }

                // set names to class name if empty/default:
                if (caption == null || I18nAnnotation.DEFAULT_VALUE.equals(caption)) {
                    caption = EnglishGrammar.capitalize(EnglishGrammar.classNameToEnglish(jClassType.getSimpleSourceName()));
                }
                if (navigLabel == null || I18nAnnotation.DEFAULT_VALUE.equals(navigLabel)) {
                    navigLabel = caption;
                }
                writer.print("new ");
                writer.print(AppPlaceInfo.class.getSimpleName());
                writer.print("(" + i18nEscapeSourceString(navigLabel) + ", " + i18nEscapeSourceString(caption) + ", " + i18nEscapeSourceString(staticContent)
                        + ")");

                writer.println(");");
            }
        }

        writer.outdent();
        writer.println("}");
        writer.outdent();

        writer.println();
    }
}

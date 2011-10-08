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

import org.xnap.commons.i18n.I18n;

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
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.site.client.place.AppPlaceHistoryMapper;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.site.rpc.annotations.NavigationItem;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.SiteMap;

public class AppPlaceListingGenerator extends Generator {

    private JClassType placeType;

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
            composer.addImport(List.class.getName());
            composer.addImport(ArrayList.class.getName());
            composer.addImport(HashMap.class.getName());
            composer.addImport(JsArrayString.class.getName());
            composer.addImport(GWT.class.getName());
            composer.addImport(AppPlaceHistoryMapper.class.getName());
            composer.addImport(AppPlace.class.getName());
            composer.addImport(AppPlaceInfo.class.getName());
            composer.addImport(NavigationItem.class.getName());
            composer.addImport(PlaceProperties.class.getName());
            composer.addImport(SiteMap.class.getName());
            composer.addImport(I18n.class.getName());
            composer.addImport(I18nFactory.class.getName());

            PrintWriter printWriter = context.tryCreate(logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
            if (printWriter == null) {
                // the generated type already exists
                return packageName + "." + simpleName;
            }

            placeType = oracle.getType(AppPlace.class.getName());
            List<JClassType> placeClasses = new Vector<JClassType>();

            for (JClassType type : oracle.getTypes()) {
                if ((type.isClass() != null) && type.isAssignableTo(placeType) && (placeType != type)) {
                    placeClasses.add(type);
                    logger.log(TreeLogger.Type.DEBUG, "Place class: " + type.getName());
                }
            }

            SourceWriter writer = composer.createSourceWriter(context, printWriter);
            writeImpl(writer, placeClasses);
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

    private void writeImpl(SourceWriter writer, List<JClassType> placeClasses) {
        writer.println();

        writer.println("private static I18n i18n = I18nFactory.getI18n();");

        //getPlace()
        writer.println("@Override");
        writer.println("public AppPlace getPlace(Class<? extends SiteMap> siteMapClass, String token) {");
        writer.indent();

        for (JClassType jClassType : placeClasses) {
            if (!jClassType.isAbstract()) {
                String type = jClassType.getQualifiedSourceName();
                writer.println("if (\"" + type + "\".contains(siteMapClass.getName()) && ");
                writer.println("    token.equals(AppPlaceInfo.getPlaceId(" + type + ".class))) {");
                writer.indent();
                writer.println("return new " + type + "();");
                writer.outdent();
                writer.println("}");
            }
        }

        writer.println("return null;");
        writer.outdent();
        writer.println("}");

        writer.println();

        //getPlaceInfo()
        writer.println("@Override");
        writer.println("public AppPlaceInfo getPlaceInfo(AppPlace place) {");
        writer.indent();

        for (JClassType jClassType : placeClasses) {
            if (!jClassType.isAbstract()) {
                writer.println("if (place.getClass() == " + jClassType.getQualifiedSourceName() + ".class) {");
                writer.indent();

                PlaceProperties placeProperties = jClassType.getAnnotation(PlaceProperties.class);
                String caption = I18nAnnotation.DEFAULT_VALUE;
                String staticContent = null;
                if (placeProperties != null) {
                    caption = placeProperties.caption();
                    staticContent = placeProperties.staticContent();
                }
                if (I18nAnnotation.DEFAULT_VALUE.equals(caption)) {
                    caption = EnglishGrammar.capitalize(jClassType.getSimpleSourceName());
                }

                NavigationItem navigationItem = jClassType.getAnnotation(NavigationItem.class);
                String navigLabel = null;
                if (navigationItem != null) {
                    navigLabel = navigationItem.navigLabel();
                }

                writer.print("return new ");
                writer.print(AppPlaceInfo.class.getSimpleName());
                writer.println("(" + i18nEscapeSourceString(navigLabel) + ", " + i18nEscapeSourceString(caption) + ", " + i18nEscapeSourceString(staticContent)
                        + ");");
                writer.outdent();
                writer.println("}");
            }
        }

        writer.println("return null;");
        writer.outdent();
        writer.println("}");

        writer.println();

        //getPlaceInfo()
        writer.println("@Override");
        writer.println("public List<AppPlace> getTopNavigation() {");
        writer.indent();
        writer.println("List<AppPlace> places = new ArrayList<AppPlace>();");

        for (JClassType jClassType : placeClasses) {
            String type = jClassType.getQualifiedSourceName();
            NavigationItem navigationItem = jClassType.getAnnotation(NavigationItem.class);

            if (!jClassType.isAbstract() && !jClassType.getEnclosingType().isAssignableFrom(placeType) && navigationItem != null) {
                writer.println("places.add(new " + type + "());");
            }
        }

        writer.println("return places;");
        writer.outdent();
        writer.println("}");

        writer.println();

    }
}

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
 * Created on 2011-05-09
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JEnumConstant;
import com.google.gwt.core.ext.typeinfo.JEnumType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.i18n.client.I18nEnumResourceBundleImpl;
import com.pyx4j.i18n.shared.Translatable;
import com.pyx4j.i18n.shared.Translation;

public class I18nEnumResourceBundleGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        JClassType interfaceType;
        try {
            interfaceType = context.getTypeOracle().getType(typeName);
        } catch (NotFoundException e) {
            logger.log(TreeLogger.ERROR, "Could not gnerate EnumResourceBundle", e);
            throw new UnableToCompleteException();
        }
        String packageName = interfaceType.getPackage().getName();
        String simpleName = interfaceType.getSimpleSourceName() + "_Generated";
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

        composer.setSuperclass(I18nEnumResourceBundleImpl.class.getName());

        PrintWriter printWriter = context.tryCreate(logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
        if (printWriter == null) {
            // the generated type already exists
            return interfaceType.getParameterizedQualifiedSourceName() + "_Generated";
        }

        SourceWriter writer = composer.createSourceWriter(context, printWriter);

        writer.println();
        writer.indent();
        writer.println("public " + simpleName + "() { ");
        writer.indent();

        writer.println("super();");

        for (JClassType type : context.getTypeOracle().getTypes()) {
            if (type instanceof JEnumType) {

                boolean translationPresent = (type.getAnnotation(Translatable.class) != null);

                if (!translationPresent) {
                    // Find if @Translation present on any declaration
                    for (JEnumConstant field : ((JEnumType) type).getEnumConstants()) {
                        if (field.getAnnotation(Translation.class) != null) {
                            translationPresent = true;
                            break;
                        }
                    }
                }

                if (translationPresent) {
                    if (!type.isPublic()) {
                        logger.log(TreeLogger.ERROR, "enum " + type.getQualifiedSourceName() + " should be declared public to be used in internationalization");
                    }
                    for (JEnumConstant field : ((JEnumType) type).getEnumConstants()) {

                        writer.print("add(");

                        writer.print(type.getQualifiedSourceName());
                        writer.print(".");
                        writer.print(field.getName());

                        writer.print(", i18n.tr(");

                        String name;
                        Translation tr = field.getAnnotation(Translation.class);
                        if (tr != null) {
                            name = tr.value();
                        } else {
                            name = field.getName();
                        }

                        writer.print(escapeSourceString(name));

                        writer.println("));");
                    }
                }
            }
        }

        writer.outdent();
        writer.println("}");
        writer.outdent();

        writer.commit(logger);
        return composer.getCreatedClassName();
    }

    private String escapeSourceString(String value) {
        if (value == null) {
            return "null";
        } else {
            return "\"" + value.replace("\"", "\\\"").replace("\n", "\\n") + "\"";
        }
    }
}

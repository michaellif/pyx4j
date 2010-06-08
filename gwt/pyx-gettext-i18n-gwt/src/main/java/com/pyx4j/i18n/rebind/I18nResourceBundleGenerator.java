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
 * Created on Jun 7, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.rebind;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Locale;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.SelectionProperty;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.i18n.client.I18nResourceBundleImpl;

public class I18nResourceBundleGenerator extends Generator {

    /**
     * The locale property.
     */
    private static final String PROP_LOCALE = "locale";

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        String locale;
        try {
            SelectionProperty localeProp = context.getPropertyOracle().getSelectionProperty(logger, PROP_LOCALE);
            locale = localeProp.getCurrentValue();
        } catch (BadPropertyValueException e) {
            logger.log(TreeLogger.ERROR, "Could not parse specified locale", e);
            throw new UnableToCompleteException();
        }

        if ((locale.equals("en")) || (locale.equals("default"))) {
            return null;
        }

        try {
            JClassType interfaceType = context.getTypeOracle().getType(typeName);
            String packageName = interfaceType.getPackage().getName();
            String simpleName = interfaceType.getSimpleSourceName() + "_" + locale;
            ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

            composer.setSuperclass(I18nResourceBundleImpl.class.getName());

            PrintWriter printWriter = context.tryCreate(logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
            if (printWriter == null) {
                // the generated type already exists
                return interfaceType.getParameterizedQualifiedSourceName() + "_" + locale;
            }

            logger.log(TreeLogger.Type.INFO, "Adding gettext for locale '" + locale + "'");

            SourceWriter writer = composer.createSourceWriter(context, printWriter);
            writeImpl(writer, simpleName, locale);
            writer.commit(logger);
            return composer.getCreatedClassName();
        } catch (NotFoundException e) {
            logger.log(TreeLogger.ERROR, "Could gnerate locale resources", e);
            throw new UnableToCompleteException();
        }
    }

    private void writeImpl(SourceWriter writer, String simpleName, String locale) {
        writer.println();
        writer.indent();
        writer.println("public " + simpleName + "() { ");
        writer.indent();

        writer.println("super(new String[]{");

        I18n i18n = I18nFactory.getI18n(getClass(), "Messages", new Locale(locale));

        Enumeration<String> en = i18n.getResources().getKeys();
        while (en.hasMoreElements()) {
            String key = en.nextElement();
            if (key.length() > 0) {
                String value = i18n.tr(key);
                if (value.length() > 0) {
                    writer.print(escapeSourceString(key));
                    writer.print(", ");
                    writer.print(escapeSourceString(value));
                    writer.println(",");
                }
            }
        }

        writer.println("});");
        writer.outdent();
        writer.println("}");
        writer.outdent();

    }

    private String escapeSourceString(String value) {
        if (value == null) {
            return "null";
        } else {
            return "\"" + value.replace("\"", "\\\"").replace("\n", "\\n") + "\"";
        }
    }
}

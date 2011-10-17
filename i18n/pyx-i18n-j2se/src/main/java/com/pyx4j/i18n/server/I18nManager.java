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
 * Created on Oct 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.i18n.gettext.POCatalog;
import com.pyx4j.i18n.gettext.Translator;

public class I18nManager {

    private static final Logger log = LoggerFactory.getLogger(I18nManager.class);

    private static final ThreadLocal<Locale> threadLocale = new ThreadLocal<Locale>() {
        @Override
        protected Locale initialValue() {
            return Locale.getDefault();
        }
    };

    private static final Map<Locale, Translator> catalogs = new HashMap<Locale, Translator>();

    public static void setThreadLocale(Locale locale) {
        threadLocale.set(locale);
    }

    public static Locale getThreadLocale() {
        return threadLocale.get();
    }

    static Translator getTranslator(Locale locale) {
        Translator t = catalogs.get(locale);
        if (t == null) {
            synchronized (I18nManager.class) {
                try {
                    t = new POCatalog(locale);
                } catch (IOException e) {
                    //log.error("translation for locale {} not available", locale, e);
                    t = new EmptyTranslator();
                }
            }
            catalogs.put(locale, t);
        }
        return t;
    }

    static Translator getThreadLocaleTranslator() {
        return getTranslator(getThreadLocale());
    }

    public static void removeThreadLocale() {
        threadLocale.remove();
    }

}

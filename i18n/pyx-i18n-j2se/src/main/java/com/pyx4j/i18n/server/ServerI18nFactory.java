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
 * Created on Oct 9, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.server;

import java.util.Locale;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.i18n.shared.II18nFactory;

public class ServerI18nFactory implements II18nFactory {

    private static I18n i18n;

    @Override
    public I18n get(Class<?> clazz) {
        if (i18n == null) {
            synchronized (I18n.class) {
                i18n = new ServerI18nImpl();
            }
        }
        return i18n;
    }

    public static I18n get(Class<?> clazz, Locale locale) {
        return new ServerI18nLocaleImpl(I18nManager.getTranslator(locale));
    }
}

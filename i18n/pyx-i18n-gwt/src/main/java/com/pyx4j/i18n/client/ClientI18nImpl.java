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
package com.pyx4j.i18n.client;

import com.pyx4j.i18n.shared.I18n;

/**
 * Only single Locale is supported at GWT runtime.
 * 
 * N.B. Plural forms of gettext not supported, use java.text.MessageFormat choice format.
 */
class ClientI18nImpl extends I18n {

    private final I18nResourceBundle bundle;

    public ClientI18nImpl(I18nResourceBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public final String translate(String text) {
        String value = bundle.getString(text);
        if (value == null) {
            return text;
        } else {
            return value;
        }
    }

}

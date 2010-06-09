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

import java.text.MessageFormat;

/**
 * Port of gettext-commons to GWT.
 * 
 * N.B. Plural forms of gettext not supported, use java.text.MessageFormat choice format.
 */
public class I18nImpl {

    private final I18nResourceBundle bundle;

    public I18nImpl(I18nResourceBundle bundle) {
        this.bundle = bundle;
    }

    public String tr(String text) {
        String value = bundle.getString(text);
        if (value == null) {
            return text;
        } else {
            return value;
        }
    }

    public String tr(String text, Object... objects) {
        return MessageFormat.format(tr(text), objects);
    }

    //    public String trn(String text, String pluralText, long n) {
    //        //TODO
    //        return null;
    //    }
    //
    //    public final String trn(String text, String pluralText, long n, Object... objects) {
    //        return MessageFormat.format(trn(text, pluralText, n), objects);
    //    }
}

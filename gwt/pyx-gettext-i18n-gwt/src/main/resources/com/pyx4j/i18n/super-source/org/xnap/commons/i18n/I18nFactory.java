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
 * Created on Jun 8, 2010
 * @author vlads
 * @version $Id$
 */
package org.xnap.commons.i18n;

import com.pyx4j.i18n.client.I18nResourceBundle;
import com.google.gwt.core.client.GWT;

/**
 * Port of gettext-commons to GWT.
 * 
 * Only single Locale is supported at runtime.
 */
public class I18nFactory {

    private static I18n i18n;

    public static I18n getI18n(final Class<?> clazz) {
        if (i18n == null) {
            synchronized (I18nFactory.class) {
                i18n = new I18n((I18nResourceBundle) GWT.create(I18nResourceBundle.class));
            }
        }
        return i18n;
    }
}

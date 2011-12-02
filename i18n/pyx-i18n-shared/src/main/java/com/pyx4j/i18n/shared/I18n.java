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
package com.pyx4j.i18n.shared;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;

public abstract class I18n {

    public static final String CONTEXT_GLUE = "\u0004";

    private static II18nFactory factory;

    static {
        if (ApplicationMode.hasGWT()) {
            if (GWT.isClient()) {
                factory = GWT.create(II18nFactory.class);
            } else {
                factory = ServerSideFactory.create(II18nFactory.class);
            }
        } else {
            factory = ServerSideFactory.create(II18nFactory.class);
        }
    }

    public I18n() {

    }

    protected abstract String translate(final String context, final String text);

    /**
     * Returns <code>text</code> translated into the currently selected
     * language. Every user-visible string in the program must be wrapped into
     * this function.
     * 
     * @param text
     *            Constant text to translate
     * @return the translation
     */
    public final String tr(final String text) {
        return translate(null, text);
    }

    /**
     * Returns <code>text</code> translated into the currently selected
     * language.
     * <p>
     * Occurrences of {number} placeholders in text are replaced by <code>objects</code>.
     * <p>
     * Invokes {@link SimpleMessageFormat#format(java.lang.String, java.lang.Object[])}.
     * 
     * @param text
     *            text to translate
     * @param objects
     *            arguments to <code>SimpleMessageFormat.format()</code>
     * @return the translated text
     */
    public final String tr(final String text, Object... objects) {
        return SimpleMessageFormat.format(translate(null, text), objects);
    }

    /**
     * Disambiguates translation keys.
     * 
     * @param context
     *            the context of the text to be translated
     * @param text
     *            the ambiguous key message in the source locale
     * @return <code>text</code> if the locale of the underlying resource
     *         bundle equals the source code locale, the translation of <code>comment</code> otherwise.
     */
    public final String trc(String context, String text) {
        return translate(context, text);
    }

    public static final I18n get(final Class<?> clazz) {
        return factory.get(clazz);
    }

}

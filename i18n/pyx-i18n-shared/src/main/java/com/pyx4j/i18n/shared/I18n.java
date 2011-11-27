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

    protected abstract String translate(final String text);

    public final String tr(final String text) {
        return translate(text);
    }

    public final String tr(final String text, Object... objects) {
        return SimpleMessageFormat.format(translate(text), objects);
    }

    public static final I18n get(final Class<?> clazz) {
        return factory.get(clazz);
    }

}

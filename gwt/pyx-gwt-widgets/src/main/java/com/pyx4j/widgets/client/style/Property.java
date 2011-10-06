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
 * Created on Apr 27, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

import com.google.gwt.core.client.GWT;

public abstract class Property {

    private final String name;

    public Property(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString(Theme theme, Palette palette) {
        return injectAlternativeHostForwarding(convertToString(theme, palette));
    }

    protected abstract String convertToString(Theme theme, Palette palette);

    protected String injectAlternativeHostForwarding(String value) {
        int urlIdx = value.indexOf("url(");
        if ((urlIdx != -1) && (value.indexOf("url('data:image/") == -1) && (value.indexOf("url('http://") == -1)) {
            value = value.substring(0, urlIdx) + " url(" + StyleManger.getAlternativeHostname() + GWT.getModuleName() + "/" + value.substring(urlIdx + 4);
        }
        return value;
    }
}

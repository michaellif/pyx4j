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

public class Property {

    public static final String COLOR_REF_PREFIX = "$";

    private final String name;

    private final String value;

    private final ThemePalette color;

    public Property(String name, String value) {
        this(name, value, null);
    }

    public Property(String name, ThemePalette color) {
        this(name, null, color);
    }

    public Property(String name, String value, ThemePalette color) {
        this.name = name;
        this.value = value;
        this.color = color;
    }

    public Property(String editableValue) {
        String[] nameValue = editableValue.split(":");
        if (nameValue.length != 2) {
            throw new Error("Invalid style property " + editableValue);
        }
        name = nameValue[0].trim();
        String v = nameValue[1].trim();
        if (v.startsWith(COLOR_REF_PREFIX)) {
            color = ThemePalette.valueOf(v.substring(1));
            value = null;
        } else {
            value = v;
            color = null;
        }
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public ThemePalette getColor() {
        return color;
    }

    @Override
    public String toString() {
        if (value == null) {
            return name + ": " + COLOR_REF_PREFIX + color.name() + ";";
        } else {
            return name + ": " + value + ";";
        }
    }

    public String toString(Theme theme) {
        if (value == null) {
            if (color == null) {
                throw new RuntimeException("theme property " + name + " should be set with value or color");
            }
            return name + ": " + theme.getThemeColorString(color) + ";";
        }

        String retVal = name + ": " + value + ";";

        int urlIdx = retVal.indexOf("url(");
        if ((urlIdx != -1) && (retVal.indexOf("url('data:image/") == -1) && (retVal.indexOf("url('http://") == -1)) {
            retVal = retVal.substring(0, urlIdx) + " url(" + StyleManger.getAlternativeHostname() + GWT.getModuleName() + "/" + retVal.substring(urlIdx + 4);
        }

        int colorIdx = retVal.indexOf("{}");
        if (colorIdx != -1) {
            retVal = retVal.substring(0, colorIdx) + theme.getThemeColorString(color) + retVal.substring(colorIdx + 2);
        }

        return retVal;
    }
}

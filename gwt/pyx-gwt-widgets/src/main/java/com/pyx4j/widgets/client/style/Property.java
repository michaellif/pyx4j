/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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

    private final ThemeColor color;

    public Property(String name, String value) {
        this(name, value, null);
    }

    public Property(String name, ThemeColor color) {
        this(name, null, color);
    }

    public Property(String name, String value, ThemeColor color) {
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
            color = ThemeColor.valueOf(v.substring(1));
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

    public ThemeColor getColor() {
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
            return name + ": " + theme.getThemeColor(color) + ";";
        }
        int urlIdx = value.indexOf("url(");
        if (urlIdx != -1) {
            return name + ": " + value.substring(0, urlIdx) + " url(" + GWT.getModuleName() + "/" + value.substring(urlIdx + 4) + ";";
        } else {
            return name + ": " + value + ";";
        }
    }
}

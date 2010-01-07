/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Theme {

    private final Map<String, Style> styles;

    private final Map<ThemeColor, String> themeColors;

    public Theme() {
        styles = new HashMap<String, Style>();
        themeColors = new HashMap<ThemeColor, String>();
    }

    protected Theme(Theme other) {
        styles = new HashMap<String, Style>(other.styles);
        themeColors = new HashMap<ThemeColor, String>(other.themeColors);
    }

    public Collection<Style> getAllStyles() {
        return styles.values();
    }

    public void putStyle(Style style) {
        styles.put(style.getSelector(), style);
    }

    public String getThemeColor(ThemeColor color) {
        return themeColors.get(color);
    }

    public void putThemeColor(ThemeColor color, String value) {
        themeColors.put(color, value);
    }

    public Theme duplicate() {
        return new Theme(this);
    }

}

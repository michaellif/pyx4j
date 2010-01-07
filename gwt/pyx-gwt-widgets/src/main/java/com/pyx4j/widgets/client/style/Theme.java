/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Theme {

    private final List<Style> styles;

    private final Map<ThemeColor, String> themeColors;

    public Theme() {
        styles = new ArrayList<Style>();
        themeColors = new HashMap<ThemeColor, String>();
    }

    protected Theme(Theme other) {
        styles = new ArrayList<Style>(other.styles);
        themeColors = new HashMap<ThemeColor, String>(other.themeColors);
    }

    public List<Style> getAllStyles() {
        return styles;
    }

    public void addStyle(Style style) {
        styles.add(style);
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

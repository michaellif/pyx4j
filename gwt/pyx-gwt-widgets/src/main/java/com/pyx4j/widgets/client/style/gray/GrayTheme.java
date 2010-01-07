/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.style.gray;

import com.pyx4j.widgets.client.style.ThemeColor;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

public class GrayTheme extends WindowsTheme {

    public GrayTheme() {
        super();
    }

    @Override
    protected void initThemeColors() {
        putThemeColor(ThemeColor.OBJECT_TONE1, "#404040");
        putThemeColor(ThemeColor.OBJECT_TONE2, "#303030");
        putThemeColor(ThemeColor.OBJECT_TONE3, "#505050");
        putThemeColor(ThemeColor.BORDER, "white");
        putThemeColor(ThemeColor.SELECTION, "orange");
        putThemeColor(ThemeColor.SELECTION_TEXT, "#000000");
        putThemeColor(ThemeColor.TEXT_BACKGROUND, "#000000");
        putThemeColor(ThemeColor.TEXT, "#ffffff");
        putThemeColor(ThemeColor.DISABLED_TEXT_BACKGROUND, "#fafafa");
        putThemeColor(ThemeColor.MANDATORY_TEXT_BACKGROUND, "#fcba84");
        putThemeColor(ThemeColor.READ_ONLY_TEXT_BACKGROUND, "#eeeeee");
        putThemeColor(ThemeColor.SEPARATOR, "#999999");
    }

}

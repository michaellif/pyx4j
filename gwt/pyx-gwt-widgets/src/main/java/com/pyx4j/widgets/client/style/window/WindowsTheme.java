/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.style.window;

import com.pyx4j.widgets.client.style.AbstarctTheme;

public class WindowsTheme extends AbstarctTheme {

    public WindowsTheme() {
        setProperty(ThemeColorProperty.OBJECT_TONE1, "#ece9d8");
        setProperty(ThemeColorProperty.OBJECT_TONE2, "#fdfae9");
        setProperty(ThemeColorProperty.OBJECT_TONE3, "#dbd8c7");
        setProperty(ThemeColorProperty.BORDER, "#666666");
        setProperty(ThemeColorProperty.SELECTION, "#86adc4");
        setProperty(ThemeColorProperty.SELECTION_TEXT, "#ffffff");
        setProperty(ThemeColorProperty.TEXT, "#000000");
        setProperty(ThemeColorProperty.TEXT_BACKGROUND, "#ffffff");
        setProperty(ThemeColorProperty.DISABLED_TEXT_BACKGROUND, "#fafafa");
        setProperty(ThemeColorProperty.MANDATORY_TEXT_BACKGROUND, "#fcba84");
        setProperty(ThemeColorProperty.READ_ONLY_TEXT_BACKGROUND, "#eeeeee");
        setProperty(ThemeColorProperty.SEPARATOR, "#eeeeee");
    }

}

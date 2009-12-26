/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.style.gray;

import com.pyx4j.widgets.client.style.AbstarctTheme;

public class GrayTheme extends AbstarctTheme {

    public GrayTheme() {
        setProperty(ThemeColorProperty.OBJECT_TONE1, "#404040");
        setProperty(ThemeColorProperty.OBJECT_TONE2, "#303030");
        setProperty(ThemeColorProperty.OBJECT_TONE3, "#505050");
        setProperty(ThemeColorProperty.BORDER, "white");
        setProperty(ThemeColorProperty.SELECTION, "orange");
        setProperty(ThemeColorProperty.SELECTION_TEXT, "#000000");
        setProperty(ThemeColorProperty.TEXT_BACKGROUND, "#000000");
        setProperty(ThemeColorProperty.TEXT, "#ffffff");
        setProperty(ThemeColorProperty.DISABLED_TEXT_BACKGROUND, "#fafafa");
        setProperty(ThemeColorProperty.MANDATORY_TEXT_BACKGROUND, "#fcba84");
        setProperty(ThemeColorProperty.READ_ONLY_TEXT_BACKGROUND, "#eeeeee");
        setProperty(ThemeColorProperty.SEPARATOR, "#999999");
    }

}

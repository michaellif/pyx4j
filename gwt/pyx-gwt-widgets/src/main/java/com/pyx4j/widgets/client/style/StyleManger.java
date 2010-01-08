/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

import com.google.gwt.dom.client.StyleInjector;

public class StyleManger {

    private static StyleManger instance;

    private Theme theme;

    private StyleManger() {
    }

    public static StyleManger instance() {
        if (instance == null) {
            instance = new StyleManger();
        }
        return instance;
    }

    public static void installTheme(Theme theme) {
        instance().theme = theme;
        StringBuilder stylesString = new StringBuilder();
        for (Style style : theme.getAllStyles()) {
            stylesString.append(style.toString(theme));
        }
        StyleInjector.inject(stylesString.toString());
    }

    public static Theme getTheme() {
        return instance().theme;
    }
}

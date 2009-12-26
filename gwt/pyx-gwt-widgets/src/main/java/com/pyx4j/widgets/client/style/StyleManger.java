/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

import com.google.gwt.user.client.Cookies;

import com.pyx4j.widgets.client.style.classic.ClassicTheme;
import com.pyx4j.widgets.client.style.gray.GrayTheme;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

public class StyleManger {

    public static String STYLE_COOKIE = "AppStyle";

    private static StyleManger instance;

    private final StyleInjector styleInjector;

    private Theme theme;

    private StyleManger() {
        styleInjector = new StyleInjector();
    }

    public static StyleManger instance() {
        if (instance == null) {
            instance = new StyleManger();
        }
        return instance;
    }

    public static void installTheme(Theme theme) {
        instance().theme = theme;
        instance().theme.compileStyles();
        StringBuilder stylesString = new StringBuilder();
        for (Style style : theme.getStyles()) {
            stylesString.append(style.toString(theme));
        }
        instance().styleInjector.injectStyle(stylesString.toString());
        Cookies.setCookie(STYLE_COOKIE, theme.getClass().getName());
    }

    public static void installDefaultTheme() {
        String styleCookie = Cookies.getCookie(STYLE_COOKIE);
        if (ClassicTheme.class.getName().equals(styleCookie)) {
            StyleManger.installTheme(new ClassicTheme());
        } else if (GrayTheme.class.getName().equals(styleCookie)) {
            StyleManger.installTheme(new GrayTheme());
        } else {
            StyleManger.installTheme(new WindowsTheme());
        }
    }

    public static Theme getTheme() {
        return instance().theme;
    }
}

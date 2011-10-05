package com.pyx4j.site.client.ui;

import com.pyx4j.widgets.client.style.IStyleSuffix;

public interface AppSiteView {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public static enum StyleSuffix implements IStyleSuffix {
        Header, MainNavig, Caption, SecondaryNavig, Message, Content, Center, Main, Left, Right, Footer, Display
    }

}

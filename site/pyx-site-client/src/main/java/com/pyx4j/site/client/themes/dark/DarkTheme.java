/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 7, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.themes.dark;

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

public class DarkTheme extends WindowsTheme {

    @Override
    protected void initStyles() {
        super.initStyles();
        initSitePanelStyles();
        initContentPanelStyles();
        initHeaderStyles();
        initMainPanelStyles();
        initFooterStyles();
        initHeaderCaptionsStyles();
        initLogoStyles();
        initMainNavigStyles();
    }

    private void initSitePanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_SitePanel.name());
        style.addProperty("background", "#21262C url(images/background.jpg) repeat-x");
        addStyle(style);
    }

    private void initContentPanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_ContentPanel.name());
        style.addProperty("width", "924px");
        style.addProperty("padding-top", "0px");
        style.addProperty("padding-bottom", "0px");
        addStyle(style);
    }

    private void initHeaderStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Header.name());
        style.addProperty("background", "url(images/topHdr_ecommerce.jpg) no-repeat");
        style.addProperty("height", "250px");
        addStyle(style);
    }

    protected void initMainPanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_MainPanel.name());
        style.addProperty("background", "white");
        addStyle(style);
    }

    private void initFooterStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Footer.name());
        style.addProperty("background", "url(images/topHdr_ecommerce.jpg) no-repeat 50% 100%");
        style.addProperty("height", "40px");
        addStyle(style);
    }

    private void initHeaderCaptionsStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderCaptions.name());
        style.addProperty("color", "#ff6600");
        style.addProperty("margin-left", "270px");
        style.addProperty("margin-top", "167px");
        style.addProperty("font-size", "28px");
        addStyle(style);
    }

    protected void initLogoStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Logo.name());
        style.addProperty("margin-left", "20px");
        style.addProperty("margin-top", "20px");
        addStyle(style);
    }

    protected void initMainNavigStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_MainNavig.name());
        style.addProperty("margin-left", "20px");
        style.addProperty("margin-top", "100px");
        addStyle(style);
    }

}

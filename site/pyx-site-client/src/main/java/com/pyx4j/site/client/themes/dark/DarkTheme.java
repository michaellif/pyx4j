/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 7, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.themes.dark;

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;
import com.pyx4j.widgets.client.style.gray.GrayTheme;

public class DarkTheme extends GrayTheme {

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
        initPrimaryNavigStyles();

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
        style.addProperty("color", "gray");
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

    protected void initPrimaryNavigStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavig.name());
        style.addProperty("margin-left", "0px");
        style.addProperty("margin-top", "100px");
        style.addProperty("background", "url(images/menubkg2.gif) repeat-x");
        style.addProperty("width", "100%");
        style.addProperty("height", "32px");
        style.addProperty("padding-top", "6px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name());
        style.addProperty("background", "url(images/menusep.gif) no-repeat 100%");
        style.addProperty("padding", "6px 0 6px 0");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTabAnchor.name());
        style.addProperty("color", getThemeColor(ThemeColor.TEXT));
        style.addProperty("margin-left", "20px");
        style.addProperty("margin-right", "20px");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTabAnchor.name() + "-selected");
        style.addProperty("color", "#ff6600");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTabAnchor.name() + "-mouseOver");
        style.addProperty("text-decoration", "underline");
        addStyle(style);
    }

}

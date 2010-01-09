/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 7, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.themes.light;

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

public class LightTheme extends WindowsTheme {

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
        style.addProperty("background", "#F8F8F8");
        addStyle(style);
    }

    private void initContentPanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_ContentPanel.name());
        style.addProperty("width", "968px");
        style.addProperty("padding-top", "20px");
        style.addProperty("padding-bottom", "20px");
        addStyle(style);
    }

    protected void initHeaderStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Header.name());
        style.addProperty("background", "url(images/container-header.gif) no-repeat");
        style.addProperty("height", "200px");
        addStyle(style);
    }

    protected void initMainPanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_MainPanel.name());
        style.addProperty("background", "url(images/container-main.gif) repeat-y");
        addStyle(style);
    }

    private void initFooterStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Footer.name());
        style.addProperty("background", "url(images/container-footer.gif) no-repeat 50% 100%");
        style.addProperty("height", "30px");
        addStyle(style);
    }

    private void initHeaderCaptionsStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderCaptions.name());
        style.addProperty("color", "gray");
        style.addProperty("margin-left", "60px");
        style.addProperty("margin-top", "150px");
        style.addProperty("font-size", "22px");

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
        style.addProperty("margin-left", "20px");
        style.addProperty("margin-top", "100px");
        style.addProperty("background", "transparent");
        style.addProperty("width", "900px");
        style.addProperty("height", "23px");
        style.addProperty("padding-top", "6px");
        style.addProperty("padding-left", "10px");
        style.addProperty("background", "transparent url(images/primaryNav-underline.gif) repeat-x 100% 100%");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name());
        style.addProperty("background", "transparent");
        style.addProperty("padding", "6px 4px 6px 0px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + "-selected");
        style.addProperty("background", "transparent url(images/primaryNav-right.gif) no-repeat 100%");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + " a");
        style.addProperty("color", getThemeColor(ThemeColor.TEXT));
        style.addProperty("padding", "6px 16px 6px 20px");
        style.addProperty("text-decoration", "none");
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + "-selected a");
        style.addProperty("background", "transparent url(images/primaryNav-bg.gif) no-repeat");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + "-mouseOver a");
        style.addProperty("text-decoration", "underline");
        addStyle(style);
    }

}

/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 7, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.themes.dark;

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.client.themes.light.LightTheme;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemePalette;

public class DarkTheme extends LightTheme {

    @Override
    protected void initThemeColors() {
        putThemeColor(ThemePalette.OBJECT_TONE1, 0x404040);
        putThemeColor(ThemePalette.OBJECT_TONE2, 0x303030);
        putThemeColor(ThemePalette.OBJECT_TONE3, 0x505050);
        putThemeColor(ThemePalette.OBJECT_TONE4, 0x505050);
        putThemeColor(ThemePalette.OBJECT_TONE5, 0x505050);
        putThemeColor(ThemePalette.BORDER, 0x666666);
        putThemeColor(ThemePalette.SELECTION, 0xff6600);
        putThemeColor(ThemePalette.SELECTION_TEXT, 0xffffff);
        putThemeColor(ThemePalette.TEXT_BACKGROUND, 0xffffff);
        putThemeColor(ThemePalette.TEXT, 0x000000);
        putThemeColor(ThemePalette.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemePalette.MANDATORY_TEXT_BACKGROUND, 0xfcba84);
        putThemeColor(ThemePalette.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemePalette.SEPARATOR, 0x999999);
    }

    @Override
    protected void initGeneralStyles() {
        super.initGeneralStyles();
        Style style = new Style("body");
        style.addProperty("background", "#21262C");
        addStyle(style);
    }

    @Override
    protected void initSitePanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_SitePanel.name());
        style.addProperty("background", "url(images/background.jpg) repeat-x");
        addStyle(style);
    }

    @Override
    protected void initContentPanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_ContentPanel.name());
        style.addProperty("width", "924px");
        style.addProperty("padding-top", "0px");
        style.addProperty("padding-bottom", "50px");
        addStyle(style);
    }

    @Override
    protected void initHeaderStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Header.name());
        style.addProperty("background", "url(images/topHdr_ecommerce.jpg) no-repeat");
        style.addProperty("height", "250px");
        addStyle(style);
    }

    @Override
    protected void initMainPanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_MainPanel.name());
        style.addProperty("background", "white");
        style.addProperty("color", "gray");
        addStyle(style);
    }

    @Override
    protected void initPagePanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name());
        style.addProperty("padding", "20px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " h1, h2, h3, h4 ,h5");
        style.addProperty("margin", "0 0 .5em 0");
        style.addProperty("font-weight", "bold");
        style.addProperty("color", "#306");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " a");
        style.addProperty("color", "black");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " a:hover");
        style.addProperty("color", "grey");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " p, li");
        style.addProperty("margin-bottom", "0.5em");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " p, li, dl, span");
        style.addProperty("font-size", "0.81em");
        style.addProperty("line-height", "1.5em");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " ul, ol");
        style.addProperty(" margin-left", "1em");
        style.addProperty(" margin-bottom", "1em");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " ul li");
        style.addProperty(" list-style-type", "square");
        addStyle(style);
    }

    @Override
    protected void initFooterStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Footer.name());
        style.addProperty("background", "url(images/topHdr_ecommerce.jpg) no-repeat 50% 100%");
        style.addProperty("height", "35px");
        addStyle(style);
    }

    @Override
    protected void initHeaderCaptionsStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderCaptions.name());
        style.addProperty("color", "#ff6600");
        style.addProperty("margin-left", "270px");
        style.addProperty("margin-top", "167px");
        style.addProperty("font-size", "28px");
        addStyle(style);
    }

    @Override
    protected void initLogoStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Logo.name());
        style.addProperty("margin-left", "20px");
        style.addProperty("margin-top", "20px");
        addStyle(style);
    }

    @Override
    protected void initPrimaryNavigStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavig.name());
        style.addProperty("margin-left", "0px");
        style.addProperty("margin-top", "100px");
        style.addProperty("width", "100%");
        style.addProperty("height", "32px");
        style.addProperty("padding-left", "0px");
        style.addProperty("background", "transparent url(images/menubkg2.gif) repeat-x");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name());
        style.addProperty("background", "transparent url(images/menusep.gif) no-repeat 100%");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + " a");
        style.addProperty("color", getThemeColorString(ThemePalette.SELECTION_TEXT));
        style.addProperty("padding", "0px 20px 0px 20px");
        style.addProperty("text-decoration", "none");
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + " a span");
        style.addProperty("padding-top", "6px");
        style.addProperty("display", "block");
        style.addProperty("font-size", "18px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + "-selected a");
        style.addProperty("color", "#ff6600");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + " a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);
    }

    @Override
    protected void initHeaderLinksStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLinks.name());
        style.addProperty("margin-left", "100px");
        style.addProperty("margin-top", "10px");
        style.addProperty("color", "black");
        style.addProperty("width", "750px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLinks.name() + " ul");
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLinks.name() + " li");
        style.addProperty("padding", "3px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLink.name() + " a");
        style.addProperty("text-decoration", "none");
        style.addProperty("color", "black");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLink.name() + " a:hover");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", "#ff6600");
        addStyle(style);

    }

    @Override
    protected void initFooterLinksStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLinks.name());
        style.addProperty("margin-left", "300px");
        style.addProperty("margin-top", "5px");
        style.addProperty("width", "550px");
        style.addProperty("color", "lightGrey");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLinks.name() + " ul");
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLinks.name() + " li");
        style.addProperty("padding", "3px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLink.name() + " a");
        style.addProperty("text-decoration", "none");
        style.addProperty("color", "lightGrey");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLink.name() + " a:hover");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", "white");
        addStyle(style);

    }

    @Override
    protected void initFooterCopyrightStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_FooterCopyright.name());
        style.addProperty("margin-left", "20px");
        style.addProperty("margin-top", "5px");
        style.addProperty("color", "lightGrey");
        addStyle(style);
    }

    @Override
    protected void initHtmlPortletStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortlet.name());
        style.addProperty("margin", "15px");
        style.addProperty("width", "150px");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletHeader.name());
        style.addProperty("background", "transparent url(images/header.gif) no-repeat bottom 80%");
        style.addProperty("color", "gray");
        style.addProperty("padding", "10px 5px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletEmptyHeader.name());
        style.addProperty("padding", "0px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletBody.name());
        style.addProperty("padding", "5px");
        style.addProperty("color", "black");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletBody.name() + " a");
        style.addProperty("color", "black");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletBody.name() + " a:hover");
        style.addProperty("color", "grey");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

    }

}

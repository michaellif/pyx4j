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
package com.pyx4j.site.client.themes.business;

import com.pyx4j.entity.client.EntityCSSClass;
import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.client.themes.SiteTheme;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;

public class BusinessTheme extends SiteTheme {

    @Override
    protected void initThemeColors() {
        putThemeColor(ThemeColor.OBJECT_TONE1, "#404040");
        putThemeColor(ThemeColor.OBJECT_TONE2, "#303030");
        putThemeColor(ThemeColor.OBJECT_TONE3, "#505050");
        putThemeColor(ThemeColor.BORDER, "#666666");
        putThemeColor(ThemeColor.SELECTION, "#C90");
        putThemeColor(ThemeColor.SELECTION_TEXT, "#ffffff");
        putThemeColor(ThemeColor.TEXT_BACKGROUND, "#ffffff");
        putThemeColor(ThemeColor.TEXT, "#000000");
        putThemeColor(ThemeColor.DISABLED_TEXT_BACKGROUND, "#fafafa");
        putThemeColor(ThemeColor.MANDATORY_TEXT_BACKGROUND, "#fcba84");
        putThemeColor(ThemeColor.READ_ONLY_TEXT_BACKGROUND, "#eeeeee");
        putThemeColor(ThemeColor.SEPARATOR, "#999999");
    }

    @Override
    protected void initGeneralStyles() {
        super.initGeneralStyles();
        Style style = new Style("body");
        style.addProperty("background", "white");
        style.addProperty("font-family", "'Helvetica Neue', Arial, Helvetica, Geneva, sans-serif");
        addStyle(style);
    }

    @Override
    protected void initSitePanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_SitePanel.name());
        style.addProperty("background", "url(images/business_background.gif) no-repeat 50% 0%");
        addStyle(style);
    }

    @Override
    protected void initContentPanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_ContentPanel.name());
        style.addProperty("width", "760px");
        style.addProperty("padding-top", "0px");
        style.addProperty("padding-bottom", "50px");
        addStyle(style);
    }

    @Override
    protected void initHeaderStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Header.name());
        style.addProperty("background", "url(images/business_banner.jpg) no-repeat");
        style.addProperty("height", "291px");
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
    protected void initPageWidgetStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_PageWidget.name());
        style.addProperty("padding", "20px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PageWidget.name() + " h1, h2, h3, h4 ,h5");
        style.addProperty("margin", "0 0 .5em 0");
        style.addProperty("font-weight", "bold");
        style.addProperty("color", "#306");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PageWidget.name() + " a");
        style.addProperty("color", "#c90");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PageWidget.name() + " a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PageWidget.name() + " p, li");
        style.addProperty("margin-bottom", "0.5em");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PageWidget.name() + " p, li, dl, span");
        style.addProperty("font-size", "0.81em");
        style.addProperty("line-height", "1.5em");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PageWidget.name() + " ul, ol");
        style.addProperty(" margin-left", "1em");
        style.addProperty(" margin-bottom", "1em");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PageWidget.name() + " ul li");
        style.addProperty(" list-style-type", "square");
        style.addProperty(" list-style-image", "url(images/bullet.gif)");
        addStyle(style);

    }

    @Override
    protected void initFooterStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Footer.name());
        style.addProperty("background", "white");
        style.addProperty("margin-top", "20px");
        style.addProperty("border-top", "solid 1px #C90");
        style.addProperty("height", "35px");
        addStyle(style);
    }

    @Override
    protected void initHeaderCaptionsStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderCaptions.name());
        style.addProperty("color", "#330066");
        style.addProperty("margin-left", "270px");
        style.addProperty("margin-top", "167px");
        style.addProperty("font-size", "28px");
        style.addProperty("font-weight", "bold");
        addStyle(style);
    }

    @Override
    protected void initLogoStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Logo.name());
        style.addProperty("margin-left", "20px");
        style.addProperty("margin-top", "10px");
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
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name());
        style.addProperty("background", "transparent url(images/business_nav_divider.gif) no-repeat left center");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + " a");
        style.addProperty("color", "#330066");
        style.addProperty("padding", "0px 20px 0px 20px");
        style.addProperty("text-decoration", "none");
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + " a span");
        style.addProperty("padding-top", "6px");
        style.addProperty("display", "block");
        style.addProperty("font-size", "1em");
        style.addProperty("font-weight", "bold");
        style.addProperty("line-height", "1em");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + "-selected a");
        style.addProperty("color", "#886625");
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
        style.addProperty("color", "#C90");
        style.addProperty("width", "650px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLinks.name() + " ul");
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLinks.name() + " li");
        style.addProperty("padding", "3px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLink.name() + " a");
        style.addProperty("text-decoration", "none");
        style.addProperty("font-weight", "bold");
        style.addProperty("font-size", "13px");
        style.addProperty("color", "#C90");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLink.name() + " span");
        style.addProperty("font-size", "11px");
        style.addProperty("color", "#C90");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLink.name() + " a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

    }

    @Override
    protected void initFooterLinksStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLinks.name());
        style.addProperty("margin-left", "250px");
        style.addProperty("width", "500px");
        style.addProperty("color", "#C90");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLinks.name() + " ul");
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLinks.name() + " li");
        style.addProperty("padding", "3px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLink.name() + " a");
        style.addProperty("text-decoration", "none");
        style.addProperty("font-size", "13px");
        style.addProperty("color", "#C90");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLink.name() + " span");
        style.addProperty("font-size", "11px");
        style.addProperty("color", "#C90");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLink.name() + " a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

    }

    @Override
    protected void initFooterCopyrightStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_FooterCopiright.name());
        style.addProperty("margin-left", "20px");
        style.addProperty("margin-top", "5px");
        style.addProperty("font-size", "13px");
        style.addProperty("color", "#999");
        addStyle(style);
    }

    @Override
    protected void initHtmlPortletStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortlet.name());
        style.addProperty("margin", "15px");
        style.addProperty("width", "220px");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletHeader.name());
        style.addProperty("width", "220px");
        style.addProperty("background", "#E5CC7F");
        style.addProperty("padding", "5px 3px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletEmptyHeader.name());
        style.addProperty("width", "220px");
        style.addProperty("padding", "0px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletBody.name());
        style.addProperty("padding", "5px");
        style.addProperty("color", "black");
        style.addProperty("text-align", "center");
        style.addProperty("border", "1px solid #E5CC7F");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletBody.name() + " a");
        style.addProperty("color", "#330066");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletBody.name() + " a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

    }

    @Override
    protected void initEntityCRUDStyles() {
        Style style = new Style("." + EntityCSSClass.pyx4j_Entity_EntitySearchCriteria.name());
        style.addProperty("width", "875px");
        style.addProperty("background-color", "#F8F8F8");
        style.addProperty("padding", "10px 40px 10px 10px");
        style.addProperty("margin-bottom", "10px");
        style.addProperty("border", "solid 1px #F0F0F0");
        addStyle(style);
        style = new Style("." + EntityCSSClass.pyx4j_Entity_EntityEditor.name());
        style.addProperty("width", "875px");
        style.addProperty("background-color", "#F8F8F8");
        style.addProperty("padding", "10px 40px 10px 10px");
        style.addProperty("margin-bottom", "10px");
        style.addProperty("border", "solid 1px #F0F0F0");
        addStyle(style);
    }

}

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
package com.pyx4j.site.client.themes.crm;

import com.pyx4j.entity.client.EntityCSSClass;
import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.client.themes.SiteTheme;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;

public class CrmTheme extends SiteTheme {

    @Override
    protected void initThemeColors() {
        super.initThemeColors();
        putThemeColor(ThemeColor.OBJECT_TONE1, 0xece9d8);
        putThemeColor(ThemeColor.OBJECT_TONE2, 0xF6F9FF);
        putThemeColor(ThemeColor.OBJECT_TONE3, 0xdbd8c7);
        putThemeColor(ThemeColor.OBJECT_TONE4, 0xdbd8c7);
        putThemeColor(ThemeColor.OBJECT_TONE5, 0xdbd8c7);
        putThemeColor(ThemeColor.BORDER, 0xD0CAC4);
        putThemeColor(ThemeColor.SELECTION, 0x86adc4);
        putThemeColor(ThemeColor.SELECTION_TEXT, 0xffffff);
        putThemeColor(ThemeColor.TEXT, 0x000000);
        putThemeColor(ThemeColor.TEXT_BACKGROUND, 0xffffff);
        putThemeColor(ThemeColor.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemeColor.MANDATORY_TEXT_BACKGROUND, 0xfcba84);
        putThemeColor(ThemeColor.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemeColor.SEPARATOR, 0xeeeeee);
    }

    @Override
    protected void initGeneralStyles() {
        super.initGeneralStyles();
        Style style = new Style("textarea");
        style.addProperty("font-family", "'Lucida Grande', 'Segoe UI', 'Bitstream Vera Sans', Tahoma, Verdana, Arial, sans-serif");
        style.addProperty("font-size", "1em");
        addStyle(style);
        style = new Style("input");
        style.addProperty("font-family", "'Lucida Grande', 'Segoe UI', 'Bitstream Vera Sans', Tahoma, Verdana, Arial, sans-serif");
        style.addProperty("font-size", "1em");
        addStyle(style);
    }

    @Override
    protected void initBodyStyles() {
        Style style = new Style("body");
        style.addProperty("font-family", "'Lucida Grande', 'Segoe UI', 'Bitstream Vera Sans', Tahoma, Verdana, Arial, sans-serif");
        style.addProperty("font-size", "0.81em");
        style.addProperty("background", "white");
        addStyle(style);
    }

    @Override
    protected void initSitePanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_SitePanel.name());
        addStyle(style);
    }

    @Override
    protected void initContentPanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_ContentPanel.name());
        style.addProperty("width", "976px");
        style.addProperty("padding-top", "4px");
        style.addProperty("padding-bottom", "4px");
        addStyle(style);
    }

    @Override
    protected void initHeaderStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Header.name());
        style.addProperty("background", "transparent");
        style.addProperty("height", "130px");
        addStyle(style);
    }

    @Override
    protected void initMainPanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_MainPanel.name());
        style.addProperty("background", "transparent");
        addStyle(style);
    }

    @Override
    protected void initPagePanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name());
        style.addProperty("padding", "2px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " h1, h2, h3, h4 ,h5");
        style.addProperty("margin", "0 0 .5em 0");
        style.addProperty("font-weight", "bold");
        style.addProperty("color", "#306");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " a");
        style.addProperty("color", "gray");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " a:hover");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", "black");
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
        style.addProperty(" list-style-image", "url(images/light_bullet.gif)");
        addStyle(style);

    }

    @Override
    protected void initFooterStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Footer.name());
        style.addProperty("background", "transparent");
        style.addProperty("height", "30px");
        style.addProperty("border-top", "1px solid");
        style.addProperty("border-color", ThemeColor.BORDER);
        style.addProperty("margin-top", "30px");

        addStyle(style);
    }

    @Override
    protected void initHeaderCaptionsStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderCaptions.name());
        style.addProperty("visibility", "hidden");
        addStyle(style);
    }

    @Override
    protected void initLogoStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Logo.name());
        style.addProperty("margin-left", "20px");
        style.addProperty("margin-top", "2px");
        addStyle(style);
    }

    @Override
    protected void initPrimaryNavigStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavig.name());
        style.addProperty("margin-left", "20px");
        style.addProperty("margin-top", "90px");
        style.addProperty("width", "908px");
        style.addProperty("height", "32px");
        style.addProperty("padding-left", "20px");
        style.addProperty("background", "transparent url(images/primaryNav-underline.gif) repeat-x 100% 100%");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name());
        style.addProperty("padding", "0px 4px 0px 0px");
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + " a");
        style.addProperty("color", getThemeColorString(ThemeColor.TEXT));
        style.addProperty("padding", "0px 16px 0px 20px");
        style.addProperty("height", "32px");
        style.addProperty("text-decoration", "none");
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + " a span");
        style.addProperty("padding-top", "3px");
        style.addProperty("display", "block");
        style.addProperty("font-size", "18px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + "-selected");
        style.addProperty("background", "transparent url(images/primaryNav-right.gif) no-repeat 100%");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + "-selected a");
        style.addProperty("background", "transparent url(images/primaryNav-bg.gif) no-repeat");
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
        style.addProperty("width", "850px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLinks.name() + " ul");
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLinks.name() + " li");
        style.addProperty("padding", "0px 3px 0px 3px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLink.name() + " a");
        style.addProperty("text-decoration", "none");
        style.addProperty("font-size", "13px");
        style.addProperty("color", "gray");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLink.name() + " span");
        style.addProperty("font-size", "11px");
        style.addProperty("color", "black");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLink.name() + " a:hover");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", "black");
        addStyle(style);

    }

    @Override
    protected void initFooterLinksStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLinks.name());
        style.addProperty("margin-left", "300px");
        style.addProperty("width", "650px");
        style.addProperty("color", "gray");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLinks.name() + " ul");
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLinks.name() + " li");
        style.addProperty("padding", "3px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLink.name() + " a");
        style.addProperty("font-size", "13px");
        style.addProperty("color", "gray");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLink.name() + " span");
        style.addProperty("font-size", "11px");
        style.addProperty("color", "gray");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLink.name() + " a:hover");
        style.addProperty("color", "black");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

    }

    @Override
    protected void initFooterCopyrightStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_FooterCopyright.name());
        style.addProperty("margin-left", "20px");
        style.addProperty("margin-top", "5px");
        style.addProperty("font-size", "13px");
        style.addProperty("color", "gray");
        addStyle(style);
    }

    @Override
    protected void initHtmlPortletStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortlet.name());
        style.addProperty("margin", "4px");
        style.addProperty("width", "230px");
        style.addProperty("padding", "4px");
        style.addProperty("color", "gray");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortlet.name() + " h3");
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortlet.name() + " tr");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletHeader.name());
        style.addProperty("color", "#ff6600");
        style.addProperty("padding", "5px");
        style.addProperty("background", "transparent url(images/portlet-border-header.gif) no-repeat 0% 0%");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletEmptyHeader.name());
        style.addProperty("padding", "5px");
        style.addProperty("background", "transparent url(images/portlet-border-header.gif) no-repeat 0% 0%");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletBody.name());
        style.addProperty("padding", "5px");
        style.addProperty("background", "transparent url(images/portlet-border-body.gif) no-repeat 0% 100%");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletBody.name() + " a");
        style.addProperty("color", "gray");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletBody.name() + " a:hover");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", "black");
        addStyle(style);

    }

    @Override
    protected void initEntityCRUDStyles() {
        Style style = new Style("." + EntityCSSClass.pyx4j_Entity_EntitySearchCriteria.name());
        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        style.addProperty("padding", "2px");
        style.addProperty("border", "solid 1px");
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
        style = new Style("." + EntityCSSClass.pyx4j_Entity_EntityEditor.name());
        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        style.addProperty("padding", "3px");
        style.addProperty("margin-bottom", "10px");
        style.addProperty("border", "solid 1px");
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
    }

    @Override
    protected void initEntityDataTableStyles() {
        String prefix = DataTable.BASE_NAME;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("margin", "2px 0px 2px 0px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row));
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.even));
        style.addProperty("background-color", "#F4F4F4");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.odd));
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.nodetails));
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Header));
        style.addProperty("background-color", "#A0A0A0");
        style.addProperty("color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ActionsBar));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
    }

    @Override
    protected void initMapStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Map.name());
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
    }

}
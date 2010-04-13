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
package com.pyx4j.site.client.themes.light;

import com.pyx4j.entity.client.EntityCSSClass;
import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.client.themes.SiteTheme;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;

public class LightTheme extends SiteTheme {

    @Override
    protected void initThemeColors() {
        super.initThemeColors();
        putThemeColor(ThemeColor.OBJECT_TONE1, "#ece9d8");
        putThemeColor(ThemeColor.OBJECT_TONE2, "#F6F9FF");
        putThemeColor(ThemeColor.OBJECT_TONE3, "#dbd8c7");
        putThemeColor(ThemeColor.BORDER, "#E5ECF9");
        putThemeColor(ThemeColor.SELECTION, "#86adc4");
        putThemeColor(ThemeColor.SELECTION_TEXT, "#ffffff");
        putThemeColor(ThemeColor.TEXT, "#000000");
        putThemeColor(ThemeColor.TEXT_BACKGROUND, "#ffffff");
        putThemeColor(ThemeColor.DISABLED_TEXT_BACKGROUND, "#fafafa");
        putThemeColor(ThemeColor.MANDATORY_TEXT_BACKGROUND, "#fcba84");
        putThemeColor(ThemeColor.READ_ONLY_TEXT_BACKGROUND, "#eeeeee");
        putThemeColor(ThemeColor.SEPARATOR, "#eeeeee");
    }

    @Override
    protected void initGeneralStyles() {
        super.initGeneralStyles();
        Style style = new Style("body");
        style.addProperty("font-family", "'Lucida Grande', 'Segoe UI', 'Bitstream Vera Sans', Tahoma, Verdana, Arial, sans-serif");
        style.addProperty("font-size", "0.81em");
        style.addProperty("background", "#F8F8F8");
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
        style.addProperty("width", "968px");
        style.addProperty("padding-top", "20px");
        style.addProperty("padding-bottom", "20px");
        addStyle(style);
    }

    @Override
    protected void initHeaderStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Header.name());
        style.addProperty("background", "transparent url(images/container-header.gif) no-repeat");
        style.addProperty("height", "170px");
        addStyle(style);
    }

    @Override
    protected void initMainPanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_MainPanel.name());
        style.addProperty("background", "transparent url(images/container-main.gif) repeat-y");
        style.addProperty("padding-left", "10px");
        style.addProperty("padding-right", "10px");
        addStyle(style);
    }

    @Override
    protected void initPageWidgetStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_PageWidget.name());
        style.addProperty("padding", "10px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PageWidget.name() + " h1, h2, h3, h4 ,h5");
        style.addProperty("margin", "0 0 .5em 0");
        style.addProperty("font-weight", "bold");
        style.addProperty("color", "#306");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PageWidget.name() + " a");
        style.addProperty("color", "gray");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PageWidget.name() + " a:hover");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", "black");
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
        style.addProperty(" list-style-image", "url(images/light_bullet.gif)");
        addStyle(style);

    }

    @Override
    protected void initFooterStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Footer.name());
        style.addProperty("background", "transparent url(images/container-footer.gif) no-repeat 50% 100%");
        style.addProperty("height", "30px");
        addStyle(style);
    }

    @Override
    protected void initHeaderCaptionsStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderCaptions.name());
        style.addProperty("color", "gray");
        style.addProperty("margin-left", "20px");
        style.addProperty("margin-top", "130px");
        style.addProperty("font-size", "22px");

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
        style.addProperty("color", getThemeColor(ThemeColor.TEXT));
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
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_FooterCopiright.name());
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
        style.addProperty("width", "875px");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        style.addProperty("padding", "10px 40px 10px 10px");
        style.addProperty("margin-bottom", "10px");
        style.addProperty("border", "solid 1px");
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
        style = new Style("." + EntityCSSClass.pyx4j_Entity_EntityEditor.name());
        style.addProperty("width", "100%");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        style.addProperty("padding", "3px");
        style.addProperty("margin-bottom", "10px");
        style.addProperty("border", "solid 1px");
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
    }

    @Override
    protected void initEntityDataTableStyles() {
        Style style = new Style("." + EntityCSSClass.pyx4j_Entity_DataTable.name());
        style.addProperty("margin", "2px 0px 2px 0px");
        addStyle(style);
        style = new Style("." + EntityCSSClass.pyx4j_Entity_DataTableRow.name());
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        addStyle(style);
        style = new Style("." + EntityCSSClass.pyx4j_Entity_DataTableRow.name() + "-even");
        style.addProperty("background-color", "#F4F4F4");
        addStyle(style);
        style = new Style("." + EntityCSSClass.pyx4j_Entity_DataTableRow.name() + "-odd");
        style.addProperty("background-color", "white");
        addStyle(style);
        style = new Style("." + EntityCSSClass.pyx4j_Entity_DataTableHeader.name());
        style.addProperty("background-color", "#A0A0A0");
        style.addProperty("color", "white");
        addStyle(style);
        style = new Style("." + EntityCSSClass.pyx4j_Entity_DataTableActionsBar.name());
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

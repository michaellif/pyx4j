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
package com.pyx4j.site.client.themes.console;

import com.pyx4j.entity.client.EntityCSSClass;
import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.client.themes.SiteTheme;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;

public class ConsoleTheme extends SiteTheme {

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
    protected void initBodyStyles() {
        Style style = new Style("body");
        style.addProperty("background", "#FFFFFF");
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
        // 770 - > 795px; We need Total 780 for GAE Console
        style.addProperty("width", "755px");
        style.addProperty("padding-top", "2px");
        style.addProperty("padding-bottom", "2px");
        addStyle(style);
    }

    @Override
    protected void initHeaderStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Header.name());
        style.addProperty("height", "120px");
        addStyle(style);
    }

    @Override
    protected void initMainPanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_MainPanel.name());
        style.addProperty("padding-left", "10px");
        style.addProperty("padding-right", "10px");
        addStyle(style);
    }

    @Override
    protected void initPagePanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name());
        style.addProperty("padding", "10px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " h1, h2, h3, h4 ,h5");
        style.addProperty("margin", "0 0 .5em 0");
        style.addProperty("font-weight", "bold");
        style.addProperty("color", "#306");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " a");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " a:hover");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " p, li");
        style.addProperty("margin-bottom", "0.5em");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PagePanel.name() + " p, li, dl, span");
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
        style.addProperty("border-top", "2px solid #C3D9FF");
        style.addProperty("height", "30px");
        addStyle(style);
    }

    @Override
    protected void initHeaderCaptionsStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderCaptions.name());
        style.addProperty("margin-left", "10px");
        style.addProperty("margin-top", "80px");
        style.addProperty("font-size", "22px");

        addStyle(style);
    }

    @Override
    protected void initLogoStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_Logo.name());
        style.addProperty("visibility", "hidden");
        addStyle(style);
    }

    @Override
    protected void initPrimaryNavigStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavig.name());
        style.addProperty("margin-left", "0px");
        style.addProperty("margin-top", "30px");
        style.addProperty("width", "700px");
        style.addProperty("height", "32px");
        style.addProperty("padding-left", "0px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name());
        style.addProperty("background", "transparent url(images/console_menusep.gif) no-repeat 100%");
        style.addProperty("padding", "0px 4px 0px 0px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + "-last");
        style.addProperty("background", "transparent");
        style.addProperty("padding", "0px 4px 0px 0px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + " a");
        style.addProperty("padding", "0px 6px 0px 10px");
        style.addProperty("height", "32px");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + " a span");
        style.addProperty("padding-top", "6px");
        style.addProperty("display", "block");
        style.addProperty("font-size", "14px");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + "-selected");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + "-selected a");
        style.addProperty("text-decoration", "none");
        style.addProperty("color", getThemeColor(ThemeColor.TEXT));
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + " a:hover");
        addStyle(style);
    }

    @Override
    protected void initHeaderLinksStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLinks.name());
        style.addProperty("margin-left", "100px");
        style.addProperty("margin-top", "10px");
        style.addProperty("width", "600px");
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
        addStyle(style);
    }

    @Override
    protected void initHtmlPortletStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortlet.name());
        style.addProperty("margin", "10px");
        style.addProperty("width", "230px");
        style.addProperty("padding", "5px");
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
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletEmptyHeader.name());
        style.addProperty("padding", "5px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletBody.name());
        style.addProperty("padding", "5px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletBody.name() + " a");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletBody.name() + " a:hover");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", "black");
        addStyle(style);

    }

    @Override
    protected void initEntityCRUDStyles() {
        Style style = new Style("." + EntityCSSClass.pyx4j_Entity_EntitySearchCriteria.name());
        style.addProperty("width", "750px");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        style.addProperty("padding", "10px 40px 10px 10px");
        style.addProperty("margin-bottom", "10px");
        style.addProperty("border", "solid 1px");
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
        style = new Style("." + EntityCSSClass.pyx4j_Entity_EntityEditor.name());
        style.addProperty("width", "750px");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        style.addProperty("padding", "10px 40px 10px 10px");
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

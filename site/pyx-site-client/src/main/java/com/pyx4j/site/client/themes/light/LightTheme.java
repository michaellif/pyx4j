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

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.client.themes.SiteTheme;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;

public class LightTheme extends SiteTheme {

    @Override
    protected void initGeneralStyles() {
        super.initGeneralStyles();
        Style style = new Style("body");
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
        style.addProperty("height", "200px");
        addStyle(style);
    }

    @Override
    protected void initMainPanelStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_MainPanel.name());
        style.addProperty("background", "transparent url(images/container-main.gif) repeat-y");
        style.addProperty("padding-left", "20px");
        style.addProperty("padding-right", "20px");
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
        style.addProperty("margin-top", "160px");
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
        style.addProperty("margin-top", "120px");
        style.addProperty("width", "800px");
        style.addProperty("height", "32px");
        style.addProperty("padding-left", "20px");
        style.addProperty("background", "transparent url(images/primaryNav-underline.gif) repeat-x 100% 100%");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name());
        style.addProperty("padding", "0px 4px 0px 0px");
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + "-selected");
        style.addProperty("background", "transparent url(images/primaryNav-right.gif) no-repeat 100%");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + " a");
        style.addProperty("color", getThemeColor(ThemeColor.TEXT));
        style.addProperty("padding", "0px 16px 0px 20px");
        style.addProperty("height", "32px");
        style.addProperty("text-decoration", "none");
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + " a span");
        style.addProperty("padding-top", "6px");
        style.addProperty("display", "block");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + "-selected a");
        style.addProperty("background", "transparent url(images/primaryNav-bg.gif) no-repeat");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name() + "-mouseOver a");
        style.addProperty("text-decoration", "underline");
        addStyle(style);
    }

    @Override
    protected void initHeaderLinksStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLinks.name());
        style.addProperty("margin-left", "700px");
        style.addProperty("margin-top", "10px");
        style.addProperty("color", "gray");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLinks.name() + " li");
        style.addProperty("padding", "3px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLink.name() + " a");
        style.addProperty("text-decoration", "none");
        style.addProperty("color", "gray");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_HeaderLink.name() + "-mouseOver a");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", "black");
        addStyle(style);

    }

    @Override
    protected void initFooterLinksStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLinks.name());
        style.addProperty("margin-left", "450px");
        style.addProperty("margin-top", "1px");
        style.addProperty("color", "gray");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLinks.name() + " li");
        style.addProperty("padding", "3px");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLink.name() + " a");
        style.addProperty("color", "gray");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("." + SiteCSSClass.pyx4j_Site_FooterLink.name() + "-mouseOver a");
        style.addProperty("color", "black");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

    }

    @Override
    protected void initFooterCopyrightStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_FooterCopiright.name());
        style.addProperty("margin-left", "20px");
        style.addProperty("margin-top", "1px");
        style.addProperty("color", "gray");
        addStyle(style);
    }

    @Override
    protected void initHtmlPortletStyles() {
        Style style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortlet.name());
        style.addProperty("margin", "20px");
        style.addProperty("width", "230px");
        style.addProperty("padding", "5px");
        style.addProperty("color", "gray");
        style.addProperty("text-align", "center");
        addStyle(style);
        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortlet.name() + " h3");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletHeader.name());
        style.addProperty("color", "#ff6600");
        style.addProperty("padding", "5px");
        style.addProperty("background", "transparent url(images/portlet-border-header.gif) no-repeat 0% 0%");
        addStyle(style);
        style = new Style("." + SiteCSSClass.pyx4j_Site_HtmlPortletBody.name());
        style.addProperty("padding", "5px");
        style.addProperty("background", "transparent url(images/portlet-border-body.gif) no-repeat 0% 100%");
        addStyle(style);
    }

}

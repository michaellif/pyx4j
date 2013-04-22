/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Apr 22, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.layout;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class RiaLayoutPanelTheme extends Theme {

    public static enum StyleName implements IStyleName {
        SiteView, SiteViewContent, SiteViewAction, SiteViewHeader, SiteViewNavigation, SiteViewFooter, SiteViewNavigContainer;
    }

    public RiaLayoutPanelTheme() {
        // All viewable area:
        Style style = new Style(".", StyleName.SiteView.name());
        style.addProperty("color", ThemeColor.foreground);
        addStyle(style);

        // DockLayoutPanel:
        style = new Style(".", StyleName.SiteViewContent.name());
        style.addProperty("min-width", "700px");
        style.addProperty("min-height", "500px");
        addStyle(style);

        // Header:
        style = new Style(".", StyleName.SiteViewHeader.name());
        style.addGradient(ThemeColor.object1, 1, ThemeColor.object1, 0.7);
        style.addProperty("color", "white");
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding-left", "0.3em");
        addStyle(style);

        // Footer:
        style = new Style(".", StyleName.SiteViewFooter.name());
//        style.addProperty("background", "url('" + VistaImages.INSTANCE.logo().getSafeUri().asString() + "') no-repeat scroll left center transparent");
        style.addProperty("background-color", ThemeColor.object1);
        addStyle(style);

        // NavigationContainer (Accordion menu):
        style = new Style(".", StyleName.SiteViewNavigContainer.name());
        style.addProperty("border-right", "4px solid");
        style.addProperty("border-right-color", ThemeColor.object1);

        addStyle(style);

        // Action (Header right side hyperlinks):
        style = new Style(".", StyleName.SiteViewAction.name());
        //style.addProperty("min-width", "700px");
        style.addProperty("color", ThemeColor.object1, 0.1);
        style.addProperty("font-size", "1em");
        addStyle(style);

        style = new Style(".", StyleName.SiteViewAction.name(), " td");
        style.addProperty("vertical-align", "middle !important");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        // anchors within the ActionBar:
        style = new Style(".", StyleName.SiteViewAction.name(), " a:link, .", StyleName.SiteViewAction.name(), " a:visited, .",
                StyleName.SiteViewAction.name(), " a:active");
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColor.object1, 0.2);
        addStyle(style);

        style = new Style(".", StyleName.SiteViewAction.name(), " a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }
}
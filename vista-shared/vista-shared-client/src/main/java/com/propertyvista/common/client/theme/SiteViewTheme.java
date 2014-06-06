/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.common.client.theme;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public class SiteViewTheme extends Theme {

    public static enum StyleName implements IStyleName {
        SiteView, SiteViewContent, SiteViewAction, SiteViewHeader, SiteViewSideMenu, SiteViewFooter, SiteViewShortCuts, SiteViewShortCutsItem, SiteViewShortCutsTitle;
    }

    public static enum StyleDependent implements IStyleDependent {
        selected
    }

    public SiteViewTheme() {
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
        style.addProperty("height", "100%");
        style.addProperty("width", "100%");
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding-left", "0.3em");
        addStyle(style);

        style = new Style(".", StyleName.SiteViewHeader, " .", DefaultWidgetsTheme.StyleName.Toolbar);
        style.addProperty("margin", "8px 10px 0 8px");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.SiteViewHeader, " .", DefaultWidgetsTheme.StyleName.Button);
        style.addProperty("line-height", "30px");
        style.addProperty("height", "30px");
        style.addProperty("padding", "3px");
        style.addProperty("background", "none");
        style.addProperty("border", "none");
        style.addProperty("outline", "none");
        addStyle(style);

        style = new Style(".", StyleName.SiteViewHeader, " .", DefaultWidgetsTheme.StyleName.Button, ":hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        // NavigationContainer (Accordion menu):
        style = new Style(".", StyleName.SiteViewSideMenu.name());
        style.addProperty("border-right", "4px solid");
        style.addProperty("border-top", "4px solid");
        style.addProperty("border-color", ThemeColor.object1);
        style.addProperty("line-height", "1.5em");
        style.addProperty("white-space", "nowrap");
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        addStyle(style);

        // Footer:
        style = new Style(".", StyleName.SiteViewFooter.name());
//        style.addProperty("background", "url('" + VistaImages.INSTANCE.logo().getSafeUri().asString() + "') no-repeat scroll left center transparent");
        style.addProperty("background-color", ThemeColor.object1);
        addStyle(style);

        // Action (Header right side hyperlinks):
        style = new Style(".", StyleName.SiteViewAction.name());
        //style.addProperty("min-width", "700px");
        style.addProperty("color", ThemeColor.object1, 0.1);
        style.addProperty("font-size", "1em");
        style.addProperty("margin", "10px");
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

        style = new Style(".", StyleName.SiteViewShortCuts.name());
        style.addProperty("border-top", "4px solid");
        style.addProperty("border-bottom", "4px solid");
        style.addProperty("border-left", "4px solid");
        style.addProperty("border-color", ThemeColor.object1);
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        addStyle(style);

        style = new Style(".", StyleName.SiteViewShortCuts.name(), " a:link, .", StyleName.SiteViewShortCuts.name(), " a:visited, .",
                StyleName.SiteViewShortCuts.name(), " a:active");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style(".", StyleName.SiteViewShortCutsItem.name());
        style.addProperty("padding", "3px 6px");
        addStyle(style);

        style = new Style(".", StyleName.SiteViewShortCutsItem.name(), " a");
        style.addProperty("text-overflow", "ellipsis");
        style.addProperty("overflow", "hidden");
        style.addProperty("white-space", "nowrap");
        style.addProperty("width", "100%");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.SiteViewShortCutsTitle.name());
        style.addProperty("line-height", "2.2em");
        style.addProperty("font-size", "1.2em");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding", "5px");
        style.addGradient(ThemeColor.foreground, 0.2, ThemeColor.foreground, 0.15);
        addStyle(style);
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }
}
/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 21, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public class PortalWebRootPaneTheme extends Theme {

    public static enum StyleName implements IStyleName {
        PageHeader, PageFooter,

        MainToolbar, BrandImage,

        MainMenu, MainMenuHolder, MainMenuTab, MainMenuLabel, MainMenuIcon,

        Notifications;
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, active, sideMenu, collapsedMenu
    }

    public PortalWebRootPaneTheme() {
        initHeaderStyles();
        initMainToolbarStyles();
        initMainMenuStyles();
        initFooterStyles();

    }

    private void initHeaderStyles() {
        Style style = new Style(".", StyleName.PageHeader);
        style.addProperty("background-color", "#fff");
        addStyle(style);
    }

    private void initMainToolbarStyles() {
        Style style = new Style(".", StyleName.MainToolbar);
        style.addProperty("display", "inline-block");
        style.addProperty("position", "relative");

        style.addProperty("width", "100%");
        style.addProperty("min-width", "320px");
        style.addProperty("height", "60px");
        addStyle(style);

        style = new Style(".", StyleName.MainToolbar, " .", DefaultWidgetsTheme.StyleName.Button);
        style.addProperty("height", "2.6em");
        style.addGradient(ThemeColor.foreground, 1, ThemeColor.foreground, 0.95);
        style.addProperty("background", ThemeColor.foreground, 0.7);
        style.addProperty("border-color", ThemeColor.foreground, 0.75);
        style.addProperty("border-radius", "5px");
        style.addProperty("margin", "0 6px");
        addStyle(style);

        style = new Style(".", StyleName.MainToolbar, " .", DefaultWidgetsTheme.StyleName.ButtonText);
        style.addProperty("color", ThemeColor.foreground, 0.1);
        style.addProperty("line-height", "2.6em");
        addStyle(style);

        style = new Style(".", StyleName.MainToolbar, " .", DefaultWidgetsTheme.StyleName.Toolbar);
        style.addProperty("font-size", "1em");
        style.addProperty("margin", "15px");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.MainToolbar, " .", DefaultWidgetsTheme.StyleName.ToolbarItem);
        style.addProperty("font-size", "1em");
        addStyle(style);

        style = new Style(".", StyleName.BrandImage);
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "top");
        addStyle(style);

    }

    private void initMainMenuStyles() {
        Style style = new Style(".", StyleName.MainMenu);
        style.addProperty("margin", "20px 0 ");
        style.addProperty("border", "1px solid");
        style.addProperty("border-radius", "5px");
        style.addProperty("border-color", ThemeColor.foreground, 0.15);
        style.addProperty("background", ThemeColor.foreground, 0.01);
        addStyle(style);

        style = new Style(".", StyleName.MainMenu, "-", StyleDependent.sideMenu);
        style.addProperty("border-radius", "0");
        addStyle(style);

        style = new Style(".", StyleName.MainMenu, "-", StyleDependent.collapsedMenu, " .", StyleName.MainMenuLabel);
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.MainMenuTab);
        style.addProperty("line-height", "20px");
        style.addProperty("height", "20px");
        style.addProperty("white-space", "nowrap");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding", "8px");
        style.addProperty("list-style", "none");
        style.addProperty("border-bottom", "1px solid");
        style.addProperty("border-bottom-color", ThemeColor.foreground, 0.15);
        addStyle(style);

        style = new Style(".", StyleName.MainMenuTab, ":last-child");
        style.addProperty("border-bottom", "0px");
        addStyle(style);

        style = new Style(".", StyleName.MainMenuTab, "-", StyleDependent.active);
        style.addProperty("background", ThemeColor.contrast1);
        style.addProperty("color", ThemeColor.foreground, 0.01);
        addStyle(style);

        style = new Style(".", StyleName.MainMenuLabel);
        style.addProperty("float", "left");
        style.addProperty("padding", "0 10px");
        addStyle(style);

        style = new Style(".", StyleName.MainMenuIcon);
        style.addProperty("float", "left");
        addStyle(style);

    }

    private void initFooterStyles() {
        Style style = new Style(".", StyleName.PageFooter);
        style.addProperty("width", "100%");
        style.addProperty("height", "15em");
        addStyle(style);
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}

/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on Nov 4, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.themes;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;

public class PtAppSitePanelTheme extends Theme {

    public static enum StyleName implements IStyleName {
        SitePanel, SitePanelContent, SitePanelAction, SitePanelHeader, SitePanelMainNavigation, SitePanelSecondaryNavigation, SitePanelFooter, SitePanelDisplay, SitePanelNavigContainer,

        SitePanelMessage, SitePanelCenter, SitePanelMain, SitePanelCaption;
    }

    public PtAppSitePanelTheme() {

        Style style = new Style(".", PtAppSitePanelTheme.StyleName.SitePanel.name());
        style.addProperty("width", "100%");
        addStyle(style);

        // Header:
        style = new Style(".", PtAppSitePanelTheme.StyleName.SitePanelHeader.name());
        style.addProperty("height", "115px");
        style.addProperty("width", "100%");
        style.addProperty("background-color", ThemeColors.object1, 0.6);
        addStyle(style);

        style = new Style(".", PtAppSitePanelTheme.StyleName.SitePanelMainNavigation.name());
        style.addProperty("background-color", ThemeColors.object1);
        addStyle(style);

        style = new Style(".", PtAppSitePanelTheme.StyleName.SitePanelMainNavigation.name(), " .", PtAppSitePanelTheme.StyleName.SitePanelDisplay.name());
        style.addProperty("width", "930px");
        style.addProperty("margin", "0 auto");
        addStyle(style);

        style = new Style(".", PtAppSitePanelTheme.StyleName.SitePanelCenter.name());
        style.addProperty("width", "930px");
        style.addProperty("margin", "0 auto");
        addStyle(style);

        style = new Style(".", PtAppSitePanelTheme.StyleName.SitePanelMain.name());
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", PtAppSitePanelTheme.StyleName.SitePanelCaption.name());
        style.addProperty("width", "30%");
        addStyle(style);

        style = new Style(".", PtAppSitePanelTheme.StyleName.SitePanelSecondaryNavigation.name());
        style.addProperty("width", "60%");
        addStyle(style);

        style = new Style(".", PtAppSitePanelTheme.StyleName.SitePanelFooter.name());
        style.addProperty("clear", "left");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", PtAppSitePanelTheme.StyleName.SitePanelCaption.name());
        style.addProperty("font-size", "1.3em");
        style.addProperty("font-weight", "bold");
        addStyle(style);

    }
}

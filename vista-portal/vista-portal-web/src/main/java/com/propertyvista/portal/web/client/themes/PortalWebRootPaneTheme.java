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

public class PortalWebRootPaneTheme extends Theme {

    public static enum StyleName implements IStyleName {
        PageHeader, PageFooter,

        MainToolbar, MainToolbarActions,

        MainMenu, MainMenuHolder, MainMenuTab, MainMenuLabel;
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, active
    }

    public PortalWebRootPaneTheme() {
        initHeaderStyles();
        initMainToolbarStyles();
        initMainMStyles();
        initFooterStyles();

    }

    private void initHeaderStyles() {
        Style style = new Style(".", StyleName.PageHeader);
        style.addProperty("background-color", "#fff");
        addStyle(style);
    }

    private void initMainToolbarStyles() {
        Style style = new Style(".", StyleName.MainToolbar);
        style.addGradient(ThemeColor.foreground, 1, ThemeColor.foreground, 0.95);
        style.addProperty("width", "100%");
        style.addProperty("height", "5em");
        addStyle(style);

        style = new Style(" .", StyleName.MainToolbarActions);
        style.addProperty("float", "right");
        addStyle(style);
    }

    private void initMainMStyles() {
        Style style = new Style(".", StyleName.MainMenu);
        addStyle(style);
    }

    private void initFooterStyles() {
        Style style = new Style(".", StyleName.PageFooter);
        style.addGradient(ThemeColor.foreground, 0.6, ThemeColor.foreground, 0.55);
        style.addProperty("width", "100%");
        style.addProperty("height", "15em");
        addStyle(style);
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}

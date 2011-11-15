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
 * Created on Nov 13, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public class DefaultSiteCrudPanelsTheme extends Theme {

    public static enum StyleName implements IStyleName {
        Toolbar, Lister, ListerActionsPanel, ListerFiltersPanel, ListerListPanel
    }

    public DefaultSiteCrudPanelsTheme() {
        initStyles();
    }

    protected void initStyles() {
        initToolbarStyles();
        initListerStyles();
    }

    private void initToolbarStyles() {
        Style style = new Style(".", StyleName.Toolbar);
        style.addProperty("padding-top", "0.5em");
        addStyle(style);

        style = new Style(".", StyleName.Toolbar, " .", DefaultWidgetsTheme.StyleName.Button);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.05);
        style.addProperty("color", ThemeColors.foreground, 0.05);
        style.addGradient(ThemeColors.foreground, 0.7, ThemeColors.foreground, 0.9);
        addStyle(style);

        style = new Style(".", StyleName.Toolbar, " .", DefaultWidgetsTheme.StyleName.Button, "-", DefaultWidgetsTheme.StyleDependent.hover);
        style.addProperty("border-color", ThemeColors.foreground, 0.3);
        addStyle(style);
    }

    protected void initListerStyles() {

        Style style = new Style(".", StyleName.Lister);
        addStyle(style);

        style = new Style(".", StyleName.ListerActionsPanel);
        style.addProperty("background-color", ThemeColors.foreground, 0.20);
        style.addProperty("color", ThemeColors.foreground);
        style.addProperty("height", "3em");
        addStyle(style);

        style = new Style(".", StyleName.ListerFiltersPanel);
        style.addProperty("background-color", ThemeColors.foreground, 0.05);
        style.addProperty("padding-top", "0.5em");
        addStyle(style);

        style = new Style(".", StyleName.ListerListPanel);
        addStyle(style);

    }
}

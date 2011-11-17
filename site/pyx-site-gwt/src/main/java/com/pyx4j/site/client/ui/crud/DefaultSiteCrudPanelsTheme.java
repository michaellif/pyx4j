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
import com.pyx4j.entity.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public class DefaultSiteCrudPanelsTheme extends Theme {

    public static enum StyleName implements IStyleName {
        ActionsPanel, Lister, ListerFiltersPanel, ListerListPanel, Header, Footer
    }

    public DefaultSiteCrudPanelsTheme() {
        initStyles();
    }

    protected void initStyles() {
        initToolbarStyles();
        initListerStyles();
    }

    private void initToolbarStyles() {

        Style style = new Style(".", StyleName.ActionsPanel, " .", DefaultWidgetsTheme.StyleName.Button);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.05);
        style.addProperty("color", ThemeColors.foreground, 0);
        style.addProperty("padding", "2px 12px");
        style.addGradient(ThemeColors.foreground, 1, ThemeColors.foreground, 2);
        style.addProperty("font-size", "11px");
        style.addProperty("font-weight", "bold");
        style.addProperty("border-radius", "5px");
        style.addProperty("-moz-border-radius", "5px");
        addStyle(style);

        style = new Style(".", StyleName.ActionsPanel, " .", DefaultWidgetsTheme.StyleName.Button, "-", DefaultWidgetsTheme.StyleDependent.hover);
        style.addProperty("border-color", ThemeColors.foreground, 0.3);
        addStyle(style);

        style = new Style(".", StyleName.ActionsPanel, " .", DefaultWidgetsTheme.StyleName.Button, "-", DefaultWidgetsTheme.StyleDependent.disabled);
        style.addProperty("color", ThemeColors.foreground, 0);
        style.addGradient(ThemeColors.foreground, 0.4, ThemeColors.foreground, 0.4);
        addStyle(style);

        style = new Style(".", StyleName.Footer, " .", DefaultWidgetsTheme.StyleName.Toolbar);
        style.addProperty("border-top", "4px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.3);
        style.addProperty("padding", "6px");
        addStyle(style);

        style = new Style(".", StyleName.ActionsPanel, " .", DefaultWidgetsTheme.StyleName.Toolbar);
        style.addProperty("padding", "6px");
        addStyle(style);

    }

    protected void initListerStyles() {

        Style style = new Style(".", StyleName.Lister);
        style.addProperty("width", "100%");
        style.addProperty("padding", "6px");
        addStyle(style);

        style = new Style(".", StyleName.ActionsPanel);
        style.addProperty("padding", "6px");
        addStyle(style);

        style = new Style(".", StyleName.ListerFiltersPanel);
        style.addProperty("background-color", ThemeColors.foreground, 0.05);
        style.addProperty("padding-top", "0.5em");
        addStyle(style);

        style = new Style(".", StyleName.ListerListPanel);
        addStyle(style);

        style = new Style(".", StyleName.ListerListPanel, " .", DefaultDataTableTheme.StyleName.DataTable);
        style.addProperty("border-left", "1px solid");
        style.addProperty("border-right", "1px solid");
        style.addProperty("border-left-color", ThemeColors.foreground, 0.4);
        style.addProperty("border-right-color", ThemeColors.foreground, 0.4);
        addStyle(style);

    }
}

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
package com.pyx4j.entity.client.ui.datatable;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public class DefaultDataTableTheme extends Theme {

    public static enum StyleSuffix implements IStyleName {
        DataTable, DataTableRow, DataTableHeader, DataTableActionsBar, DataTableColumnSelector, DataTableColumnMenu
    }

    public static enum StyleDependent implements IStyleDependent {
        disabled, selected, hover, even, odd, nodetails
    }

    public DefaultDataTableTheme() {
        initStyles();
    }

    protected void initStyles() {

        Style style = new Style(".", StyleSuffix.DataTable);
        addStyle(style);

        style = new Style(".", StyleSuffix.DataTableHeader);
        style.addProperty("background-color", ThemeColors.foreground, 0.4);
        style.addProperty("color", ThemeColors.foreground, 0);
        style.addProperty("font-weight", "bold");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(".", StyleSuffix.DataTableColumnSelector);
        style.addProperty("background-color", ThemeColors.foreground, 0.6);
        style.addProperty("color", ThemeColors.foreground, 0.1);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        String selectorPrefix = "." + StyleSuffix.DataTableColumnSelector.name();
        style = new Style(selectorPrefix + " a:link, " + selectorPrefix + " a:visited, " + selectorPrefix + " a:active");
        style.addProperty("color", ThemeColors.foreground, 0.1);
        addStyle(style);

        style = new Style(selectorPrefix + ":hover");
        style.addProperty("background-color", ThemeColors.foreground, 0.8);
        style.addProperty("color", ThemeColors.foreground, 0.1);
        addStyle(style);

        style = new Style(".", StyleSuffix.DataTableColumnMenu);
        style.addProperty("background-color", ThemeColors.foreground, 0.1);
        style.addProperty("color", ThemeColors.foreground, 0.9);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.9);
        style.addProperty("padding", "5px 7px");
        addStyle(style);

        style = new Style(".", StyleSuffix.DataTableRow);
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(".", StyleSuffix.DataTableRow, "-", StyleDependent.even);
        style.addProperty("background-color", ThemeColors.foreground, 0.05);
        addStyle(style);

        style = new Style(".", StyleSuffix.DataTableRow, "-", StyleDependent.odd);
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(".", StyleSuffix.DataTableRow, "-", StyleDependent.nodetails);
        addStyle(style);

        style = new Style(".", StyleSuffix.DataTableRow, "-", StyleDependent.selected);
        style.addProperty("background-color", ThemeColors.foreground, 0.3);
        addStyle(style);

        style = new Style(".", StyleSuffix.DataTableActionsBar);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.3);
        style.addProperty("padding", "6px");
        style.addProperty("background-color", ThemeColors.foreground, 0.05);
        addStyle(style);

        style = new Style(".", StyleSuffix.DataTableActionsBar, " .", DefaultWidgetsTheme.StyleName.Button);
        style.addProperty("border-color", ThemeColors.foreground, 0.4);
        style.addProperty("color", ThemeColors.foreground, 0.9);
        style.addProperty("height", "20px");
        style.addGradient(ThemeColors.foreground, 0, ThemeColors.foreground, 0.2);
        addStyle(style);

        style = new Style(".", StyleSuffix.DataTableActionsBar, " .", DefaultWidgetsTheme.StyleName.Button, "-", DefaultWidgetsTheme.StyleDependent.hover);
        style.addGradient(ThemeColors.foreground, 0.2, ThemeColors.foreground, 0);
        addStyle(style);
    }
}

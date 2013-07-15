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
package com.pyx4j.forms.client.ui.datatable;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public class DefaultDataTableTheme extends Theme {

    public static enum StyleName implements IStyleName {
        DataTable, DataTableRow, DataTableHeader, DataTableActionsBar, DataTableActionsBarContent, DataTableToolBar, DataTablePageNavigBar, DataTableColumnSelector, DataTableColumnMenu,

        DataTableFilter, DataTableFilterMain, DataTableFilterHeader, DataTableFilterFooter, DataTableFilterItem,

        DataTableCriteria, DataTableCriteriaMain, DataTableCriteriaHeader, DataTableCriteriaFooter
    }

    public static enum StyleDependent implements IStyleDependent {
        disabled, selected, hover, even, odd, nodetails
    }

    public DefaultDataTableTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {

        Style style = new Style(".", StyleName.DataTable);
        style.addProperty("border-collapse", "separate");
        addStyle(style);

        style = new Style(".", StyleName.DataTableHeader);
        style.addProperty("background-color", ThemeColor.foreground, 0.4);
        style.addProperty("color", ThemeColor.foreground, 0);
        style.addProperty("font-weight", "bold");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(".", StyleName.DataTableColumnSelector);
        style.addProperty("background-color", ThemeColor.foreground, 0.6);
        style.addProperty("color", ThemeColor.foreground, 0.1);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        String selectorPrefix = "." + StyleName.DataTableColumnSelector.name();
        style = new Style(selectorPrefix + " a:link, " + selectorPrefix + " a:visited, " + selectorPrefix + " a:active");
        style.addProperty("color", ThemeColor.foreground, 0.1);
        addStyle(style);

        style = new Style(selectorPrefix + ":hover");
        style.addProperty("background-color", ThemeColor.foreground, 0.8);
        style.addProperty("color", ThemeColor.foreground, 0.1);
        addStyle(style);

        style = new Style(".", StyleName.DataTableColumnMenu);
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        style.addProperty("color", ThemeColor.foreground, 0.9);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.9);
        style.addProperty("padding", "5px 7px");
        addStyle(style);

        style = new Style(".", StyleName.DataTableRow);
        style.addProperty("cursor", "pointer");
        style.addProperty("line-height", "1.5em");
        addStyle(style);

        style = new Style(".", StyleName.DataTableRow, "-", StyleDependent.even);
        style.addProperty("background-color", ThemeColor.foreground, 0.05);
        addStyle(style);

        style = new Style(".", StyleName.DataTableRow, "-", StyleDependent.odd);
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(".", StyleName.DataTableRow, "-", StyleDependent.nodetails);
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(".", StyleName.DataTableRow, "-", StyleDependent.selected);
        style.addProperty("background-color", ThemeColor.object1, 0.8);
        style.addProperty("color", ThemeColor.foreground, 0);
        addStyle(style);

        style = new Style(".", StyleName.DataTableActionsBar);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        style.addProperty("padding-top", "6px");
        style.addProperty("background-color", ThemeColor.foreground, 0.05);
        addStyle(style);

        style = new Style(".", StyleName.DataTableActionsBar, " .", DefaultWidgetsTheme.StyleName.Button);
        style.addProperty("vertical-align", "middle");
        style.addProperty("border-color", ThemeColor.foreground, 0.4);
        style.addProperty("color", ThemeColor.foreground, 0.9);
        style.addProperty("height", "20px");
        style.addProperty("margin", "0 6px");
        style.addGradient(ThemeColor.foreground, 0, ThemeColor.foreground, 0.2);
        addStyle(style);

        style = new Style(".", StyleName.DataTableActionsBar, " .", DefaultWidgetsTheme.StyleName.ButtonText);
        style.addProperty("line-height", "22px");
        addStyle(style);

        style = new Style(".", StyleName.DataTableActionsBar, " .", DefaultWidgetsTheme.StyleName.Button, "-", DefaultWidgetsTheme.StyleDependent.hover);
        style.addGradient(ThemeColor.foreground, 0.2, ThemeColor.foreground, 0);
        addStyle(style);

        style = new Style(".", StyleName.DataTableActionsBar, " .", DefaultWidgetsTheme.StyleName.Button, "-", DefaultWidgetsTheme.StyleDependent.disabled);
        style.addGradient(ThemeColor.foreground, 0.2, ThemeColor.foreground, 0.2);
        addStyle(style);

        style = new Style(".", StyleName.DataTableActionsBarContent);
        style.addProperty("display", "inline-block");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.DataTableToolBar);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.DataTablePageNavigBar);
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", StyleName.DataTablePageNavigBar, " .", DefaultWidgetsTheme.StyleName.Anchor);
        style.addProperty("color", ThemeColor.foreground, 0.9);
        addStyle(style);

        style = new Style(".", StyleName.DataTablePageNavigBar, " .", DefaultWidgetsTheme.StyleName.Anchor, "-", DefaultWidgetsTheme.StyleDependent.disabled);
        style.addProperty("color", ThemeColor.foreground, 0.5);
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(".", StyleName.DataTableFilter);
        style.addProperty("background-color", ThemeColor.object1, 0.2);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.DataTableFilterHeader, ", ", " .", StyleName.DataTableFilterFooter);
        style.addProperty("background-color", ThemeColor.object1, 0.4);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(" .", StyleName.DataTableFilterFooter);
        style.addProperty("padding", "4px 0 0");
        addStyle(style);

        style = new Style(".", StyleName.DataTableFilterMain);
        style.addProperty("padding", "6px");
        addStyle(style);

        style = new Style(".", StyleName.DataTableFilterItem, " td");
        style.addProperty("padding", "2px 0px 2px 6px");
        addStyle(style);

        style = new Style(".", StyleName.DataTableCriteria);
        style.addProperty("background-color", ThemeColor.object1, 0.2);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.DataTableCriteriaHeader, ", ", " .", StyleName.DataTableCriteriaFooter);
        style.addProperty("background-color", ThemeColor.object1, 0.4);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(" .", StyleName.DataTableCriteriaFooter);
        style.addProperty("padding", "6px 0");
        addStyle(style);

        style = new Style(".", StyleName.DataTableCriteriaMain);
        style.addProperty("padding", "6px");
        addStyle(style);

    }
}

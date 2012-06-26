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
package com.pyx4j.widgets.client.tabpanel;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;

public class DefaultTabTheme extends Theme {

    public static enum StyleName implements IStyleName {
        TabPanel, TabDeckPanel, TabBar, TabBarItem, TabBarItemLeft, TabBarItemRight, TabBarItemLabel, TabBarItemImage, TabList, TabListItem
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hidden, hover, masked
    }

    public DefaultTabTheme() {
        initStyles();
    }

    protected void initStyles() {

        Style style = new Style(".", StyleName.TabPanel);
        addStyle(style);

        style = new Style(".", StyleName.TabBar);
        style.addProperty("border-bottom", "4px solid #D1D1D1");
        style.addProperty("padding-left", "0.5em");
        style.addProperty("padding-top", "0.5em");
        style.addProperty("border-spacing", "0");
        addStyle(style);

        style = new Style(".", StyleName.TabBarItem);
        style.addProperty("display", "inline-block");
        style.addProperty("position", "relative");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("height", "100%");
        style.addProperty("text-align", "center");
        style.addProperty("padding-right", "8px");
        style.addProperty("margin-right", "1px");
        style.addProperty("margin-left", "1px");
        style.addProperty("margin-top", "3px");
        style.addProperty("border-right", "1px solid");
        style.addProperty("border-right-color", ThemeColors.foreground, 0.6);
        style.addProperty("border-left", "1px solid");
        style.addProperty("border-left-color", ThemeColors.foreground, 0.6);
        style.addProperty("border-top", "1px solid");
        style.addProperty("border-top-color", ThemeColors.foreground, 0.6);
        style.addProperty("background", ThemeColors.object1, 1);
        style.addProperty("color", ThemeColors.foreground, 0);
        addStyle(style);

        style = new Style(StyleName.TabBarItem, StyleDependent.hover);
        style.addProperty("background", ThemeColors.object1, 0.9);
        addStyle(style);

        style = new Style(".", StyleName.TabBarItem, "-", StyleDependent.selected);
        style.addProperty("cursor", "default");
        style.addGradient(ThemeColors.foreground, 0.1, ThemeColors.foreground, 0.30);
        style.addProperty("color", "#333");
        addStyle(style);

        style = new Style(".", StyleName.TabBarItem, "-", StyleDependent.disabled);
        style.addProperty("background", ThemeColors.object1, 0.6);
        style.addProperty("color", ThemeColors.foreground, 0.2);
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(".", StyleName.TabBarItem, "-", StyleDependent.hidden);
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.TabBarItem, "-", StyleDependent.masked);
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style((IStyleName) StyleName.TabBarItemLabel);
        style.addProperty("margin", "3px");
        addStyle(style);

        style = new Style((IStyleName) StyleName.TabList);
        style.addProperty("background-color", "white");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.6);
        addStyle(style);

        style = new Style((IStyleName) StyleName.TabListItem);
        style.addProperty("color", "black");
        style.addProperty("padding", "4px 14px 4px 1px");
        addStyle(style);

        style = new Style(StyleName.TabListItem, StyleDependent.hover);
        style.addProperty("background", ThemeColors.foreground, 0.5);
        style.addProperty("color", ThemeColors.foreground, 0);
        addStyle(style);
    }
}
